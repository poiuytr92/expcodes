package exp.libs.utils.pub;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(IOUtils.class);
	
	/** 私有化构造函数 */
	protected IOUtils() {}
	
	public static boolean toFile(InputStream is, String savePath) {
		File saveFile = new File(savePath);
		FileUtils.createDir(saveFile.getParent());
		return toFile(is, saveFile);
	}
	
	public static boolean toFile(InputStream is, File saveFile) {
		boolean isOk = false;
		if(is != null && saveFile != null) {
			
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(saveFile);
				byte[] buffer = new byte[10240];
				int ch = 0;
				while ((ch = is.read(buffer)) != -1) {
					fos.write(buffer, 0, ch);
				}
				fos.flush();
				isOk = true;
				
			} catch (Exception e) {
				log.error("保存流式数据到文件 [{}] 失败.", saveFile.getAbsolutePath(), e);
				
			} finally {
				isOk = close(fos);
			}
		}
		return isOk;
	}
	
	public static boolean close(Closeable closeable) {
		boolean isOk = true;
		if (closeable != null) {
			try {
                closeable.close();
	        } catch (Exception e) {
	        	log.error("IO流关闭失败.", e);
	        	isOk = false;
	        }
		}
		return isOk;
    }
	
	public static boolean close(Statement statement) {
		boolean isOk = true;
		if (statement != null) {
			try {
				statement.close();
	        } catch (Exception e) {
	        	log.error("IO流关闭失败.", e);
	        	isOk = false;
	        }
		}
		return isOk;
    }
	
	public static boolean close(ResultSet resultSet) {
		boolean isOk = true;
		if (resultSet != null) {
			try {
				resultSet.close();
	        } catch (Exception e) {
	        	log.error("IO流关闭失败.", e);
	        	isOk = false;
	        }
		}
		return isOk;
    }
	
}
