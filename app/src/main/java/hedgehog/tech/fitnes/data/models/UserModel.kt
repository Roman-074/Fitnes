package hedgehog.tech.fitnes.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

//сущность хранящая юзера
@Entity(tableName = "user")
data class UserModel(
    @PrimaryKey
    val id: Int = 1,
    var gender: String?,
    var name: String?,
    var age: Int?,
    var tall: Float?,
    var currWeight: Float?,
    var goalWeight: Float?,
    var pathToPhoto: String?, //путь к нестандартному фото
    var isStandartPhoto: Boolean?, // флаг показывающий, используется ли фото с устройства или предустановленное в приложение
    var photoIndex: Int?, //индекс фото, если используется стандартное
    var activityLevel: String?, //уровень навыков
)
