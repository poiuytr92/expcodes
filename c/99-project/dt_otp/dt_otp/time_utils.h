/*****************************************
  Description: 日期时间工具包
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#ifndef __TIME_UTILS_H_
#define __TIME_UTILS_H_

	namespace TIME_UTILS {
		
		/*
		 * 获取1970年至今UTC的微秒时间值
		 * （与java的当前系统时间函数System.currentTimeMillis()等价的时间值）
		 * @return 当前系统时间
		 */
		const long long getCurrentTimeMillis();

	}

#endif