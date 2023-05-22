package com.victoryvalery.tfsproject.domain.models

data class DateItem(
    val day: String,
    private val month: String
) {
    override fun toString(): String = "${this.day} ${this.month}"
}

