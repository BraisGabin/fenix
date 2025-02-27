/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.wallpapers

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import mozilla.components.support.ktx.android.content.getColorFromAttr
import org.mozilla.fenix.ext.asActivity
import org.mozilla.fenix.utils.Settings

/**
 * Provides access to available wallpapers and manages their states.
 */
class WallpaperManager(private val settings: Settings) {

    var currentWallpaper: Wallpaper = getCurrentWallpaperFromSettings()
        set(value) {
            settings.currentWallpaper = value.name
            field = value
        }

    /**
     * Apply the [newWallpaper] into the [wallpaperContainer] and update the [currentWallpaper].
     */
    fun updateWallpaper(wallpaperContainer: View, newWallpaper: Wallpaper) {
        if (newWallpaper == Wallpaper.NONE) {
            val context = wallpaperContainer.context
            wallpaperContainer.setBackgroundColor(context.getColorFromAttr(newWallpaper.drawable))
        } else {
            wallpaperContainer.setBackgroundResource(newWallpaper.drawable)
        }
        currentWallpaper = newWallpaper

        adjustTheme(wallpaperContainer.context)
    }

    private fun adjustTheme(context: Context) {
        val mode = if (currentWallpaper != Wallpaper.NONE) {
            if (currentWallpaper.isDark) {
                updateThemePreference(useDarkTheme = true)
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                updateThemePreference(useLightTheme = true)
                AppCompatDelegate.MODE_NIGHT_NO
            }
        } else {
            updateThemePreference(followDeviceTheme = true)
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
            context.asActivity()?.recreate()
        }
    }

    private fun updateThemePreference(
        useDarkTheme: Boolean = false,
        useLightTheme: Boolean = false,
        followDeviceTheme: Boolean = false
    ) {
        settings.shouldUseDarkTheme = useDarkTheme
        settings.shouldUseLightTheme = useLightTheme
        settings.shouldFollowDeviceTheme = followDeviceTheme
    }

    /**
     * Returns the next available [Wallpaper], the [currentWallpaper] is the last one then
     * the first available [Wallpaper] will be returned.
     */
    fun switchToNextWallpaper(): Wallpaper {
        val values = Wallpaper.values()
        val index = values.indexOf(currentWallpaper) + 1

        return if (index >= values.size) {
            values.first()
        } else {
            values[index]
        }
    }

    private fun getCurrentWallpaperFromSettings(): Wallpaper {
        val currentWallpaper = settings.currentWallpaper
        return if (currentWallpaper.isEmpty()) {
            Wallpaper.NONE
        } else {
            Wallpaper.valueOf(currentWallpaper)
        }
    }
}
