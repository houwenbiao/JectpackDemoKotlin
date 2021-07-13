/**
 * Created with JackHou
 * Date: 2021/7/13
 * Time: 14:05
 * Description:扫码绑定设备
 */

package com.qtimes.jetpackdemokotlin.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.qtimes.jetpackdemokotlin.model.DeviceMap

@Dao
interface DeviceMapDao {

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(deviceMap: DeviceMap): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(deviceMap: DeviceMap): Int

    @Query("select * from device_map where device_id =:did or location_id =:lid")
    suspend fun findByDidOrLid(did: String, lid: String): LiveData<DeviceMap>

    @Query("select * from device_map")
    suspend fun findAll(): List<DeviceMap>*/
}