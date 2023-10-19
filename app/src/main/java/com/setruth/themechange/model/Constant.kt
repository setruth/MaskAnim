package com.setruth.themechange.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey

val ACTIVE_MASK_TAG=booleanPreferencesKey("active_mask_tag")
 val MASK_CLICK_X= floatPreferencesKey("mask_click_x")
 val MASK_CLICK_Y=floatPreferencesKey("mask_click_y")