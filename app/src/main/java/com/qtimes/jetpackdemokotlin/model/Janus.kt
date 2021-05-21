/**
 * Created with JackHou
 * Date: 2021/5/19
 * Time: 11:29
 * Description:
 */

package com.qtimes.jetpackdemokotlin.model

import org.json.JSONObject
import java.math.BigInteger

/**
 * Author: JackHou
 * Date: 2021/5/19.
 * Janus相关的实体类
 * Session表示的是一个客户端与janus服务器之间建立的一个信令通道
 */


class Publisher(val id: BigInteger, val display: String) {
    private var handleId: BigInteger = BigInteger("0")
}


class JanusRoom(val id: Int) {
    private var publishers = HashSet<Publisher>()

    fun getPublishers(): Set<Publisher> {
        return publishers
    }

    fun addPublisher(publisher: Publisher) {
        val it: MutableIterator<Publisher> = publishers.iterator()
        var found: Boolean = false
        while (it.hasNext()) {
            val next = it.next()
            if (next.id == publisher.id) {
                found = true
                break
            }
        }
        if (!found) {
            publishers.add(publisher)
        }
    }

    fun findPublisherById(id: BigInteger): Publisher {
        return publishers.stream().filter {
            it.id == id
        }.findFirst().get()
    }

    fun removePublisherById(id: BigInteger) {
        publishers.removeIf {
            it.id == id
        }
    }
}

/**
 * transaction，它表示一个事务或称之为上下文，
 * 当一件事儿由多个步骤多阶段组合完成时，我们一般都使用这种设计方案
 */
open class Transaction(val tid: String) {

    var feedId: BigInteger = BigInteger("0")

    constructor(tid: String, feedId: BigInteger) : this(tid) {
        this.feedId = feedId
    }

    fun onError() {
    }

    @Throws(Exception::class)
    open fun onSuccess(data: JSONObject) {
    }

    @Throws(Exception::class)
    open fun onSuccess(data: JSONObject, feedId: BigInteger) {
    }
}

/**
 * 在pluginHandle对象中保存着可以访问janus服务端插件的信息
 */
class PluginHandle(val handleId: BigInteger)