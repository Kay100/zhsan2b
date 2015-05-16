package com.sz.zhsan2b.desktop;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sz.zhsan2b.core.BattleUtils;
import com.sz.zhsan2b.core.Position;

public class Test {
	private static Logger logger = LoggerFactory.getLogger(Test.class);

	public static void copyFileUsingApacheCommonsIO(File source, File dest)
	        throws IOException {
	    FileUtils.copyFile(source, dest);
	}
	public static void setLoggingLevel(ch.qos.logback.classic.Level level) {
	    ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(level);
	}
	
	public static void main(String[] args) {
		setLoggingLevel(ch.qos.logback.classic.Level.DEBUG);

         //copyFiles();
		Position object = new Position(2, 2);
		Position origin = new Position(1, 1);
		int range =3;
		boolean isXie = false;
		boolean in = BattleUtils.isObjectInAttackRange(object, origin, range, isXie);
		System.out.println(in);
	}

	private static void copyFiles() {
		for(int y=10;y<20;y++){
        	 for (int x=10;x<20;x++){
 
        		 File a = new File("E:\\zhsan2b\\core\\assets\\assets-raw\\map\\kure_2.0\\"+String.valueOf(y*30+x)+".jpg");
        		 File b = new File("E:\\zhsan2b\\core\\assets\\assets-raw\\map\\kure_3.0\\"+String.valueOf((9-(y-10))*10+x-10)+".jpg");

        		 try {
					copyFileUsingApacheCommonsIO(a, b);
				} catch (IOException e) {
					e.printStackTrace();
				}

        	 
         }}
	}

}

