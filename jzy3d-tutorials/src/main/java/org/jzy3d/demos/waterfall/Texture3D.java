package org.jzy3d.demos.waterfall;

import java.nio.Buffer;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.glu.GLU;

public class Texture3D extends AbstractDrawable implements IGLBindedResource{

	/** The GL texture ID. */
    private int texID;
    private Buffer buffer;
    private int[] shape;
    private boolean mounted = false;
    
    public Texture3D(Buffer buffer, int[] shape) {
    	this.buffer = buffer;
    	this.shape = shape;
    	bbox = new BoundingBox3d(0,1,0,1,0,1);
    }
	
	@Override
	public void mount(GL gl) {
		if (!mounted) {
			bind(gl);
		}
		
	}

	@Override
	public boolean hasMountedOnce() {
		return mounted;
	}
	
	public void bind(final GL gl) throws GLException {
        validateTexID(gl, true);
        gl.glBindTexture(GL2.GL_TEXTURE_3D, texID);
        setTextureData(gl,buffer,shape);
    }
	
	public void setTextureData(final GL gl, Buffer buffer, int[] shape) {
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		gl.getGL2().glTexImage3D(GL2.GL_TEXTURE_3D, 0, GL.GL_LUMINANCE, shape[0], shape[1], shape[2], 0, GL2ES2.GL_LUMINANCE, GL.GL_BYTE, buffer);
	}
	
	private boolean validateTexID(final GL gl, final boolean throwException) {
        if( 0 == texID ) {
            if( null != gl ) {
                final int[] tmp = new int[1];
                gl.glGenTextures(1, tmp, 0);
                texID = tmp[0];
                if ( 0 == texID && throwException ) {
                    throw new GLException("Create texture ID invalid: texID "+texID+", glerr 0x"+Integer.toHexString(gl.glGetError()));
                }
            } else if ( throwException ) {
                throw new GLException("No GL context given, can't create texture ID");
            }
        }
        return 0 != texID;
    }

	@Override
	public void draw(GL gl, GLU glu, Camera cam) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyGeometryTransform(Transform transform) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateBounds() {
		bbox = new BoundingBox3d(0,1,0,1,0,1);
		
	}

}
