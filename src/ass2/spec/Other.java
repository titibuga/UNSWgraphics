package ass2.spec;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;

import ass2.spec.Shader;

public class Other {
	
	double pos[];
	private static final String VERTEX_SHADER = "shaders/PhongVertex.glsl";
    private static final String FRAGMENT_SHADER_DAY = "shaders/PhongFragDir.glsl";
    private static final String FRAGMENT_SHADER_NIGHT = "shaders/PhongFragmentSpot.glsl";
    private int shaderprogram;
    private int[] shaders;
    private boolean useShaders;
    private int[] bufferIds; //VBO
    
    
    
    
    
    private float[] normals = 
    	{
    		//F1
    		0,0,-1,0,0,-1,0,0,-1,0,0,-1,0,0,-1,0,0,-1,
    		//F2
    		0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,
    		//F3
    		-1,0,0,-1,0,0,-1,0,0,-1,0,0,-1,0,0,-1,0,0,
    		//F4
    		1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,
    		//F5
    		0,-1,0,0,-1,0,0,-1,0,0,-1,0,0,-1,0,0,-1,0,
    		///F6
    		0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,
    	};
    
    
    private float[] positions = 
    	{

    		//F1
    		0,0,0, 0,1,0, 1,1,0, 
    		0,0,0, 1,1,0, 1,0,0,

    		//F2
    		1,0,1, 1,1,1, 0,1,1,
    		1,0,1, 0,1,1, 0,0,1,

    		//F3

    		0,0,1, 0,1,1, 0,1,0,
    		0,0,1, 0,1,0, 0,0,0,

    		//F4

    		1,0,0, 1,1,0, 1,1,1,
    		1,0,0, 1,1,1, 1,0,1, 

    		//F5

    		0,0,1, 0,0,0, 1,0,0,
    		0,0,1, 1,0,0, 1,0,1,

    		//F6

    		0,1,0, 0,1,1, 1,1,1,
    		0,1,0, 1,1,1, 1,1,0,
    		
    	};
    
    
    private float texCoords[] =  
		{	
    		0,0, 1,0, 1,1, 
    		0,0,1,1,0,1,
    		
    		0,0, 1,0, 1,1, 
    		0,0,1,1,0,1,
    		
    		0,0, 1,0, 1,1, 
    		0,0,1,1,0,1,
    		
    		0,0, 1,0, 1,1, 
    		0,0,1,1,0,1,
    		
    		0,0, 1,0, 1,1, 
    		0,0,1,1,0,1,
    		
    		0,0, 1,0, 1,1, 
    		0,0,1,1,0,1,

		};
    
    
  //These are not vertex buffer objects, they are just java containers
  	private FloatBuffer  posData= Buffers.newDirectFloatBuffer(positions);
  	private FloatBuffer normalData = Buffers.newDirectFloatBuffer(normals);
  	private FloatBuffer texData = Buffers.newDirectFloatBuffer(texCoords);
    
    //Texture file information
  	private String TEX_0 = "src/ass2/spec/monster_texture.jpg";
  	private String TEX_F_0 = ".jpg";
  	
  	//Texture data
  	private MyTexture myTextures[] = new MyTexture[1];
	
	public Other(double x, double y, double z)
	{
		pos = new double[]{x,y,z};
		useShaders = true;
	}
	
	
	public void setNight(boolean b)
	{
		if(b) shaderprogram = shaders[1];
		else shaderprogram = shaders[0];
	}
	
	private void setUpvbo(GL2 gl)
	{
		bufferIds = new int[1];
		
		//Generate 1 VBO buffer and get its ID
        gl.glGenBuffers(1,bufferIds,0);
       
   	 //This buffer is now the current array buffer
        //array buffers hold vertex attribute data
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
     
        //This is just setting aside enough empty space
        //for all our data
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,    //Type of buffer  
       	        positions.length * Float.SIZE +  normals.length* Float.SIZE + texCoords.length* Float.SIZE, //size needed
       	        null,    //We are not actually loading data here yet
       	        GL2.GL_STATIC_DRAW); //We expect once we load this data we will not modify it


        //Actually load the positions data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, //From byte offset 0
       		 positions.length*Float.SIZE,posData);

        //Actually load the normal data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
       		 positions.length*Float.SIZE,  //Load after the position data
       		 normals.length*Float.SIZE,normalData);
        
        //Actually load the texcoord data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
       		 positions.length*Float.SIZE+normals.length*Float.SIZE,  //Load after the position data
       		 texCoords.length*Float.SIZE,texData);
        
      
  	   
	}
	
	
	public void loadTextures(GL2 gl)
	{
		setUpvbo(gl);
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
		 myTextures[0] = new MyTexture(gl,TEX_0,TEX_F_0,true);
	}
	
	public void switchShader()
	{
		useShaders = !useShaders;
	}
	
	
	public void draw(GL2 gl)
	{
		if(useShaders) gl.glUseProgram(shaderprogram);
		gl.glPushMatrix();
		gl.glTranslated(pos[0], pos[1], pos[2]);
		GLUT glut = new GLUT();
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
		
		/*int vertexPosLoc = gl.glGetAttribLocation(shaderprogram,"vpos");
		int vertexNLoc = gl.glGetAttribLocation(shaderprogram,"vNormal");
		gl.glEnableVertexAttribArray(vertexPosLoc);	
		gl.glVertexAttribPointer(vertexPosLoc,3, GL.GL_FLOAT, false,0, 0); //last num is the offset
		gl.glVertexAttribPointer(vertexNLoc,3, GL.GL_FLOAT, false,0, positions.length); */	
		
		  gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
	        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
	        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	        
	        // Specify locations for the co-ordinates and color arrays.
	        
	  	   gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0); //last num is the offset
	  	   gl.glNormalPointer(GL.GL_FLOAT, 0, (long)positions.length*Float.SIZE);
	  	   gl.glTexCoordPointer(3,GL.GL_FLOAT, 0, (long)positions.length*Float.SIZE + normals.length*Float.SIZE);
		
		gl.glActiveTexture(GL2.GL_TEXTURE0); 	
    	gl.glEnable(GL2.GL_TEXTURE_2D);
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());  
	
		
		//Material properties
		float matAmbAndDifAvatar[] = {1f, 0f, 0.0f, 1.0f};
        float matSpecAvatar[] = { 0.1f, 0.1f, 0.1f, 1.0f };
        float matShineAvatar[] = { 3.0f };
        float emmSun[] = {0.0f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmSun,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifAvatar,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecAvatar,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineAvatar,0);
		
		//glut.glutSolidTeapot(0.5f);	
        
        //gl.glScaled(0.5, 0.5, 0);
        /*
        for(int i = 0; i < 4; i++)
        {
        	
        	drawOneFace(gl);
        	
        	gl.glRotated(90, 0, 1, 0);
        	gl.glTranslated(-1,0,0);

        }*/
        gl.glDrawArrays(GL2.GL_TRIANGLES,0,positions.length/3);
        
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        
       
        
        
		
		gl.glPopMatrix();
		if(useShaders) gl.glUseProgram(0);
	}
	
	private void drawOneFace(GL2 gl)
	{
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glNormal3d(0, 0, -1);
		//Triangles
		gl.glTexCoord2d(0, 0); 
		gl.glVertex3d(0, 0, 0);
		gl.glTexCoord2d(0, 1); 
		gl.glVertex3d(0, 1, 0);
		gl.glTexCoord2d(1, 1); 
		gl.glVertex3d(1,1,0);
		
		gl.glTexCoord2d(0, 0); 
		gl.glVertex3d(0, 0, 0);
		gl.glTexCoord2d(1, 1); 
		gl.glVertex3d(1, 1, 0);
		gl.glTexCoord2d(1, 0); 
		gl.glVertex3d(1,0,0);
		gl.glEnd();
		
	}

}
