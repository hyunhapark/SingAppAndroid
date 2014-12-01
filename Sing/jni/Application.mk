APP_STL := gnustl_static
APP_CPPFLAGS += -fexceptions -frtti
# Supported plattforms
APP_ABI := armeabi-v7a
#APP_CFLAGS += -O2
LOCAL_ARM_MODE  := arm
APP_PLATFORM := android-8


# Optimization mode "debug" or "release"
APP_OPTIM := debug