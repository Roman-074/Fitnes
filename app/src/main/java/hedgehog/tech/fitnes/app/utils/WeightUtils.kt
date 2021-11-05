package hedgehog.tech.fitnes.app.utils

import java.math.RoundingMode
import java.text.DecimalFormat

//Класс для вычисления индекса массы тела пользователя
object WeightUtils {
    fun getBmi(currWeight: Float, tall: Float): Double {
        val tallInMeters = tall / 100
        val number = (currWeight / (tallInMeters * tallInMeters)).toDouble()
        return String.format("%.2f",number).replace(",",".").toDouble()
    }
}