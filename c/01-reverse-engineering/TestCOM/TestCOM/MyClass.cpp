// MyClass.cpp : CMyClass 的实现

#include "stdafx.h"
#include "MyClass.h"


// CMyClass



STDMETHODIMP CMyClass::add(LONG a, LONG b, LONG* result)
{
	// TODO: 在此添加实现代码
	*result = a + b;
	return S_OK;
}
