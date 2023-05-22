package com.victoryvalery.tfsproject.utill

import androidx.test.platform.app.InstrumentationRegistry

fun loadFromAssets(filePath: String) =
    InstrumentationRegistry
        .getInstrumentation().context.resources.assets.open(filePath).use {
            it.bufferedReader().readText()
        }
