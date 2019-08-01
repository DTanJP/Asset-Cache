package com.github.dtanjp.cachedemo.standard;

import java.awt.Graphics;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * MainApp.java
 * Description: Demonstrates loading files using the standard cache class
 * 
 * @author David Tan
 **/
public class MainApp extends JFrame {

	/** Serial version UID **/
	private static final long serialVersionUID = -5261780547150351186L;

	/** Constructor **/
	private MainApp() {
		///Setting up the jframe
		super("Cache demo");
		setBounds(0, 0, 500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		
		//Set up the cache
		cache = new Cache("Demo.cache");
		if(cache.exist()) {
			cache.load();
			if(cache.get("dog.png") != null) {//File names are case sensitive
				try {
						//Parse the image
						img = ImageIO.read(new ByteArrayInputStream(cache.get("dog.png")));
						
						//Scale it to fit the screen
						img = img.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
						
						//Call repaint to refresh the screen so we can see the dog
						repaint();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Error: Cannot parse dog.png");
				}
			} else
				throw new Error("Error: Is this the right cache file? There is no dog.png inside Demo.cache");
		} else
			throw new Error("Error: Demo.cache not found");
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(img != null)
			g.drawImage(img, 0, 0, null);
	}
	
	/** Main method **/
	public static void main(String[] args) {
		new MainApp();
	}
	
	/** Variables **/
	private Cache cache;
	private Image img;
}
