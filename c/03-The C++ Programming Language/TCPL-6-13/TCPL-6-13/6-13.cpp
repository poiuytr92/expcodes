//编写函数cat()用于拼接两个字符串，拼接的字符串放在new开辟的空间

#include<iostream>
using namespace std;

char* CAT(char* s,char* t)
{
	char* p=new char[strlen(s)+strlen(t)];   //不使用new char[strlen(s)+strlen(t)+1]是因为这个程序使得拼接后的字符串长度比原来
	//两个字符串总长之和少1  (少了一个'\0')

	char* ca=p;//这步是建议的，使ca指向new开辟的存储空间开头，若不这样做，最后返回的是一个指向new最后的指针p，会为后面的程序验错带来麻烦
	while(*s)         //这里不能使用while(*p++=*s++); 否则s中的'\0'也会被复制到p，使两个字符串连接后，中间还有一个'\0'
		*p++=*s++;    //目的是使在s最后只移动指针，不复制'\0'
	//另外使用*(p++)=*(s++)也是错误的做法，它是使字符值加1，而不是指针移动（地址加1）
	while(*p++=*t++); //道理同上，这样做目的是使在t最后既移动指针，又复制'\0'
	return ca;        //注意不能返回*ca，*ca是char字符，不是char*指针
}

int main(void)
{
	char a[]="zxcvb 098jfh\n";
	char b[]="%!@#$^&*()oim \tYYk";
	char* q=CAT(a,b);
	cout<<q<<endl;         //cout<<*q<<endl;  致命的错误，只输出第一个字符

	system("pause");
	return 0;
}