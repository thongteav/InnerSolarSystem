package sceneObjects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import utils.Color;

/**
 * This class represents a Sun, it also draws the corona of the Sun
 * 
 * @author Thong Teav
 * 14883251
 */
public class Sun {
	private float radius;
	private float orbitPeriod;
	private double angle;
	private boolean axisOn;
	private ArrayList<double[]> staticLines;
	private ArrayList<double[]> animatedLines;

	private GLU glu;
	private GLUquadric quadric;
	private Texture texture;
	private static Random rand = new Random(System.currentTimeMillis());
	private int base;
	
	/**
	 * The constructor to initialize a Sun
	 * 
	 * @param radius the radius of the Sun
	 * @param orbitPeriod the time taken for the Sun to rotate around itself
	 */
	public Sun(float radius, float orbitPeriod) {
		this.radius = radius;
		this.orbitPeriod = orbitPeriod;
		this.angle = 0;
		this.axisOn = false;
		
		this.glu = new GLU();
		this.quadric = glu.gluNewQuadric();
		try {
			texture = TextureIO.newTexture(new File("assets/2k_sun.jpg"), true);
		} 
		catch (IOException e) {
			// file not found
			System.out.println("File not found: " + e.getMessage());
		}
		this.generateStaticLines();
		this.generateAnimatedLines();
	}
	
	/**
	 * Generates some line vertices for the Corona which will be put in the display list
	 */
	public void generateStaticLines() {
		this.staticLines = new ArrayList<>();
		int deltaPhi = 5;
		int deltaTheta = 8;
		for(int phi = -180; phi <= 180; phi += deltaPhi) {
			for(int theta = 0; theta < 360; theta += deltaTheta) {
				double radPhi = Math.toRadians(phi + rand.nextInt(5));
				double radTheta = Math.toRadians(theta + rand.nextInt(5));
				double x = this.radius * (rand.nextDouble() * 0.2 + 1) * Math.sin(radPhi) * Math.cos(radTheta);
				double y = this.radius * (rand.nextDouble() * 0.2 + 1) * Math.sin(radPhi) * Math.sin(radTheta);
				double z = this.radius * (rand.nextDouble() * 0.2 + 1) * Math.cos(radPhi);
				staticLines.add(new double[] {x, y, z});
			}
		}
	}
	
	/**
	 * Generates some line vertices for the Corona which can be animated later
	 */
	public void generateAnimatedLines() {
		this.animatedLines = new ArrayList<>();
		int deltaPhi = 12;
		int deltaTheta = 12;
		for(int phi = -180; phi <= 180; phi += deltaPhi) {
			for(int theta = 0; theta < 360; theta += deltaTheta) {
				double radPhi = Math.toRadians(phi + rand.nextInt(3));
				double radTheta = Math.toRadians(theta + rand.nextInt(3));
				double x = this.radius * (rand.nextDouble() * 0.3 + 1) * Math.sin(radPhi) * Math.cos(radTheta);
				double y = this.radius * (rand.nextDouble() * 0.3 + 1) * Math.sin(radPhi) * Math.sin(radTheta);
				double z = this.radius * (rand.nextDouble() * 0.3 + 1) * Math.cos(radPhi);
				animatedLines.add(new double[] {x, y, z});
			}
		}
	}
	
	/**
	 * Creates the display list with the line vertices 
	 * 
	 * @param gl
	 */
	public void createLineList(GL2 gl) {
		base = gl.glGenLists(staticLines.size());
		
		for (int i = 0; i < staticLines.size(); ++i) {
			gl.glNewList(base + i, GL2.GL_COMPILE);
				gl.glBegin(GL2.GL_LINES);
					gl.glColor4d(Color.SUN[0], Color.SUN[1], Color.SUN[2], 1f);
					gl.glVertex3d(0, 0, 0);
					gl.glColor4d(Color.SUN[0], Color.SUN[1], Color.SUN[2], 0.2f);
					gl.glVertex3d(staticLines.get(i)[0], staticLines.get(i)[1], staticLines.get(i)[2]);
				gl.glEnd();
			gl.glEndList();
		}
	}
	
	public void draw(GL2 gl, double timeEllapsed) {
		gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
		gl.glColor3fv(Color.SUN, 0);
		
		this.angle = (this.angle + 360/this.orbitPeriod*timeEllapsed) % 360;
		gl.glPushMatrix();
			if (this.axisOn) {
				//draw the axis if it's on
				gl.glLineWidth(1f);
				gl.glBegin(GL2.GL_LINES);
					gl.glVertex3f(0f, this.radius * 2, 0f);
					gl.glVertex3f(0f, -this.radius * 2, 0f);
				gl.glEnd();
			}
			gl.glRotated(this.angle, 0, 1, 0);//spin the Sun around the y axis
			
			//check if the texture is available, if it doesn't, simply use paint the sphere
			if (texture == null) {
				glu.gluSphere(this.quadric, this.radius, 50, 50);
			}
			else {		
				glu.gluQuadricTexture(quadric, true);
				this.texture.enable(gl);
				texture.bind(gl);
				glu.gluSphere(quadric, this.radius, 50, 50);
				texture.disable(gl);
			}
			
			gl.glColor4f(0.7f, 0.7f, 0.7f, 0.1f);
			glu.gluSphere(this.quadric, this.radius * 1.1, 50, 50);
			this.drawCorona(gl);
		gl.glPopMatrix();
	}
	
	/**
	 * There are two parts of the Corona, some lines are on the display list for optimization, some lines are animated.
	 * 
	 * @param gl
	 */
	public void drawCorona(GL2 gl) {
		for (int i = 0; i < staticLines.size(); ++i) {
			gl.glCallList(base + i);
		}
		
		//varying the x,y,z and the alpha value, to have some animations
		for (double[] line : animatedLines) {
			gl.glBegin(GL2.GL_LINES);
				gl.glColor4d(Color.SUN[0], Color.SUN[1], Color.SUN[2], 1f);
				gl.glVertex3d(0, 0, 0);
				gl.glColor4d(Color.SUN[0], Color.SUN[1], Color.SUN[2], rand.nextDouble() * 0.3);
				gl.glVertex3d(line[0] + rand.nextDouble(), line[1] + rand.nextDouble(), line[2] + rand.nextDouble());
			gl.glEnd();
		}
	}
	
	//turns the axis on or off
	public void toggleAxis() {
		this.axisOn = !this.axisOn;
	}
}
