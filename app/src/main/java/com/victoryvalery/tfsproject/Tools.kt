package com.victoryvalery.tfsproject

import android.content.Context
import android.os.IBinder
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

fun Float.dp(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    context.resources.displayMetrics
)

fun Float.sp(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this,
    context.resources.displayMetrics
)

fun Long.getMonthFromTimestamp(): Int {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())
    return dateTime.monthValue
}

fun Long.getDayOfMonthFromTimestamp(): Int {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.systemDefault())
    return dateTime.dayOfMonth
}

fun getMonthName(monthNumber: Int): String {
    return Month.of(monthNumber).getDisplayName(TextStyle.SHORT, Locale.getDefault())
}

fun closeKeyboard(context: Context, windowToken: IBinder) {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(
        windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}
