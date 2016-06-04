package service.pool;

import org.apache.thrift.transport.TSocket;

/** 
 * @author 李军
 * @version 1.0
 * @datetime 2015-12-30 上午09:33:14 
 * 连接池接口
 */
public interface ConnectionProvider {

	/**
     * 取链接池中的一个链接
     * @return
     */
    public TSocket getConnection();
    
    /**
     * 返回链接
     * @param socket
     */
    public void closeConnection(TSocket socket);
    
    /**
     * 释放连接池
     */
    public void destroyPool();

}
