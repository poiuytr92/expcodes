package exp.libs.warp.net.websock.bean;

import java.nio.ByteBuffer;

import org.java_websocket.framing.Framedata;

import exp.libs.utils.num.BODHUtils;

/**
 * <PRE>
 * 数据帧.
 * 	注意: <b>数据帧是有时间戳的</b>，即使内容相同，时间戳不匹配当前时间的话是无法发送出去的.
 *  所以数据帧只能在发送前new出来(<b>自动生成当时的时间戳</b>)，不能提前构造好final常量.
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2017-08-21
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class Frame implements Framedata {

	public final static Frame NULL = new Frame(new byte[0]);
	
	/** 帧内数据(字节�?) */
	private ByteBuffer payloadData;
	
	/**
	 * 操作�?: CONTINUOUS, TEXT, BINARY, PING, PONG, CLOSING
	 */
	private Opcode opcode;
	
	/**
	 * 构造函�?
	 * @param byteHex 字节数据 (16进制表示形式)
	 */
	public Frame(String byteHex) {
		this(byteHex, Opcode.BINARY);
	}
	
	/**
	 * 构造函�?
	 * @param bytes 字节数据
	 */
	public Frame(byte[] bytes) {
		this(bytes, Opcode.BINARY);
	}
	
	/**
	 * 构造函�?
	 * @param byteHex 字节数据 (16进制表示形式)
	 * @param opcode 操作�?
	 */
	public Frame(String byteHex, Opcode opcode) {
		this(BODHUtils.toBytes(byteHex), opcode);
	}
	
	/**
	 * 构造函�?
	 * @param byteHex 字节数据
	 * @param opcode 操作�?
	 */
	public Frame(byte[] bytes, Opcode opcode) {
		this.payloadData = ByteBuffer.wrap(bytes);
		this.opcode = opcode;
	}
	
	@Override
	public boolean isFin() {
		return true;	// 默认不采用多帧分包发送模�?, 所以每个包都发送一个fin标识
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
	
	/**
	 * 克隆数据帧（数据帧时间戳取克隆时的时间点�?
	 * @return 仅装载数据相同的全新数据�?
	 */
	public Frame clone() {
		return new Frame(getPayloadData().array(), getOpcode());
	}
	
}
