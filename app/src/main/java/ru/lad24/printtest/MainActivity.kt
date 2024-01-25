package ru.lad24.printtest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0
                )
            }
        }
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0
                )
            }
        }

        button = findViewById<View>(R.id.btn_create_pdf) as Button
        button?.setOnClickListener { createPDFFile(Comman.getAppPath(this@MainActivity) + "test_pdf.pdf") }
    }

    private fun createPDFFile(path: String) {
        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        if (File(path).exists()) File(path).delete()
        try {
            val document = Document()
            //Save
            PdfWriter.getInstance(document, FileOutputStream(path))
            //open to write
            document.open()
            //Settings
            document.pageSize = PageSize.A4

            //Custom font
            val fontName = BaseFont.createFont("res/font/roboto.ttf", "UTF-8", BaseFont.EMBEDDED)

            //create title of document
            val titleFont = Font(fontName, 20.0f, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "Print test evotor", Element.ALIGN_CENTER, titleFont)

            document.close()
            Toast.makeText(this, "Success save pdf", Toast.LENGTH_SHORT).show()
            printPDF()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: DocumentException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(DocumentException::class)
    private fun addNewItem(document: Document, text: String, align: Int, font: Font) {
        val chunk = Chunk(text, font)
        val paragraph = Paragraph(chunk)
        paragraph.alignment = align
        document.add(paragraph)
    }

    private fun printPDF() {
        val printManager = getSystemService(PRINT_SERVICE) as PrintManager
        try {
            val printDocumentAdapter: PrintDocumentAdapter = PdfDocumentAdapter(
                this@MainActivity,
                Comman.getAppPath(this@MainActivity) + "test_pdf.pdf"
                //Environment.getExternalStorageDirectory().toString() + "/Download/PrintTest.pdf"
            )
            printManager.print("Document", printDocumentAdapter, PrintAttributes.Builder().build())
        } catch (ex: Exception) {
            Log.e("PrintEvotor", ex.message.toString())
            Toast.makeText(this@MainActivity, "Can't read pdf file", Toast.LENGTH_SHORT).show()
        }
    }
}