#version 130



in vec3 N;
in vec4 v;

/* We are only taking into consideration light0 and assuming it is a point light */
void main (void) {	
   vec4 ambient, globalAmbient;
    
    /* Compute the ambient and globalAmbient terms */
   ambient =  gl_LightSource[1].ambient * gl_FrontMaterial.ambient;
   globalAmbient = gl_LightModel.ambient * gl_FrontMaterial.ambient;


   /* Color is global ambient, at least */
   vec4 color = globalAmbient + gl_FrontMaterial.emission;
   
	
   /* Diffuse calculations */
   
   vec3 normal, lightDir; 
   
   vec4 diffuse;
   float NdotL, dist;
   
   /* normal has been interpolated and may no longer be unit length so we need to normalise*/
   normal = normalize(N);
   
   
   /* normalize the light's direction. */
   lightDir = vec3(gl_LightSource[1].position - v);
   dist = length(lightDir);
   lightDir = normalize(lightDir);


    NdotL = max(dot(normal, lightDir), 0.0); 
   

    vec4 specular = vec4(0.0,0.0,0.0,1);
    float NdotHV;
    float NdotR;
    vec3 dirToView = normalize(vec3(-v));
    
    vec3 R = normalize(reflect(-lightDir,normal)); 
    vec3 H =  normalize(lightDir+dirToView); 

    float att, spotEffect = 0; 
   
    /* compute the specular term if NdotL is  larger than zero */
    
	if (NdotL > 0.0) {
		NdotR = max(dot(R,dirToView ),0.0);

		 /* Compute the diffuse term */
    		  diffuse = NdotL * gl_FrontMaterial.diffuse * gl_LightSource[1].diffuse;

		  spotEffect = dot(normalize(vec3(gl_LightSource[1].spotDirection)), normalize(-lightDir));
		  if (spotEffect > gl_LightSource[1].spotCosCutoff) {
		    spotEffect = pow(spotEffect, gl_LightSource[1].spotExponent);
		    att = spotEffect / (gl_LightSource[1].constantAttenuation +
					gl_LightSource[1].linearAttenuation * dist +
					gl_LightSource[1].quadraticAttenuation * dist * dist);
                 
		    
		
		    //Can use the halfVector instead of the reflection vector if you wish 
		    NdotHV = max(dot(normal, H),0.0);
		
		    specular = gl_FrontMaterial.specular * gl_LightSource[1].specular * pow(NdotHV,gl_FrontMaterial.shininess);
		    specular = clamp(specular,0,1);

		    color += att * (diffuse + ambient) * specular;
		    /* color = vec4(1.0,0.0,0.0,1.0);*/
		  }
		  else
		    {
		      color = vec4(gl_LightSource[1].spotDirection.x,gl_LightSource[1].spotDirection.y,gl_LightSource[1].spotDirection.z,1.0);
		    }
	    
	}
	
	

	/*gl_FragColor = gl_FrontMaterial.emission + globalAmbient + ambient + diffuse + specular;*/
	gl_FragColor = color;
	
   
	
}

