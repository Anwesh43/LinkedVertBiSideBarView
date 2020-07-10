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
    translate(0f, h / 2)
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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.draw(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class VBSBNode(var i : Int, val state : State = State()) {

        private var next : VBSBNode? = null
        private var prev : VBSBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = VBSBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawVBSBNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : VBSBNode {
            var curr : VBSBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class VertBiSideBar(var i : Int, val state : State = State()) {

        private var curr : VBSBNode = VBSBNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : VertBiSideBarView) {

        private val animator : Animator = Animator(view)
        private val vertBiSideBar : VertBiSideBar = VertBiSideBar(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun draw(canvas : Canvas) {
            canvas.drawColor(backColor)
            vertBiSideBar.draw(canvas, paint)
            animator.animate {
                vertBiSideBar.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            vertBiSideBar.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : VertBiSideBarView {
            val view : VertBiSideBarView = VertBiSideBarView(activity)
            activity.setContentView(view)
            return view
        }
    }
}