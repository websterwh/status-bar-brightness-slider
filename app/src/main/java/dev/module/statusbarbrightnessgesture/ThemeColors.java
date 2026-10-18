/*
 * Brightness Slider — status bar brightness gesture (LSPosed module).
 * Copyright (C) 2026 ethanm6
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Based on StatusBarBrightnessGesture by mbatthew (MIT); see LICENSE-MIT.
 */
package dev.module.statusbarbrightnessgesture;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.util.TypedValue;

/**
 * The app's Material-You palette, resolved from the current theme. Consolidates
 * the theme-attribute resolution — including the ColorStateList fallback for
 * text-colour attrs — that the settings and appearance screens both need, so
 * their surfaces stay visually identical.
 */
final class ThemeColors {

    final int background;
    final int surfaceContainer;
    final int onSurface;
    final int onSurfaceVariant;
    final int primary;
    final int secondary;

    private ThemeColors(int background, int surfaceContainer, int onSurface,
                        int onSurfaceVariant, int primary, int secondary) {
        this.background = background;
        this.surfaceContainer = surfaceContainer;
        this.onSurface = onSurface;
        this.onSurfaceVariant = onSurfaceVariant;
        this.primary = primary;
        this.secondary = secondary;
    }

    /** Resolve the standard app palette from {@code context}'s current theme. */
    static ThemeColors resolve(Context context) {
        boolean night = isNight(context);
        return new ThemeColors(
                resolveAttr(context, android.R.attr.colorBackground,
                        night ? 0xFF141218 : 0xFFFEF7FF),
                resolveAttr(context, com.google.android.material.R.attr.colorSurfaceContainerHigh,
                        night ? 0xFF2B2930 : 0xFFECE6F0),
                resolveAttr(context, android.R.attr.textColorPrimary,
                        night ? 0xFFE6E1E5 : 0xFF1D1B20),
                // System secondary text colour, so descriptions follow the system
                // palette; falls back to the M3 onSurfaceVariant role.
                resolveAttr(context, android.R.attr.textColorSecondary,
                        resolveAttr(context, com.google.android.material.R.attr.colorOnSurfaceVariant,
                                night ? 0xFFCAC4D0 : 0xFF49454F)),
                resolveAttr(context, android.R.attr.colorPrimary,
                        night ? 0xFFD0BCFF : 0xFF6650A4),
                resolveAttr(context, com.google.android.material.R.attr.colorSecondary,
                        night ? 0xFFCCC2DC : 0xFF625B71));
    }

    static boolean isNight(Context context) {
        return (context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Resolve a theme attribute to a colour int, falling back to a ColorStateList's
     * default colour for text-colour attrs (textColorPrimary/Secondary resolve to
     * state lists, not raw colour ints) and finally to {@code fallback}.
     */
    static int resolveAttr(Context context, int attr, int fallback) {
        TypedValue tv = new TypedValue();
        if (!context.getTheme().resolveAttribute(attr, tv, true)) return fallback;
        if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return tv.data;
        }
        if (tv.resourceId != 0) {
            try {
                ColorStateList csl = context.getColorStateList(tv.resourceId);
                if (csl != null) return csl.getDefaultColor();
            } catch (Throwable ignored) {}
        }
        return fallback;
    }
}
