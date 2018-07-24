//Hello,name !º¯Êý

#include<iostream>
#include<string>
using namespace std;
void Hello_name(string name)
{
	getline(cin,name);
	cout<<"Hello,"<<name<<'!'<<endl;
	return;
}
int main(void)
{
	cout<<"What's your name,please?"<<endl;
	string str;
	Hello_name(str);

	system("pause");
	return 0;
}
