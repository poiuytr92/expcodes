//打印各种类型的大小

#include<iostream>
#include<iomanip>
using namespace std;

int main(void)
{
	enum Two{};
	enum Three{r,s,t};
	enum Num{m=3,n=4};
	enum Number{a=2,b=5,c=9};
	cout<<"size of char ="<<sizeof(char)<<endl;
	cout<<"size of bool ="<<sizeof(bool)<<endl;
	cout<<"size of int ="<<sizeof(int)<<endl;
	cout<<"size of short int ="<<sizeof(short int)<<endl;
	cout<<"size of long int ="<<sizeof(long int)<<endl;
	cout<<"size of unsigned int ="<<sizeof(unsigned int)<<endl;
	cout<<"size of unsigned short int ="<<sizeof(unsigned short int)<<endl;
	cout<<"size of unsigned long int ="<<sizeof(unsigned long int)<<endl;
	cout<<"size of float ="<<sizeof(float)<<endl;
	cout<<"size of double ="<<sizeof(double)<<endl;
	cout<<"size of long double ="<<sizeof(long double)<<endl;
	// cout<<"size of enum ="<<sizeof(enum)<<endl;			// vs2010不支持c++0x新增的强类型enum class
	//	cout<<"size of void ="<<sizeof(void)<<endl;          //错误的做法，void无大小        
	cout<<"size of int* ="<<sizeof(int*)<<endl;
	cout<<"size of char* ="<<sizeof(char*)<<endl;
	cout<<"size of float* ="<<sizeof(float*)<<endl;
	cout<<"size of double* ="<<sizeof(double*)<<endl;
	cout<<"size of double& ="<<sizeof(double&)<<endl;
	cout<<"size of E-Two ="<<sizeof(Two)<<endl;
	cout<<"size of E-Three ="<<sizeof(Three)<<endl;
	cout<<"size of E-Num ="<<sizeof(Num)<<endl;
	cout<<"size of E-Number ="<<sizeof(Number)<<endl;
	system("pause");
	return 0;
}
