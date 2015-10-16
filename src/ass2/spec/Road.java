package ass2.spec;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {

    private List<Double> myPoints;
    private double myWidth;
	
	//Texture file information
	private String TEX_0 = "src/ass2/spec/road_texture.jpg";
	private String TEX_F_0 = ".jpg";
	
	//Texture data
	private MyTexture myTextures[] = new MyTexture[1];
	
    
    /** 
     * Create a new road starting at the specified point
     */
    public Road(double width, double x0, double y0) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        myPoints.add(x0);
        myPoints.add(y0);
    }

    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(double width, double[] spine) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        for (int i = 0; i < spine.length; i++) {
            myPoints.add(spine[i]);
        }
    }

    /**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return myWidth;
    }

    /**
     * Add a new segment of road, beginning at the last point added and ending at (x3, y3).
     * (x1, y1) and (x2, y2) are interpolated as bezier control points.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void addSegment(double x1, double y1, double x2, double y2, double x3, double y3) {
        myPoints.add(x1);
        myPoints.add(y1);
        myPoints.add(x2);
        myPoints.add(y2);
        myPoints.add(x3);
        myPoints.add(y3);        
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return myPoints.size() / 6;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public double[] controlPoint(int i) {
        double[] p = new double[2];
        p[0] = myPoints.get(i*2);
        p[1] = myPoints.get(i*2+1);
        return p;
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public double[] point(double t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 6;
        
        double x0 = myPoints.get(i++);
        double y0 = myPoints.get(i++);
        double x1 = myPoints.get(i++);
        double y1 = myPoints.get(i++);
        double x2 = myPoints.get(i++);
        double y2 = myPoints.get(i++);
        double x3 = myPoints.get(i++);
        double y3 = myPoints.get(i++);
        
        double[] p = new double[2];

        p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
        p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;        
        
        return p;
    }
    
    /**
     * 
     * Return the 2D tangent vector of the Bezier curve at instant t
     * 
     * @param t
     * @return
     */
   
    public double[] tangent(double t)
    {
    	double[] tg = new double[3];
    	
    	
    	int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 6;
        
        double x0 = myPoints.get(i++);
        double y0 = myPoints.get(i++);
        double x1 = myPoints.get(i++);
        double y1 = myPoints.get(i++);
        double x2 = myPoints.get(i++);
        double y2 = myPoints.get(i++);
        double x3 = myPoints.get(i++);
        double y3 = myPoints.get(i++);
        
       
        tg[0] = 3*( b2(0, t) *(x1-x0) + b2(1, t) *(x2 - x1) + b2(2, t) *(x3-x2)) ;
        tg[1] = 3*( b2(0, t) *(y1-y0) + b2(1, t) *(y2 - y1) + b2(2, t) *(y3-y2));  
       
    
    	
    	return tg;	
    }
    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private double b(int i, double t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }
    
    /**
     * Calculate the coeficients for the bezier tangent
     * 
     * @param i
     * @param t
     * @return
     */
    
    private double b2(int i, double t)
    {
    	 switch(i) {
         
         case 0:
             return (1-t) * (1-t);

         case 1:
             return 2 * (1-t) * t;
             
         case 2:
             return t * t;

         }
         
         // this should never happen
         throw new IllegalArgumentException("" + i);
    }
    
   
    public void draw(GL2 gl, double h, double step)
    {
    	double i = 0;
//    	gl.glColor3f(0,0,0);
    	
        float matAmbAndDif[] = {0.5f, 0.5f, 0.5f, 1.0f};
        float matSpec[] = { 0.4f, 0.4f, 0.4f, 0.2f };
        float matShine[] = { 0.0f };
        float emm[] = {0.0f, 0.0f, 0.0f, 1.0f};
        // Material properties of sphere.
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpec,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShine,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
        
        // Texture road
        myTextures[0] = new MyTexture(gl,TEX_0,TEX_F_0,true);
    	gl.glActiveTexture(GL2.GL_TEXTURE0); 	
    	gl.glEnable(GL2.GL_TEXTURE_2D);
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());
    	
    	gl.glBegin(GL2.GL_TRIANGLE_STRIP);
    	
    	while(i < this.size())
    	{
    		
    		double[] p = this.point(i);
    		double[] tg2d = this.tangent(i);
    		double[] normal2d = new double[]{-tg2d[1],tg2d[0]};
    		
    		//normalize the normal
    		
    		double norm = normal2d[0]*normal2d[0] + normal2d[1]*normal2d[1];
    		norm = Math.sqrt(norm);
    		normal2d[0] /= norm;
    		normal2d[1] /= norm;
    		
    		// Draw the points on the plane with y = h
    		
    		//Order matters! CCW order!
    		
    		//TODO: Remove epsilon. For now it is just for it not to be so
    		// near the ground 
    		
    		double eps = 0.001;
    		double w = this.width()/2;
    	
    		gl.glTexCoord2d(-w*normal2d[0]+p[0], -w*normal2d[1] + p[1]); 
    		gl.glVertex3d(-w*normal2d[0]+p[0], h + eps, -w*normal2d[1] + p[1]);
    		gl.glTexCoord2d(w*normal2d[0]+p[0], w*normal2d[1] + p[1]); 
    		gl.glVertex3d(w*normal2d[0]+p[0], h + eps, w*normal2d[1] + p[1]);
    		
    		
    		
    		
    		//Increment
    		i += step;
    	}
    	
    	gl.glEnd();
    	
    	
    }


}
