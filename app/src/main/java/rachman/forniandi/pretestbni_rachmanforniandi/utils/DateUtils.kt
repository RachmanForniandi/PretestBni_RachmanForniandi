package rachman.forniandi.pretestbni_rachmanforniandi.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    private val API_DATE_FORMAT = SimpleDateFormat(Constants.API_DATE_FORMAT, Locale.getDefault())
    private val DISPLAY_DATE_FORMAT = SimpleDateFormat(Constants.DISPLAY_DATE_FORMAT, Locale("id", "ID"))
    private val RECEIPT_DATE_FORMAT = SimpleDateFormat(Constants.RECEIPT_DATE_FORMAT, Locale("id", "ID"))

    fun formatDateForApi(date: Date): String {
        return API_DATE_FORMAT.format(date)
    }

    fun formatDateForDisplay(date: Date): String {
        return DISPLAY_DATE_FORMAT.format(date)
    }

    fun formatDateForReceipt(date: Date): String {
        return RECEIPT_DATE_FORMAT.format(date)
    }

    fun generateReffId(): String {
        return (1..Constants.REFF_ID_LENGTH)
            .map { (0..9).random() }
            .joinToString("")
    }
}