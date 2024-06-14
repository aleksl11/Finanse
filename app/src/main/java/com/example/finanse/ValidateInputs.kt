package com.example.finanse

import androidx.core.text.isDigitsOnly

class ValidateInputs {
    fun isAmountValid(amount: String): Boolean{
        return if (amount.isDigitsOnly()) true
        else amount.matches(Regex("\\d+\\.\\d*"))
    }

    fun isDateValid(date: String): Boolean{
        return date.matches(Regex("\\b((0[1-9]|1[0-9]|2[0-8])\\.(02)\\.\\d{4}|" +
                "(29)\\.(02)\\.(\\d{2}(([02468][048])|([13579][26]))|([048][048])|([13579][26])00)|" +
                "(0[1-9]|[12][0-9]|30)\\.(04|06|09|11)\\.\\d{4}|" +
                "(0[1-9]|[12][0-9]|3[01])\\.(01|03|05|07|08|10|12)\\.\\d{4})\\b"))
    }
}