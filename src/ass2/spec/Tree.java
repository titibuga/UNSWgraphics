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
	//Texture file information
	private String TEX_0 = "textures/trunk_texture.jpg";
	private String TEX_1 = "textures/leaves_texture.jpg";
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
    
    public void loadTextures(GL2 gl)
    {
    	//Texture of the trunk
    	 myTextures[0] = new MyTexture(gl,TEX_0,TEX_F_0,true);
    	// Texture of Sphere
         myTextures[1] = new MyTexture(gl,TEX_1,TEX_F_1,true);
    }
   
    public void draw(GL2 gl, double height, double diameter, double diameter_leaves) {
    	int slices = 100;
    	double y1 = 0;
    	double y2 = height;
    	
       
    	gl.glActiveTexture(GL2.GL_TEXTURE0); 	
    	gl.glEnable(GL2.GL_TEXTURE_2D);
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());  
    	
    	// Materials and Color of Trees
        float matAmbAndDifTrunk[] = {0.3f, 0.16f, 0.15f, 1.0f};
        float matSpecTrunk[] = { 0.1f, 0.1f, 0.1f, 1.0f };
        float matShineTrunk[] = { 2.0f };
//        float emm[] = {0.0f, 0.0f, 0.0f, 1.0f};
        
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifTrunk,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecTrunk,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineTrunk,0);
//        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
        
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
        float matSpecLeaves[] = { 0.3f, 0.3f, 0.3f, 1.0f };
        float matShineLeaves[] = { 5.0f };
        float emmLeaves[] = {0.0f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifLeaves, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecLeaves, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineLeaves, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmLeaves, 0);
        
        
    	gl.glActiveTexture(GL2.GL_TEXTURE0); 	
    	gl.glEnable(GL2.GL_TEXTURE_2D);
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[1].getTextureId());
    	
    	//Draw Spheres
        gl.glPushMatrix();
	        gl.glTranslated(0, height, 0);
	        gl.glRotated(90, 1, 0, 0);
	        int stack = 20;
	        int slice = 20;
	       	uvSphere(gl, diameter_leaves, slice, stack, true);
        gl.glPopMatrix();
    }
    
	/**
	 * Draw a sphere with a given radius, number of slices, and number
	 * of stacks.  The number of slices is the number of lines of longitude
	 * (like the slices of an orange).  The number of stacks is the number
	 * of divisions perpendicular the axis; the lines of latitude are the
	 * dividing lines between stacks, so there are stacks-1 lines of latitude.
	 * The last parameter tells whether or not to generate texture
	 * coordinates for the sphere.  The texture wraps once around the sphere.
	 * The sphere is centered at (0,0,0), and its axis lies along the z-axis.
	 */
	public static void uvSphere(GL2 gl, double radius, int slices, int stacks, boolean makeTexCoords) {
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
			for (int i = 0; i <= slices; i++) {
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
				if (makeTexCoords)
					gl.glTexCoord2d(1.0/slices * i, 1.0/stacks * (j+1));
				gl.glVertex3d(radius*x2,radius*y2,radius*z2);
				gl.glNormal3d(x1,y1,z1);
				if (makeTexCoords)
					gl.glTexCoord2d(1.0/slices * i, 1.0/stacks * j);
				gl.glVertex3d(radius*x1,radius*y1,radius*z1);
			}
			gl.glEnd();
		}
	} 
}
