package com.example.codriving.common

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.codriving.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


// on below line we are creating a generate PDF
// method which is use to generate our PDF file.
fun generatePDF(context: Context) {

    if (!checkAndRequestPermissions(context as Activity)) {
        return
    }

    val pageHeight = 1120
    val pageWidth = 792

    val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.alquiler_de_coche)
    val scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false)

    val pdfDocument = PdfDocument()
    val paint = Paint()
    val title = Paint()

    val myPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    val myPage = pdfDocument.startPage(myPageInfo)

    val canvas = myPage.canvas
    canvas.drawBitmap(scaledbmp, 56F, 40F, paint)

    title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    title.textSize = 18F
    title.color = Color.BLACK
    title.textAlign = Paint.Align.CENTER
    canvas.drawText("Acuerdo de Alquiler de Coche", pageWidth / 2F, 200F, title)

    val textPaint = Paint()
    textPaint.textSize = 14F
    textPaint.color = Color.BLACK

    val text = """
        Este Acuerdo de Alquiler de Coche se celebra entre:
        
        [Nombre del Arrendador]
        Dirección: [Dirección del Arrendador]
        
        Y
        
        [Nombre del Arrendatario]
        Dirección: [Dirección del Arrendatario]
        
        Detalles del coche:
        - Marca: [Marca del Coche]
        - Modelo: [Modelo del Coche]
        - Año: [Año del Coche]
        - Matrícula: [Matrícula del Coche]
        
        Términos y condiciones:
        1. El arrendatario se compromete a devolver el coche en las mismas condiciones en que lo recibió.
        2. El período de alquiler comienza el [Fecha de Inicio] y termina el [Fecha de Finalización].
        3. El arrendatario se responsabiliza de cualquier daño o pérdida del coche durante el período de alquiler.
        4. El arrendatario debe pagar la cantidad de [Monto del Alquiler] antes de [Fecha de Pago].
        
        Firma del Arrendador: _________________________
        Firma del Arrendatario: _________________________
    """.trimIndent()

    val textLines = text.split("\n")
    var yPosition = 250F

    for (line in textLines) {
        canvas.drawText(line, 56F, yPosition, textPaint)
        yPosition += textPaint.descent() - textPaint.ascent()
    }
    pdfDocument.finishPage(myPage)

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "rentCarFile.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                contentValues
            )

            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                    Toast.makeText(
                        context,
                        "PDF file generated in Documents: ${it.path}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            val documentsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (!documentsDir.exists()) {
                documentsDir.mkdirs()
            }
            val file = File(documentsDir, "GFG.pdf")
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
                Toast.makeText(
                    context,
                    "PDF file generated in Documents: ${file.absolutePath}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to generate PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }

}

fun checkAndRequestPermissions(activity: Activity): Boolean {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
            false
        } else {
            true
        }
    } else {
        true
    }
}

