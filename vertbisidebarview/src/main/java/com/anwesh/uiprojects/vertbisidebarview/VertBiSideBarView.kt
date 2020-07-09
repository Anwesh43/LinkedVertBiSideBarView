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
