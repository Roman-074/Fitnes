package hedgehog.tech.fitnes.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hedgehog.tech.fitnes.app.utils.DaysOfWeek

//Класс для конвертирования коллекций в стринги, для хранения в БД
object DatabaseConverter {

    @TypeConverter
    fun stringToMapIntBoolean(value: String): MutableMap<Int, Boolean> {
        return Gson().fromJson(value,  object : TypeToken<Map<Int, Boolean>>() {}.type)
    }

    @TypeConverter
    fun mapIntBooleanToString(value: MutableMap<Int, Boolean>?): String {
        return if(value == null) "" else Gson().toJson(value)
    }

    @TypeConverter
    fun stringToMapIntInt(value: String): MutableMap<Int, Int> {
        return Gson().fromJson(value,  object : TypeToken<Map<Int, Int>>() {}.type)
    }

    @TypeConverter
    fun mapIntIntToString(value: MutableMap<Int, Int>?): String {
        return if(value == null) "" else Gson().toJson(value)
    }

    @TypeConverter
    fun stringToListDaysOfWeek(value: String): List<DaysOfWeek>{
        return Gson().fromJson(value,object : TypeToken<List<DaysOfWeek>>() {}.type)
    }

    @TypeConverter
    fun listDaysOfWeekToString(value: List<DaysOfWeek>?): String {
        return if(value == null) "" else Gson().toJson(value)
    }

    @TypeConverter
    fun stringToList(value: String): MutableList<Int> {
        return Gson().fromJson(value,  object : TypeToken<MutableList<Int>>() {}.type)
    }

    @TypeConverter
    fun listToString(value: MutableList<Int>?): String {
        return if(value == null) "" else Gson().toJson(value)
    }



    @TypeConverter
    fun stringToSet(value: String): MutableSet<Pair<String,Int>> {
        return Gson().fromJson(value,  object : TypeToken<Set<Pair<String,Int>>>() {}.type)
    }

    @TypeConverter
    fun setToString(value: MutableSet<Pair<String,Int>>?): String {
        return if(value == null) "" else Gson().toJson(value)
    }
}