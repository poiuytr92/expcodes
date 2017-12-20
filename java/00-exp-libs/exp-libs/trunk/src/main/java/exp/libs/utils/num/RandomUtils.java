package exp.libs.utils.num;

import java.util.Random;

import exp.libs.utils.other.StrUtils;

/**
 * <PRE>
 * 随机生成器工具.
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-02-02
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class RandomUtils {
	
	/** 随机对象 */
	private final static Random random = new Random();
	
	/** 单姓集  */
	private final static String[][] SINGLE_SURNAME = {
		new String[] {
				"bai", "bai", 
				"cai", "cao", "chen", 
				"dai", "dou", "deng", "di", "du", "duan", 
				"fan", "fan", "fang", "feng", "fu", "fu", 
				"gao", "gu", "guan", "guo", 
				"han", "hu", "hua", "hong", "hou", "huang", 
				"jia", "jiang", "jin", 
				"liao", "liang", "li", "lin", "liu", "long", "lu", "lu", "luo", 
				"ma", "mao", 
				"niu", 
				"ou", "ou", 
				"pang", "pei", "peng", 
				"qi", "qi", "qian", "qiao", "qin", "qiu", "qiu", "qiu", 
				"sha", "shang", "shang", "shao", "shen", "shi", "shi", "song", "sun", 
				"tong", 
				"wan", "wang", "wei", "wei", "wu", "wu", 
				"xiao", "xiao", "xiang", "xu", "xu", "xue", 
				"yang", "yang", "yang", "yi", "yin", "yu", 
				"zhao", "zhong", "zhou", "zheng", "zhu"
		}, 
		new String[] {
				"百", "白", 
				"蔡", "曹", "陈", 
				"戴", "窦", "邓", "狄", "杜", "段", 
				"范", "樊", "房", "风", "符", "福", 
				"高", "古", "关", "郭", 
				"韩", "胡", "花", "洪", "侯", "黄", 
				"贾", "蒋", "金", 
				"廖", "梁", "李", "林", "刘", "龙", "陆", "卢", "罗", 
				"马", "毛", 
				"牛", 
				"欧", "区", 
				"庞", "裴", "彭", 
				"戚", "齐", "钱", "乔", "秦", "邱", "裘", "仇", 
				"沙", "商", "尚", "邵", "沈", "师", "施", "宋", "孙", 
				"童", 
				"万", "王", "魏", "卫", "吴", "武", 
				"萧", "肖", "项", "许", "徐", "薛", 
				"杨", "羊", "阳", "易", "尹", "俞", 
				"赵", "钟", "周", "郑", "朱"
		},
	};
	
	/** 复姓集  */
	private final static String[][] COMPOUND_SURNAME = {
		new String[] {
				"dongfang", "dugu", "murong", "ouyang", 
				"sima", "ximen", "yuchi", "zhangsun", "zhuge"
		}, 
		new String[] {
				"东方", "独孤", "慕容", "欧阳", 
				"司马", "西门", "尉迟", "长孙", "诸葛", 
		},
	};
	
	/** 名字集  */
	public final static String[][] WORD = {
		new String[] {
				"ai", "an", "ao", "ang", 
				"ba", "bai", "ban", "bang", "bei", "biao", "bian", "bu", 
				"cao", "cang", "chang", "chi", "ci", 
				"du", "dong", "dou", 
				"fa", "fan", "fang", "feng", "fu", 
				"gao", 
				"hong", "hu", "hua", "hao", 
				"ji", "jian", 
				"kan", "ke", 
				"lang", "li", "lin", 
				"ma", "mao", "miao", 
				"nan", "niu", 
				"pian", 
				"qian", "qiang", "qin", "qing", 
				"ran", "ren", 
				"sha", "shang", "shen", "shi", "shui", "si", "song", 
				"tang", "tong", "tian", 
				"wan", "wei", "wu", 
				"xi", "xiao", "xiong", 
				"yang", "yi", "yin", "ying", "you", "yu", 
				"zhi", "zhong", "zhou", "zhu", "zhuo", "zi", "zong", "zu", "zuo"
		},
		new String[] {
				"皑艾哀", "安黯谙", "奥傲敖骜翱", "昂盎", 
				"罢霸", "白佰", "斑般", "邦", "北倍贝备", "表标彪飚飙", "边卞弁忭", "步不", 
				"曹草操漕", "苍仓", "常长昌敞玚", "迟持池赤尺驰炽", "此次词茨辞慈", 
				"独都", "东侗", "都", 
				"发乏珐", "范凡反泛帆蕃", "方访邡昉", "风凤封丰奉枫峰锋", "夫符弗芙", 
				"高皋郜镐", 
				"洪红宏鸿虹泓弘", "虎忽湖护乎祜浒怙", "化花华骅桦", "号浩皓蒿浩昊灏淏", 
				"积极济技击疾及基集记纪季继吉计冀祭际籍绩忌寂霁稷玑芨蓟戢佶奇诘笈畿犄", "渐剑见建间柬坚俭", 
				"刊戡", "可克科刻珂恪溘牁", 
				"朗浪廊琅阆莨", "历离里理利立力丽礼黎栗荔沥栎璃", "临霖林琳", 
				"马", "贸冒貌冒懋矛卯瑁", "淼渺邈", 
				"楠南", "牛妞", 
				"片翩", 
				"潜谦倩茜乾虔千", "强羌锖玱", "亲琴钦沁芩矜", "清庆卿晴", 
				"冉然染燃", "仁刃壬仞", 
				"沙煞", "上裳商", "深审神申慎参莘", "师史石时十世士诗始示适炻", "水", "思斯丝司祀嗣巳", "松颂诵", 
				"堂唐棠瑭", "统通同童彤仝", "天田忝", 
				"万宛晚", "卫微伟维威韦纬炜惟玮为", "吴物务武午五巫邬兀毋戊", 
				"西席锡洗夕兮熹惜", "潇萧笑晓肖霄骁校", "熊雄", 
				"羊洋阳漾央秧炀飏鸯", "易意依亦伊夷倚毅义宜仪艺译翼逸忆怡熠沂颐奕弈懿翊轶屹猗翌", "隐因引银音寅吟胤訚烟荫", "映英影颖瑛应莹郢鹰", "幽悠右忧猷酉", "渔郁寓于余玉雨语预羽舆育宇禹域誉瑜屿御渝毓虞禺豫裕钰煜聿", 
				"制至值知质致智志直治执止置芝旨峙芷挚郅炙雉帜", "中忠钟衷", "周州舟胄繇昼", "竹主驻足朱祝诸珠著竺", "卓灼灼拙琢濯斫擢焯酌", "子资兹紫姿孜梓秭", "宗枞", "足族祖卒", "作左佐笮凿"
		}
	};
	
	/** 单字-单姓: 30% */
	private final static int SINGLE_SURNAME_1WORD = 30;
	
	/** 单字-双姓: 5% */
	private final static int TWOSYLLABLE_SURNAME_1WORD = 35;
	
	/** 单字-复姓: 5% */
	private final static int COMPOUND_SURNAME_1WORD = 40;
	
	/** 二字-单姓: 50% */
	private final static int SINGLE_SURNAME_2WORD = 90;
	
	/** 二字-双姓: 5% */
	private final static int TWOSYLLABLE_SURNAME_2WORD = 95;
	
	/** 二字-复姓: 5% */
	private final static int COMPOUND_SURNAME_2WORD = 100;
	
	/** 私有化构造函数 */
	protected RandomUtils() {}
	
	/**
	 * 获取int随机数
	 * @return 返回随机数范围 [0,+∞)
	 */
	public static int randomInt() {
		return random.nextInt();
	}
	
	/**
	 * 获取int随机数
	 * @param scope 随机数限界
	 * @return 返回随机数范围 [0,scope)
	 */
	public static int randomInt(int scope) {
		return (scope <= 0 ? 0 : random.nextInt(scope));
	}
	
	/**
	 * 获取int随机数
	 * @param min 随机数限界最小值
	 * @param max 随机数限界最大值
	 * @return 返回随机数范围 [min,max]
	 */
	public static int randomInt(final int min, final int max) {
		int num = random.nextInt(max + 1);
		if (num < min) {
			num += min;
		}
		return num;
	}
	
	/**
	 * 获取long随机数
	 * @return 返回随机数范围 [0,+∞)
	 */
	public static long randomLong() {
		return random.nextLong();
	}
	
	/**
	 * 获取float随机数
	 * @return 返回随机数范围 [0,+∞)
	 */
	public static float randomFloat() {
		return random.nextFloat();
	}
	
	/**
	 * 获取double随机数
	 * @return 返回随机数范围 [0,+∞)
	 */
	public static double randomDouble() {
		return random.nextDouble();
	}
	
	/**
	 * 获取bool随机数
	 * @return 返回随机数范围 true|false
	 */
	public static boolean randomBoolean() {
		return random.nextBoolean();
	}
	
	/**
	 * 获取高斯随机数
	 * @return 返回随机数范围 (-∞,+∞)
	 */
	public static double randomGaussian() {
		return random.nextGaussian();
	}
	
	/**
	 * 获取随机姓名（拼音）
	 * @return 随机姓名（拼音）
	 */
	public static String randomSpellName() {
		return randomChineseName()[0];
	}
	
	/**
	 * 获取随机姓名（汉字）
	 * @return 随机姓名（汉字）
	 */
	public static String randomKanjiName() {
		return randomChineseName()[1];
	}
	
	/**
	 * 获取随机姓名
	 * @return 返回值为只有2个元素的数组，其中 0:拼音名; 1:中文名
	 */
	public static String[] randomChineseName() {
		String[] chineseName = new String[2];
		
		String[] compoundSurname = randomCompoundSurname();
		String[] singleSurname1 = randomSingleSurname();
		String[] singleSurname2 = randomSingleSurname();
		String[] word1 = randomWord();
		String[] word2 = randomWord();
		
		int nameType = randomInt(1, 100);
		if(nameType <= SINGLE_SURNAME_1WORD) {
			chineseName[0] = StrUtils.concat(singleSurname1[0], "-", word1[0]);
			chineseName[1] = StrUtils.concat(singleSurname1[1], word1[1]);
			
		} else if(nameType <= TWOSYLLABLE_SURNAME_1WORD) {
			chineseName[0] = StrUtils.concat(singleSurname1[0], singleSurname2[0], "-", word1[0]);
			chineseName[1] = StrUtils.concat(singleSurname1[1], singleSurname2[1], word1[1]);
			
		} else if(nameType <= COMPOUND_SURNAME_1WORD) {
			chineseName[0] = StrUtils.concat(compoundSurname[0], "-", word1[0]);
			chineseName[1] = StrUtils.concat(compoundSurname[1], word1[1]);
			
		} else if(nameType <= SINGLE_SURNAME_2WORD) {
			chineseName[0] = StrUtils.concat(singleSurname1[0], "-", word1[0], word2[0]);
			chineseName[1] = StrUtils.concat(singleSurname1[1], word1[1], word2[1]);
			
		} else if(nameType <= TWOSYLLABLE_SURNAME_2WORD) {
			chineseName[0] = StrUtils.concat(singleSurname1[0], singleSurname2[0], "-", word1[0], word2[0]);
			chineseName[1] = StrUtils.concat(singleSurname1[1], singleSurname2[1], word1[1], word2[1]);
			
		} else if(nameType <= COMPOUND_SURNAME_2WORD) {
			chineseName[0] = StrUtils.concat(compoundSurname[0], "-", word1[0], word2[0]);
			chineseName[1] = StrUtils.concat(compoundSurname[1], word1[1], word2[1]);
		}
		
		return chineseName;
	}
	
	private static String[] randomSingleSurname() {
		int idx = randomInt(SINGLE_SURNAME[0].length);
		return new String[] { SINGLE_SURNAME[0][idx], SINGLE_SURNAME[1][idx] };
	}
	
	private static String[] randomCompoundSurname() {
		int idx = randomInt(COMPOUND_SURNAME[0].length);
		return new String[] { COMPOUND_SURNAME[0][idx], COMPOUND_SURNAME[1][idx] };
	}
	
	private static String[] randomWord() {
		int idx = randomInt(WORD[0].length);
		String[] word = { WORD[0][idx], WORD[1][idx] };
		
		char[] kanji = word[1].toCharArray();
		word[1] = String.valueOf(kanji[randomInt(kanji.length)]);
		return word;
	}
	
}

