/*****************************************
  Description: 日志工具包
  Authod     : EXP
  Modify By  : None
  Date       : 2017-11-14
******************************************/

#define DLL_IMPLEMENT

#include "stdafx.h"
#include "utils_log.h"

namespace LOG_UTILS {

	const static char* DEFAULT_LOG_FILE_PATH = "./log/log.log";

	DLL_API bool log(const char* msg, bool isAppend) {
		return log(DEFAULT_LOG_FILE_PATH, msg, isAppend);
	}

	DLL_API bool log(const char* logFilePath, const char* msg, bool isAppend) {
		bool isOk = false;
		FILE* fp = fopen(logFilePath, isAppend ? "a+" : "w+");
		if(fp != NULL) {
			fprintf(fp, "%s\n", msg);	// 输出到文件
			fclose(fp);
			isOk = true;
		}
		printf("%s\n", msg);	// 输出到控制台
		return isOk;
	}

}