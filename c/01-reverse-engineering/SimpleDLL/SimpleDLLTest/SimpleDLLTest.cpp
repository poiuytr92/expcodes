// SimpleDLLTest.cpp : 定义控制台应用程序的入口点。

#include "stdafx.h"
#include "../SimpleDLL/simpleDLL.h" //添加头文件引用
#include "../SimpleDLL/time_utils.h"
#pragma comment(lib, "..\\x64\\Debug\\SimpleDLL.lib") //添加lib文件引用 
#include <process.h>
#include <locale.h>
#include <iostream>
using namespace std;


int _tmain(int argc, _TCHAR* argv[])
{
	cout << SIMPLE_UTILS::add(1, 2) << endl;
	cout << TIME_UTILS::getCurrentTimeMillis() << endl;
	system("pause");
	return 0;
}
