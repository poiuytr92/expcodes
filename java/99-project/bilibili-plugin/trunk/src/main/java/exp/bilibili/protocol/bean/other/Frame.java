package exp.bilibili.protocol.bean.other;

import java.nio.ByteBuffer;

import org.java_websocket.framing.Framedata;

import exp.bilibili.protocol.envm.BiliBinary;
import exp.libs.utils.num.BODHUtils;

/**
 * <PRE>
 * 数据帧.
 * 	注意数据帧是有时间戳的，即使内容相同，时间戳不匹配当前时间的话是无法发送出去的.
 *  所以数据帧只能在发送前new出来，不能提前构造好final常量.
 * </PRE>
 * <B>PROJECT：</B> bilibili-plugin
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Frame implements Framedata {

	/**
	 * 连接WebSock数据帧（到指定房间）
	 * @param roomId
	 * @return
	 */
	public static Frame C2S_CONN(int roomId) {
		return new Frame(BiliBinary.CLIENT_CONNECT(roomId));
	}
	
	/**
	 * 关闭WebSock连接数据帧
	 * @return
	 */
	public static Frame C2S_CLOSE() {
		return new Frame(BiliBinary.CLIENT_CLOSE, Opcode.CLOSING);
	}
	
	/**
	 * WebSock心跳数据帧
	 * @return
	 */
	public static Frame C2S_HB() {
		return new Frame(BiliBinary.CLIENT_HB);
	}
	
	
	//////////////////////////////////////////////////////////////
	
	
	private ByteBuffer payloadData;
	
	private Opcode opcode;
	
	public Frame(String byteHex) {
		this(byteHex, Opcode.BINARY);
	}
	
	public Frame(byte[] bytes) {
		this(bytes, Opcode.BINARY);
	}
	
	public Frame(String byteHex, Opcode opcode) {
		this(BODHUtils.toBytes(byteHex), opcode);
	}
	
	public Frame(byte[] bytes, Opcode opcode) {
		this.payloadData = ByteBuffer.wrap(bytes);
		this.opcode = opcode;
	}
	
	@Override
	public boolean isFin() {
		return true;	// 暂不存在多帧分包发送的情况, 所以每个包都发送一个fin标识
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
		return opcode;
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
