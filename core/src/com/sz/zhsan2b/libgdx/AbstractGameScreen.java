package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

public abstract class AbstractGameScreen implements Screen {
	protected DirectedGame game;

	public AbstractGameScreen(DirectedGame game) {
		this.game = game;
	}

	public abstract void render(float deltaTime);

	public abstract void resize(int width, int height);

	public abstract void show();

	public abstract void hide();

	public abstract void pause();
	
	public abstract InputProcessor getInputProcessor ();

	public void resume() {
		//Assets.instance.init(new AssetManager());
	}

	public void dispose() {
		Assets.instance.dispose();
	}
}
