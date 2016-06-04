package exp.libs.utils.pub.test;

import static org.junit.Assert.*;

import org.junit.Test;

import exp.libs.utils.pub.NumUtils;

public class TestNumUtils {

	@Test
	public void testCompare() {
		fail("Not yet implemented");
	}

	@Test
	public void testNumToPrecent() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrecentToNum() {
		fail("Not yet implemented");
	}

	@Test
	public void testToInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testTolong() {
		fail("Not yet implemented");
	}

	@Test
	public void testToFloat() {
		fail("Not yet implemented");
	}

	@Test
	public void testToDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testIncrement() {
		fail("Not yet implemented");
	}

	@Test
	public void testToNegativeInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testToNegativelong() {
		fail("Not yet implemented");
	}

	@Test
	public void testToPositiveInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testToPositivelong() {
		fail("Not yet implemented");
	}

	@Test
	public void testMaxlonglong() {
		fail("Not yet implemented");
	}

	@Test
	public void testMaxIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testMinlonglong() {
		fail("Not yet implemented");
	}

	@Test
	public void testMinIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testCompress() {
		System.out.println(NumUtils.compress(null));
		System.out.println(NumUtils.compress(new long[] {}));
		System.out.println(NumUtils.compress(new long[] { 56L }));
		System.out.println(NumUtils.compress(new long[] { 1L, 3L, 5L, 7L, 9L }));
		System.out.println(NumUtils.compress(new long[] { 0L, 2L, 4L, 6L, 8L }));
		System.out.println(NumUtils.compress(new long[] { 0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L }));
		System.out.println(NumUtils.compress(new long[] { 1L, 2L, 3L, 5L, 6L, 8L }));
		System.out.println(NumUtils.compress(new long[] { 1L, 2L, 3L, 3L, 5L, 6L, 8L }));
		System.out.println(NumUtils.compress(new long[] { 1L, 2L, 3L, 5L, 6L, 9L }));
		System.out.println(NumUtils.compress(new long[] { 1L, 2L, 3L, 5L, 6L, 8L, 10L }));
		System.out.println(NumUtils.compress(new long[] { 1L, 2L, 3L, 5L, 6L, 8L, 9L }));
	}

}
