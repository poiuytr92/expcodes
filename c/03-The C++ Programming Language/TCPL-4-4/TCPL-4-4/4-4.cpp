//打印'a'~'z'，'0'~'9'以及它们对应的整型值

#include<iostream>
using namespace std;
int main(void)
{
	char num=48;
	char letter=97;
	int i,j;
	for(i=0;i<=26;i++,letter++)
		cout<<letter<<"==>"<<(int)letter<<endl;
	for(j=0;j<=9;j++,num++)
		cout<<num<<"==>"<<(int)num<<endl;

	system("pause");
	return 0;
}