/*****************************************
  Description: MD5算法
  Authod     : Internet: [http://blog.csdn.net/flydream0/article/details/7045429]
  Modify By  : EXP
  Date       : 2017-11-14
******************************************/

#include "stdafx.h"
#include "_md5.h"

/* Constants for MD5Transform routine. */
#define S11 7
#define S12 12
#define S13 17
#define S14 22
#define S21 5
#define S22 9
#define S23 14
#define S24 20
#define S31 4
#define S32 11
#define S33 16
#define S34 23
#define S41 6
#define S42 10
#define S43 15
#define S44 21

/* F, G, H and I are basic MD5 functions. */
#define F(x, y, z) (((x) & (y)) | ((~x) & (z)))
#define G(x, y, z) (((x) & (z)) | ((y) & (~z)))
#define H(x, y, z) ((x) ^ (y) ^ (z))
#define I(x, y, z) ((y) ^ ((x) | (~z)))


/* ROTATE_LEFT rotates x left n bits. */
#define ROTATE_LEFT(x, n) (((x) << (n)) | ((x) >> (32-(n))))

/* 
 * FF, GG, HH, and II transformations for rounds 1, 2, 3, and 4.
 * Rotation is separate from addition to prevent recomputation.
 */
#define FF(a, b, c, d, x, s, ac) { \
	(a) += F ((b), (c), (d)) + (x) + ac; \
	(a) = ROTATE_LEFT ((a), (s)); \
	(a) += (b); \
}
#define GG(a, b, c, d, x, s, ac) { \
	(a) += G ((b), (c), (d)) + (x) + ac; \
	(a) = ROTATE_LEFT ((a), (s)); \
	(a) += (b); \
}
#define HH(a, b, c, d, x, s, ac) { \
	(a) += H ((b), (c), (d)) + (x) + ac; \
	(a) = ROTATE_LEFT ((a), (s)); \
	(a) += (b); \
}
#define II(a, b, c, d, x, s, ac) { \
	(a) += I ((b), (c), (d)) + (x) + ac; \
	(a) = ROTATE_LEFT ((a), (s)); \
	(a) += (b); \
}

/* padding for calculate */
const _byte MD5::PADDING[64] = { 0x80 };

/* 16进制表 */
const char MD5::HEX[16] = {
	'0', '1', '2', '3',
	'4', '5', '6', '7',
	'8', '9', 'a', 'b',
	'c', 'd', 'e', 'f'
};

/* Default construct. */
MD5::MD5() {
	reset();
}

/*
 * 构造函数:生成指定字符串的MD5码.
 * Construct a MD5 object with a input buffer.
 * 
 * @param str 指定字符串
 * @param length 指定字符串长度
 */
MD5::MD5(const void *input, size_t length) {
	reset();
	update(input, length);
}

/*
 * 构造函数:生成指定字符串的MD5码.
 * Construct a MD5 object with a string.
 * 
 * @param str 指定字符串
 */
MD5::MD5(const string &str) {
	reset();
	update(str);
}

/*
 * 构造函数:生成指定文件的MD5码.
 * Construct a MD5 object with a file.
 * 
 * @param in 指定文件的读入流
 */
MD5::MD5(ifstream &in) {
	reset();
	update(in);
}

/* Reset the calculate state */
void MD5::reset() {
	_finished = false;

	/* reset number of bits. */
	_count[0] = _count[1] = 0;

	/* Load magic initialization constants. */
	_state[0] = 0x67452301;
	_state[1] = 0xefcdab89;
	_state[2] = 0x98badcfe;
	_state[3] = 0x10325476;
}

/* Return the message-digest */
const _byte* MD5::digest() {
	if (!_finished) {
		_finished = true;
		final();
	}
	return _digest;
}

/* Updating the context with a input buffer. */
void MD5::update(const void *input, size_t length) {
	update((const _byte*)input, length);
}

/* Updating the context with a string. */
void MD5::update(const string &str) {
	update((const _byte*)str.c_str(), str.length());
}

/* Updating the context with a file. */
void MD5::update(ifstream &in) {
	if (!in) {
		return;
	}

	std::streamsize length;
	char buffer[BUFFER_SIZE];
	while (!in.eof()) {
		in.read(buffer, BUFFER_SIZE);
		length = in.gcount();
		if (length > 0) {
			update(buffer, length);
		}
	}
	in.close();
}

/* 
 * MD5 block update operation. Continues an MD5 message-digest
 * operation, processing another message block, and updating the context.
 */
void MD5::update(const _byte *input, size_t length) {
	_ulong i, index, partLen;
	_finished = false;

	/* Compute number of bytes mod 64 */
	index = (_ulong)((_count[0] >> 3) & 0x3f);

	/* update number of bits */
	if((_count[0] += ((_ulong)length << 3)) < ((_ulong)length << 3)) {
		_count[1]++;
	}
	_count[1] += ((_ulong)length >> 29);

	partLen = 64 - index;

	/* transform as many times as possible. */
	if(length >= partLen) {
		memcpy(&_buffer[index], input, partLen);
		transform(_buffer);
		
		for (i = partLen; i + 63 < length; i += 64) {
			transform(&input[i]);
		}
		index = 0;
	
	} else {
		i = 0;
	}

	/* Buffer remaining input */
	memcpy(&_buffer[index], &input[i], length-i);
}

/*
 * MD5 finalization. Ends an MD5 message-_digest operation, writing the
 * the message _digest and zeroizing the context.
 */
void MD5::final() {
	_byte bits[8];
	_ulong oldState[4];
	_ulong oldCount[2];
	_ulong index, padLen;

	/* Save current state and count. */
	memcpy(oldState, _state, 16);
	memcpy(oldCount, _count, 8);

	/* Save number of bits */
	encode(_count, bits, 8);

	/* Pad out to 56 mod 64. */
	index = (_ulong)((_count[0] >> 3) & 0x3f);
	padLen = (index < 56) ? (56 - index) : (120 - index);
	update(PADDING, padLen);

	/* Append length (before padding) */
	update(bits, 8);

	/* Store state in digest */
	encode(_state, _digest, 16);

	/* Restore current state and count. */
	memcpy(_state, oldState, 16);
	memcpy(_count, oldCount, 8);
}

/* MD5 basic transformation. Transforms _state based on block. */
void MD5::transform(const _byte block[64]) {
	_ulong a = _state[0];
	_ulong b = _state[1];
	_ulong c = _state[2];
	_ulong d = _state[3];
	_ulong x[16];

	decode(block, x, 64);

	/* Round 1 */
	FF (a, b, c, d, x[ 0], S11, 0xd76aa478); /* 1 */
	FF (d, a, b, c, x[ 1], S12, 0xe8c7b756); /* 2 */
	FF (c, d, a, b, x[ 2], S13, 0x242070db); /* 3 */
	FF (b, c, d, a, x[ 3], S14, 0xc1bdceee); /* 4 */
	FF (a, b, c, d, x[ 4], S11, 0xf57c0faf); /* 5 */
	FF (d, a, b, c, x[ 5], S12, 0x4787c62a); /* 6 */
	FF (c, d, a, b, x[ 6], S13, 0xa8304613); /* 7 */
	FF (b, c, d, a, x[ 7], S14, 0xfd469501); /* 8 */
	FF (a, b, c, d, x[ 8], S11, 0x698098d8); /* 9 */
	FF (d, a, b, c, x[ 9], S12, 0x8b44f7af); /* 10 */
	FF (c, d, a, b, x[10], S13, 0xffff5bb1); /* 11 */
	FF (b, c, d, a, x[11], S14, 0x895cd7be); /* 12 */
	FF (a, b, c, d, x[12], S11, 0x6b901122); /* 13 */
	FF (d, a, b, c, x[13], S12, 0xfd987193); /* 14 */
	FF (c, d, a, b, x[14], S13, 0xa679438e); /* 15 */
	FF (b, c, d, a, x[15], S14, 0x49b40821); /* 16 */

	/* Round 2 */
	GG (a, b, c, d, x[ 1], S21, 0xf61e2562); /* 17 */
	GG (d, a, b, c, x[ 6], S22, 0xc040b340); /* 18 */
	GG (c, d, a, b, x[11], S23, 0x265e5a51); /* 19 */
	GG (b, c, d, a, x[ 0], S24, 0xe9b6c7aa); /* 20 */
	GG (a, b, c, d, x[ 5], S21, 0xd62f105d); /* 21 */
	GG (d, a, b, c, x[10], S22,  0x2441453); /* 22 */
	GG (c, d, a, b, x[15], S23, 0xd8a1e681); /* 23 */
	GG (b, c, d, a, x[ 4], S24, 0xe7d3fbc8); /* 24 */
	GG (a, b, c, d, x[ 9], S21, 0x21e1cde6); /* 25 */
	GG (d, a, b, c, x[14], S22, 0xc33707d6); /* 26 */
	GG (c, d, a, b, x[ 3], S23, 0xf4d50d87); /* 27 */
	GG (b, c, d, a, x[ 8], S24, 0x455a14ed); /* 28 */
	GG (a, b, c, d, x[13], S21, 0xa9e3e905); /* 29 */
	GG (d, a, b, c, x[ 2], S22, 0xfcefa3f8); /* 30 */
	GG (c, d, a, b, x[ 7], S23, 0x676f02d9); /* 31 */
	GG (b, c, d, a, x[12], S24, 0x8d2a4c8a); /* 32 */

	/* Round 3 */
	HH (a, b, c, d, x[ 5], S31, 0xfffa3942); /* 33 */
	HH (d, a, b, c, x[ 8], S32, 0x8771f681); /* 34 */
	HH (c, d, a, b, x[11], S33, 0x6d9d6122); /* 35 */
	HH (b, c, d, a, x[14], S34, 0xfde5380c); /* 36 */
	HH (a, b, c, d, x[ 1], S31, 0xa4beea44); /* 37 */
	HH (d, a, b, c, x[ 4], S32, 0x4bdecfa9); /* 38 */
	HH (c, d, a, b, x[ 7], S33, 0xf6bb4b60); /* 39 */
	HH (b, c, d, a, x[10], S34, 0xbebfbc70); /* 40 */
	HH (a, b, c, d, x[13], S31, 0x289b7ec6); /* 41 */
	HH (d, a, b, c, x[ 0], S32, 0xeaa127fa); /* 42 */
	HH (c, d, a, b, x[ 3], S33, 0xd4ef3085); /* 43 */
	HH (b, c, d, a, x[ 6], S34,  0x4881d05); /* 44 */
	HH (a, b, c, d, x[ 9], S31, 0xd9d4d039); /* 45 */
	HH (d, a, b, c, x[12], S32, 0xe6db99e5); /* 46 */
	HH (c, d, a, b, x[15], S33, 0x1fa27cf8); /* 47 */
	HH (b, c, d, a, x[ 2], S34, 0xc4ac5665); /* 48 */

	/* Round 4 */
	II (a, b, c, d, x[ 0], S41, 0xf4292244); /* 49 */
	II (d, a, b, c, x[ 7], S42, 0x432aff97); /* 50 */
	II (c, d, a, b, x[14], S43, 0xab9423a7); /* 51 */
	II (b, c, d, a, x[ 5], S44, 0xfc93a039); /* 52 */
	II (a, b, c, d, x[12], S41, 0x655b59c3); /* 53 */
	II (d, a, b, c, x[ 3], S42, 0x8f0ccc92); /* 54 */
	II (c, d, a, b, x[10], S43, 0xffeff47d); /* 55 */
	II (b, c, d, a, x[ 1], S44, 0x85845dd1); /* 56 */
	II (a, b, c, d, x[ 8], S41, 0x6fa87e4f); /* 57 */
	II (d, a, b, c, x[15], S42, 0xfe2ce6e0); /* 58 */
	II (c, d, a, b, x[ 6], S43, 0xa3014314); /* 59 */
	II (b, c, d, a, x[13], S44, 0x4e0811a1); /* 60 */
	II (a, b, c, d, x[ 4], S41, 0xf7537e82); /* 61 */
	II (d, a, b, c, x[11], S42, 0xbd3af235); /* 62 */
	II (c, d, a, b, x[ 2], S43, 0x2ad7d2bb); /* 63 */
	II (b, c, d, a, x[ 9], S44, 0xeb86d391); /* 64 */

	_state[0] += a;
	_state[1] += b;
	_state[2] += c;
	_state[3] += d;
}

/* Encodes input (_ulong) into output (_byte). Assumes length is a multiple of 4. */
void MD5::encode(const _ulong *input, _byte *output, size_t length) {
	for(size_t i=0, j=0; j<length; i++, j+=4) {
		output[j]= (_byte)(input[i] & 0xff);
		output[j+1] = (_byte)((input[i] >> 8) & 0xff);
		output[j+2] = (_byte)((input[i] >> 16) & 0xff);
		output[j+3] = (_byte)((input[i] >> 24) & 0xff);
	}
}

/* Decodes input (_byte) into output (_ulong). Assumes length is a multiple of 4. */
void MD5::decode(const _byte *input, _ulong *output, size_t length) {
	for(size_t i=0, j=0; j<length; i++, j+=4) {
		output[i] = ((_ulong)input[j]) | (((_ulong)input[j+1]) << 8) | 
			(((_ulong)input[j+2]) << 16) | (((_ulong)input[j+3]) << 24);
	}
}

/*
 * 内存块重置: 将 s 中前 n 个字节用 c 替换并返回 s .
 * （实现采用的是c库的源码，目的是为了平台无关）
 * @param s 内存块
 * @param c 替换字符
 * @param n 替换长度(字节数)
 * @return 替换后的内存块
 */
void *(MD5::memset) (void *s, int c, size_t n)
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
void *MD5::memcpy(void *dst, const void *src, size_t len)
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

/*
 * 把生成的MD5码从ASCII形式转换为16进制形式.
 * Convert digest to string value.
 * 
 * @return 16进制形式的MD5码(string类型)
 */
const string MD5::toString() {
	return bytesToHexString(digest(), 16);
}

/* Convert _byte array to hex string. */
string MD5::bytesToHexString(const _byte *input, size_t length) {
	string str;
	str.reserve(length << 1);
	for(size_t i = 0; i < length; i++) {
		int t = input[i];
		int a = t / 16;
		int b = t % 16;
		str.append(1, HEX[a]);
		str.append(1, HEX[b]);
	}
	return str;
}

/*
 * 把生成的MD5码从ASCII形式转换为16进制形式.
 * 
 * @return 16进制形式的MD5码(char*类型)
 */
const char* MD5::toCharArray() {
	const string strMD5 = toString();
	const char* tmpAryMd5 = strMD5.c_str();	// stack, 非持久保存, 无法作为返回值

	char* aryMD5 = new char[33];	// heap, 持久保存, delete才释放
	for(int i = 0; i < 32; i++) {
		aryMD5[i] = tmpAryMd5[i];
	}
	aryMD5[32] = '\0';
	return aryMD5;
}
 