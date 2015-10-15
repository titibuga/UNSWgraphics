package ass2.spec;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private double[] myPos;
    
    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }
    
    public double[] getPosition() {
        return myPos;
    }
   
    public void draw(GL2 gl, double height, double diameter, double diameter_leaves) {
    	int slices = 100;
    	double y1 = 0;
    	double y2 = height;
    	
    	// Materials and Color of Trees
        float matAmbAndDif[] = {0.4f, 0.16f, 0.15f, 1.0f};
        float matSpec[] = { 0.5f, 0.5f, 0.5f, 1.0f };
        float matShine[] = { 10.0f };
        float emm[] = {0.0f, 0.0f, 0.0f, 1.0f};
        
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpec,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShine,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
//    	gl.glColor3d(0.40,0.16, 0.15);
        
    	gl.glBegin(GL2.GL_TRIANGLE_FAN);{
    		 gl.glNormal3d(0,-1,0);
    		 gl.glVertex3d(0,y1,0);
    		 double angleStep = 2*Math.PI/slices;
             for (int i = 0; i <= slices ; i++){//slices; i++) {
                 double a0 = i * angleStep;
                 double x0 = diameter*Math.cos(a0);
                 double z0 = diameter*Math.sin(a0);
                 gl.glVertex3d(x0,y1,z0);
             }
    	}
    	gl.glEnd();
    	
    	gl.glBegin(GL2.GL_TRIANGLE_FAN);{
   		 	double angleStep = 2*Math.PI/slices;
      		gl.glNormal3d(0,1,0);
      		gl.glVertex3d(0,y2,0);
            for (int i = 0; i <= slices ; i++){//slices; i++) {
           	 	double a0 = i * angleStep;
                double x0 = diameter*Math.cos(a0);
                double z0 = diameter*Math.sin(a0);
                gl.glVertex3d(x0,y2,z0);
            }
	   	}
	   	gl.glEnd();
	   	
    	gl.glBegin(GL2.GL_QUADS);
        {
            double angleStep = 2*Math.PI/slices;
            for (int i = 0; i <= slices ; i++){//slices; i++) {
                double a0 = i * angleStep;
                double a1 = ((i+1) % slices) * angleStep;
                
                double x0 = diameter*Math.cos(a0);
                double z0 = diameter*Math.sin(a0);

                double x1 = diameter*Math.cos(a1);
                double z1 = diameter*Math.sin(a1);

                gl.glNormal3d(x0, 0, z0);
                gl.glVertex3d(x0, y1, z0);
                gl.glVertex3d(x0, y2, z0);
                
                gl.glNormal3d(x1, 0, z1);
                gl.glVertex3d(x1, y2, z1);
                gl.glVertex3d(x1, y1, z1);               
            }

        }
        
        gl.glEnd();
        
        // Materials and Color of Leaves
        float matAmbAndDifLeaves[] = {0.0f, 0.5f, 0.0f, 1.0f};
        float matSpecLeaves[] = { 0.5f, 0.5f, 0.5f, 1.0f };
        float matShineLeaves[] = { 30.0f };
        float emmLeaves[] = {0.0f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifLeaves, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecLeaves, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineLeaves, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmLeaves, 0);
//        gl.glColor3d(0.0, 0.5, 0);
        
        //Draw Spheres
        gl.glPushMatrix();
	        gl.glTranslated(0, height, 0);
	       	GLUT glut = new GLUT();
	        glut.glutSolidSphere(diameter_leaves, 40, 40);
        gl.glPopMatrix();
    }
}
