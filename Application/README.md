## Application

### Android Studio Initialisation (with Aruco and OpenCV)

1. Download and install **Android Studio**
2. Start a new project with **include C++ support**
3. Open Android Studio and go to the **SDK Manager** and install **CMake** and **NDK**
4. Download **OpenCvSDK**
5. Go to *File*->*New*->*Import Module* and select OpenCvSDK
6. Go to *File*->*Project Structure*, choose *app*, go to *Dependencies* and add the openCVLibrary
7. Add the JAR libraries to *app/libs* and import **commons-collections**, **commons-configuration**, **commons-lang** and **org-apache-commons-logging** (same way as step 6)
8. create folder *jniLibs* in *app/src/main* and add **armeabi**, **armeabi-v7a** etc from your OpenCvSDK folder
9. change *minSdkVersion*, *targetSdkVersion* and *compileSdkVersion* in *build.grade (OpenCV)* to the values in *build.grade (app)*
10. add  *ndk {abiFilters "armeabi-v7a"}* and *sourceSets { main { jni.srcDirs = ['src/main/jni', 'src/main/jniLibs/'] } }* to *build.grade (app)*