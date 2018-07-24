//比较指针和下标迭代 产生的结果

#include<iostream>
using namespace std;
int main()
{
	int a[5]={9,6,4,3,2};
	int* p;
	int i;
	for(i=0,p=a;i<5,p<a+5;i++,p++)
	{
		cout<<"address: &a["<<i<<"]-->"<<&a[i]<<"      p+"<<i<<"-->"<<p<<endl;
		cout<<"number : a["<<i<<"]-->"<<a[i]<<"            *(p+"<<i<<")-->"<<*p<<endl;
		cout<<"============================================="<<endl;
	}

	system("pause");
	return 0;
}

