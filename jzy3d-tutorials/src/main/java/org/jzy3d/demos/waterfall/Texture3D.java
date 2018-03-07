package org.jzy3d.demos.waterfall;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Quad;
import org.jzy3d.plot3d.primitives.vbo.buffers.FloatVBO;
import org.jzy3d.plot3d.primitives.vbo.builders.VBOBuilder;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.glu.GLU;

public class Texture3D extends AbstractDrawable implements IGLBindedResource{

	/** The GL texture ID. */
    private int texID;
    private Buffer buffer;
    private int[] shape;
    private boolean mounted = false;
    private Quad quad;
    private DrawableVBO shapeVBO;
    
    public Texture3D(Buffer buffer, int[] shape) {
    	this.buffer = buffer;
    	buffer.rewind();
    	this.shape = shape;
    	bbox = new BoundingBox3d(0,1,0,1,0,1);
    	quad = new Quad();
    	quad.add(new Point(new Coord3d(0, 0, 0)));
    	quad.add(new Point(new Coord3d(0, 1, 1)));
    	quad.add(new Point(new Coord3d(0, 1, 0)));
    	quad.add(new Point(new Coord3d(1, 1, 0)));
    	shapeVBO = buildShape();
    }
	
	@Override
	public void mount(GL gl) {
		if (!mounted) {
			shapeVBO.mount(gl);
			bind(gl);
			mounted = true;
		}
		
	}

	@Override
	public boolean hasMountedOnce() {
		return mounted;
	}
	
	public void bind(final GL gl) throws GLException {
		gl.glEnable(GL2.GL_TEXTURE_3D);
        validateTexID(gl, true);
        gl.glBindTexture(GL2.GL_TEXTURE_3D, texID);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MAG_FILTER,
                GL.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MIN_FILTER,
                GL.GL_LINEAR);
        setTextureData(gl,buffer,shape);
    }
	
	public void setTextureData(final GL gl, Buffer buffer, int[] shape) {
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		gl.getGL2().glTexImage3D(GL2.GL_TEXTURE_3D, 0, GL.GL_RGB, shape[2], shape[1], shape[0], 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, buffer);
//		gl.getGL2().glTexSubImage3D(GL2.GL_TEXTURE_3D,0,0, 0,0, shape[0], shape[1], shape[2], GL2ES2.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
//		gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
//		gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
//		gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);
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
    	
    	Coord3d eye = cam.getEye();
    	
    	String glGetString = gl.glGetString(GL.GL_VERSION);
    	String glGetString2 = gl.glGetString(GL2.GL_SHADING_LANGUAGE_VERSION);
    	gl.getGL2().glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
    	gl.getGL2().glGetFloatv(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);

    	int vertexShaderID = gl.getGL2().glCreateShader(GL2.GL_VERTEX_SHADER);
    	int fragmentShaderID = gl.getGL2().glCreateShader(GL2.GL_FRAGMENT_SHADER);
    	
    	String  vertex = "#version 130\n uniform mat4 modelViewMatrix; uniform mat4 projectionMatrix;in vec4 vt; out vec4 vVaryingColor; void main() { vVaryingColor=gl_Color; gl_Position=gl_ProjectionMatrix*gl_ModelViewMatrix*vt;}";
//    	String  fragment = "#version 130\n in vec4 vVaryingColor;uniform sampler3D volumeTexture; out vec4 vFragColor; void main() { vec4 value = texture3D(volumeTexture,vVaryingColor); vFragColor = vec4(255.0,0.5,0.5,255.0);}";
    	String  fragment = "#version 130\n in vec4 vVaryingColor;uniform sampler3D volumeTexture; out vec4 vFragColor; void main() { vec4 test = vVaryingColor;vec4 value = texture3D(volumeTexture,test.xyz); value = 1-value;value.a = 1-value.r; vFragColor = value;}";
    
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
         
       gl.glEnable(GL2.GL_BLEND);
          gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
          gl.getGL2().glPolygonMode(GL.GL_FRONT, GL2GL3.GL_FILL);
		
         shapeVBO.draw(gl, glu, cam);
         
//         gl.getGL2().glBegin(GL2.GL_QUADS);
//         quad.setFaceDisplayed(true);
//         quad.setPolygonOffsetFillEnable(true);
//         quad.setColor(new Color(0,0,0));
//         quad.draw(gl, glu, cam);
//         gl.getGL2().glEnd();

		
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
	
	
	public static DrawableVBO buildShape() {
		
		DrawableVBO v =  new DrawableVBO(new CubeVBO(0,1,0,1,0,1)) {
			
			boolean disposed = false;
			
			@Override
		    public void draw(GL gl, GLU glu, Camera cam) {
				
				doTransform(gl, glu, cam);
		    	
//		    	float mvmatrix[] = new float[16];
//		    	float projmatrix[] = new float[16];
//		    	
//		    	String glGetString = gl.glGetString(GL.GL_VERSION);
//		    	String glGetString2 = gl.glGetString(GL2.GL_SHADING_LANGUAGE_VERSION);
//		    	gl.getGL2().glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
//		    	gl.getGL2().glGetFloatv(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
//
//		    	int vertexShaderID = gl.getGL2().glCreateShader(GL2.GL_VERTEX_SHADER);
//		    	int fragmentShaderID = gl.getGL2().glCreateShader(GL2.GL_FRAGMENT_SHADER);
//		    	
//		    	String  vertex = "#version 130\n uniform mat4 modelViewMatrix; uniform mat4 projectionMatrix;in vec4 vt; out vec4 vVaryingColor; void main() { vVaryingColor=gl_Color; gl_Position=gl_ProjectionMatrix*gl_ModelViewMatrix*vt;}";
////		    	String  fragment = "#version 130\n in vec4 vVaryingColor; out vec4 vFragColor; void main() { if (vVaryingColor.r < 0.9 && vVaryingColor.g < 0.9)  {vFragColor =vec4(1,1,1,1); }else {vFragColor=vVaryingColor;}}";
////		    	String  fragment = "#version 130\n in vec4 vVaryingColor; out vec4 vFragColor; void main() {vFragColor=vVaryingColor}";
//		    	String  fragment = "#version 130\n in vec4 vVaryingColor;uniform sampler3D volumeTexture; out vec4 vFragColor; void main() { vec4 test = vVaryingColor; test.b = 0.3;vec4 value = texture3D(volumeTexture,vec3(test)); value.a = 1; vFragColor = value;}";
//		    	
//		    	gl.getGL2().glShaderSource(vertexShaderID, 1, new String[] {vertex} , null);
//		    	gl.getGL2().glCompileShader(vertexShaderID);
//		    	
//		    	 int[] compileStatus = new int[] { 0 };
//		         int[] logLength = new int[] { 0 };
//
//		         gl.getGL2().glGetShaderiv(vertexShaderID, GL2.GL_COMPILE_STATUS, compileStatus, 0);
//		         gl.getGL2().glGetShaderiv(vertexShaderID, GL2.GL_INFO_LOG_LENGTH, logLength, 0);
//		         
//		         int ERROR_BUFFER_SIZE = 8192;
//		 		byte[] errorBuffer = new byte[ERROR_BUFFER_SIZE]; 
//		 		int[] messageLength = new int[1]; 
//		 		gl.getGL2().glGetShaderInfoLog(vertexShaderID, ERROR_BUFFER_SIZE, messageLength, 0, errorBuffer, 0);
//		 		String t = new String(errorBuffer);
//		    	
//		    	gl.getGL2().glShaderSource(fragmentShaderID, 1, new String[] {fragment} , null);
//		    	gl.getGL2().glCompileShader(fragmentShaderID);
//		    	
//		    	compileStatus = new int[] { 0 };
//		        logLength = new int[] { 0 };
//
//		         gl.getGL2().glGetShaderiv(fragmentShaderID, GL2.GL_COMPILE_STATUS, compileStatus, 0);
//		         gl.getGL2().glGetShaderiv(fragmentShaderID, GL2.GL_INFO_LOG_LENGTH, logLength, 0);
//
//		 		errorBuffer = new byte[ERROR_BUFFER_SIZE]; 
//		 		messageLength = new int[1]; 
//		 		gl.getGL2().glGetShaderInfoLog(fragmentShaderID, ERROR_BUFFER_SIZE, messageLength, 0, errorBuffer, 0);
//		 		t = new String(errorBuffer);
//		    	
//		    	 int glProgram = gl.getGL2().glCreateProgram();
//		    	 gl.getGL2().glAttachShader(glProgram, vertexShaderID);
//		    	 gl.getGL2().glAttachShader(glProgram, fragmentShaderID);
//		    	 gl.getGL2().glLinkProgram(glProgram);
//		    	
//		    	 gl.getGL2().glDetachShader(glProgram, vertexShaderID);
//		    	 gl.getGL2().glDetachShader(glProgram, fragmentShaderID);
//		    		
//		    	 gl.getGL2().glDeleteShader(vertexShaderID);
//		    	 gl.getGL2().glDeleteShader(fragmentShaderID);
//
//		    	 gl.getGL2().glUseProgram(glProgram);
//		    	 
//		    	 
//		    	 int id = gl.getGL2().glGetUniformLocation(glProgram, "modelViewMatrix");
//		         gl.getGL2().glUniform4fv(id, 1, mvmatrix, 0);
//		         
//		         int idp = gl.getGL2().glGetUniformLocation(glProgram, "projectionMatrix");
//		         gl.getGL2().glUniform4fv(idp, 1, projmatrix, 0);
				
				
				if (!hasMountedOnce) {
					mount(gl);
				}
				
				if (disposed) {
					
					gl.glDeleteBuffers(1, arrayName, 0);
					gl.glDeleteBuffers(1, elementName, 0);
					return;
				}
				
//				if (!hasMountedOnce) {
//					badmount(gl);
//				}
//				
//				gl.getGL2().glDisable(GL.GL_BLEND);
				
				super.draw(gl, glu, cam);
//				gl.getGL2().glEnable(GL.GL_BLEND);
				
				gl.getGL2().glUseProgramObjectARB(0);
			}
			
//			@Override
//			protected void color(GL gl) {
//				int p = 3 * Buffers.SIZEOF_FLOAT;
////	            gl.getGL2().glEnableClientState(GL2.GL_COLOR_ARRAY);
//	            gl.getGL2().glColorPointer(colorChannelNumber, GL.GL_FLOAT, byteOffset, p);
//				gl.getGL2().glEnableVertexAttribArray(0);
//				gl.getGL2().glVertexAttribPointer(0, colorChannelNumber, GL.GL_FLOAT, false, 0, 0);
//			}
			
			
			 @Override
			    public void mount(GL gl) {
			        try {
			            loader.load(gl, this);
			            hasMountedOnce = true;
			        } catch (Exception e) {
			            e.printStackTrace();
//			            Logger.getLogger(DrawableVBO.class).error(e, e);
			        }
			    }
			 
//			 public void badmount(GL gl) {
//			        try {
//			            loader.load(gl, this);
//			            hasMountedOnce = true;
//			        } catch (Exception e) {
//			            e.printStackTrace();
////			            Logger.getLogger(DrawableVBO.class).error(e, e);
//			        }
//			 }
			
			@Override
			public void dispose() {
				disposed = true;
			}
			
		};
		v.setGeometry(GL2.GL_QUADS);
		v.setColor( new Color(1f, 0f, 1f, 1f));

		
		v.doSetBoundingBox(new BoundingBox3d(0, 1, 0, 1, 0, 1));
//		v.setWidth(1f);
		
		return v;
		
	}

public static class CubeVBO extends VBOBuilder {
	
	
	private float xMin;
	private float xMax;
	private float yMin;
	private float yMax;
	private float zMin;
	private float zMax;

	public CubeVBO(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
		
	}
	

	@Override
	public void load(GL gl, DrawableVBO drawable) throws Exception {
		FloatVBO vbo = initFloatVBO(drawable, true, 24);
		fillFromArray(drawable,  xMin,  xMax,  yMin,  yMax,  zMin,  zMax, vbo);
        drawable.setData(gl, vbo);
	}
	
//	private List<Coord3d> buildCoord(){
//		int size = 0;
//        List<Coord3d> co = new ArrayList<>(z.length);
//        for(int yi=0; yi<y.length-1; yi++){
//			for(int xi=0; xi<x.length-1; xi++){
//				// Compute quad making a polygon 
////				Point p[] = getRealQuadStandingOnPoint(xi, yi, x, y, z);
//				// Store quad
//				Coord3d c = new Coord3d();
//				c.x = x[xi];
//				c.y = y[yi];
//				c.z = z[xi+yi*x.length];
////	            putCoord(vertices, c);
////	            putColor(vertices, colors.getColor(c));
//	            co.add(c);
////				AbstractDrawable quad = newQuad(p);
////                polygons.add(quad);
//                
//			}
//		}	
//        
//        return co;
//	}
	
	private void fillFromArray(DrawableVBO drawable, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax, FloatVBO vbo) {
		FloatBuffer vertices= vbo.getVertices();
		IntBuffer indices = vbo.getIndices();
		drawable.setHasColorBuffer(true);

		int size = 0;
		Coord3d c = new Coord3d();
		//first
		indices.put(size++);
		c.x = xMin;
		c.y = yMin;
		c.z = zMin;

		putCoord(vertices, c);
		putColor(vertices, new Color(0,0,0));
		
		indices.put(size++);
		c.x = xMin;
		c.y = yMax;
		c.z = zMin;

		putCoord(vertices, c);
		putColor(vertices, new Color(0,255,0));
		
		indices.put(size++);
		c.x = xMax;
		c.y = yMax;
		c.z = zMin;

		putCoord(vertices, c);
		putColor(vertices, new Color(255,255,0));
		
		indices.put(size++);
		c.x = xMax;
		c.y = yMin;
		c.z = zMin;

		putCoord(vertices, c);
		putColor(vertices, new Color(255,0,0));
		//second
		indices.put(size++);
		c.x = xMin;
		c.y = yMin;
		c.z = zMin;

		putCoord(vertices, c);
		putColor(vertices, new Color(0,0,0));
		
		indices.put(size++);
		c.x = xMin;
		c.y = yMax;
		c.z = zMin;

		putCoord(vertices, c);
		putColor(vertices, new Color(0,255,0));
		
		indices.put(size++);
		c.x = xMin;
		c.y = yMax;
		c.z = zMax;

		putCoord(vertices, c);
		putColor(vertices, new Color(0,255,255));
		
		indices.put(size++);
		c.x = xMin;
		c.y = yMin;
		c.z = zMax;

		putCoord(vertices, c);
		putColor(vertices, new Color(0,0,255));

		//third
		indices.put(size++);
		c.x = xMin;
		c.y = yMin;
		c.z = zMin;

		putCoord(vertices, c);
		putColor(vertices, new Color(0,0,0));
		
		indices.put(size++);
		c.x = xMin;
		c.y = yMin;
		c.z = zMax;

		putCoord(vertices, c);
		putColor(vertices, new Color(0,0,255));
		
		indices.put(size++);
		c.x = xMax;
		c.y = yMin;
		c.z = zMax;

		putCoord(vertices, c);
		putColor(vertices, new Color(255,0,255));
		
		indices.put(size++);
		c.x = xMax;
		c.y = yMin;
		c.z = zMin;

		putCoord(vertices, c);
		putColor(vertices, new Color(255,0,0));
		
		//first - 2
				indices.put(size++);
				c.x = xMax;
				c.y = yMax;
				c.z = zMax;

				putCoord(vertices, c);
				putColor(vertices, new Color(255,255,255));
				
				indices.put(size++);
				c.x = xMax;
				c.y = yMin;
				c.z = zMax;

				putCoord(vertices, c);
				putColor(vertices, new Color(255,0,255));
				
				indices.put(size++);
				c.x = xMin;
				c.y = yMin;
				c.z = zMax;

				putCoord(vertices, c);
				putColor(vertices, new Color(0,0,255));
				
				indices.put(size++);
				c.x = xMin;
				c.y = yMax;
				c.z = zMax;

				putCoord(vertices, c);
				putColor(vertices, new Color(0,255,255));
				//second -2
				indices.put(size++);
				c.x = xMax;
				c.y = yMax;
				c.z = zMax;

				putCoord(vertices, c);
				putColor(vertices, new Color(255,255,255));
				
				indices.put(size++);
				c.x = xMax;
				c.y = yMin;
				c.z = zMax;

				putCoord(vertices, c);
				putColor(vertices, new Color(255,0,255));
				
				indices.put(size++);
				c.x = xMax;
				c.y = yMin;
				c.z = zMin;

				putCoord(vertices, c);
				putColor(vertices, new Color(255,0,0));
				
				indices.put(size++);
				c.x = xMax;
				c.y = yMax;
				c.z = zMin;

				putCoord(vertices, c);
				putColor(vertices, new Color(255,255,0));

				//third -2
				indices.put(size++);
				c.x = xMax;
				c.y = yMax;
				c.z = zMax;

				putCoord(vertices, c);
				putColor(vertices, new Color(255,255,255));
				
				indices.put(size++);
				c.x = xMax;
				c.y = yMax;
				c.z = zMin;

				putCoord(vertices, c);
				putColor(vertices, new Color(255,255,0));
				
				indices.put(size++);
				c.x = xMin;
				c.y = yMax;
				c.z = zMin;

				putCoord(vertices, c);
				putColor(vertices, new Color(0,255,0));
				
				indices.put(size++);
				c.x = xMin;
				c.y = yMax;
				c.z = zMax;

				putCoord(vertices, c);
				putColor(vertices, new Color(0,255,255));
		
		vertices.rewind();
		indices.rewind();
		vbo.setBounds(new BoundingBox3d(xMin, yMin, xMax, yMax, zMin, zMax));
    }
}
}
