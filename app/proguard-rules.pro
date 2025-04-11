
# Keep all public Android app components (Activities, Services, etc.)
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Application

# Keep Gson model classes and annotations
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*

# Keep classes for logging
-keep class android.util.Log { *; }

# Keep classes for Json
-keep class com.fasterxml.jackson.** { *; }

# Suppress warnings for missing Jackson classes
-dontwarn com.fasterxml.jackson.core.JsonFactory
-dontwarn com.fasterxml.jackson.core.JsonGenerator
-dontwarn com.fasterxml.jackson.core.JsonParser
-dontwarn com.fasterxml.jackson.core.JsonToken
