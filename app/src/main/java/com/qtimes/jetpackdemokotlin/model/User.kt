/**
 * Created with JackHou
 * Date: 2021/4/1
 * Time: 19:14
 * Description:用户实体类
 */

package com.qtimes.jetpackdemokotlin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user", indices = [Index(value = ["user_name"], unique = true)])
data class User(
    @ColumnInfo(name = "user_name") val name: String,
    @ColumnInfo(name = "user_password") val password: String,
    @ColumnInfo(name = "user_phone") val phone: String? = "",
    @ColumnInfo(name = "user_state") val state: String = UserState.OFFLINE.name
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L
}


enum class UserState {
    ONLINE,
    OFFLINE
}