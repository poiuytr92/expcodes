package exp.bilibili.plugin.bean.ldm;

import java.nio.ByteBuffer;

import org.java_websocket.framing.Framedata;

import exp.bilibili.plugin.envm.Binary;
import exp.libs.utils.num.BODHUtils;

/**
 * <PRE>
 * 数据帧.
 * 	注意数据帧是有时间戳的，即使内容相同，时间戳不匹配当前时间的话是无法发送出去的.
 *  所以数据帧只能在发送前new出来，不能提前构造好final常量.
 * </PRE>
 * <B>PROJECT : </B> bilibili-plugin
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 2017-12-17
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Frame implements Framedata {

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
		return true;	// 暂不存在多帧分包发送的情况
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
		return true;	// 客户端发送的数据均需要掩�?
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

	// {"uid":0,"roomid":390480,"protover":1}
	public static Frame C2S_CONN(int roomId) {
		return new Frame(Binary.CLIENT_CONNECT(roomId));
	}
	
	public static Frame C2S_CLOSE() {
		return new Frame(Binary.CLIENT_CLOSE, Opcode.CLOSING);
	}
	
	public static Frame C2S_HB() {
		return new Frame(Binary.CLIENT_HB);
	}
	
}
