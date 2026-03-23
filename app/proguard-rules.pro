# Keep ExoPlayer and Media3 components
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Keep Leanback UI components
-keep class androidx.leanback.** { *; }
-dontwarn androidx.leanback.**

# Keep WebView Client overrides
-keepclassmembers class * extends android.webkit.WebViewClient { public *; }
-keepclassmembers class * extends android.webkit.WebChromeClient { public *; }

# Keep Glide and Gson
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.secure.firetv.Movie { *; } # Keep our data model

-optimizationpasses 5
-allowaccessmodification
-dontobfuscate
