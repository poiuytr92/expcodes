// exp-libs-test.cpp : 定义控制台应用程序的入口点。
// 参考教程：《VS2010编写动态链接库DLL及单元测试用例》 http://blog.csdn.net/testcs_dn/article/details/27237509

#include "stdafx.h"
#include "..\\exp-libs\utils_time.h"
#include "..\\exp-libs\utils_num.h"
#include "..\\exp-libs\\utils_str.h"
#include "..\\exp-libs\\utils_log.h"
#include "..\\exp-libs\\utils_crypto.h"
#include "..\\exp-libs\\utils_os.h"

#include <string>
#include <iostream>
using namespace std;

#pragma comment(lib, "..\\x64\\Debug\\exp-libs.lib")	// 添加dll库(仅测试用)，需放在include头文件之后


int _tmain(int argc, _TCHAR* argv[])
{
	cout << TIME_UTILS::getCurrentTimeMillis() << endl;
	cout << NUM_UTILS::toStr(1234LL) << endl;
	cout << STR_UTILS::toUpperCase("abC12XDsdk") << endl;
	cout << LOG_UTILS::log("line1", false) << endl;
	cout << LOG_UTILS::log("line2", true) << endl;
	cout << CRYPTO_UTILS::toMD5("asfjhsj128y89&&32", "explibs") << endl;
	cout << OS_UTILS::isX64() << endl;
	cout << string(STR_UTILS::toChar(_T("ABCf134"))) << endl;

	wprintf(STR_UTILS::toWChar("System.console"));
	cout << endl;

	OS_UTILS::copyToClipboard("abcd剪贴板测试uasa");
	const char* data = OS_UTILS::pasteFromClipboard();
	cout << data << endl;
	
	system("pause");
	return 0;
}

