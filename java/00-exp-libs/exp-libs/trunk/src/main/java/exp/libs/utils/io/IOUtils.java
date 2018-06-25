package exp.libs.utils.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.utils.num.UnitUtils;

/**
 * <PRE>
 * IO工具.
 * </PRE>
 * <B>PROJECT : </B> exp-libs
 * <B>SUPPORT : </B> <a href="http://www.exp-blog.com" target="_blank">www.exp-blog.com</a>
 * @version   1.0 # 2016-02-02
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class IOUtils {

	/** 日志�? */
	private final static Logger log = LoggerFactory.getLogger(IOUtils.class);
	
	/** 私有化构造函�? */
	protected IOUtils() {}
	
	/**
	 * 保存流式数据到字符串
	 * @param is 流式数据读取�?
	 * @return 若保存失败则返回空字符串""
	 */
	public static String toStr(InputStreamReader is) {
		StringBuilder sb = new StringBuilder();
		try {
			int len = 0;
			char[] buffer = new char[UnitUtils._1_MB];
			while((len = is.read(buffer)) != -1) {
				sb.append(buffer, 0, len);
			}
		} catch(Exception e) {
			log.error("保存流式数据失败.", e);
		}
		return sb.toString();
	}
	
	/**
	 * 保存流式数据到文�?
	 * @param is 流式数据通道
	 * @param savePath 保存文件位置
	 * @return true:保存成功; false:保存失败
	 */
	public static boolean toFile(InputStream is, String savePath) {
		File saveFile = new File(savePath);
		return toFile(is, saveFile);
	}
	
	/**
	 * 保存流式数据到文�?
	 * @param is 流式数据通道
	 * @param saveFile 保存文件对象
	 * @return true:保存成功; false:保存失败
	 */
	public static boolean toFile(InputStream is, File saveFile) {
		boolean isOk = false;
		if(is != null && saveFile != null) {
			FileUtils.createDir(saveFile.getParent());
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(saveFile);
				int len = 0;
				byte[] buffer = new byte[UnitUtils._1_MB];
				while((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				fos.flush();
				isOk = true;
				
			} catch (Exception e) {
				log.error("保存流式数据到文�? [{}] 失败.", saveFile.getAbsolutePath(), e);
				
			} finally {
				isOk = close(fos);
			}
		}
		return isOk;
	}
	
	/**
	 * 关闭IO�?
	 * @param closeable IO流接�?
	 * @return true:关闭成功; false:关闭失败
	 */
	public static boolean close(Closeable closeable) {
		boolean isOk = true;
		if (closeable != null) {
			try {
                closeable.close();
	        } catch (Exception e) {
	        	log.error("IO流关闭失�?.", e);
	        	isOk = false;
	        }
		}
		return isOk;
    }
	
	/**
	 * 关闭数据库sql接口
	 * @param statement 数据库sql接口
	 * @return true:关闭成功; false:关闭失败
	 */
	public static boolean close(Statement statement) {
		boolean isOk = true;
		if (statement != null) {
			try {
				statement.close();
	        } catch (Exception e) {
	        	log.error("IO流关闭失�?.", e);
	        	isOk = false;
	        }
		}
		return isOk;
    }
	
	/**
	 * 关闭数据库结果集接口
	 * @param resultSet 数据库结果集接口
	 * @return true:关闭成功; false:关闭失败
	 */
	public static boolean close(ResultSet resultSet) {
		boolean isOk = true;
		if (resultSet != null) {
			try {
				resultSet.close();
	        } catch (Exception e) {
	        	log.error("IO流关闭失�?.", e);
	        	isOk = false;
	        }
		}
		return isOk;
    }
	
}
