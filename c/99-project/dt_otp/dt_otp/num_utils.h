/*****************************************
  Description: 数字处理工具包
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#ifndef __NUM_UTILS_H_
#define __NUM_UTILS_H_

	namespace NUM_UTILS {

		/*
		 * 把数字字符串转换成long long数值
		 * @param str 数字字符串
		 * @return long long 数值
		 */
		const long long toLongLong(const char* str);

		/*
		 * 把long long数值转换成数字字符串
		 * @param _long long long 数值
		 * @return 数字字符串
		 */
		const char* toStr(const long long _long);

	}

#endif
