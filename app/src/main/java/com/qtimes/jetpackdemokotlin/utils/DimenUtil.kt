/**
 * Created with JackHou
 * Date: 2021/4/2
 * Time: 13:45
 * Description:
 */

package com.qtimes.jetpackdemokotlin.utils

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.PrintWriter


object DimenUtil {
    //文件保存的路径  是在该项目下根路径下创建
    private const val rootPath = "app/src/main/res/values-{0}x{1}"


    private const val dw = 768f //默认布局的宽

    private const val dh = 1280f //默认布局的高


    private const val WTemplate = "<dimen name=\"x{0}\">{1}px</dimen>\n"
    private const val HTemplate = "<dimen name=\"y{0}\">{1}px</dimen>\n"

    @JvmStatic
    fun main(args: Array<String>) {
        makeString(600, 940)
        makeString(600, 1024)
        makeString(1080, 1920)
        makeString(1080, 2400)
    }

    //获取dimen.xml的文本内容
    private fun makeString(w: Int, h: Int) {
        println("1111111111")
        val sb = StringBuffer()
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
        sb.append("<resources>")

        //遍历获取一系列宽的值
        val cellw = w / dw //宽的比例
        run {
            var i = 0
            while (i < dw) {
                sb.append(
                    WTemplate.replace("{0}", i.toString() + "")
                        .replace("{1}", change(cellw * i).toString() + "")
                )
                i++
            }
        }
        sb.append(WTemplate.replace("{0}", dw.toString() + "").replace("{1}", w.toString() + ""))

        //遍历获取一系列高的值
        val cellh = h / dh //高的比例
        var i = 0
        while (i < dh) {
            sb.append(
                HTemplate.replace("{0}", i.toString() + "")
                    .replace("{1}", change(cellh * i).toString() + "")
            )
            i++
        }
        sb.append(HTemplate.replace("{0}", dh.toString() + "").replace("{1}", h.toString() + ""))
        sb.append("</resources>")
        makeFile(w, h, sb.toString())
    }

    //创建文件并写入内容
    private fun makeFile(w: Int, h: Int, text: String) {
        println("22222222222222")
        val path = rootPath.replace("{0}", h.toString() + "").replace("{1}", w.toString() + "")
        val rootFile = File(path)
        if (!rootFile.exists()) {
            rootFile.mkdirs()
        }
        val file = File(path, "dimens.xml")
        println("333333333333333:" + file.absolutePath)
        try {
            val pw = PrintWriter(FileOutputStream(file))
            pw.println(text)
            pw.close()
            println("4444444444444444")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            println("5555555555555555")
        }
    }


    private fun change(a: Float): Float {
        val temp = (a * 100).toInt()
        return temp / 100f
    }
}