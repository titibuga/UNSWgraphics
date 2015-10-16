package ass2.spec;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private double[] myPos;
	//Texture file information
	private String TEX_0 = "src/ass2/spec/trunk_texture.jpg";
	private String TEX_1 = "src/ass2/spec/leaves_texture.jpg";
	private String TEX_F_0 = ".jpg";
	private String TEX_F_1 = ".jpg";
	
	//Texture data
	private MyTexture myTextures[] = new MyTexture[2];
	
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
    	
        myTextures[0] = new MyTexture(gl,TEX_0,TEX_F_0,true);
    	gl.glActiveTexture(GL2.GL_TEXTURE0); 	
    	gl.glEnable(GL2.GL_TEXTURE_2D);
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());  
    	
    	// Materials and Color of Trees
        float matAmbAndDifTree[] = {0.3f, 0.16f, 0.15f, 1.0f};
        float matSpecTree[] = { 0.1f, 0.1f, 0.1f, 1.0f };
        float matShineTree[] = { 2.0f };
        float emm[] = {0.0f, 0.0f, 0.0f, 1.0f};
        
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifTree,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecTree,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineTree,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
        
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
	   	
    	gl.glBegin(GL2.GL_QUAD_STRIP);
        {
            double angleStep = 2*Math.PI/slices;
            for (int i = 0; i <= slices ; i++){//slices; i++) {
                double a0 = i * angleStep;
                
                double x0 = diameter*Math.cos(a0);
                double z0 = diameter*Math.sin(a0);
                
                float s =  i/(float)slices; 
                
                gl.glNormal3d(x0, 0, z0);
                gl.glTexCoord2d(s, 0); 
                gl.glVertex3d(x0, y1, z0);
                gl.glTexCoord2d(s, 1);
                gl.glVertex3d(x0, y2, z0);
            }

        }
        
        gl.glEnd();
        gl.glDisable(GL2.GL_TEXTURE_2D);
        
        // Materials and Color of Leaves
        float matAmbAndDifLeaves[] = {0.18f, 0.31f, 0.18f, 1.0f};
        float matSpecLeaves[] = { 0.1f, 0.1f, 0.1f, 1.0f };
        float matShineLeaves[] = { 5.0f };
        float emmLeaves[] = {0.0f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifLeaves, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecLeaves, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineLeaves, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmLeaves, 0);
        
        //Draw Spheres
        gl.glPushMatrix();
	        gl.glTranslated(0, height, 0);
	       	GLUT glu = new GLUT();
	        glu.glutSolidSphere(diameter_leaves, 40, 40);
        gl.glPopMatrix();
    }
}
