# ============================================================
# LSPosed module ProGuard/R8 rules
# ============================================================
#
# minifyEnabled + shrinkResources are enabled for release builds, so R8
# runs on the APK. All of the hook's reflective lookups target framework
# classes by string name (unaffected by R8); the rules below keep the
# module's own entry class and the compileOnly Xposed API references valid.

# The hook class is referenced by name in assets/xposed_init. LSPosed finds
# it via Class.forName() at runtime, so its name must never be obfuscated or
# removed. Keep the whole module package for the same reason (activities and
# the QS tile service are instantiated by the framework by name).
-keep class dev.module.statusbarbrightnessgesture.** { *; }

# Xposed API classes are compileOnly (not in the APK); keep the references
# intact so the DEX bytecode stays valid and suppress missing-class warnings.
-keep class de.robv.android.xposed.** { *; }
-dontwarn de.robv.android.xposed.**

# BrightnessInfo is accessed via Display.getBrightnessInfo() reflection — keep
# its fields (read by name in readBrightnessRange()).
-keep class android.hardware.display.BrightnessInfo { *; }

# Compile-only stubs shadowed by the boot classpath at runtime. R8 must NOT
# rename them: the cast and anonymous subclass in initGestureMonitor() run
# against the real framework classes, so an obfuscated stub name makes the
# checkcast fail with ClassCastException and kills the fullscreen gesture.
# (-keep also suppresses the missing-member warnings for these stubs.)
-keep class android.view.InputChannel { *; }
-keep class android.view.InputEventReceiver { *; }
-dontwarn android.view.InputChannel
-dontwarn android.view.InputEventReceiver
