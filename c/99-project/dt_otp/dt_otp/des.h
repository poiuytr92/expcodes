/*****************************************
  Description: DES算法.
               此算法的解码方法存在内存溢出缺陷（当提供的密文为随机字符串时99%会出现）,
			   不再使用, 后续考虑亲自重写.

  Authod     : Internet: [http://blog.csdn.net/stilling2006/article/details/4036136]
  Modify By  : EXP | http://exp-blog.com
  Date       : 2015-07-20
******************************************/

#ifndef __DES_H_
#define __DES_H_

	#ifdef _LP64
		typedef unsigned long UINT;
	#else
		typedef unsigned long long UINT;
	#endif

	typedef unsigned char BYTE;
	typedef BYTE* LPBYTE;
	typedef BYTE* PBYTE;
	typedef bool BOOL;
	#define TRUE true;
	#define FALSE false;

	class DES {
		private:
			BYTE bOriMsg[8] ;				// 初始消息 ( 64 bit )
			BYTE LMsg[17][4], RMsg[17][4] ;	// 中间变量L0-->L16,R0-->R16 ( 32 bit )
			BYTE bKey[8] ;					// 保存密钥 ( 64 bit )
			BYTE bSubKey[17][6] ;			// 保存子密钥K1-->K16 ( 48 bit )
			BYTE bCryptedMsg[8] ;			// 密文

			const static BYTE bInitSwapTable[64];	// 初始置换表 (IP)
			const static BYTE bInitReSwapTable[64];	// 初始逆置换表 (IP-1)
			const static BYTE bBitExternTable[48];	// 位扩展表32-->48 (E)
			const static BYTE bTailSwapTable[32];	// 32位置换表--用于F的尾置换 (P)
			const static BYTE SB[8][4][16];			// 8个S盒 (S)
			const static BYTE bSelSwapTable_1[56];	// 置换选择表一 (PC-1)
			const static BYTE bSelSwapTable_2[48];	// 置换选择表二 (PC-2)
			const static BYTE bBitMask[8];			// 位掩码表
			const static char HEX[16];				// 16进制表

		public:
			const static char* encrypt(const char* eText, const int eLen, const char* key);	// DES加密（原方法不好用，故再封装一层）
			const static char* decrypt(const char* cHexText, const int cHexLen, const char* key);	// DES解密（原方法不好用，故再封装一层）

		private:
			const char* char2Hex(const unsigned char* s, int slen);	// 把ASCII字符串转换成16进制表示形式
			unsigned char* hex2char(const char* hex, int xlen);		// 把16进制形式的字符串转换为ASCII字符串
			unsigned int hex2Uint(char hex);	// 计算16进制字符对应的10进制数值

			unsigned char* cConst2Uvar(const char* s);	// 把 [常量]字符串 转换为 [非常量][无符号]字符串

		private:
			void *(memset) (void *s, int c, size_t n);	// 内存块重置（实现采用的是c库的源码，目的是为了平台无关）
			void *memcpy(void *dst, const void *src, size_t len);// 内存块复制（实现采用的是c库的源码，目的是为了平台无关）

		private:
			void DES_Encrypt () ;	// DES加密
			void DES_Decrypt () ;	// DES解密
			BOOL DES_Encrypt ( LPBYTE lpSour, LPBYTE lpDest, UINT uLen, LPBYTE lpKey ) ;	// DES加密
			BOOL DES_Decrypt ( LPBYTE lpSour, LPBYTE lpDest, UINT uLen, LPBYTE lpKey ) ;	// DES解密

			void DESInitSwap () ;	// 初始置换
			void DESInitReSwap () ;	// 初始逆置换
			void DESGenSubKey () ;	// 产生子密钥
			void DESSingleTurn ( BYTE nTurnIndex, BOOL bMode ) ;// DES的单轮加密过程
			void DES_f ( BYTE bTurnIndex, BOOL bMode ) ;		// DES的F函数

			void DES_SysInit () ;	// 初始化
			void DES_SetOriMsg ( PBYTE pMsg, UINT uLen ) ;	// 设置明文
			void DES_SetKey ( PBYTE pKey, UINT uLen ) ;		// 设置密钥
	} ;

#endif