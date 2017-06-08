package exp.libs.algorithm.tsp.qant.bean;

import java.util.Arrays;

import exp.libs.algorithm.tsp.qant.QACA;
import exp.libs.utils.pub.RandomUtils;

/**
 * 量子蚂蚁
 */
public class QAnt {

	private int generation;	//第generation代的量子蚂蚁
	
	private QPA[][] qtpa;	//qtpa[i][j]: city[i]->city[j]路径信息素的概率幅
	
	private int nowCityNo;	//蚂蚁当前所在的城市编号
	
	private boolean[] tabuCity;	//城市禁忌表

	private int tabuCnt;
	
	private int nCity;		//tmp: 城市规模

	public QAnt() {
		
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public int getNowCityNo() {
		return nowCityNo;
	}

	public void setNowCityNo(int nowCityNo) {
		this.nowCityNo = nowCityNo;
	}

	public void addTabuCity(int cityId) {
		if(tabuCity[cityId] == false) {
			tabuCity[cityId] = true;
			tabuCnt++;
		}
	}

	public QPA[][] getQtpa() {
		return qtpa;
	}

	//初始化量子蚂蚁
	public void initAnt(int nCity) {
		this.nCity = nCity;

		//初始化每只蚂蚁携带的路径信息素，初值为1/√2
		double val = (double) (1.0/Math.sqrt(2.0D));
		this.qtpa = new QPA[nCity][nCity];
		for(int i=0; i<nCity; i++) {
			for(int j=0; j<nCity; j++) {
				qtpa[i][j] = new QPA();
				qtpa[i][j].setAlpha(val);
				qtpa[i][j].setBeta(val);
			}
		}

		//分配蚂蚁禁忌表空间
		this.tabuCity = new boolean[nCity];
		resetTabuCity();
	}

	//获取cityA->cityB的路径信息素浓度: τ(cityA, cityB) = (qtpa[cityA][cityB].beta)^2
	public double getTau(int cityA, int cityB) {
		double beta2 = qtpa[cityA][cityB].getBeta() * qtpa[cityA][cityB].getBeta();
		return beta2;
	}

	//蚂蚁根据转移规则选择下一个城市
	public int selectNextCity(double[][] eta, int srcId, int snkId) {
		int selectCityNo = -1; 
		int randNum = RandomUtils.randomInt() % 11;

		if(randNum < QACA.SRAND_CRIVAL)
		{
			double argmax = -1;
			for(int nextCityNo=0; nextCityNo<nCity; nextCityNo++)
			{
				if(isTabu(nextCityNo, eta, srcId, snkId))
					continue;

				double tmpVal = Math.pow(getTau(nowCityNo, nextCityNo), QACA.ZETA) * 
						Math.pow(eta[nowCityNo][nextCityNo], QACA.GAMMA);
				if(argmax <= tmpVal) 
				{
					argmax = tmpVal;
					selectCityNo = nextCityNo;
				}
			}
		}
		else 
		{
			double frand = ((double) (RandomUtils.randomInt()%11))/11.0;
			double sumVal = 0;
			double perVal = 0;
			int nextCityNo;

			for(nextCityNo=0; nextCityNo<nCity; nextCityNo++)
			{
				if(isTabu(nextCityNo, eta, srcId, snkId))
					continue;

				sumVal += Math.pow(getTau(nowCityNo, nextCityNo), QACA.ZETA) * 
						Math.pow(eta[nowCityNo][nextCityNo], QACA.GAMMA);
			}

			for(nextCityNo=0; nextCityNo<nCity; nextCityNo++) {
				if(isTabu(nextCityNo, eta, srcId, snkId))
					continue;

				perVal += (Math.pow(getTau(nowCityNo, nextCityNo), QACA.ZETA) * 
						Math.pow(eta[nowCityNo][nextCityNo], QACA.GAMMA)) / sumVal;
				selectCityNo = nextCityNo;
				if(perVal >= frand)
					break;
			}
		}
		return selectCityNo;
	}

	private boolean isTabu(int cityId, double[][] eta, int srcId, int snkId) {
		boolean isTabu = false;
		if(tabuCity[cityId]) {
			isTabu = true;
		} else if((cityId == srcId || cityId == snkId) && tabuCnt < nCity - 1) {
			isTabu = true;
		} else if(QACA.isZero(eta[nowCityNo][cityId])) {
			isTabu = true;
		}
		return isTabu;
	}
	
	//计算量子旋转门的旋转角θ
	public double getTheta(double beta2, int cityA, int cityB, QPA[][] bestPheromone) {
		double ratio = generation / QACA.MAX_GENERATION;
		double theta = (QACA.MAX_THETA - QACA.DELTA_THETA * ratio) * 
				beta2 * getThetaDirection(cityA, cityB, bestPheromone);
		return theta;
	}

	//计算旋转角θ的旋转方向
	public int getThetaDirection(int cityA, int cityB, QPA[][] bestPheromone) {
		double dbest = bestPheromone[cityA][cityB].getBeta() / bestPheromone[cityA][cityB].getAlpha();
		double dnow = qtpa[cityA][cityB].getBeta() / qtpa[cityA][cityB].getAlpha();
		double psiBest = Math.atan(dbest);
		double psiNow = Math.atan(dnow);
		int direction = -1;

		if(((dbest/dnow)*(psiBest-psiNow)) >= 0)
			direction = 1;

		return direction;
	}

	//使用量子旋转门更新cityA->cityB的路径信息素
	public void updateQTPA(int cityA, int cityB, double theta) {
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		double alphaTmp = qtpa[cityA][cityB].getAlpha();
		double betaTmp = qtpa[cityA][cityB].getBeta();

		qtpa[cityA][cityB].setAlpha(cosTheta*alphaTmp - sinTheta*betaTmp);
		qtpa[cityA][cityB].setBeta(sinTheta*alphaTmp + cosTheta*betaTmp);
		
		qtpa[cityB][cityA].setAlpha(qtpa[cityA][cityB].getAlpha());
		qtpa[cityB][cityA].setBeta(qtpa[cityA][cityB].getBeta());
	}

	/*
	 * 挥发非preCityNo->curCityNo和非curCityNo->nextCityNo路径的路径信息素
	 * 当蚂蚁选择了路径curCityNo->nextCityNo时
	 * 以curCityNo为起点，非nextCityNo为终点的其他路径的信息素将自然挥发
	 * 由于路径信息素是对称，因此curCityNo->preCityNo的路径信息素也不能挥发
	 */
	public void updateOtherQTPA(int preCityNo, int curCityNo, int nextCityNo, double theta) {
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);

		for(int j=0; j<nCity; j++)
		{
			if(j==preCityNo || j==nextCityNo)
				continue;
			
			double alphaTmp = qtpa[curCityNo][j].getAlpha();
			double betaTmp = qtpa[curCityNo][j].getBeta();

			qtpa[curCityNo][j].setAlpha(cosTheta*alphaTmp - sinTheta*betaTmp);
			qtpa[curCityNo][j].setBeta(sinTheta*alphaTmp + cosTheta*betaTmp);

			qtpa[j][curCityNo].setAlpha(qtpa[curCityNo][j].getAlpha());
			qtpa[j][curCityNo].setBeta(qtpa[curCityNo][j].getBeta());
		}
	}

	//变异处理：量子交叉
	public void qtCross() {
		for(int i=0; i<nCity; i++)
		{
			int k = ((i+1)==nCity)?0:i+1;
			for(int j=0; j<=i; j++)
			{
				double tmp = qtpa[i][j].getBeta();
				qtpa[i][j].setBeta(qtpa[i][j].getAlpha());
				qtpa[i][j].setAlpha(tmp);

				qtpa[j][i].setBeta(qtpa[i][j].getBeta());
				qtpa[j][i].setAlpha(qtpa[i][j].getAlpha());			}
		}
	}

	//重置蚂蚁禁忌表
	public void resetTabuCity() {
		this.tabuCnt = 0;
		Arrays.fill(tabuCity, false);
	}
	
	//释放量子蚂蚁占用的内存
	public void destoryAnt() {
		
	}
		
}
