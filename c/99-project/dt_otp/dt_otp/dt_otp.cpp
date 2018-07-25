/*****************************************
  Description: 定义 DLL 应用程序的导出函数
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#include "stdafx.h"
#include "jni_utils.h"
#include "otp_impl.h"
#include "dt_otp.h"

#define DEFAULTE_PRIMARY_KEY "www.exp-blog.com\0"
#define DEFAULTE_TIME_OFFSET 300000LL

/*
 * Class:     exp_token_otp__OTP_CAPI
 * Method:    getDefaultPrivateKey
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_exp_token_otp__1OTP_1CAPI_getDefaultPrivateKey
  (JNIEnv *env, jclass method)
{
	return JNI_UTILS::char2Jstring(env, DEFAULTE_PRIMARY_KEY);
}

/*
 * Class:     exp_token_otp__OTP_CAPI
 * Method:    getDefaultTimeOffset
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_exp_token_otp__1OTP_1CAPI_getDefaultTimeOffset
  (JNIEnv *env, jclass method)
{
	return JNI_UTILS::ll2Jlong(DEFAULTE_TIME_OFFSET);
}

/*
 * Class:     exp_token_otp__OTP_CAPI
 * Method:    getOtpToken
 * Signature: (Ljava/lang/String;J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_exp_token_otp__1OTP_1CAPI_getOtpToken
  (JNIEnv *env, jclass method, jstring privateKey, jlong timeOffset)
{
	const char* cPrivateKey = JNI_UTILS::jstring2Char(env, privateKey);
	const long long cTimeOffset = JNI_UTILS::jlong2LL(timeOffset);
	const char* otpToken = OTP_IMPL::getOtpToket(cPrivateKey, cTimeOffset);

	JNI_UTILS::releaseJstring(env, privateKey, cPrivateKey);
	return JNI_UTILS::char2Jstring(env, otpToken);
}

/*
 * Class:     exp_token_otp__OTP_CAPI
 * Method:    isValid
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_exp_token_otp__1OTP_1CAPI_isValid
  (JNIEnv *env, jclass method, jstring otpToken, jstring privateKey)
{
	const char* cOtpToken = JNI_UTILS::jstring2Char(env, otpToken);
	const char* cPrivateKey = JNI_UTILS::jstring2Char(env, privateKey);
	bool isVaild = OTP_IMPL::isValid(cOtpToken, cPrivateKey);

	JNI_UTILS::releaseJstring(env, otpToken, cOtpToken);
	JNI_UTILS::releaseJstring(env, privateKey, cPrivateKey);
	return isVaild;
}


