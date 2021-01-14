package com.lge.lgshoptimem.model.dto

class Login() {
    data class Response(val data: Token): BaseResponse() {

        // TODO
//        override fun toString(): String {
//            return super.toString() + "Response(data=$data)"
//        }
    }

    data class Token(val accessToken: String)
}