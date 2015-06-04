package com.sz.zhsan2b.libgdx;

import com.sz.zhsan2b.core.BattleField;
import com.sz.zhsan2b.core.BattleProperties;
import com.sz.zhsan2b.core.Command;
import com.sz.zhsan2b.core.Command.ACTION_KIND;
import com.sz.zhsan2b.core.MilitaryKind;
import com.sz.zhsan2b.core.PLAYER_TYPE;
import com.sz.zhsan2b.core.Position;
import com.sz.zhsan2b.core.Troop;

public class BattleFieldOperationStage {

	private BattleField battleField;

	public BattleFieldOperationStage(BattleField battleField) {
		this.battleField = battleField;
		// 测试
		BattleProperties bp = new BattleProperties();
		bp.ack = 20;
		bp.def = 10;
		bp.hp = 100;
		bp.isXie = true;
		bp.move = 30;
		bp.range = 1;
		bp.speed = 20;
		BattleProperties bp2= new BattleProperties(bp);
		bp2.range=3;
		
		Command com1 = new Command(new Position(3, 4));
		Command com2 = new Command(new Position(4, 6));
		com1.actionKind=ACTION_KIND.ATTACK;
		com2.actionKind=ACTION_KIND.ATTACK;

		Troop tr1= new Troop(new MilitaryKind(0), bp, new Position(6, 5),
				com1, PLAYER_TYPE.PLAYER.AI, battleField);
		
		Troop tr2= new Troop(new MilitaryKind(1), bp2, new Position(5, 9),
				com2, PLAYER_TYPE.PLAYER.PLAYER, battleField);
		tr2.setStepAttack(true);
		com1.object=tr2;
		com2.object=tr1;
		
	}

}
