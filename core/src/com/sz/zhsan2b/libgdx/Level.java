package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class Level {
	public static final String TAG = Level.class.getName();


	// objects
	public MapView mapView;


	public Level() {
		init();
	}

	private void init() {
           mapView = new MapView();
	}
	public void update (float deltaTime) {

		}	

	public void render(SpriteBatch batch) {

		mapView.render(batch);

		
	
	}
}
