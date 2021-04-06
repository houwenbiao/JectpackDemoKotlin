/**
 * Created with JackHou
 * Date: 2021/4/1
 * Time: 19:14
 * Description:
 */

package com.qtimes.jetpackdemokotlin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user", indices = [Index(value = ["user_name"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "user_name") val name: String,
    @ColumnInfo(name = "user_password") val password: String,
    @ColumnInfo(name = "user_phone") val phone: String? = ""
)