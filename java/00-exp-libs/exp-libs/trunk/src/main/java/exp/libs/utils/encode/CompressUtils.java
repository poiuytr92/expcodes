package exp.libs.utils.encode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exp.libs.envm.Charset;
import exp.libs.envm.FileType;
import exp.libs.utils.StrUtils;
import exp.libs.utils.io.FileUtils;
import exp.libs.utils.num.BODHUtils;

/**
 * <PRE>
 * 压缩工具类
 * </PRE>
 * <B>PROJECT：</B> exp-libs
 * <B>SUPPORT：</B> EXP
 * @version   1.0 2016-01-19
 * @author    EXP: 272629724@qq.com
 * @since     jdk版本：jdk1.6
 */
public class CompressUtils {

	/** 日志器 */
	private final static Logger log = LoggerFactory.getLogger(CompressUtils.class);
	
	/** 默认压缩编码 */
	public final static String DEFAULT_ENCODE = Charset.UTF8;
	
	/** 私有化构造函数 */
	protected CompressUtils() {}
	
	public static boolean compress(String srcPath) {
		return compress(srcPath, StrUtils.concat(
				new File(srcPath).getPath(), FileType.ZIP.EXT), FileType.ZIP);
	}
	
	public static boolean compress(String srcPath, FileType fileType) {
		fileType = (fileType == null ? FileType.ZIP : fileType);
		return compress(srcPath, StrUtils.concat(
				new File(srcPath).getPath(), fileType.EXT), fileType);
	}
	
	public static boolean compress(String srcPath, String destPath) {
		return compress(srcPath, destPath, null);
	}
	
	public static boolean compress(String srcPath, String destPath, FileType fileType) {
		if(StrUtils.isEmpty(srcPath) || StrUtils.isEmpty(destPath)){
			log.warn("压缩文件 [{}] 失败：源路径/目标路径为空.");
			return false;
		}
		
		String ext = (fileType != null ? 
				fileType.EXT : FileUtils.getExtension(destPath));
		if(StrUtils.isEmpty(ext)) {
			log.warn("压缩文件 [{}] 失败：未指定压缩格式.");
			return false;
		}
		
		boolean isOk = false;
		try{
			if (FileType.ZIP.EXT.equals(ext)) {
				isOk = toZip(srcPath, destPath);
				
			} else if (FileType.TAR.EXT.equals(ext)) {
				isOk = toTar(srcPath, destPath);
				
			} else if (FileType.GZ.EXT.equals(ext)) {
				isOk = toGZip(srcPath, destPath);
				
			} else if (FileType.BZ2.EXT.equals(ext)) {
				isOk = toBZ2(srcPath, destPath);
				
			} else {
				log.warn("压缩文件 [{}] 失败： 不支持的压缩格式 [{}].", srcPath, ext);
			}
		} catch(Exception e){
			log.error("压缩文件 [{}] 到 [{}] 失败.", srcPath, destPath, e);
		}
		return isOk;
	}

	public static boolean toZip(String srcPath, String zipPath)  {
		boolean isOk = true;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipArchiveOutputStream zos = null;
		
		try {
			fos = new FileOutputStream(new File(zipPath));
			bos = new BufferedOutputStream(fos);
			zos = new ZipArchiveOutputStream(bos);
			addFileToZip(zos, new File(srcPath), "");
			zos.finish();
			
		} catch(Exception e){
			isOk = false;
			log.error("[ERROR-ZIP] 压缩文件 [{}] 到 [{}] 失败.", srcPath, zipPath, e);
			
		} finally {
			if(zos != null){
				try {
					zos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-ZIP] 关闭文件压缩流失败: [{}].", zipPath, e);
				}
			}
			
			if(bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-ZIP] 关闭文件压缩流失败: [{}].", zipPath, e);
				}
			}
			
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-ZIP] 关闭文件压缩流失败: [{}].", zipPath, e);
				}
			}
		}
		return isOk;
	}
	
	public static boolean toZip(String[] srcPaths, String zipPath) {
		if(srcPaths == null || srcPaths.length <= 0 || 
				StrUtils.isEmpty(zipPath)) {
			return false;
		}
		
		if(!FileType.ZIP.EXT.equals(FileUtils.getExtension(zipPath))) {
			return false;
		}
		
		boolean isOk = true;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipArchiveOutputStream zos = null;
		try {
			fos = new FileOutputStream(new File(zipPath));
			bos = new BufferedOutputStream(fos);
			zos = new ZipArchiveOutputStream(bos);
			for(String srcPath : srcPaths) {
				addFileToZip(zos, new File(srcPath), "");
			}
			zos.finish();
			
		} catch(Exception e){
			isOk = false;
			log.error("[ERROR-ZIP] 压缩文件集到 [{}] 失败.", zipPath, e);
			
		} finally {
			if(zos != null){
				try {
					zos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-ZIP] 关闭文件压缩流失败: [{}].", zipPath, e);
				}
			}
			
			if(bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-ZIP] 关闭文件压缩流失败: [{}].", zipPath, e);
				}
			}
			
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-ZIP] 关闭文件压缩流失败: [{}].", zipPath, e);
				}
			}
		}
		return isOk;
	}
	
	private static void addFileToZip(ZipArchiveOutputStream zos, 
			File file, String base) throws IOException {
		String entryName = StrUtils.concat(base, file.getName());
		ZipArchiveEntry zipEntry = new ZipArchiveEntry(file, entryName);
		zos.putArchiveEntry(zipEntry);
		
		if (file.isFile()) {
			FileInputStream fis = new FileInputStream(file);
			IOUtils.copy(fis, zos);
			zos.closeArchiveEntry();
			fis.close();
			
		} else {
			zos.closeArchiveEntry();
			File[] cFiles = file.listFiles();
			for (File cFile : cFiles) {
				addFileToZip(zos, cFile, StrUtils.concat(entryName, "/"));
			}
		}
	}
	
	public static boolean toTar(String srcPath, String tarPath) {
		boolean isOk = true;
		File srcFile = new File(srcPath);
		File tarFile = FileUtils.createFile(tarPath);
		
		FileOutputStream fos = null;
		TarArchiveOutputStream tos = null;
		try{
			fos = new FileOutputStream(tarFile);
			tos = new TarArchiveOutputStream(fos);
			addFileToTar(tos, srcFile, "");
			tos.flush();
			
		} catch(Exception e){
			isOk = false;
			log.error("[ERROR-TAR] 压缩文件 [{}] 到 [{}] 失败.", srcPath, tarPath, e);
			
		} finally {
			if(tos != null){
				try {
					tos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-TAR] 关闭文件压缩流失败: [{}].", tarPath, e);
				}
			}
			
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-TAR] 关闭文件压缩流失败: [{}].", tarPath, e);
				}
			}
		}
		return isOk;
	}
	
	public static boolean toTar(String[] srcPaths, String tarPath) {
		if(srcPaths == null || srcPaths.length <= 0 || 
				StrUtils.isEmpty(tarPath)) {
			return false;
		}
		
		if(!FileType.TAR.EXT.equals(FileUtils.getExtension(tarPath))) {
			return false;
		}
		
		boolean isOk = true;
		File tarFile = FileUtils.createFile(tarPath);
		FileOutputStream fos = null;
		TarArchiveOutputStream tos = null;
		try{
			fos = new FileOutputStream(tarFile);
			tos = new TarArchiveOutputStream(fos);
			for(String srcPath : srcPaths) {
				File srcFile = new File(srcPath);
				addFileToTar(tos, srcFile, "");
			}
			tos.flush();
			
		} catch(Exception e){
			isOk = false;
			log.error("[ERROR-TAR] 压缩文件集到 [{}] 失败.", tarPath, e);
			
		} finally {
			if(tos != null){
				try {
					tos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-TAR] 关闭文件压缩流失败: [{}].", tarPath, e);
				}
			}
			
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-TAR] 关闭文件压缩流失败: [{}].", tarPath, e);
				}
			}
		}
		return isOk;
	}
	
	private static void addFileToTar(TarArchiveOutputStream tos, 
			File file, String base) throws IOException {
		String entryName = StrUtils.concat(base, file.getName());
		TarArchiveEntry tarEntry = new TarArchiveEntry(file, entryName);
		tos.putArchiveEntry(tarEntry);
		
		if (file.isFile()) {
			FileInputStream fis = new FileInputStream(file);
			IOUtils.copy(fis, tos);
			tos.closeArchiveEntry();
			fis.close();
			
		} else {
			tos.closeArchiveEntry();
			File[] cFiles = file.listFiles();
			for (File cFile : cFiles) {
				addFileToTar(tos, cFile, StrUtils.concat(entryName, "/"));
			}
		}
	}

	public static boolean toGZip(String srcPath, String gzipPath) {
		File srcFile = new File(srcPath);
		if (srcFile.isDirectory()) {
			log.warn("[ERROR-GZIP] GZ不支持对目录压缩: [{}]", srcPath);
			return false;
		}
		
		boolean isOk = true;
		BufferedInputStream bis = null;
		GzipCompressorOutputStream gos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(srcFile));
			gos = new GzipCompressorOutputStream(new FileOutputStream(gzipPath));

			int cnt = -1;
			byte[] buf = new byte[1024];
			while ((cnt = bis.read(buf)) > 0) {
				gos.write(buf, 0, cnt);
			}
			gos.flush();
			
		} catch(Exception e){
			isOk = false;
			log.error("[ERROR-GZIP] 压缩文件 [{}] 到 [{}] 失败.", srcPath, gzipPath, e);
			
		} finally {
			if (gos != null) {
				try {
					gos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-GZIP] 关闭文件输出流失败: [{}].", gzipPath, e);
				}
			}
			
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-GZIP] 关闭文件输入流失败: [{}].", srcFile, e);
				}
			}
		}
		return isOk;
	}
	
	public static boolean toBZ2(String srcPath, String bzPath) {
		File srcFile = new File(srcPath);
		if (srcFile.isDirectory()) {
			log.warn("[ERROR-BZ2] BZ2不支持对目录压缩: [{}]", srcPath);
			return false;
		}
		
		boolean isOk = true;
		InputStream is = null;
		OutputStream os = null;
		BZip2CompressorOutputStream bos = null;
		try {
			is = new FileInputStream(srcFile);
			os = new FileOutputStream(new File(bzPath));
			bos = new BZip2CompressorOutputStream(os);

			int count;
			byte data[] = new byte[1024];
			while ((count = is.read(data, 0, 1024)) != -1) {
				bos.write(data, 0, count);
			}
			bos.finish();
			bos.flush();
			
		} catch (Exception e) {
			isOk = false;
			log.error("[ERROR-BZ2] 压缩文件 [{}] 到 [{}] 失败.", srcPath, bzPath, e);
			
		} finally {
			if(bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-BZ2] 关闭文件压缩流失败: [{}].", bzPath, e);
				}
			}
			
			if(os != null){
				try {
					os.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-BZ2] 关闭文件输出流失败: [{}].", bzPath, e);
				}
			}
			
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-BZ2] 关闭文件输入流失败: [{}].", srcFile, e);
				}
			}
		}
		return isOk;
	}
	
	public static boolean extract(String srcPath) {
		return extract(srcPath, new File(srcPath).getParent(), null);
	}
	
	public static boolean extract(String srcPath, FileType fileType) {
		return extract(srcPath, new File(srcPath).getParent(), fileType);
	}
	
	public static boolean extract(String srcPath, String destPath) {
		return extract(srcPath, destPath, null);
	}
	
	public static boolean extract(String srcPath, String destPath, FileType fileType) {
		if(StrUtils.isEmpty(srcPath) || StrUtils.isEmpty(destPath)){
			log.warn("解压文件 [{}] 失败：源路径/目标路径为空.");
			return false;
		}
		
		String headMsg = FileUtils.getHeadMsg(srcPath).toUpperCase();
		String ext = FileUtils.getExtension(srcPath).toLowerCase();
		
		boolean isOk = false;
		try {
			if (FileType.ZIP.HEAD_MSG.equals(headMsg) || 
					FileType.ZIP.EXT.equals(ext)) {
				isOk = unZip(srcPath, destPath);
				
			} else if (FileType.TAR.HEAD_MSG.equals(headMsg) || 
					FileType.TAR.EXT.equals(ext)) {
				isOk = unTar(srcPath, destPath);
				
			} else if (FileType.GZ.HEAD_MSG.equals(headMsg) || 
					FileType.GZ.EXT.equals(ext)) {
				isOk = unGZip(srcPath, destPath);
				
			} else if (FileType.BZ2.HEAD_MSG.equals(headMsg) || 
					FileType.BZ2.EXT.equals(ext)) {
				isOk = unBZ2(srcPath, destPath);
				
			} else {
				log.warn("解压文件 [{}] 失败： 不支持的压缩格式 [{}].", srcPath, ext);
			}
		} catch (Exception e) {
			log.error("解压文件 [{}] 到 [{}] 失败.", srcPath, destPath, e);
		}
		return isOk;
	}

	public static boolean unZip(String zipPath, String destPath) {
		boolean isOk = true;
		destPath = getDestPath(zipPath, destPath);
		
		File zipFile = new File(zipPath);
		File unZipDir = FileUtils.createDir(destPath);
		ZipFile zip = null;
		try {
			zip = new ZipFile(zipFile);
			
			Enumeration<ZipArchiveEntry> entries = zip.getEntries();
			while (entries.hasMoreElements()) {
				ZipArchiveEntry zipEntry = entries.nextElement();
				String name = zipEntry.getName().trim();
				name = name.replace('\\', '/').replaceFirst("[^/]+/", "");
				File destFile = new File(unZipDir, name);
				
				// 所解压的是目录
				if (name.endsWith("/")) {
					if (!destFile.isDirectory() && !destFile.mkdirs()) {
						isOk = false;
						break;
					}
					continue;

				// 所解压的是文件, 先创建其所有祖先目录
				} else if (name.indexOf('/') != -1) {
					File parentDir = destFile.getParentFile();
					if (!parentDir.isDirectory() && !parentDir.mkdirs()) {
						isOk = false;
					}
				}

				// 解压文件
				FileOutputStream fos = new FileOutputStream(destFile);
				InputStream is = zip.getInputStream(zipEntry);
				int cnt = -1;
				byte[] buf = new byte[1024];
				while ((cnt = is.read(buf)) != -1) {
					fos.write(buf, 0, cnt);
				}
				fos.close();
			}
		} catch (Exception e) {
			isOk = false;
			log.error("[ERROR-ZIP] 解压文件 [{}] 到 [{}] 失败.", zipPath, destPath, e);
			
		} finally {
			if (zip != null) {
				try {
					zip.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-ZIP] 关闭文件压缩流失败: [{}].", zipPath, e);
				}
			}
		}
		return isOk;
	}

	public static boolean unTar(String tarPath, String destPath) {
		boolean isOk = true;
		destPath = getDestPath(tarPath, destPath);
		
		File tarFile = new File(tarPath);
		File unTarDir = new File(destPath);
		unTarDir.mkdirs();
		
		TarArchiveInputStream tis = null;
		try {
			tis = new TarArchiveInputStream(new FileInputStream(tarFile));
			TarArchiveEntry tarEntry = null;
			while ((tarEntry = tis.getNextTarEntry()) != null) {
				String name = tarEntry.getName().trim();
				name = name.replace('\\', '/').replaceFirst("[^/]+/", "");
				File destFile = new File(unTarDir, name);
				
				// 所解压的是目录
				if (name.endsWith("/")) {
					if (!destFile.isDirectory() && !destFile.mkdirs()) {
						isOk = false;
						break;
					}
					continue;

				// 所解压的是文件, 先创建其所有祖先目录
				} else if (name.indexOf('/') != -1) {
					File parentDir = destFile.getParentFile();
					if (!parentDir.isDirectory() && !parentDir.mkdirs()) {
						isOk = false;
					}
				}

				// 解压文件
				FileOutputStream fos = new FileOutputStream(destFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				int count = -1;
				byte buf[] = new byte[1024];
				while ((count = tis.read(buf)) != -1) {
					bos.write(buf, 0, count);
				}
				bos.close();
				fos.close();
			}
		} catch (Exception e) {
			isOk = false;
			log.error("[ERROR-TAR] 解压文件 [{}] 到 [{}] 失败.", tarPath, destPath, e);
			
		} finally {
			if (tis != null) {
				try {
					tis.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-TAR] 关闭文件压缩流失败: [{}].", tarPath, e);
				}
			}
		}
		return isOk;
	}

	public static boolean unGZip(String gzipPath, String destPath) {
		boolean isOk = true;
		destPath = getDestPath(gzipPath, destPath);
		
		GZIPInputStream gis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		try {
			gis = new GZIPInputStream(new FileInputStream(gzipPath));
			bis = new BufferedInputStream(gis);
			fos = new FileOutputStream(destPath);

			int cnt = 0;
			byte[] buf = new byte[1024];
			while ((cnt = bis.read(buf)) != -1) {
				fos.write(buf, 0, cnt);
			}
		} catch (Exception e) {
			isOk = false;
			log.error("[ERROR-GZIP] 解压文件 [{}] 到 [{}] 失败.", gzipPath, destPath, e);
			
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-GZIP] 关闭文件输出流失败: [{}].", destPath, e);
				}
			}
			
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-GZIP] 关闭文件输入流失败: [{}].", gzipPath, e);
				}
			}
			
			if (gis != null) {
				try {
					gis.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-GZIP] 关闭文件输入流失败: [{}].", gzipPath, e);
				}
			}
		}
		return isOk;
	}

	public static boolean unBZ2(String bzPath, String destPath) {
		boolean isOk = true;
		destPath = getDestPath(bzPath, destPath);
		
		BZip2CompressorInputStream bzis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		try {
			bzis = new BZip2CompressorInputStream(new FileInputStream(bzPath));
			bis = new BufferedInputStream(bzis);
			fos = new FileOutputStream(destPath);

			int cnt = 0;
			byte[] buf = new byte[1024];
			while ((cnt = bis.read(buf)) != -1) {
				fos.write(buf, 0, cnt);
			}
		} catch (Exception e) {
			isOk = false;
			log.error("[ERROR-BZ2] 解压文件 [{}] 到 [{}] 失败.", bzPath, destPath, e);
			
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-BZ2] 关闭文件输出流失败: [{}].", destPath, e);
				}
			}
			
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-BZ2] 关闭文件输入流失败: [{}].", bzPath, e);
				}
			}
			
			if (bzis != null) {
				try {
					bzis.close();
				} catch (IOException e) {
					isOk = false;
					log.error("[ERROR-BZ2] 关闭文件输入流失败: [{}].", bzPath, e);
				}
			}
		}
		return isOk;
	}

	private static String getDestPath(String srcPath, String destPath) {
		String targetPath = "";
		if(StrUtils.isNotEmpty(srcPath) && StrUtils.isNotEmpty(destPath)) {
			targetPath = destPath;
			if(FileUtils.isDirectory(destPath)) {
				String name = FileUtils.getName(srcPath);
				int pos = name.lastIndexOf('.');
				name = (pos > 0 ? name.substring(0, pos) : name);
				targetPath = StrUtils.concat(targetPath, File.separator, name);
			}
		}
		return targetPath;
	}
	
	/**
	 * 把字符串以【GZIP方式】进行压缩，并得到【压缩串】的【16进制表示形式】.
	 * 
	 * 被压缩的字符串越长，压缩率越高。
	 * 对于很短的字符串，压缩后可能变得更大，因为GZIP的文件头需要存储压缩字典（约20字节）
	 * 
	 * 返回16进制的表示形式是为了便于对压缩串进行存储、复制等，
	 * 否则一堆乱码是不便于处理的。
	 * 但缺点是16进制显示形式会直接把压缩串的长度在原来基础上扩展1倍（原本1个字节被拆分成高低位两个字符）。
	 * 
	 * @param str 原字符串（默认为UTF-8编码）
	 * @return 【压缩串】的【16进制表示形式】, 压缩失败则返回空串（非null）
	 */
	public static String toGZipString(final String str) {
		return toGZipString(str, DEFAULT_ENCODE);
	}
	
	/**
	 * 把字符串以【GZIP方式】进行压缩，并得到【压缩串】的【16进制表示形式】.
	 * 
	 * 被压缩的字符串越长，压缩率越高。
	 * 对于很短的字符串，压缩后可能变得更大，因为GZIP的文件头需要存储压缩字典（约20字节）
	 * 
	 * 返回16进制的表示形式是为了便于对压缩串进行存储、复制等，
	 * 否则一堆乱码是不便于处理的。
	 * 但缺点是16进制显示形式会直接把压缩串的长度在原来基础上扩展1倍（原本1个字节被拆分成高低位两个字符）。
	 * 
	 * @param str 原字符串
	 * @param encode 原字符串编码
	 * @return 【压缩串】的【16进制表示形式】, 压缩失败则返回空串（非null）
	 */
	public static String toGZipString(final String str, final String encode) {
		String hex = "";
		if (str != null && str.length() > 0) {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				GZIPOutputStream gos = new GZIPOutputStream(bos);
				gos.write(str.getBytes(encode));
				gos.close();
				bos.close();
				hex = BODHUtils.toHex(bos.toByteArray());
				
			} catch (Exception e) {
				log.error("压缩字符串失败: [{}]", StrUtils.showSummary(str), e);
			}
		}
		return hex;
	}
	
	/**
	 * 把【16进制表示形式】的、以【GZIP方式】压缩的【压缩串】还原为原字符串（默认原字符串的编码方式为UTF-8）
	 * 
	 * @param hex 【16进制表示形式】的、以【GZIP方式】压缩的【压缩串】
	 * @return 原字符串, 还原失败则返回空串（非null）
	 */
	public static String unGZipString(final String hex) {
		return unGZipString(hex, DEFAULT_ENCODE);
	}

	/**
	 * 把【16进制表示形式】的、以【GZIP方式】压缩的【压缩串】还原为原字符串
	 * 
	 * @param hex 【16进制表示形式】的、以【GZIP方式】压缩的【压缩串】
	 * @param encode 原字符串的编码方式
	 * @return 原字符串, 还原失败则返回空串（非null）
	 */
	public static String unGZipString(String hex, final String encode) {
		String str = "";
		if (hex == null) {
			return str;
		}
		
		hex = hex.trim();
		if(hex.length() <= 0 || hex.length() % 2 == 1) {
			return str;
		}
			
		byte[] bytes = BODHUtils.toBytes(hex);
		if(bytes != null) {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				GZIPInputStream gis = new GZIPInputStream(bis);
				byte[] buf = new byte[1024];
				int cnt = -1;
				while ((cnt = gis.read(buf)) >= 0) {
					bos.write(buf, 0, cnt);
				}
				str = bos.toString(encode);
				bos.close();
				gis.close();
				bis.close();
				
			} catch (Exception e) {
				log.error("解压字符串失败: [{}]", StrUtils.showSummary(hex), e);
			}
		}
		return str;
	}
	
}