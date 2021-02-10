package com.lge.core.net

object HttpConst {
    const val HTTP_FEATURE_OFFLINE = false
    const val HTTP_FEATURE_COROUTINE = true
    const val HTTP_FEATURE_DOWNLOAD_URL = true

    const val HTTP_GET = 0
    const val HTTP_POST = 1
    const val HTTP_FILE_UPLOAD = 2
    const val HTTP_MULTIPART = 3

    const val HTTP_CONNECT_TIMEOUT = 10
    const val HTTP_READ_TIMEOUT = 30

    const val HTTP_HEADER_CONTENT_TYPE = "Content-Type"
    const val HTTP_HEADER_CONTENT_LENGTH = "Content-Length"
    const val HTTP_HEADER_SET_COOKIE = "Set-Cookie"
    const val HTTP_HEADER_COOKIE = "Cookie"
    const val HTTP_HEADER_CHARSET_UTF8 = "charset=UTF-8"

    const val HTTP_MIME_TYPE_HTML = "text/html"
    const val HTTP_MIME_TYPE_JSON = "application/json";
    const val HTTP_MIME_TYPE_JPEG = "image/jpeg"
    const val HTTP_MIME_TYPE_PNG = "image/png"
    const val HTTP_MIME_TYPE_GIF = "image/gif"
    const val HTTP_MIME_TYPE_APK = "application/vnd.android.package-archive"
    const val HTTP_MIME_TYPE_JAR = "application/java-archive"
    const val HTTP_MIME_TYPE_STREAM = "application/octet-stream"
    const val HTTP_MIME_TYPE_OBJECT = "application/object"

    const val HTTP_MULTIPART_JSON = "json-part"
    const val HTTP_MULTIPART_FILE = "file-part"

    const val HTTP_RESPONSE_SUCCESS = 0

    enum class Error(private val nCode: Int, private val strMsg: String) {
        HTTP_DISCONNECTED(-1, "HTTP Disconnected"),
        HTTP_TIME_OUT(-2, "HTTP Timeout"),
        HTTP_NOT_FOUND(-3, "HTTP Not Found"),
        HTTP_NOT_CONNECTED(-4, "HTTP Not Connected"),
        HTTP_NOT_SUPPORTED_CONTENT_TYPE(-5, "HTTP Not Supported Content Type"),
        HTTP_DOWNLOAD_FAIL(-6, "HTTP Download Fail"),
        HTTP_DATA_NOT_EXIST(-7, "HTTP Data Not Exist"),
        HTTP_SOCKET_DISCONNECTED(-8, "HTTP Socket Disconnected"),
        HTTP_IO_EXCEPTION(-9, "HTTP IO Exception"),
        HTTP_FILE_NOT_FOUND(-10, "HTTP File Not Found");

        fun getCode(): Int = nCode

        fun getMessage(): String = "$strMsg [$nCode]"
    }

    enum class Status(private val nCode: Int, private val strMsg: String) {
        HTTP_200(200, "OK"),
        HTTP_301(301, "Permanent Redirect"),
        HTTP_302(302, "Temporary Redirect"),
        HTTP_304(304, "Not Modified"),
        HTTP_401(401, "Unauthorized Error"),
        HTTP_403(403, "Forbidden"),
        HTTP_404(404, "Not Found"),
        HTTP_405(405, "Method Not Allowed"),
        HTTP_501(501, "Not Implemented"),
        HTTP_502(502, "Bad Gateway"),
        HTTP_503(503, "Service Unavailable"),
        HTTP_504(504, "Gateway Timeout");

        fun getCode(): Int = nCode

        fun getMessage(): String = "$strMsg [$nCode]"
    }
}