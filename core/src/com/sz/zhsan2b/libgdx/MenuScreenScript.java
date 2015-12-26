package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.ImageItem;
import com.uwsoft.editor.renderer.actor.SpriteAnimation;
import com.uwsoft.editor.renderer.script.IScript;

/*
 * iScript for menu logic
 */
public class MenuScreenScript implements IScript {
	
	/*
	 * reference to GameStage
	 */
	private Stage stage;
	private SceneLoader menuLoader;
	
	/*
	 * this is the main root menu actor to work with
	 */
	private CompositeItem menu;
	
	/*
	 * this will be holding 2-ground system composite item 
	 */
	private CompositeItem groundRotator;
	
	/*
	 * this will be the bird sprite animation displayed in center of screen
	 */
	private SpriteAnimation bird;
	
	// this variables are used to wiggle bird up and down with sin function
	private float iterator = 0;
	private float birdInitialPos;
	
	public MenuScreenScript(Stage stage,SceneLoader menuLoader) {
		this.stage = stage;
		this.menuLoader= menuLoader;
	}

	public void init(CompositeItem menuItem) {
		menu = menuItem;
		CompositeItem abcd = menuLoader.getLibraryAsActor("abcd");
		menu.addItem(abcd);
		abcd.setPosition(100, 100);
		// Finding playButton by id and storing in variable
		ImageItem playBtn = menuItem.getImageById("playBtn");
		
		// Finding ground composite and storing in variable 
		groundRotator = menuItem.getCompositeById("groundRotator");
		
		// Finding bird and storing in variable
		bird = menuItem.getSpriteAnimationById("bird");
		
		// let's remember where bird was initially
		birdInitialPos = bird.getY();
		
		// Adding a Click listener to playButton so we can start game when clicked
		playBtn.addListener(new ClickListener() {
			// Need to keep touch down in order for touch up to work normal (libGDX awkwardness)
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				// when finger is up, ask stage to load the game
				return;
			}
		});
	}
	
	/*
	 * This is called every frame
	 */
	public void act(float delta) {

	}

    @Override
    public void dispose() {

    }
	
}
