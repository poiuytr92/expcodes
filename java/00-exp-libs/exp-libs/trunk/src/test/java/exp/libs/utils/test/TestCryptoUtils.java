package exp.libs.utils.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exp.libs.envm.Charset;
import exp.libs.utils.encode.CryptoUtils;

public class TestCryptoUtils {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToMD5String() {
		String md5 = CryptoUtils.toMD5("测试MD5");
		System.out.println(md5);
	}
	
	@Test
	public void testToMD5StringString() {
		String md5 = CryptoUtils.toMD5("测试MD5", Charset.UTF8);
		System.out.println(md5);
		
		md5 = CryptoUtils.toMD5("测试MD5", Charset.GBK);
		System.out.println(md5);
	}

	@Test
	public void testToMD5StringArray() {
		String md5 = CryptoUtils.toMD5(new String[] { "测试", "MD5" });
		System.out.println(md5);
	}
	
	@Test
	public void testToMD5StringArrayString() {
		String md5 = CryptoUtils.toMD5(new String[] { "测试", "MD5" }, Charset.UTF8);
		System.out.println(md5);
		
		md5 = CryptoUtils.toMD5(new String[] { "测试", "MD5" }, Charset.GBK);
		System.out.println(md5);
	}

	@Test
	public void testTo16MD5() {
		String _32MD5 = CryptoUtils.toMD5("测试MD5");
		System.out.println(_32MD5);
		
		String _16MD5 = CryptoUtils.to16MD5(_32MD5);
		System.out.println(_16MD5);
	}

	@Test
	public void testToDESString() {
		String des = CryptoUtils.toDES("测试DES-123");
		System.out.println(des);
	}

	@Test
	public void testToDESStringString() {
		String des = CryptoUtils.toDES("测试DES-123", "test-key");
		System.out.println(des);
		
		des = CryptoUtils.toDES("测试DES-123", "<8bit");
		System.out.println(des);
	}

	@Test
	public void testToDESStringStringString() {
		String des = CryptoUtils.toDES("测试DES-123", "test-key", Charset.UTF8);
		System.out.println(des);
	}

	@Test
	public void testDeDESString() {
		String des = CryptoUtils.toDES("测试DES-123");
		System.out.println(des);
		
		String undes = CryptoUtils.deDES(des);
		System.out.println(undes);
	}

	@Test
	public void testDeDESStringString() {
		String des = CryptoUtils.toDES("测试DES-123", "test-key");
		System.out.println(des);
		
		String undes = CryptoUtils.deDES(des, "test-key");
		System.out.println(undes);
		
		undes = CryptoUtils.deDES(des, "error-key");
		System.out.println(undes);
		
		des = CryptoUtils.toDES("测试DES-456", "<8bit");
		System.out.println(des);
		undes = CryptoUtils.deDES(des, "<8bit");
		System.out.println(undes);
	}

	@Test
	public void testDeDESStringStringString() {
		String des = CryptoUtils.toDES("测试DES-123", "test-key", Charset.UTF8);
		System.out.println(des);
		
		String undes = CryptoUtils.deDES(des, "test-key", Charset.UTF8);
		System.out.println(undes);
		
		undes = CryptoUtils.deDES(des, "test-key", Charset.GBK);
		System.out.println(undes);
	}

}
