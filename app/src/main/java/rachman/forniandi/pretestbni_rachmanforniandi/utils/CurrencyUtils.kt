package rachman.forniandi.pretestbni_rachmanforniandi.utils

import java.text.NumberFormat
import java.util.*

object CurrencyUtils {
    private val formatter: NumberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))

    fun formatRupiah(amount: Double): String {
        return "Rp${formatter.format(amount)}"
    }



    fun formatNumber(amount: Double): String {
        return formatter.format(amount)
    }



    fun formatCurrency(amount: Double): String {
        return formatNumber(amount)
    }
}