
package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class MapView extends AbstractGameObject {

	private TextureRegion[][] map = Assets.instance.assetMap.map;
	private TextureRegion wangge = Assets.instance.assetWangge.wangge;


	public MapView() {
		init();
	}

	private void init() {

	}



	@Override
	public void update(float deltaTime) {

	}	

	@Override
	public void render(SpriteBatch batch) {
		renderMap(batch);
		renderWangge(batch);
	}

	private void renderWangge(SpriteBatch batch) {
		float relX = 0;
		float relY = 0;
		batch.setColor(1, 1, 1, 0.5f);
		for (int y=0;y<Constants.WANGGE_YCOUNT;y++){
			for(int x=0;x<Constants.WANGGE_XCOUNT;x++){
				batch.draw(wangge.getTexture(), 0+relX, 0+relY, Constants.WANGGE_UNIT_WIDTH/2,
						Constants.WORLD_HEIGHT/2, Constants.WANGGE_UNIT_WIDTH, Constants.WANGGE_UNIT_HEIGHT, 1, 1, 0,
						wangge.getRegionX(), wangge.getRegionY(),wangge.getRegionWidth(),
						wangge.getRegionHeight(), false,
						false);	
				relX+=Constants.WANGGE_UNIT_WIDTH;
			}
			relX=0;
			relY+=Constants.WANGGE_UNIT_HEIGHT;
		}     
		batch.setColor(1, 1, 1, 1);
	}

	private void renderMap(SpriteBatch batch) {
		float relX = 0;
		float relY = 0;
		for (int y=0;y<Constants.MAP_TEXTURE_YCOUNT;y++){
			for(int x=0;x<Constants.MAP_TEXTURE_XCOUNT;x++){
				batch.draw(map[x][y].getTexture(), 0+relX, 0+relY, Constants.MAP_SINGLE_ATLAS_WIDTH/2,
						Constants.MAP_SINGLE_ATLAS_HEIGHT/2, Constants.MAP_SINGLE_ATLAS_WIDTH, Constants.MAP_SINGLE_ATLAS_HEIGHT, 1, 1, 0,
						map[x][y].getRegionX(), map[x][y].getRegionY(), map[x][y].getRegionWidth(),
						map[x][y].getRegionHeight(), false,
						false);	
				relX+=Constants.MAP_SINGLE_ATLAS_WIDTH;
			}
			relX=0;
			relY+=Constants.MAP_SINGLE_ATLAS_HEIGHT;
		}
	}
}
