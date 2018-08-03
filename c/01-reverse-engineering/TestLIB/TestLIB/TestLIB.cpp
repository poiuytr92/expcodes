// TestLIB.cpp : 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include "simpleDLL.h"
#include "utils_os.h"
#include "utils_str.h"
#include <iostream>
using namespace std;


int _tmain(int argc, _TCHAR* argv[])
{
	cout << add(1, 2) <<endl;
	cout << OS_UTILS::isX64() <<endl;
	cout << STR_UTILS::toWChar("sjah12") << endl;

	system("pause");
	return 0;
}

