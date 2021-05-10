/**
 * Created with JackHou
 * Date: 2021/5/6
 * Time: 18:21
 * Description:
 */

package com.qtimes.jetpackdemokotlin.model

import java.io.Serializable

/*门的信息*/
class DoorInfo(val typeId: Int, val name: String, val type: String) : Serializable

/*模型信息*/
class ModelInfo(
    val typeId: Int,
    val url: String,
    val modelKey: String,
    val modelId: Int,
    val ts: Long,
    val base: Int,
    val version: String,
    val md5: String
) : Serializable

/*工单信息*/

class TicketInfo(
    val ticketId: Int,
    val ticketType: Int,
    val status: Int,
    val userName: String,
    val userPhone: String,
    val supportName: String,
    val supportPhone: String,
    val info: String,
    val deviceName: String
) : Serializable