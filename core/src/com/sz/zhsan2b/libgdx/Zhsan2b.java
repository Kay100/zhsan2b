package com.sz.zhsan2b.libgdx;
import java.util.Arrays;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import org.springframework.context.ApplicationContext;

import org.springframework.context.annotation.ComponentScan;

import org.springframework.context.annotation.ImportResource;

import ch.qos.logback.classic.Logger;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.esotericsoftware.kryo.Kryo;
import com.sz.zhsan2b.Profiles;
import com.sz.zhsan2b.appwarp.WarpController;
import com.sz.zhsan2b.core.GameContext;
import com.sz.zhsan2b.core.entity.Command;
import com.sz.zhsan2b.core.entity.StepAction;
import com.sz.zhsan2b.core.entity.Troop;

@ComponentScan(basePackages="com.sz.zhsan2b")
@ImportResource("classpath:/com/sz/zhsan2b/applicationContext.xml") 
public class Zhsan2b extends DirectedGame {
	
	public  static  BattleScreen battleScreen;
	//set 
	public final ch.qos.logback.classic.Level logLevel = ch.qos.logback.classic.Level.DEBUG;
	@Override
	public void create() {
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Load assets
		Assets.instance.init(new AssetManager());
		Profiles.setProfileAsSystemProperty(Profiles.UNIT_TEST);
		//System.setProperty("spring.config.location", "classpath:/com/sz/zhsan2b/application.properties");
		ApplicationContext ctx = SpringApplication.run(Zhsan2b.class);
		((Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(logLevel);
		GameContext.setContext(ctx);
//		if(ctx.getEnvironment().containsProperty("jdbc.url")){
//			System.out.println("have environment!!!");
//		}
        System.out.println("Let's inspect the beans provided by Spring Boot:");
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }    
		// Load preferences for audio settings and start playing music
		//GamePreferences.instance.load();	
		//other init
		Gdx.input.setCursorImage(Assets.instance.assetArrow.normal, 0, 0);
		
		// Start game at menu screen
		battleScreen=new BattleScreen(this);
		setScreen(battleScreen);
		WarpController.getInstance().setListener(battleScreen);		
		//kyro
		initKyroRegister();
		
	}
	private void initKyroRegister() {
		Kryo kryo = GameContext.getContext().getBean(Kryo.class);
		kryo.register(Command.class);
		kryo.register(Troop.class);
		kryo.register(StepAction.class);
		
	}
}