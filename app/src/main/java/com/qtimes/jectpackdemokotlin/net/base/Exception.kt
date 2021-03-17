/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 14:13
 * Description:
 */

package com.qtimes.jectpackdemokotlin.net.base

import com.qtimes.jectpackdemokotlin.net.HttpConfig
import java.lang.RuntimeException


sealed class BaseException(errorMsg: String, code: Int = HttpConfig.HttpCode.CODE_UNKNOWN) :
    RuntimeException(errorMsg)

class ServerResultException(errorMsg: String, code: Int = HttpConfig.HttpCode.CODE_UNKNOWN) :
    BaseException(errorMsg, code)