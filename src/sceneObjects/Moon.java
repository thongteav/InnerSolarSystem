package sceneObjects;

import com.jogamp.opengl.GL2;

/**
 * The moon orbits around the parent parent at the tilting angle specified from the horizontal axis (z axis)
 * 
 * @author Thong Teav
 * 14883251
 * 
 */
public class Moon extends AstronomicalObject{

	/**
	 * The constructor to instantiate a moon
	 * 
	 * @param radius The radius of  the moon
	 * @param orbitDist The distance away from the center of the parent planet
	 * @param orbitalPeriod The time taken to orbit around the parent planet
	 * @param color The color of the moon surface
	 * @param tiltingAngle The angle above the horizontal axis for its orbit
	 */
	public Moon(float radius, float orbitDist, float orbitalPeriod, float[] color, double tiltingAngle) {
		super(radius, orbitDist, orbitalPeriod, color, tiltingAngle);
	}

	@Override
	public void draw(GL2 gl, double timeEllapsed) {
		super.draw(gl, timeEllapsed);
		
		this.angle += (360/this.orbitalPeriod*timeEllapsed) % 360;
		gl.glPushMatrix();
			//tilt the orbit
			gl.glRotated(tiltingAngle, Math.cos(Math.toRadians(tiltingAngle)), Math.sin(Math.toRadians(this.tiltingAngle)), 0);
			gl.glTranslatef(this.getOrbitDist(), 0f, 0f);
			
			//rotate the moon around the parent planet
			gl.glTranslatef(-this.getOrbitDist(), 0f, 0f);
			gl.glRotatef(angle, 0, 1, 0);
			gl.glTranslatef(this.getOrbitDist(), 0f, 0f);
			
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
		gl.glPopMatrix();
	}

}
