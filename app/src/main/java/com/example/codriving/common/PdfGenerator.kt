package com.example.codriving.common

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.content.CursorLoader
import com.example.codriving.R
import com.example.codriving.data.model.Car
import com.example.codriving.data.model.RentCars
import com.example.codriving.data.model.User
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// on below line we are creating a generate PDF
// method which is use to generate our PDF file.
fun generatePDF(
    context: Context,
    owner: User,
    client: User,
    car: Car,
    listOfRents: List<RentCars>
): String? {
    val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
    val timestampFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val timestamp = timestampFormat.format(Date())

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
    val ownerName = owner.fullName!!.replaceFirstChar { it.uppercase() }
    val clientName = client.fullName!!.replaceFirstChar { it.uppercase() }

    val rentTextBuilder = StringBuilder()
    listOfRents.forEachIndexed { index, rent ->
        val startDateFormatted = dateFormat.format(rent.startDate.toDate())
        val endDateFormatted = dateFormat.format(rent.endDate.toDate())

        rentTextBuilder.append("2.${index + 1} El período de alquiler comienza el $startDateFormatted y termina el $endDateFormatted.\n")


    }

    val rentText: String = rentTextBuilder.toString()
        .trimEnd() // Para asegurarte de que no haya un salto de línea extra al final


    val text = """
        Este Acuerdo de Alquiler de Coche se celebra entre:
        
        $ownerName
        Dirección: [Dirección del Arrendador]
        
        Y
        
        $clientName
        Dirección: [Dirección del Arrendatario]
        
        Detalles del coche:
        - Marca: ${car.brand.replaceFirstChar { it.uppercase() }}
        - Modelo: ${car.model.replaceFirstChar { it.uppercase() }}
        - Año: ${car.year}
        - Matrícula: ${car.plate}
        
        Términos y condiciones:
        1. El arrendatario se compromete a devolver el coche en las mismas condiciones en que lo recibió.
${rentText.prependIndent("        ")}        
        3. El arrendatario se responsabiliza de cualquier daño o pérdida del coche durante el período de alquiler.        
        
        Firma del Arrendador: _________________________       Firma del Arrendatario: _________________________

    """.trimIndent()
//4. El arrendatario debe pagar la cantidad de [Monto del Alquiler] antes de [Fecha de Pago].
    val textLines: List<String> = text.split("\n")
    var yPosition = 250F

    for (line in textLines) {
        canvas.drawText(line, 56F, yPosition, textPaint)
        yPosition += textPaint.descent() - textPaint.ascent()
    }
    pdfDocument.finishPage(myPage)
    var filePath: String? = null

    try {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "rentcarFile_$timestamp.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                contentValues
            )

            if (uri == null) {
                Log.e("PDFGeneration", "Failed to create MediaStore entry.")
            } else {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                filePath = getRealPathFromURI(uri, context)
                Log.d("PDFGeneration", "PDF saved successfully: $filePath")
            }

        } else {
            val documentsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (!documentsDir.exists()) {
                documentsDir.mkdirs()
            }
            val file = File(documentsDir, "Arrendantariado.pdf")
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            filePath = file.absolutePath

        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        pdfDocument.close()
        return filePath
    }

}

fun generatefilePathPDF(
    context: Context,
    owner: User,
    client: User,
    car: Car,
    listOfRents: List<RentCars>
): String? {

    if (!checkAndRequestPermissions(context as Activity)) {
        throw IllegalAccessError("No tienes permisos")
    }
    return generatePDF(context, owner, client, car, listOfRents)
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

private fun getRealPathFromURI(contentUri: Uri, context: Context): String? {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val loader = CursorLoader(context, contentUri, proj, null, null, null)
    val cursor: Cursor? = loader.loadInBackground()
    val columnIndex: Int = cursor
        ?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        ?: return null
    cursor.moveToFirst()
    val result: String? = cursor.getString(columnIndex)
    cursor.close()
    return result
}