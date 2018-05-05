package viewer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import utils.Movement;

/**
 * Represents a 3rd person camera which doesn't follow any object
 * 
 * @author Thong Teav
 * 14883251
 */
public class Camera {
	private static final double FOV = 80;
	private final static double MOVE_DIST = 10;
	private final static double LOOK_AT_DIST = 40; //depends on eye[] and lookAt[]	
	
	private double windowWidth = 1;
	private double windowHeight = 1;
	
	private double eye[] = {40, 0, 0}; //position of the camera
	private double lookAt[] = {0, 0, 0}; //position to look at
	
	private double pitchAngle = 0; //since we're looking at straight ahead at y = 0, pitch angle is 0
	private double yawAngle = 180;	//the middle is 180 degrees

	private Movement yaw = Movement.NONE;
	private Movement pitch = Movement.NONE;
	private Movement strafe = Movement.NONE;
	private Movement move = Movement.NONE;
	private Movement elevate = Movement.NONE;	
	
	public void draw(GL2 gl){
		// set up projection first
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU glu = new GLU();
        //clipping plane
        glu.gluPerspective(FOV, (float) windowWidth / (float) windowHeight, 0.1, 500);
        // set up the camera position and orientation
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(eye[0],    eye[1],    eye[2], 	// eye
                	  lookAt[0], lookAt[1], lookAt[2],  // looking at 
                      0.0,       1.0,       0.0); 		// y is up   
	}
	
	/**
	 * Checks the movement and calls the corresponding method
	 * 
	 * @param timeEllapsed
	 */
	public void update(double timeEllapsed) {
		if (this.pitch == Movement.PITCH_UP) {
			pitchUp(timeEllapsed);
		}
		else if (this.pitch == Movement.PITCH_DOWN) {
			pitchDown(timeEllapsed);
		}
		
		if (this.yaw == Movement.YAW_LEFT) {
			yawLeft(timeEllapsed);
		}
		else if (this.yaw == Movement.YAW_RIGHT) {
			yawRight(timeEllapsed);
		}
		
		if (this.strafe == Movement.STRAFE_LEFT) {
			strafeLeft(timeEllapsed);
		}
		else if (this.strafe == Movement.STRAFE_RIGHT) {
			strafeRight(timeEllapsed);
		}
		
		if (this.elevate == Movement.UP) {
			moveUp(timeEllapsed);
		}
		else if (this.elevate == Movement.DOWN) {
			moveDown(timeEllapsed);
		}
		
		if (this.move == Movement.FORWARD) {
			moveForward(timeEllapsed);
		}
		else if (this.move == Movement.BACKWARD) {
			moveBackward(timeEllapsed);
		}
	}		

	public void newWindowSize(int width, int height) {
        windowWidth = Math.max(1.0, width);
        windowHeight = Math.max(1.0, height);
	}
	
	//movement methods-----------------------------------------------------------------------------
	public void moveUp(double timeEllapsed) {
		eye[1] += MOVE_DIST * timeEllapsed;
		lookAt[1] += MOVE_DIST * timeEllapsed;
	}
	
	public void moveDown(double timeEllapsed) {
		eye[1] -= MOVE_DIST * timeEllapsed;
		lookAt[1] -= MOVE_DIST * timeEllapsed;
	}
	
	public void moveBackward(double timeEllapsed) {
		this.eye = projection(-MOVE_DIST, this.pitchAngle, this.yawAngle, timeEllapsed);
		this.lookAt = projection(LOOK_AT_DIST, this.pitchAngle, this.yawAngle, timeEllapsed);
	}
	
	public void moveForward(double timeEllapsed) {
		this.eye = projection(MOVE_DIST, this.pitchAngle, this.yawAngle, timeEllapsed);
		this.lookAt = projection(LOOK_AT_DIST, this.pitchAngle, this.yawAngle, timeEllapsed);
	}
	
	public void pitchUp(double timeEllapsed) {
		if (this.pitchAngle + MOVE_DIST * timeEllapsed < 89) {
			this.pitchAngle += MOVE_DIST * timeEllapsed;
		}
		this.lookAt = projection(LOOK_AT_DIST, this.pitchAngle, this.yawAngle, timeEllapsed);
	}
	
	public void pitchDown(double timeEllapsed) {
		if (this.pitchAngle - MOVE_DIST * timeEllapsed > -89) {
			this.pitchAngle -= MOVE_DIST * timeEllapsed;
		}
		this.lookAt = projection(LOOK_AT_DIST, this.pitchAngle, this.yawAngle, timeEllapsed);
	}
	
	public void yawLeft(double timeEllapsed) {
		this.yawAngle -= MOVE_DIST * timeEllapsed;
		if(this.yawAngle <= 0) {
			this.yawAngle = 360;
		}
		this.lookAt = projection(LOOK_AT_DIST, this.pitchAngle, this.yawAngle, timeEllapsed);
	}
	
	public void yawRight(double timeEllapsed) {
		this.yawAngle = (this.yawAngle + MOVE_DIST * timeEllapsed) % 360;
		this.lookAt = projection(LOOK_AT_DIST, this.pitchAngle, this.yawAngle, timeEllapsed);
	}
	
	public void strafe(double angle, double timeEllapsed) {
		eye[0] = eye[0] + Math.cos(Math.toRadians(angle)) * MOVE_DIST * timeEllapsed;
		eye[2] = eye[2] + Math.sin(Math.toRadians(angle)) * MOVE_DIST * timeEllapsed;
		lookAt[0] = lookAt[0] + Math.cos(Math.toRadians(angle)) * MOVE_DIST * timeEllapsed;
		lookAt[2] = lookAt[2] + Math.sin(Math.toRadians(angle)) * MOVE_DIST * timeEllapsed;
	}
	
	public void strafeLeft(double timeEllapsed) {
		strafe(this.yawAngle - 90, timeEllapsed);
	}
	
	public void strafeRight(double timeEllapsed) {
		strafe(this.yawAngle + 90, timeEllapsed);
	}
	
	public double[] projection(double distance, double pitchAngle, double yawAngle, double timeEllapsed) {
		double[] newPoint = new double[3];
		
		newPoint[1] = eye[1] + Math.sin(Math.toRadians(pitchAngle)) * distance * timeEllapsed;
		double distXZ = Math.cos(Math.toRadians(pitchAngle)) * distance * timeEllapsed;
		newPoint[0] = eye[0] + Math.cos(Math.toRadians(yawAngle)) * distXZ;
		newPoint[2] = eye[2] + Math.sin(Math.toRadians(yawAngle)) * distXZ;
		
		return newPoint;
	}
	//---------------------------------------------------------------------------------------------
	
	//movement setters--------------------------------------------------------------------------------
	public void setYaw(Movement m) {
		this.yaw = m;
	}
	
	public void setPitch(Movement m) {
		this.pitch = m;
	}
	
	public void setStrafe(Movement m) {
		this.strafe = m;
	}
	
	public void setElevate(Movement m) {
		this.elevate = m;
	}
	
	public void setMove(Movement m) {
		this.move = m;
	}
	//---------------------------------------------------------------------------------------------
}
