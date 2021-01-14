package com.lge.lgshoptimem

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.lge.core.net.HttpDownloadable
import com.lge.core.net.HttpResponsable
import com.lge.core.net.NetworkManager
import com.lge.core.net.ProtocolFactory
import com.lge.core.sys.Config
import com.lge.core.sys.Device
import com.lge.core.sys.Trace
import com.lge.lgshoptimem.model.dto.AppVersionList
import com.lge.lgshoptimem.model.http.AppVersionProtocol
import com.lge.lgshoptimem.model.http.FileDownloadProtocol
import com.lge.lgshoptimem.ui.common.AppConst
import com.lge.lgshoptimem.ui.common.SinglePopupDialog
import com.lge.lgshoptimem.ui.home.HomeActivity
import java.io.File

class SplashActivity : AppCompatActivity()
{
    private var mProgressBar: ProgressBar? = null
    private lateinit var mVersion: TextView
    private var mDescription:TextView? = null
    private val mHandler = Handler()
    private var mFinishTimer: Long = 0
    private var ctInput = ""
    private val ctKey = "2425242425"
    private var mFile: File? = null
    private var mAppVerInfoList: List<AppVersionList.AppVerInfo>? = null
    private var mAppVerCnt = 0

    private var mFailCheckFlag = false

    private val mRunnable = Runnable {
        launchActivity()
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        Trace.debug(">> onCreate() ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mVersion = findViewById(R.id.as_tv_version)
        mDescription = findViewById(R.id.as_tv_description)
        mProgressBar = findViewById(R.id.as_update_progress)

        // ignore URI exposure exception
//        val builder = VmPolicy.Builder()
//        StrictMode.setVmPolicy(builder.build())
//        askPermission()
        startSplash()
    }

    override fun onStart() {
        Trace.debug(">> onStart() ")
        super.onStart()

        if (mFailCheckFlag) {
            checkNextVersion()
        }
    }

    override fun onResume() {
        super.onResume()
        Trace.debug(">> onResume() ")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun askPermission() {
        val nCheckSelfPermission: Int =
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) +
                checkSelfPermission(Manifest.permission.CAMERA) +
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) +
                checkSelfPermission(Manifest.permission.READ_PHONE_STATE) +
                checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE);

        val bShouldShowRequestPermissionRationale =
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) ||
                shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE) ||
                shouldShowRequestPermissionRationale(Manifest.permission.CHANGE_WIFI_STATE);

        val strManifestPermissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CHANGE_WIFI_STATE)

        if (nCheckSelfPermission != PackageManager.PERMISSION_GRANTED) {
            if (bShouldShowRequestPermissionRationale) {
                // TODO
                requestPermissions(strManifestPermissions, 1)
            } else {
                requestPermissions(strManifestPermissions, 1)
            }
        } else {
            startSplash()
        }
    }

    fun startSplash() {
        showVersion()

        if (Config.SUPPORT_AUTO_UPGRADE) {
            requestVersionList()
        } else {
            mHandler.postDelayed(mRunnable, 3000)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        startSplash()
    }

    fun requestVersionList() {
        val appVersionProtocol: AppVersionProtocol = ProtocolFactory.create(AppVersionProtocol::class.java)

        appVersionProtocol.setHttpResponsable(object : HttpResponsable<AppVersionList> {
            override fun onResponse(appVerRes: AppVersionList) {
                Trace.debug(">> versionCheck() onResponse() : ${appVerRes.resultList}")
                mAppVerInfoList = appVerRes.resultList
                mAppVerCnt = mAppVerInfoList!!.size
                checkVersionInfo()
            }

            override fun onFailure(nError: Int, strMsg: String) {
                showDialog("네크워크 연결을 확인해 주세요.", null, null, 0)
            }
        })

        NetworkManager.getInstance().asyncRequest(appVersionProtocol)
    }

    private fun checkVersionInfo() {
        if (mAppVerCnt > 0) {
            val appVerInfo: AppVersionList.AppVerInfo = mAppVerInfoList!![mAppVerCnt - 1]
            val code: Int = appVerInfo.appVrsnCd

            if (getPackageName() == appVerInfo.appPkgNm) {
//                CommonData.getInstance().setAppVerInfo(appVerInfo);
                if (code > getAppVersionCode()) {
                    showUpgrade(appVerInfo.appNm + " v" + appVerInfo.appVrsnNm + " 으로 업데이트를 진행중 입니다.")
                    requestBinary(appVerInfo.appFileId)
                } else {
                    checkNextVersion()
                }
            } else {
                val versionCode = getInstallPackageVersionCode(appVerInfo.appPkgNm)
                Trace.debug(">> checkVersionInfo() versionCode : \n$versionCode")
                if (versionCode != -1) {
                    if (code > versionCode) {
                        showUpgrade(appVerInfo.appNm + " v" + appVerInfo.appVrsnNm + " 설치를 진행중 입니다.")
                        requestBinary(appVerInfo.appFileId)
                    } else {
                        checkNextVersion()
                    }
                } else {
                    showUpgrade(appVerInfo.appNm + " v" + appVerInfo.appVrsnNm + " 으로 업데이트를 진행중 입니다.")
                    requestBinary(appVerInfo.appFileId)
                }
            }
        } else {
            mDescription?.text = ""
            mHandler.postDelayed(mRunnable, 2000)
        }
    }

    private fun checkNextVersion() {
        val appVerInfo: AppVersionList.AppVerInfo = mAppVerInfoList!![mAppVerCnt - 1]
        val code: Int = appVerInfo.appVrsnCd
        if (appVerInfo.forceUptYn.equals("Y", ignoreCase = true)) {
            if (getPackageName() == appVerInfo.appPkgNm) {
                // this app
                if (code > getAppVersionCode()) {
                    showDialog(getString(R.string.require_upgrade_app), null, null, 0)
                    return
                }
            } else {
                // other app
                val versionCode = getInstallPackageVersionCode(appVerInfo.appPkgNm)
                if (versionCode != -1) {
                    if (code > versionCode) {
                        showDialog(getString(R.string.require_upgrade), null, null, 0)
                        return
                    }
                } else {
                    // not installed
                    showDialog(getString(R.string.not_installed), null, null, 0)
                    return
                }
            }
        }

        mAppVerCnt--
        checkVersionInfo()
    }

    fun getAppVersionCode(): Int {
        lateinit var packageInfo: PackageInfo

        packageInfo = try {
            getPackageManager().getPackageInfo(getPackageName(), 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return -1
        }

        Trace.debug(">> getVersionInfo() " + packageInfo.versionCode)
        return packageInfo.versionCode
    }

    fun getInstallPackageVersionCode(packageName: String): Int {
        return try {
            val pm: PackageManager = getPackageManager()
            val pi = pm.getPackageInfo(packageName.trim { it <= ' ' }, PackageManager.GET_META_DATA)
            val appInfo = pi.applicationInfo
            Trace.debug(">> getInstallPackage() ${appInfo.enabled}")
            pi.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            Trace.debug(">> getInstallPackage() not installed package")
            -1
        }
    }

    fun requestBinary(appFileId: String) {
        val fileDownload: FileDownloadProtocol = ProtocolFactory.create(FileDownloadProtocol::class.java)
        //        fileDownload.setAppFileId(appFileId);
        fileDownload.setFileId(appFileId)

        fileDownload.setHttpResponsable(object : HttpDownloadable<File> {
            override fun onResponse(file: File) {
                try {
                    Trace.debug(">> requestBinary() onResponse() : ${file.absolutePath}")
                    //mpbDownloading.setProgress(60, true);
                    mFile = file
                } catch (e: Exception) {
                    checkNextVersion()
                }
            }

            override fun onFailure(nError: Int, strMsg: String) {
                Trace.debug(">> requestBinary() onFailure($nError) : $strMsg")
                SinglePopupDialog.Builder.create()
                        .setMessage("서버 요청에 실패하였습니다.($nError)\n$strMsg")
                        .setPositiveButton("재시도", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            requestBinary(appFileId)
                        })
                        .setNegativeButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            checkNextVersion()
                        })
                        .setCancelable(false)
                        .show()
            }

            override fun onStart(lFileLength: Long) {
                Trace.debug(">> requestBinary() onStart() lFileLength = " + lFileLength + "bytes")
            }

            override fun onProgress(lDonwloaded: Long, lFileLength: Long) {
                Trace.debug(">> requestBinary() onProgress() = " + (60 + (lDonwloaded * 30 / lFileLength).toInt()) + "%")
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onComplete(lDonwloaded: Long) {
                Trace.debug(">> requestBinary() onComplete() = " + lDonwloaded + "bytes")
                requestInstall()
            }
        })

        NetworkManager.getInstance().asyncRequest(fileDownload)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestInstall() {
        val packageManager: PackageManager = getApplicationContext().getPackageManager()

        if (!packageManager.canRequestPackageInstalls()) {
            val alertDialog = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog)
            alertDialog.setTitle("알림")
            alertDialog.setMessage("보안상의 이유로 이 경로를 통한 알 수 없는 앱을 휴대전화에 설치할 수 없습니다.")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("설정") { dialog, which ->
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.data = Uri.parse("package:" + getPackageName())
                startActivityForResult(intent, AppConst.UPGRADE.UNKNOWN_APP_SOURCE)
            }

            alertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, which -> checkNextVersion() }
            alertDialog.show()
        } else {
            installPackage()
        }
    }

    fun installPackage() {
        Trace.debug(">> mFile.getAbsolutePath() = " + mFile!!.absolutePath)
        // fixme
//        val intent = Intent(getApplicationContext(), InstallerActivity::class.java)
//        intent.data = Uri.fromFile(mFile)
//        startActivityForResult(intent, AppConst.UPGRADE.REQUEST_INSTALL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Trace.debug("++ onActivityResult() requestCode = $requestCode, resultCode = $resultCode")

        when (requestCode) {
            AppConst.UPGRADE.UNKNOWN_APP_SOURCE -> if (resultCode == Activity.RESULT_OK) {
                installPackage()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                checkNextVersion()
            }
            AppConst.UPGRADE.DOWNLOAD_COMPLETE -> mFailCheckFlag = true
            AppConst.UPGRADE.REQUEST_INSTALL -> checkNextVersion()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun launchActivity() {
        val intent: Intent

        if (ctInput == ctKey) {
            intent = Intent(getApplicationContext(), IndexActivity::class.java)
        } else {
            if (Config.SUPPORT_DEBUG) {
                intent = Intent(getApplicationContext(), IndexActivity::class.java)
            } else {
                intent = Intent(getApplicationContext(), HomeActivity::class.java)
            }
        }

        startActivity(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        ctInput += keyCode.toString()
        return super.onKeyDown(keyCode, event)
    }

    fun showVersion() {
        mVersion.text = "v ${Device.State.getVersionName()}\n${Device.State.getBuildNumber()}"
    }

    fun showUpgrade(text: String?) {
        mProgressBar!!.visibility = View.VISIBLE
        mDescription?.text = text
    }

    fun showDialog(strMsg: String?, leftBtn: String?, rightBtn: String?, code: Int) {
        val builder: SinglePopupDialog.Builder = SinglePopupDialog.Builder.create()
        builder.setMessage(strMsg)
                .setCancelable(false)

        if (code == 0) {
            builder.setNeutralButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
//                    mHandler.postDelayed(mRunnable, 2000);
                finish()
            }).show(supportFragmentManager)
        } else if (code == 1) {
            builder.setNeutralButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                checkNextVersion()
            }).show(supportFragmentManager)
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() > mFinishTimer + 2000) {
            mFinishTimer = System.currentTimeMillis()
            Toast.makeText(this, R.string.more_back_finish, Toast.LENGTH_SHORT).show()
            return
        }

        if (System.currentTimeMillis() <= mFinishTimer + 2000) {
            mHandler.removeCallbacks(mRunnable)
            finish()
            //Toast.cancel();
        }
    }
}