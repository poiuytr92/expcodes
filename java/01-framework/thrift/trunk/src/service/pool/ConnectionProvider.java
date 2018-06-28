package service.pool;

import org.apache.thrift.transport.TSocket;

/**
 * 连接池接口
 * 
 * <br/><B>PROJECT : </B> thrift
 * <br/><B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a> 
 * @version   2015-12-28
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
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
