/**
 * Created with JackHou
 * Date: 2021/3/23
 * Time: 16:03
 * Description:
 */

package com.qtimes.jetpackdemokotlin.net.base

import kotlinx.coroutines.Job


open class BaseActionEvent

class ShowLoadingEvent(val job: Job?) : BaseActionEvent()

object DismissLoadingEvent : BaseActionEvent()

object FinishViewEvent : BaseActionEvent()

class ShowToastEvent(val message: String) : BaseActionEvent()
