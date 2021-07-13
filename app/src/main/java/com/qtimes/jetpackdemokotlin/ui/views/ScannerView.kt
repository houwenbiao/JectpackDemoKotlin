package com.qtimes.jetpackdemokotlin.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.zxing.ResultPoint
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.utils.ScreenUtil
import com.qtimes.jetpackdemokotlin.utils.ScreenUtil.dip2px
import com.qtimes.jetpackdemokotlin.utils.ScreenUtil.sp2px
import java.util.*

class ScannerView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private val maskPaint: Paint
    private val laserPaint: Paint
    private val dotPaint: Paint
    private var resultBitmap: Bitmap? = null
    private val maskColor: Int
    private val resultColor: Int
    private val dots: MutableMap<ResultPoint, Long> = HashMap(
        16
    )
    private var frame: Rect? = null
    private var framePreview: Rect? = null
    private val textPaint: Paint
    fun setFraming(
        frame: Rect?,
        framePreview: Rect?
    ) {
        this.frame = frame
        this.framePreview = framePreview
        invalidate()
    }

    fun drawResultBitmap(bitmap: Bitmap?) {
        resultBitmap = bitmap
        invalidate()
    }

    fun addDot(dot: ResultPoint) {
        dots[dot] = System.currentTimeMillis()
        invalidate()
    }

    public override fun onDraw(canvas: Canvas) {
        if (frame == null) return
        val now = System.currentTimeMillis()
        val width = canvas.width
        val height = canvas.height

        // draw mask darkened
        maskPaint.color = if (resultBitmap != null) resultColor else maskColor
        canvas.drawRect(0f, 0f, width.toFloat(), frame!!.top.toFloat(), maskPaint)
        canvas.drawRect(
            0f,
            frame!!.top.toFloat(),
            frame!!.left.toFloat(),
            (frame!!.bottom + 1).toFloat(),
            maskPaint
        )
        canvas.drawRect(
            (frame!!.right + 1).toFloat(),
            frame!!.top.toFloat(),
            width.toFloat(),
            (frame!!.bottom + 1).toFloat(),
            maskPaint
        )
        canvas.drawRect(
            0f,
            (frame!!.bottom + 1).toFloat(),
            width.toFloat(),
            height.toFloat(),
            maskPaint
        )
        val rect = Rect()
        textPaint.getTextBounds(
            resources.getString(R.string.scan_qr_code_warn),
            0,
            resources.getString(R.string.scan_qr_code_warn).length,
            rect
        )
        canvas.drawText(
            resources.getString(R.string.scan_qr_code_warn),
            ScreenUtil.getWidthInPx(context) / 2f,
            frame!!.bottom + rect.height() + ScreenUtil.dip2px(context, 7f) * 1f,
            textPaint
        )
        if (resultBitmap != null) {
            canvas.drawBitmap(resultBitmap!!, null, frame!!, maskPaint)
        } else {
            // draw red "laser scanner" to show decoding is active
            val laserPhase = now / 600 % 2 == 0L
            laserPaint.alpha = if (laserPhase) 160 else 255
            canvas.drawRect(frame!!, laserPaint)

            // draw points
            val frameLeft = frame!!.left
            val frameTop = frame!!.top
            val scaleX = frame!!.width() / framePreview!!.width().toFloat()
            val scaleY = frame!!.height() / framePreview!!.height().toFloat()
            val i: MutableIterator<Map.Entry<ResultPoint, Long>> = dots
                .entries.iterator()
            while (i.hasNext()) {
                val entry = i.next()
                val age = now - entry.value
                if (age < DOT_TTL_MS) {
                    dotPaint.alpha = ((DOT_TTL_MS - age) * 256 / DOT_TTL_MS).toInt()
                    val point = entry.key
                    canvas.drawPoint(
                        (frameLeft + (point.x * scaleX).toInt()).toFloat(), (
                                frameTop + (point.y * scaleY).toInt()).toFloat(), dotPaint
                    )
                } else {
                    i.remove()
                }
            }

            // schedule redraw
            postInvalidateDelayed(LASER_ANIMATION_DELAY_MS)
        }
    }

    companion object {
        private const val LASER_ANIMATION_DELAY_MS = 100L
        private const val DOT_OPACITY = 0xa0
        private const val DOT_TTL_MS = 500
    }

    init {
        val res = resources
        maskColor = res.getColor(R.color.scan_mask)
        resultColor = res.getColor(R.color.scan_result_view)
        val laserColor = res.getColor(R.color.scan_laser)
        val dotColor = res.getColor(R.color.scan_dot)
        maskPaint = Paint()
        maskPaint.style = Paint.Style.FILL
        textPaint = Paint()
        textPaint.color = Color.parseColor("#FFFFFF")
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = sp2px(context!!, 16f).toFloat()
        val DOT_SIZE = dip2px(context, 3f)
        laserPaint = Paint()
        laserPaint.color = laserColor
        laserPaint.strokeWidth = DOT_SIZE.toFloat()
        laserPaint.style = Paint.Style.STROKE
        dotPaint = Paint()
        dotPaint.color = dotColor
        dotPaint.alpha = DOT_OPACITY
        dotPaint.style = Paint.Style.STROKE
        dotPaint.strokeWidth = DOT_SIZE.toFloat()
        dotPaint.isAntiAlias = true
    }
}