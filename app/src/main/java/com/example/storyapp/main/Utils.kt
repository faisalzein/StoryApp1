package com.example.storyapp.main

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun getImageUri(context: Context): Uri? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
       makeImageUriForQAndAbove(context)
    } else {
        makeImageUriForPreQ(context)
    }
}

private fun makeImageUriForQAndAbove(context: Context): Uri? {
    val timeStamp = System.currentTimeMillis().toString()
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
    }
    return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}

private fun makeImageUriForPreQ(context: Context): Uri {
    val timeStamp = System.currentTimeMillis().toString()
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg").apply {
        parentFile?.takeIf { !it.exists() }?.mkdirs()
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
}

@RequiresApi(Build.VERSION_CODES.Q)
fun File.reduceFileImage(): File {
    val bitmap = BitmapFactory.decodeFile(path).getRotatedBitmap(this)
    compressImage(bitmap)
    return this
}

private const val MAXIMAL_SIZE = 1024 * 1024

private fun File.compressImage(bitmap: Bitmap?) {
    var compressQuality = 100
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        compressQuality -= 5
    } while (bmpStream.size() > MAXIMAL_SIZE && compressQuality > 0)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(this))
}

@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(270F)
        else -> this
    }
}

private fun Bitmap.rotateImage(angle: Float): Bitmap? {
    return Matrix().let {
        it.postRotate(angle)
        Bitmap.createBitmap(this, 0, 0, width, height, it, true)
    }
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = makeCustomTempFile(context)
    context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
        FileOutputStream(myFile).use { outputStream ->
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        }
    }
    return myFile
}

fun makeCustomTempFile(context: Context): File {
    val timeStamp = System.currentTimeMillis().toString()
    return File.createTempFile(timeStamp, ".jpg", context.externalCacheDir)
}
