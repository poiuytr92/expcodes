//编写和C风格具有相同功能的三个字符串函数 strlen() ， strcpy() , strcmp()

#include<iostream>
#include<cstring>
using namespace std;

int STRLEN(char* p)
{
	int i=0;
	while(*(p++))
		i++;
	return i;
}

void STRCPY(char* s,char* t)
{
	while(*s++=*t++);
	return;
}

int STRCMP(char* m,char* n)
{
	int r;
	while(!(r=*(m++)-*(n++)))
		if(!(*m)||!(*n))break;
	if(r<0)
		return -1;
	else if(r>0)
		return 1;
	else
		return 0;
}

int main(void)
{
	char a[]="abc defg\n";
	char b[20];
	char c[]="zxy";

	cout<<STRLEN(a)<<endl;
	cout<<strlen(a)<<endl;
	cout<<endl;

	STRCPY(b,a);
	cout<<b;
	STRCPY(b," ");
	strcpy(b,a);
	cout<<b<<endl;

	cout<<STRCMP(a,b)<<','<<STRCMP(a,c)<<','<<STRCMP(c,a)<<endl;
	cout<<strcmp(a,b)<<','<<strcmp(a,c)<<','<<strcmp(c,a)<<endl;
	cout<<endl;

	system("pause");
	return 0;
}

