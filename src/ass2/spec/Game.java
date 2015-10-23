package ass2.spec;


import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener, KeyListener{

    private Terrain myTerrain;
    int angle = 0;
    int angleCamera = 0;
    double angleSun = 0;
    double yAngle = 0;
    double theta = 0;
    double camSpeed = 0.3;
    double posCamx = 0, posCamz = 0;
    double[] rotationCam;
    boolean firstPerson;
    float green;
    float red;
    boolean night;
    boolean rotateSun;
    int openMouth;
    double angleAvatar;

    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;
        firstPerson = false;
        night = false;
        rotateSun = false;
        green = 0.9f;
        red = 0.9f;
        openMouth = 1;
        //Initial cam position 
        rotationCam = new double[]{0, 0, 0};   
    }
    
    /** 
     * Run the game.
     *
     */
    public void run() {
    	  GLProfile glp = GLProfile.getDefault();
          GLCapabilities caps = new GLCapabilities(glp);
          GLJPanel panel = new GLJPanel();
          panel.addGLEventListener(this);
          panel.addKeyListener(this);
          panel.setFocusable(true);  

 
          // Add an animator to call 'display' at 60fps        
          FPSAnimator animator = new FPSAnimator(60);
          animator.add(panel);
          animator.start();

          getContentPane().add(panel);
          setSize(800, 800);        
          setVisible(true);
          setDefaultCloseOperation(EXIT_ON_CLOSE);        
    }
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        Game game = new Game(terrain);
        game.run();
    }

	@Override
	public void display(GLAutoDrawable drawable) {
		//Basic stuff first
		GL2 gl = drawable.getGL().getGL2();
		// Sky color
		if (!night) gl.glClearColor(0.88f, 1f, 1f, 1.0f);
		else gl.glClearColor(0f, 0f, 0f, 1);
		if (!night && rotateSun) updateSun();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT  );
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
	    
	    setUpCamera(gl);
	    //Start drawing
	    gl.glPushMatrix();
			// Draw Light
			if (!night) {
				gl.glEnable(GL2.GL_LIGHT0);
				gl.glDisable(GL2.GL_LIGHT1);
		        gl.glPushMatrix();
		        	float lightDir[] = { 3*myTerrain.getSunlight()[0], 3*myTerrain.getSunlight()[1], 3*myTerrain.getSunlight()[2], 0.0f };
		        	if (rotateSun) {
		        		float sun_height = 4.0f;
		        		lightDir[0] = myTerrain.size().width/2;
		        		lightDir[1] = sun_height;
		        		lightDir[2] = myTerrain.size().height/2;
		        		gl.glTranslated(lightDir[0], 0 ,lightDir[2]);
		        		gl.glRotated(angleSun, -1.0, 0.0, 1.0);
		        	}
		        	else {
		                green = 0.9f;
		                red = 0.9f;
		        	    angleSun = 0;
		        	}
			    	// Draw Sun		        	    
		        	gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightDir, 0);
	        	    gl.glTranslatef(lightDir[0], lightDir[1], lightDir[2]);
			    	if (rotateSun) gl.glRotated(theta, 0, 1, 0);
			    	double sun_lines_length = 0.35;
			    	double stack = 3;
			    	double sun_rays = 10;
			    	drawSun(gl, sun_lines_length, stack, sun_rays);
				gl.glPopMatrix();
			}
			else {
				gl.glDisable(GL2.GL_LIGHT0);
				gl.glEnable(GL2.GL_LIGHT1);
				gl.glPushMatrix();
					double hav;
					hav = myTerrain.altitude(posCamx, posCamz);
					gl.glTranslated(posCamx, hav + 0.2, posCamz);
					gl.glRotated(angleAvatar, 0, 1, 0);
					gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, new float[]{0.0f,0.0f,0.0f,1.0f}, 0);
					gl.glEnable(GL2.GL_LIGHT1);
					// Parameters for Torch (spotlight)
			    	float spotDirection[] = {0.1f, 0.1f, 1.0f}; // Spotlight direction.
			    	float spotAngle = 45.0f; // Spotlight cone half-angle.
		        	float spotExponent = 2.0f; // Spotlight exponent = attenuation factor.
		        	gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_CUTOFF, spotAngle);
		        	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPOT_DIRECTION, spotDirection, 0);	        	   
		        	gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_EXPONENT, spotExponent);
	        	gl.glPopMatrix();
			}
			if(!this.firstPerson)
			{
				double hav;
				hav = myTerrain.altitude(posCamx, posCamz);
				gl.glPushMatrix();
					gl.glTranslated(posCamx, hav + 0.20 , posCamz);
					gl.glRotated(-90,0,1,0);
					if (night){
			    		int slices = 20;
			    		float diameter_cone = 0.03f;
			    		float height_cone = 0.3f;
			    		double diameter_fire = 0.05;
			    		float lightDirAvatar[] = {0.0f, 0.4f , 0f, 1f };
			    		gl.glPushMatrix();
				    		gl.glRotated(angleAvatar, 0, 1, 0);
				        	drawTorch(gl, lightDirAvatar, slices, diameter_cone, height_cone, diameter_fire);
			        	gl.glPopMatrix();
					}
					//Draw avatar 
					gl.glPushMatrix();
						gl.glRotated(angleAvatar, 0, 1, 0);
						drawAvatar(gl);
					gl.glPopMatrix();
				gl.glPopMatrix();
			}
			
			// Draw others
		    for(Other o : myTerrain.others())
		    	o.draw(gl);
			
			// Draw roads
			for(Road rd : myTerrain.roads()) {
				double[] p0 = rd.point(0);
				double h = myTerrain.altitude(p0[0], p0[1]);
				rd.draw(gl, h, 0.01);
			}
			
			// Draw trees
		    for (Tree tree: myTerrain.trees()) {
		    	gl.glPushMatrix();
			    	double x = tree.getPosition()[0];
			    	double y = tree.getPosition()[1];
			    	double z = tree.getPosition()[2];
			    	double height_tree = 1.0;
			    	double diameter_tree = 0.1;
			    	double diameter_leaves = 0.4;
			    	gl.glTranslated(x, y, z);
			    	tree.draw(gl, height_tree, diameter_tree, diameter_leaves);
		    	gl.glPopMatrix();
		    }
		    // Draw terrain
	        myTerrain.draw(gl);
	    gl.glPopMatrix();
	}
	
	public void drawAvatar(GL2 gl) {
    	float matAmbAndDifAvatar[] = {0.7f, 0.7f, 0.0f, 1.0f};
        float matSpecAvatar[] = { 0.3f, 0.3f, 0.3f, 1.0f };
        float matShineAvatar[] = { 20.0f };
        float emmSun[] = {0.0f, 0.0f, 0.0f, 1.0f};
        float matAmbAndDifAvatarBack[] = {0.5f, 0.0f, 0.0f, 1.0f};
        float matSpecAvatarBack[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        float matShineAvatarBack[] = { 0.0f };
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmSun,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifAvatar,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecAvatar,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineAvatar,0);
        gl.glMaterialfv(GL2.GL_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifAvatarBack,0);
        gl.glMaterialfv(GL2.GL_BACK, GL2.GL_SPECULAR, matSpecAvatarBack,0);
        gl.glMaterialfv(GL2.GL_BACK, GL2.GL_SHININESS, matShineAvatarBack,0);
        gl.glPushMatrix();
			// Above terrain
			gl.glTranslated(0, 0.3, 0);
	        uvSphere(gl, 0.3, 25, 30, openMouth);
	    gl.glPopMatrix();
	}
	
	public static void uvSphere(GL2 gl, double radius, int slices, int stacks, int openMouth) {
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
		if (radius <= 0)
			throw new IllegalArgumentException("Radius must be positive.");
		if (slices < 3)
			throw new IllegalArgumentException("Number of slices must be at least 3.");
		if (stacks < 2)
			throw new IllegalArgumentException("Number of stacks must be at least 2.");
		for (int j = 0; j < stacks; j++) {
			double latitude1 = (Math.PI/stacks) * j - Math.PI/2;
			double latitude2 = (Math.PI/stacks) * (j+1) - Math.PI/2;
			double sinLat1 = Math.sin(latitude1);
			double cosLat1 = Math.cos(latitude1);
			double sinLat2 = Math.sin(latitude2);
			double cosLat2 = Math.cos(latitude2);
			gl.glBegin(GL2.GL_QUAD_STRIP);
			for (int i = openMouth; i <= slices - openMouth; i++) {
				double longitude = (2*Math.PI/slices) * i;
				double sinLong = Math.sin(longitude);
				double cosLong = Math.cos(longitude);
				double x1 = cosLong * cosLat1;
				double y1 = sinLong * cosLat1;
				double z1 = sinLat1;
				double x2 = cosLong * cosLat2;
				double y2 = sinLong * cosLat2;
				double z2 = sinLat2;
				gl.glNormal3d(x2,y2,z2);
				gl.glVertex3d(radius*x2,radius*y2,radius*z2);
				gl.glNormal3d(x1,y1,z1);
				gl.glVertex3d(radius*x1,radius*y1,radius*z1);
			}
			gl.glEnd();
		}
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_FALSE);
		// First Eye
		float matAmbAndDifAvatarEyes[] = {0.0f, 0.0f, 0.0f, 1.0f};
		radius = 0.05;
		GLUT glut = new GLUT();
		gl.glPushMatrix();
			gl.glTranslated(0.08,0.22,-0.15);
			gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifAvatarEyes,0);
        	glut.glutSolidSphere(radius, 40, 40);
	 	gl.glPopMatrix();
	 	// Second Eye
		gl.glPushMatrix();
			gl.glTranslated(0.08,0.22,0.15);
			gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifAvatarEyes,0);
	    	glut.glutSolidSphere(radius, 40, 40);
		gl.glPopMatrix();
	} 
	
	public void drawTorch(GL2 gl, float[] lightDirAvatar, int slices, float diameter_cone, float height_cone, double diameter_fire){
    	gl.glPushMatrix();
	    	float matAmbAndDifTorch[] = {0.3f, 0.16f, 0.15f, 1.0f};
	        float matSpecTorch[] = { 0.8f, 0.8f, 0.8f, 1.0f };
	        float matShineTorch[] = { 3.0f };
	        float emmTorch[] = {0.1f, 0.1f, 0.1f, 1.0f};
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmTorch,0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifTorch,0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecTorch,0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineTorch,0);
			gl.glTranslated(lightDirAvatar[0], lightDirAvatar[1], lightDirAvatar[2]);
	    	gl.glBegin(GL2.GL_TRIANGLE_FAN);
	       		gl.glNormal3d(0,1,0);
	       		gl.glVertex3d(0,0,0);
	       		double angleStep = 2*Math.PI/slices;
	            for (int i = 0; i <= slices ; i++){//slices; i++) {
	                double a0 = i * angleStep;
	                double x0 = diameter_cone*Math.cos(a0);
	                double z0 = diameter_cone*Math.sin(a0);
	                gl.glVertex3d(x0,height_cone,z0);
	            }
	       	gl.glEnd();
	        gl.glPushMatrix();
		    	float matAmbAndDifFire[] = {0.0f, 0.0f, 0.0f, 1.0f};
		        float matSpecFire[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		        float matShineFire[] = { 3.0f };
		        float emmFire[] = {0.7f, 0.3f, 0.0f, 1.0f};
		        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmFire,0);
		        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifFire,0);
		        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecFire,0);
		        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineFire,0);
	        	GLUT glut = new GLUT();
	        	gl.glTranslated(0, height_cone + diameter_fire, 0);
	        	glut.glutSolidSphere(diameter_fire, 40, 40);
	    	gl.glPopMatrix();
    	gl.glPopMatrix();
	}
	
	public void drawSun(GL2 gl,double sun_lines_length, double stack, double sun_rays){
    	float matAmbAndDifSun[] = {red, green, 0.0f, 1.0f};
        float matSpecSun[] = { 0.3f, 0.3f, 1f, 1.0f };
        float matShineSun[] = { 20.0f };
        float emm[] = {red, green, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifSun,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecSun,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineSun,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
		gl.glPushMatrix();
	        for (int j = 0; j < stack; j++) { 
	        	gl.glRotated(180f / stack, 0, 1, 0 );
	        	gl.glPushMatrix();
			        for (int i = 0; i < sun_rays; i++) { 
			        	gl.glRotated(360f / sun_rays, 0, 0, 1 );
			            gl.glLineWidth(8.0f);
			        	gl.glBegin(GL2.GL_LINES);
			          	   gl.glVertex2d(0,0);
			          	   gl.glVertex2d(sun_lines_length,0);
			            gl.glEnd();
			            gl.glLineWidth(1.0f);
			        }
		        gl.glPopMatrix();
	        }
        gl.glPopMatrix();
        GLUT glut = new GLUT();
        glut.glutSolidSphere(0.1, 40, 40);
	}
	
    //Update model of sun used in texture
    private void updateSun() {
    	angleSun = (angleSun + 2) % 360;
    	theta = (theta + 5) % 360;
    	// Reset color of the sun
		if (angleSun == 320) red = 0.4f;
		// Update
		green = 0.4f;
		red = red + 1.2f/360f;
    }
    
	public void setUpLightning(GL2 gl){
	    gl.glEnable(GL2.GL_LIGHTING);
	  
	    gl.glEnable(GL2.GL_NORMALIZE);
	    // low ambient light for the night
        float lightAmbNight[] = { 0.0f, 0.0f, 0.0f, 1.0f };
    	float lightDifAndSpecNight[] = {1.0f, 1.0f, 1.0f, 1.0f};
    	float globAmbNight[] = {0.15f, 0.15f, 0.15f, 1.0f};
    	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightAmbNight,0);
    	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, lightDifAndSpecNight,0);
    	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightDifAndSpecNight,0);
    	//Spotlight parameters
    	float lightDirAvatar[] = {0.3f, 0.3f , 0f, 1f };
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightDirAvatar, 0);
		// Parameters for Torch
    	float spotAngle = 45.0f; // Spotlight cone half-angle.
    	float spotDirection[] = {0.0f, 0.0f, 1f}; // Spotlight direction.
    	float spotExponent = 2.0f; // Spotlight exponent = attenuation factor.
    	gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_CUTOFF, spotAngle);
    	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPOT_DIRECTION, spotDirection,0);    
    	gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_EXPONENT, spotExponent);
    	
    	
		// light for the sun (day)
        float lightAmb[] = { 0.4f, 0.4f, 0.4f, 1.0f };
        float lightDifAndSpec[] = { 0.6f, 0.6f, 0.6f, 1.0f };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmb,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDifAndSpec,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightDifAndSpec,0);
        
        gl.glEnable(GL2.GL_LIGHT0);
	    gl.glEnable(GL2.GL_LIGHT1);
        
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmbNight,0);
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL2.GL_TRUE);
        gl.glDisable(GL2.GL_LIGHT1);
	}
	public void setUpCamera(GL2 gl){
		
		double h = 0;
		double camHeight = 3;
		double thirdPersDist = 3;
		
		//Get height from terrain 
		h = myTerrain.altitude(posCamx, posCamz);
	
		//Apply camera rotations
		if(firstPerson)
			gl.glRotated(-rotationCam[0], 1, 0, 0);
		else
			gl.glRotated(25, 1, 0 , 0);	
		
		//180 for the cam to be facing the positive z
		gl.glRotated(180, 0, 1, 0);
		gl.glRotated(-rotationCam[2], 0, 0, 1);
		
		//If we are in third person the cam rotates in a difFerent
		// axis for the y rotation
		if(!firstPerson)
		{
			gl.glTranslated(0,0,thirdPersDist);
			gl.glRotated(-rotationCam[1], 0, 1, 0);
			gl.glTranslated(0,0,-thirdPersDist);
		}	
		else
			gl.glRotated(-rotationCam[1], 0, 1, 0);
		
		if(firstPerson)
			//0.5 more so it isn't inside the terrain.
			gl.glTranslated(-posCamx, -(h+0.5), -posCamz);
		else
			gl.glTranslated(-posCamx, -(h + camHeight), -(posCamz -thirdPersDist));
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// Temporary
		GL2 gl = drawable.getGL().getGL2();
	    gl.glClearColor(0f, 0f, 0f, 1);
	    gl.glEnable(GL2.GL_DEPTH_TEST);
	    setUpLightning(gl);
	    myTerrain.loadTextures(gl);
	}
	

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2();
	    gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
	    double distNear = 0.01;
	    double ar = ((double)width)/((double)height);
	    if(!firstPerson) distNear = 2;
	    GLU glu = new GLU();
	    glu.gluPerspective(60, ar, distNear, 100);
//	    double window = 0.5;
//	    gl.glOrtho(-window, window, -window, window, 0.1, 10);
//	    gl.glFrustum(-window, window, -window, window, 0.5, 10);
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		int direction = 1;
		switch (e.getKeyCode()) {  
			case KeyEvent.VK_DOWN:
				direction = -1;
			case KeyEvent.VK_UP:
				// Control the size of the Pacman's mouth
				if (openMouth == 3) openMouth = 1;
				else openMouth = 3;
				// Update camera position 
				double rads = 0;
				if(firstPerson) rads = Math.toRadians(rotationCam[1]);
				else rads = Math.toRadians(angleAvatar);
				posCamz+= direction*Math.cos(rads)*camSpeed;
				posCamx+= direction*Math.sin(rads)*camSpeed;
				break;
			case KeyEvent.VK_LEFT:
				if(firstPerson || e.isShiftDown()) 	rotationCam[1] = (rotationCam[1] + 5)%360;
				else angleAvatar = (angleAvatar + 5) % 360;
				break;
			case KeyEvent.VK_RIGHT:
				if(firstPerson || e.isShiftDown()) rotationCam[1] = (rotationCam[1] - 5)%360;
				else angleAvatar = (angleAvatar - 5) % 360;
				break;
			 case KeyEvent.VK_J:
				//angle = (angle - 5) % 360;
				 if(!firstPerson) rotationCam[0] = (rotationCam[0] - 5) % 360;
				break;
		     case KeyEvent.VK_U:
				//angle = (angle + 5) % 360;
		    	 if(!firstPerson )rotationCam[0] =  (rotationCam[0] + 5) % 360;
				break;	
		    case KeyEvent.VK_P:
		   		firstPerson = !firstPerson;
		   		if(firstPerson) rotationCam[1] = angleAvatar;
		   		else angleAvatar = rotationCam[1];
		   		//Force reshape
		   		//TODO: Force reshape in a better way
		   		Dimension d = getSize();
		   		setSize(d.height/2, d.width/2);
		   		setSize(d.height, d.width);
		   		break;
		
		    case KeyEvent.VK_C:
				if (!night) night = true;
				else (night) = false;
				myTerrain.setNight(night);
				/*for(Other o : myTerrain.others())
					o.setNight(night);
				for(Road r : myTerrain.roads())
					r.setNight(night);*/
		   		break;
		    case KeyEvent.VK_SPACE:
		    	myTerrain.switchWire();
		    	break;
		   /* case KeyEvent.VK_SPACE:
		    	for(Other o : myTerrain.others()) o.switchShader();
		   		break;*/
		    case KeyEvent.VK_Z:
				if (!rotateSun) rotateSun = true;
				else (rotateSun) = false;
		   		break;
			default:
				break;
		 }
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
