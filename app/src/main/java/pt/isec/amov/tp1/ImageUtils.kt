package pt.isec.amov.tp1

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.net.URI.create
import java.nio.channels.AsynchronousFileChannel.open
import java.nio.channels.FileChannel.open

fun saveImage(context: Context, bitmap: Bitmap, filename: String = "avatar.jpg") {
    val cw = ContextWrapper(context)
    val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
    val file = File(directory, filename)
    if (file.exists()) file.delete()
    FileOutputStream (file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
}

fun loadImage(context: Context, filename: String = "avatar.jpg") : Bitmap? {
    val cw = ContextWrapper(context)
    val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
    val file = File(directory, filename)
    if (!file.exists()) return null
    return BitmapFactory.decodeFile(file.absolutePath)
}

fun createFileFromUri(
    context: Context,
    uri: Uri,
    filename: String = getTempFileName(context)
) : String {
    FileOutputStream(filename).use{ outputStream ->
        context.contentResolver.openInputStream(uri)?.use { inputStream -> inputStream.copyTo(outputStream) }
    }
    return filename
}

fun getTempFileName(context: Context): String = File.createTempFile("image", ".img", context.externalCacheDir).absolutePath