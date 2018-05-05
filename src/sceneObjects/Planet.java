package sceneObjects;

import java.util.ArrayList;

import com.jogamp.opengl.GL2;

/**
 * The planet rotates around its axis at a tilting angle and around the Sun
 * 
 * @author Thong Teav
 * 14883251
 * 
 */
public class Planet extends AstronomicalObject {
	private ArrayList<Moon> moons;
	private boolean pathOn, orbitOn, axisOn;
	private float selfRotateAngle;
	private float selfOrbitPeriod;
	private float selfRotateDirection;

	/**
	 * 
	 * @param radius
	 * @param orbitDist
	 * @param orbitalPeriod
	 * @param color
	 * @param selfOrbitPeriod
	 * @param tiltingAngle
	 */
	public Planet(float radius, float orbitDist, float orbitalPeriod, float[] color, float selfOrbitPeriod, double tiltingAngle) {
		super(radius, orbitDist, orbitalPeriod, color, tiltingAngle);
		this.moons = new ArrayList<>();
		this.pathOn = true;
		this.orbitOn = true;
		this.axisOn = false;
		this.selfRotateAngle = 0;
		this.selfOrbitPeriod = selfOrbitPeriod;
		this.selfRotateDirection = 1; //planet should rotate clockwise by default
	}
	
	public ArrayList<Moon> getMoons(){
		return this.moons;
	}
	
	public boolean addMoon(Moon moon) {
		return this.moons.add(moon);
	}

	@Override
	public void draw(GL2 gl, double timeEllapsed) {
		super.draw(gl, timeEllapsed);
		
		if(orbitOn) {//update the angle to rotate the planet around the Sun
			this.angle = (float) ((this.angle + 360*timeEllapsed/this.orbitalPeriod) % 360);			
		}
		
		//update the angle to rotate around its axis
		this.selfRotateAngle = (float) (this.selfRotateAngle + (360 * this.selfRotateDirection*timeEllapsed /this.selfOrbitPeriod) % 360);
		gl.glPushMatrix();
			gl.glTranslatef(this.getOrbitDist(), 0, 0);//move the planet away from the Sun
			gl.glPushMatrix();
				//rotate the planet around the sun
				gl.glTranslatef(-this.getOrbitDist(), 0f, 0f);
				gl.glRotatef(angle, 0, 1, 0);
				gl.glTranslatef(this.getOrbitDist(), 0f, 0f);
				
				gl.glPushMatrix();//moon shouldn't inherit this rotation	
					//tilt the axis of planet at the specified angle
					gl.glRotated(this.tiltingAngle, -Math.sin(Math.toRadians(this.tiltingAngle)) * this.selfRotateDirection, Math.cos(Math.toRadians(this.tiltingAngle)) * this.selfRotateDirection, 0);
					gl.glRotated(this.selfRotateAngle, 0, 1, 0);//rotate the planet around y axis, so it will apply the tilting angle
	
					//draw the axis with the planet color called in the superclass draw method
					if (this.axisOn) {
						gl.glBegin(GL2.GL_LINES);
							gl.glVertex3f(0f, this.radius * 2, 0f);
							gl.glVertex3f(0f, -this.radius * 2, 0f);
						gl.glEnd();
					}
					
					//apply the texture if it's loaded
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
				
				//draw the moons at the distance away from the planet
				for (Moon m: this.getMoons()) {
					m.draw(gl, timeEllapsed);
				}
			gl.glPopMatrix();
			
			//draw the orbit path
			if(pathOn) {
				this.drawOrbitPath(gl);
			}
		gl.glPopMatrix();
	}
	
	public void drawOrbitPath(GL2 gl) {
		gl.glPushMatrix();
			gl.glColor4f(1, 1, 1, 0.5f);
			gl.glLineWidth(1f);
			gl.glTranslatef(-this.getOrbitDist(), 0f, 0f);
			gl.glBegin(GL2.GL_LINE_LOOP);
			//360 divided by number of vertices wanted
			//in this case, 360 vertices, because this will generate a smoother circle
			int degree = 360 / 360; 
			//this leaves a gap from the last point to the first point so that the loop can be connected
			for(int deg = 0; deg < 360; deg += degree) { 
				double rad = Math.toRadians(deg);
				double x = this.orbitDist * Math.cos(rad);
				double z = this.orbitDist * Math.sin(rad);
				double[] vertex = {x, 0, z};
				gl.glVertex3dv(vertex, 0);
			}
			gl.glEnd();
		gl.glPopMatrix();
	}
	
	public void toggleDrawPath() {
		this.pathOn = !this.pathOn;
	}
	
	public void toggleOrbit() {
		this.orbitOn = !this.orbitOn;
	}
	
	public void toggleAxis() {
		this.axisOn = !this.axisOn;
	}
	
	public void setSelfRotateDirection(float direction) {
		this.selfRotateDirection = direction;
	}
}
