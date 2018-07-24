//利用struct输入今天的日期（年月日），输出今天的日期

#include<iostream>
using namespace std;
int main()
{
	struct Date
	{
		int year,month,day;
	}date={2010,10,21};        //用一个日期初始化Date
	cout<<"Please input the date:"<<endl;
	cout<<"year-->";
	cin>>date.year;
	cout<<"month-->";
	cin>>date.month;
	cout<<"day-->";
	cin>>date.day;
	cout<<"Today is "<<date.year<<'.'<<date.month<<'.'<<date.day<<endl;
	cout<<endl;

	system("pause");
	return 0;
}
