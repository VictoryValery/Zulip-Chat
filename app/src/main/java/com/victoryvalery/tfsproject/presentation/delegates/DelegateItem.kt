package com.victoryvalery.tfsproject.presentation.delegates

interface DelegateItem {
    fun content(): Any
    fun id(): Int
    fun compareToOther(other: DelegateItem): Boolean
}
