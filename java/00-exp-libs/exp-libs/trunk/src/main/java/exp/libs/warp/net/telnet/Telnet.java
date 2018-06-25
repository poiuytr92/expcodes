package exp.libs.warp.net.telnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;

import exp.libs.warp.net.tracert.Tracert;
import exp.libs.warp.net.tracert.bean.TracertBean;

/**
 * <PRE>
 * telnet客户端类，里面提供连接，发指令、获取返回信息，断开连接
 * 1.telnet有VT100 VT102 VT220 VTNT ANSI LINUX等协议。
 *  默认VT100，win telnet linux时，有乱码，则改用VT200
 * 2.vt100控制码(ansi控制码)过滤的问题,可以过滤，也可以在服务设置不要。
 * 	不过滤都是一些乱码。是以\033[***一个字母结尾的格式。
 * 3.中文乱码的问题。
 * new String(old.getBytes("ISO8859-1"),"GBK")。
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-02-14
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Telnet {
	
	/** 终端类型枚举  */
	public final static String PROTOCOL_VT100 = "VT100";
	
	/** 终端类型枚举  */
	public final static String PROTOCOL_VT220 = "VT220";
	
	/** 终端类型枚举  */
	public final static String PROTOCOL_VTNT = "VTNT";
	
	/** 终端类型枚举  */
	public final static String PROTOCOL_VTANSI = "VTANSI";
	
	/** 终端类型枚举  */
	public final static String PROTOCOL_LINUX = "LINUX";
	
	/** 结束判断符，默认  */
	public final static char PROMPT_DOLLAR_SIGN = '$';
	
	/** 结束判断符，root用户  */
	public final static char PROMPT_NUMBER_SIGN = '#';
	
	/** 系统名称 */
	public static final String OS = System.getProperty("os.name");

	/** 系统编码 */
	public static String encoding = System
			.getProperty("sun.jnu.encoding");
	
	/** 系统编码，中�?  */
	public final static String CONNECT_CHARACTER_CN_GBK = "zh_CN.gbk";
	
	/** 系统编码，英�?  */
	public final static String CONNECT_CHARACTER_EN_UTF8 = "en_US.UTF-8";
	
	/** 发送指令的字符编码 */
	private String sConnectCharacter = CONNECT_CHARACTER_EN_UTF8;
	
	/** apache commons TelnetClinet对象  */
	private TelnetClient telnet;
	
	/** 输入�?  */
	private InputStream in;
	
	/** 输出�?  */
	private PrintStream out;
	
	/** 结束判断�?  */
	private char prompt = PROMPT_DOLLAR_SIGN;
	
	/** 连接状�?  */
	private boolean connectFlag = false;
	
	/** 连接超时，默�?5�?	 */
	private int connectTimeOut = 5000;

	/** 返回超时，默�?5�?	 */
	private int soTimeOut = 5000;
	
	/**
	 * 构造方�?
	 */
	public Telnet() {
		telnet = new TelnetClient(PROTOCOL_VT100);
	}
	
	/**
	 * 构造方�?
	 * @param termtype 终端类型,win访问linux请使用PROTOCOL_VT220
	 */
	public Telnet(String termtype) {
		telnet = new TelnetClient(termtype);
	}

	/**
	 * telnet连接，成功返回true，失败返回false
	 * 
	 * @param ip IP
	 *            
	 * @param port 端口
	 *            
	 * @return boolean
	 * @throws IOException 
	 */
	public boolean connect(String ip, Integer port) throws IOException {
		try {
			telnet.setConnectTimeout(connectTimeOut);
			telnet.connect(ip, port);
			telnet.setSoTimeout(soTimeOut);

			in = telnet.getInputStream();
			out = new PrintStream(telnet.getOutputStream());
		} catch (SocketException e) {
			connectFlag = false;
			throw new IOException("ip地址或端口出�?", e);
		} catch (IOException e) {
			connectFlag = false;
			throw new IOException("连接telnet的时候IO出现异常", e);
		}
		return true;
	}
	
	/**
	 * telnet连接并登陆，成功返回true，失败返回false
	 * 
	 * @param ip IP
	 *            
	 * @param port 端口
	 *            
	 * @param user	用户�?
	 *            
	 * @param password	密码
	 *            
	 * @return boolean
	 * @throws IOException 
	 */
	public boolean connect(String ip, Integer port, String user, 
			String password) throws IOException {
		try {
			telnet.setConnectTimeout(connectTimeOut);
			telnet.connect(ip, port);
			telnet.setSoTimeout(soTimeOut);

			in = telnet.getInputStream();
			out = new PrintStream(telnet.getOutputStream());

			connectFlag = login(user, password);
		} catch (SocketException e) {
			connectFlag = false;
			throw new IOException("ip地址或端口出�?", e);
		} catch (IOException e) {
			connectFlag = false;
			throw new IOException("连接telnet的时候IO出现异常", e);
		}
		return true;
	}

	/**
	 * 断开连接
	 * 
	 * @param null
	 * @return boolean
	 */
	public boolean disConnect() {
		try {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
			if (connectFlag) {
				telnet.disconnect();
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	
	/**
	 * connectTimeOut
	 * @return the connectTimeOut
	 */
	public int getConnectTimeOut() {
		return connectTimeOut;
	}
	
	/**
	 * prompt
	 * @return the prompt
	 */
	public char getPrompt() {
		return prompt;
	}

	/**
	 * sConnectCharacter
	 * @return the sConnectCharacter
	 */
	public String getsConnectCharacter() {
		return sConnectCharacter;
	}

	/**
	 * soTimeOut
	 * @return the soTimeOut
	 */
	public int getSoTimeOut() {
		return soTimeOut;
	}
	
	/**
	 * 登陆
	 * 
	 * @param user 用户�?
	 * @param password 密码
	 * @return boolean
	 * @throws IOException 
	 */
	private boolean login(String user, String password) throws IOException {
			readUntil("ogin: ");
			write(user);
			readUntil("assword: ");
			write(password);
			readUntil(prompt + " ");
			return true;
	}

	/**
	 * 读取返回信息,出错则返回null
	 * 
	 * @param pattern 结束�?
	 * @return String
	 * @throws IOException 
	 */
	private String readUntil(String pattern) throws IOException  {
		char lastChar = pattern.charAt(pattern.length() - 1);
		StringBuffer sb = new StringBuffer();
		String str;
		try {
			char ch = (char) in.read();
			while (true) {
				sb.append(ch);
				if (sb.length() > 1024 * 3) {
					throw new IOException("返回信息过长，请检查是否用户名或密码错误，或者指令错�?");
				}
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						str = new String(sb.toString().getBytes("ISO-8859-1"),
								encoding);
						return str;
					}
				}
				ch = (char) in.read();
			}
		} catch (UnsupportedEncodingException e) {
			throw new IOException(
					"telnet连接读取返回信息的时候出现UnsupportedEncoding异常", e);
		} catch (IOException e) {
			throw new IOException("telnet连接读取返回信息的时候出现IO异常", e);
		}
	}

	/**
	 * 发指令同时返回发送的信息，出错或没有信息返回则返回null
	 * 
	 * @param command 命令
	 *            
	 * @return String
	 * @throws IOException
	 */
	public String sendCommand(String command) throws IOException {
		String commandStr = "LANG=" + sConnectCharacter + ";" + command;
		write(commandStr);
		String str = readUntil(prompt + " ");
		return replaceHeadAndTail(str, command);
	}
	
	/**
	 * 去掉返回头尾
	 * （头：发送的命令单独一行）
	 * （尾：返回内容会多出一行命令输入行，例如[用户名@计算机名 ~]$ �?
	 * @param str  返回内容
	 * @param command	发送的命令
	 * @return String
	 */
	private String replaceHeadAndTail(String str, String command) {
//		String strTemp = null;
		if (str != null) {
			// 去掉‘回车’字符和‘空格加回车’字�?
//			str = str.replaceAll(" \r", "");
//			str = str.replaceAll("\r", "");
//			String regex = 
//			".*没有那个文件或目�?.*\n|.*没有那个文件或目�?.*|.*No such file or directory.*\n|.*No such file or directory.*";
//			Pattern prn = Pattern.compile(regex);
//			Matcher matcher = prn.matcher(str);
//			if (matcher.find()) {
//				return null;
//			}
//
//			// 去掉指令太久没连上而返回的系统信息
//			regex = "You have new mail.*\n?";
//			if ((strTemp = getRgString(regex, str)) != null) {
//				str = str.replace(strTemp, "");
//			}

			int endpt = str.indexOf("\n") + 1;
			if (endpt == 0) {
				endpt = str.length();
			}
			if (str.indexOf(command) != -1) {
				str = str.substring(endpt, str.length());
			}

			endpt = str.lastIndexOf("\n");
			if (endpt == -1) {
				endpt = 0;
			}
			if (str.indexOf(prompt + " ") != -1) {
				str = str.substring(0, endpt);
			}
			
			// 当没有返回值时返回null
			if (str.trim().length() == 0) {
				return null;
			}
		}
		return str;
	}

//	public static String getRgString(String pattern, String value) {
//
//		Pattern prn = Pattern.compile(pattern);
//		Matcher matcher = prn.matcher(value);
//		if (matcher.find()) {
//			return matcher.group();
//		}
//		return null;
//	}

	/**
	 * connectTimeOut
	 * @param connectTimeOut the connectTimeOut to set
	 */
	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}

	/**
	 * prompt
	 * @param prompt the prompt to set
	 */
	public void setPrompt(char prompt) {
		this.prompt = prompt;
	}

	/**
	 * sConnectCharacter
	 * @param sConnectCharacter the sConnectCharacter to set
	 */
	public void setsConnectCharacter(String sConnectCharacter) {
		this.sConnectCharacter = sConnectCharacter;
	}

	/**
	 * soTimeOut
	 * @param soTimeOut the soTimeOut to set
	 */
	public void setSoTimeOut(int soTimeOut) {
		this.soTimeOut = soTimeOut;
	}

	/**
	 * 转换为超级用户登陆，并修改其结束�?
	 * 
	 * @param password 密码
	 * @throws IOException 
	 */
	public void su(String password) throws IOException {
		write("su");
		readUntil("Password: ");
		write(password);
		prompt = '#';
		readUntil(prompt + " ");
	}

	/**
	 * 发送指�?
	 * 
	 * @param value 发送的字符�?
	 */
	private void write(String value) {
		out.println(value);
		out.flush();

	}

	/**
	 * 主方法是拿来做测试用�?
	 * 
	 * @param args null
	 */
	public static void main(String[] args) {
		String command = "traceroute -w 0.1 -n -q 1  www.baidu.com";
//		command = "ll";
//		command = "ping -c 4 www.baidu.com";
		Telnet telnet = null;
		try {
			telnet = new Telnet(Telnet.PROTOCOL_VT220);
			
			/* 登录超时 */
			telnet.setConnectTimeOut(3000);
			
			/* ping 和traceroute 等命令，要注意返回超时设�? */
			telnet.setSoTimeOut(15 * 1000);
			
			if (telnet.connect("172.168.10.95", 23, "username", "password")) {
				System.out.println("连接成功");
				
				// col -b 去掉控制�?
				String str = telnet.sendCommand(command);

				System.out.println("开始~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

				System.out.println(str);
				
				TracertBean trb = new Tracert().parseTracertReturn(str);
				
				System.out.println("#############\n" + trb);
				System.out.println("结束~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			} else {
				System.out.println("连接错误");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			telnet.disConnect();
		}
		System.out.println("程序结束");
	}

}
