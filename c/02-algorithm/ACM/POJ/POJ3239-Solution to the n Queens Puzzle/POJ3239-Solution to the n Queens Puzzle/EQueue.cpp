#include<iostream>
using namespace std;
#include<stdlib.h>
#include<ctime>
#define N 1000
#define MAX 65536
int Queen[N];
int Evaluation[N];
int main(void)
{
    int sum=1;
    bool flag=true;
    int min=N+1;
    int CurrentPosition=0;
    srand((unsigned int)time(0));
    int count=0;
    while(sum!=0)
    {
        cout<<"number "<<count++<<endl;
        flag=true;
        for(int i=0;i<N;i++)
        {
            Queen[i]=rand()%N;
        }
        while(flag)
        {
            sum=0;
            flag=false;
            for(int i=0;i<N;i++)//对于每列
            {
                min=MAX;
                for(int ii=0;ii<N;ii++)//该列冲突书清零
                {
                    Evaluation[ii]=0;
                }
                for(int l=0;l<N;l++)//对于第i列的第l个格子
                {
                    for(int j=0;j<N;j++)//对于第j个皇后
                    {
                        if(j==i)//第i列中的第i个皇后对于当前列的冲突不用计算
                            continue;
                        if(Queen[j]==l)
                            Evaluation[l]++;
                        else if((Queen[j]-l)/(j-i)==1||(Queen[j]-l)/(j-i)==-1)
                            Evaluation[l]++;
                    }
                    //cout<<Evaluation[l];
                    if(Evaluation[l]<min)
                    {
                        min=Evaluation[l];
                        CurrentPosition=l;
                    }
                    if(Evaluation[l]==min&&rand()%2)//程序中的随机性
                    {
                        min=Evaluation[l];
                        CurrentPosition=l;
                    }
                }
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
        /*for(int i=0;i<N;i++)
        {
            cout<<Queen[i]<<endl;
        }

        cout<<sum;
        cout<<endl;*/
    }
    //cout<<sum;
    cout<<endl;
}