package com.victoryvalery.tfsproject.utill

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import com.victoryvalery.tfsproject.presentation.customViews.SingleReactionView

class HasSingleReactionViewWithSelectedAssertion : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }

        if (view is ViewGroup && view.childCount > 0) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                if (child is SingleReactionView && child.isSelected) {
                    return
                }
            }
        }
        throw AssertionError("No selected SingleReactionView found")
    }
}

class HasSingleReactionViewFromOtherUsers : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }

        if (view is ViewGroup && view.childCount > 0) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                if (child is SingleReactionView && (!child.isSelected || child.count > 1)) {
                    return
                }
            }
        }
        throw AssertionError("No other user SingleReactionView found")
    }
}
