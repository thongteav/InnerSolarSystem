import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.TextureIO;

import sceneObjects.Moon;
import sceneObjects.Planet;
import sceneObjects.Sun;
import utils.Color;

/**
 * Represents the Inner Solar System with a Sun and its first four planets with their moons
 * 
 * @author Thong Teav
 * 14883251
 *
 */
public class SolarSystem {
	private Sun sun;
	private ArrayList<Planet> planets;
	
	public SolarSystem() {
		sun = new Sun(10f, 25f);
		
		//scale factors for radius and orbit distance
		float radiusScale = 1f;
		float orbitDistScale = 100f;
		
		//radii are linearly scaled based on Mercury, orbit distances are scaled based on the Earth
		Planet mercury = new Planet(1f * radiusScale, 0.39f * orbitDistScale, 88f, Color.MERCURY, 58.7f, 0.01);
		Planet venus = new Planet(2.449f * radiusScale, 0.72f * orbitDistScale, 224.7f, Color.VENUS, 243f, 32.7);
		Planet earth = new Planet(1.083f * radiusScale, 1f * orbitDistScale, 365.2f, Color.EARTH, 1f, 23.439281);
		Planet mars = new Planet(0.523f * radiusScale, 1.52f * orbitDistScale, 687f, Color.MARS, 1.0417f, 25.19);		
		venus.setSelfRotateDirection(-1f); //Venus rotates in anticlockwise

		//create the moons at the distance way from the center of the parent planet and add them to their parent planets
		//Moon(radius, orbitDist, orbitalPeriod, color, selfOrbitPeriod, tiltingAngle)
		Moon moon = new Moon(0.289f * radiusScale, 0.013f * orbitDistScale + 1.083f * radiusScale, 27.3f, Color.MOON, 5);
		Moon phobos = new Moon(0.0017f * radiusScale * 40f, 0.0000313f * orbitDistScale + 0.7f * radiusScale, 0.32f, new float[] {0.3f, 0.3f, 0.3f}, 0);
		Moon deimos = new Moon(0.002f * radiusScale * 40f, 0.007f * orbitDistScale + 0.7f * radiusScale, 1.3f, new float[] {0.5f, 0.5f, 0.5f}, 0);
		earth.addMoon(moon);
		mars.addMoon(phobos);
		mars.addMoon(deimos);
		
		try {
			mercury.addTexture(TextureIO.newTexture(new File("assets/2k_mercury.jpg"), true));
			venus.addTexture(TextureIO.newTexture(new File("assets/2k_venus_atmosphere.jpg"), true));
			mars.addTexture(TextureIO.newTexture(new File("assets/2k_mars.jpg"), true));
			earth.addTexture(TextureIO.newTexture(new File("assets/2k_earth.jpg"), true));
			moon.addTexture(TextureIO.newTexture(new File("assets/2k_moon.jpg"), true));
			phobos.addTexture(TextureIO.newTexture(new File("assets/2k_moon.jpg"), true));
			deimos.addTexture(TextureIO.newTexture(new File("assets/2k_moon.jpg"), true));
		} 
		catch (IOException e) {
			// file not found
			System.out.println("File not found: " + e.getMessage());
		}
		
		//add the planets to the arraylist
		planets = new ArrayList<>();
		planets.add(mercury);
		planets.add(venus);
		planets.add(earth);
		planets.add(mars);
	}
	
	/**
	 * Draws the inner solar system
	 * @param gl
	 * @param timeEllapsed
	 */
	public void draw(GL2 gl, double timeEllapsed) {
		sun.draw(gl, timeEllapsed);
		for (Planet p : planets) {
			p.draw(gl, timeEllapsed);
		}
	}
		
	public void toggleDrawPath() {
		for (Planet p : planets) {
			p.toggleDrawPath();
		}
	}
	
	public void toggleOrbit() {
		for (Planet p : planets) {
			p.toggleOrbit();
		}
	}
	
	public void toggleAxes() {
		sun.toggleAxis();
		for (Planet p : planets) {
			p.toggleAxis();
		}
	}
	
	public Sun getSun() {
		return this.sun;
	}
}
