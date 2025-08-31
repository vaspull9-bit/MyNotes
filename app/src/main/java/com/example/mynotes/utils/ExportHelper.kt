package com.example.mynotes.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportHelper(private val context: Context) {

    fun exportToTxt(title: String, content: String): Boolean {
        return try {
            val fileName = "Note_${getTimestamp()}.txt"
            val file = getExportDirectory().resolve(fileName)

            FileOutputStream(file).use { output ->
                val text = "$title\n\n$content"
                output.write(text.toByteArray(Charsets.UTF_8))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun getTimestamp(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }

    fun getExportDirectory(): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_DOCUMENTS)
                .firstOrNull() ?: context.filesDir
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        }.apply {
            if (!exists()) mkdirs()
        }
    }

    fun exportToPdf(title: String, content: String): Boolean {
        // TODO: Implement PDF export
        return try {
            Toast.makeText(context, "PDF export will be implemented soon", Toast.LENGTH_SHORT).show()
            false
        } catch (e: Exception) {
            false
        }
    }

    fun exportToXls(title: String, content: String): Boolean {
        // TODO: Implement Excel export
        return false
    }

    fun exportToDocx(title: String, content: String): Boolean {
        // TODO: Implement Word export
        return false
    }

    fun exportToPng(title: String, content: String): Boolean {
        // TODO: Implement PNG export
        return false
    }
}