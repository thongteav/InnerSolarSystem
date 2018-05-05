import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import utils.Movement;
import viewer.Camera;

/**
 * The texture used for the planets and Sun in this project is free to use from: https://www.solarsystemscope.com/textures/
 * 
 * This class handles the interactions like key presses and apply lighting the whole scene
 * 
 * @author Thong Teav
 * 14883251
 *
 */
public class Main implements GLEventListener, KeyListener {
	private static int WIN_HEIGHT = 1200;
	private static int WIN_WIDTH = 1200;
	
	private Camera camera;
	private SolarSystem solarSystem;
	private double timeEllapsed, tick, prevTick;
	private boolean debugging;
	
	//lighting
	float globalAmbient[] = { 0.4f, 0.4f, 0.4f, 1 }; 	// global light properties
	public float[] lightPosition = { 0.0f, 0.0f, 0.0f, 1.0f };
	public float[] ambientLight = { 0.5f, 0.5f, 0.5f, 1 };
	public float[] diffuseLight = { 0.5f, 0.5f, 0.5f, 0.8f };
	
	@Override
	public void display(GLAutoDrawable gld) {
		GL2 gl = gld.getGL().getGL2();
		// clear the depth and color buffers
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		camera.draw(gl);
		
		// calculate time since last frame
		tick = System.currentTimeMillis() / 1000.0;
		timeEllapsed = tick - prevTick;
		if (prevTick > 0) {
			camera.update(timeEllapsed);
		}
		prevTick = tick;
		
		lights(gl);
		
		if (debugging) {
			drawXYZ(gl);
		}
		
		solarSystem.draw(gl, timeEllapsed);
	}
	
	public void lights(GL2 gl){
		// set the global ambient light level
	    gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globalAmbient, 0); 
	    //set light 0 properties
	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0);
		//normalise the normal surface vectors
		gl.glEnable(GL2.GL_NORMALIZE);
		//position light 0 at the origin
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPosition, 0);
		//enable light 0
		gl.glEnable(GL2.GL_LIGHT0);
	}
	
	//draws the x,y,z axis from the origin
	public void drawXYZ(GL2 gl) {	
		gl.glLineWidth(5f);
		gl.glBegin(GL2.GL_LINES);
			//draw X in red
			gl.glColor3f(1, 0, 0);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(20, 0, 0);
			//draw Y in green
			gl.glColor3f(0, 1, 0);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, 20, 0);
			//draw Z in blue
			gl.glColor3f(0, 0, 1);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, 0, 20);
		gl.glEnd();
	}

	@Override
	public void dispose(GLAutoDrawable gld) {
				
	}

	@Override
	public void init(GLAutoDrawable gld) {
		GL2 gl = gld.getGL().getGL2();		
		camera = new Camera();
		solarSystem = new SolarSystem();
		solarSystem.getSun().createLineList(gl);//create the display list of lines representing the corona
		
		// enable depth test and set shading mode
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glEnable(GL2.GL_BLEND);
		
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		camera.newWindowSize(width, height);
	}
	
	public static void main(String[] args) {
		Frame frame = new Frame("Inner Solar System Viewer");
		GLCanvas canvas = new GLCanvas();
		Main app = new Main();
		
		canvas.addGLEventListener(app);
		canvas.addKeyListener(app);
		
		frame.add(canvas);
		frame.setSize(WIN_WIDTH, WIN_HEIGHT);
		final FPSAnimator animator = new FPSAnimator(canvas, 60);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Run this on another thread than the AWT event queue to
				// make sure the call to Animator.stop() completes before
				// exiting
				new Thread(new Runnable() {

					@Override
					public void run() {
						animator.stop();
						System.exit(0);
					}
				}).start();
			}
		});
		// Center frame
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		canvas.setFocusable(true);
		canvas.requestFocus();
		animator.start();
	}

	/**
	 * Handles the key presses to set different movements of the camera and toggles drawing on or off.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
			case KeyEvent.VK_UP:
				camera.setPitch(Movement.PITCH_UP);
				break;
			case KeyEvent.VK_DOWN:
				camera.setPitch(Movement.PITCH_DOWN);
				break;
			case KeyEvent.VK_LEFT:
				camera.setYaw(Movement.YAW_LEFT);
				break;
			case KeyEvent.VK_RIGHT:
				camera.setYaw(Movement.YAW_RIGHT);
				break;
			case KeyEvent.VK_W:
				camera.setMove(Movement.FORWARD);
				break;
			case KeyEvent.VK_S:
				camera.setMove(Movement.BACKWARD);
				break;
			case KeyEvent.VK_A:
				camera.setStrafe(Movement.STRAFE_LEFT);
				break;
			case KeyEvent.VK_D:
				camera.setStrafe(Movement.STRAFE_RIGHT);
				break;
			case KeyEvent.VK_E:
				camera.setElevate(Movement.UP);
				break;
			case KeyEvent.VK_C:
				camera.setElevate(Movement.DOWN);
				break;			
			case KeyEvent.VK_O:
				solarSystem.toggleDrawPath();
				break;
			case KeyEvent.VK_SPACE:
				solarSystem.toggleOrbit();
				break;
			case KeyEvent.VK_T:
				this.debugging = !this.debugging;
				solarSystem.toggleAxes();
				break;
		}
	}

	/**
	 * Using the key released to reset the corresponding movement of the camera.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
			case KeyEvent.VK_UP:
				camera.setPitch(Movement.NONE);
				break;
			case KeyEvent.VK_DOWN:
				camera.setPitch(Movement.NONE);
				break;
			case KeyEvent.VK_LEFT:
				camera.setYaw(Movement.NONE);
				break;
			case KeyEvent.VK_RIGHT:
				camera.setYaw(Movement.NONE);
				break;
			case KeyEvent.VK_W:
				camera.setMove(Movement.NONE);
				break;
			case KeyEvent.VK_S:
				camera.setMove(Movement.NONE);
				break;
			case KeyEvent.VK_A:
				camera.setStrafe(Movement.NONE);
				break;
			case KeyEvent.VK_D:
				camera.setStrafe(Movement.NONE);
				break;
			case KeyEvent.VK_E:
				camera.setElevate(Movement.NONE);
				break;
			case KeyEvent.VK_C:
				camera.setElevate(Movement.NONE);
				break;			
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}
