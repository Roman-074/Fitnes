package hedgehog.tech.fitnes.app.utils

//класс для подсчета очков пользователя на основе уровня его навыков
object UserScoreUtil {
     fun setUserScore(exercisesCount: Int, activityLevel: String?): String {
        activityLevel?.let {
            when (it) {
                "weak" -> {
                    return exercisesCount.toString()
                }
                "advanced" -> {
                    return (exercisesCount * 2).toString()
                }
                "master" -> {
                    return (exercisesCount * 3).toString()
                }
                else -> return "0"
            }
        }
        return "0"
    }
}