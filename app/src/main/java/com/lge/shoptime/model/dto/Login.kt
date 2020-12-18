package com.lge.shoptime.model.dto

class Login() {
    data class Response(val data: Token): BaseResponse()

    data class Token(val accessToken: String)
}