/**
 * Created with JackHou
 * Date: 2021/4/1
 * Time: 19:45
 * Description:
 */

package com.qtimes.jetpackdemokotlin.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.qtimes.jetpackdemokotlin.common.MainApplication
import com.qtimes.jetpackdemokotlin.model.User
import com.qtimes.jetpackdemokotlin.room.dao.UserDao
import com.qtimes.jetpackdemokotlin.utils.LogUtil

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    companion object {

        //单例模式
        val instance: AppDatabase by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            buildDatabase()
        }

        private fun buildDatabase(): AppDatabase {
            return Room.databaseBuilder(
                MainApplication.context,
                AppDatabase::class.java,
                "jetpack_demo_kotlin"
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        LogUtil.d("Database created")
                    }
                }).build()
        }
    }
}