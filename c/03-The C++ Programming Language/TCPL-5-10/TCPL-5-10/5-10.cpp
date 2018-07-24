//分别在主函数和自定义函数中打印字符串数组

#include<iostream>
using namespace std;

void print(char* pm[])
{
	int j;
	for(j=0;j<12;j++)
		cout<<pm[j]<<' ';
	cout<<endl;
	return;
}

int main()
{
	char* month[12]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	int i;
	for(i=0;i<12;i++)
		cout<<month[i]<<' ';
	cout<<endl;
	cout<<endl;
	cout<<endl;
	print(month);

	system("pause");
	return 0;
}