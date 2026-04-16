package rachman.forniandi.pretestbni_rachmanforniandi.utils

object Constants {
    // Base URL untuk API
    const val BASE_URL = " http://192.168.1.19:3001/" // Ganti dengan URL API Anda

    // Local Keys
    const val PREF_NAME = "transaction_app_pref"
    const val KEY_USER_ID = "user_id"
    const val KEY_IS_LOGGED_IN = "is_logged_in"

    // Validation Constants
    const val MIN_TRANSACTION_AMOUNT = 10000.0
    const val MAX_TRANSACTION_AMOUNT = 1000000000.0

    // Transaction Types
    const val TYPE_TRANSFER = "TRANSFER"
    const val TYPE_TOPUP = "TOPUP"

    // API Response Status
    const val STATUS_SUCCESS = "SUCCESS"
    const val STATUS_FAILED = "FAILED"

    // Database
    const val DATABASE_NAME = "transaction_database"
    const val DATABASE_VERSION = 1

    // Date Format
    const val API_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ"
    const val DISPLAY_DATE_FORMAT = "dd MMM yyyy, HH:mm"
    const val RECEIPT_DATE_FORMAT = "dd MMM yyyy - HH:mm:ss 'WIB'"

    // ReffId
    const val REFF_ID_LENGTH = 13


}