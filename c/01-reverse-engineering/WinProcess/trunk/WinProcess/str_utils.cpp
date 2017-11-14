
#include "stdafx.h"
#include "str_utils.h"

TCHAR* toWChar(const int num) {
	TCHAR* _out_wchar = new TCHAR[22];	// int32最长为11位, int64最长为21位
	wsprintf(_out_wchar, _T("%d"), num);
	return _out_wchar;
}

TCHAR* toWChar(const char* _char) {
	int len = MultiByteToWideChar (CP_ACP, 0, _char, (strlen(_char) + 1), NULL, 0) ;  
	TCHAR* _out_wchar = new TCHAR[len];	// 堆空间new的局部变量，在delete之前不会释放

	MultiByteToWideChar (CP_ACP, 0, _char, (strlen(_char) + 1), _out_wchar, len) ;  
	return _out_wchar;
}

char* toChar(const TCHAR* _wchar) {
	int len = WideCharToMultiByte(CP_ACP, 0, _wchar, -1, NULL, 0, NULL, NULL);
	char* _out_char = new char[len];	// 堆空间new的局部变量，在delete之前不会释放

	WideCharToMultiByte(CP_ACP, 0, _wchar, -1, _out_char, len, NULL, NULL);   
	return _out_char;
}
