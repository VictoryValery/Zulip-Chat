package com.victoryvalery.tfsproject

import android.content.res.Resources
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.victoryvalery.tfsproject.presentation.cicerone.Screens
import com.victoryvalery.tfsproject.presentation.screenMainActivity.MainActivity
import com.victoryvalery.tfsproject.screen.BottomActionsDialogScreen
import com.victoryvalery.tfsproject.screen.BottomEmojiDialogScreen
import com.victoryvalery.tfsproject.screen.MessageScreen
import com.victoryvalery.tfsproject.utill.HasSingleReactionViewFromOtherUsers
import com.victoryvalery.tfsproject.utill.HasSingleReactionViewWithSelectedAssertion
import com.victoryvalery.tfsproject.utill.MockRequestDispatcher
import com.victoryvalery.tfsproject.utill.loadFromAssets
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@RunWith(AndroidJUnit4::class)
class MessageScreenTest : TestCase() {

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
    fun displayMessageList() = run {
        mockServer.dispatcher = MockRequestDispatcher().apply {
            returnsForPath("/users/me/subscriptions") { setBody(loadFromAssets("subscriptions.json")) }
            returnsForPath("/streams") { setBody(loadFromAssets("all_streams.json")) }
        }
        ActivityScenario.launch(MainActivity::class.java)
        scenario(NavigateToMessagesScenario())
        MessageScreen {
            messageList.scrollToEnd()
            messageList.isDisplayed()
        }
    }

    @Test
    fun emptyMessageList() = run {
        mockServer.dispatcher = MockRequestDispatcher().apply {
            returnsForPath("/users/me/subscriptions") { setBody(loadFromAssets("subscriptions_messages_empty.json")) }
            returnsForPath("/streams") { setBody(loadFromAssets("all_streams.json")) }
        }
        ActivityScenario.launch(MainActivity::class.java)
        scenario(NavigateToMessagesScenario())
        MessageScreen {
            loader.isGone()
        }
    }

    @Test
    fun errorIsDisplayed() = run {
        ActivityScenario.launch(MainActivity::class.java)
        before {
            val router = App.INSTANCE.appComponent.getRouter()
            router.newRootScreen(Screens.Messages("general88", 0, true, "gold1"))
        }.after {
        }.run {
            MessageScreen {
                flakySafely(timeoutMs = 20_000) {
                    kSnackbar.isDisplayed()
                }
            }
        }
    }

    @Test
    fun messageWithoutReactions() = run {
        mockServer.dispatcher = MockRequestDispatcher().apply {
            returnsForPath("/users/me/subscriptions") { setBody(loadFromAssets("subscriptions.json")) }
            returnsForPath("/streams") { setBody(loadFromAssets("all_streams.json")) }
        }
        ActivityScenario.launch(MainActivity::class.java)
        scenario(NavigateToMessagesScenario())
        MessageScreen {
            messageList.lastChild<MessageScreen.MessageItem> {
                reactions.isGone()
            }
        }
    }

    @Test
    fun messageWithMyReaction() = run {
        mockServer.dispatcher = MockRequestDispatcher().apply {
            returnsForPath("/users/me/subscriptions") { setBody(loadFromAssets("subscriptions.json")) }
            returnsForPath("/streams") { setBody(loadFromAssets("all_streams.json")) }
        }
        ActivityScenario.launch(MainActivity::class.java)
        scenario(NavigateToMessagesScenario())
        MessageScreen {
            messageList.childAt<MessageScreen.MessageItem>(2) {
                reactions.view.check(HasSingleReactionViewWithSelectedAssertion())
            }
        }
    }

    @Test
    fun messageWithOtherUserReaction() = run {
        mockServer.dispatcher = MockRequestDispatcher().apply {
            returnsForPath("/users/me/subscriptions") { setBody(loadFromAssets("subscriptions.json")) }
            returnsForPath("/streams") { setBody(loadFromAssets("all_streams.json")) }
        }
        ActivityScenario.launch(MainActivity::class.java)
        scenario(NavigateToMessagesScenario())
        MessageScreen {
            messageList.childAt<MessageScreen.MessageItem>(2) {
                reactions.view.check(HasSingleReactionViewFromOtherUsers())
            }
        }
    }

    @Test
    fun groupingByDates() = run {
        mockServer.dispatcher = MockRequestDispatcher().apply {
            returnsForPath("/users/me/subscriptions") { setBody(loadFromAssets("subscriptions.json")) }
            returnsForPath("/streams") { setBody(loadFromAssets("all_streams.json")) }
        }
        ActivityScenario.launch(MainActivity::class.java)
        scenario(NavigateToMessagesScenario())

        val resources: Resources = InstrumentationRegistry.getInstrumentation().context.resources
        val locale = resources.configuration.locales[0]
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM", locale)

        MessageScreen {
            messageList {
                lastChild<MessageScreen.MessageItem> {
                    messageBody.isVisible()
                }
                childAt<MessageScreen.DateItem>(10) {
                    date.isVisible()
                    val expectedDate = LocalDate.of(2023, 4, 24)
                    date.containsText(dateFormatter.format(expectedDate))
                }
                childAt<MessageScreen.MessageItem>(9) {
                    messageBody.hasText("dot")
                }
                childAt<MessageScreen.MessageItem>(8) {
                    messageBody.hasText("dot")
                }
                childAt<MessageScreen.DateItem>(7) {
                    date.isVisible()
                    val expectedDate = LocalDate.of(2023, 4, 17)
                    date.containsText(dateFormatter.format(expectedDate))
                }
                childAt<MessageScreen.MessageItem>(6) {
                    messageBody.hasText("ehf")
                }
                childAt<MessageScreen.DateItem>(5) {
                    date.isVisible()
                    val expectedDate = LocalDate.of(2023, 4, 9)
                    date.containsText(dateFormatter.format(expectedDate))
                }
                childAt<MessageScreen.MessageItem>(4) {
                    messageBody.hasText("toptop")
                }
                childAt<MessageScreen.DateItem>(3) {
                    date.isVisible()
                    val expectedDate = LocalDate.of(2023, 4, 4)
                    date.containsText(dateFormatter.format(expectedDate))
                }
            }
        }
    }


    @Test
    fun addMyReactionToMessage() = run {
        mockServer.dispatcher = MockRequestDispatcher().apply {
            returnsForPath("/users/me/subscriptions") { setBody(loadFromAssets("subscriptions.json")) }
            returnsForPath("/streams") { setBody(loadFromAssets("all_streams.json")) }
        }
        ActivityScenario.launch(MainActivity::class.java)
        scenario(NavigateToMessagesScenario())
        MessageScreen {
            messageList {
                lastChild<MessageScreen.MessageItem> {
                    messageBody.longClick()
                }
            }
        }
        BottomActionsDialogScreen {
            actionAddReaction {
                click()
            }
        }
        BottomEmojiDialogScreen {
            flakySafely(timeoutMs = 10_000) {
                emojiList {
                    lastChild<BottomEmojiDialogScreen.EmojiItem> {
                        click()
                    }
                }
            }
        }
    }
}
