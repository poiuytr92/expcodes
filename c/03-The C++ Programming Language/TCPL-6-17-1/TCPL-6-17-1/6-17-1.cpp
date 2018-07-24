//类型转换!!
//建立整型数据的string字符串

#include<iostream>
#include<string>
#include<sstream>
using namespace std;
string ITOA(int i,string b)
{
	ostringstream oss;
	oss<<i;
	b=oss.str();
	return b;
}
int main(void)
{
	int num;
	string a;
	cin>>num;
	cout<<ITOA(num,a)<<endl;

	system("pause");
	return 0;
}
