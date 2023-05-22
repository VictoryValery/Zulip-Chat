package com.victoryvalery.tfsproject.utill

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockRequestDispatcher : Dispatcher() {

    private val responses: MutableMap<String, MockResponse> = mutableMapOf()

    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: return MockResponse().setResponseCode(404)

        return when {
            path.startsWith("/messages") -> MockResponse()
                .setResponseCode(200)
                .setBody(
                    loadFromAssets("messages_all_streams_stream_events.json")
                )
            else -> responses[request.path] ?: MockResponse().setResponseCode(404)
        }
    }

    fun returnsForPath(path: String, response: MockResponse.() -> MockResponse) {
        responses[path] = response(MockResponse())
    }
}
