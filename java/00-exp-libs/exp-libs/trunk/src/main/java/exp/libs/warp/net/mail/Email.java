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

public class Email {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(Email.class);
	
	private final static SMTP DEFAULT_SMTP = SMTP._126;
	
	private final static int DEFAULT_SMTP_PORT = 25;
	
	private final static String DEFAULT_SECRET_KEY = "EXP-MAIL";
	
	private String smtpServer;
	
	private int smtpPort;
	
	private String sender;
	
	private String password;
	
	private Address[] receivers;
	
	private String secretKey;
	
	private String encoding;
	
	private Session session;
	
	private String messageType;
	
	public Email(SMTP smtp, String sender, String password, 
			String receiver, String secretKey, String encoding) {
		smtp = (smtp == null ? DEFAULT_SMTP : smtp);
		init(smtp.SERVER, smtp.PORT, sender, password, 
				new String[] { receiver }, secretKey, encoding);
	}
	
	/**
	 * 
	 * @param smtp
	 * @param sender
	 * @param password
	 * @param receivers
	 * @param secretKey
	 * @param encoding
	 */
	public Email(SMTP smtp, String sender, String password, 
			String[] receivers, String secretKey, String encoding) {
		smtp = (smtp == null ? DEFAULT_SMTP : smtp);
		init(smtp.SERVER, smtp.PORT, sender, password, 
				receivers, secretKey, encoding);
	}
	
	public Email(String smtpServer, int smtpPort, String sender, String password, 
			String receiver, String secretKey, String encoding) {
		init(smtpServer, smtpPort, sender, password, 
				new String[] { receiver }, secretKey, encoding);
	}
	
	/**
	 * 
	 * @param smtpServer
	 * @param smtpPort
	 * @param sender
	 * @param password
	 * @param receivers
	 * @param secretKey
	 * @param encoding
	 */
	public Email(String smtpServer, int smtpPort, String sender, String password, 
			String[] receivers, String secretKey, String encoding) {
		init(smtpServer, smtpPort, sender, password, receivers, secretKey, encoding);
	}
	
	private void init(String smtpServer, int smtpPort, String sender, String password, 
			String[] receivers, String secretKey, String encoding) {
		this.smtpServer = StrUtils.trim(smtpServer);
		this.smtpPort = (VerifyUtils.isPort(smtpPort) ? smtpPort : DEFAULT_SMTP_PORT);
		this.sender = StrUtils.trim(sender);
		this.receivers = toAddress(receivers);
		this.password = StrUtils.trim(password);
		this.secretKey = (StrUtils.isEmpty(secretKey) ? DEFAULT_SECRET_KEY : secretKey);
		this.encoding = (CharsetUtils.isInvalid(encoding) ? Charset.UTF8 : encoding);
		this.messageType = StrUtils.concat("text/html;charset=", this.encoding);
		
		Properties prop = new Properties();
		prop.setProperty("mail.transport.protocol", "smtp");
		prop.setProperty("mail.smtp.host", this.smtpServer);
		prop.setProperty("mail.smtp.port", String.valueOf(this.smtpPort));
		prop.setProperty("mail.smtp.auth", "true");
		this.session = Session.getInstance(prop);
		this.session.setDebug(false);
	}
	
	/**
	 * 开关debug模式，用于查看程序发送Email的详细状态
	 * @param debug
	 */
	public void debug(boolean debug) {
		this.session.setDebug(debug);
	}
	
	public boolean send(String title, String content) {
		return send(title, content, null, null, false);
	}

	public boolean send(String title, String content, boolean encrypt) {
		return send(title, content, null, null, encrypt);
	}
	
	/**
	 * 
	 * @param title
	 * @param content
	 * @param CC 抄送
	 * @param encrypt
	 * @return
	 */
	public boolean send(String title, String content, 
			String[] CC, boolean encrypt) {
		return send(title, content, null, CC, encrypt);
	}
	
	/**
	 * 
	 * @param title
	 * @param content
	 * @param receivers
	 * @param CC
	 * @param encrypt
	 * @return
	 */
	public boolean send(String title, String content, 
			String[] receivers, String[] CC, boolean encrypt) {
		boolean isOk = false;
		try {
			Transport ts = session.getTransport();
			ts.connect(smtpServer, sender, password);
			Message message = createMessage(title, content, receivers, CC, encrypt);
			ts.sendMessage(message, message.getAllRecipients());
			ts.close();
			isOk = true;
			
		} catch (Exception e){
			log.error("使用SMTP服务 [{}] 发送邮件失败", 
					StrUtils.concat(smtpServer, ":", smtpPort), e);
		}
		return isOk;
	}
	
    private MimeMessage createMessage(String title, String content, 
    		String[] receivers, String[] CC, boolean encrypt) throws Exception {
		 MimeMessage message = new MimeMessage(session);
		 message.setSubject(title);
		 message.setFrom(new InternetAddress(sender));
		 message.setContent((encrypt ? 
				 CryptoUtils.toDES(content, secretKey) : content), messageType);
		 
		 message.setRecipients(Message.RecipientType.TO, 
				 (receivers != null ? toAddress(receivers) : this.receivers));
		 if(CC != null) {
			 message.setRecipients(Message.RecipientType.CC, toAddress(CC));
		 }
		 return message;     
    }
    
    private Address[] toAddress(String[] receivers) {
    	receivers = (receivers == null || receivers.length <= 0 ? 
    			new String[] { sender } : receivers);
    	int size = ListUtils.cutbackNull(receivers);
    	
    	Address[] address = new InternetAddress[size];
    	for(int i = 0; i < size; i++) {
    		try {
				address[i] = new InternetAddress(receivers[i]);
			} catch (AddressException e) {
				log.error("转换Email地址 [{}] 为标准格式失败.", receivers[i], e);
			}
    	}
    	return address;
    }
    
}
