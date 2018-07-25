/*****************************************
  Description: java-to-c类型转换工具包
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/


#include "stdafx.h"	
#include "str_utils.h"
#include "jni_utils.h"

namespace JNI_UTILS {

	/*
	 * [jlong] -> [long long]
	 * @param _long [jlong]类型值（java）
	 * @return [long long]类型值（c++）
	 */
	const long long jlong2LL(const jlong _long) {
		return (long long) _long;
	}

	/*
	 * [long long] -> [jlong]
	 * @param _long [long long]类型值（c++）
	 * @return [jlong]类型值（java）
	 */
	const jlong ll2Jlong(const long long _long) {
		return (jlong) _long;
	}

	/*
	 * [jstring] -> [char*]
	 * @param env JNI环境参数指针
	 * @param str [jstring]类型值（java）
	 * @return [char*]类型值（c++）
	 */
	const char* jstring2Char(JNIEnv* env, const jstring str) {
		return env->GetStringUTFChars(str, NULL);
	}

	/*
	 * [char*] -> [jstring]
	 * @param env JNI环境参数指针
	 * @param str [char*]类型值（c++）
	 * @return [jstring]类型值（java）
	 */
	const jstring char2Jstring(JNIEnv* env, const char* str) {
		jclass stringClass = env->FindClass("java/lang/String");
		jmethodID mId = env->GetMethodID(stringClass, "<init>", "([BLjava/lang/String;)V");
		jstring encode = env->NewStringUTF("utf-8");

		jsize len = STR_UTILS::sLen(str);
		jbyteArray byteAry = env->NewByteArray(len);
		env->SetByteArrayRegion(byteAry, 0, len, (jbyte*) str);

		return (jstring) env->NewObject(stringClass, mId, byteAry, encode);
	}

	/*
	 * 释放[已使用完的][配对的] jstring 与 char* 指向的内存
	 * @param env JNI环境参数指针
	 * @param jStr [jstring]类型值（java）
	 * @param cStr [char*]类型值（c++）
	 */
	void releaseJstring(JNIEnv* env, const jstring jStr, const char* cStr) {
		env->ReleaseStringUTFChars(jStr, cStr);
	}

}
