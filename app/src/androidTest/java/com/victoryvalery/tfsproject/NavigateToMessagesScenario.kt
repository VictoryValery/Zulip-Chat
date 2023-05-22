package com.victoryvalery.tfsproject

import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import com.victoryvalery.tfsproject.screen.StreamsScreen

class NavigateToMessagesScenario : Scenario() {
    override val steps: TestContext<Unit>.() -> Unit = {
        step("Open messages") {
            StreamsScreen {
                streamsList {
                    lastChild<StreamsScreen.StreamItem> {
                        streamButton.click()
                    }
                    lastChild<StreamsScreen.TopicItem> {
                        topicTitle.click()
                    }
                }
            }
        }
    }
}
