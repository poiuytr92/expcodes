package exp.libs.utils.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;





import org.apache.commons.io.IOUtils;

import exp.libs.utils.StrUtils;

/**
 * <PRE>
 * jar文件加载工具类
 * 案例：
 * 	File file = new File(JAR_FILE);
 * 	URLClassLoader load = JarLoaderUtils.getURLClassLoader(file);
 * 	Class<?> clazz = load.loadClass(LOAD_CLASS1);
 * 	ITest test = (ITest) clazz.newInstance();
 * 	test.print();
 * </PRE>
 * 
 */
//FIXME
@Deprecated
public class JarLoaderUtils {

	protected JarLoaderUtils() {}
	
	/**
	 * 加载jar文件
	 * 
	 * @param file
	 *            jar文件
	 * @return URLClassLoader
	 * @throws MalformedURLException
	 *             URL路径错误
	 */
	public static synchronized URLClassLoader getURLClassLoader(File file)
			throws MalformedURLException {
		String path = "file:" + file.getAbsolutePath().replace("\\", "/");
		URL[] urls = new URL[1];
		URL url = new URL(path);
		urls[0] = url;
		URLClassLoader loader = null;
		loader = new URLClassLoader(urls);
		return loader;
	}

	/**
	 * 加载目录下所有jar文件
	 * 
	 * @param libPath
	 *            jar文件目录
	 * @return URLClassLoader
	 * @throws MalformedURLException
	 *             URL路径错误
	 */
	public static synchronized URLClassLoader getURLClassLoader(String libPath)
			throws MalformedURLException {
		List<String> jars = listFiles(libPath);
		// System.out.println(jars);
		int size = jars.size();
		if (size == 0) {
			return null;
		}
		URL urls[] = new URL[size];
		for (int i = 0; i < size; i++) {
			String url = "file:" + jars.get(i);
			// System.out.println(url);
			urls[i] = new URL(url);
		}
		return new URLClassLoader(urls);
	}

	/**
	 * 查询目录下的所有jar文件
	 * 
	 * @param libPath
	 *            jar文件目录
	 * @return 有文件的Linux绝对路径
	 */
	private static List<String> listFiles(String libPath) {
		String[] extensions = { "jar" };
		File dir = new File(libPath);
		List<String> filePaths = new ArrayList<String>();
		Iterator<File> it = FileUtils.iterateFiles(dir, extensions, true);

		while (it.hasNext()) {
			String path = it.next().getAbsolutePath().replace("\\", "/");
			filePaths.add(path);
		}
		return filePaths;
	}

	/**
	 * 返回jar包中的所有文件
	 * 
	 * @param file
	 *            jar文件
	 * @return 文件路径集合
	 * @throws IOException
	 */
	public static List<String> getJarFiles(File file) throws IOException {
		JarFile jar = new JarFile(file);
		Enumeration<JarEntry> ens = jar.entries();
		List<String> list = new ArrayList<String>();
		while (ens.hasMoreElements()) {
			JarEntry e = ens.nextElement();
			list.add(e.getName());
		}
		return list;
	}

	/**
	 * 返回jar包中的所有文件
	 * 
	 * @param path
	 *            jar文件路径
	 * @return 文件路径集合
	 * @throws IOException
	 */
	public static List<String> getJarFiles(String path) throws IOException {
		return getJarFiles(new File(path));
	}

	/**
	 * 返回jar包中的所有指定后缀文件
	 * 
	 * @param file
	 *            jar文件
	 * @param suffix
	 *            文件后缀
	 * @return 文件路径集合
	 * @throws IOException
	 */
	public static List<String> getJarFiles(File file, String suffix)
			throws IOException {
		JarFile jar = new JarFile(file);
		Enumeration<JarEntry> ens = jar.entries();
		List<String> list = new ArrayList<String>();
		while (ens.hasMoreElements()) {
			JarEntry e = ens.nextElement();
			String path = e.getName();
			if (path.toLowerCase().endsWith(suffix.toLowerCase())) {
				list.add(path);
			}
		}
		return list;
	}

	/**
	 * 返回jar包中的所有指定后缀文件
	 * 
	 * @param path
	 *            jar文件路径
	 * @param suffix
	 *            文件后缀
	 * @return 文件路径集合
	 * @throws IOException
	 */
	public static List<String> getJarFiles(String path, String suffix)
			throws IOException {
		return getJarFiles(new File(path), suffix);
	}
	
	/**
	 * 
	 * 拷贝jar包内的文件
	 * 
	 * @param sourcePath
	 *            文件路径 例如：com/catt/*.xml
	 * @param targetPath
	 *            目标路径 例如：./copy/file.log
	 * @param encoding
	 *            编码
	 * @throws IOException
	 *             IO异常
	 */
	public static void copyJarFile(String sourcePath, String targetPath) 
			throws IOException {
		copyJarFile(null, sourcePath, targetPath);
	}
	
	/**
	 * 
	 * 拷贝jar包内的文件
	 * 
	 * @param _class
	 *            类对象
	 * @param sourcePath
	 *            文件路径 例如：com/catt/*.xml
	 * @param targetPath
	 *            目标路径 例如：./copy/file.log
	 * @param encoding
	 *            编码
	 * @throws IOException
	 *             IO异常
	 */
	@SuppressWarnings("rawtypes")
	public static void copyJarFile(Class _class, String sourcePath,
			String targetPath) throws IOException {
		InputStream in = null;
		if (_class == null) {
			in = JarLoaderUtils.class.getClassLoader().getResourceAsStream(
					sourcePath);
		} else {
			in = _class.getClassLoader().getResourceAsStream(sourcePath);
		}
		FileUtils.createFile(targetPath);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(targetPath);
			byte[] buff = new byte[1024];
			int rc = 0;
			while ((rc = in.read(buff, 0, 1024)) > 0) {
				out.write(buff, 0, rc);
			}
			out.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 
	 * 读取jar包内文件内容
	 * 
	 * @param _class
	 *            类对象
	 * @param sourcePath
	 *            文件路径 例如：./com/catt/*.xml
	 * @param encoding
	 *            编码
	 * @throws IOException
	 *             IO异常
	 */
	@SuppressWarnings("rawtypes")
	public static String readJarFile(Class _class, String sourcePath,
			String encoding) throws IOException {

		if (StrUtils.isEmpty(encoding)) {
			encoding = "ISO-8859-1";
		}

		InputStream in = null;
		if (_class == null) {
			in = JarLoaderUtils.class.getClassLoader().getResourceAsStream(
					sourcePath);
		} else {
			in = _class.getClassLoader().getResourceAsStream(sourcePath);
		}
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		int rc = 0;
		while ((rc = in.read(buff, 0, 1024)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		String str = new String(swapStream.toByteArray(), encoding);
		
		in.close();
		swapStream.close();
		return str;
	}
}
