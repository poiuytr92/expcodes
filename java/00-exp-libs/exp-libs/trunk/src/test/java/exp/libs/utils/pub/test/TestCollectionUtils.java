package exp.libs.utils.pub.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exp.libs.utils.other.CollectionUtils;

public class TestCollectionUtils {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRemoveDuplicate() {
		fail("Not yet implemented");
	}

	@Test
	public void testCutbackNull() {
		String[] array = new String[] { null, "aaa", "bbb", null, "ccc", null, "ddd" };
		System.out.println(CollectionUtils.toString(array));
		System.out.println(CollectionUtils.cutbackNull(array));
		System.out.println(CollectionUtils.toString(array));
		
		array = new String[] { null, "aaa", "bbb", null, null, null, "ccc", null, "ddd", null };
		System.out.println(CollectionUtils.toString(array));
		System.out.println(CollectionUtils.cutbackNull(array));
		System.out.println(CollectionUtils.toString(array));
		
		array = new String[] { "aaa", "bbb", null, null, null, "ccc", null, "ddd", null };
		System.out.println(CollectionUtils.toString(array));
		System.out.println(CollectionUtils.cutbackNull(array));
		System.out.println(CollectionUtils.toString(array));
		
		array = new String[] { "aaa", null, null, null, null };
		System.out.println(CollectionUtils.toString(array));
		System.out.println(CollectionUtils.cutbackNull(array));
		System.out.println(CollectionUtils.toString(array));
		
		array = new String[] { null, null, null, null };
		System.out.println(CollectionUtils.toString(array));
		System.out.println(CollectionUtils.cutbackNull(array));
		System.out.println(CollectionUtils.toString(array));
		
		array = new String[] { "aaa", "bbb", "ccc", "ddd" };
		System.out.println(CollectionUtils.toString(array));
		System.out.println(CollectionUtils.cutbackNull(array));
		System.out.println(CollectionUtils.toString(array));
		
		array = new String[] { };
		System.out.println(CollectionUtils.toString(array));
		System.out.println(CollectionUtils.cutbackNull(array));
		System.out.println(CollectionUtils.toString(array));
		
		array = null;
		System.out.println(CollectionUtils.toString(array));
		System.out.println(CollectionUtils.cutbackNull(array));
		System.out.println(CollectionUtils.toString(array));
	}

	@Test
	public void testCheckSize() {
		fail("Not yet implemented");
	}

	@Test
	public void testIntersection() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubtraction() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnion() {
		fail("Not yet implemented");
	}

	@Test
	public void testCompareCollectionOfECollectionOfE() {
		fail("Not yet implemented");
	}

	@Test
	public void testCompareListOfEListOfE() {
		fail("Not yet implemented");
	}

}
