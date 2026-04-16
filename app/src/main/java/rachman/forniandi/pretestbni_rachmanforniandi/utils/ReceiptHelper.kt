package rachman.forniandi.pretestbni_rachmanforniandi.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.Transaction
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.TransactionStatus
import rachman.forniandi.pretestbni_rachmanforniandi.domain.model.TransactionType
import rachman.forniandi.pretestbni_rachmanforniandi.utils.CurrencyUtils.formatCurrency
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReceiptHelper {

    fun shareReceipt(context: Context, transaction: Transaction, bitmap: Bitmap) {
        val receiptText = buildReceiptText(transaction)
        val imageFile = saveBitmapToCache(context, bitmap, "receipt_${transaction.reffId}.png")

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/png"
            putExtra(Intent.EXTRA_TEXT, receiptText)

            imageFile?.let { file ->
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        context.startActivity(Intent.createChooser(shareIntent, "Bagikan Bukti Transaksi"))
    }

    private fun buildReceiptText(transaction: Transaction): String {
        val statusText = if (transaction.status == TransactionStatus.SUCCESS) "Berhasil" else "Gagal"
        val typeText = when (transaction.type) {
            TransactionType.TRANSFER -> "Transfer"
            TransactionType.TOPUP -> "Topup"
        }

        val nominalFormatted = formatCurrency(transaction.nominal)
        val dateFormatted = DateUtils.formatDateForReceipt(transaction.time)

        return "wondr by BNI\n" +
                "\n" +
                "$typeText Saldo $statusText\n" +
                "\n" +
                "Nominal: Rp$nominalFormatted\n" +
                "Tanggal: $dateFormatted\n" +
                "Ref ID: ${transaction.reffId}\n" +
                "\n" +
                "Detail Transaksi:\n" +
                "Nominal: Rp$nominalFormatted\n" +
                "Biaya Admin: Rp0\n" +
                "Total: Rp$nominalFormatted\n" +
                "\n" +
                "Terima kasih telah menggunakan layanan kami."
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap, filename: String): File? {
        return try {
            val cachePath = File(context.cacheDir, "images")
            if (!cachePath.exists()) {
                cachePath.mkdirs()
            }

            val file = File(cachePath, filename)
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Helper ui Functions
    fun getStatusTitle(type: TransactionType, isSuccess: Boolean): String {
        return when {
            type == TransactionType.TRANSFER && isSuccess -> "Transfer Saldo Berhasil"
            type == TransactionType.TRANSFER && !isSuccess -> "Transfer Saldo Gagal"
            type == TransactionType.TOPUP && isSuccess -> "Topup Saldo Berhasil"
            type == TransactionType.TOPUP && !isSuccess -> "Topup Saldo Gagal"
            else -> "Transaksi"
        }
    }

   fun formatDateTime(date: Date): String {
        val format = SimpleDateFormat("dd MMM yyyy - HH:mm:ss 'WIB'", Locale("id", "ID"))
        return format.format(date)
    }


    /*private fun formatCurrency(amount: Double): String {
        return String.format("%,.0f", amount).replace(',', '.')
    }*/
}