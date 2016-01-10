package com.sz.zhsan2b.core;


import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sz.zhsan2b.core.entity.BattleField;
import com.sz.zhsan2b.core.entity.GameStateData;

import com.sz.zhsan2b.core.entity.Troop;
import com.sz.zhsan2b.core.entity.BattleField.SYN_TYPE;
import com.sz.zhsan2b.core.entity.BattleField.State;

@Component
public class BattleFieldManager {
	private static Logger logger = LoggerFactory.getLogger(BattleFieldManager.class);
	private BattleField battleField = GameContext.getBattleField();
	
	
	private TroopManager troopManager;
	private Kryo kryo;
	
	@PersistenceContext
	private EntityManager em;
	
	public BattleFieldManager() {
		init();
	}

	public void init() {

		//test,暂时是battle状态
		battleField.state=State.OPERATE;
		battleField.loadMaps();	
		
	}
	//开始一轮战斗
	public void startBattle(){
		battleField.clearStepActionList();
		troopManager.refresh();
		calculateBattle();
	}

	//进行一轮战斗
	public void calculateBattle(){
		Metamodel model = em.getEntityManagerFactory().getMetamodel();

		for (EntityType entityType : model.getEntities()) {
			String entityName = entityType.getName();
			em.createQuery("select o from " + entityName + " o").getResultList();
			System.out.println("ok: " + entityName);

		}			
		while(!isAllTroopsActioned()){
			//重置命令的目标位置为目标部队的位置
			for(Troop tr:battleField.getTroopList()){
				Troop object = tr.getCommand().object;
				if(object!=null){
					tr.getCommand().objectPosition.setPosition(object.getPosition());
				}
			}
			//主循环
			
			for(int i= 0,size=battleField.getTroopList().size;i<size;i++){
				Troop curTr = battleField.getTroopList().get(i);
				if(curTr.getCommand().isCompeted==true)
					continue;
				switch(curTr.getCommand().actionKind){
				case ATTACK:
					if(!troopManager.attackObject(curTr)){
						moveOneStepToAttack(curTr,true);
					}
					break;
				case CAST:
					//暂时不实现
					
					break;
				case MOVE:
					moveOneStepToAttack(curTr,false);
					break;
				case NONE:
					curTr.setCommandComplete();
					break;
				}
			}
			
		}
	}

	private void moveOneStepToAttack(Troop curTr,boolean isAttackFirst) {
		if(troopManager.moveToAttackPositionByOneStep(isAttackFirst,curTr)){
				troopManager.oneRandomAttack(curTr);
		}
	}
	

	private boolean isAllTroopsActioned() {
		boolean allActioned = true;
		for(Troop tr:battleField.getTroopList()){
			if(tr.getCommand()!=null&&!tr.getCommand().isCompeted){
				allActioned = false;
			}
		}
		
		
		return allActioned;
	}
	
	public void deleteDestroyedTroops(){
		troopManager.deleteDestroyedTroops();
	}
	public byte[] buildGameStateData() {
	    ByteArrayOutputStream serializedOutputStream = new ByteArrayOutputStream(); 
	
	    Output output = new Output(serializedOutputStream);
	    GameStateData stateData = new GameStateData();
	    stateData.setUserNameForWarp(GameContext.getCurrentUser().getName());
	    stateData.setType(GameStateData.TYPE_BATTLEFIELD);
	    stateData.setRemoteBattleField(battleField);
	    kryo.writeObject(output, stateData);
	    output.close();
	    return serializedOutputStream.toByteArray();
	}
	public GameStateData rebuildGameStateData(byte[] data) {
	    Input input = new Input(new ByteArrayInputStream(data));
	    GameStateData remoteGameStateData = kryo.readObject(input,GameStateData.class);
	    input.close();
		return remoteGameStateData;
	}	
	public void updateBattleField(BattleField remoteBattleField) {
		// TODO Auto-generated method stub
		battleField.state=remoteBattleField.state;
	}	
	public SYN_TYPE updateBattleFieldRelatedData(String message) {
		String dataType = message.substring(0, message.indexOf(Constants.SYN_DATA_SEPARATOR));
		String data = message.substring(message.indexOf(Constants.SYN_DATA_SEPARATOR)+2, message.length());
		switch(SYN_TYPE.valueOf(dataType)){
		case BattlefieldData:
			break;
		case CommandData:
			troopManager.updateLocalTroopCommandByJSON(data);
			break;
		default:
			throw new RuntimeException("no such syn type!");
		}
		return SYN_TYPE.valueOf(dataType);
	}

	public TroopManager getTroopManager() {
		return troopManager;
	}
	@Autowired
	public void setTroopManager(TroopManager troopManager) {
		this.troopManager = troopManager;
	}
	@Autowired
	public void setKryo(Kryo kryo) {
		this.kryo = kryo;
	}






}
