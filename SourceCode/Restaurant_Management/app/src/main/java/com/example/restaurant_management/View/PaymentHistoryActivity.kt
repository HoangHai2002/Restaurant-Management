package com.example.restaurant_management.View

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant_management.Adapter.BillAdapter
import com.example.restaurant_management.Adapter.IClickBillItem
import com.example.restaurant_management.Controller.BillController
import com.example.restaurant_management.Controller.IBillView
import com.example.restaurant_management.Models.Bill
import com.example.restaurant_management.databinding.ActivityPaymentHistoryBinding
import com.github.mikephil.charting.data.BarEntry
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar

class PaymentHistoryActivity : AppCompatActivity(), IBillView {
    lateinit var bind: ActivityPaymentHistoryBinding
    lateinit var adapter: BillAdapter
    lateinit var listBill: MutableList<Bill>

    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityPaymentHistoryBinding.inflate(layoutInflater)
        setContentView(bind.root)

        setSupportActionBar(bind.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bind.toolbar.setTitle("Lịch sử thanh toán")

        bind.editText.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    bind.editText.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
        bind.btnFind.setOnClickListener {
            if (bind.editText.text.toString().trim() != "") {
                try {
                    val date = LocalDate.parse(
                        bind.editText.text.toString().trim(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                    BillController(this).getByDate(date)
                } catch (e: DateTimeParseException) {
                    Toast.makeText(this, "Thông tin không đúng định dạng", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                BillController(this).getAll()
            }
        }

        var listener = object : IClickBillItem {
            override fun onClick(data: Bill) {
                val intent = Intent(this@PaymentHistoryActivity, BillDetailActivity::class.java)
                intent.putExtra("billId", data.id)
                startActivity(intent)
            }
        }
        listBill = mutableListOf()
        adapter = BillAdapter(listBill, listener)
        bind.rcBill.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        bind.rcBill.adapter = adapter

        BillController(this).getAll()

        bind.imvMenu.setOnClickListener {
            val popupmenu = PopupMenu(this, bind.imvMenu)
            popupmenu.menu.add("Tải danh sách hóa đơn")
            popupmenu.menu.add("Chia sẻ")

            popupmenu.setOnMenuItemClickListener {  menuItem ->
                when(menuItem.title.toString()){
                    "Tải danh sách hóa đơn" ->{
                        val excelData = createExcelData(listBill)
                        saveFileToDownloads("HÓA ĐƠN THANH TOÁN.xlsx", excelData.toByteArray())
                        Toast.makeText(this, "Đã lưu file HÓA ĐƠN THANH TOÁN.xlsx vào thư mục Download", Toast.LENGTH_LONG).show()
                        true
                    }

                    "Chia sẻ" ->{
                        val excelData = createExcelData(listBill)
                        val file = File(cacheDir, "HÓA ĐƠN THANH TOÁN.xlsx")
                        file.writeBytes(excelData.toByteArray())
                        val fileUri = FileProvider.getUriForFile(
                            this@PaymentHistoryActivity,
                            "${packageName}.fileprovider",
                           file
                        )
                        shareFile(this@PaymentHistoryActivity, fileUri, "HÓA ĐƠN THANH TOÁN.xlsx")
                        true
                    }


                    else -> false
                }
            }
            popupmenu.show()
        }


//        bind.btnDownload.setOnClickListener {
//            val excelData = createExcelData(listBill)
//            saveFileToDownloads("HÓA ĐƠN THANH TOÁN.xlsx", excelData.toByteArray())
//            Toast.makeText(this, "Đã lưu file HÓA ĐƠN THANH TOÁN.xlsx vào thư mục Download", Toast.LENGTH_LONG).show()
//        }
    }
    fun shareFile(context: Context, fileUri: Uri, fileName: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ file: $fileName")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ file qua"))
    }

    fun saveFileToDownloads(fileName: String, fileData: ByteArray) {
        val outputStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = this.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { this.contentResolver.openOutputStream(it) }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = java.io.File(downloadsDir, fileName)
            FileOutputStream(file)
        }

        outputStream?.use {
            it.write(fileData)
        }
    }
    fun createExcelData(listBill: MutableList<Bill>): ByteArrayOutputStream {
        val workbook = XSSFWorkbook()

        for (it in listBill) {
            val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("HH'h'mm dd-MM-yyyy")
            val datetime = LocalDateTime.parse(it.createdAt?.trim(), inputFormatter).format(outputFormatter)
            val sheet = workbook.createSheet(datetime)

            val centerStyle = workbook.createCellStyle().apply {
                alignment = HorizontalAlignment.CENTER
                verticalAlignment = VerticalAlignment.CENTER
            }

            val headerFont = workbook.createFont().apply {
                bold = true
                fontHeightInPoints = 12
            }
            val headerCellStyle = workbook.createCellStyle().apply {
                setFont(headerFont)
                alignment = HorizontalAlignment.CENTER
            }

            fun createMergedRow(rowIndex: Int, label: String, value: String) {
                val row = sheet.createRow(rowIndex)
                row.createCell(0).setCellValue(label)
                row.createCell(2).apply {
                    setCellValue(value)
                    cellStyle = centerStyle
                }
                sheet.addMergedRegion(CellRangeAddress(rowIndex, rowIndex, 0, 1))
            }

            val row0 = sheet.createRow(0)
            row0.createCell(0).apply {
                setCellValue("HÓA ĐƠN THANH TOÁN")
                cellStyle = centerStyle
            }
            sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 2))

            createMergedRow(1, "Ngày thanh toán:", LocalDateTime.parse(it.createdAt, inputFormatter).toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
            createMergedRow(2, "Thời gian thanh toán:", LocalDateTime.parse(it.createdAt, inputFormatter).toLocalTime().toString())
            createMergedRow(3, "Nhân viên thanh toán:", it.createdBy ?: "")

            val headerRow = sheet.createRow(5)
            listOf("STT", "Tên món ăn", "Số lượng", "Giá").forEachIndexed { index, title ->
                headerRow.createCell(index).apply {
                    setCellValue(title)
                    cellStyle = headerCellStyle
                }
            }

            var indexRow = 5
            var count = 1
            for (item in it.order?.orderItem.orEmpty()) {
                indexRow++
                val row = sheet.createRow(indexRow)
                row.createCell(0).apply {
                    setCellValue(count++.toString())
                    cellStyle = centerStyle
                }
                row.createCell(1).apply {
                    setCellValue(item.dishName)
                    cellStyle = centerStyle
                }
                row.createCell(2).apply {
                    setCellValue(item.quantity.toString())
                    cellStyle = centerStyle
                }
                row.createCell(3).apply {
                    setCellValue(DecimalFormat("###,###.##").format(item.price))
                    cellStyle = centerStyle
                }
            }

            indexRow++
            createMergedRow(indexRow++, "Tổng tiền hàng:", DecimalFormat("###,###.##").format(it.order?.total ?: 0.0))
            createMergedRow(indexRow++, "Giảm giá:", "${it.discount}${it.discountType}")
            createMergedRow(indexRow++, "Thổng thu:", DecimalFormat("###,###.##").format(it.total))
            createMergedRow(indexRow, "Phương thức thanh toán:", it.paymentMethod ?: "")

            sheet.setColumnWidth(0, 2000)
            sheet.setColumnWidth(1, 5000)
            sheet.setColumnWidth(2, 3000)
            sheet.setColumnWidth(3, 4000)
        }

        val byteStream = ByteArrayOutputStream()
        workbook.write(byteStream)
        workbook.close()
        return byteStream
    }

    fun saveExcelToDownloads(listBill: MutableList<Bill>) {
        val workbook = XSSFWorkbook()

        for (it in listBill) {
            val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("HH'h'mm dd-MM-yyyy")

            val datetime =
                LocalDateTime.parse(it.createdAt?.trim(), inputFormatter).format(outputFormatter)

            val sheet = workbook.createSheet(datetime)
            val row0 = sheet.createRow(0)
            val A1 = row0.createCell(0)
            A1.setCellValue("HÓA ĐƠN THANH TOÁN")
            sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 2))

            val centerStyle = workbook.createCellStyle().apply {
                alignment = HorizontalAlignment.CENTER
                verticalAlignment = VerticalAlignment.CENTER
            }

            A1.cellStyle = centerStyle


            val row1 = sheet.createRow(1)
            row1.createCell(0).setCellValue("Ngày thanh toán:")
            row1.createCell(2).setCellValue(
                LocalDateTime.parse(it.createdAt, inputFormatter).toLocalDate()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()
            )
            sheet.addMergedRegion(CellRangeAddress(1, 1, 0, 1))

            val row2 = sheet.createRow(2)
            row2.createCell(0).setCellValue("Thời gian thanh toán:")
            row2.createCell(2)
                .setCellValue(
                    LocalDateTime.parse(it.createdAt, inputFormatter).toLocalTime().toString()
                )
            sheet.addMergedRegion(CellRangeAddress(2, 2, 0, 1))

            val row3 = sheet.createRow(3)
            row3.createCell(0).setCellValue("Nhân viên thanh toán:")
            row3.createCell(2).setCellValue(it.createdBy)
            sheet.addMergedRegion(CellRangeAddress(3, 3, 0, 1))

            val headerFont = workbook.createFont().apply {
                bold = true
                fontHeightInPoints = 12
            }
            val headerCellStyle = workbook.createCellStyle().apply {
                setFont(headerFont)
                alignment = HorizontalAlignment.CENTER
            }

            val headerRow = sheet.createRow(5)
            val headers = listOf(
                "STT",
                "Tên món ăn",
                "Số lượng",
                "Giá",
            )
            headers.forEachIndexed { index, title ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(title)
                cell.cellStyle = headerCellStyle
            }

            var indexRow = 5
            var count = 0
            for (item in it.order?.orderItem!!) {
                indexRow++
                val row = sheet.createRow(indexRow)

                row.createCell(0).apply {
                    setCellValue(count++.toString())
                    cellStyle = centerStyle
                }
                row.createCell(1).apply {
                    setCellValue(item.dishName)
                    cellStyle = centerStyle
                }
                row.createCell(2).apply {
                    setCellValue(item.quantity.toString())
                    cellStyle = centerStyle
                }
                row.createCell(3).apply {
                    setCellValue(DecimalFormat("###,###.##").format(item.price))
                    cellStyle = centerStyle
                }

            }

            val row4 = sheet.createRow(++indexRow)
            row4.createCell(0).setCellValue("Tổng tiền hàng:")
            row4.createCell(2).apply {
                setCellValue(DecimalFormat("###,###.##").format(it.order!!.total))
                cellStyle = centerStyle
            }
            sheet.addMergedRegion(CellRangeAddress(indexRow, indexRow, 0, 1))

            val row5 = sheet.createRow(++indexRow)
            row5.createCell(0).setCellValue("Giảm giá:")
            row5.createCell(2).apply {
                setCellValue(it.discount.toString() + it.discountType)
                cellStyle = centerStyle
            }
            sheet.addMergedRegion(CellRangeAddress(indexRow, indexRow, 0, 1))

            val row6 = sheet.createRow(++indexRow)
            row6.createCell(0).setCellValue("Thổng thu:")
            row6.createCell(2).apply {
                setCellValue(DecimalFormat("###,###.##").format(it.total))
                cellStyle = centerStyle
            }
            sheet.addMergedRegion(CellRangeAddress(indexRow, indexRow, 0, 1))

            val row7 = sheet.createRow(++indexRow)
            row7.createCell(0).setCellValue(" Phương thức thanh toán:")
            row7.createCell(2).apply {
                setCellValue(it.paymentMethod)
                cellStyle = centerStyle
            }
            sheet.addMergedRegion(CellRangeAddress(indexRow, indexRow, 0, 1))

            sheet.setColumnWidth(0, 2000) // STT
            sheet.setColumnWidth(1, 5000) // Tên món ăn
            sheet.setColumnWidth(2, 3000) // Số lượng
            sheet.setColumnWidth(3, 4000) // Giá
        }


        val filename = "HÓA ĐƠN THANH TOÁN.xlsx"

        val outputStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 trở lên → dùng MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, filename)
                put(
                    MediaStore.Downloads.MIME_TYPE,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = this.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let { this.contentResolver.openOutputStream(it) }
        } else {
            // Android 9 trở xuống → lưu trực tiếp vào thư mục Download
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = java.io.File(downloadsDir, filename)
            FileOutputStream(file)
        }

        outputStream?.use {
            workbook.write(it)
            workbook.close()
            Toast.makeText(this, "Đã lưu file $filename vào thư mục Download", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> false
        }
    }

    override fun onBillDataLoaded(data: Bill?) {
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBillListLoaded(dataList: List<Bill>) {
        dataList.let {
            listBill.clear()
            listBill.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onBarEntryListLoaded(dataList: List<BarEntry>) {
    }

    override fun onBillActionSuccess(message: String) {
    }

    override fun onBillActionError(error: String) {
    }
}