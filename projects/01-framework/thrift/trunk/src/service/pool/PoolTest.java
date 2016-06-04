package service.pool;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.transport.TSocket;

/** 
 * @author 李军
 * @version 1.0
 * @datetime 2015-12-30 下午03:35:59 
 * 类说明 
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
