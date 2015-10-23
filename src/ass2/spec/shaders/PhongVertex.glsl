#version 120

varying vec3 N; 
varying vec4 v;

//varying vec3 vpos;
//varying vec3 vNormal;	

varying vec2 texCoordV; 


void main (void) {
  //vec4 vp = (vpos.x, vpos.y, vpos.z, 1);
  // vec4 np = (vNormal.x, vNormal.y, vNormal.z, 0);	
  v = gl_ModelViewMatrix * gl_Vertex;
  N = vec3(normalize(gl_NormalMatrix * normalize(gl_Normal)));
  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
  
  texCoordV = vec2(gl_MultiTexCoord0);
}

