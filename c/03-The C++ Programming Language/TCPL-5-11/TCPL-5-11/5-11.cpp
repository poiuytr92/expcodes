//读入一系列单词（用空格或回车分隔），当输入Quit时结束，并按字母表顺序不重复地打印这些单词

#include<iostream>
#include<string>
using namespace std;
int main()
{
	string temp;
	string word[20];               //建立字符串数组
	int i,j,count=0;
	cout<<"Please input some words.\nIf you finish ,input 'Quit'."<<endl;
	cout<<"======================================================"<<endl;
	cout<<"--> ";
	for(i=0;word[i++]!="Quit";)           //输入一系列单词，存放在字符串数组中，当判定输入的单词为Quit时结束输入
	{
		cin>>word[i];
		count++;
	}
	for(i=0;i<count-1;i++)             //按照ASCII码对输入的单词进行冒泡排序
		for(j=0;j<count-1-i;j++)
			if(word[j]>word[j+1])
			{
				temp=word[j];
				word[j]=word[j+1];
				word[j+1]=temp;
			}
			for(i=0;i<count;i++)                   //对于相同的单词，把后一个单词重新赋值为"**"
				for(j=i+1;j<count;j++)
					if(word[i]==word[j])
						word[j]="**";                  //注意虽然字符串允许输入单个字符，但不允许用单个字符去赋值，因此不采用'*'重新赋值
			cout<<endl;
			cout<<endl;
			cout<<"======================================================"<<endl;
			cout<<"Now print your words:"<<endl;              //打印所输入的一系列单词，遇到单词为"**"或"Quit"则跳过，相当于不打印重复单词
			cout<<"--> ";
			for(i=1;i<count;i++)                              //从i=1开始时因为在输入判定时word[1]被空置了，无需输出
				if(word[i]=="**"||word[i]=="Quit")
					continue;
				else
					cout<<word[i]<<' ';
			cout<<endl;
			cout<<"======================================================"<<endl;

	system("pause");
	return 0;
}
