package ass2.spec;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.GL;
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
    int trans = 0;
    int transy = 0;
    int xAngle = 0;
    int yAngle = 0;
    double camSpeed = 0.3;
    double posCamx = 0, posCamz = 0;
    double[] rotationCam;

    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;
        
        //Initial cam position facing the positive z axis
        rotationCam = new double[]{0, 180, 0};
   
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
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT  );
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
	    
	    setUpCamera(gl);
	    
	    GLUT glut = new GLUT();
	    
	    float lightDir[] = { myTerrain.getSunlight()[0], myTerrain.getSunlight()[1], myTerrain.getSunlight()[2], 0.0f };
        float lightAmb[] = { 0.2f, 0.2f, 0.2f, 1.0f };
        float lightDifAndSpec[] = { 0.8f, 0.8f, 0.8f, 1.0f };
//        float globAmb[] = { 0.3f, 0.3f, 0.3f, 1.0f };

        // Light0 properties.
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmb,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDifAndSpec,0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightDifAndSpec,0);
//        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb,0); 
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, 1); 
        
	    gl.glPushMatrix();
	    
		    //Start drawing   
			gl.glRotated(angle,1,0,0);
			gl.glTranslated(trans,transy,0);
			
			// Draw Light
			gl.glPushMatrix();
		    	gl.glRotated(xAngle, 1.0, 0.0, 0.0);
		    	gl.glRotated(yAngle, 0.0, 0.0, 1.0);
		    	gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightDir, 0);
		    	// Dark ball
		    	gl.glTranslatef(lightDir[0], lightDir[1], lightDir[2]);
		    	gl.glColor3f(0,0,0);
		    	glut.glutSolidSphere(0.2, 40, 40);
			gl.glPopMatrix();
			
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
	
	public void setUpCamera(GL2 gl){
		
		//double h = this.myTerrain.altitude(posCamx, posCamz);
		double h = 0;
		double camHeight = 2;
		Dimension s = myTerrain.size();
		
		//Get height from terrain if we are on it
		if(posCamz >= 0 && posCamx >= 0 &&
			       posCamz < s.height && posCamx < s.width)
			h = myTerrain.altitude(posCamx, posCamz);
		
		
		//GLU glu = new GLU();
		//glu.gluLookAt(0, h + camHeight, 0,
		//			  0, h, 3, 
		//			  0, 1, 0);
		
		//Apply camera rotations
		gl.glRotated(-rotationCam[0], 1, 0, 0);
		gl.glRotated(-rotationCam[1], 0, 1, 0);
		gl.glRotated(-rotationCam[2], 0, 0, 1);
			
		
		//Translate and rotate it to its proper position
		gl.glTranslated(-posCamx, -(h + camHeight), -posCamz);
		
		
		
		
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// Temporary
		GL2 gl = drawable.getGL().getGL2();
	    gl.glClearColor(1f, 1f, 1f, 1);
	    gl.glEnable(GL2.GL_DEPTH_TEST);
	    gl.glEnable(GL2.GL_LIGHTING);
	    gl.glEnable(GL2.GL_LIGHT0);
	    gl.glEnable(GL2.GL_NORMALIZE);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2();
	    gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();

	    double window = 0.5;
	  //  gl.glOrtho(-window, window, -window, window, 0.1, 10);
	    
//	    gl.glOrtho(-3, 3, -3, 3, 1, 10);
	    gl.glFrustum(-window, window, -window, window, 0.5, 10);
//	    glu.gluPerspective(60, 1, 0, 10);
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {  
			case KeyEvent.VK_U:
				posCamz+= camSpeed;
				break;
			case KeyEvent.VK_J:
				posCamz-= camSpeed;
				break;		
			case KeyEvent.VK_LEFT:
				posCamx+= camSpeed;
				break;
			case KeyEvent.VK_RIGHT:
				posCamx-= camSpeed;
				break;
			 case KeyEvent.VK_DOWN:
				//angle = (angle - 5) % 360;
				 rotationCam[0] = (rotationCam[0] - 5) % 360;
				break;
		     case KeyEvent.VK_UP:
				//angle = (angle + 5) % 360;
		    	 rotationCam[0] =  (rotationCam[0] + 5) % 360;
				break;	
		    case KeyEvent.VK_W:
		   		xAngle = xAngle - 10;
		   		if (xAngle < 0.0) xAngle += 360.0;
		   		break;
		    case KeyEvent.VK_S:
		   		xAngle = xAngle + 10;
		   		if (xAngle > 360.0) xAngle -= 360.0;
		   		break;
		    case KeyEvent.VK_A:
		   		yAngle = yAngle - 10;
		   		if (yAngle < 0.0) yAngle += 360.0;
		   		break;
		    case KeyEvent.VK_D:
		   		yAngle = yAngle + 10;
		   		if (yAngle > 360.0) yAngle -= 360.0;
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
