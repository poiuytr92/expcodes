//两种等价循环

#include<iostream>
#include<string>
using namespace std;
int main(void)
{
	char str[]="?hgju? du??76?=098dd?jnmcn??";
	char* input_line=str;
	int max_length=strlen(str);
	int i,quest_count=0;
	//原题程序
	for(i=0;i<max_length;i++)
		if(input_line[i]=='?')
			quest_count++;
	cout<<"原题程序：quest_count="<<quest_count<<endl;
	quest_count=0;
	//重写程序
	i=0;
	while(i<max_length)
	{
		if(input_line[i]=='?')
			quest_count++;
		i++;
	}
	cout<<"重写程序：quest_count="<<quest_count<<endl;

	system("pause");
	return 0;
}
