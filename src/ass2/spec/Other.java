package ass2.spec;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

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
	
	public void loadTextures(GL2 gl)
	{
		shaders = new int[2];
		 try 
		 {
			 shaders[0] = Shader.initShaders(gl,VERTEX_SHADER,FRAGMENT_SHADER_DAY);   	
			 shaders[1] = Shader.initShaders(gl,VERTEX_SHADER,FRAGMENT_SHADER_NIGHT); 
	     }
		 catch (Exception e) {
			 System.err.println("Error while loadn shader");
			 e.printStackTrace();
	         System.exit(1);
		 }
		 
		 shaderprogram = shaders[0];
	}
	
	public void switchShader()
	{
		useShaders = !useShaders;
	}
	
	
	public void draw(GL2 gl)
	{
		if(useShaders) gl.glUseProgram(shaderprogram);
		gl.glPushMatrix();
		gl.glTranslated(pos[0], pos[1] + 0.2, pos[2]);
		GLUT glut = new GLUT();
		
		
		//Material properties
		float matAmbAndDifAvatar[] = {1f, 0f, 0.0f, 1.0f};
        float matSpecAvatar[] = { 0.1f, 0.1f, 0.1f, 1.0f };
        float matShineAvatar[] = { 3.0f };
        float emmSun[] = {0.0f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emmSun,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDifAvatar,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpecAvatar,0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShineAvatar,0);
		
		glut.glutSolidTeapot(0.5f);	
		
		gl.glPopMatrix();
		if(useShaders) gl.glUseProgram(0);
	}

}
