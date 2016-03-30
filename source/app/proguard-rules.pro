# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in d:\tools\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

#other
-keep class org.apache.** { *; }
-keep class com.google.** { *; }
-keep class com.squareup.** { *; }
-keep class com.qb.** { *; }
-keep class android.** { *; }
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**

-keep class android.net.http.** { *; }
-dontwarn android.net.http.**

-keep class okio.** { *; }
-keep class java.nio.** { *; }

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

#PARSE
-keepattributes *Annotation*
-keep class com.parse.** { *; }
-keep class bolts.** { *; }
-dontwarn android.net.SSLCertificateSocketFactory
-dontwarn android.app.Notification

-keep class com.zapporoo.safeingredientsscanner.model.** { *; }

#QuickBlox
-keepattributes Signature
-keep class org.jivesoftware.** { *; }
-keep class org.jivesoftware.** { public *; }
-keep class com.quickblox.** { public *; }
-keep class * extends org.jivesoftware.smack { public *; }
-keep class * implements org.jivesoftware.smack.debugger.SmackDebugger { public *; }

-keep class com.quickblox.chat.**
-keep class com.quickblox.chat.** { *; }
-keepnames class com.quickblox.chat.**
-keepnames class com.quickblox.chat.** { *; }
-keepclassmembers class com.quickblox.chat.** {*;}
-keepclassmembers enum com.quickblox.chat.** {*;}
-keepclassmembers interface com.quickblox.chat.** {*;}


#other
-keep class javax.** { *; }
-keep class autovalue.** { *; }
-keep class java.** { *; }
-dontwarn java.lang.invoke**

-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault,*Annotation*

-dontwarn sun.misc.Unsafe

-keep class org.apache.** { *; }
-keep class com.google.** { *; }
-keep class com.twitter.** { *; }
-keep class com.facebook.** { *; }
-keep class com.squareup.** { *; }
-keep class com.qb.** { *; }
-keep class android.** { *; }
-keep class org.apache.** { *; }
-dontwarn org.apache.http.**