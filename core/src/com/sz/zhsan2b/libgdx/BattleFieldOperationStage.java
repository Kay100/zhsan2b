package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.sz.zhsan2b.core.BattleField;
import com.sz.zhsan2b.core.BattleField.State;
import com.sz.zhsan2b.core.BattleProperties;
import com.sz.zhsan2b.core.Command;
import com.sz.zhsan2b.core.Command.ACTION_KIND;
import com.sz.zhsan2b.core.MilitaryKind;
import com.sz.zhsan2b.core.PLAYER_TYPE;
import com.sz.zhsan2b.core.Position;
import com.sz.zhsan2b.core.Troop;
import com.sz.zhsan2b.libgdx.ContextMenu.Executable;

public class BattleFieldOperationStage {
	public class OnBattleProceedClicked implements Executable {

		@Override
		public void execute() {
			layerOperation.clear();
			transferToBattle();	
			Gdx.app.debug(TAG, "transferToBattle");
		}

	}	
	public static final String TAG = BattleFieldOperationStage.class.getName();	
	private BattleField battleField;
	private Table layerOperation;
	

	public Table getLayerOperation() {
		return layerOperation;
	}


	public BattleFieldOperationStage(BattleField battleField) {
		layerOperation = new Table();
		layerOperation.setLayoutEnabled(false);	
		layerOperation.setName("layerOperation");
		this.battleField = battleField;
		// 测试
		BattleProperties bp = new BattleProperties();
		bp.ack = 20;
		bp.def = 10;
		bp.hp = 100;
		bp.isXie = false;
		bp.move = 15;
		bp.range = 1;
		bp.speed = 20;
		BattleProperties bp2= new BattleProperties(bp);
		bp2.range=2;
		BattleProperties bp3= new BattleProperties(bp);
		
		Command com1 = new Command(new Position(3, 4));
		Command com2 = new Command(new Position(4, 6));
		Command com3 = new Command(new Position(4, 6));
		
		com1.actionKind=ACTION_KIND.MOVE;
		com2.actionKind=ACTION_KIND.ATTACK;
		com3.actionKind=ACTION_KIND.MOVE;

		Troop tr1= new Troop(new MilitaryKind(0), bp, new Position(6, 5),
				com1, PLAYER_TYPE.PLAYER.AI, battleField);
		
		Troop tr2= new Troop(new MilitaryKind(1), bp2, new Position(1, 9),
				com2, PLAYER_TYPE.PLAYER.PLAYER, battleField);
		Troop tr3= new Troop(new MilitaryKind(0), bp3, new Position(6, 4),
				com3, PLAYER_TYPE.PLAYER.AI, battleField);		
		tr2.setStepAttack(true);
		tr2.setMultiObject(true);
		com1.object=null;
		com2.object=tr1;
		com3.object=null;
	}
	public void transferToBattle() {
		battleField.refresh();
		battleField.state=State.BATTLE;
		Zhsan2b.battleScreen.startBattle();	
	}	

}
