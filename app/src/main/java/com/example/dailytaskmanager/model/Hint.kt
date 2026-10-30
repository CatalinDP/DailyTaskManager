package com.example.dailytaskmanager.model

import androidx.annotation.StringRes
import com.example.dailytaskmanager.R

/** First-use tips. [id] is persisted in DataStore, so never change an existing one. */
enum class Hint(val id: String, @StringRes val title: Int, @StringRes val body: Int) {
    HOME_STATS("home_stats", R.string.hint_home_stats_title, R.string.hint_home_stats_body),
    HOME_TAP_TASK("home_tap_task", R.string.hint_home_tap_task_title, R.string.hint_home_tap_task_body),
    TASK_ADD("task_add", R.string.hint_task_add_title, R.string.hint_task_add_body),
    TASK_SWIPE_DELETE("task_swipe_delete", R.string.hint_task_swipe_delete_title, R.string.hint_task_swipe_delete_body),
    TASK_DAY_SELECTOR("task_day_selector", R.string.hint_task_day_selector_title, R.string.hint_task_day_selector_body);

    companion object {
        /** Display order matters: only the first pending hint of a screen is shown. */
        val home = listOf(HOME_STATS, HOME_TAP_TASK)
        val tasks = listOf(TASK_ADD, TASK_SWIPE_DELETE, TASK_DAY_SELECTOR)
    }
}

/** Next hint to show, or null while dismissed hints are still loading or all were dismissed. */
fun List<Hint>.firstPending(dismissed: Set<String>?): Hint? =
    if (dismissed == null) null else firstOrNull { it.id !in dismissed }
