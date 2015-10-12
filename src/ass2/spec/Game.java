package ass2.spec;

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



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener, KeyListener{

    private Terrain myTerrain;
    int angle = 0;
    int trans = 0;

    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;
   
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
          setSize(800, 600);        
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
		// TODO Auto-generated method stub
		
		//Basic stuff first
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT  ); // Fills the scene with blue.
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    int width = myTerrain.size().width;
	    int height = myTerrain.size().height;
	    gl.glLoadIdentity();
	    
	    gl.glPushMatrix();
	    gl.glColor3f(1,0,0);
	  
	   
	    
	    
	    double[] v1 = new double[3];

		v1[0] = 1;
		v1[2] = -1;
	    double[] v2 = new double[3];
	    //Start drawing   
	    
	    
	    
	    
	   
	    //gl.glTranslated(0,0, -5);
		gl.glRotated(angle,1,0,0);
		gl.glTranslated(trans,0,1);
	 
	    /*
	    
	    gl.glRotated(180,0,1,0);
	    
	    gl.glTranslated(-width/2, 0, -height/2);
    	gl.glRotated(angle,1,0,0);
    	gl.glTranslated(width/2, 0, height/2);
    	
    	   gl.glScaled(0.3,0.3, 0.3);*/
    	
	    
	    
	    gl.glBegin(GL.GL_TRIANGLES);
	    for(int i = 0; i < width-1; i++){
	    	
	    	for(int j = 0; j < height-1; j++)
	    	{
	    		v1[1] = myTerrain.getGridAltitude(i+1, j) - myTerrain.getGridAltitude(i, j+1);
	    		
	    		v2[0] = 0;
	    		v2[2] = -1;
	    		v2[1] = myTerrain.getGridAltitude(i, j) - myTerrain.getGridAltitude(i, j+1);
	    		
	    		double[] n1 = crossProduct(v1,v2);
	    		
	    		gl.glNormal3d(n1[0], n1[1], n1[2]);
	    		
		    	gl.glVertex3d(i,myTerrain.getGridAltitude(i, j),j); // P0
		    	gl.glVertex3d(i,myTerrain.getGridAltitude(i, j+1),j+1);// P1
		    	gl.glVertex3d(i+1,myTerrain.getGridAltitude(i+1, j),j); // P2
		    	
		    	
		    	v2[0] = 1;
	    		v2[2] = 0;
	    		v2[1] = myTerrain.getGridAltitude(i+1, j+1) - myTerrain.getGridAltitude(i, j+1);
	    		n1 = crossProduct(v2,v1);
	    		gl.glNormal3d(n1[0], n1[1], n1[2]);
	    		
		    	
		    	
		    	gl.glVertex3d(i,myTerrain.getGridAltitude(i, j+1),j+1);// P3
		    	gl.glVertex3d(i+1,myTerrain.getGridAltitude(i+1, j+1),j+1); // P4
		    	gl.glVertex3d(i+1,myTerrain.getGridAltitude(i+1, j),j); // P5
	    		
	    		
	    	}
	    	
	    }
	    gl.glEnd();
	    gl.glPopMatrix();
		
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		//PS: From assig1
		GL2 gl = drawable.getGL().getGL2();
	    // The next three lines set up the coordinates system.
	    gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
	    GLU glu = new GLU();
	    gl.glOrtho(-10, 10, -10, 10, 1, 20);
	   // gl.glOrtho(-3, 3, -3, 3, 1, 10);
	    
	    
	   
	    //gl.glFrustum(-1, 1, -1, 1, 1, 20);
	    //glu.gluPerspective(60, 1, 1, 8);
	    
	   
	    
		
	}
	
	private double[] crossProduct(double[] a, double[] b)
	{
		double[] result = new double[3];
		
		result[0] = (b[2]*a[1] - a[2]*b[1]);
		result[1] = (b[0]*a[2] - a[0]*b[2]);
		result[2] = (b[1]*a[0] - a[1]*b[0]);
		
		return result;
	
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {  

				 
		
		case KeyEvent.VK_LEFT:
			trans++;
			
			break;
			
		case KeyEvent.VK_RIGHT:
			trans--;
			
			break;
				  
		 case KeyEvent.VK_DOWN:
		       
			  angle = (angle - 10) % 360;
			  break;
	     case KeyEvent.VK_UP:
		     
			  angle = (angle + 10) % 360;
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
