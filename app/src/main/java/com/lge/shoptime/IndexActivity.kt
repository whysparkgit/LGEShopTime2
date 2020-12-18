package com.lge.shoptime

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.lge.core.fs.StorageManager
import com.lge.core.net.HttpConst
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Const
import com.lge.core.sys.Trace
import com.lge.shoptime.model.dto.Login
import com.lge.shoptime.model.http.LoginProtocol
import com.lge.shoptime.ui.common.SinglePopupDialog
import com.lge.shoptime.ui.home.HomeActivity
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

class IndexActivity : AppCompatActivity()
{
    private var mbClearWebView = false
    private lateinit var mImageView: ImageView
    private lateinit var mTextView: TextView
    private var mCaptureFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        mImageView = findViewById(R.id.iv_image)
        mTextView = findViewById(R.id.tv_log)
        mTextView.textSize = 8f
        registerClickListener()
    }

    fun launchActivity(strUrl: String?) {
//        val intent = Intent(applicationContext, WebViewActivity::class.java)
//        intent.putExtra("URL", strUrl)
//        intent.putExtra("CLR", mbClearWebView)
//        startActivity(intent)
//        mbClearWebView = false
    }

    fun launchActivity(clazz: Class<*>?) {
        val intent = Intent(applicationContext, clazz)
        startActivity(intent)
    }

    fun launchActivity(clazz: Class<*>?, param: Any?) {
        val intent = Intent(applicationContext, clazz)
        intent.putExtra("param", param as Bundle?)
        startActivity(intent)
    }

    fun launchService(clazz: Class<*>?) {
        val intent = Intent(applicationContext, clazz)
        startService(intent)
    }

    fun registerClickListener() {
        val onClickListener: View.OnClickListener = OnButtonClickListener()
        findViewById<View>(R.id.btn_11).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_12).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_13).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_14).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_15).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_16).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_21).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_22).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_23).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_24).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_25).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_26).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_31).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_32).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_33).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_34).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_35).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_36).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_41).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_42).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_43).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_44).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_45).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_46).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_51).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_52).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_53).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_54).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_55).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_56).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_61).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_62).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_63).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_64).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_65).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_66).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_71).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_72).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_73).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_74).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_75).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_76).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_81).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_82).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_83).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_84).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_85).setOnClickListener(onClickListener)
        findViewById<View>(R.id.btn_86).setOnClickListener(onClickListener)
    }

    inner class OnButtonClickListener : View.OnClickListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onClick(v: View) {
            when (v.id) {
                R.id.btn_11 -> inactivateAppControl()
                R.id.btn_12 -> activateAppControl()
                R.id.btn_13 -> deleteAppControl()
                R.id.btn_14 -> launchActivity(Const.NETWORK.DEV_SERVER.DOMAIN.toString() + "/mob/download_mems.html")
                R.id.btn_15 -> {
//                    Device.Connection.isConnected(true)
//                    Device.State.printStatus()
                }
                R.id.btn_16 -> StorageManager.getInstance() //.printStatus()
                R.id.btn_21 -> StorageManager.getInstance().init()
                R.id.btn_22 -> fileDownload()
                R.id.btn_23 -> requestInstall()
                R.id.btn_24 -> previewCamera()
                R.id.btn_25 -> showPopup()
                R.id.btn_26 -> nTPServer
                R.id.btn_31 -> launchActivity(HomeActivity::class.java)
                R.id.btn_32 -> launchActivity("https://17.91.10.77/conferenceWeb_back")
                R.id.btn_33 -> launchActivity("https://17.91.10.77/janus/C/")
                R.id.btn_34 -> launchActivity("http://17.91.10.58:8080/mob")
                R.id.btn_35 -> {
                }
                R.id.btn_36 -> {
                }
                R.id.btn_41 -> requestLogin()
//                R.id.btn_42 -> launchActivity(Const.NETWORK.WEB_SERVER.OP_AF_DOMAIN + Const.NETWORK.WEB_SERVER.PATH)
//                R.id.btn_43 -> launchService(ForegroundService::class.java)
                R.id.btn_44 -> {
                }
                R.id.btn_45 -> {
                }
                R.id.btn_46 -> {
                }
                R.id.btn_51 -> {
                }
                R.id.btn_52 -> {
                }
                R.id.btn_53 -> {
                }
                R.id.btn_54 -> {
                }
                R.id.btn_55 -> {
                }
                R.id.btn_56 -> {
                }
                R.id.btn_61 -> {
                }
                R.id.btn_62 -> {
                }
                R.id.btn_63 -> {
                }
                R.id.btn_64 -> {
                }
                R.id.btn_65 -> {
                }
                R.id.btn_66 -> {
                }
                R.id.btn_71 -> launchDatePicker()
                R.id.btn_72 -> launchNotes()
                R.id.btn_73 -> openImages()
                R.id.btn_74 -> openChooser()
                R.id.btn_75 -> openGallery()
                R.id.btn_76 -> {  }
//                R.id.btn_81 -> wsOpen()
//                R.id.btn_82 -> wsLogin()
//                R.id.btn_83 -> wsClose()
                R.id.btn_84 -> logToView()
                R.id.btn_85 -> mTextView!!.text = ""
                R.id.btn_86 -> {
                }
                else -> {  }
            }
        }
    }

    private fun logToView() {
        try {
//            Process process = Runtime.getRuntime().exec("logcat -d -s main");
            val process = Runtime.getRuntime().exec("logcat -d")
            val bufferedReader = BufferedReader(
                    InputStreamReader(process.inputStream))
            var line = ""

            while (bufferedReader.readLine().also { line = it } != null) {
                mTextView!!.append("$line".trimIndent())
            }

            val svLog = mTextView!!.parent as ScrollView
            svLog.post { svLog.fullScroll(View.FOCUS_DOWN) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun inactivateAppControl() {
        var intent = packageManager.getLaunchIntentForPackage(APP_CONTROL)
        if (intent == null) {
            Toast.makeText(this, "AppControl이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        intent = Intent("ACTION_PAUSE_CONTROL_MOBILE")
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY)
        sendBroadcast(intent)
        Toast.makeText(this, "AppControl이 비활성화 되었습니다.", Toast.LENGTH_SHORT).show()
    }

    fun activateAppControl() {
        var intent = packageManager.getLaunchIntentForPackage(APP_CONTROL)
        if (intent == null) {
            Toast.makeText(this, "AppControl이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val defaultLauncher = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (APP_CONTROL == defaultLauncher!!.activityInfo.packageName) {
            Toast.makeText(this, "AppControl이 활성화되어 있습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val dialog: AlertDialog
        val alertDialog = AlertDialog.Builder(applicationContext)
        dialog = alertDialog.setTitle("경고")
                .setMessage("설정 > 애플리케이션 > 기본 애플리케이션 > 기본설정 > 홈 > 단말통제를 선택하시고 홈 키를 누르신 후에 다시 실행해주세요.")
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, which -> dialog.cancel() }.show()
        val tv = dialog.findViewById<View>(R.id.message) as TextView?
        tv!!.textSize = 28f
        tv.gravity = Gravity.CENTER
    }

    fun deleteAppControl() {
        var intent = packageManager.getLaunchIntentForPackage(APP_CONTROL)
        if (intent == null) {
            Toast.makeText(this, "AppControl이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:" + APP_CONTROL)
        startActivity(intent)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Trace.debug("++ onActivityResult() requestCode = $requestCode, resultCode = $resultCode")
        when (requestCode) {
            UNKNOWN_APP_SOURCE -> if (resultCode == RESULT_OK) {
                installPackage()
            }

            INSTALL_COMPLETE -> Trace.debug(">> onActivityResult() installComplete resultCode = $resultCode")

            REQ_CODE_GALLERY -> {
                if (data == null) return
                Trace.debug(">> REQ_CODE_GALLERY getData = " + data.data)
                Trace.debug(">> REQ_CODE_GALLERY getClipData = " + data.clipData)
                if (data.data != null) {
//                    Trace.debug(">> REQ_CODE_GALLERY AbsolutePath = " + getAbsolutePath(data.data))
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Android 9 : content://com.android.providers.media.documents/document/image:27
     * Android 10 : content://com.android.externalstorage.documents/document/primary:DCIM/Camera/20200708_173940.jpg
     */
//    fun getAbsolutePath(mediaUri: Uri?): String {
//        val docId = DocumentsContract.getDocumentId(mediaUri)
//        val splits = docId.split(":".toRegex()).toTypedArray()
//        val type = splits[0]
//        val selection = MediaStore.Images.Media._ID + "=?"
//        val selectionArgs = arrayOf(splits[1])
//        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//        var contentUri: Uri? = null
//        val cursor: Cursor?
//        if (AppConst.VALUE.MIME_IMAGE.equals(type)) {
//            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        } else if (AppConst.VALUE.MIME_VIDEO.equals(type)) {
//            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//        } else if ("audio" == type) {
//            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        } else if ("primary" == type) {
//            return Environment.getExternalStorageDirectory().toString() + "/" + splits[1]
//        }
//        cursor = contentResolver.query(contentUri!!, filePathColumn, selection, selectionArgs, null)
//        cursor!!.moveToFirst()
//        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
//        val mediaPath = cursor.getString(columnIndex)
//        cursor.close()
//        return mediaPath
//    }

    fun fileDownload() {
//        val fileDownload: FileDownloadProtocol = ProtocolFactory.create(FileDownloadProtocol::class.java)
////        fileDownload.setUrl("http://17.91.24.52:8080/mobilemgr-server/mob/Samsung_SDI_Mobile_MES_2.0.apk");
////        fileDownload.setUrl("http://17.91.10.62:8080/mobilemgr-server/download-checksheet-file/95134137.jar");
//
//        fileDownload.setHttpResponsable(object : HttpResponsable<BaseResponse?> {
//            override fun onResponse(responseResult: BaseResponse) {
//                Trace.debug(">> fileDownload() onResponse() = ${responseResult.toString()}")
//            }
//
//            override fun onFailure(nError: Int, strMsg: String) {
//                Trace.debug(">> fileDownload() onFailure($nError) $strMsg")
//            }
//        })

//        fileDownload.setHttpResponsable(new HttpDownloadable<File>() {
//            @Override
//            public void onResponse(File file) {
//                Trace.Debug(">> fileDownload() onResponse() = " + file.getAbsolutePath());
//            }
//
//            @Override
//            public void onFailure(int nError, String strMsg) {
//                Trace.Debug(">> fileDownload() onFailure(" + nError + ") " + strMsg);
//            }
//
//            @Override
//            public void onStart(long lFileLength) {
//                Trace.Debug(">> fileDownload() onStart() lFileLength = " + lFileLength + "bytes");
//            }
//
//            @Override
//            public void onProgress(long lDonwloaded, long lFileLength) {
//                Trace.Debug(">> fileDownload() onProgress() = " +(int) (lDonwloaded * 100 / lFileLength) + "%");
//            }
//
//            @Override
//            public void onComplete(long lDonwloaded) {
//                Trace.Debug(">> fileDownload() onComplete() = " + lDonwloaded + "bytes");
//                // TODO requestInstall(File)
//                // TODO StorageManager.getInstance().extractFromJar(jarFile);
//            }
//        });

//        NetworkManager.getInstance().asyncRequest(fileDownload)
    }

    //    public void uploadFile() {
    //        File file = new File(StorageManager.getInstance().getExtPath("Samsung_SDI_MMS_2.0.apk"));
    //        FileUploadProtocol fileUpload = ProtocolFactory.getInstance().create(FileUploadProtocol.class);
    //        fileUpload.setRequestBody(file);
    //
    //        // TODO callback
    //        NetworkManager.getInstance().asyncRequest(fileUpload);
    //    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestInstall() {
        val packageManager = applicationContext.packageManager
        if (!packageManager.canRequestPackageInstalls()) {
            // TODO test
//            inactivateAppControl();
            val alertDialog = AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog)
            alertDialog.setTitle("알림")
            alertDialog.setMessage("보안상의 이유로 이 경로를 통한 알 수 없는 앱을 휴대전화에 설치할 수 없습니다.")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("설정") { dialog, which ->
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, UNKNOWN_APP_SOURCE)
            }
            alertDialog.setNegativeButton("취소") { dialog, which -> }
            alertDialog.show()
        } else {
            installPackage()
        }
    }

    fun installPackage() {
        val file: File = File(StorageManager.getInstance().getExtPath("Samsung_SDI_MMS_2.0.apk"))
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), HttpConst.HTTP_MIME_TYPE_APK)

//        Intent intent = new Intent();
//        Uri uri = FileProvider.getUriForFile(getApplicationContext(),  getApplicationInfo().packageName + ".provider", file);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.setDataAndType(uri, HttpConst.HTTP_MIME_TYPE_APK);
//        Trace.Debug(">> installPackage() URI = " + uri.getPath());
//        Trace.Debug(">> installPackage() URI.toString() = " + uri.toString());
//        Trace.Debug(">> installPackage() URI.getHost() = " + uri.getHost());
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivityForResult(intent, INSTALL_COMPLETE)
    }

    //    public void launchLogin() {
    //        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
    //
    //        switch (Device.State.getIpAddress()) {
    //            case "17.91.30.66" :    // park
    //                intent.putExtra(AppConst.KEY.USER_ID, "03127078");
    //                break;
    //            case "17.91.30.67" :    // cha
    //                intent.putExtra(AppConst.KEY.USER_ID, "10122484");
    //                break;
    //            case "17.91.30.68" :    // go
    //                intent.putExtra(AppConst.KEY.USER_ID, "12122297");
    //                break;
    //            default :
    //                intent.putExtra(AppConst.KEY.USER_ID, "06126106");
    //                break;
    //        }
    //
    //        intent.putExtra(AppConst.KEY.PASSWORD, "init00");
    //        startActivity(intent);
    //    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun launchDatePicker() {
        val datePickerDialog = DatePickerDialog(this)
        datePickerDialog.show()
    }

    fun launchNotes() {
        val launchIntent = packageManager.getLaunchIntentForPackage("com.samsung.android.app.notes")
        startActivity(launchIntent)
    }

    fun requestLogin() {
        val loginProtocol: LoginProtocol = ProtocolFactory.create(LoginProtocol::class.java)

        loginProtocol.setHttpResponsable(object : HttpResponsable<Login.Response> {
            override fun onResponse(login: Login.Response) {
                Trace.debug(">> requestLogin() onResponse() : $login")
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestLogin() onFailure($nError) : $strMsg")
            }
        })

        NetworkManager.getInstance().asyncRequest(loginProtocol)
    }

    fun requestImage() {
//        val lCurrent = System.currentTimeMillis()
//        val imageLinkProtocol: ImageLinkProtocol = ProtocolFactory.create(ImageLinkProtocol::class.java)
//        imageLinkProtocol.setUrl("http://17.91.24.52:8080/mobilemgr-server/mob/manufacture.png")
//
//        imageLinkProtocol.setHttpResponsable(object : HttpResponsable<Bitmap>() {
//            override fun onResponse(bitmap: Bitmap) {
//                Trace.debug(">> requestImage() onResponse() = ${System.currentTimeMillis() - lCurrent}")
//                mImageView.setImageBitmap(bitmap)
//                mImageView.invalidate()
//            }
//
//            override fun onFailure(nError: Int, strMsg: String) {
//                Trace.debug(">> requestImage() onFailure() = ${System.currentTimeMillis() - lCurrent}")
//            }
//        })
//
//        NetworkManager.getInstance().asyncRequest(imageLinkProtocol)
    }

    fun previewCamera() {
        mCaptureFile = File(StorageManager.getInstance().getExtPath("capture.jpg"))
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCaptureFile))
        startActivityForResult(intent, CAMERA_SNAPSHOT)

//        launchActivity(CameraSnapshotActivity.class);
    }

    //    public void requestLogout() {
    //        LogoutProtocol logoutProtocol = ProtocolFactory.getInstance().create(LogoutProtocol.class);
    //
    //        logoutProtocol.setHttpResponsable(new HttpResponsable<BaseResponse>() {
    //            @Override
    //            public void onResponse(BaseResponse responseResult) {
    //                Trace.Debug(">> requestLogout() onResponse() : \n" + responseResult.toString());
    //            }
    //
    //            @Override
    //            public void onFailure(int nError, String strMsg) {
    //                Trace.Debug(">> requestLogout() onFailure(" + nError + ") : " + strMsg);
    //            }
    //        });
    //
    //        NetworkManager.getInstance().asyncRequest(logoutProtocol);
    //    }
    fun viewPDF() {
        val path = Environment.getExternalStorageDirectory().toString() + "/Download/pdf_open_parameters.pdf"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(path))
        intent.type = "application/pdf"
        startActivity(intent)
    }

    fun openImages() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT) // /document/image:24
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_GALLERY)
    }

    fun openChooser() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT) // /document/image:24
        intent.type = "video/*"
        //        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_GALLERY)
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK) // /external/images/media/26
        //        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);   // /external/video/media/30
        intent.type = "image/* video/*"
        //        intent.setType("*/*");
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
        startActivityForResult(intent, REQ_CODE_GALLERY)
    }

    /** download process
     * public void checksheetDownload1() {
     * // DownloadRequestProtocol
     * DownloadRequestProtocol downloadRequest = ProtocolFactory.create(DownloadRequestProtocol.class);
     *
     * downloadRequest.setHttpResponsable(new HttpResponsable<DownloadUrl>() {
     * @Override
     * public void onResponse(DownloadUrl downloadUrl) {
     * Trace.Debug(">> checksheetDownload1() onResponse() : \n" + downloadUrl.toString());
     *
     * if (downloadUrl.getResponseResult().getCode() == HttpConst.HTTP_RESPONSE_SUCCESS) {
     * checksheetDownload2(downloadUrl);
     * } else {
     * Trace.Debug(">> checksheetDownload1() onResponse(" + downloadUrl.getResponseResult().getCode() + ") : "
     * + downloadUrl.getResponseResult().getMessage());
     * }
     *
     * }
     *
     * @Override
     * public void onFailure(int nError, String strMsg) {
     * Trace.Debug(">> checksheetDownload1() onFailure(" + nError + ") : " + strMsg);
     * }
     * });
     *
     * NetworkManager.getInstance().asyncRequest(downloadRequest);
     * }
     *
     * public void checksheetDownload2(DownloadUrl downloadUrl) {
     * // FileDownloadProtocol
     * FileDownloadProtocol fileDownload = ProtocolFactory.create(FileDownloadProtocol.class);
     * // TODO url
     * fileDownload.setUrl(Const.NETWORK.DEV_SERVER.DOMAIN + downloadUrl.getUrl());
     *
     * fileDownload.setHttpResponsable(new HttpDownloadable<File>() {
     * @Override
     * public void onResponse(File file) {
     * Trace.Debug(">> checksheetDownload2() onResponse() : \n" + file.getAbsolutePath());
     * }
     *
     * @Override
     * public void onFailure(int nError, String strMsg) {
     * Trace.Debug(">> checksheetDownload2() onFailure(" + nError + ") : " + strMsg);
     * }
     *
     * @Override
     * public void onStart(long lFileLength) {
     * Trace.Debug(">> checksheetDownload2() onStart() lFileLength = " + lFileLength + "bytes");
     * }
     *
     * @Override
     * public void onProgress(long lDonwloaded, long lFileLength) {
     * Trace.Debug(">> checksheetDownload2() onProgress() = " +(int) (lDonwloaded * 100 / lFileLength) + "%");
     * }
     *
     * @Override
     * public void onComplete(long lDonwloaded) {
     * Trace.Debug(">> checksheetDownload2() onComplete() = " + lDonwloaded + "bytes");
     * checksheetDownload3();
     * }
     * });
     *
     * NetworkManager.getInstance().asyncRequest(fileDownload);
     * }
     *
     * public void checksheetDownload3() {
     * // DownloadCompleteProtocol
     * DownloadCompleteProtocol downloadComplete = ProtocolFactory.create(DownloadCompleteProtocol.class);
     *
     * downloadComplete.setHttpResponsable(new HttpResponsable<ResponseResult>() {
     * @Override
     * public void onResponse(ResponseResult responseResult) {
     * Trace.Debug(">> checksheetDownload3() onResponse() : \n" + responseResult.toString());
     * }
     *
     * @Override
     * public void onFailure(int nError, String strMsg) {
     * Trace.Debug(">> checksheetDownload3() onFailure(" + nError + ") : " + strMsg);
     * }
     * });
     *
     * NetworkManager.getInstance().asyncRequest(downloadComplete);
     * }
    </ResponseResult></File></DownloadUrl> */
    fun showPopup() {
        val builder: SinglePopupDialog.Builder = SinglePopupDialog.Builder.create()
        builder.setTitle("SELECT")
                .setMessage("close popup or one more popup")
//                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        builder.destroy();
//                    }
//                })
                .setNeutralButton("More", DialogInterface.OnClickListener { dialog, which ->
                    SinglePopupDialog.Builder.create()
                            .setTitle("WARNING")
                            .setMessage("popup shows only one")
                            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                            .setCancelable(false)
                            .show()
                }).show()
    }

    /**
     * public void showPopup() {
     * if (PopupDialog.isShown()) return;
     *
     * PopupDialog dialog = PopupDialog.newInstance();
     *
     * dialog.setTitle("Warning");
     * dialog.setMessage("서버 통신 오류가 발생하였습니다. 재시도 하시겠습니까?");
     *
     * //        dialog.setNeutralButton("확인", new View.OnClickListener() {
     * //            @Override
     * //            public void onClick(View v) {
     * //                Toast.makeText(getApplicationContext(), "Dialog.onClick : 확인", Toast.LENGTH_SHORT).show();
     * //            }
     * //        });
     *
     * dialog.setPositiveButton("예", new View.OnClickListener() {
     * @Override
     * public void onClick(View v) {
     * Trace.Debug(">> dialog.getDialog().isShowing() = " + dialog.getDialog().isShowing());
     * Toast.makeText(getApplicationContext(), "Dialog.onClick : 예", Toast.LENGTH_SHORT).show();
     * dialog.dismiss();
     * }
     * });
     *
     * dialog.setNegativeButton("아니오", new View.OnClickListener() {
     * @Override
     * public void onClick(View v) {
     * Toast.makeText(getApplicationContext(), "Dialog.onClick : 아니오", Toast.LENGTH_SHORT).show();
     * dialog.setCancelable(false);
     * }
     * });
     *
     * dialog.show(getSupportFragmentManager());
     *
     * if (PopupDialog.isShown()) return;
     * PopupDialog dialog2 = PopupDialog.newInstance();
     *
     * dialog2.setTitle("Warning");
     * dialog2.setMessage("네트워크 오류가 발생하였습니다. 재시도 하시겠습니까?");
     *
     * dialog2.setPositiveButton("예", new View.OnClickListener() {
     * @Override
     * public void onClick(View v) {
     * Trace.Debug(">> dialog.getDialog().isShowing() = " + dialog2.getDialog().isShowing());
     * Toast.makeText(getApplicationContext(), "Dialog.onClick : 예", Toast.LENGTH_SHORT).show();
     * dialog2.dismiss();
     * }
     * });
     *
     * dialog.setNegativeButton("아니오", new View.OnClickListener() {
     * @Override
     * public void onClick(View v) {
     * Toast.makeText(getApplicationContext(), "Dialog.onClick : 아니오", Toast.LENGTH_SHORT).show();
     * dialog2.setCancelable(false);
     * }
     * });
     *
     * dialog2.show(getSupportFragmentManager());
     * }
     */
    val nTPServer: Unit
        get() {
            val res = this.resources
            val id = Resources.getSystem().getIdentifier("config_ntpServer", "string", "android")
            val defaultServer = res.getString(id)
            Trace.debug(">> getNTPServer() = $defaultServer")
        }

//    @SuppressLint("CheckResult")
//    fun wsOpen() {
//        Trace.Debug("++ connect()")
//        WebSocketManager.getInstance().connect(object : WebSocketCallBack() {
//            fun onOpen() {
//                Trace.Debug(">> onOpen()")
//            }
//
//            fun onClose() {
//                Trace.Debug(">> onClose()")
//            }
//
//            fun onFailure(nError: Int, strMsg: String) {
//                Trace.Debug(">> onFailure() nError = $nError strMsg = $strMsg")
//            }
//
//            fun onReceive(protocol: WebSocketReceivable) {
//                Trace.Debug(">> onReceive() protocol.getId = " + protocol.getId())
//                Trace.Debug(">> onReceive() protocol.getBody = " + protocol.getBody())
//            }
//        })
//    }
//
//    fun wsLogin() {
//        Trace.Debug("++ sendMessage()")
//        var msg = """
//               CONNECT
//               accept-version:1.1, 2.0
//               host:17.91.240.38:8080/mems/mems-websocket/websocket
//
//                
//               """.trimIndent()
//        WebSocketManager.getInstance().getWebSocket().send(msg)
//        Trace.Debug("++ sendMessage()\n$msg")
//        try {
//            Thread.sleep(100)
//        } catch (e: InterruptedException) {
//        }
//        msg = """
//               SUBSCRIBE
//               id:06126106
//               destination:/mems/user/1111/push
//
//                
//               """.trimIndent()
//        WebSocketManager.getInstance().getWebSocket().send(msg)
//        Trace.Debug("++ sendMessage()\n$msg")
//    }
//
//    fun wsClose() {
//        Trace.Debug("++  wsClose()")
//        WebSocketManager.getInstance().disconnect()
//        //        ProtocolFactory.removeAll();
//    }

    companion object {
        private const val APP_CONTROL = "com.sdi.control.mobile"
        const val UNKNOWN_APP_SOURCE = 500
        const val INSTALL_COMPLETE = 600
        const val QRCODE_SCAN_RESULT = 49374
        const val CAMERA_SNAPSHOT = 700
        const val REQ_CODE_GALLERY = 800
    }
}