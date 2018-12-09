///////////////////////////////////////////////////////////////////////////////
///
/// Copyright (c) 2017 - <company name here>
///
/// Original filename: VS2008_WDK_Demo.h
/// Project          : VS2008_WDK_Demo
/// Date of creation : <see VS2008_WDK_Demo.cpp>
/// Author(s)        : <see VS2008_WDK_Demo.cpp>
///
/// Purpose          : <see VS2008_WDK_Demo.cpp>
///
/// Revisions:         <see VS2008_WDK_Demo.cpp>
///
///////////////////////////////////////////////////////////////////////////////

// $Id$

#ifndef __VS2008WDK_DEMO_H_VERSION__
#define __VS2008WDK_DEMO_H_VERSION__ 100

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif


#include "drvcommon.h"
#include "drvversion.h"

#define DEVICE_NAME			"\\Device\\VS2008WDK_DEMO_DeviceName"
#define SYMLINK_NAME		"\\DosDevices\\VS2008WDK_DEMO_DeviceName"
PRESET_UNICODE_STRING(usDeviceName, DEVICE_NAME);
PRESET_UNICODE_STRING(usSymlinkName, SYMLINK_NAME);

#ifndef FILE_DEVICE_VS2008WDK_DEMO
#define FILE_DEVICE_VS2008WDK_DEMO 0x8000
#endif

// Values defined for "Method"
// METHOD_BUFFERED
// METHOD_IN_DIRECT
// METHOD_OUT_DIRECT
// METHOD_NEITHER
// 
// Values defined for "Access"
// FILE_ANY_ACCESS
// FILE_READ_ACCESS
// FILE_WRITE_ACCESS

const ULONG IOCTL_VS2008WDK_DEMO_OPERATION = CTL_CODE(FILE_DEVICE_VS2008WDK_DEMO, 0x01, METHOD_BUFFERED, FILE_READ_DATA | FILE_WRITE_DATA);

#endif // __VS2008WDK_DEMO_H_VERSION__
