//编写函数rev()使字符串首尾倒置

#include<iostream>
using namespace std;

char* REV(char* p)
{
	char* s=p+strlen(p)-1;           //使s指向p字符串'\0'的前一个字符
	char* t=new char[strlen(p)+1];   //注意存储空间大小，分清为什么上一步是-1，这里是+1
	char* ca=t;                      //目的是使最后返回一个指向处理过的字符串开头的指针
	while((s+1)!=p)                  //由循环体知在复制完p第一个字符后，s指向了p字符串开头的外面，因而用s+1与p比较结束循环
		//p指针自始至终没有移动
		*t++=*s--;
	*t='\0';                         //复制完后t最后是没有'\0'的，必须额外加上
	return ca;
}

int main(void)
{
	char a[]="hjhg46 87!@##$%^*()\t\n+_-==";
	char* q=REV(a);
	cout<<q<<endl;

	system("pause");
	return 0;
}