//类型转换!!
//对于对于十进制、八进制、十六进制形式的字符串，无论输入的字符串是哪种进制形式，输出均转换为十进制值

#include<iostream>
#include<sstream>   //istringstream
#include<cctype>     //isdigit,isalpha
#include<iomanip>    //hex,oct,dec
using namespace std;
int ATOI(const char* pc)
{
	int num;
	istringstream iss(pc);
	if(*pc=='0'&&isdigit(*(pc+1)))        //对于十进制、八进制、十六进制，0值都是一样的，所以这里为了方便输出，把0归入八进制
		iss>>oct>>num;                        //isdigit(ch)判断ch是否为数字，是返回非零，否返回零
	else if(*pc=='0'&&isalpha(*(pc+1)))	    //isalpha(ch)判断ch是否为字母，是返回非零，否返回零
		iss>>hex>>num;
	else
		iss>>dec>>num;
	iss.str("");                      //清空iss存储的流
	return num;
}
int main(void)
{
	char str[10];
	cin>>str;
	cout<<ATOI(str)<<endl;

	system("pause");
	return 0;
}