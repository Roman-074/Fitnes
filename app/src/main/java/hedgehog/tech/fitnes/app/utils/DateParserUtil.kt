package hedgehog.tech.fitnes.app.utils

import android.content.Context
import hedgehog.tech.fitnes.R
import timber.log.Timber
import java.lang.StringBuilder
import java.util.*

object DateParserUtil {
    //Парсим время, для показа его в списке упражнений
    fun parseDate(calendar: Calendar, context: Context): String {
        //выставляем зону UTC, так как нам нужен интервал времени, без смещения по часовому поясу
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)
        val hours = calendar.get(Calendar.HOUR)
        val parsedDate = StringBuilder()
        Timber.d(calendar.toString())
        if (hours != 0) {
            parsedDate.append(context.getString(R.string.hours_num, hours.toString()) + " ")
        }
        if (minutes != 0) {
            parsedDate.append(context.getString(R.string.min_num, minutes.toString()) + " ")
        }
        if (seconds != 0) {
            parsedDate.append(context.getString(R.string.sec_num, seconds.toString()) + " ")
        }
        return parsedDate.toString()
    }
}