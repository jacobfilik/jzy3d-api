package org.jzy3d.plot3d.rendering.textures;

public class SimpleAlphaMapper implements IAlphaMapper {

	private float min;
	private float max;
	
	public SimpleAlphaMapper(float min, float max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public float getAlpha(float value) {
		
		if (value < min) return 0;
		if (value > max) return 1;
		
		return value/(max-min);
	}

}
