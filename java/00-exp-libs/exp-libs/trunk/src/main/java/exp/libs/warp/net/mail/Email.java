package exp.libs.warp.net.mail;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.envm.SMTP;
import exp.libs.utils.encode.CharsetUtils;
import exp.libs.utils.encode.CryptoUtils;
import exp.libs.utils.other.ListUtils;
import exp.libs.utils.other.StrUtils;
import exp.libs.utils.verify.VerifyUtils;

/**
 * <PRE>
 * Email发送工具.
 * 
 * 使用示例:
 * 	Email mail = new Email(SMTP._126, "username@126.com", "password", 
 * 		new String[] { recv1@qq.com, recv@163.com }, "KEY-TEST", Charset.UTF8);
 * 	mail.send("title-1", "content-abcdefg");
 * 	mail.send("title-2", "content-xyzzyx");
 * 
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2015-12-27
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Email {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(Email.class);
	
	/** 默认SMTP服务�? */
	private final static SMTP DEFAULT_SMTP = SMTP._126;
	
	/** 默认SMTP端口 */
	private final static int DEFAULT_SMTP_PORT = 25;
	
	/** 默认加密密钥 */
	private final static String DEFAULT_SECRET_KEY = "EXP-MAIL";
	
	/** SMTP服务�? */
	private String smtpServer;
	
	/** SMTP端口 */
	private int smtpPort;
	
	/** 发件�?(邮箱账号) */
	private String sender;
	
	/** 邮箱密码 */
	private String password;
	
	/** 收件人标准邮箱地址 */
	private Address[] receivers;
	
	/** 安全加密密钥 */
	private String secretKey;
	
	/** 邮件内容编码 */
	private String charset;
	
	/** 邮箱会话 */
	private Session session;
	
	/** 消息类型 */
	private String messageType;
	
	/**
	 * 构造函�?
	 * @param smtp SMTP服务�?(发件服务�?)
	 * @param sender 发送人(邮箱账号)
	 * @param password 邮箱密码
	 * @param receiver 默认收件人邮箱地址
	 * @param secretKey 邮件内容加密密钥
	 * @param charset 邮件内容编码
	 */
	public Email(SMTP smtp, String sender, String password, 
			String receiver, String secretKey, String charset) {
		smtp = (smtp == null ? DEFAULT_SMTP : smtp);
		init(smtp.SERVER, smtp.PORT, sender, password, 
				new String[] { receiver }, secretKey, charset);
	}
	
	/**
	 * 构造函�?
	 * @param smtp SMTP服务�?(发件服务�?)
	 * @param sender 发送人(邮箱账号)
	 * @param password 邮箱密码
	 * @param receivers 默认收件人邮箱地址
	 * @param secretKey 邮件内容加密密钥
	 * @param charset 邮件内容编码
	 */
	public Email(SMTP smtp, String sender, String password, 
			String[] receivers, String secretKey, String charset) {
		smtp = (smtp == null ? DEFAULT_SMTP : smtp);
		init(smtp.SERVER, smtp.PORT, sender, password, 
				receivers, secretKey, charset);
	}
	
	/**
	 * 构造函�?
	 * @param smtpServer SMTP服务端口(发件服务器IP)
	 * @param smtpPort SMTP服务端口(发件服务器端�?)
	 * @param sender 发送人(邮箱账号)
	 * @param password 邮箱密码
	 * @param receiver 默认收件人邮箱地址
	 * @param secretKey 邮件内容加密密钥
	 * @param charset 邮件内容编码
	 */
	public Email(String smtpServer, int smtpPort, String sender, String password, 
			String receiver, String secretKey, String charset) {
		init(smtpServer, smtpPort, sender, password, 
				new String[] { receiver }, secretKey, charset);
	}
	
	/**
	 * 构造函�?
	 * @param smtpServer SMTP服务端口(发件服务器IP)
	 * @param smtpPort SMTP服务端口(发件服务器端�?)
	 * @param sender 发送人(邮箱账号)
	 * @param password 邮箱密码
	 * @param receivers 默认收件人邮箱地址
	 * @param secretKey 邮件内容加密密钥
	 * @param charset 邮件内容编码
	 */
	public Email(String smtpServer, int smtpPort, String sender, String password, 
			String[] receivers, String secretKey, String charset) {
		init(smtpServer, smtpPort, sender, password, receivers, secretKey, charset);
	}
	
	/**
	 * 构造函�?
	 * @param smtpServer SMTP服务端口(发件服务器IP)
	 * @param smtpPort SMTP服务端口(发件服务器端�?)
	 * @param sender 发送人(邮箱账号)
	 * @param password 邮箱密码
	 * @param receivers 默认收件人邮箱地址
	 * @param secretKey 邮件内容加密密钥
	 * @param charset 邮件内容编码
	 */
	private void init(String smtpServer, int smtpPort, String sender, String password, 
			String[] receivers, String secretKey, String charset) {
		this.smtpServer = StrUtils.trim(smtpServer);
		this.smtpPort = (VerifyUtils.isPort(smtpPort) ? smtpPort : DEFAULT_SMTP_PORT);
		this.sender = StrUtils.trim(sender);
		this.receivers = toAddress(receivers);
		this.password = StrUtils.trim(password);
		this.secretKey = (StrUtils.isEmpty(secretKey) ? DEFAULT_SECRET_KEY : secretKey);
		this.charset = (CharsetUtils.isInvalid(charset) ? Charset.UTF8 : charset);
		this.messageType = StrUtils.concat("text/html;charset=", this.charset);
		
		Properties prop = new Properties();
		prop.setProperty("mail.transport.protocol", "smtp");
		prop.setProperty("mail.smtp.host", this.smtpServer);
		prop.setProperty("mail.smtp.port", String.valueOf(this.smtpPort));
		prop.setProperty("mail.smtp.auth", "true");
		prop.setProperty("mail.smtp.starttls.enable", "true");
		this.session = Session.getInstance(prop);
		this.session.setDebug(false);
	}
	
	/**
	 * 开关debug模式，用于查看程序发送Email的详细状�?
	 * @param debug
	 */
	public void debug(boolean debug) {
		this.session.setDebug(debug);
	}
	
	/**
	 * 发送非加密邮件
	 * @param title 标题
	 * @param content 正文
	 * @return true:发送成�?; false:发送失�?
	 */
	public boolean send(String title, String content) {
		return send(title, content, null, null, false);
	}

	/**
	 * 发送邮�?
	 * @param title 标题
	 * @param content 正文
	 * @param encrypt true:加密�? false：不加密
	 * @return true:发送成�?; false:发送失�?
	 */
	public boolean send(String title, String content, boolean encrypt) {
		return send(title, content, null, null, encrypt);
	}
	
	/**
	 * 发送邮�?
	 * @param title 标题
	 * @param content 正文
	 * @param CCs 抄送人邮箱地址
	 * @param encrypt true:加密�? false：不加密
	 * @return true:发送成�?; false:发送失�?
	 */
	public boolean send(String title, String content, 
			String[] CCs, boolean encrypt) {
		return send(title, content, null, CCs, encrypt);
	}
	
	/**
	 * 发送邮�?
	 * @param title 标题
	 * @param content 正文
	 * @param receivers 临时收件人邮箱地址（若非空，则默认收件人不会受到此封邮件）
	 * @param CCs 抄送人邮箱地址
	 * @param encrypt true:加密�? false：不加密
	 * @return true:发送成�?; false:发送失�?
	 */
	public boolean send(String title, String content, 
			String[] receivers, String[] CCs, boolean encrypt) {
		boolean isOk = false;
		try {
			Transport ts = session.getTransport();
			ts.connect(smtpServer, sender, password);
			Message message = createMessage(title, content, receivers, CCs, encrypt);
			ts.sendMessage(message, message.getAllRecipients());
			ts.close();
			isOk = true;
			
		} catch (Exception e){
			log.error("使用SMTP服务 [{}] 发送邮件失�?", 
					StrUtils.concat(smtpServer, ":", smtpPort), e);
		}
		return isOk;
	}
	
	/**
	 * 创建邮件
	 * @param title
	 * @param content
	 * @param receivers
	 * @param CCs
	 * @param encrypt
	 * @return
	 * @throws Exception
	 */
    private MimeMessage createMessage(String title, String content, 
    		String[] receivers, String[] CCs, boolean encrypt) throws Exception {
		 MimeMessage message = new MimeMessage(session);
		 message.setSubject(title);
		 message.setFrom(new InternetAddress(sender));
		 message.setContent((encrypt ? 
				 CryptoUtils.toDES(content, secretKey) : content), messageType);
		 
		 message.setRecipients(Message.RecipientType.TO, 
				 (receivers != null ? toAddress(receivers) : this.receivers));
		 if(CCs != null) {
			 message.setRecipients(Message.RecipientType.CC, toAddress(CCs));
		 }
		 return message;     
    }
    
    /**
     * 标准化邮箱地址
     * @param mailAddr 邮箱地址
     * @return 标准化邮箱地址
     */
    private Address[] toAddress(String[] mailAddr) {
    	mailAddr = (mailAddr == null || mailAddr.length <= 0 ? 
    			new String[] { sender } : mailAddr);
    	int size = ListUtils.cutbackNull(mailAddr);
    	
    	Address[] address = new InternetAddress[size];
    	for(int i = 0; i < size; i++) {
    		try {
				address[i] = new InternetAddress(mailAddr[i]);
			} catch (AddressException e) {
				log.error("转换Email地址 [{}] 为标准格式失�?.", receivers[i], e);
			}
    	}
    	return address;
    }
    
}
