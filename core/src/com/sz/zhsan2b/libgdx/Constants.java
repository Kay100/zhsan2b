package com.sz.zhsan2b.libgdx;

public class Constants {
// Visible game world is 5 meters wide  
public static final float VIEWPORT_WIDTH = 1000.0f;  //临时的
// Visible game world is 5 meters tall
public static final float VIEWPORT_HEIGHT = 600.0f;


public static final float WORLD_WIDTH = 1000f;
public static final float WORLD_HEIGHT = 1000f;

public static final float UNITSPERPIXEL = 1F;


//GUI Width
public static final float VIEWPORT_GUI_WIDTH = 800.0f;
//GUI Height
public static final float VIEWPORT_GUI_HEIGHT = 480.0f;
public static final String TEXTURE_ATLAS_OBJECTS =
"images/canyonbunny.atlas";
public static final String LEVEL_01 = "levels/level-01.png";
//Location of image file for level 01
//Amount of extra lives at level start
public static final int LIVES_START = 3;
//Duration of feather power-up in seconds
public static final float ITEM_FEATHER_POWERUP_DURATION = 9;
//Delay after game over
public static final float TIME_DELAY_GAME_OVER = 3;

public static final String TEXTURE_ATLAS_UI =
"images/canyonbunny-ui.atlas";
public static final String TEXTURE_ATLAS_MAP = "images/zhsan2b-map.atlas";

public static final String TEXTURE_ATLUS_WANGGE = "images/zhsan2b-wangge.atlas";

public static final String TEXTURE_ATLAS_LIBGDX_UI =
"images/uiskin.atlas";
// Location of description file for skins
public static final String SKIN_LIBGDX_UI =
"images/uiskin.json";
public static final String SKIN_CANYONBUNNY_UI =
"images/canyonbunny-ui.json";
public static final String PREFERENCES = "sz.test.settings";
//map
public static final int MAP_TEXTURE_XCOUNT = 1;
public static final int MAP_TEXTURE_YCOUNT = 1;

public static final int MAP_SINGLE_ATLAS_WIDTH = 1000;
public static final int MAP_SINGLE_ATLAS_HEIGHT = 1000;

//wangge
public static final int WANGGE_XCOUNT = 10;
public static final int WANGGE_YCOUNT = 10;
public static final int WANGGE_UNIT_WIDTH = 100;
public static final int WANGGE_UNIT_HEIGHT = 100;

//troop
public static final int TROOP_MODEL_UNIT_WIDTH = 128;
public static final int TROOP_MODEL_UNIT_HEIGHT = 128;
public static final int TROOP_MODEL_XCOUNT = 10;
public static final int TROOP_MODEL_YCOUNT = 8;


//step
public static final float ONE_STEP_TIME = 1f;


}
