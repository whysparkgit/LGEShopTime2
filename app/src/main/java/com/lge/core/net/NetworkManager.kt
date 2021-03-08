package com.lge.core.net

import android.os.Handler
import android.os.Looper
import com.lge.core.sys.Config
import com.lge.core.sys.Device
import com.lge.core.sys.Trace
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*

class NetworkManager private constructor()
{
    private val mOkHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    private val mRetrofitBuilder: Retrofit.Builder = Retrofit.Builder();
    private var mTransactionCallback: HttpTransactionCallback? = null
    private lateinit var mDelayHandler: Handler
    private lateinit var mRunnable : Runnable

    companion object {
        private var mInstance: NetworkManager? = null

        @JvmStatic
        fun getInstance(): NetworkManager = mInstance?:
            synchronized(this) {
                mInstance?: NetworkManager().also {
                    mInstance = it
                }
            }
    }

    fun init() {
        if (Config.SUPPORT_DEBUG) {
            val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)  // HEADERS)
            mOkHttpClientBuilder.addInterceptor(interceptor)
        }

        mOkHttpClientBuilder.followRedirects(false)
    }

    fun registerTransactionCallback(callback: HttpTransactionCallback) {
        mTransactionCallback = callback
    }

    private fun getCallByMethod(retrofitInterface: RetrofitInterface, protocol: HttpRequestable): Call<ResponseBody>? =
        when (protocol.getMethod()) {
            HttpConst.HTTP_GET -> {
                retrofitInterface.requestGet(protocol.getUrl(), protocol.getRequestHeaderMap())
            }

            HttpConst.HTTP_POST -> {
                val reqBody: RequestBody = if (protocol.getJsonRequestBody() != null) {
                    Trace.debug(">> Request.getJsonRequestBody() = \n${protocol.getJsonRequestBody()}")
                    (protocol.getJsonRequestBody()?:"{}").toRequestBody(protocol.getContentType().toMediaType())
                } else {
                    Trace.debug(">> Request.getRequestBody() = \n${protocol.getRequestBody().toString()}")
                    protocol.getRequestBody().toString().toRequestBody(protocol.getContentType().toMediaType())
                }

                retrofitInterface.requestPost(protocol.getUrl(), protocol.getRequestHeaderMap(), reqBody);
            }

            HttpConst.HTTP_FILE_UPLOAD -> {
                val file: File = protocol.getRequestBody() as File
                val reqBody: RequestBody = file.asRequestBody(HttpConst.HTTP_MIME_TYPE_JPEG.toMediaType())
                val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(HttpConst.HTTP_MULTIPART_FILE, file.name, reqBody)
                retrofitInterface.uploadFile(protocol.getUrl(), protocol.getRequestHeaderMap(), filePart)
            }

            HttpConst.HTTP_MULTIPART -> {
                val multipart: Map<String, Any> = protocol.getRequestBody() as Map<String, Any>
                val json: String = multipart.get(HttpConst.HTTP_MULTIPART_JSON) as String
                val jsonPart: RequestBody = json.toRequestBody(HttpConst.HTTP_MIME_TYPE_JSON.toMediaType())
                val file: File = multipart.get(HttpConst.HTTP_MULTIPART_FILE) as File

                if (file != null) {
                    val fileBody = file.asRequestBody(HttpConst.HTTP_MIME_TYPE_JPEG.toMediaType())
                    val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(HttpConst.HTTP_MULTIPART_FILE, file.name, fileBody)
                    retrofitInterface.uploadMultiPart(protocol.getUrl(), protocol.getRequestHeaderMap(), jsonPart, filePart)
                } else {
                    retrofitInterface.uploadMultiPart(protocol.getUrl(), protocol.getRequestHeaderMap(), jsonPart, null)
                }
            }

            else -> {
                Trace.debug("-- getCallByMethod() not supported method.");
                null
            }
        }

    fun delayRequest(protocol: HttpRequestable, millisec: Int) {
        Trace.debug("++ delayRequest()")

        mRunnable = Runnable { asyncRequest(protocol) }
        mDelayHandler = Handler(Looper.getMainLooper())
        mDelayHandler.postDelayed(mRunnable, millisec.toLong())
    }

    fun cancelRequest(protocol: HttpRequestable) {
        Trace.debug("++ cancelRequest()")

        if (::mDelayHandler.isInitialized && ::mRunnable.isInitialized) {
            mDelayHandler.removeCallbacks(mRunnable, protocol)
        }
    }

    fun syncRequest(protocol: HttpRequestable) {
        Trace.debug("++ syncRequest()")
        Trace.debug(">> request url = " + protocol.getUrl())

        mOkHttpClientBuilder.connectTimeout(protocol.getConnectTimeout().toLong(), TimeUnit.SECONDS)
        mOkHttpClientBuilder.readTimeout(protocol.getReadTimeout().toLong(), TimeUnit.SECONDS)
        mRetrofitBuilder.client(mOkHttpClientBuilder.build())

        val retrofit = mRetrofitBuilder.baseUrl(protocol.getDomain()).build()
        val retrofitInterface = retrofit.create(RetrofitInterface::class.java)
        val call = getCallByMethod(retrofitInterface, protocol) ?: return

        try {
            val response = call.execute()

            if (!response.isSuccessful) {
                protocol.requestFailed(response.code(), response.message())
                return;
            }

            Trace.debug(">> syncRequest() Response = $response");
            protocol.setResponseHeaderMap(response.headers().toMultimap())
            protocol.parse(response.body().toString())
        } catch (e: IOException) {
            e.printStackTrace()
            protocol.requestFailed(HttpConst.Error.HTTP_DISCONNECTED.getCode(),
                    e.message?: HttpConst.Error.HTTP_DISCONNECTED.getMessage())
        }

        Trace.debug("-- syncRequest()")
    }

    fun asyncRequest(protocol: HttpRequestable) {
        Trace.debug("++ asyncRequest()")

        if (HttpConst.HTTP_FEATURE_OFFLINE) {
            if (!Device.Connection.isConnected() && protocol.hasOfflineJob()) {
                Trace.debug(">> hasOfflineJob() begin")

                if (HttpConst.HTTP_FEATURE_COROUTINE) {
                    CoroutineScope(Dispatchers.Main).launch {
                        protocol.processOffline()
                    }
                } else {
                    protocol.processOffline()
                }

                Trace.debug(">> hasOfflineJob() end")
                return
            }
        }

        mTransactionCallback?.transactionBegin()

        mOkHttpClientBuilder.connectTimeout(protocol.getConnectTimeout().toLong(), TimeUnit.SECONDS)
        mOkHttpClientBuilder.readTimeout(protocol.getReadTimeout().toLong(), TimeUnit.SECONDS)
        mRetrofitBuilder.client(mOkHttpClientBuilder.build())

        val retrofit = mRetrofitBuilder.baseUrl(protocol.getDomain()).build()
        val retrofitInterface = retrofit.create(RetrofitInterface::class.java)
        val call = getCallByMethod(retrofitInterface, protocol) ?: return

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Trace.debug(">> asyncRequest() onResponse = $response");

                if (response.code() != HttpConst.Status.HTTP_200.getCode() || response.body() == null) {
                    protocol.requestFailed(response.code(), response.message())
                    mTransactionCallback?.transactionEnd()
                    return
                }

                if (HttpConst.HTTP_FEATURE_COROUTINE) {
                    CoroutineScope(Dispatchers.Main).launch {
                        processResponse(protocol, response)
                    }
                } else {
                    processResponse(protocol, response)
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                Trace.debug("++ onFailure() url = ${protocol.getUrl()} msg = ${throwable.message}")
                mTransactionCallback?.transactionEnd()

                when (throwable) {
                    is SocketTimeoutException -> {
                        protocol.requestFailed(HttpConst.Error.HTTP_TIME_OUT.getCode(),
                                HttpConst.Error.HTTP_TIME_OUT.getMessage())
                    }

                    is ConnectException -> {
                        protocol.requestFailed(HttpConst.Error.HTTP_NOT_CONNECTED.getCode(),
                                HttpConst.Error.HTTP_NOT_CONNECTED.getMessage())
                    }

                    is SocketException -> {
                        protocol.requestFailed(HttpConst.Error.HTTP_SOCKET_DISCONNECTED.getCode(),
                                HttpConst.Error.HTTP_SOCKET_DISCONNECTED.getMessage())
                    }

                    is IOException -> {
                        protocol.requestFailed(HttpConst.Error.HTTP_IO_EXCEPTION.getCode(),
                                throwable.message?: HttpConst.Error.HTTP_IO_EXCEPTION.getMessage())
                    }
                }
            }
        })
    }

    fun processResponse(protocol: HttpRequestable, response: Response<ResponseBody?>) {
        protocol.setResponseHeaderMap(response.headers().toMultimap())

        when (protocol.getResponseHeader(HttpConst.HTTP_HEADER_CONTENT_TYPE, 0)) {
            HttpConst.HTTP_MIME_TYPE_JPEG,
            HttpConst.HTTP_MIME_TYPE_PNG,
            HttpConst.HTTP_MIME_TYPE_GIF,
            HttpConst.HTTP_MIME_TYPE_JAR,
            HttpConst.HTTP_MIME_TYPE_APK,
            HttpConst.HTTP_MIME_TYPE_STREAM -> {
                val inputStream: InputStream? = response.body()?.byteStream()
                protocol.parse(inputStream)
            }

            else -> {
                var strResult: String? = "";

                try {
                    strResult = response.body()?.string()
                } catch (e: IOException) {
                    e.printStackTrace()
                    protocol.requestFailed(HttpConst.Error.HTTP_DISCONNECTED.getCode(),
                        e.message?: HttpConst.Error.HTTP_DISCONNECTED.getMessage())
                }

                protocol.parse(strResult)
            }
        }

        mTransactionCallback?.transactionEnd()
    }

    interface RetrofitInterface
    {
        @GET
        fun requestGet(
                @Url strUrl: String,
                @HeaderMap headers: Map<String, String>,
        ): Call<ResponseBody>

        @POST
        fun requestPost(
                @Url strUrl: String,
                @HeaderMap headers: Map<String, String>,
                @Body jsonPart: RequestBody,
        ): Call<ResponseBody>

        @POST
        @Multipart
        fun uploadFile(
                @Url strUrl: String,
                @HeaderMap headers: Map<String, String>,
                @Part filePart: MultipartBody.Part,
        ): Call<ResponseBody>

        @POST
        @Multipart
        fun uploadMultiPart(
                @Url strUrl: String,
                @HeaderMap headers: Map<String, String>,
                @Part(HttpConst.HTTP_MULTIPART_JSON) jsonPart: RequestBody,
                @Part filePart: MultipartBody.Part?,
        ): Call<ResponseBody>
    }
}