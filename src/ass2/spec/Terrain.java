package ass2.spec;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private float[] mySunlight;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[3];
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
    	//TODO: CHANGE THIS SHIT (minus)
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * 
     * @param x
     * @param z
     * @return
     */
    public double altitude(double x, double z) {
        double altitude = 0;
        int xInt = (int) x;
        int zInt = (int) z;
        int xInt2 = (int) x;
        int zInt2 = (int) z;
        if (x - xInt != 0){
        	xInt2++;
        }
        if (z - zInt != 0){
        	zInt2++;
        }
        double[][] p = new double [4][3];
        p[0][0] = xInt;
        p[0][1] = zInt;
        p[0][2] = getGridAltitude(xInt, zInt);
        p[1][0] = xInt2;
        p[1][1] = zInt;
        p[1][2] = getGridAltitude(xInt2, zInt);
        p[2][0] = xInt;
        p[2][1] = zInt2;
        p[2][2] = getGridAltitude(xInt, zInt2);
        p[3][0] = xInt2;
        p[3][1] = zInt2;
        p[3][2] = getGridAltitude(xInt2, zInt2);

        double frac1 = x - xInt;
        double frac2 = z - zInt;
        altitude = (p[1][2]*frac1 + p[0][2]*(1-frac1))*(1-frac2) + 
        			(p[3][2]*frac1 + p[2][2]*(1-frac1))*frac2;
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        myTrees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        myRoads.add(road);        
    }
    
	private double[] crossProduct(double[] a, double[] b)
	{
		double[] result = new double[3];
		result[0] = (b[2]*a[1] - a[2]*b[1]);
		result[1] = (b[0]*a[2] - a[0]*b[2]);
		result[2] = (b[1]*a[0] - a[1]*b[0]);
		return result;
	}
    
    public void draw(GL2 gl){
	    double[] v1 = new double[3];
		v1[0] = 1;
		v1[2] = -1;
	    double[] v2 = new double[3];
    	// Materials and Color of terrain
    	float matAmbAndDifTerrain[] = {1.0f, 1.0f, 0.0f, 1.0f};
        float matSpecTerrain[] = { 0.0f, 0.0f, 0.0f, 0.2f };
        float matShineTerrain[] = { 0.0f };
        float emmTerrain[] = {0.0f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifTerrain,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecTerrain,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineTerrain,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmTerrain,0);
	    // Draw terrain
	    for(int i = 0; i < size().width-1; i++) {
	    	for(int j = 0; j < size().height-1; j++) {
	    		double[] h = new double[]{ getGridAltitude(i, j),
	    								   getGridAltitude(i, j+1),
	    								   getGridAltitude(i+1, j),
	    								   getGridAltitude(i+1, j+1),};
	    		gl.glColor3f(0, 1, 0);
	    		gl.glBegin(GL.GL_TRIANGLES);
		    		v1[1] = h[2] - h[1];
		    		
		    		v2[0] = 0;
		    		v2[2] = -1;
		    		v2[1] = h[0] - h[1];
		    		
		    		double[] n1 = crossProduct(v1,v2);
		    		
		    		gl.glNormal3d(n1[0], n1[1], n1[2]);
		    		gl.glVertex3d(i+1,h[2],j); // P2
		    		gl.glVertex3d(i,h[1],j+1);// P1
			    	gl.glVertex3d(i,h[0],j); // P0
			    	
			    	v2[0] = 1;
		    		v2[2] = 0;
		    		v2[1] = h[3] - h[1];
		    		n1 = crossProduct(v2,v1);
		    		gl.glNormal3d(n1[0], n1[1], n1[2]);
		    		
		    		gl.glVertex3d(i+1,h[2],j); // P2
		    		gl.glVertex3d(i+1,h[3],j+1); // P3
			    	gl.glVertex3d(i,h[1],j+1);// P1
			    	
			    	
		    	gl.glEnd();
		    	
		    	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		    	gl.glBegin(GL.GL_TRIANGLES);

			    	gl.glVertex3d(i,h[0],j); // P0
			    	gl.glVertex3d(i,h[1],j+1);// P1
			    	gl.glVertex3d(i+1,h[2],j); // P2
			    	
			    	gl.glVertex3d(i,h[1],j+1);// P1
			    	gl.glVertex3d(i+1,h[3],j+1); // P3
			    	gl.glVertex3d(i+1,h[2],j); // P2
			    	
		    	gl.glEnd();
		    	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	    	}
	    }
    }
}
