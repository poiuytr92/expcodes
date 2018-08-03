///////////////////////////////////////////////////////////////////////////////
///
/// Copyright (c) 2017 - <company name here>
///
/// Original filename: Driver2008.h
/// Project          : Driver2008
/// Date of creation : <see Driver2008.cpp>
/// Author(s)        : <see Driver2008.cpp>
///
/// Purpose          : <see Driver2008.cpp>
///
/// Revisions:         <see Driver2008.cpp>
///
///////////////////////////////////////////////////////////////////////////////

// $Id$

#ifndef __DRIVER2008_H_VERSION__
#define __DRIVER2008_H_VERSION__ 100

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif


#include "drvcommon.h"
#include "drvversion.h"

#define DEVICE_NAME			"\\Device\\DRIVER2008_DeviceName"
#define SYMLINK_NAME		"\\DosDevices\\DRIVER2008_DeviceName"
PRESET_UNICODE_STRING(usDeviceName, DEVICE_NAME);
PRESET_UNICODE_STRING(usSymlinkName, SYMLINK_NAME);

#ifndef FILE_DEVICE_DRIVER2008
#define FILE_DEVICE_DRIVER2008 0x8000
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

const ULONG IOCTL_DRIVER2008_OPERATION = CTL_CODE(FILE_DEVICE_DRIVER2008, 0x01, METHOD_BUFFERED, FILE_READ_DATA | FILE_WRITE_DATA);

#endif // __DRIVER2008_H_VERSION__
