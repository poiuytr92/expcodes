package exp.libs.algorithm.heuristic;

/*
 * 作者：LCQiong
 * 问题：N皇后的棋盘排列
 * 使用的方法：人工智能——启发式修补法；
 * 步骤：1、随机生成每一行皇后的位子；
 *       2、计算一行中，每个位子受到其他皇后的攻击数；
 *       3、选取最小攻击数的位子放置皇后；
 *       4、循环2-3步，直到将N个皇后全部遍历一遍；
 *       5、检查结果是否符合要求；如果不符合要求，则重新由第一步做起；
 *       6、输出结果；
 */
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class EQueen extends JFrame{
    /** serialVersionUID */
	private static final long serialVersionUID = 1396025336657144800L;
	private static  int iniqueen=0;//皇后的个数；
    private static  int[][] queens;//棋盘的格子（数组）；
    private static  int seep=25;//随机数的种子值；
    private JTextArea textArea;
    /**
     * 主函数
     * @param args 
     */
    public static void main(String args[]) {
      try {
       EQueen frame = new EQueen();
       frame.setVisible(true);
      } catch (Exception e) {
       e.printStackTrace();
      }
     }
    
    /**
     * 生成界面信息；
     */
    public EQueen(){
          super("N皇后问题（使用启发式修改法）");
          getContentPane().setLayout(null);
          setBounds(150, 150, 500, 300);
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          //label标签；
          final JLabel label_1 = new JLabel();
          label_1.setText("请输入皇后的个数:");
          label_1.setBounds(15, 15, 120, 20);
          getContentPane().add(label_1);
          //textFeil文本框；
          final JTextField textfield_1 =new JTextField(20);
          textfield_1.setBounds(135,15,250,22);
          getContentPane().add(textfield_1);
//          iniqueen=Integer.parseInt(textfield_1.getText());
          
          //按钮：点击按钮，输出棋盘
          final JButton button = new JButton();
          button.addActionListener(new ActionListener() {
           public void actionPerformed(final ActionEvent e) {
//            iniqueen=Integer.parseInt(textfield_1.getText()); 
//            queens=new int[iniqueen][iniqueen];
            EQueen eq = new EQueen();
            iniqueen=Integer.parseInt(textfield_1.getText()); 
            queens=new int[iniqueen][iniqueen];
            eq.Qaction();
//            iniqueen=Integer.parseInt(textfield_1.getText());
            String str="";
            for(int i=0;i<iniqueen;i++){
                for(int j=0;j<iniqueen;j++){
                    if(queens[i][j]==1){
                       str+="  ■"; 
                    }
                    else{
                        str+="  □";
                    }
                }
                str+="\n"; 
            }
         textArea.setText(str);
           }
          });
          button.setText("求值");
          button.setBounds(400, 15, 75, 20);
          getContentPane().add(button);

         //JPanel:画板；
          final JPanel panel = new JPanel();
          panel.setLayout(new BorderLayout());
          panel.setBounds(16, 45, 462, 220);
          getContentPane().add(panel);

          final JScrollPane scrollPane = new JScrollPane();
          panel.add(scrollPane, BorderLayout.CENTER);

          textArea = new JTextArea();
          scrollPane.setViewportView(textArea);
    }
    
    /**
     * 随机生成皇后在每行的位子；
     * reture:queens;/返回一组数组；
     */
    private int[][] iniqueens(){
        //初始化数组，将数组全部的值设置成0；
         for(int i=0;i<iniqueen;i++)
         {
             for(int j=0;j<iniqueen;j++)
             {
                queens[i][j]=0; 
             }
         }
         //随机生成皇后的位子，皇后的位子值设置成1；
         Random rand =new Random(seep);
        for(int i=0; i <iniqueen;i++){
//            Random rand =new Random(8);
           int j= rand.nextInt(iniqueen);
           queens[i][j]=1;
        }
        return queens;
    }
    /**
     * 计算每个格的受到其他皇后攻击的次数；
     * return:num;/次数；
     */
    private int attacks(int i,int j){
        int num=0;//用于记录被其他皇后攻击的次数；
        int b=0;//记录对角线和反对角线的函数位移值；
        //同一列中，是否存在其他皇后攻击；
        for (int k=0;k<iniqueen;k++){
            if (queens[k][j]==1 && k!=i)
            {
                num++;
            }
        }
        //y=kx+b,计算斜率k=1的情况，是否存在其他皇后攻击
        b=j-i;
        for (int x=0;x<iniqueen;x++){
            int y=x+b;
            if(x<iniqueen && y>=0 && y<iniqueen && x!=i){
                if(queens[x][y]==1){
                    num++;
                }
            }
        }
        //y=kx+b,计算斜率k=-1的情况，是否存在其他皇后攻击 
        b=j+i;
        for (int x=0;x<iniqueen;x++){
            int y=(-1)*x+b;
            if(x<iniqueen && y>=0 && y<iniqueen && x!=i){
                if(queens[x][y]==1){
                    num++;
                }
            }
        }     
        return num;
    }
    /**
     * 选择一行中最小攻击次数的格子，设置成皇后；
     */
    private void setqueens(int i){
        int count=0;//用于记录格子的被攻击次数；
        int small=iniqueen*iniqueen;//记录最小攻击次数；
        int p=0;//记录最小攻击测试的列坐标；
        for(int j=0;j<iniqueen;j++){
            queens[i][j]=0;
            count=this.attacks(i,j);
            if(small>count){
                small=count;
                p=j;
            }
        }
        queens[i][p]=1;
    }
    
    /**
     * 校验生成的结果是否符合要求；
     * return:boolen;//返回成功或不成功；
     */
    private boolean Qcheakout(){
        boolean lab=true;
        loop:for(int i=0;i<iniqueen;i++){
                for(int j=0;j<iniqueen;j++){
                    if(queens[i][j]==1){
                        if(this.attacks(i, j)!=0){
                           lab=false;
                           break loop;
                        }  
                    }
                }
            }
       return lab;
    }
    /**
     * 执行皇后程序
     */
    private void Qaction(){
        if(this.iniqueen>0 && this.iniqueen<1000){
            do{
                this.iniqueens();
                for(int i=0;i<this.iniqueen;i++){
                    this.setqueens(i);
                   
                }
                seep+=3;//种子值需要跟随重算而重新设置；
            }while(this.Qcheakout()==false);
            
        }
        else{
           JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "输入的皇后个数需在1~1000以内！", "提示",JOptionPane.INFORMATION_MESSAGE); 
        }
            
    }
}

