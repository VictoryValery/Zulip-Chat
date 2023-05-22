package com.victoryvalery.tfsproject

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.victoryvalery.tfsproject.presentation.screenMainActivity.MainActivity
import com.victoryvalery.tfsproject.screen.StreamsScreen
import com.victoryvalery.tfsproject.utill.MockRequestDispatcher
import com.victoryvalery.tfsproject.utill.loadFromAssets
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StreamScreenTest : TestCase() {

    @get:Rule
    val mockServer = MockWebServer()

    @Before
    fun setUp() {
        val appComponent = App.INSTANCE.appComponent.getApiUrlProvider()
        appComponent.url = mockServer.url("/").toString()
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun streamEmptySearchResult() = run {
        mockServer.dispatcher = MockRequestDispatcher().apply {
            returnsForPath("/users/me/subscriptions") { setBody(loadFromAssets("subscriptions_test.json")) }
            returnsForPath("/streams") { setBody(loadFromAssets("all_streams.json")) }
        }
        ActivityScenario.launch(MainActivity::class.java)
        StreamsScreen {
            streamSearch.click()
            streamSearch.typeQuery("test")
            device.uiDevice.pressBack()
            streamsList.hasSize(0)
        }
    }

    @Test
    fun streamNotEmptySearchResult() = run {
        mockServer.dispatcher = MockRequestDispatcher().apply {
            returnsForPath("/users/me/subscriptions") { setBody(loadFromAssets("subscriptions_test.json")) }
            returnsForPath("/streams") { setBody(loadFromAssets("all_streams.json")) }
        }
        ActivityScenario.launch(MainActivity::class.java)
        StreamsScreen {
            streamSearch.click()
            streamSearch.typeQuery("general")
            device.uiDevice.pressBack()
            streamsList.hasSize(1)
        }
    }
}
