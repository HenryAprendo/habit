# 🛡️ Faith Steward - Production ProGuard Rules

# [Hilt / Dagger]
# Evita que R8 elimine las clases generadas por Hilt para la inyección de dependencias.
-keep class * extends android.app.Application
-keep class * extends android.app.Activity
-keep class * extends androidx.work.ListenableWorker

# [Room]
# Mantiene las clases de persistencia para que Room pueda instanciar los DAO.
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}

# [Google Play Billing]
# REGLA CRÍTICA: Evita que se rompa la comunicación con la tienda de Google.
-keep class com.android.vending.billing.** { *; }
-keep class com.android.billingclient.** { *; }

# [Kotlin Serialization / JSON]
# Si usas clases de datos para JSON, evita que se renombren sus atributos.
-keepattributes *Annotation*, EnclosingMethod, InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable