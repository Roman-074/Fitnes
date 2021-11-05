package hedgehog.tech.fitnes.app.utils

import hedgehog.tech.fitnes.R


//Дни недели для запоминалок
enum class DaysOfWeek(val longName:Int,val shortName:Int) {
    MONDAY(R.string.monday_long,R.string.monday_short),
    TUESDAY(R.string.tuesday_long,R.string.tuesday_short),
    WEDNESDAY(R.string.wednesday_long,R.string.wednesday_short),
    THURSDAY(R.string.thursday_long,R.string.thursday_short),
    FRIDAY(R.string.friday_long,R.string.friday_short),
    SATURDAY(R.string.saturday_long,R.string.saturday_short),
    SUNDAY(R.string.sunday_long,R.string.sunday_short),
}