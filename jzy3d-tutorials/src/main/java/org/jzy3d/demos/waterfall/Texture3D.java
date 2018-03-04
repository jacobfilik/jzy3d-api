package org.jzy3d.demos.waterfall;

import java.nio.Buffer;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Quad;
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
    private Quad quad;
    
    public Texture3D(Buffer buffer, int[] shape) {
    	this.buffer = buffer;
    	this.shape = shape;
    	bbox = new BoundingBox3d(0,1,0,1,0,1);
    	quad = new Quad();
    	quad.add(new Point(new Coord3d(0, 0, 0)));
    	quad.add(new Point(new Coord3d(0, 1, 1)));
    	quad.add(new Point(new Coord3d(0, 1, 0)));
    	quad.add(new Point(new Coord3d(1, 1, 0)));
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
		doTransform(gl, glu, cam);
    	
    	float mvmatrix[] = new float[16];
    	float projmatrix[] = new float[16];
    	
    	String glGetString = gl.glGetString(GL.GL_VERSION);
    	String glGetString2 = gl.glGetString(GL2.GL_SHADING_LANGUAGE_VERSION);
    	gl.getGL2().glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
    	gl.getGL2().glGetFloatv(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);

    	int vertexShaderID = gl.getGL2().glCreateShader(GL2.GL_VERTEX_SHADER);
    	int fragmentShaderID = gl.getGL2().glCreateShader(GL2.GL_FRAGMENT_SHADER);
    	
    	String  vertex = "#version 130\n uniform mat4 modelViewMatrix; uniform mat4 projectionMatrix;in vec4 vt; out vec4 vVaryingColor; void main() { vVaryingColor=gl_Color; gl_Position=gl_ProjectionMatrix*gl_ModelViewMatrix*vt;}";
    	String  fragment = "#version 130\n in vec4 vVaryingColor;uniform sampler3D volumeTexture; out vec4 vFragColor; void main() { vec4 value = texture3D(volumeTexture,vec3(0.5,0.5,0.5)); vFragColor = vec4(255.0,0.5,0.5,255.0);}";
    	
    	gl.getGL2().glShaderSource(vertexShaderID, 1, new String[] {vertex} , null);
    	gl.getGL2().glCompileShader(vertexShaderID);
    	
    	 int[] compileStatus = new int[] { 0 };
         int[] logLength = new int[] { 0 };

         gl.getGL2().glGetShaderiv(vertexShaderID, GL2.GL_COMPILE_STATUS, compileStatus, 0);
         gl.getGL2().glGetShaderiv(vertexShaderID, GL2.GL_INFO_LOG_LENGTH, logLength, 0);
         
         int ERROR_BUFFER_SIZE = 8192;
 		byte[] errorBuffer = new byte[ERROR_BUFFER_SIZE]; 
 		int[] messageLength = new int[1]; 
 		gl.getGL2().glGetShaderInfoLog(vertexShaderID, ERROR_BUFFER_SIZE, messageLength, 0, errorBuffer, 0);
 		String t = new String(errorBuffer);
    	
    	gl.getGL2().glShaderSource(fragmentShaderID, 1, new String[] {fragment} , null);
    	gl.getGL2().glCompileShader(fragmentShaderID);
    	
    	compileStatus = new int[] { 0 };
        logLength = new int[] { 0 };

         gl.getGL2().glGetShaderiv(fragmentShaderID, GL2.GL_COMPILE_STATUS, compileStatus, 0);
         gl.getGL2().glGetShaderiv(fragmentShaderID, GL2.GL_INFO_LOG_LENGTH, logLength, 0);

 		errorBuffer = new byte[ERROR_BUFFER_SIZE]; 
 		messageLength = new int[1]; 
 		gl.getGL2().glGetShaderInfoLog(fragmentShaderID, ERROR_BUFFER_SIZE, messageLength, 0, errorBuffer, 0);
 		t = new String(errorBuffer);
    	
    	 int glProgram = gl.getGL2().glCreateProgram();
    	 gl.getGL2().glAttachShader(glProgram, vertexShaderID);
    	 gl.getGL2().glAttachShader(glProgram, fragmentShaderID);
    	 gl.getGL2().glLinkProgram(glProgram);
    	
    	 gl.getGL2().glDetachShader(glProgram, vertexShaderID);
    	 gl.getGL2().glDetachShader(glProgram, fragmentShaderID);
    		
    	 gl.getGL2().glDeleteShader(vertexShaderID);
    	 gl.getGL2().glDeleteShader(fragmentShaderID);

    	 gl.getGL2().glUseProgram(glProgram);
    	 
    	 
    	 int id = gl.getGL2().glGetUniformLocation(glProgram, "modelViewMatrix");
         gl.getGL2().glUniform4fv(id, 1, mvmatrix, 0);
         
         int idp = gl.getGL2().glGetUniformLocation(glProgram, "projectionMatrix");
         gl.getGL2().glUniform4fv(idp, 1, projmatrix, 0);
		
         gl.getGL2().glBegin(GL2.GL_QUADS);
         quad.setFaceDisplayed(true);
         quad.setPolygonOffsetFillEnable(true);
         quad.setColor(new Color(0,0,0));
         quad.draw(gl, glu, cam);
         gl.getGL2().glEnd();

		
//		if (!hasMountedOnce) {
//			badmount(gl);
//		}
//		
//		gl.getGL2().glDisable(GL.GL_BLEND);
		
//		super.draw(gl, glu, cam);
//		gl.getGL2().glEnable(GL.GL_BLEND);
		
		gl.getGL2().glUseProgramObjectARB(0);
	
		
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
