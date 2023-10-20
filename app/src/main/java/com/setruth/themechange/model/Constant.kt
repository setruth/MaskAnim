package com.setruth.themechange.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey

val IS_DARK_MODEL = booleanPreferencesKey("is_dark_model")
val DARK_SWITCH_ACTIVE = booleanPreferencesKey("dark_switch_active")
val THEME_SWITCH_ACTIVE = booleanPreferencesKey("theme_switch_active")
val MASK_CLICK_X = floatPreferencesKey("mask_click_x")
val MASK_CLICK_Y = floatPreferencesKey("mask_click_y")

enum class MaskAnimModel {
    EXPEND,
    SHRINK,
}