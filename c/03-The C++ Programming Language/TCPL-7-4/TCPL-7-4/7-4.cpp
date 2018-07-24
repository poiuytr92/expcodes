//多个字符串拼接

#include<iostream>
#include<sstream>
#include<string>
using namespace std;

void CAT(string ss[])
{
	int i;
	ostringstream oss;
	for(i=0;ss[i++]!="Exit";)
	{
		cin>>ss[i];
		if(ss[i]!="Exit")
			oss<<ss[i];
	}
	cout<<oss.str()<<endl;
	oss.str("");
	return;
}
int main(void)
{
	const int num=20;
	string str[num];
	cout<<"请输入要拼接字符串，用空格或回车分隔均可，输入Exit结束输入:"<<endl;
	CAT(str);

	system("pause");
	return 0;
}

