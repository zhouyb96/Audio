# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.
include_directories(ffmpeg/include)
include_directories(person)

project("audio")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        native-lib.c)

add_library( # Sets the name of the library.
        native-util

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-util.cpp )

add_library( # Sets the name of the library.
        native-cutil

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-cutil.c )


add_library( # Sets the name of the library.
        native-audio-filter

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-audio-filter.c )

add_library( # Sets the name of the library.
        native-player

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-player.c )

add_library( # Sets the name of the library.
        native-audio-encoder

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-audio-encoder.c )

add_library( # Sets the name of the library.
        native-recorder

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-recorder.c)

add_library(fdk-aac
        SHARED
        IMPORTED)

set_target_properties(fdk-aac
        PROPERTIES IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/libfdk-aac.so)

add_library(mp3lame
        SHARED
        IMPORTED)

set_target_properties(mp3lame
        PROPERTIES IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/libmp3lame.so)


add_library(avcodec
        SHARED
        IMPORTED)

set_target_properties(avcodec
        PROPERTIES IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/libavcodec.so)

add_library(avformat
        SHARED
        IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/libavformat.so)

add_library( avutil
        SHARED
        IMPORTED)

set_target_properties( avutil
        PROPERTIES IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/libavutil.so)

#target_link_libraries( avutil
#
#        ${log-lib})

add_library(
        swresample
        SHARED
        IMPORTED)

set_target_properties(
        swresample
        PROPERTIES IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/libswresample.so)

add_library( postproc
        SHARED
        IMPORTED)

set_target_properties( postproc
        PROPERTIES IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/libpostproc.so)

add_library( swscale
        SHARED
        IMPORTED)

set_target_properties( swscale
        PROPERTIES IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/libswscale.so)

add_library( avfilter
        SHARED
        IMPORTED)

set_target_properties( avfilter
        PROPERTIES IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/libavfilter.so)


# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
target_link_libraries( # Specifies the target library.
        native-util
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )

target_link_libraries(native-recorder
        OpenSLES)
target_link_libraries( # Specifies the target library.
        native-recorder
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )
target_link_libraries(native-player
        OpenSLES)

target_link_libraries(native-player
        ${log-lib})
target_link_libraries( # Specifies the target library.
        native-lib
        # Links the target library to the log library
        # included in the NDK.
        avcodec
        avutil
        avformat
        swresample
        postproc
        swscale
        avfilter
        ${log-lib}
        native-player
        native-audio-filter
        native-audio-encoder
        )

target_link_libraries( # Specifies the target library.
        native-audio-filter
        # Links the target library to the log library
        # included in the NDK.
        avcodec
        avutil
        avformat
        swresample
        postproc
        swscale
        avfilter
        ${log-lib}
        )

target_link_libraries( # Specifies the target library.
        native-audio-encoder
        # Links the target library to the log library
        # included in the NDK.
        fdk-aac
        mp3lame
        avcodec
        avutil
        avformat
        swresample
        postproc
        swscale
        avfilter
        ${log-lib}
        )