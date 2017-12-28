#include<iostream>
using namespace std;
#include<stdlib.h>
#include<ctime>
#define N 8
#define MAX 65536
int Queen[N];
int Evaluation[N];	
int __main()
{
    int sum=1;
    bool flag=true;
    int min=N+1;
    int CurrentPosition=0;
    srand((unsigned int)time(0));
    int count=0;
    while(sum!=0 && count < 20)
    {
        cout<<"number "<<count++<<endl;
        flag=true;
        for(int i=0;i<N;i++)
        {
            Queen[i]=rand()%N;	// 预选n个皇后的位置
        }

        while(flag)
        {
            sum=0;
            flag=false;	// lqb: 标记本次是否发生位置修正


            for(int i=0;i<N;i++)//对于每列
            {
                min=MAX;
                for(int ii=0;ii<N;ii++)//该列冲突书清零
                {
                    Evaluation[ii]=0;
                }

				// lqb: 遍历所有皇后，计算当前行所有格子的冲突数, 取冲突数最小的一格
                for(int l=0;l<N;l++)//对于第i列的第l个格子
                {
                    for(int j=0;j<N;j++)//对于第j个皇后
                    {
                        if(j==i)//第i列中的第i个皇后对于当前列的冲突不用计算 ????
                            continue;


						// lqb:  同列格与斜率格 冲突数+1
                        if(Queen[j] == l)
                            Evaluation[l]++;

                        else if((Queen[j]-l) / (j-i) == 1 || (Queen[j]  -l) / (j-i) == -1)
                            Evaluation[l]++;
                    }

                    //cout<<Evaluation[l];
                    if(Evaluation[l]<min)
                    {
                        min=Evaluation[l];
                        CurrentPosition=l;
                    }

					// 当存在两个相同的最小值时，通过随机数判断取历史还是取当前
                    if(Evaluation[l]==min && rand()%2)//程序中的随机性
                    {
                        min=Evaluation[l];
                        CurrentPosition=l;
                    }
                }


				// lqb: 修正第i个皇后的位置
                //cout<<endl;
                if(Queen[i]!=CurrentPosition)
                {
                    Queen[i]=CurrentPosition;
                    sum+=Evaluation[CurrentPosition];
                    flag=true;
                }
                else
                {
                    sum+=Evaluation[Queen[i]];
                }
            }
            //cout<<endl;
        }
        for(int i=0;i<N;i++)
        {
            cout<<Queen[i]<<" ";
        }
        cout<<endl;
		cout<<sum << endl;
    }
    //cout<<sum;
    cout<<endl;
	system("pause");
	return 0;
}