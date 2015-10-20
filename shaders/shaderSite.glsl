#version 130


in vec3 N;
in vec4 v;
 
 
void main()
{
  vec3 n,halfV, lightDir;
    float NdotL,NdotHV, dist;
   
    float att,spotEffect;
    vec3 dirToView = normalize(vec3(-v));

    vec4 diffuse;

    vec4 ambient, ambientGlobal;
    
    /* Compute the ambient and globalAmbient terms */
	ambient =  gl_LightSource[1].ambient * gl_FrontMaterial.ambient;
	ambientGlobal = gl_LightModel.ambient * gl_FrontMaterial.ambient;

    vec4 color = ambientGlobal;

    
    /* a fragment shader can't write a verying variable, hence we need
    a new variable to store the normalized interpolated normal */
    n = normalize(N);
     
    // Compute the ligt direction
    lightDir = vec3(gl_LightSource[1].position-v);
    dist = length(lightDir);

    /*halfway vec*/
    vec3 halfVector =  gl_LightSource[1].halfVector.xyz; 
     
     
    /* compute the distance to the light source to a varying variable*/
    dist = length(lightDir);
 
    /* compute the dot product between normal and ldir */
    NdotL = max(dot(n,normalize(lightDir)),0.0);
    diffuse = NdotL * gl_FrontMaterial.diffuse * gl_LightSource[1].diffuse;
 
    if (NdotL > 0.0) {

      
     
      spotEffect = max(0.0,dot(normalize(gl_LightSource[1].spotDirection), normalize(-lightDir)));
      if (spotEffect > cos(radians(gl_LightSource[1].spotCutoff))) {
            spotEffect = pow(spotEffect, gl_LightSource[1].spotExponent);
            att = spotEffect / (gl_LightSource[1].constantAttenuation +
                    gl_LightSource[1].linearAttenuation * dist +
                    gl_LightSource[1].quadraticAttenuation * dist * dist);
                 
            color += att * (diffuse * NdotL + ambient);
         
             
            halfV = normalize(halfVector);
            NdotHV = max(dot(n,halfV),0.0);
            color += att * gl_FrontMaterial.specular * gl_LightSource[1].specular * pow(NdotHV,gl_FrontMaterial.shininess);
        }
	
    }
 
    gl_FragColor = color;
}
