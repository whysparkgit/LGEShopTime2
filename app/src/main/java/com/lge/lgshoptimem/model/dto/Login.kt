package com.lge.lgshoptimem.model.dto

class Login {
    data class Response(val data: Token): BaseResponse()
    {
        override fun toString(): String {
            return super.toString() + data.toString()
        }
    }

    data class Token(val accessToken: String)
}