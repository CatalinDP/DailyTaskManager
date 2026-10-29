package com.example.dailytaskmanager.model

import androidx.annotation.StringRes
import com.example.dailytaskmanager.R

enum class StandUpInterval(
    val hours: Int,
    @StringRes override val labelRes: Int
) : HasLabel {
    ZERO(0, R.string.common_disabled),
    ONE(1, R.string.interval_1h),
    TWO(2, R.string.interval_2h),
    THREE(3, R.string.interval_3h),
    FOUR(4, R.string.interval_4h)
}