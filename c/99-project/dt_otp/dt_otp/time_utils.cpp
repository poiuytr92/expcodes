/*****************************************
  Description: 日期时间工具包
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#include "stdafx.h"
#include "time_utils.h"

namespace TIME_UTILS {
	
	#ifdef _WIN32
		#include <windows.h>

		/*
		 * 获取1970年至今UTC的微秒时间值(win版本, 精确到ms)
		 * （与java的当前系统时间函数System.currentTimeMillis()等价的时间值）
		 * @return 当前系统时间
		 */
		const long long getCurrentTimeMillis() {
			union {
				long long ns100;	//纳秒时间
				FILETIME filetime;	//系统文件时间
			} now;
			GetSystemTimeAsFileTime(&now.filetime);

			long long second = (now.ns100 - 116444736000000000LL) / 10000000LL;	// 秒部分(单位ns)
			long long millis = (now.ns100 / 10LL) % 1000000LL;	// 微秒部分(单位ns)
			long long ms = second * 1000 + millis / 1000;	// 微秒数(单位ms)
			return ms;
		}

	#else
		#include <time.h>

		/*
		 * 获取1970年至今UTC的微秒时间值（linux版本, 精确到s）
		 * @return 当前系统时间
		 */
		const long long getCurrentTimeMillis() {
			long long second = (long long) time((time_t*) NULL);    // time_t 在32位编译环境会溢出, 必须用long long强转存储
			long long millis = second * 1000;
			return millis;
		}

	#endif
	
}
