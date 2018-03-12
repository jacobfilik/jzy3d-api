#version 130

in vec4 vVaryingColor;
uniform sampler3D volumeTexture;
uniform vec3 eye;
out vec4 vFragColor;


vec4 UnderCompositing(vec4 src, vec4 dst) {
  vec4 result = dst;
  result.rgb   -= src.rgb * (1.0-dst.a)*src.a;
  result.a     += src.a   * (1.0-dst.a);
  return result;
}

void main() {
//	if (!gl_FrontFacing) {
//		vFragColor = vec4(0.0,0.0,0.0,0.0);
//		return;
//	}
	vec4 test = vVaryingColor;
	vec4 value = texture3D(volumeTexture,test.xyz);
	value = 1-value;
	value.a = 1-value.r;
//	if (value.a > 0.1) {
//		value.a = 0.01;
//	}

		for (int i = 1; i < 200; i++) {
			vec3 tmp = test.xyz;
			tmp = tmp - eye*0.005*i;
			if (tmp.x > 1 || tmp.y > 1 || tmp.z > 1 || tmp.x < 0 || tmp.y < 0 || tmp.z < 0) {
				break;
			}
			vec4 v = texture3D(volumeTexture,tmp);
			v = 1-v;
			v.a = 1-v.r;
						if (v.a > 0.1) {
								v.a = 0.01;
							}


			if (v.r < 0.91 && v.r > 0.905) {
				v.a=0.5;
				v.b = 0;
				v.g = 0;
//				vFragColor = v;
//				return;
			}

//			if (v.a > 0.1) {
//					v.a = 0.01;
//				}
//			if (i ==5) {
//				value = v;
//			}
			value = UnderCompositing(v,value);
		}


		vFragColor = value;

}


