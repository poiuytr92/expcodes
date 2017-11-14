/*****************************************
  Description: MD5算法
  Authod     : Internet: [http://blog.csdn.net/flydream0/article/details/7045429]
  Modify By  : EXP
  Date       : 2017-11-14
******************************************/

#pragma once;  

#ifndef __MD5_H_
#define __MD5_H_

	#include <string>
	#include <fstream>
	using std::string;
	using std::ifstream;

	/* Type define */
	typedef unsigned char _byte;

	// MD5算法会针对_ulong数据做位移运算，必须保证 32bit和64bit 的数据等长
	// 否则会因为数据被截断问题，导致 32bit和64bit 平台的运算结果不一致。
	#ifdef _LP64	
		typedef unsigned long _ulong;
	#else
		typedef unsigned long long _ulong;  // 其实不论32还是64，直接声明long long即可固定精度为64
	#endif

	/* MD5 declaration. */
	class MD5 {
		public:
			MD5();
			MD5(const void *input, size_t length);  // 计算字符串的MD5码（推荐）
			MD5(const string &str);	// 计算字符串的MD5码
			MD5(ifstream &in);		// 计算文件流的MD5码
			
			const string toString();	// 生成MD5码
			const char* toCharArray();	// 生成MD5码 （由于string.c_str()非堆空间，无法持久保存，故增加此方法）

		private:
            string bytesToHexString(const _byte *input, size_t length);
            
			void *(memset) (void *s, int c, size_t n);	// 内存块重置（实现采用的是c库的源码，目的是为了平台无关）
			void *memcpy(void *dst, const void *src, size_t len);// 内存块复制（实现采用的是c库的源码，目的是为了平台无关）

		private:
            void reset();
            const _byte* digest();

			void update(const void *input, size_t length);
			void update(const string &str);
			void update(ifstream &in);
			void update(const _byte *input, size_t length);
			void transform(const _byte block[64]);
            void final();

			void encode(const _ulong *input, _byte *output, size_t length);
			void decode(const _byte *input, _ulong *output, size_t length);
			
			/* class uncopyable */
			MD5(const MD5&);
			MD5& operator=(const MD5&);

        private:
			_ulong _state[4]; /* state (ABCD) */
			_ulong _count[2]; /* number of bits, modulo 2^64 (low-order word first) */
			_byte _buffer[64]; /* input buffer */
			_byte _digest[16]; /* message digest */
			bool _finished;  /* calculate finished ? */

			const static _byte PADDING[64]; /* padding for calculate */
			const static char HEX[16];		// 16进制表

			const static size_t BUFFER_SIZE = 1024;
	};


#endif/*_MD5_H_*/