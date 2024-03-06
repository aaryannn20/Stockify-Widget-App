package com.starorigins.stockify.widgetapp

import java.io.Serializable


class LoginResponse:Serializable {
    var email: String? = null
    var password: String? = null
    var token: String? = null
}
