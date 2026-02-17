#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;

uniform float Intensity;

float contrast(float x,float a) {
	return clamp(a*(x-0.5)+0.5,0.,1.);
}

void main(){
	vec4 diffuseColor = texture2D(DiffuseSampler, texCoord);

	float avg = (diffuseColor.r+diffuseColor.g+diffuseColor.b)/3.;
	float red = 240./255.;
	vec4 desatColor = vec4(avg,avg*red,avg*red,diffuseColor.a);
	vec4 mixColor = mix(diffuseColor, desatColor, Intensity*0.95);
	vec4 outColor = vec4(contrast(mixColor.r,Intensity+1.),contrast(mixColor.g,Intensity+1.),contrast(mixColor.b,Intensity+1.),mixColor.a);

	gl_FragColor = outColor;
}
