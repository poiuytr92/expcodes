package exp.bilibli.plugin.bean.ldm;

import java.nio.ByteBuffer;

import org.java_websocket.framing.Framedata;

import exp.libs.utils.num.BODHUtils;

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

 */
public class Frame implements Framedata {

	// 通过Fiddler抓包解析得到B站WebSocket建立会话时发送的链接数据
	public final static String CLIENT_CONNECT = 
			("00-00-00-36-00-10-00-01-00-00-00-07-00-00-00-01-" + 
			"7B-22-75-69-64-22-3A-30-2C-22-72-6F-6F-6D-69-64-22-3A-33-39-30-34-38-30-2C-22-70-72-6F-74-6F-76-65-72-22-3A-31-7D").
			replace("-", "");
	
	// 通过Fiddler抓包解析得到B站WebSocket保持会话时发送的心跳数据
	public final static String CLIENT_HB = 
			("00-00-00-1F-00-10-00-01-00-00-00-02-00-00-00-01-" + 
			"5B-6F-62-6A-65-63-74-20-4F-62-6A-65-63-74-5D").
			replace("-", "");
	
	// 通过Fiddler抓包解析得到B站WebSocket保持会话时收到的心跳数据
	public final static String SERVER_HB = 
			"00-00-00-14-00-10-00-01-00-00-00-03-00-00-00-01-00-00-00-0B".
			replace("-", "");
	
	private ByteBuffer payloadData;
	
	public Frame(boolean forConn) {
		this.payloadData = ByteBuffer.wrap(
				BODHUtils.toBytes(forConn ? CLIENT_CONNECT : CLIENT_HB));
	}
	
	@Override
	public boolean isFin() {
		return true;
	}

	@Override
	public boolean isRSV1() {
		return false;
	}

	@Override
	public boolean isRSV2() {
		return false;
	}

	@Override
	public boolean isRSV3() {
		return false;
	}

	@Override
	public boolean getTransfereMasked() {
		return true;	// 客户端发送的数据均需要掩码
	}

	@Override
	public Opcode getOpcode() {
		return Opcode.BINARY;
	}

	@Override
	public ByteBuffer getPayloadData() {
		return payloadData;
	}

	@Override
	public void append(Framedata framedata) {
		// TODO Auto-generated method stub
		
	}

}
