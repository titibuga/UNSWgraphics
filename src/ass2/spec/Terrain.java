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
    private List<Other> myOthers;
    private List<Road> myRoads;
    private float[] mySunlight;
    private boolean wireframe;
	
	//Texture file information
	private String TEX_0 = "textures/grass_texture.jpg";
	private String TEX_F_0 = ".jpg";
	
	//Shader
    private static final String VERTEX_SHADER = "shaders/PhongVertex.glsl";
    private static final String FRAGMENT_SHADER_DAY = "shaders/PhongFragDir.glsl";
    private static final String FRAGMENT_SHADER_NIGHT = "shaders/PhongFragmentSpot.glsl";
    private int shaderprogram;
    private int[] shaders;
	
	//Texture data
	private MyTexture myTextures[] = new MyTexture[2];

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
        myOthers = new ArrayList<Other>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[3];
        wireframe = false;
        
    }
    
    public void switchWire()
    {
    	wireframe = !wireframe;
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
    
   public List<Other> others()
   {
	   return myOthers;
   }

    public List<Road> roads() {
        return myRoads;
    }

    public float[] getSunlight() {
        return mySunlight;
    }
    
    public void setNight(boolean b)
	{
		if(b) shaderprogram = shaders[1];
		else shaderprogram = shaders[0];
		
		for(Other o : this.others())
			o.setNight(b);
		for(Road r : this.roads())
			r.setNight(b);
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
        
        if(x < 0 || z < 0 ||
			       zInt2 >= this.size().height ||
			       xInt2 >= this.size().width)
        	return 0;      
       
        
        
        int bla = 0;
        double[][] p = new double [4][3];
        p[0][0] = xInt;
        p[0][1] = zInt;
        p[0][2] = getGridAltitude(xInt, zInt);
      //  System.out.println("Interp1 : x:"+p[bla][0]+" y:"+p[bla][2]+" z:"+p[bla][1]);
        p[1][0] = xInt2;
        p[1][1] = zInt;
        p[1][2] = getGridAltitude(xInt2, zInt);
        bla = 1;
       // System.out.println("Interp2 : x:"+p[bla][0]+" y:"+p[bla][2]+" z:"+p[bla][1]);
        p[2][0] = xInt;
        p[2][1] = zInt2;
        p[2][2] = getGridAltitude(xInt, zInt2);
        bla = 2;
        //System.out.println("Interp3 : x:"+p[bla][0]+" y:"+p[bla][2]+" z:"+p[bla][1]);
        p[3][0] = xInt2;
        p[3][1] = zInt2;
        p[3][2] = getGridAltitude(xInt2, zInt2);
        bla = 3;
        //System.out.println("Interp4 : x:"+p[bla][0]+" y:"+p[bla][2]+" z:"+p[bla][1]);
        
        double frac1 = x - xInt;
        double frac2 = z - zInt;
        
        //Simples cases first
        
        if(x - xInt == 0)
        {
        	return p[0][2]*(1-frac2) + p[2][2]*frac2;
        }
        else if(z - zInt == 0)
        {
        	return p[0][2]*(1-frac1) + p[1][2]*frac1;
        }
        
        //More complicated case
        
       // double distp0 = distSquare(x,z,p[0][0],p[0][1]);
        //double distp3 = distSquare(x,z,p[3][0],p[3][1]);
        
        double[] u1, u2, u3, t;
        
        t = new double[]{x,z};
        
        u1 = p[1];
        u2 = p[2];
        
        
        //In which triangle is our tree?
        if( left(u1,u2, t) ) u3 = p[0];
        else u3 = p[3];
        

        
        //The weight of ui will be the area Ai of the
        // triangle using all other vertices

        double A1 = unsarea2(u2, u3, t);
        double A2 = unsarea2(u1,u3, t);
        double A3 = unsarea2(u1,u2,t);
        double A = A1 + A2 + A3;
/*
        System.out.println("u1:("+u1[0]+","+u1[1]+","+u1[2]+") - w:"+A1/A);
        System.out.println("u2:("+u2[0]+","+u2[1]+","+u2[2]+") - w:"+A2/A);
        System.out.println("u3:("+u3[0]+","+u3[1]+","+u3[2]+") - w:"+A3/A);
        */
        
        altitude = u1[2]*(A1/A) + u2[2]*(A2/A) + u3[2]*(A3/A);
        //System.out.println("Result:"+altitude+" for x:"+x+" y:"+z);
        return altitude;
    }
    
    
    private double unsarea2(double[] a, double[] b, double[] c)
    {
    	return Math.abs(area2(a,b,c));
    }
    
    private double area2(double[] a, double[] b, double[] c)
    {
    	return ((b[0] - a[0])*(c[1] - a[1]) - (b[1] - a[1])*(c[0] - a[0]));
    }
    
    //true if c is on the left of a->b
    private boolean left(double[] a, double[] b, double[] c)
    {
    	return area2(a,b,c) > 0;
    	
    }
    
    private double distSquare(double x1,double y1,double x2,double y2)
    {
    	double dx = (x2 - x1);
    	double dy = (y2 - y1);
    	return dx*dx + dy+dy;
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
    
    
    public void addOther(double x, double z)
    {
    	double y = altitude(x, z);
        Other o = new Other(x, y, z);
        myOthers.add(o);
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
	
	public void loadTextures(GL2 gl)
	{
		// Texture ground
        myTextures[0] = new MyTexture(gl,TEX_0,TEX_F_0,true);
        
        //Shader
        shaders = new int[2];
		 try 
		 {
			 shaders[0] = Shader.initShaders(gl,VERTEX_SHADER,FRAGMENT_SHADER_DAY);   	
			 shaders[1] = Shader.initShaders(gl,VERTEX_SHADER,FRAGMENT_SHADER_NIGHT); 
	     }
		 catch (Exception e) {
			 System.err.println("Error while loading  shader");
			 e.printStackTrace();
	         System.exit(1);
		 }
		 int texUnitLoc = gl.glGetUniformLocation(shaders[0],"texUnit1");
		 gl.glUniform1i(texUnitLoc, 0);
		 texUnitLoc = gl.glGetUniformLocation(shaders[1],"texUnit1");
		 gl.glUniform1i(texUnitLoc, 0);
		 
		 shaderprogram = shaders[0];
        
        //Recursively call the load texture of other objects from the terrain
        for(Tree t : this.myTrees) t.loadTextures(gl);
        for(Road r : this.myRoads) r.loadTextures(gl);
        for(Other o : this.myOthers) o.loadTextures(gl);
	}
    
    public void draw(GL2 gl){
    	gl.glActiveTexture(GL2.GL_TEXTURE0); 	
    	gl.glEnable(GL2.GL_TEXTURE_2D);
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());  
    	
	    double[] v1 = new double[3];
		v1[0] = 1;
		v1[2] = -1;
	    double[] v2 = new double[3];
	    
	    //Shader
	    gl.glUseProgram(shaderprogram);
	    
    	// Materials and Color of terrain
    	float matAmbAndDifTerrain[] = {0.55f, 0.65f, 0.31f, 1.0f};
    	float matAmbAndDifTerrainBack[] = {0.0f, 0.0f, 0.0f, 1.0f};
        float matSpecTerrain[] = { 0.1f, 0.1f, 0.1f, 1.0f };
        float matShineTerrain[] = { 3.0f };
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifTerrain,0);
        gl.glMaterialfv(GL2.GL_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifTerrainBack,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecTerrain,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineTerrain,0);
	    // Draw terrain
	    for(int i = 0; i < size().width-1; i++) {
	    	for(int j = 0; j < size().height-1; j++) {
	    		double[] h = new double[]{ getGridAltitude(i, j),
	    								   getGridAltitude(i, j+1),
	    								   getGridAltitude(i+1, j),
	    								   getGridAltitude(i+1, j+1),};
		    	
	    		gl.glBegin(GL.GL_TRIANGLES);
		    		v1[1] = h[2] - h[1];
		    		
		    		v2[0] = 0;
		    		v2[2] = -1;
		    		v2[1] = h[0] - h[1];
		    		
		    		double[] n1 = crossProduct(v1,v2);
		    		
		    		gl.glNormal3d(n1[0], n1[1], n1[2]);
		    		gl.glTexCoord2d(1, 0); 
			    	gl.glVertex3d(i,h[0],j); // P0
		    		gl.glTexCoord2d(1, 1); 
		    		gl.glVertex3d(i,h[1],j+1);// P1
		    		gl.glTexCoord2d(0, 0); 
		    		gl.glVertex3d(i+1,h[2],j); // P2
			    	
			    	v2[0] = 1;
		    		v2[2] = 0;
		    		v2[1] = h[3] - h[1];
		    		n1 = crossProduct(v2,v1);
		    		gl.glNormal3d(n1[0], n1[1], n1[2]);
		    		
		    		gl.glTexCoord2d(1, 1); 
			    	gl.glVertex3d(i,h[1],j+1);// P1
		    		gl.glTexCoord2d(0, 1); 
		    		gl.glVertex3d(i+1,h[3],j+1); // P3
		    		gl.glTexCoord2d(0, 0); 
		    		gl.glVertex3d(i+1,h[2],j); // P2


		    	gl.glEnd();
	    	}
	    }
	    gl.glUseProgram(0);
	    gl.glDisable(GL2.GL_TEXTURE_2D);
	    gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_FALSE);
	    // Draw grid of terrain
	    if(wireframe){
		    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
	    	float matAmbAndDifTerrainG[] = {0.0f, 0.0f, 0.0f, 1.0f};
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifTerrainG,0);
		    for(int i = 0; i < size().width-1; i++) {
		    	for(int j = 0; j < size().height-1; j++) {
		    		double[] h = new double[]{ getGridAltitude(i, j),
							   getGridAltitude(i, j+1),
							   getGridAltitude(i+1, j),
							   getGridAltitude(i+1, j+1),};
		    		gl.glLineWidth(3.0f);
		        	gl.glBegin(GL.GL_TRIANGLES);
		    	    	gl.glVertex3d(i,h[0],j); // P0
		    	    	gl.glVertex3d(i,h[1],j+1);// P1
		    	    	gl.glVertex3d(i+1,h[2],j); // P2
		    	    	
		    	    	gl.glVertex3d(i,h[1],j+1);// P1
		    	    	gl.glVertex3d(i+1,h[3],j+1); // P3
		    	    	gl.glVertex3d(i+1,h[2],j); // P2
		        	gl.glEnd();
		        	gl.glLineWidth(1.0f);
		    	}
		    }
		    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	    }
    }
}
