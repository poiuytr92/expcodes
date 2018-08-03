// dllmain.h : 模块类的声明。

class CTestCOMModule : public ATL::CAtlDllModuleT< CTestCOMModule >
{
public :
	DECLARE_LIBID(LIBID_TestCOMLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_TESTCOM, "{9621DC53-8F6C-431D-92ED-5A06CB2A553C}")
};

extern class CTestCOMModule _AtlModule;
