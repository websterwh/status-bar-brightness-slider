package dev.module.statusbarbrightnessgesture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public final class PrefsTest {

    @Test
    public void isWritableKey_acceptsEveryWhitelistedKey() {
        for (String key : Prefs.WRITABLE_KEYS) {
            assertTrue("expected writable: " + key, Prefs.isWritableKey(key));
        }
    }

    @Test
    public void isWritableKey_rejectsSavedBrightness() {
        assertFalse(Prefs.isWritableKey(Prefs.KEY_SAVED_BRIGHTNESS));
    }

    @Test
    public void isWritableKey_rejectsNull() {
        assertFalse(Prefs.isWritableKey(null));
    }

    @Test
    public void isWritableKey_rejectsUnknownKey() {
        assertFalse(Prefs.isWritableKey("sbbrightness_test_marker"));
    }

    @Test
    public void writableKeys_hasExpectedSize() {
        assertEquals(22, Prefs.WRITABLE_KEYS.size());
    }

    @Test
    public void relativeControl_canBePersistedByTheApp() {
        assertTrue(Prefs.isWritableKey(Prefs.KEY_RELATIVE_CONTROL));
    }

    @Test
    public void writableKeys_excludesSavedBrightness() {
        assertFalse(Prefs.WRITABLE_KEYS.contains(Prefs.KEY_SAVED_BRIGHTNESS));
    }

    @Test
    public void writableKeys_isUnmodifiable() {
        try {
            Prefs.WRITABLE_KEYS.add("sbbrightness_sneaky_key");
            fail("expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            // expected
        }
    }
}
