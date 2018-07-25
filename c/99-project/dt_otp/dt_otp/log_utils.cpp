/*****************************************
  Description: 日志工具包（调试用）
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#include "stdafx.h"
#include "log_utils.h"

namespace LOG_UTILS {

	void print(const char* msg, bool isAppend) {
		const char* logFilePath  = "./log/clog.log";
		print(logFilePath, msg, isAppend);
	}

	void print(const char* logFilePath, const char* msg, bool isAppend) {
		FILE* fp = fopen(logFilePath, isAppend ? "a+" : "w+");
		printf("%s\n", msg);
		fprintf(fp, "%s\n", msg);
		fclose(fp);

		delete[] logFilePath; logFilePath = NULL;
		delete[] msg; msg = NULL;
	}

}