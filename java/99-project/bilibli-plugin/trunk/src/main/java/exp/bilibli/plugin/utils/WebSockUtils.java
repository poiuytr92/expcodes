package exp.bilibli.plugin.utils;

import exp.libs.utils.num.BODHUtils;
import exp.libs.utils.num.RandomUtils;
import exp.libs.utils.other.StrUtils;

public class WebSockUtils {

	private final static char[] HEX = {
		'0', '1', '2', '3', '4', '5', '6', '7', 
		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};
	
	/**
	 * 
	 * 
16:10:13:7051 WSSession26.WebSocket'WebSocket #26'
MessageID:	Client.1
MessageType:	Binary
PayloadString:	00-00-00-35-00-10-00-01-00-00-00-07-00-00-00-01-7B-22-75-69-64-22-3A-30-2C-22-72-6F-6F-6D-69-64-22-3A-35-31-31-30-38-2C-22-70-72-6F-74-6F-76-65-72-22-3A-31-7D
Masking:	9A-EC-AB-C5
                00-00-00-36-00-10-00-01-00-00-00-07-00-00-00-01-7B-22-75-69-64-22-3A-30-2C-22-72-6F-6F-6D-69-64-22-3A-33-31-34-33-36-38-2C-22-70-72-6F-74-6F-76-65-72-22-3A-31-7D
Masking:	1F-30-D0-E4


10:50:03:6853 WSSession199.WebSocket'WebSocket #199'
MessageID:	Client.1
MessageType:	Binary
PayloadString:	00-00-00-36-00-10-00-01-00-00-00-07-00-00-00-01-7B-22-75-69-64-22-3A-30-2C-22-72-6F-6F-6D-69-64-22-3A-33-39-30-34-38-30-2C-22-70-72-6F-74-6F-76-65-72-22-3A-31-7D
Masking:	BC-B8-DE-E1


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
	
	/**
	 * 
1byte
bit: frame-fin，x0表示该message后续还有frame；x1表示是message的最后一个frame
3bit: 分别是frame-rsv1、frame-rsv2和frame-rsv3，通常都是x0
4bit: frame-opcode，x0表示是延续frame；x1表示文本frame；x2表示二进制frame；x3-7保留给非控制frame；x8表示关 闭连接；x9表示ping；xA表示pong；xB-F保留给控制frame

2byte
1bit: Mask，1表示该frame包含掩码；0，表示无掩码
7bit、7bit+2byte、7bit+8byte: 7bit取整数值，若在0-125之间，则是负载数据长度；若是126表示，后两个byte取无符号16位整数值，是负载长度；127表示后8个 byte，取64位无符号整数值，是负载长度

3-6byte: 这里假定负载长度在0-125之间，并且Mask为1，则这4个byte是掩码

7-end byte: 长度是上面取出的负载长度，包括扩展数据和应用数据两部分，通常没有扩展数据；若Mask为1，则此数据需要解码，解码规则为1-4byte掩码循环和数据byte做异或操作。
	 * @param args
	 */
	
	public static void main(String[] args) {
		System.out.println(toStr("00-00-00-36-00-10-00-01-00-00-00-07-00-00-00-01-7B-22-75-69-64-22-3A-30-2C-22-72-6F-6F-6D-69-64-22-3A-33-31-34-33-36-38-2C-22-70-72-6F-74-6F-76-65-72-22-3A-31-7D"));
		System.out.println(toStr("00-00-00-36"));
		
//		FileFlowReader ffr = new FileFlowReader("./data/ws.txt", Charset.ISO);
//		FileSegmentReader fsr = new FileSegmentReader(
//				ffr, '\n', "WebSocket'WebSocket #199'\r\n", "--\r\n");
//		while(fsr.hasNextSegment()) {
//			String segment = fsr.getSegment();
//			
//			String id = RegexUtils.findFirst(segment, "MessageID:\\s+([\\S]+)");
//			String hex = RegexUtils.findFirst(segment, "PayloadString:\\s+([\\S]+)");
//			String mask = RegexUtils.findFirst(segment, "Masking:\\s+([\\S]+)");
//			
//			String data = StrUtils.concat(id, "(", mask, "):\r\n -> ", toStr(hex), "\r\n\r\n");
//			FileUtils.write("./log/rst.log", data, true);
//			System.out.println(data);
//		}
//		fsr.close();
//		ffr.close();
		System.out.println(getRandMask().length);
	}
	
	
	
	public static byte[] getRandMask() {
		final int MASK_LEN = 8;
		StringBuilder hex = new StringBuilder();
		for(int i = 0; i < MASK_LEN; i++) {
			hex.append(HEX[RandomUtils.randomInt(HEX.length)]);
		}
		return BODHUtils.toBytes(hex.toString());
	}
	
	public static byte[] mask(byte[] mask, byte[] data) {
		byte[] mData = new byte[mask.length + data.length];
		for(int i = 0; i < mask.length; i++) {
			mData[i] = mask[i];
		}
		
		final int OFFSET = mask.length;
		for(int i = 0; i < data.length; i++) {
			mData[i + OFFSET] = (byte) (mask[i % OFFSET] ^ mData[i + OFFSET]);
		}
		return mData;
	}
	
	private static String toStr(String hex) {
		hex = hex.replace("-", "");
		byte[] bytes = BODHUtils.toBytes(hex);
		String data = new String(bytes);
		return StrUtils.view(data);
	}
	
//	private void foo() {
//		while ((hasRedad = is.read(buff)) > 0) { //必须这么写
//            System.out.println("接收到客户端"
//                    + socket.getInetAddress().getHostName() + "字节数："
//                    + hasRedad);
//            /*
//             * 因为WebSocket发送过来的数据遁寻了一定的协议格式， 其中第3~6个字节是数据掩码，
//             * 从第七个字节开始才是真正的有效数据。 因此程序使用第3~6个字节对后面的数据进行了处理
//             */
//            for (int i = 0; i < hasRedad - 6; i++) {
//                buff[i+6] = (byte)(buff[i%4+2]^ buff[i+6]);
//            }
//            //获得从浏览器发送过来的数据
//            String pushMsg = new String(buff, 6, hasRedad-6, "utf-8");
//            //遍历Socket集合,依次向每个Socket发送数据
//            int a=1;
//            for(Iterator<Socket> it = ChatServer.clientSocket.iterator();it.hasNext();){
//                    //获得集合中的Socket对象
//                System.out.println("Socket集合中有："+ChatServer.clientSocket.size()+"个对象等待发送信息");
//                    Socket s = it.next();
//                    //发送数据时，第一个字节必须与读到的第一个字节相同
//                    byte[] pushHead = new byte[2];
//                    pushHead[0] = buff[0];
//                    //发送数据时，第二个字节记录 发送数据的长度
//                    pushHead[1] = (byte)pushMsg.getBytes("utf-8").length;
//                    try {
//                        System.out.println("web推送前");
//                            System.out.println("Socket 的InputStream值:"+is.available());
//
//                                System.out.println("web推送中........");
//                                //发送前两个字节
//                                s.getOutputStream().write(pushHead);
//                                //发送有效数据
//                                s.getOutputStream().write(pushMsg.getBytes("utf-8"));
//                    } catch (Exception e) {
//                        System.out.println("向前端推送数据后发生了异常");
//                        e.printStackTrace();
//                    }finally{
//                        //如果s.getInputStream().available() == 0，表明该Scoket已经关闭
//                        //将该Socket从Socket集合中删除
//                        System.out.println("从集合中删除该Socket对象");
//                        ChatServer.clientSocket.remove(s);
//                        a=2;
//                        break;
//                    }
//                    }
//                    System.out.println("WEB 推送后");
//            if(a==2){
//                break;
//            }
//        }
//	}
}
