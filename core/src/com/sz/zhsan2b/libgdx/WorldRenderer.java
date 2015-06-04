package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class WorldRenderer {

	private WorldController worldController;
	private SpriteBatch batch;
	private Camera camera;
	private OrthographicCamera cameraGUI;

	public WorldRenderer(WorldController worldController) {
		this.worldController = worldController;
		init();
	}

	private void init() {
		batch = new SpriteBatch();
		camera = worldController.stage.getCamera();
		//camera.position.set(0, 0, 0);
		// camera的改变需要update。
		//camera.update();
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update();
	}

	public void render() {
		// renderTestObjects();
		renderWorld(batch);
		renderGui(batch);
	}


	private void renderWorld(SpriteBatch batch) {
		worldController.cameraHelper.applyTo((OrthographicCamera) camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.level.render(batch);	
		batch.end();
	}

	public void resize(int width, int height) {
		// 调整像素比例，保证不失真，全靠调整分辨率。此处是相对于world的尺寸
		//camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
	    worldController.stage.getViewport().update(width, height);
		camera.update();
		// 这个camera相对于窗口是不动的，不动的carema可以类同于世界坐标系。
		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / (float) height)
				* (float) width;
		cameraGUI.position.set(cameraGUI.viewportWidth / 2,
				cameraGUI.viewportHeight / 2, 0);
		cameraGUI.update();
	}

	public void dispose() {
		batch.dispose();
	}


	private void renderGuiFpsCounter(SpriteBatch batch) {
		float x = cameraGUI.viewportWidth - 55;
		float y = cameraGUI.viewportHeight - 15;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
		if (fps >= 45) {
			// 45 or more FPS show up in green
			fpsFont.setColor(0, 1, 0, 1);
		} else if (fps >= 30) {
			// 30 or more FPS show up in yellow
			fpsFont.setColor(1, 1, 0, 1);
		} else {
			// less than 30 FPS show up in red
			fpsFont.setColor(1, 0, 0, 1);
		}
		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(1, 1, 1, 1); // white
	}

	private void renderGui(SpriteBatch batch) {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		// draw FPS text (anchored to bottom right edge)
		if (GamePreferences.instance.showFpsCounter)
		renderGuiFpsCounter(batch);	
		batch.end();
	}

	public Camera getCamera() {
		return camera;
	}


}
