# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)
project("audio")
# Declares and names the project.
include_directories(ffmpeg/include)
include_directories(person)
include_directories(ffmpeg/fftools)
include_directories(algorithm)
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#add_library( # Sets the name of the library.
#        VOICE_3D
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/voice_3d/VOICE_3D.c )
#
#add_library( # Sets the name of the library.
#        VS_3D
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/source_voice_3d/VS_3D.c )
#
#add_library( # Sets the name of the library.
#        SOURCE_3D
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/source_3d/SOURCE_3D.c )
#
#add_library( # Sets the name of the library.
#        ANC
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/anc/ANC.c )
#
#
#add_library( # Sets the name of the library.
#        DifferentialSurround
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/dif_sur/DifferentialSurround.c )
#
#
#add_library( # Sets the name of the library.
#        DifferentialSurroundEquilibrium
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/dif_sur_eq/DifferentialSurroundEquilibrium.c )
#
#
#add_library( # Sets the name of the library.
#        DIY_3D
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/diy_3d/DIY_3D.c )
#
#add_library( # Sets the name of the library.
#        EQ0
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/eq/EQ0.c )
#
#add_library( # Sets the name of the library.
#        limiter0
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/limiter/limiter0.c)
#
#add_library( # Sets the name of the library.
#        lowcut
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/lowcut/lowcut.c )
#
#add_library( # Sets the name of the library.
#        noisegate
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/noisegate/gate.c )
#
#add_library( # Sets the name of the library.
#        reverb
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/reverb/reverb.c )
#
#add_library( # Sets the name of the library.
#        voiceenhance
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        algorithm/voiceenhance/Voice_enhancement.c )
#
#add_library( # Sets the name of the library.
#        algorithm
#
#        # Sets the library as a shared library.
#        SHARED
#
#        # Provides a relative path to your source file(s).
#        #        algorithm/lowcut/lowcut.c
#        #        algorithm/limiter/limiter0.c
#        #        algorithm/eq/EQ0.c
#        #        algorithm/diy_3d/DIY_3D.c
#        #        algorithm/dif_sur_eq/DifferentialSurroundEquilibrium.c
#        #        algorithm/dif_sur/DifferentialSurround.c
#        #        algorithm/anc/ANC.c
#        #        algorithm/source_3d/SOURCE_3D.c
#        #        algorithm/voice_3d/VOICE_3D.c
#        #        algorithm/noisegate/gate.c
#        #        algorithm/reverb/reverb.c
#        #        algorithm/voiceenhance/Voice_enhancement.c
#        algorithm/algorithm.c
#        )

add_library( # Sets the name of the library.
        native-audio-player

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
        native-audio-player.c)

add_library( # Sets the name of the library.
        filter-util

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        filter-util.c)

add_library( # Sets the name of the library.
        native-audio-filter

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-audio-filter.c )

add_library( # Sets the name of the library.
        native-audio-decoder

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-audio-decoder.c )

add_library( # Sets the name of the library.
        opensl-player

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        opensl-player.c)

add_library( # Sets the name of the library.
        native-audio-encoder

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-audio-encoder.c )

add_library( # Sets the name of the library.
        opensl-recorder

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        opensl-recorder.c)


add_library( # Sets the name of the library.
        audio-cache-pool

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        audio-cache-pool.c)

add_library( # Sets the name of the library.
        ffmpeg-cmd

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ffmpeg/fftools/cmdutils.c
        ffmpeg/fftools/ffmpeg.c
        ffmpeg/fftools/ffmpeg_opt.c
        ffmpeg/fftools/ffmpeg_filter.c
        ffmpeg/fftools/ffmpeg_hw.c
        ffmpeg-cmd.c
        )

target_link_libraries( # Specidefies the target library.
        ffmpeg-cmd
        # Links the target library to the log library
        # included in the NDK.
        fdk-aac
        mp3lame
        avdevice
        postproc
        avfilter
        avformat
        avcodec
        avutil
        swresample
        swscale
        )

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

add_library( avdevice
        SHARED
        IMPORTED)

set_target_properties( avdevice
        PROPERTIES IMPORTED_LOCATION
        ${PROJECT_SOURCE_DIR}/ffmpeg/${ANDROID_ABI}/libavdevice.so)

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

target_link_libraries( # Specifies the target library.
        opensl-recorder
        # Links the target library to the log library
        # included in the NDK.
        OpenSLES
        ${log-lib} )

target_link_libraries(opensl-player
        OpenSLES
        ${log-lib}
        audio-cache-pool

        )

target_link_libraries(audio-cache-pool
        ${log-lib}
        )

#target_link_libraries(EQ0
#        ${log-lib}
#        )

target_link_libraries( # Specifies the target library.
        native-audio-player
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
        opensl-player
        native-audio-decoder
        audio-cache-pool
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

target_link_libraries( # Specidefies the target library.
        native-audio-decoder
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
        audio-cache-pool
        native-audio-filter
#        algorithm
        )

target_link_libraries( # Specidefies the target library.
        ffmpeg-cmd
        # Links the target library to the log library
        # included in the NDK.
        fdk-aac
        mp3lame
        avdevice
        postproc
        avfilter
        avformat
        avcodec
        avutil
        swresample
        swscale
        ${log-lib}
        )
#target_link_libraries( # Specifies the target library.
#        algorithm
#
#        # Links the target library to the log library
#        # included ANC_in the NDK.
#        VOICE_3D
#        SOURCE_3D
#        VS_3D
#        ANC
#        DifferentialSurround
#        DifferentialSurroundEquilibrium
#        DIY_3D
#        EQ0
#        limiter0
#        lowcut
#        reverb
#        noisegate
#        voiceenhance)