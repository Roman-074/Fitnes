package hedgehog.tech.fitnes.data.dao

import androidx.room.*
import hedgehog.tech.fitnes.data.models.UserModel


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: UserModel)

    @Query("SELECT * FROM user where id=1")
    suspend fun getUser() : UserModel
}