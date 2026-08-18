# 保留 Room DAO/Entity
-keep class com.mass.notiguard.data.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.** <methods>;
}

# 保留 ViewModel / 序列化无关
-keep class com.mass.notiguard.ui.** { *; }

# DataStore / Compose 常规保留
-dontwarn androidx.datastore.**
