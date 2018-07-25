/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#ifndef _JAVASOFT_JNI_MD_H_
#define _JAVASOFT_JNI_MD_H_

	// linux不需要也不存在这种方式去控制哪些函数可以导出, 这仅是win平台dll特有的。
	// linux平台默认所有函数都是public的，除非在编译时增加-fvisibility=hidden参数，
	// 如：gcc -shared -o test.so -fvisibility=hidden test.c 
	// 此时仅声明了 __attribute__ ((visibility("default"))) 的函数才是可以导出的。
	// 因此只要编译时控制gcc参数，linux也就不需要 dllexport 了
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
