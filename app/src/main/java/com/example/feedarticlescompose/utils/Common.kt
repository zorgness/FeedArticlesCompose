import java.text.SimpleDateFormat
import java.util.*

fun dateForrmater(dateStr: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    return parser.parse(dateStr)?.run { formatter.format(this) } ?: "01/01/1980"
}