package dev.module.statusbarbrightnessgesture;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class GestureMathTest {

    @Test
    public void detent_ignoresReleaseJitterAndContinuedDimming() {
        GestureMath.RelativeSlider slider = new GestureMath.RelativeSlider();
        slider.begin(0.5f, true);
        assertTrue(slider.move(-0.4f));
        assertFalse(slider.move(0.002f));
        assertFalse(slider.move(0.003f));
        assertEquals(0.2f, slider.position(), 0f);
        assertFalse(slider.move(-0.1f));
        assertEquals(0.2f, slider.position(), 0f);
        assertFalse(slider.move(0.02f));
        assertEquals(0.22f, slider.position(), 0.00001f);
    }

    @Test
    public void detent_autoBrightnessCannotRearmBarrierBeforeSecondSwipe() {
        GestureMath.RelativeSlider slider = new GestureMath.RelativeSlider();
        slider.begin(0.5f, true);
        assertTrue(slider.move(-0.4f));
        assertFalse(slider.move(0.001f));
        // The ambient-light sensor reports a brighter level before the next swipe.
        slider.begin(slider.startingPosition(0.6f, true), true);
        assertFalse(slider.move(-0.1f));
        assertEquals(50, GestureMath.extraDimStrength(slider.position()));
    }

    @Test
    public void detent_manualExternalChangesAndDeliberateBrighteningRemainRespected() {
        GestureMath.RelativeSlider slider = new GestureMath.RelativeSlider();
        slider.begin(0.5f, true);
        assertTrue(slider.move(-0.4f));
        assertEquals(0.7f, slider.startingPosition(0.7f, false), 0f);
        assertFalse(slider.move(0.03f));
        assertEquals(0.7f, slider.startingPosition(0.7f, true), 0f);
    }

    @Test
    public void absoluteDetent_repeatedJitterTargetsCannotReleaseStop() {
        GestureMath.RelativeSlider slider = new GestureMath.RelativeSlider();
        slider.begin(0.6f, true);
        assertTrue(slider.moveTo(0.1f));
        for (int i = 0; i < 100; i++) {
            assertFalse(slider.moveTo(0.205f));
        }
        assertEquals(0.2f, slider.position(), 0f);
        assertFalse(slider.moveTo(0.23f));
        assertEquals(0.23f, slider.position(), 0.00001f);
    }

    @Test
    public void absoluteControl_sameTouchPositionIgnoresStartingBrightness() {
        GestureMath.RelativeSlider first = new GestureMath.RelativeSlider();
        GestureMath.RelativeSlider second = new GestureMath.RelativeSlider();
        first.begin(0.4f, true);
        second.begin(0.9f, true);
        assertFalse(first.moveTo(0.7f));
        assertFalse(second.moveTo(0.7f));
        assertEquals(0.7f, first.position(), 0.00001f);
        assertEquals(first.position(), second.position(), 0.00001f);
    }

    @Test
    public void absoluteControl_retainsDetentAndAllowsNextSwipeBelowIt() {
        GestureMath.RelativeSlider slider = new GestureMath.RelativeSlider();
        slider.begin(0.7f, true);
        assertTrue(slider.moveTo(0.1f));
        assertEquals(0.2f, slider.position(), 0f);
        assertFalse(slider.moveTo(0f));
        assertEquals(0.2f, slider.position(), 0f);
        slider.begin(slider.position(), true);
        assertFalse(slider.moveTo(0.1f));
        assertEquals(50, GestureMath.extraDimStrength(slider.position()));
    }

    @Test
    public void relativeMovement_isIndependentOfStartingLocation() {
        GestureMath.RelativeSlider left = new GestureMath.RelativeSlider();
        GestureMath.RelativeSlider right = new GestureMath.RelativeSlider();
        left.begin(0.5f, true);
        right.begin(0.5f, true);
        assertFalse(left.move(GestureMath.relativeDelta(200f, 100f, 1000, false)));
        assertFalse(right.move(GestureMath.relativeDelta(900f, 800f, 1000, false)));
        assertEquals(0.6f, left.position(), 0.00001f);
        assertEquals(left.position(), right.position(), 0f);
        assertFalse(left.move(0f)); // duplicate root-view event / vertical jitter
        assertEquals(0.6f, left.position(), 0.00001f);
    }

    @Test
    public void relativeMovement_reverseFlipsDirection() {
        assertEquals(-0.1f, GestureMath.relativeDelta(200f, 100f, 1000, true), 0.00001f);
        assertEquals(0.1f, GestureMath.relativeDelta(100f, 200f, 1000, true), 0.00001f);
    }

    @Test
    public void detent_blocksOvershootAndSignalsOnlyOncePerSwipe() {
        GestureMath.RelativeSlider slider = new GestureMath.RelativeSlider();
        slider.begin(0.6f, true);
        assertTrue(slider.move(-0.8f));
        assertEquals(0.2f, slider.position(), 0f);
        assertEquals(0, GestureMath.extraDimStrength(slider.position()));
        assertFalse(slider.move(-0.5f));
        assertEquals(0.2f, slider.position(), 0f);
        assertFalse(slider.move(0.1f)); // no stored overshoot to work off
        assertEquals(0.3f, slider.position(), 0.00001f);
        assertFalse(slider.move(-0.2f));
        assertEquals(0.2f, slider.position(), 0f);
    }

    @Test
    public void detent_secondSwipeAtMinimumEntersExtraDim() {
        GestureMath.RelativeSlider slider = new GestureMath.RelativeSlider();
        slider.begin(0.6f, true);
        assertTrue(slider.move(-0.5f));
        slider.begin(slider.position(), true); // lift, then start a fresh swipe
        assertFalse(slider.move(-0.1f));
        assertEquals(50, GestureMath.extraDimStrength(slider.position()));
        assertFalse(slider.move(-0.1f));
        assertEquals(100, GestureMath.extraDimStrength(slider.position()));
    }

    @Test
    public void relativeMovement_existingExtraDimCanExitToNormal() {
        GestureMath.RelativeSlider slider = new GestureMath.RelativeSlider();
        slider.begin(GestureMath.extendedPosition(0f, 50), true);
        assertEquals(0.1f, slider.position(), 0.00001f);
        assertFalse(slider.move(0.2f));
        assertEquals(0, GestureMath.extraDimStrength(slider.position()));
        assertTrue(GestureMath.normalBrightnessPosition(slider.position()) > 0f);
    }

    @Test
    public void relativeMovement_clampsEndpointsAndReversesImmediately() {
        GestureMath.RelativeSlider slider = new GestureMath.RelativeSlider();
        slider.begin(0.2f, true);
        assertFalse(slider.move(-2f));
        assertEquals(0f, slider.position(), 0f);
        assertFalse(slider.move(0.1f));
        assertEquals(0.1f, slider.position(), 0.00001f);
        assertFalse(slider.move(2f));
        assertEquals(1f, slider.position(), 0f);
        assertFalse(slider.move(-0.1f));
        assertEquals(0.9f, slider.position(), 0.00001f);
    }

    @Test
    public void relativeMovement_withoutExtraDimHasNoBarrier() {
        GestureMath.RelativeSlider slider = new GestureMath.RelativeSlider();
        slider.begin(0.6f, false);
        assertFalse(slider.move(-0.8f));
        assertEquals(0f, slider.position(), 0f);
        assertFalse(slider.move(0.4f));
        assertEquals(0.4f, slider.position(), 0.00001f);
    }

    @Test
    public void newSwipe_reloadsExternalChangesAndRearmsDetent() {
        GestureMath.RelativeSlider slider = new GestureMath.RelativeSlider();
        slider.begin(0.6f, true);
        assertTrue(slider.move(-1f));
        slider.begin(GestureMath.extendedPosition(0.75f, 0), true);
        assertEquals(0.8f, slider.position(), 0.00001f);
        assertTrue(slider.move(-1f));
        assertEquals(0.2f, slider.position(), 0f);
    }

    @Test
    public void extraDimRange_holdsHardwareAtMinimum() {
        assertEquals(100, GestureMath.extraDimStrength(0f));
        assertEquals(50, GestureMath.extraDimStrength(0.1f));
        assertEquals(0f, GestureMath.normalBrightnessPosition(0.1f), 0.00001f);
    }

    @Test
    public void extraDimBoundary_transitionsToNormalBrightness() {
        assertEquals(0, GestureMath.extraDimStrength(0.2f));
        assertEquals(0f, GestureMath.normalBrightnessPosition(0.2f), 0.00001f);
        assertEquals(0, GestureMath.extraDimStrength(0.6f));
        assertEquals(0.5f, GestureMath.normalBrightnessPosition(0.6f), 0.00001f);
        assertEquals(1f, GestureMath.normalBrightnessPosition(1f), 0.00001f);
    }

    @Test
    public void extendedRange_clampsEndpointsAndIsMonotonic() {
        assertEquals(100, GestureMath.extraDimStrength(-1f));
        assertEquals(0, GestureMath.extraDimStrength(2f));
        assertEquals(0f, GestureMath.normalBrightnessPosition(-1f), 0f);
        assertEquals(1f, GestureMath.normalBrightnessPosition(2f), 0f);
        for (int i = 1; i <= 1000; i++) {
            float previous = (i - 1) / 1000f;
            float current = i / 1000f;
            assertTrue(GestureMath.extraDimStrength(current) <= GestureMath.extraDimStrength(previous));
            assertTrue(GestureMath.normalBrightnessPosition(current)
                    >= GestureMath.normalBrightnessPosition(previous));
        }
    }

    // ── isHorizontalDrag ──────────────────────────────────────────────────────

    @Test
    public void horizontalDrag_trueWhenPastSlopAndDominant() {
        assertTrue(GestureMath.isHorizontalDrag(60f, 10f, 48f, 2f));
    }

    @Test
    public void horizontalDrag_falseWhenWithinSlop() {
        assertFalse(GestureMath.isHorizontalDrag(48f, 0f, 48f, 2f));
        assertFalse(GestureMath.isHorizontalDrag(47f, 0f, 48f, 2f));
    }

    @Test
    public void horizontalDrag_falseWhenNotDominant() {
        assertFalse(GestureMath.isHorizontalDrag(60f, 40f, 48f, 2f)); // 60 <= 40*2
    }

    @Test
    public void horizontalDrag_strictlyGreaterThanRatio() {
        // dx == dy*ratio exactly → not a claim; one unit more → claim.
        assertFalse(GestureMath.isHorizontalDrag(60f, 30f, 48f, 2f));
        assertTrue(GestureMath.isHorizontalDrag(61f, 30f, 48f, 2f));
    }

    @Test
    public void horizontalDrag_respectsHigherRatio() {
        // Same movement, stricter ratio flips it to not-dominant.
        assertTrue(GestureMath.isHorizontalDrag(60f, 20f, 48f, 2f));
        assertFalse(GestureMath.isHorizontalDrag(60f, 20f, 48f, 3f));
    }

    @Test
    public void horizontalDrag_zeroMovement_isFalse() {
        assertFalse(GestureMath.isHorizontalDrag(0f, 0f, 48f, 2f));
    }

    // ── isVerticalDrag ────────────────────────────────────────────────────────

    @Test
    public void verticalDrag_trueWhenPastSlopAndVertical() {
        assertTrue(GestureMath.isVerticalDrag(60f, 10f, 48f));
    }

    @Test
    public void verticalDrag_falseWhenWithinSlop() {
        assertFalse(GestureMath.isVerticalDrag(48f, 0f, 48f));
        assertFalse(GestureMath.isVerticalDrag(47f, 0f, 48f));
    }

    @Test
    public void verticalDrag_falseWhenNotVertical() {
        assertFalse(GestureMath.isVerticalDrag(40f, 60f, 48f)); // horizontal dominant
    }

    @Test
    public void verticalDrag_strictlyGreaterThanHorizontal() {
        // dy == dx → not vertical; one unit more → vertical.
        assertFalse(GestureMath.isVerticalDrag(60f, 60f, 48f));
        assertTrue(GestureMath.isVerticalDrag(61f, 60f, 48f));
    }

    @Test
    public void verticalDrag_zeroMovement_isFalse() {
        assertFalse(GestureMath.isVerticalDrag(0f, 0f, 48f));
    }
}
