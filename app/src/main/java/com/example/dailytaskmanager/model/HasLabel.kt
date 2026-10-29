package com.example.dailytaskmanager.model

import androidx.annotation.StringRes

interface HasLabel {
    @get:StringRes
    val labelRes: Int
}