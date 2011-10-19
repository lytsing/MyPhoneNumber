LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := MyPhoneNumber
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)
