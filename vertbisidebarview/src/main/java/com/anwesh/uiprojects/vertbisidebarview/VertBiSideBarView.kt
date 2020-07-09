package com.anwesh.uiprojects.vertbisidebarview

/**
 * Created by anweshmishra on 09/07/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.content.Context
import android.app.Activity

val colors : Array<String> = arrayOf("#F44336", "#03A9F4", "#3F51B5", "#4CAF50", "#009688")
val bars : Int = 2
val scGap : Float = 0.02f / (bars + 2)
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
val sizeFactor : Float = 4f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawVertBiSideBar(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, 2)
    val sf2 : Float = sf.divideScale(1, 2)
    save()
    translate(w / 2, h / 2)
    save()
    translate(h / 2, 0f)
    drawRect(RectF(-size / 2, -h * sf1, size / 2, 0f), paint)
    restore()
    for (j in 0..1) {
        val sf2i : Float = sf2.divideScale(j, 2)
        save()
        scale(1f - 2 * j, 1f - 2 * j)
        translate(0f, h / 2)
        drawRect(RectF(-(w / 2 * sf2i), -size, 0f, 0f), paint)
        restore()
    }
    restore()
}

fun Canvas.drawVBSBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    drawVertBiSideBar(scale, w, h, paint)
}

class VertBiSideBarView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}