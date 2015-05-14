package com.sz.zhsan2b.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.sz.zhsan2b.libgdx.Zhsan2b;

public class DesktopLauncher {
	private static boolean rebuildAtlas = false;
	private static boolean drawDebugOutline = false;

	public static void main(String[] arg) {

		if (rebuildAtlas) {
			Settings settings = new Settings();
			settings.maxWidth = 1024*2;
			settings.maxHeight = 1024;
			settings.debug = drawDebugOutline;
			//settings.scale[0]=0.1f;
			TexturePacker.process(settings, "assets-raw/map/kure_3.0", "images",
					"zhsan2b-map");
			TexturePacker.process(settings, "assets-raw/TileFrame", "images",
					"zhsan2b-wangge");		
			TexturePacker.process(settings, "assets-raw/TileEffect", "Effects/TileEffect",
					"zhsan2b-tileeffect");
		}
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Zhsan2b";
		// cfg.useGL30 = true;
		cfg.width = 1000;
		cfg.height = 600;
		new LwjglApplication(new Zhsan2b(), cfg);
	}
}