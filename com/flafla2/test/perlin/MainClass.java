package com.flafla2.test.perlin;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import java.nio.FloatBuffer;
import java.util.Random;

public class MainClass {
	boolean useWireframe = false;
	double lastFPS = 0;
	double fps = 0;
	int seed = 0;
	float resolution = 1; // 10 / resolution = size of grid tile
	float maxHeight = 100;
	int size = 64; // size of noise
	int maxX = 3;
	int maxY = 3;
	float[] cameraPos = {-300,500,300};
	float[][][][]noise = new float[0][0][0][0];
	
	public static void main(String[] args)
	{
		MainClass mainClass = new MainClass();
		Random r = new Random();
		mainClass.seed = r.nextInt();
		mainClass.launchScreen();
	}
	
	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public void updateFPS() {
	    if (getTime() - lastFPS > 1000) {
	        Display.setTitle("FPS: " + fps + "; Seed: " + seed + "; Res:" + resolution + "; size:" + size);
	        fps = 0; //reset the FPS counter
	        lastFPS += 1000; //add one second
	    }
	    fps++;
	}
	
	public void generateGrid()
	{
		noise = new float[maxX][maxY][0][0];
		for(int x=0;x<noise.length;x++)
			for(int y=0;y<noise[x].length;y++)
			{
				PerlinGen pg = new PerlinGen(x, y);
				noise[x][y] = pg.GeneratePerlinNoise(pg.GenerateWhiteNoise(size, size, seed), 5);
			}
					
		
		//System.out.println("Noise length is: (" + noise.length + "," + noise[0].length + ")");
	}
	
	public void launchScreen()
	{
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		lastFPS = getTime();
		generateGrid();
		
		// init OpenGL here
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective( 45.0f, (float)600/(float)800, 1.0f, 5000.0f );
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glShadeModel (GL11.GL_SMOOTH);
		
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);// enables opengl to use glColor3f to define material color
		GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE);
		
		float x = (noise[0][0].length*10/resolution)*maxX;
		float y = maxHeight;
		float z = (noise[0][0][0].length*-10/resolution)*maxY;
		float r = 0;

		while (!Display.isCloseRequested()) {
			
			updateFPS();
			
			// render OpenGL here
			//GL11.glClearColor(1,1,1,1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	    	GL11.glLoadIdentity();
	    	
	    	//Draw GUI
	    	GLU.gluLookAt(-1200, 2000,1200, 
					x/2, y/2, z/2, 
					0, 1, 0);
	    	
	    	pollInput();
	    	
			drawRose();
			
			
			//initLighting(x/2,50,0);
			//GL11.glEnable(GL11.GL_LIGHTING);
			//GL11.glEnable (GL11.GL_LIGHT0);
			GL11.glPushMatrix();
				GL11.glTranslatef(x/2, 0, z/2);
				GL11.glRotatef(r, 0, 1, 0);
				GL11.glTranslatef(-x/2, 0, -z/2);
		    	r+=0.2;
		    	if(r>=360)
		    		r=0;
			    drawNoiseGrid();
		    GL11.glPopMatrix();
		    //GL11.glDisable (GL11.GL_LIGHT0);
		    //GL11.glDisable(GL11.GL_LIGHTING);
			
			Display.update();
			Display.sync(60);
		}
		
		Display.destroy();
	}
	
	public void pollInput() {
		
        if (Mouse.isButtonDown(0)) {
		    int x = Mouse.getX();
		    int y = Mouse.getY();
				
		    System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
        }
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
		    System.out.println("SPACE KEY IS DOWN");
		}
			
		while (Keyboard.next()) { //Has a key event occured?
		    if (Keyboard.getEventKeyState()) { //Has a key been pressed?
		        if (Keyboard.getEventKey() == Keyboard.KEY_R) {
		        	System.out.println("R Key Pressed");
		        	useWireframe = !useWireframe;
		        	if(useWireframe)
		        		GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE ); //Wireframe
		        	else
		        		GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_FILL ); //Normal
				} else if(Keyboard.getEventKey() == Keyboard.KEY_G)
				{
					seed = new Random().nextInt();
					generateGrid();
				} else if(Keyboard.getEventKey() == Keyboard.KEY_P)
				{
					resolution*=2;
					size *= 2;
					generateGrid();
				} else if(Keyboard.getEventKey() == Keyboard.KEY_O)
				{
					if(resolution/2 >= 1)
					{
						resolution/=2;
						size /= 2;
						generateGrid();
					}
				}
		    } else { //Has a key been released?
		        if (Keyboard.getEventKey() == Keyboard.KEY_R) {
		        	System.out.println("R Key Released");
		        }
		    }
		}
    }
	
	public void drawRose()
	{
		// Draw the positive side of the lines x,y,z
	    GL11.glBegin(GL11.GL_LINES);
	    GL11.glColor3f(0.0f, 1.0f, 0.0f); // Green for x axis
	    GL11.glVertex3f(0,0,0);
	    GL11.glVertex3f(10,0,0);
	    GL11.glColor3f(1.0f,0.0f,0.0f); // Red for y axis
	    GL11.glVertex3f(0,0,0);
	    GL11.glVertex3f(0,10,0);
	    GL11.glColor3f(0.0f,0.0f,1.0f); // Blue for z axis
	    GL11.glVertex3f(0,0,0); 
	    GL11.glVertex3f(0,0,10);
	    GL11.glEnd();
	}
	
	public void initLighting(float x, float y, float z)
	{
		//START: POSITION//
		FloatBuffer position = BufferUtils.createFloatBuffer(4);;
		position.put(x).put(y).put(z).put(1.0f).flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, position);
		//START: AMBIENT//
		FloatBuffer ambient = BufferUtils.createFloatBuffer(4);;
		ambient.put(0.5f).put(0.5f).put(0.5f).put(0.5f).flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, ambient);
		//START: DIFFUSE//
		FloatBuffer diffuse = BufferUtils.createFloatBuffer(4);;
		diffuse.put(0.7f).put(0.7f).put(0.7f).put(1.0f).flip();
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, diffuse);
		//START: SPECULAR//
		FloatBuffer specular = BufferUtils.createFloatBuffer(4);;
		specular.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
		GL11.glMaterial(GL11.GL_LIGHT0, GL11.GL_SPECULAR, specular);
		GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, 50.0f);
	}
	
	public void drawNoiseGrid()
	{
		GL11.glColor3f(1f,1f,1f);
		GL11.glBegin(GL11.GL_QUADS);
		for(int x=0;x<noise.length;x++)
		{
		    for(int y=0;y<noise[x].length;y++)
		    {
		        drawNoiseGridWithOffset(x,y);
		    }
		}
		GL11.glEnd();
	}
	
	public void drawNoiseGridWithOffset(int xOff, int yOff)
	{
		GL11.glColor3f(1f,1f,1f);
		GL11.glBegin(GL11.GL_QUADS);
		float i = (size-1)*xOff;
    	float j = (size-1)*yOff;
		for(int x=0;x<noise[xOff][yOff].length-1;x++)
		{
		    for(int y=0;y<noise[xOff][yOff][x].length-1;y++)
		    {
		        addBoxVertex(x+i,noise[xOff][yOff][x][y+1],(y+1)+j);//bottomleft
		        addBoxVertex((x+1)+i,noise[xOff][yOff][x+1][y+1],(y+1)+j);//bottomright
		        addBoxVertex((x+1)+i,noise[xOff][yOff][x+1][y],y+j);//topright
		        addBoxVertex(x+i,noise[xOff][yOff][x][y],y+j);//topleft
		    }
		}
		GL11.glEnd();
	}
	
	public void addBoxVertex(float x, float noise, float z)
	{
		GL11.glColor3f(lerp(0,1,noise), lerp(1,0.54f,noise), 0);
		GL11.glVertex3f(x*10/resolution,noise*maxHeight,z*-10/resolution);
	}
	
	public float lerp(float y1,float y2,float mu)
	{
	   return(y1*(1-mu)+y2*mu);
	}
	
	/**
	 * Get the time in milliseconds
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
}
