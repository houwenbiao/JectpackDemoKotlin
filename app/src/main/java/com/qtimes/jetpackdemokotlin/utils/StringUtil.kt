/**
 * Created with JackHou
 * Date: 2021/3/16
 * Time: 18:03
 * Description:
 */

package com.qtimes.jetpackdemokotlin.utils

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern


object StringUtil {
    private var DEFAULT_INT = -100
    fun isPhoneNumberValid(number: String): Boolean {
        return !number.startsWith("1") || !isNumeric(number) || number.length != 11
    }

    fun isNumeric(str: String?): Boolean {
        val pattern = Pattern.compile("[0-9]*")
        return pattern.matcher(str).matches()
    }

    /*
     * 判断应用程序是否安装
     */
    fun isPkgInstalled(pkgName: String?, context: Context): Boolean {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager
                .getPackageInfo(pkgName!!, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            packageInfo = null
            e.printStackTrace()
        }
        return packageInfo != null
    }

    fun getText(text: String?): String {
        return if (TextUtils.isEmpty(text)) "" else text!!
    }

    /*
     * 字符拆分成数组
     */
    fun getStrSplitByCondition(str: String, split: String, condition: String?): String {
        val cookieArr = str.split(split.toRegex()).toTypedArray()
        val result = ""
        for (i in cookieArr.indices) {
            Log.i("ss", "___________________________cookieArr[" + i + "]:" + cookieArr[i])
            if (cookieArr[i].contains(condition!!)) {
                Log.i("ss", "________________________________cookieArr[" + i + "]:" + cookieArr[i])
                return cookieArr[i]
            }
        }
        return result
    }

    /*
     * MD5 加密
     */
    fun getSign(signStr: String): String {
        val buf = StringBuffer("")
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(signStr.toByteArray())
            val b = md.digest()
            var i: Int
            for (aB in b) {
                i = aB.toInt()
                if (i < 0) {
                    i += 256
                }
                if (i < 16) {
                    buf.append("0")
                }
                buf.append(Integer.toHexString(i))
            }
        } catch (e: NoSuchAlgorithmException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return buf.toString()
    }

    /**
     * @param obj
     * @return 拼接后的字符串
     */
    fun copy(vararg obj: Any?): String {
        val mStringBuffer = StringBuffer()
        for (anObj in obj) {
            mStringBuffer.append(anObj)
        }
        return mStringBuffer.toString()
    }

    /**
     * 替换字符串
     *
     * @param strSc  需要进行替换的字符串
     * @param oldStr 源字符串
     * @param newStr 替换后的字符串
     * @return 替换后对应的字符串
     * @since 1.1
     */
    fun replace(strSc: String?, oldStr: String?, newStr: String?): String? {
        var ret = strSc
        if (ret != null && oldStr != null && newStr != null) {
            ret = strSc!!.replace(oldStr.toRegex(), newStr)
        }
        return ret
    }

    fun getSplitString(srcString: String?, split: String?): String {
        val stringBuilder = StringBuilder(srcString)
        var i = 4
        while (i < stringBuilder.length) {
            stringBuilder.insert(i, split)
            i += 5
        }
        return stringBuilder.toString()
    }

    fun isContain(strSc: String, str: String, splitStr: String): Boolean {
        var split = ","
        if (!isNull(splitStr)) {
            split = splitStr
        }
        if (!isNull(strSc, str)) {
            val strs = strSc.split(split.toRegex()).toTypedArray()
            for (newStr in strs) {
                if (newStr.trim { it <= ' ' } == str) {
                    return true
                }
            }
        }
        return false
    }

    fun subZeroAndDot(s: String): String {
        var s = s
        if (s.indexOf(".") > 0) {
            s = s.replace("0+?$".toRegex(), "") //去掉多余的0
            s = s.replace("[.]$".toRegex(), "") //如最后一位是.则去掉
        }
        return s
    }

    /**
     * 判断多个参数是否都为空
     *
     * @param strArray
     * @return
     */
    fun isNull(vararg strArray: Any?): Boolean {
        var result = false
        for (str in strArray) {
            if (isEmpty(str)) {
                result = true
                break
            } else {
                result = false
            }
        }
        return result
    }

    /**
     * 判断多个参数是否为空
     *
     * @param str
     * @return
     */
    fun isEmpty(str: Any?): Boolean {
        return "" == str || str == null
    }

    /**
     * 替换字符串，修复java.lang.String类的replaceAll方法时第一参数是字符串常量正则时(如："address".
     * replaceAll("dd","$");)的抛出异常：java.lang.StringIndexOutOfBoundsException:
     * String index out of range: 1的问题。
     *
     * @param strSc  需要进行替换的字符串
     * @param oldStr 源字符串
     * @param newStr 替换后的字符串
     * @return 替换后对应的字符串
     * @since 1.2
     */
    fun replaceAll(strSc: String, oldStr: String, newStr: String?): String {
        var strSc = strSc
        var i = -1
        while (strSc.indexOf(oldStr).also { i = it } != -1) {
            strSc = StringBuffer(strSc.substring(0, i)).append(newStr)
                .append(strSc.substring(i + oldStr.length)).toString()
        }
        return strSc
    }

    /**
     * 将字符串转换成HTML格式的字符串
     *
     * @param str 需要进行转换的字符串
     * @return 转换后的字符串
     * @since 1.1
     */
    fun toHtml(str: String?): String? {
        var html = str
        return if (str == null || str.length == 0) {
            ""
        } else {
            html = replace(html, "&", "&amp;")
            html = replace(html, "<", "&lt;")
            html = replace(html, ">", "&gt;")
            html = replace(html, "\r\n", "\n")
            html = replace(html, "\n", "<br>\n")
            html = replace(html, "\"", "&quot;")
            html = replace(html, " ", "&nbsp;")
            html
        }
    }

    /**
     * 将HTML格式的字符串转换成常规显示的字符串
     *
     * @param str 需要进行转换的字符串
     * @return 转换后的字符串
     * @since 1.1
     */
    fun toText(str: String?): String? {
        var text = str
        return if (str == null || str.length == 0) {
            ""
        } else {
            text = replace(text, "&amp;", "&")
            text = replace(text, "&lt;", "<")
            text = replace(text, "&gt;", ">")
            text = replace(text, "<br>\n", "\n")
            text = replace(text, "<br>", "\n")
            text = replace(text, "&quot;", "\"")
            text = replace(text, "&nbsp;", " ")
            text = replace(text, "&ldquo;", "“")
            text = replace(text, "&rdquo;", "”")
            text
        }
    }

    /**
     * 获取加密的手机号
     *
     * @param phoneNum
     * @return
     */
    fun getEncryptMobile(phoneNum: String): String {
        if (!checkMobile(phoneNum)) {
            return phoneNum
        }
        val stringBuilder = StringBuilder(phoneNum.substring(0, 3))
        stringBuilder.append("****")
        stringBuilder.append(phoneNum.substring(7))
        return stringBuilder.toString()
    }

    /**
     * 检查手机号
     *
     * @param phoneNum
     * @return
     */
    fun checkMobile(phoneNum: String?): Boolean {
        val p = Pattern.compile("^1[3|4|5|7|8]\\d{9}$")
        val m = p.matcher(phoneNum)
        return m.matches()
    }

    /**
     * 验证固定电话号码
     *
     * @param phone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
     *
     * **国家（地区） 代码 ：**标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9 的一位或多位数字，
     * 数字之后是空格分隔的国家（地区）代码。
     *
     * **区号（城市代码）：**这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号——
     * 对不使用地区或城市代码的国家（地区），则省略该组件。
     *
     * **电话号码：**这包含从 0 到 9 的一个或多个数字
     * @return 验证成功返回true，验证失败返回false
     */
    fun checkPhone(phone: String): Boolean {
        if (phone.length != 11 || !TextUtils.isEmpty(phone) && !phone.startsWith("1")) {
            return false
        }
        val regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$"
        return Pattern.matches(regex, phone)
    }

    /**
     * 检查密码有效
     * 大于6位包含数字小写字母和大写字母
     *
     * @param pass
     * @return
     */
    fun checkPass(pass: String): Boolean {
        var match = !TextUtils.isEmpty(pass) && pass.length > 6 //检查位数
        if (match) {
            val patterns = arrayOf("[0-9]+", "[a-zA-Z]+")
            for (patternStr in patterns) {
                val pattern = Pattern.compile(patternStr)
                val matcher = pattern.matcher(pass)
                matcher.reset().usePattern(pattern)
                if (!matcher.find()) {
                    match = false
                    break
                }
            }
        }
        return match
    }

    /**
     * 根据string.xml资源格式化字符串
     *
     * @param context
     * @param resource
     * @param args
     * @return
     */
    fun formatResourceString(context: Context, resource: Int, vararg args: Any?): String? {
        val str = context.resources.getString(resource)
        return if (TextUtils.isEmpty(str)) {
            null
        } else String.format(str, *args)
    }

    /**
     * 验证身份证号码
     *
     * @param idCard 居民身份证号码15位或18位，最后一位可能是数字或字母
     * @return 验证成功返回true，验证失败返回false
     */
    fun checkIdCard(idCard: String): Boolean {
        if (idCard.length != 15 && idCard.length != 18) {
            return false
        }
        val regex = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}"
        return Pattern.matches(regex, idCard)
    }

    /**
     * 将元单位数字转成int类型的元
     *
     * @param numStr
     */
    fun numStrToInt(numStr: String?): Int {
        var num = 0
        try {
            if (!TextUtils.isEmpty(numStr)) {
                num = Integer.valueOf(numStr)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return num
    }

    /**
     * 格式化数字
     *
     * @param num (int)
     */
    fun simpleFormat(num: Int): String {
        val df = DecimalFormat("#.#")
        val numFormat = StringBuilder()
        val numDouble: Double
        if (num > 1000 && num < 10000) { //1千以上
            numDouble = num / 1000.0
            numFormat.append(df.format(numDouble)).append("k")
        } else if (num > 10000) { // 万以上
            numDouble = num / 10000.0
            numFormat.append(df.format(numDouble)).append("w")
        } else {
            numFormat.append(num)
        }
        return numFormat.toString()
    }

    /**
     * 数字转成以万、亿为单位，1.0-->1; 1.1-->1.1
     *
     * @param numStr (String)
     */
    fun newNumFormat(numStr: String): String {
        return try {
            val num = Integer.valueOf(numStr).toLong()
            newNumFormat(num)
        } catch (e: Exception) {
            e.printStackTrace()
            numStr
        }
    }

    /**
     * 数字转成以万、亿为单位，1.0-->1; 1.1-->1.1
     *
     * @param num (int)
     */
    fun newNumFormat(num: Long): String {
        val df = DecimalFormat("#.#")
        val numFormat = StringBuilder()
        val numDouble: Double
        if (num <= 1000000 && num > 10000) { // 万以上
            numDouble = num / 10000.0
            numFormat.append(df.format(numDouble)).append("万")
        } else if (num > 1000000) { // 百万以上
            numDouble = num / 1000000.0
            numFormat.append(df.format(numDouble)).append("百万")
        } else {
            numFormat.append(num)
        }
        return numFormat.toString()
    }

    /**
     * 将一字符串数组以某特定的字符串作为分隔来变成字符串
     *
     * @param strs  字符串数组
     * @param token 分隔字符串
     * @return 以token为分隔的字符串
     * @since 1.0
     */
    fun join(strs: Array<String?>?, token: String?): String? {
        if (strs == null) {
            return null
        }
        val sb = StringBuffer()
        for (i in strs.indices) {
            if (i != 0) {
                sb.append(token)
            }
            sb.append(strs[i])
        }
        return sb.toString()
    }

    /**
     * 将一字符串以某特定的字符串作为分隔来变成字符串数组
     *
     * @param str   需要拆分的字符串("@12@34@56")
     * @param token 分隔字符串("@")
     * @return 以token为分隔的拆分开的字符串数组
     * @since 1.0
     */
    fun split(str: String, token: String): Array<String> {
        val temp = str.substring(1, str.length)
        return temp.split(token.toRegex()).toTypedArray()
    }

    /**
     * 验证字符串合法性
     *
     * @param str  需要验证的字符串
     * @param test 非法字符串（如："~!#$%^&*()',;:?"）
     * @return true:非法;false:合法
     * @since 1.0
     */
    fun check(str: String?, test: String): Boolean {
        if (str == null || str == "") {
            return true
        }
        var flag = false
        for (i in 0 until test.length) {
            if (str.indexOf(test[i]) != -1) {
                flag = true
                break
            }
        }
        return flag
    }
    /**
     * 将数值型字符串转换成Integer型
     *
     * @param str          需要转换的字符型字符串
     * @param defaultValue 转换失败时返回的值
     * @return 成功则返回转换后的Integer型值；失败则返回ret
     * @since 1.0
     */
    /**
     * has already set default int value in the method body.
     *
     * @param str input string to convert to integer value
     * @return str's int value,if empty return default value
     */
    @JvmOverloads
    fun String2Integer(str: String, defaultValue: Int = DEFAULT_INT): Int {
        return if (TextUtils.isEmpty(str)) {
            defaultValue
        } else try {
            str.toInt()
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    fun getLongValue(o: Any, defaultValue: Long): Long {
        if (!isNull(o)) {
            try {
                return o.toString().toLong()
            } catch (e: Exception) {
            }
        }
        return defaultValue
    }

    /**
     * 将数值型转换成字符串
     *
     * @param it  需要转换的Integer型值
     * @param ret 转换失败的返回值
     * @return 成功则返回转换后的字符串；失败则返回ret
     * @since 1.0
     */
    fun Integer2String(it: Int?, ret: String): String {
        return try {
            Integer.toString(it!!)
        } catch (e: NumberFormatException) {
            ret
        }
    }

    /**
     * 比较两字符串大小(ASCII码顺序)
     *
     * @param str1 参与比较的字符串1
     * @param str2 参与比较的字符串2
     * @return str1>str2:1;str1<str2:-1></str2:-1>;str1=str2:0
     * @since 1.1
     */
    fun compare(str1: String, str2: String): Int { //
        if (str1 == str2) {
            return 0
        }
        val str1Length = str1.length
        val str2Length = str2.length
        var length = 0
        length = if (str1Length > str2Length) {
            str2Length
        } else {
            str1Length
        }
        for (i in 0 until length) {
            if (str1[i] > str2[i]) {
                return 1
            }
        }
        return -1
    }

    /**
     * 将阿拉伯数字的钱数转换成中文方式
     *
     * @param num 需要转换的钱的阿拉伯数字形式
     * @return 转换后的中文形式
     * @since 1.1
     */
    fun num2Chinese(num: Double): String {
        var result = ""
        val str = java.lang.Double.toString(num)
        if (str.contains(".")) {
            val begin = str.substring(0, str.indexOf("."))
            val end = str.substring(str.indexOf(".") + 1, str.length)
            val b = begin.toByteArray()
            val j = b.size
            run {
                var i = 0
                var k = j
                while (i < j) {
                    result += getConvert(begin[i])
                    if ("零" != result[result.length - 1].toString() + "") {
                        result += getWei(k)
                    }
                    println(result)
                    i++
                    k--
                }
            }
            for (i in 0 until result.length) {
                result = result.replace("零零".toRegex(), "零")
            }
            if ("零" == result[result.length - 1].toString() + "") {
                result = result.substring(0, result.length - 1)
            }
            result += "元"
            val bb = end.toByteArray()
            val jj = bb.size
            var i = 0
            var k = jj
            while (i < jj) {
                result += getConvert(end[i])
                if (bb.size == 1) {
                    result += "角"
                } else if (bb.size == 2) {
                    result += getFloat(k)
                }
                i++
                k--
            }
        } else {
            val b = str.toByteArray()
            val j = b.size
            var i = 0
            var k = j
            while (i < j) {
                result += getConvert(str[i])
                result += getWei(k)
                i++
                k--
            }
        }
        return result
    }

    fun getString(str: String, count: Int): String {
        return if (!(str[count - 1] >= 'a' && str[count - 1] <= 'z' || str[count - 1] >= 'A' && str[count - 1] <= 'Z')) {
            str.substring(0, count - 1)
        } else {
            str.substring(0, count)
        }
    }

    private fun getConvert(num: Char): String {
        return if (num == '0') {
            "零"
        } else if (num == '1') {
            "一"
        } else if (num == '2') {
            "二"
        } else if (num == '3') {
            "三"
        } else if (num == '4') {
            "四"
        } else if (num == '5') {
            "五"
        } else if (num == '6') {
            "六"
        } else if (num == '7') {
            "七"
        } else if (num == '8') {
            "八"
        } else if (num == '9') {
            "九"
        } else {
            ""
        }
    }

    private fun getFloat(num: Int): String {
        return if (num == 2) {
            "角"
        } else if (num == 1) {
            "分"
        } else {
            ""
        }
    }

    private fun getWei(num: Int): String {
        return if (num == 1) {
            ""
        } else if (num == 2) {
            "十"
        } else if (num == 3) {
            "百"
        } else if (num == 4) {
            "千"
        } else if (num == 5) {
            "万"
        } else if (num == 6) {
            "十"
        } else if (num == 7) {
            "百"
        } else if (num == 8) {
            "千"
        } else if (num == 9) {
            "亿"
        } else if (num == 10) {
            "十"
        } else if (num == 11) {
            "百"
        } else if (num == 12) {
            "千"
        } else if (num == 13) {
            "兆"
        } else {
            ""
        }
    }

    /**
     * 将字符串的首字母改为大写
     *
     * @param str 需要改写的字符串
     * @return 改写后的字符串
     * @since 1.2
     */
    fun firstToUpper(str: String): String {
        return str.substring(0, 1).toUpperCase() + str.substring(1)
    }

    /**
     * 判断list中是否包含某一个字符串
     *
     * @param str1
     * @return
     */
    fun listContain(list: List<*>?, str1: String?): Boolean {
        return !(list == null || list.size == 0) && list.contains(str1)
    }

    /**
     * 判断某个字符串是否包含某些字符串
     *
     * @param str  源字符串
     * @param list
     * @return
     */
    fun contains(str: String, list: Array<String?>): Boolean {
        for (aList in list) {
            if (str.contains(aList!!)) {
                return true
            }
        }
        return false
    }

    /**
     * list转String
     *
     * @param list
     * @param sign 分隔符号
     * @return
     */
    fun List2String(list: List<String?>?, sign: String?): String? {
        if (list == null || list.size == 0) {
            return null
        }
        val sb = StringBuffer()
        for (string in list) {
            sb.append(string).append(sign)
        }
        return sb.substring(0, sb.length - 1)
    }

    /**
     * String转list 去除null 空串
     *
     * @param target
     * @param sign   分隔符号
     * @return
     */
    fun String2List(target: String, sign: String): List<String> {
        val usersList: MutableList<String> = ArrayList()
        if (!isEmpty(target)) {
            val vs = target.split(sign.toRegex()).toTypedArray()
            for (v in vs) {
                if (!isEmpty(v)) {
                    usersList.add(v)
                }
            }
        }
        return usersList
    }

    fun escapeHtmlSign(value: String?): String? {
        if (value == null) {
            return null
        }
        return if (value is String) {
            var result: String = value
            // "'<>&
            result = result.replace("&".toRegex(), "&amp;").replace(">".toRegex(), "&gt;")
                .replace("<".toRegex(), "&lt;").replace("\"".toRegex(), "&quot;")
                .replace("'".toRegex(), "&#39;")
            result
        } else {
            value
        }
    }

    fun unEscapeHtmlSign(value: String?): String? {
        if (value == null) {
            return null
        }
        return if (value is String) {
            var result: String = value
            // "'<>&
            result = result.replace("&amp;".toRegex(), "&").replace("&gt;".toRegex(), ">")
                .replace("&lt;".toRegex(), "<").replace("&quot;".toRegex(), "\"")
                .replace("&#39;".toRegex(), "'")
            result
        } else {
            value
        }
    }

    /**
     * 根据Resource ID获取字符串
     *
     * @param resId
     * @return
     */
    @Deprecated("直接用getString()或者getContext().getString()的系统方法")
    fun getStringFromId(app: Application, resId: Int): String {
        return app.getString(resId)
    }

    fun formatNum(num: Float): String {
        val decimalFormat = DecimalFormat("0.00")
        return decimalFormat.format(num.toDouble())
    }

    fun numToString(str: Int): String {
        return doubleToString(str.toDouble(), 2)
    }

    /**
     * 将浮点数进行四舍五入
     *
     * @return 改写后的字符串
     */
    @JvmOverloads
    fun doubleToString(str: Double, offset: Int = 2): String {
        return BigDecimal(str.toString() + "").setScale(
            offset,
            BigDecimal.ROUND_HALF_UP
        ).toString()
    }

    fun stringDateTodate(date: String): Date {
        val time = date.substring(6, date.length - 7)
        return Date(time.toLong())
    }

    /**
     * 去除字符串前后的空格
     *
     * @param text
     * @return
     */
    fun trimString(text: String): String {
        var text = text
        if (!TextUtils.isEmpty(text)) {
            text = text.replace("[ |　]".toRegex(), " ").trim { it <= ' ' } //替换全角空格为半角，然后过滤
        }
        return text
    }

    fun strToInt(value: String, defauult: Int): Int {
        return try {
            value.toInt()
        } catch (e: Exception) {
            defauult
        }
    }

    /**
     * 去除转义
     *
     * @param text
     * @return
     */
    fun escapeString(text: String): String {
        var text = text
        try {
            if (!TextUtils.isEmpty(text)) {
                text = text.replace("[\\n\\r]*".toRegex(), "")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (TextUtils.isEmpty(text)) {
            text = ""
        }
        return text
    }

    /**
     * 添加url参数
     *
     * @param url
     * @param params
     * @return
     */
    fun appendUrlParams(url: String, params: String): String {
        var url = url
        if (TextUtils.isEmpty(url)) {
            return url
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length - 1)
        }
        val pattern = Pattern.compile("\\?[\\w]*=")
        val matcher = pattern.matcher(url)
        return if (matcher.find()) {
            "$url&$params"
        } else {
            "$url?$params"
        }
    }

    /**
     * 设置一段不同颜色的文字
     *
     * @param colors：颜色数组，按顺序取，getColor所得的值
     * @param index：切换颜色对应的位置（第几个内容需要变色）
     * @param s：文字数组
     */
    fun getColorSpan(
        index: Array<Int?>,
        colors: IntArray,
        vararg s: CharSequence
    ): SpannableStringBuilder {
        val indexList = Arrays.asList(*index)
        val stringBuilder = SpannableStringBuilder()
        var colorIndex = 0
        for (i in 0 until s.size) {
            if (indexList.contains(Integer.valueOf(i))) {
                val spanString = SpannableString(s[i])
                if (colorIndex >= colors.size) {
                    colorIndex = colors.size - 1
                }
                val span = ForegroundColorSpan(colors[colorIndex])
                colorIndex++
                spanString.setSpan(span, 0, s[i].length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                stringBuilder.append(spanString)
            } else {
                stringBuilder.append(s[i])
            }
        }
        return stringBuilder
    }

    /**
     * 字符拆分
     *
     * @param str   原字符
     * @param split 分隔符
     * @return
     */
    fun getSubStrSplit(str: String, split: String?): String {
        var str = str
        if (!TextUtils.isEmpty(split) && !TextUtils.isEmpty(str) && str.contains(split!!)) {
            str = str.substring(0, str.indexOf(split))
        }
        return str
    }

    /**
     * 截取指定size的String
     */
    fun resizeContent(content: String, size: Int): String {
        val resizeContent = StringBuilder()
        if (!TextUtils.isEmpty(content) && content.length > size) {
            resizeContent.append(content.substring(0, size))
            resizeContent.append("...")
        } else {
            resizeContent.append(content)
        }
        return resizeContent.toString()
    }

    /**
     * 转码中文字符串
     *
     * @param srcStr
     * @return
     */
    fun encodeChineseStr(srcStr: String): String? {
        var dstStr: String? = srcStr
        if (!TextUtils.isEmpty(dstStr)) {
            if (srcStr.length < srcStr.toByteArray().size) {
                try {
                    dstStr = URLEncoder.encode(dstStr, "utf-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            }
        }
        return dstStr
    }

    /**
     * 用来比较手机号版本
     *
     * @param s1
     * @param s2
     * @return s1<s2></s2>
     */
    fun compareToMin(s1: String, s2: String): Boolean {
        var s1 = s1
        var s2 = s2
        if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)) {
            return false
        }
        s1 = s1.replace("[a-zA-Z]".toRegex(), "")
        s2 = s2.replace("[a-zA-Z]".toRegex(), "")
        val rt = s1.compareTo(s2)
        return if (rt < 0) {
            true
        } else if (rt > 0) {
            false
        } else {
            false
        }
    }

    /**
     * 截取指定长度 从0开始，包左不包右
     */
    fun spliteTime(dateStr: String, start: Int, end: Int): String {
        val sequence = dateStr.subSequence(start, end)
        return sequence.toString()
    }

    /**
     * 读取表情配置文件
     *
     * @param context
     * @return
     */
    fun getEmojiFile(context: Context): List<String?>? {
        try {
            val list: MutableList<String?> = ArrayList()
            val `in` = context.resources.assets.open("emoji")
            val br = BufferedReader(InputStreamReader(`in`, "UTF-8"))
            var str: String? = null
            while (br.readLine().also { str = it } != null) {
                list.add(str)
            }
            return list
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 是否有中文字符
     *
     * @param str
     * @return
     */
    fun hasChineseChar(str: String?): Boolean {
        var temp = false
        val p = Pattern.compile("[\u4e00-\u9fa5]")
        val m = p.matcher(str)
        if (m.find()) {
            temp = true
        }
        return temp
    }

    /**
     * 字符是否是中文字符
     *
     *
     * 不包括““”号，“。”号，“，”号
     *
     *
     * GENERAL_PUNCTUATION 判断中文的“号
     *
     *
     * CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
     *
     *
     * HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
     */
    private fun isChinese(c: Char): Boolean {
        val ub = Character.UnicodeBlock.of(c)
        return ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
    }

    /**
     * 判断字符串是否含有中文字符
     */
    fun isContainChinese(strName: String): Boolean {
        val ch = strName.toCharArray()
        for (c in ch) {
            if (isChinese(c)) {
                return true
            }
        }
        return false
    }

    /**
     * 去掉字符串中的换行和空格
     *
     * @param str str
     * @return string
     */
    fun replaceBlank(str: String?): String {
        var dest = ""
        if (str != null) {
            val p = Pattern.compile("\\r\\n|\\r|\\n")
            val m = p.matcher(str)
            dest = m.replaceAll("")
        }
        return dest
    }
}