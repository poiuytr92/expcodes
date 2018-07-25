/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef _JAVASOFT_JNI_MD_H_
#define _JAVASOFT_JNI_MD_H_

	#ifdef _WIN32
		#define JNIEXPORT __declspec(dllexport)
		#define JNIIMPORT __declspec(dllimport)
		#define JNICALL __stdcall
	#else
		#define JNIEXPORT
		#define JNIIMPORT
		#define JNICALL
	#endif

	typedef long jint;
	typedef signed char jbyte;

	#ifdef _LP64 /* 64-bit Solaris */
		typedef long jlong;
	#else
		typedef long long jlong;
	#endif

#endif /* !_JAVASOFT_JNI_MD_H_ */
