package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

public class Zhsan2b extends DirectedGame {
	@Override
	public void create() {
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Load assets
		Assets.instance.init(new AssetManager());
		// Load preferences for audio settings and start playing music
		//GamePreferences.instance.load();	
		// Start game at menu screen
		setScreen(new BattleScreen(this));
	}
}