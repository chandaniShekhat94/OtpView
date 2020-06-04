package com.otpviewcontrol.utility

import java.util.regex.Pattern

fun String.isValidEmail(): Boolean {
    return Pattern.compile(
        "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
    ).matcher(this).matches()
}

fun String.isValidPhoneNumber(): Boolean {
    val numberAsIntCorrect = Character.getNumericValue(this[0])
    return this.length == 10 && numberAsIntCorrect > 1
}

