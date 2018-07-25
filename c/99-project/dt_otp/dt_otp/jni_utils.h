/*****************************************
  Description: java-to-c类型转换工具包
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#ifndef __JNI_UTILS_H_
#define __JNI_UTILS_H_

	#include "jni.h"	

	namespace JNI_UTILS {

		/*
		 * [jlong] -> [long long]
		 * @param _long [jlong]类型值（java）
		 * @return [long long]类型值（c++）
		 */
		const long long jlong2LL(const jlong _long);

		/*
		 * [long long] -> [jlong]
		 * @param _long [long long]类型值（c++）
		 * @return [jlong]类型值（java）
		 */
		const jlong ll2Jlong(const long long _long);

		/*
		 * [jstring] -> [char*]
		 * @param env JNI环境参数指针
		 * @param str [jstring]类型值（java）
		 * @return [char*]类型值（c++）
		 */
		const char* jstring2Char(JNIEnv* env, const jstring str);

		/*
		 * [char*] -> [jstring]
		 * @param env JNI环境参数指针
		 * @param str [char*]类型值（c++）
		 * @return [jstring]类型值（java）
		 */
		const jstring char2Jstring(JNIEnv* env, const char* str);

		/*
		 * 释放由 jstring 生成的 char* 所指向的内存
		 * @param env JNI环境参数指针
		 * @param jStr [jstring]类型值（java）
		 * @param cStr [char*]类型值（c++）
		 */
		void releaseJstring(JNIEnv* env, const jstring jStr, const char* cStr);

	}
	

#endif