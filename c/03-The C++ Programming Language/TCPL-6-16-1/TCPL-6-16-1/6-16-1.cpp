//类型转换!!
//对于对于十进制、八进制、十六进制形式的字符串，输入的字符串是哪种进制形式，输出也是那种进制形式

#include<iostream>
#include<sstream>   //istringstream
#include<cctype>     //isdigit,isalpha
#include<iomanip>    //hex,oct,dec,showbase,uppercase
using namespace std;
void ATOI(const char* pc)
{
	int num;
	istringstream iss(pc);
	if(*pc=='0'&&isdigit(*(pc+1)))        //对于十进制、八进制、十六进制，0值都是一样的，所以这里为了方便输出，把0归入八进制
	{                                         ///isdigit(ch)判断ch是否为数字，是返回非零，否返回零
		iss>>oct>>num;
		cout<<showbase<<oct<<num<<endl;
	}
	else if(*pc=='0'&&isalpha(*(pc+1)))        //isalpha(ch)判断ch是否为字母，是返回非零，否返回零
	{	
		iss>>hex>>num;
		cout<<uppercase<<showbase<<hex<<num<<endl;
	}
	else
	{
		iss>>dec>>num;
		cout<<num<<endl;
	}
	iss.str("");                      //清空iss存储的流
	return;
}
int main(void)
{
	char str[10];
	cin>>str;
	ATOI(str);
	
	system("pause");
	return 0;
}

