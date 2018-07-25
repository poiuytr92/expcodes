/*****************************************
  Description: 日志工具包（调试用）
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#ifndef __LOG_UTILS_H_
#define __LOG_UTILS_H_

	namespace LOG_UTILS {

		void print(const char* msg, bool isAppend);

		void print(const char* logFilePath, const char* msg, bool isAppend);

	}

#endif