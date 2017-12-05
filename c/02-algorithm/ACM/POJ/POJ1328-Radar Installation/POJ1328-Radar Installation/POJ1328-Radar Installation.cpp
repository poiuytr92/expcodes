/*
	Author:     Exp
	Date:       2017-11-30
	Code:       POJ 1328
	Problem:    Radar Installation
	URL:		http://poj.org/problem?id=1328
*/

/*
	题意分析：
	  给定一个直角坐标系，定义x轴为海岸线，海岸线上方是海，下方是陆地.
	  在海域零星分布一些海岛, 需要要在海岸线上安装若干个雷达覆盖这些海岛,
	  每个雷达的覆盖半径都是相同且固定的.

	  现在给定所有海岛的坐标(x,y), 以及雷达的覆盖半径d，
	  问可以覆盖所有海岛的最小雷达数.


	解题思路：
	  首先可以明确的是：只要存在任意一个海岛位置超出雷达最大覆盖半径（即y>d），则无解.

	  在所有海岛的 y<=d 的前提下去思考此问题，
	  那么此问题的切入点是需要知道【一个雷达要覆盖一个海岛，其可以安装范围是多少】

	  以海岛坐标(x,y)为圆心，用雷达覆盖半径d画圆，根据前提条件可知，
	  这个圆与x轴必定存在最少1个(y=d)、最多2个交点(y<d).

	  可以认为1个交点是2个交点重合的特殊情况，那么这2个交点在x轴上构成的线性区间，
	  可以看作海岛的被覆盖范围在x轴上的投影 (由此就可以把二维的几何问题转化成一维几何问题)

	  按照所有海岛的x轴坐标，从小到大依次计算每个海岛在x轴上的投影区间（投影可能存在重叠），
	  同时把每个雷达抽象成1个点，那么此问题就转化成：

	  【已知x轴上若干个区间（可能存在交集），现在要往这些区间内放若干个点，
	  问怎样放置这些点，使得每个区间内至少存在一个点，且所放置的点的总量尽可能最少】
	
	  
	  可使用贪心算法求解：
	    根据这n个海岛的x坐标顺序从小到大遍历对应的区间:
		① 在第i个区间的右端点 R(i) 放置一个点，总放置点数 P+1 , 其中i∈[1, n]
		② 令j=i
		③ 令j++
		④ 若第j个区间的左端点 L(j)<=R(i)，则转③，否则转⑤
		⑤ 令i=j，转①
*/


#include <iostream>
using namespace std;



int main(void) {

	return 0;
}






//Memory Time 
//288K   110MS 


#include<iostream>
#include<math.h>
using namespace std;

const int island_max=1000;

int main(void)
{
	int i,j;
	double x[island_max],y[island_max];
	double num,rad;
	for(;;)
	{
		/*Input end*/
		cin>>num>>rad;
		if(!(num&&rad))break;

		static int count=1;  //记录case数目

		/*read in coordinate*/
		bool flag=false;
		for(i=0;i<num;i++)
		{
			cin>>x[i]>>y[i];
			if(y[i]>rad)
				flag=true;
		}

		/*case fail*/
		if(flag)
		{
			cout<<"Case "<<count++<<": -1"<<endl;
			continue;
		}

		/*bubble sort*/
		//这里由于y要随x连带排序，不能简单地使用 快排qsort
		double temp;
		for(i=0;i<num-1;i++)
			for(j=0;j<num-i-1;j++)
				if(x[j]>x[j+1])
				{
					temp=x[j];
					x[j]=x[j+1];
					x[j+1]=temp;
					temp=y[j];
					y[j]=y[j+1];
					y[j+1]=temp;
				}

				double left[island_max],righ[island_max];  //海岛圆在海岸线上的左右交点
				for(i=0;i<num;i++)
				{
					left[i]=x[i]-sqrt(rad*rad-y[i]*y[i]);
					righ[i]=x[i]+sqrt(rad*rad-y[i]*y[i]);
				}

				int radar=1;
				for(i=0,temp=righ[0];i<num-1;i++)
					if(left[i+1]>temp)
					{
						temp=righ[i+1];
						radar++;
					}
					else if(righ[i+1]<temp)
						temp=righ[i+1];

				cout<<"Case "<<count++<<": "<<radar<<endl;
	}
	return 0;
}