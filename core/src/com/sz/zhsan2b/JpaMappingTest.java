package com.sz.zhsan2b;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sz.zhsan2b.core.Constants;
import com.sz.zhsan2b.core.GameContext;
import com.sz.zhsan2b.core.entity.BattleField;
import com.sz.zhsan2b.core.entity.BattleProperties;
import com.sz.zhsan2b.core.entity.Command;
import com.sz.zhsan2b.core.entity.MilitaryKind;
import com.sz.zhsan2b.core.entity.Position;
import com.sz.zhsan2b.core.entity.Troop;
import com.sz.zhsan2b.core.entity.BattleField.SYN_TYPE;
import com.sz.zhsan2b.core.entity.DamageRange.DamageRangeType;
import com.sz.zhsan2b.core.entity.User.PLAYER_TYPE;
import com.sz.zhsan2b.core.repository.UserDao;

import ch.qos.logback.classic.Level;

@ContextConfiguration(locations = { "applicationContext.xml" })
public class JpaMappingTest extends SpringTransactionalTestCase {

	private static Logger logger = LoggerFactory.getLogger(JpaMappingTest.class);

	@PersistenceContext
	private EntityManager em;
	private Kryo kryo;
	private UserDao userDao;
	


	public static void setLoggingLevel(ch.qos.logback.classic.Level level) {
	    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(level);
	}
	
	
	public JpaMappingTest() {
		super();
		setLoggingLevel(Level.INFO);
	}


	@Test
	public void allClassMapping() throws Exception {

		Metamodel model = em.getEntityManagerFactory().getMetamodel();

		assertTrue("No entity mapping found", model.getEntities().size() > 0);

		for (EntityType entityType : model.getEntities()) {
			String entityName = entityType.getName();
			em.createQuery("select o from " + entityName + " o").getResultList();
			logger.info("ok: " + entityName);

		}
	}
	@Test
	public void buildBattleFieldDataString() throws UnsupportedEncodingException {
	    ByteArrayOutputStream serializedOutputStream = new ByteArrayOutputStream(); 
	    BattleField battleField = buildTestData();;
	    Output output = new Output(serializedOutputStream);
	    kryo.writeObject(output, battleField);
	    output.close();
	    byte[] lens = serializedOutputStream.toByteArray();
	    
	    
	    
	    logger.info(String.valueOf(lens.length));
	    
	    Input input = new Input(new ByteArrayInputStream(lens));
	    BattleField remoteBattleField = kryo.readObject(input, BattleField.class);
	    input.close();
	    logger.info(remoteBattleField.toString());
	    
	}


	private BattleField buildTestData() { 
		
		BattleProperties bp = new BattleProperties();
		bp.ack = 20;
		bp.def = 10;
		bp.hp = 100;
		bp.isXie = false;
		bp.move = 15;
		bp.range = 1;
		bp.speed = 20;
		bp.damageRangeType=DamageRangeType.ARROW_TOWER;
		bp.damageRange=2;
		bp.backUp();
		BattleProperties bp2= new BattleProperties(bp);
		bp2.range=4;
		bp2.notRange=2;
		bp2.isXie=false;
		BattleProperties bp3= new BattleProperties(bp);
		bp3.isXie=true;
		Command com1 = new Command();
		Command com2 = new Command();
		Command com3 = new Command();		
//		Command com1 = new Command(new Position(3, 4));
//		Command com2 = new Command(new Position(4, 6));
//		Command com3 = new Command(new Position(4, 6));
//		
//		com1.actionKind=ACTION_KIND.MOVE;
//		com2.actionKind=ACTION_KIND.ATTACK;
//		com3.actionKind=ACTION_KIND.MOVE;
//		com1.object=null;
//		com2.object=null;
//		com3.object=null;

		Troop tr1= new Troop(new MilitaryKind(0), bp, new Position(6, 5),
				com1, PLAYER_TYPE.PLAYER.AI);
		
		Troop tr2= new Troop(new MilitaryKind(1), bp2, new Position(1, 9),
				com2, PLAYER_TYPE.PLAYER.PLAYER);
		Troop tr3= new Troop(new MilitaryKind(0), bp3, new Position(6, 4),
				com3, PLAYER_TYPE.PLAYER.AI);		
		tr2.setStepAttack(true);
		tr2.setMultiObject(true);
		BattleField battleField = new BattleField();

		battleField.getTroopList().add(tr1);
		battleField.getTroopList().add(tr2);
		battleField.getTroopList().add(tr3);
		return battleField;
		
	}
	@Test
	public void testDao() throws Exception{
		logger.info("userCount:"+userDao.count());
		logger.info("loginName:"+userDao.findAll().iterator().next().getLoginName());
		assertTrue(userDao.count()>0);
		
	}
	

	@Autowired
	public void setKryo(Kryo kryo) {
		this.kryo = kryo;
	}
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
}
