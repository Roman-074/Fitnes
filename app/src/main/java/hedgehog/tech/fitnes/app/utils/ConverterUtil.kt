package hedgehog.tech.fitnes.app.utils

object ConverterUtil {
    //Конвертирует футы и дюймы в сантиметры
    fun convertFtAndInToCm(ft: Float, inch:Float) = (ft / 0.032808 + inch / 0.39370).toFloat()
    fun convertLbInKg(lb:Float) = (lb / 2.2046).toFloat()
}