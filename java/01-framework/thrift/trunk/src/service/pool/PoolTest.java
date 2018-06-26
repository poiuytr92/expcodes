package service.pool;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.transport.TSocket;

/**
 * 
 * <B>PROJECT : </B> thrift
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2015-12-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk°æ±¾£ºjdk1.6
 */
public class PoolTest {

	public static void main(String[] args) {
		List<TSocket> list = new ArrayList<TSocket>();
		GenericConnectionProvider gcp = GenericConnectionProvider.init(); 
		for(int i=0; i<5; i++) {
			TSocket socket = gcp.getConnection();
			list.add(socket);
		}
		
		for(int i=0; i<list.size(); i++) {
			TSocket socket = list.get(i);
			gcp.closeConnection(socket);
		}
		
		for(int i=0; i<6; i++) {
			TSocket socket = gcp.getConnection();
			list.add(socket);
		}
	}
}
