/*****************************************
  Description: DES算法.
               此算法的解码方法存在内存溢出缺陷（当提供的密文为随机字符串时99%会出现）,
			   不再使用, 后续考虑亲自重写.

  Authod     : Internet: [http://blog.csdn.net/stilling2006/article/details/4036136]
  Modify By  : EXP
  Date       : 2017-11-14
******************************************/

#include "stdafx.h"
#include "_des.h"


/* 初始置换表 (IP) */
const BYTE DES::bInitSwapTable[64] = {
	58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4,
	62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8,
	57, 49, 41, 33, 25, 17, 9,  1, 59, 51, 43, 35, 27, 19, 11, 3,
	61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7
} ;

/* 初始逆置换表 (IP-1) */
const BYTE DES::bInitReSwapTable[64] = {
	40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31,
	38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29,
	36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27,
	34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41,  9, 49, 17, 57, 25
} ;

/* 位扩展表32-->48 (E) */
const BYTE DES::bBitExternTable[48] = {
	32,  1,  2,  3,  4,  5,  4,  5,  6,  7,  8,  9,
	 8,  9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
	16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25,
	24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32,  1
} ;

/* 32位置换表--用于F的尾置换 (P) */
const BYTE DES::bTailSwapTable[32] = {
	16,  7, 20, 21, 29, 12, 28, 17,
	 1, 15, 23, 26,  5, 18, 31, 10,
	 2,  8, 24, 14, 32, 27,  3,  9,
	19, 13, 30,  6, 22, 11,  4, 25
} ;

/* 8个S盒 (S) */
const BYTE DES::SB[8][4][16] = {
	{
		14, 4, 13, 1, 1, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
		0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
		4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
		15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
	},
		
	{
		15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
		3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
		0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
		13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
	},
	
	{
		10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
		13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 
		13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
		1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
	},
	
	{
		7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 
		13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
		10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
		3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
	},
	
	{
		2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
		14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
		4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
		11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
	},
	
	{
		12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
		10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
		9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
		4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
	},
	
	{
		4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
		13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
		1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
		6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
	},
	
	{
		13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 13, 14, 5, 0, 12, 7,
		1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
		7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
		2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
	}
} ;

/* 置换选择表一 (PC-1) */
const BYTE DES::bSelSwapTable_1[56] = {
	57, 49, 41, 33, 25, 17,  9,  1, 58, 50, 42, 34, 26, 18, 
	10,  2, 59, 51, 43, 35, 27, 19, 11,  3, 60, 52, 44, 36,
	63, 55, 47, 39, 31, 23, 15,  7, 62, 54, 46, 38, 30, 22,
	14,  6, 61, 53, 45, 37, 29, 21, 13,  5, 28, 20, 12,  4
} ;

/* 置换选择表二 (PC-2) */
const BYTE DES::bSelSwapTable_2[48] = {
	14, 17, 11, 24,  1,  5,  3, 28, 15,  6, 21, 10,
	23, 19, 12,  4, 26,  8, 16,  7, 27, 20, 13,  2,
	41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48,
	44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32
} ;

/* 位掩码表 */
const BYTE DES::bBitMask[8] = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80 } ;

/* 16进制表 */
const char DES::HEX[16] = {
	'0', '1', '2', '3',
	'4', '5', '6', '7',
	'8', '9', 'a', 'b',
	'c', 'd', 'e', 'f'
};

/*
 * DES加密:
 * @param eText 明文（ASCII表示形式）
 * @param eLen 明文长度
 * @param key 密钥
 * @return 密文（16进制表示形式）
 */
const char* DES::encrypt(const char* eText, const int eLen, const char* key) {
	int cLen = eLen + (8 - eLen % 8);	// 密文长度（DES要求: 密文长度 > 明文长度，且为8的整数倍）
	unsigned char* cText = new unsigned char[cLen];	// 密文容器

	// 调用内部方法生成DES码
	DES* desObj = new DES();
	unsigned char* ueText = desObj->cConst2Uvar(eText);
	unsigned char* uKey = desObj->cConst2Uvar(key);
	desObj->DES_Encrypt(ueText, cText, cLen, uKey);
	const char* des = desObj->char2Hex(cText, cLen);

	// 释放临时资源
	delete desObj; desObj = NULL;
	delete[] ueText; ueText = NULL;
	delete[] uKey; uKey = NULL;
	delete[] cText; cText = NULL;
	return des;
}

/*
 * DES解密:
 * @param cText 密文（16进制表示形式）
 * @param cLen 密文长度（16进制形式长度）
 * @param key 密钥
 * @return 明文（ASCII表示形式）
 */
const char* DES::decrypt(const char* cHexText, const int cHexLen, const char* key) {
	DES* desObj = new DES();
	unsigned char* cText = desObj->hex2char(cHexText, cHexLen);	// 密文（ASCII表示形式）
	int cLen = cHexLen / 2;	// ASCII密文长度（ASCII形式长度 必定是 16进制形式长度 的一半）
	unsigned char* eText = new unsigned char[cLen];	// 明文容器

	// 调用内部方法对DES解码
	unsigned char* uKey = desObj->cConst2Uvar(key);
	desObj->DES_Decrypt(cText, eText, cLen, uKey);

	// 对明文做截断并标记结束位（密文长度 >= 明文长度，但明文的真正长度未知）
	char* eTextSub = new char[cLen + 1];
	int i = 0;
	for(; i < cLen && eText[i] != '\0'; i++) {
		eTextSub[i] = (char) eText[i];
	}
	eTextSub[i] = '\0';

	// 释放临时资源
	delete desObj; desObj = NULL;
	delete[] cText; cText = NULL;
	delete[] uKey; uKey = NULL;
	delete[] eText; eText = NULL;
	return eTextSub;
}

/*
 * 把ASCII字符串转换成16进制表示形式
 * @param s ASCII字符串
 * @param slen ASCII字符串长度
 * @return 16进制字符串
 */
const char* DES::char2Hex(const unsigned char* s, int slen) {
	char* hex = new char[slen * 2 + 1];
	int idx = 0;
	for(int i = 0; i < slen; i++) {
		int n = (int) s[i];
		int hn = n >> 4;
		int ln = n & 0x0F;
		hex[idx++] = HEX[hn];
		hex[idx++] = HEX[ln];
	}
	hex[idx] = '\0';
	return hex;
}

/*
 * 把16进制形式的字符串转换为ASCII字符串
 * @param hex 16进制字符串
 * @param xlen 16进制字符串
 * @return ASCII字符串
 */
unsigned char* DES::hex2char(const char* hex, int xlen) {
	unsigned char* s = new unsigned char[xlen / 2 + 1];
	int idx = 0;
	for(int i = 0; i < xlen; i+=2) {
		char hc = hex[i];
		char lc = hex[i + 1];
		unsigned int hn = hex2Uint(hc) << 4;
		unsigned int ln = hex2Uint(lc);
		s[idx++] = (unsigned char) (hn | ln);
	}
	s[idx] = '\0';
	return s;
}

/*
 * 计算16进制字符对应的10进制数值
 * @param hex 16进制字符
 * @return 10进制数值
 */
unsigned int DES::hex2Uint(char hex) {
	unsigned int n = 0;
	if(hex >= '0' && hex <= '9') {
		n = hex - '0';

	} else if(hex >= 'a' && hex <= 'f') {
		n = hex - 'a' + 10;

	} else if(hex >= 'A' && hex <= 'F') {
		n = hex - 'A' + 10;
	}
	return n;
}

/*
 * 把 [常量]字符串 转换为 [非常量][无符号]字符串
 * @param s [常量]字符串
 * @return [非常量][无符号]字符串
 */
unsigned char* DES::cConst2Uvar(const char* s) {
	int len = 0;
	while(*(s + len) != '\0') {
		len++;
	}
	
	unsigned char* us = new unsigned char[len + 1];
	int idx = 0;
	while(idx < len) {
		us[idx] = (unsigned char) s[idx];
		idx++;
	}
	us[idx] = '\0';
	return us;
}

/*
 * 内存块重置: 将 s 中前 n 个字节用 c 替换并返回 s .
 * （实现采用的是c库的源码，目的是为了平台无关）
 * @param s 内存块
 * @param c 替换字符
 * @param n 替换长度(字节数)
 * @return 替换后的内存块
 */
void *(DES::memset) (void *s, int c, size_t n)
{
	const unsigned char uc = c;
	unsigned char *su = (unsigned char *)s;
	for(; 0 < n; ++su, --n) {
		*su = uc;
	}
	return s;
}

/*
 * 内存块复制: 从源src所指的内存地址的起始位置开始拷贝len个字节到目标det所指的内存地址的起始位置中.
 * （实现采用的是c库的源码，目的是为了平台无关）
 * @param dst 目标内存块
 * @param src 源内存块
 * @param len 复制长度（字节数）
 * @return 目标内存块
 */
void *DES::memcpy(void *dst, const void *src, size_t len)
{
	if(NULL == dst || NULL == src){
		return NULL;
	}
	
	void *ret = dst;
	
	if(dst <= src || (char *)dst >= (char *)src + len){
		//没有内存重叠，从低地址开始复制
		while(len--){
			*(char *)dst = *(char *)src;
			dst = (char *)dst + 1;
			src = (char *)src + 1;
		}
	}else{
		//有内存重叠，从高地址开始复制
		src = (char *)src + len - 1;
		dst = (char *)dst + len - 1;
		while(len--){
			*(char *)dst = *(char *)src;
			dst = (char *)dst - 1;
			src = (char *)src - 1;
		}
	}
	return ret;
}

/* DES加密 */
void DES::DES_Encrypt ( )
{
	this->DES_SysInit() ;
	this->DESGenSubKey () ;
	this->DESInitSwap () ;
	for ( BYTE i = 1; i <= 16; i++ )
		this->DESSingleTurn ( i, true ) ;
	this->DESInitReSwap () ;
}

/* DES解密 */
void DES::DES_Decrypt ( )
{
	this->DES_SysInit() ;
	this->DESGenSubKey () ;
	this->DESInitSwap () ;
	for ( BYTE i = 1; i <= 16; i++ )
		this->DESSingleTurn ( i, false ) ;
	this->DESInitReSwap () ;
}

/*
 * DES加密
 * @param lpSour 明文，任意长度
 * @param lpDest 密文数组容器，长度要足够保存密文
 * @param uLen 密文数组长度（长度 > 明文且为8的整数倍）
 * @param lpKey 密钥（任意长度，但实际上只取前8个字符）
 * @return DES密文（ASCII）
 */
BOOL DES::DES_Encrypt ( LPBYTE lpSour, LPBYTE lpDest, UINT uLen, LPBYTE lpKey )
{
	if ( ( uLen & 7 ) > 0 )
		return FALSE ;
	memset ( this->bKey, 0, sizeof(this->bKey) ) ;
	memcpy ( this->bKey, lpKey, 8 ) ;
	this->DESGenSubKey () ;
	UINT nCurLen ;
	for ( nCurLen = 0; nCurLen < uLen; nCurLen += 8 )
	{
		memset ( this->bOriMsg, 0, sizeof(this->bOriMsg) ) ;
		memcpy ( this->bOriMsg, &(lpSour[nCurLen]), 8 ) ;
		this->DESInitSwap () ;
		for ( BYTE i = 1; i <= 16; i++ )
			this->DESSingleTurn ( i, true ) ;
		this->DESInitReSwap () ;
		memcpy ( &(lpDest[nCurLen]), this->bCryptedMsg, 8 ) ;
		this->DES_SysInit() ;
	}
	return TRUE ;
}

/*
 * DES解密
 * @param lpSour 密文，长度为8的整数倍
 * @param lpDest 明文数组（长度要与密文等同，但解密后的内容长度可能小于密文长度[因为DES加密存在填充机制]）
 * @param uLen 密文数组长度（长度 > 明文且为8的整数倍）
 * @param lpKey 密钥（任意长度，但实际上只取前8个字符）
 * @return 明文（ASCII）
 */
BOOL DES::DES_Decrypt  ( LPBYTE lpSour, LPBYTE lpDest, UINT uLen, LPBYTE lpKey )
{
	if ( ( uLen & 7 ) > 0 )
		return FALSE ;
	memset ( this->bKey, 0, sizeof(this->bKey) ) ;
	memcpy ( this->bKey, lpKey, 8 ) ;
	this->DESGenSubKey () ;
	UINT nCurLen ;
	for ( nCurLen = 0; nCurLen < uLen; nCurLen += 8 )
	{
		memset ( this->bOriMsg, 0, sizeof(this->bOriMsg) ) ;
		memcpy ( this->bOriMsg, &(lpSour[nCurLen]), 8 ) ;
		this->DESInitSwap () ;
		for ( BYTE i = 1; i <= 16; i++ )
			this->DESSingleTurn ( i, false ) ;
		this->DESInitReSwap () ;
		memcpy ( &(lpDest[nCurLen]), this->bCryptedMsg, 8 ) ;
		this->DES_SysInit() ;
	}
	return TRUE ;
}

/* 初始置换 */
void DES::DESInitSwap ( )
{
	// R[16]-L[16] 构成64位，经初始逆置换
	for ( UINT i = 0; i < 32; i++ )
	{
		if ( bOriMsg[(bInitSwapTable[i]-1)>>3] & bBitMask[(~(bInitSwapTable[i]-1))&7] )
			this->LMsg[0][i>>3] |= bBitMask[(~i)&7] ;
		else
			this->LMsg[0][i>>3] &= ( ~(bBitMask[(~i)&7]) );
		if ( bOriMsg[(bInitSwapTable[i+32]-1)>>3] & bBitMask[(~(bInitSwapTable[i+32]-1))&7] )
			this->RMsg[0][i>>3] |= bBitMask[(~i)&7] ;
		else
			this->RMsg[0][i>>3] &= ( ~(bBitMask[(~i)&7]) );
	}
}

/* 初始逆置换 */
void DES::DESInitReSwap ()
{
	// R[16]-L[16] 构成64位，经初始逆置换
	for ( UINT i = 0; i < 64; i++ )
	{
		if ( bInitReSwapTable[i] <= 32 )
		{
			if ( RMsg[16][(bInitReSwapTable[i]-1)>>3] & bBitMask[(~(bInitReSwapTable[i]-1))&7] )
				this->bCryptedMsg[i>>3] |= bBitMask[(~i)&7] ;
			else
				this->bCryptedMsg[i>>3] &= ( ~(bBitMask[(~i)&7]) ) ;
		}
		else
		{
			if ( LMsg[16][(bInitReSwapTable[i]-32-1)>>3] & bBitMask[(~(bInitReSwapTable[i]-32-1))&7] )
				this->bCryptedMsg[i>>3] |= bBitMask[(~i)&7] ;
			else
				this->bCryptedMsg[i>>3] &= ( ~(bBitMask[(~i)&7]) ) ;
		}
	}
}

/* 产生子密钥 */
void DES::DESGenSubKey ( )
{
	memset ( this->bSubKey, 0, sizeof(this->bSubKey) ) ;
	// 置换选择一
	UINT i ;
	BYTE bByteIndex, bBitIndex, bTemp[7] = {0} ;
	for ( i = 0; i < 56; i++ )
	{
		bByteIndex	= ( bSelSwapTable_1[i] - 1 ) / 8 ;
		bBitIndex	= 7 - ( bSelSwapTable_1[i] - 1 ) % 8 ;
		if ( this->bKey[bByteIndex] & bBitMask[bBitIndex] )
			bTemp[i>>3] |= bBitMask[(~i)&7] ;
	}
	// 产生子密钥过程
	BYTE bTempKey[7] = {0} ;
	for ( UINT uTurn = 1; uTurn <= 16; uTurn ++ )
	{
		UINT bShiftBit = 2 ;
		if ( uTurn == 1 || uTurn == 2 || uTurn == 9 || uTurn == 16 )
			bShiftBit = 1 ;
		// 循环移位
		for ( i = bShiftBit; i < (28+bShiftBit); i++ )
		{
			if ( bTemp[(i%28)>>3] & bBitMask[(~(i%28))&7] )
				bTempKey[(i-bShiftBit)>>3] |= bBitMask[(~(i-bShiftBit))&7] ;
			if ( bTemp[(i%28+28)>>3] & bBitMask[(~(i%28+28))&7] )
				bTempKey[(i+28-bShiftBit)>>3] |= bBitMask[(~(i+28-bShiftBit))&7] ;
		}
		// 置换选择二
		for ( i = 0; i < 48; i++ )
		{
			bByteIndex	= ( bSelSwapTable_2[i] - 1 ) / 8 ;
			bBitIndex	= 7 - ( bSelSwapTable_2[i] - 1 ) % 8 ;
			if ( bTempKey[bByteIndex] & bBitMask[bBitIndex] )
				this->bSubKey[uTurn][i>>3] |= bBitMask[(~i)&7] ;
		}
		//cout << "turn " << uTurn << "/t :  " ;
		//this->Output ( bTempKey, 7 ) ;
		memcpy ( bTemp, bTempKey, 7 ) ;
		memset ( bTempKey, 0, sizeof(bTempKey) ) ;
	}
}

/* DES的单轮加密过程 */
void DES::DESSingleTurn ( BYTE nTurnIndex, BOOL bMode )
{
	// L[n] = R[n-1]
	for ( UINT i = 0; i < 4; i++ )
		LMsg[nTurnIndex][i] = RMsg[nTurnIndex-1][i] ;
	
	// DES的F函数，（内部处理：R[n] = F( R[n-1], k[n] )）
	this->DES_f ( nTurnIndex, bMode ) ;
	
	// R[n] = L[n-1] xor F( R[n-1], k[n] ) ;
	for ( UINT i = 0; i < 4; i++ )
		RMsg[nTurnIndex][i] ^= LMsg[nTurnIndex-1][i] ;
}

/* DES的F函数 */
void DES::DES_f ( BYTE bTurnIndex, BOOL bMode )
{
	PBYTE	R = RMsg[bTurnIndex-1] ;
	BYTE	bTemp[6] = {0} ;
	BYTE	C[4] = {0} ;
	
	// 扩展 R --> E(R)，结果保存在bTemp
	for ( BYTE i = 0; i < 48; i++ )
	{
		if ( R[(bBitExternTable[i]-1)>>3] & bBitMask[(~(bBitExternTable[i]-1))&7] )
			bTemp[i>>3] |= bBitMask[(~i)&7] ;
		else
			bTemp[i>>3] &= ( ~(bBitMask[(~i)&7]) );
	}
	
	// E(R) xor K，结果保存在bTemp
	for ( BYTE i = 0; i < 6; i++ )
	{
		if ( bMode )
			bTemp[i] ^= this->bSubKey[bTurnIndex][i] ;
		else
			bTemp[i] ^= this->bSubKey[17-bTurnIndex][i] ;
	}
	
	BYTE bStartPos = 0, bTarPos = 0, bTarValue ;
	BYTE bRowIndex = 0, bColIndex = 0 ;
	for ( BYTE bBoxIndex = 0; bBoxIndex < 8; bBoxIndex ++ )
	{
		// 确定行值
		if ( bTemp[bStartPos>>3] & bBitMask[(~bStartPos)&7] )
			bColIndex += 2 ;
		if ( bTemp[(bStartPos+5)>>3] & bBitMask[(~(bStartPos+5))&7] )
			bColIndex += 1 ;
		
		// 确定列值
		if ( bTemp[(bStartPos+1)>>3] & bBitMask[(~(bStartPos+1))&7] )
			bRowIndex += 8 ;
		if ( bTemp[(bStartPos+2)>>3] & bBitMask[(~(bStartPos+2))&7] )
			bRowIndex += 4 ;
		if ( bTemp[(bStartPos+3)>>3] & bBitMask[(~(bStartPos+3))&7] )
			bRowIndex += 2 ;
		if ( bTemp[(bStartPos+4)>>3] & bBitMask[(~(bStartPos+4))&7] )
			bRowIndex += 1 ;
		
		// 在S(i)盒中由上面行列所对应的值bTarValue
		bTarValue = SB[bBoxIndex][bColIndex][bRowIndex] ;
		
		// 把bTarValue转化成对应的C(i)
		if ( bTarValue >= 8 )
		{
			C[bTarPos>>3] |= bBitMask[(~bTarPos)&7] ;
			bTarValue -= 8 ;
		}
		if ( bTarValue >= 4 )
		{
			C[(bTarPos+1)>>3] |= bBitMask[(~(bTarPos+1))&7] ;
			bTarValue -= 4 ;
		}
		if ( bTarValue >= 2 )
		{
			C[(bTarPos+2)>>3] |= bBitMask[(~(bTarPos+2))&7] ;
			bTarValue -= 2 ;
		}
		if ( bTarValue >= 1 )
		{
			C[(bTarPos+3)>>3] |= bBitMask[(~(bTarPos+3))&7] ;
			bTarValue -= 1 ;
		}		
	
		bRowIndex	= bColIndex = 0 ;
		bStartPos	+= 6 ;
		bTarPos		+= 4 ;
	}
	
	// 尾置换
	for ( BYTE i = 0; i < 32; i++ )
	{
		if ( C[(bTailSwapTable[i]-1)>>3] & bBitMask[(~(bTailSwapTable[i]-1))&7] )
			this->RMsg[bTurnIndex][i>>3] |= bBitMask[(~i)&7] ;
		else
			this->RMsg[bTurnIndex][i>>3] &= ( ~(bBitMask[(~i)&7]) );
	}
}

/* 初始化 */
void DES::DES_SysInit ()
{
	memset ( LMsg,		0, sizeof(LMsg) ) ;
	memset ( RMsg,		0, sizeof(RMsg) ) ;
	memset ( bSubKey,	0, sizeof(bSubKey) ) ;
	memset ( bCryptedMsg,	0, sizeof(bCryptedMsg) ) ; 
}

/* 设置明文 */
void DES::DES_SetOriMsg ( PBYTE pMsg, UINT uLen )
{
	memset ( this->bOriMsg, 0, sizeof(this->bOriMsg) ) ;
	memcpy ( this->bOriMsg, pMsg, uLen ) ;
}

/* 设置密钥 */
void DES::DES_SetKey ( PBYTE pKey, UINT uLen )
{
	memset ( this->bKey, 0, sizeof(this->bKey) ) ;
	memcpy ( this->bKey, pKey, uLen ) ;
}
