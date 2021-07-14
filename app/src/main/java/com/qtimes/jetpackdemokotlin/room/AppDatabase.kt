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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.qtimes.jetpackdemokotlin.common.MainApplication
import com.qtimes.jetpackdemokotlin.model.DeviceMap
import com.qtimes.jetpackdemokotlin.model.User
import com.qtimes.jetpackdemokotlin.model.UserState
import com.qtimes.jetpackdemokotlin.room.dao.DeviceMapDao
import com.qtimes.jetpackdemokotlin.room.dao.UserDao
import com.qtimes.jetpackdemokotlin.utils.LogUtil

@Database(entities = [User::class, DeviceMap::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    abstract fun getDeviceMapDao(): DeviceMapDao

    companion object {

        /**
         * User表添加state字段
         */
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table user add column user_state string default ${UserState.OFFLINE.name}")
            }
        }

        //双重校验单例模式
        @Volatile
        private var appDatabase: AppDatabase? = null

        fun getInstance(): AppDatabase {
            LogUtil.d("AppDatabase getInstance")
            return appDatabase ?: synchronized(this) {
                appDatabase ?: buildDatabase(MainApplication.context).also {
                    appDatabase = it
                }
            }
        }

        private fun buildDatabase(ctx: Context): AppDatabase {
            return Room.databaseBuilder(
                ctx,
                AppDatabase::class.java,
                ctx.packageName
            )
                .allowMainThreadQueries()
//                .addMigrations(MIGRATION_1_2)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        LogUtil.i("Database created")
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        LogUtil.i("Database opened, version = ${db.version}")
                    }
                }).build()
        }
    }
}