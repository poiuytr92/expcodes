//分别统计string字符串和char字符数组中某个 字符对 出现的频率,区别两种字符串

#include<iostream>
#include<string>
using namespace std;

void style_Cpp(void)
{
	int i,count=0;
	string str;       //可以认为string定义的其实就是一个指向字符串地址的“指针”
	string let;       //但是这个指针值既不能赋值给同类型的string“指针”，更不能赋值给char*指针
	cout<<"Please input a string:"<<endl;
	cin>>str;
	cout<<"please input a pair of letters:"<<endl;
	cin>>let;
	cout<<endl;
	for(i=0;i<sizeof(str);i++)
		if(let[0]==str[i])     //可以通过下标找到string“指针”所指向字符串中的具体某个字符
			if(let[1]==str[i+1])
			{
				count++;
				if(let[0]==let[1])  //避免字符对为叠字时，字符串刚好又存在3个以上叠字的重复数数的情况
					i++;            //如果没有这个判断，那么判断诸如"abcccd"中"cc"的个数时，会判断为2个
			}
			cout<<"There are(is) "<<count<<" '"<<let<<"' int the string '"<<str<<"'."<<endl;
			//	cout<<sizeof(str)<<endl;  //所定义的string字符串，默认串长最大为16个字符
			return;
}

void style_C(void)
{
	int i,count=0;
	char stri[30],lett[2];
	cout<<"Please input a string:"<<endl;
	gets(stri);
	cout<<"please input a pair of letters:"<<endl;
	gets(lett);
	cout<<endl;
	for(i=0;stri[i]!='\0';i++)
		if(lett[0]==stri[i])
			if(lett[1]==stri[i+1])
			{
				count++;
				if(lett[0]==lett[1])
					i++;
			}
			cout<<"There are(is) "<<count<<" '"<<lett<<"' int the string '"<<stri<<"'."<<endl;
			return;
}

int main()
{
	cout<<"=====================style_Cpp====================="<<endl;
	style_Cpp();

	getchar();
	//两种风格函数单独运行都是没有问题
	//但当放到一个文件时，若C++先执行，要注意C风格字符数组的赋值特点
	//必须用这条语句把C++最后一次输入let的回车符“吃掉”，不然C中的数组S就存放了回车符，不能再对其输入字符串了
	cout<<endl;

	cout<<"======================style_C======================"<<endl;
	style_C();
	cout<<endl;

	//当C风格先执行时，这里无需使用getchar()“吃掉”回车符，可见C++的string是经过优化的
	cout<<"=====================style_Cpp====================="<<endl;
	style_Cpp();
	cout<<endl;

	system("pause");
	return 0;
}


