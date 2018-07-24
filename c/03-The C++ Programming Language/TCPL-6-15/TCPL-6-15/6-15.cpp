#include<iostream>
using namespace std;

//把整型数组from复制到整形数组to,其中count是数组to的长度
//若数组to的长度比数组from的长度 小，则只复制数组from的前count个元素
//若数组to的长度比数组from的长度 大，则循环复制数组from
void send(int* to,int* from,int count)
{
	int n=(count+7)/8;
	switch(count%8){
	case 0: do{*to++=*from++;
	case 7:    *to++=*from++;
	case 6:    *to++=*from++;
	case 5:    *to++=*from++;
	case 4:    *to++=*from++;
	case 3:    *to++=*from++;
	case 2:    *to++=*from++;
	case 1:    *to++=*from++;
			}while(--n>0);
	}
}

int main(void)
{
	const int NUM=5;
	int a[NUM];
	int b[11]={19,18,17,16,15,14,13,12,11,10,9};
	int i;
	send(a,b,NUM);
	for(i=0;i<NUM;i++)
		cout<<a[i]<<' ';
	cout<<endl;

	system("pause");
	return 0;
}
