package sceneObjects;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;

/**
 * Represents spherical object in space like Planets or Moon
 * 
 * @author Thong Teav
 * 14883251
 */
public abstract class AstronomicalObject {
	protected float radius;
	protected float orbitDist;
	protected float orbitalPeriod;
	protected float[] color;
	protected float angle;
	protected double tiltingAngle;
	protected GLU glu;
	protected GLUquadric quadric;
	protected Texture texture;
	
	/**
	 * Constructor to initialize an astronomical object
	 * 
	 * @param radius The radius of the object
	 * @param orbitDist The distance away from the Sun
	 * @param orbitalPeriod The time taken to make one orbit around the Sun
	 * @param color The color to paint the object
	 * @param tiltingAngle The angle to tilt the axis of the object to
	 */
	public AstronomicalObject(float radius, float orbitDist, float orbitalPeriod, float[] color, double tiltingAngle) {
		this.radius = radius;
		this.orbitDist = orbitDist;
		this.orbitalPeriod = orbitalPeriod;
		this.glu = new GLU();
		this.quadric = glu.gluNewQuadric();
		this.color = color;
		this.angle = 0;
		this.tiltingAngle = tiltingAngle;
	}
	
	/**
	 * Sets the color material to reflect light and the surface color
	 * 
	 * @param gl
	 * @param timeEllapsed
	 */
	public void draw(GL2 gl, double timeEllapsed) {
		gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
		gl.glColor3fv(this.color, 0);
		gl.glLineWidth(1f);
	}

	public float getRadius() {
		return radius;
	}

	public float getOrbitDist() {
		return orbitDist;
	}

	public float getOrbitalPeriod() {
		return orbitalPeriod;
	}

	public float[] getColor() {
		return color;
	}

	public GLUquadric getQuadric() {
		return quadric;
	}
	
	public void addTexture(Texture texture) {
		this.texture = texture;
	}
}
