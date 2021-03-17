/**
 * Created with JackHou
 * Date: 2021/3/15
 * Time: 18:12
 * Description:ViewModel层与UI层交互的事件
 */

package com.qtimes.jectpackdemokotlin.viewmodel.base


open class ActionEvent(open val action: Int)

class ViewModelEvent(override val action: Int) : ActionEvent(action) {
    companion object {
        const val SHOW_LOADING_DIALOG = 1
        const val DISMISS_LOADING_DIALOG = 2
        const val SHOW_TOAST = 3
        const val FINISH = 4
        const val POP = 5
    }

    var message: String = ""
}