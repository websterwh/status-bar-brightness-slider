package dev.module.statusbarbrightnessgesture;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class IndicatorDrawingTest {

    @Test
    public void dropletWidth_roundsTwiceRadius() {
        assertEquals(7, IndicatorDrawing.dropletWidth(3.5f));
    }

    @Test
    public void dropletHeight_roundsHeightFactorTimesRadius() {
        assertEquals(Math.round(2.4f * 5f), IndicatorDrawing.dropletHeight(5f));
    }

    @Test
    public void shapeTextCx_isHalfWidth() {
        assertEquals(10f, IndicatorDrawing.shapeTextCx(20f), 0f);
    }

    @Test
    public void dropletTextCy_isBulbCentre() {
        float w = 20f, h = 48f, pad = 2f;
        float expected = (h - pad) - (w - 2 * pad) / 2f;
        assertEquals(expected, IndicatorDrawing.dropletTextCy(w, h, pad), 0f);
    }

    @Test
    public void starWidth_roundsContentWidth() {
        assertEquals(Math.round(12.7f), IndicatorDrawing.starWidth(12.7f));
    }

    @Test
    public void starHeight_roundsHeightFactorTimesWidth() {
        assertEquals(Math.round(20f * IndicatorDrawing.STAR_HEIGHT_FACTOR),
                IndicatorDrawing.starHeight(20f));
    }

    @Test
    public void starTextCy_usesCenterYFactor() {
        float w = 20f, pad = 2f;
        float expected = pad + IndicatorDrawing.STAR_CENTER_Y * (w - 2 * pad);
        assertEquals(expected, IndicatorDrawing.starTextCy(w, pad), 0f);
    }

    @Test
    public void starHeightFactor_matchesReference() {
        assertEquals(0.956435f, IndicatorDrawing.STAR_HEIGHT_FACTOR, 0f);
    }
}
