package com.victoryvalery.tfsproject.data.apiStorage

const val API_USER = "basesforwork@yandex.ru"
const val USER_UPLOADS = "/user_uploads/"

//tinkoff
const val IMG_URL = "https://tinkoff-android-spring-2023.zulipchat.com/user_uploads/"
const val BASE_URL = "https://tinkoff-android-spring-2023.zulipchat.com/api/v1/"
const val API_KEY = "s4vcATw4WOqBur1eEtd914JbFHtNQ80u"
const val ME_USER_ID_CONST = 604182

//test
//const val IMG_URL = "https://victoryvalery.zulipchat.com/user_uploads/"
//const val BASE_URL = "https://victoryvalery.zulipchat.com/api/v1/"
//const val API_KEY = "w0YWoDJhCisSSOFEpBl2mH5t2bqkmPzw"
//const val ME_USER_ID_CONST = 598404

const val REGISTER_EVENT = "register"
const val EVENT = "events"
const val SEND_FILE = "user_uploads"
const val SINGLE_MESSAGE = "messages/{message_id}"
const val GET_STREAM_TOPICS = "users/me/{stream_id}/topics"
const val GET_ME_USER = "users/me"
const val GET_ALL_USERS = "users"
const val GET_USER_PRESENCE = "users/{user_id}/presence"
const val GET_ALL_USERS_PRESENCE = "realm/presence"
const val GET_STREAMS = "streams"
const val GET_SUBSCRIBED_STREAMS = "users/me/subscriptions"
const val GET_MESSAGES = "messages"
const val POST_ME_PRESENCE = "users/me/presence"
const val POST_MESSAGES = "messages"
const val PATH_REACTIONS = "messages/{message_id}/reactions"

const val API_DELAY = 500L
const val API_SECONDS = 1000L
const val API_TRY_COUNT = 3
