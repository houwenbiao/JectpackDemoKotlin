/**
 * Created with JackHou
 * Date: 2021/4/1
 * Time: 19:28
 * Description:用户Dao
 */

package com.qtimes.jetpackdemokotlin.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.qtimes.jetpackdemokotlin.model.User


@Dao
interface UserDao {

    /**
     * 注册用户
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerUser(user: User): Long

    /**
     * 更新用户信息
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(user: User): Int

    /**
     * 注销用户
     */
    @Delete
    suspend fun unregisterUser(user: User)

    /**
     * 通过Id查找用户
     */
    @Query("select * from user where id =:id")
    fun findUserById(id: Int): LiveData<User>

    /**
     * 通过手机号查找用户
     */
    @Query("select * from user where user_phone =:param or user_name =:param")
    fun findUserByAccountOrPhone(param: String): LiveData<User?>
}