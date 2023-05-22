package com.victoryvalery.tfsproject.presentation.delegates.date

import com.victoryvalery.tfsproject.domain.models.DateItem
import com.victoryvalery.tfsproject.presentation.delegates.DelegateItem

data class DateDelegateItem(
    val value: DateItem
) : DelegateItem {
    override fun content(): Any = value
    override fun id(): Int = value.hashCode()
    override fun compareToOther(other: DelegateItem): Boolean {
        return (other as DateDelegateItem).value == content()
    }
}
