package com.qtimes.jetpackdemokotlin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Author: JackHou
 * Date: 2021/7/13.
 * 仓库设备扫码时候的绑定关系
 */

@Entity(
    tableName = "device_map",
    indices = [Index(
        value = ["device_id", "location_id"],
        unique = true
    ), Index(
        value = ["device_id"],
        unique = true
    ), Index(
        value = ["location_id"],
        unique = true
    )]
)
data class DeviceMap(
    @ColumnInfo(name = "device_id") var deviceId: String,
    @ColumnInfo(name = "location_id") var locationId: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L
}
