package com.sz.zhsan2b.desktop;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.sz.zhsan2b.core.Map;
import com.sz.zhsan2b.core.MapBuilder;
import com.sz.zhsan2b.core.Position;

public class PositionTestCase {
	
	Position position;
	Map map = MapBuilder.buildMap(1l);

	@Test
	public void test() {
		position = new Position("25");
		assertEquals(position.x, 5);
		assertEquals(position.y, 2);
		assertTrue(position.equal(new Position(5,2)));
	}
	@Test
	public void testGetEdgeWeight(){
		int testWeight =map.calculateNextEdgeWeight(new Position(2,1), new Position(1,1));
		assertEquals(testWeight, 5);
	}
	@Test
	public void testArray(){
		int[] a = new int[5];
		a[1] = 1;
		assertEquals(a.length, 5);
	}
	@Test
	public void testString(){
		String testString = StringUtils.replace("women%1women", "%1", "women");
		assertEquals(testString, "womenwomenwomen");
	}	

}
