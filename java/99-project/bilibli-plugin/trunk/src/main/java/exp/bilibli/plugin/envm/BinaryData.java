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
	// 内部数据包含了访问的直播房间号（无需登录）
	public final static String CLIENT_CONNECT = 
			"000000360010000100000007000000017B22756964223A302C22726F6F6D6964223A3339303438302C2270726F746F766572223A317D";

	// 通过Fiddler抓包解析得到B站WebSocket保持会话时发送的心跳数据
	public final static String CLIENT_HB = 
			"0000001F0010000100000002000000015B6F626A656374204F626A6563745D";
	
	// 通过Fiddler抓包解析得到浏览器向B站客户端主动发送断开连接的数据
	public final static String CLIENT_CLOSE = "03E9";
	
	// 通过Fiddler抓包解析得到B站WebSocket返回的连接确认信息
	public final static String SERVER_CONN_CONFIRM = 
			"00000010001000010000000800000001";
	
	// 通过Fiddler抓包解析得到B站WebSocket返回的心跳确认信息（末4位字节因为是变化值，此处已删除）
	public final static String SERVER_HB_CONFIRM = 
			"00000014001000010000000300000001";
	
}
