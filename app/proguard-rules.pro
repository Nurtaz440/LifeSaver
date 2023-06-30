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
#-dontwarn com.example.ambulance.**
#-keep class com.example.ambulance.MainActivity
#-keep class com.example.ambulance.adapter.MyListAdapter
#-keep class com.example.ambulance.app.ApplicationApp
#-keep class com.example.ambulance.database.AppDatabase
#-keep class com.example.ambulance.database.UserDao
#-keep class com.example.ambulance.model.LocationModel
#-keep class com.example.ambulance.model.UserDetails
#-keep class com.example.ambulance.repository.AuthRepository
#-keep class com.example.ambulance.repository.UserRepository
#-keep class com.example.ambulance.util.SharedPreferencesManager
#-keep class com.example.ambulance.R
#-keep class com.example.ambulance.Manifest
#-keep class com.example.ambulance.ui.registration.sign_in.SignInFragment
#-keep class com.example.ambulance.ui.registration.sign_up.RegistrationViewModel
#-keep class com.example.ambulance.ui.registration.sign_up.SignUpFragment
#-keep class com.example.ambulance.ui.registration.viewModel.AuthViewModel
#-keep class com.example.ambulance.ui.admin.HomeAdminFragment
#-keep class com.example.ambulance.ui.client.ClientHomeFragment
#-keep class com.example.ambulance.ui.client.ProfileViewModel
#-keep class com.example.ambulance.ui.client.ui.AboutFragment
#-keep class com.example.ambulance.ui.client.ui.ComplainFragment
#-keep class com.example.ambulance.ui.client.ui.HistoryClientFragment
#-keep class com.example.ambulance.ui.client.ui.HomeMapFragment
#-keep class com.example.ambulance.ui.client.ui.LocationsFragment
#-keep class com.example.ambulance.ui.client.ui.SettingsFragment
