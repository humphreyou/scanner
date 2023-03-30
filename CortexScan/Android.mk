LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under,src) \
    $(call all-java-files-under,../BarcodeApi)

LOCAL_CERTIFICATE := platform
LOCAL_PACKAGE_NAME := DummyScanDriver

include $(BUILD_PACKAGE)
