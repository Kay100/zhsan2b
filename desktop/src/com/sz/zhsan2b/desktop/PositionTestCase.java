package com.sz.zhsan2b.desktop;

import static org.junit.Assert.*;

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

}
