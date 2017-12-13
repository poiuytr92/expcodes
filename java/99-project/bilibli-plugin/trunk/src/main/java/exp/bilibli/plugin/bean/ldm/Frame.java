package exp.bilibli.plugin.bean.ldm;

import java.nio.ByteBuffer;

import org.java_websocket.framing.Framedata;

import exp.bilibli.plugin.envm.BinaryData;
import exp.libs.utils.num.BODHUtils;

/**
 * <PRE>
 * 数据帧.
 * 	注意数据帧是有时间戳的，即使内容相同，时间戳不匹配当前时间的话是无法发送出去的.
 *  所以数据帧只能在发送前new出来，不能提前构造好final常量.
 * </PRE>
 * 
 * @author Administrator
 * @date 2017年12月13日
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

	public static Frame C2S_CONN() {
		return new Frame(BinaryData.CLIENT_CONNECT);
	}
	
	public static Frame C2S_CLOSE() {
		return new Frame(BinaryData.CLIENT_CLOSE, Opcode.CLOSING);
	}
	
	public static Frame C2S_HB() {
		return new Frame(BinaryData.CLIENT_HB);
	}
	
}