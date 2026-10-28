# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- SpeakHub release optimization rules (R8) ---

# Keep line numbers so Play Console crash stacks stay readable with the
# mapping file that ships inside the app bundle.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Gson parses the API DTOs by FIELD NAME (the ADV DTOs have no
# @SerializedName at all), so DTO classes and their members must never be
# renamed or removed. Without this the first sync returns empty/broken
# data on release builds only.
-keep class org.callapp.regularenglish.data.remote.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses,EnclosingMethod

# Room looks up the generated database implementation by name at runtime.
-keep class org.callapp.regularenglish.data.local.**_Impl { *; }

# WorkManager (pulled in transitively by the ads SDK) has Room instantiate
# its internal database BY NAME at app startup via androidx.startup.
# Renaming WorkDatabase_Impl crashes the app on first launch with
# NoSuchMethodException: androidx.work.impl.WorkDatabase_Impl.<init>
-keep class androidx.work.impl.WorkDatabase_Impl { *; }

# Safety net: any library Room database implementation is also created
# by name, so keep every *_Impl class name and its constructor.
-keep class **_Impl { <init>(); }
