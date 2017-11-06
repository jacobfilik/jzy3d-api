package org.jzy3d.plot3d.rendering.textures;

import java.nio.ByteBuffer;

import com.jogamp.nativewindow.util.PixelFormat;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.GLPixelBuffer.GLPixelAttributes;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;

public class ColorMappedTexture extends SharedTexture {

	int iWidth = 16;
	int iHeight = 16;
	private ByteBuffer image;
	
	
	public ColorMappedTexture() {
		super();
		
	}
	
	public ColorMappedTexture(ByteBuffer buffer, int h, int w) {
		super();
		
    	iWidth = w;
    	iHeight = h;

         // Holds the RGBA buffer
         image = buffer;
		
		
	}
	
	@Override
	public void mount(GL gl) {
		GLProfile glp = gl.getGLProfile();
		TextureData td = makeTextureData(glp);
		texture = new Texture(gl, td);
		coords = texture.getImageTexCoords();
        halfWidth = texture.getWidth() / 2;
        halfHeight = texture.getHeight() / 2;
	}
	
	private TextureData makeTextureData(GLProfile glp) {
		final GLPixelAttributes glpa = new GLPixelAttributes(glp, PixelFormat.RGBA8888,false /* pack */);
        
        return new TextureData(glp, glpa.format,
                               iWidth,
                               iHeight,
                               0,
                               glpa.format,
                               glpa.type,
                               false,
                               false,
                               false,
                               image,
                               null);
	}

}
