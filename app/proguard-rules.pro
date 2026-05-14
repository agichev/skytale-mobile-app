-keep class kotlinx.serialization.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-dontwarn org.conscrypt.**

