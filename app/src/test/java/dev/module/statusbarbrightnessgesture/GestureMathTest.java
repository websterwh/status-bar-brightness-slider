package dev.module.statusbarbrightnessgesture;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class GestureMathTest {

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
