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

/**
 * Pure geometry decisions for the brightness gesture. Kept free of Android
 * dependencies so they can be unit-tested on the JVM (see GestureMathTest).
 */
final class GestureMath {

    private GestureMath() {}

    // Reserve the dimmest fifth for the native Extra Dim color transform.
    private static final float EXTRA_DIM_END = 0.2f;

    static float normalBrightnessPosition(float fraction) {
        return Math.max(0f, Math.min(1f,
                (fraction - EXTRA_DIM_END) / (1f - EXTRA_DIM_END)));
    }

    static int extraDimStrength(float fraction) {
        return Math.round(Math.max(0f, Math.min(1f,
                (EXTRA_DIM_END - fraction) / EXTRA_DIM_END)) * 100f);
    }

    /**
     * True when an absolute movement (dx, dy) from the DOWN point is horizontal-
     * dominant and past the given slop. This is the single rule shared by the
     * brightness gesture's activation, its fullscreen early-claim pilfer, and the
     * system-side swipe-from-top blind — so all three agree on what counts as a
     * horizontal drag.
     *
     * @param dx    absolute horizontal travel
     * @param dy    absolute vertical travel
     * @param slop  minimum travel before a drag is recognized
     * @param ratio horizontal-dominance ratio (dx must exceed dy × ratio)
     */
    static boolean isHorizontalDrag(float dx, float dy, float slop, float ratio) {
        return dx > slop && dx > dy * ratio;
    }

    /**
     * True when an absolute movement is clearly vertical and past the given slop.
     * Used to release a deliberate vertical swipe (shade pull / status-bar reveal)
     * from the brightness gesture instead of mistaking it for a drag.
     *
     * @param dy   absolute vertical travel
     * @param dx   absolute horizontal travel
     * @param slop minimum travel before a drag is recognized
     */
    static boolean isVerticalDrag(float dy, float dx, float slop) {
        return dy > slop && dy > dx;
    }
}
