package exp.bilibli.plugin.envm;

/**
 * 
14:15:04:9179 WSSession199.WebSocket'WebSocket #199'
MessageID:	Client.924
MessageType:	Binary
PayloadString:	00-00-00-1F-00-10-00-01-00-00-00-02-00-00-00-01-5B-6F-62-6A-65-63-74-20-4F-62-6A-65-63-74-5D
Masking:	27-72-F5-28

14:15:04:9529 WSSession199.WebSocket'WebSocket #199'
MessageID:	Server.925
MessageType:	Binary
PayloadString:	00-00-00-14-00-10-00-01-00-00-00-03-00-00-00-01-00-00-00-0B
Masking:	<none>

14:47:20:8430 WSSession54.WebSocket'WebSocket #54'
MessageID:	Client.7
MessageType:	Close
PayloadString:	03-E9
Masking:	AD-73-35-23

 */
public class BinaryData {

	// 通过Fiddler抓包解析得到B站WebSocket建立会话时发送的链接数据
	public final static String CLIENT_CONNECT = 
			"00-00-00-36-00-10-00-01-00-00-00-07-00-00-00-01-7B-22-75-69-64-22-3A-30-2C-22-72-6F-6F-6D-69-64-22-3A-33-39-30-34-38-30-2C-22-70-72-6F-74-6F-76-65-72-22-3A-31-7D".
			replace("-", "");

	// 通过Fiddler抓包解析得到B站WebSocket保持会话时发送的心跳数据
	public final static String CLIENT_HB = 
			"00-00-00-1F-00-10-00-01-00-00-00-02-00-00-00-01-5B-6F-62-6A-65-63-74-20-4F-62-6A-65-63-74-5D".
			replace("-", "");
	
	// 通过Fiddler抓包解析得到浏览器向B站客户端主动发送断开连接的数据
	public final static String CLIENT_CLOSE = "03-E9".replace("-", "");
	
	// 通过Fiddler抓包解析得到B站WebSocket返回的连接确认信息
	public final static String SERVER_CONN_CONFIRM = 
			"00-00-00-10-00-10-00-01-00-00-00-08-00-00-00-01".
			replace("-", "");
	
	// 通过Fiddler抓包解析得到B站WebSocket返回的心跳确认信息（末4位字节是变化值）
	public final static String SERVER_HB_CONFIRM = 
			"00-00-00-14-00-10-00-01-00-00-00-03-00-00-00-01-".
			replace("-", "");
	
}
