package com.sz.zhsan2b.desktop;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sz.zhsan2b.core.BattleUtils;
import com.sz.zhsan2b.core.entity.Command.ACTION_KIND;
import com.sz.zhsan2b.core.entity.Position;

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
		 //testObjectInAttackRange();
		//disposeImage();
		testEnum();
		
	}
	private static void disposeImage() {
		File srcDir = new File("C:\\Users\\Administrator\\git\\zhsan2b\\core\\assets\\assets-raw\\MouseArrow");
		File desDir = new File("C:\\Users\\Administrator\\git\\zhsan2b\\core\\assets\\assets-raw\\MouseArrowAlpha");
		File[] files = srcDir.listFiles();
		if(!desDir.exists()){
			desDir.mkdir();
		}
		for(int i=0;i<files.length;i++){
			File curFile = files[i];
			transferAlpha(curFile, new File(desDir,curFile.getName()));
		}
	}
	private static void testObjectInAttackRange() {
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
	
	public static byte[] transferAlpha(File srcFile,File desFile) {
		Image  image = Toolkit.getDefaultToolkit().getImage(srcFile.getAbsolutePath());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIcon imageIcon = new ImageIcon(image);
            BufferedImage bufferedImage = new BufferedImage(imageIcon
                    .getIconWidth(), imageIcon.getIconHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
            g2D.drawImage(imageIcon.getImage(), 0, 0, imageIcon
                    .getImageObserver());
            int alpha = 0;
            for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage
                    .getHeight(); j1++) {
                for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage
                        .getWidth(); j2++) {
                    int rgb = bufferedImage.getRGB(j2, j1);
 
                    int R =(rgb & 0xff0000 ) >> 16 ;
                int G= (rgb & 0xff00 ) >> 8 ;
                int B= (rgb & 0xff );
                if(((255-R)<30) && ((255-G)<30) && ((255-B)<30)){ //去除白色背景
                //if(((255-R)>160) && ((255-G)>160) && ((255-B)>160)){//去除黑色背景
                    rgb = ((alpha + 1) << 24) | (rgb & 0x00ffffff);
                }
                bufferedImage.setRGB(j2, j1, rgb);
                }
            }
            g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
            ImageIO.write(bufferedImage, "png", desFile);
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        } catch (Exception e) {
        }finally{
        }
 
        return byteArrayOutputStream.toByteArray();
    }
 
    
 
    //byte[] ------>BufferedImage
    public static BufferedImage ByteToBufferedImage(byte[] byteImage) throws IOException{
        ByteArrayInputStream in = new ByteArrayInputStream(byteImage);
        BufferedImage buffImage = ImageIO.read(in);    
        return buffImage;
    }
 
    //Image转换为BufferedImage；
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        image = new ImageIcon(image).getImage();
        boolean hasAlpha = false;
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null),
                    image.getHeight(null), transparency);
        } catch (HeadlessException e) {
        }
        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }	

    public static void testEnum(){
    	ACTION_KIND temp = ACTION_KIND.valueOf("NONE");
    	System.out.println(temp);		
    }
}

