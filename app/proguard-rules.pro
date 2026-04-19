# Keep line numbers for crash stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Kotlinx Serialization ---
# @Serializable annotated classes need their companion + serializer preserved.
-keepattributes InnerClasses,Signature
-keepclassmembers,allowobfuscation class **$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
    static kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.muhammetkocak.turkcekelimeapp.**$$serializer { *; }
-keepclassmembers class com.muhammetkocak.turkcekelimeapp.** {
    *** Companion;
}
-keepclasseswithmembers class com.muhammetkocak.turkcekelimeapp.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Room ---
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep class com.muhammetkocak.turkcekelimeapp.data.local.entity.** { *; }
-keep class com.muhammetkocak.turkcekelimeapp.data.local.dao.** { *; }
-keep class com.muhammetkocak.turkcekelimeapp.data.local.AppDatabase_Impl { *; }

# --- Hilt / Dagger ---
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ApplicationComponentManager { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keepclassmembers @dagger.hilt.android.HiltAndroidApp class * { *; }

# --- Compose Navigation type-safe routes (rely on Serializable Screen sealed interface) ---
-keep class com.muhammetkocak.turkcekelimeapp.navigation.Screen { *; }
-keep class com.muhammetkocak.turkcekelimeapp.navigation.Screen$* { *; }
