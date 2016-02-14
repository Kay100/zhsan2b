package com.sz.zhsan2b.desktop;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.sz.zhsan2b.core.entity.DamageRange;
import com.sz.zhsan2b.core.entity.Position;
import com.sz.zhsan2b.core.entity.DamageRange.DamageRangeType;
import com.sz.zhsan2b.core.GameMap;
import com.sz.zhsan2b.core.MapBuilder;

public class PositionTestCase {
	
	Position position;
	GameMap map = MapBuilder.buildMap(1l);

	@Test
	public void test() {
		position = new Position("25");
		assertEquals(position.x, 5);
		assertEquals(position.y, 2);
		assertTrue(position.equals(new Position(5,2)));
	}
	@Test
	public void testGetNodeWeight(){
		int testWeight =map.calculateNextNodeWeight(new Position(1,1));
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
	@Test
	public void testDamageRange(){
		DamageRange damageR = new DamageRange(DamageRangeType.LINE, new Position(2,2),null, 2, false, 1, 2);
		assertEquals(damageR.getDamageRangeList().size(), 0);
		for(Position p:damageR.getDamageRangeList()){
			
			System.out.println(p.toString());
		}

	}

}
