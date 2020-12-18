package com.lge.core.fs

import com.lge.core.sys.Const
import com.lge.core.sys.Trace
import java.io.*
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class StorageManager
{
    companion object {
        private var mInstance: StorageManager? = null

        @JvmStatic
        fun getInstance(): StorageManager = mInstance?:
        synchronized(this) {
            mInstance?: StorageManager().also {
                mInstance = it
            }
        }
    }

    fun init() {
        File(Const.STORAGE.APP_MAIN)
        File(Const.STORAGE.EXT_ROOT)
    }

    fun getAppPath(strFileName: String): String = Const.STORAGE.APP_MAIN + "/" + strFileName

    fun getExtPath(strFileName: String): String = Const.STORAGE.EXT_ROOT + "/" + strFileName

    @Throws(IOException::class)
    fun jsonToCache(strJson: String, strFileName: String?): File? {
        val file = File(getAppPath(strFileName!!))

        ByteArrayInputStream(strJson.toByteArray()).use { bais ->
            FileOutputStream(file).use { fos ->
                val bBuf = ByteArray(Const.STORAGE.FILE_READ_BUFFER_SIZE)
                var n = 0

                while (bais.read(bBuf).also { n = it } > 0) {
                    fos.write(bBuf, 0, n)
                }

                fos.flush()
                Trace.debug("-- jsonToCache() = ${file.name} (${file.length()}bytes )")
            }
        }
        return file
    }

    @Throws(IOException::class)
    fun jsonFromCache(strFileName: String?): String? {
        var strResult: String? = null
        val file = File(getAppPath(strFileName!!))

        if (find(file) == null) {
            Trace.debug("-- jsonFromCache() file not found.")
            return strResult
        }

        FileInputStream(file).use { fis ->
            ByteArrayOutputStream().use { baos ->
                val bBuf = ByteArray(Const.STORAGE.FILE_READ_BUFFER_SIZE)
                var n = 0

                while (fis.read(bBuf).also { n = it } > 0) {
                    baos.write(bBuf, 0, n)
                }

                baos.flush()
                strResult = baos.toString()
                Trace.debug("-- jsonFromCache() = ${baos.size()}bytes")
            }
        }

        return strResult
    }

    fun removeFile(file: File): Boolean {
        return removeFile(file.absolutePath)
    }

    // remove file or directory
    fun removeFile(strPath: String): Boolean {
        val file = File(strPath)

        if (!file.exists()) {
            Trace.debug("-- removeFile() file not exist.")
            return false
        }

        if (file.isDirectory) {
            for (f in file.listFiles()) {
                if (f.isDirectory) {
                    removeFile(strPath + "/" + f.name)
                }
                f.delete()
            }
        }

        file.delete()

        return true
    }

    fun isExist(strPath: String?): Boolean {
        val file = File(strPath)

        return file.exists()
    }

    // find file (not directory)
    fun find(file: File): File? {
        if (file.isDirectory) {
            return null
        }

        Trace.debug("++ find() getAbsolutePath = ${file.absolutePath}")

        return find(file.parent, file.name)
    }

    // find file (not directory)
    fun find(strPath: String, strName: String?): File? {
        Trace.debug("++ find() strPath = $strPath, strName = $strName")
        val file = File(strPath)
        var target: File? = null

        if (file.isFile || !file.exists() || strName == null) {
            Trace.debug("-- find() file not exist.")
            return null
        }

        for (f in file.listFiles()) {
            if (f.name == strName) {
                target = File(f.absolutePath)
                break
            }

            if (f.isDirectory) {
                val result = find(strPath + "/" + f.name, strName)
                if (result != null) {
                    target = result
                    break
                }
            }
        }

        return target
    }

    // return file or directory size
    fun sizeOf(strPath: String): Int {
        val file = File(strPath)
        var lSize: Long = 0

        if (!file.exists()) {
            return lSize.toInt()
        }

        if (file.isFile) {
            lSize = file.length()
            Trace.debug(">> file sizeOf() = $lSize")
        } else if (file.isDirectory) {
            for (f in file.listFiles()) {
                lSize += if (f.isDirectory) {
                    sizeOf(strPath + "/" + f.name).toLong()
                } else {
                    f.length()
                }
            }

            Trace.debug(">> directory sizeOf() = $lSize")
        }

        return lSize.toInt()
    }

    // 1. files in path directory to jar
    // 2. remove path directory
    fun compressToJar(strPath: String): Boolean {
        Trace.debug("++ compressToJar() strPath = $strPath")
        val dir = File(strPath)
        val files = dir.listFiles()

        if (files == null || files.size == 0) {
            Trace.debug("-- compressToJar() nested files not exist.")
            return false
        }

        try {
            FileOutputStream("$strPath.jar").use { fos ->
                JarOutputStream(fos).use { jos ->
                    BufferedOutputStream(jos).use { bos ->
                        for (file in files) {
                            FileInputStream(file).use { fis ->
                                jos.putNextEntry(JarEntry(file.name))

                                val bBuf = ByteArray(Const.STORAGE.FILE_READ_BUFFER_SIZE)
                                var n = 0

                                while (fis.read(bBuf).also { n = it } > 0) {
                                    bos.write(bBuf, 0, n)
                                }

                                bos.flush()
                                Trace.debug(">> compressToJar() $strPath.jar size = ${sizeOf("$strPath.jar")}")
                            }
                        }

                        removeFile(strPath)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return true
    }

    // 1. extract from jar file in jar name directory
    // 2. remove jar file
    fun extractFromJar(jarPath: String): Boolean {
        Trace.debug("++ extractFromJar() jarPath = $jarPath")
        val file = File(jarPath)

        return extractFromJar(file)
    }

    fun extractFromJar(file: File): Boolean {
        Trace.debug("++ extractFromJar() jarFile = " + file.absolutePath)
        val destPath = file.absolutePath.substring(0, file.absolutePath.lastIndexOf("."))
        val destDir = File(destPath)

        if (file.exists() == false) {
            Trace.debug("-- extractFromJar() nested files not exist.")
            return false
        }

        try {
            destDir.mkdir()
            val jarFile = JarFile(file)

            for (entry in Collections.list(jarFile.entries())) {
                val newFile = File(destPath, entry.name)
                jarFile.getInputStream(entry).use { `is` ->
                    FileOutputStream(newFile).use { fos ->
                        val bBuf = ByteArray(Const.STORAGE.FILE_READ_BUFFER_SIZE)
                        var n = 0

                        while (`is`.read(bBuf).also { n = it } > 0) {
                            fos.write(bBuf, 0, n)
                        }

                        fos.flush()
                        Trace.debug(">> extractFromJar() entry.getName = " + entry.name + ", size = " + sizeOf(destPath + "/" + entry.name))
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        removeFile(file.absolutePath)

        Trace.debug("-- extractFromJar() jar size = ${sizeOf(destPath)}")
        return true
    }

    fun cleanStorage() {
        removeFile(Const.STORAGE.APP_MAIN)
    }
}