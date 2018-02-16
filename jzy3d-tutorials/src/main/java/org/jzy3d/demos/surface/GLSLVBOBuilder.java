package org.jzy3d.demos.surface;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.vbo.buffers.FloatVBO;
import org.jzy3d.plot3d.primitives.vbo.builders.VBOBuilder;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.view.Camera;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class GLSLVBOBuilder extends VBOBuilder {

	@Override
	public void load(GL gl, DrawableVBO drawable) throws Exception {
		
//		FloatVBO vbo = initFloatVBO(drawable, true, 3);
//		fillFromArray(drawable, x,y,z,mapper, vbo);
//        drawable.setData(gl, vbo);
//		List<Coord3d>  coordinates = buildCoo
		
	}
	
public static DrawableVBO buildShape(float[] x, float[] y, float[] z, ColorMapper mapper) {
		
		DrawableVBO v =  new DrawableVBO(new ColoredMeshVBO(x, y, z, mapper)) {
			
			boolean disposed = false;
			
			@Override
		    public void draw(GL gl, GLU glu, Camera cam) {
				
				doTransform(gl, glu, cam);
		    	
		    	float mvmatrix[] = new float[16];
		    	float projmatrix[] = new float[16];
		    	
		    	gl.getGL2().glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		    	gl.getGL2().glGetFloatv(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);

		    	int vertexShaderID = gl.getGL2().glCreateShader(GL2.GL_VERTEX_SHADER);
		    	int fragmentShaderID = gl.getGL2().glCreateShader(GL2.GL_FRAGMENT_SHADER);
		    	
		    	String  vertex = "#version 330\n uniform mat4 gl_ModelViewMatrix; uniform mat4 gl_ProjectionMatrix;in vec4 vt;in vec4 color; out vec4 vVaryingColor; void main() { vVaryingColor=color; if (vt.z == 0) {vVaryingColor=vec4(0,0,1,1);}; gl_Position=gl_ProjectionMatrix*gl_ModelViewMatrix*vt;}";
		    	String  fragment = "#version 330\n in vec4 vVaryingColor; out vec4 vFragColor; void main() { vFragColor=vVaryingColor;}";
		    	
		    	gl.getGL2().glShaderSource(vertexShaderID, 1, new String[] {vertex} , null);
		    	gl.getGL2().glCompileShader(vertexShaderID);
		    	
		    	gl.getGL2().glShaderSource(fragmentShaderID, 1, new String[] {fragment} , null);
		    	gl.getGL2().glCompileShader(fragmentShaderID);
		    	
		    	 int glProgram = gl.getGL2().glCreateProgram();
		    	 gl.getGL2().glAttachShader(glProgram, vertexShaderID);
		    	 gl.getGL2().glAttachShader(glProgram, fragmentShaderID);
		    	 gl.getGL2().glLinkProgram(glProgram);
		    	
		    	 gl.getGL2().glDetachShader(glProgram, vertexShaderID);
		    	 gl.getGL2().glDetachShader(glProgram, fragmentShaderID);
		    		
		    	 gl.getGL2().glDeleteShader(vertexShaderID);
		    	 gl.getGL2().glDeleteShader(fragmentShaderID);

		    	 gl.getGL2().glUseProgram(glProgram);
		    	 
		    	 
		    	 int id = gl.getGL2().glGetUniformLocation(glProgram, "gl_ModelViewMatrix");
		         gl.getGL2().glUniform4fv(id, 1, mvmatrix, 0);
		         
		         int idp = gl.getGL2().glGetUniformLocation(glProgram, "gl_ProjectionMatrix");
		         gl.getGL2().glUniform4fv(idp, 1, projmatrix, 0);
				
				
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
		float minY = Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        float minX = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxZ = -Float.MAX_VALUE;
        
        for (int i = 0; i < x.length;i++) {
        	float t = x[i];
        	if (t < minX) minX = t;
        	if (t > maxX) maxX = t;
        }
        
        for (int i = 0; i < y.length;i++) {
        	float t = y[i];
        	if (t < minY) minY = t;
        	if (t > maxY) maxY = t;
        }
        
        for (int i = 0; i < z.length;i++) {
        	float t = z[i];
        	if (t < minZ) minZ = t;
        	if (t > maxZ) maxZ = t;
        }
		
		v.doSetBoundingBox(new BoundingBox3d(minX, maxX, minY, maxY, minZ, maxZ));
//		v.setWidth(1f);
		
		return v;
		
	}

	public static class ColoredPointVBO extends VBOBuilder {
		
		private float[] x;
		private float[] y;
		private float[] z;
		private ColorMapper mapper;
		
		public ColoredPointVBO(float[] x, float[] y, float[] z, ColorMapper mapper) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.mapper = mapper;
		}
		

		@Override
		public void load(GL gl, DrawableVBO drawable) throws Exception {
			FloatVBO vbo = initFloatVBO(drawable, true, z.length);
			fillFromArray(drawable, x,y,z,mapper, vbo);
	        drawable.setData(gl, vbo);
//			List<Coord3d>  coordinates = buildCoord();
//			 FloatVBO vbo = initFloatVBO(drawable, true, coordinates.size());
//		        fillWithCollection(drawable, coordinates, vbo, mapper);
//		        drawable.setData(gl, vbo);
			
		}
		
		private List<Coord3d> buildCoord(){
			int size = 0;
	        List<Coord3d> co = new ArrayList<>(z.length);
	        for(int yi=0; yi<y.length-1; yi++){
				for(int xi=0; xi<x.length-1; xi++){
					// Compute quad making a polygon 
//					Point p[] = getRealQuadStandingOnPoint(xi, yi, x, y, z);
					// Store quad
					Coord3d c = new Coord3d();
					c.x = x[xi];
					c.y = y[yi];
					c.z = z[xi+yi*x.length];
//		            putCoord(vertices, c);
//		            putColor(vertices, colors.getColor(c));
		            co.add(c);
//					AbstractDrawable quad = newQuad(p);
//	                polygons.add(quad);
	                
				}
			}	
	        
	        return co;
		}
		
		private void fillFromArray(DrawableVBO drawable, float[] x, float[] y, float[] z, ColorMapper colors, FloatVBO vbo) {
			FloatBuffer vertices= vbo.getVertices();
			IntBuffer indices = vbo.getIndices();
	        drawable.setHasColorBuffer(true);
	        int size = 0;
	        Coord3d c = new Coord3d();
	        float minY = Float.MAX_VALUE;
	        float maxY = -Float.MAX_VALUE;
	        float minX = Float.MAX_VALUE;
	        float maxX = -Float.MAX_VALUE;
	        float minZ = Float.MAX_VALUE;
	        float maxZ = -Float.MAX_VALUE;
	        
	        for (int i = 0; i < x.length;i++) {
	        	float t = x[i];
	        	if (t < minX) minX = t;
	        	if (t > maxX) maxX = t;
	        }
	        
	        for (int i = 0; i < y.length;i++) {
	        	float t = y[i];
	        	if (t < minY) minY = t;
	        	if (t > maxY) maxY = t;
	        }
	        
	        for(int yi=0; yi<y.length-1; yi++){
				for(int xi=0; xi<x.length-1; xi++){
					// Compute quad making a polygon 
//					Point p[] = getRealQuadStandingOnPoint(xi, yi, x, y, z);
					// Store quad
					indices.put(size++);
					c.x = x[xi];
					c.y = y[yi];
					c.z = z[xi+yi*x.length];
					
					if (c.z < minZ) minZ = c.z;
		        	if (c.z > maxZ) maxZ = c.z;
					
		            putCoord(vertices, c);
		            putColor(vertices, colors.getColor(c));
//					AbstractDrawable quad = newQuad(p);
//	                polygons.add(quad);
	                
				}
			}	
	        
	        
	        
//	        for (Coord3d c : coordinates) {
//	            indices.put(size++);
//	            putCoord(vertices, c);
//	            bounds.add(c);
//	            if (colors != null) {
//	                putColor(vertices, colors.getColor(c));
//	            }
//	        }
	        vertices.rewind();
	        indices.rewind();
	        vbo.setBounds(new BoundingBox3d(minX, maxX, minY, maxY, minZ, maxZ));
	    }
	}
		
		public static class ColoredMeshVBO extends VBOBuilder {
			
			private float[] x;
			private float[] y;
			private float[] z;
			private ColorMapper mapper;
			
			public ColoredMeshVBO(float[] x, float[] y, float[] z, ColorMapper mapper) {
				this.x = x;
				this.y = y;
				this.z = z;
				this.mapper = mapper;
			}
			

			@Override
			public void load(GL gl, DrawableVBO drawable) throws Exception {
				FloatVBO vbo = initFloatVBO(drawable, true, (y.length-1)*(x.length-1)*4);
				fillFromArray(drawable, x,y,z,mapper, vbo);
		        drawable.setData(gl, vbo);
//				List<Coord3d>  coordinates = buildCoord();
//				 FloatVBO vbo = initFloatVBO(drawable, true, coordinates.size());
//			        fillWithCollection(drawable, coordinates, vbo, mapper);
//			        drawable.setData(gl, vbo);
				
			}
			
			private List<Coord3d> buildCoord(){
				int size = 0;
		        List<Coord3d> co = new ArrayList<>(z.length);
		        for(int yi=0; yi<y.length-1; yi++){
					for(int xi=0; xi<x.length-1; xi++){
						// Compute quad making a polygon 
//						Point p[] = getRealQuadStandingOnPoint(xi, yi, x, y, z);
						// Store quad
						Coord3d c = new Coord3d();
						c.x = x[xi];
						c.y = y[yi];
						c.z = z[xi+yi*x.length];
//			            putCoord(vertices, c);
//			            putColor(vertices, colors.getColor(c));
			            co.add(c);
//						AbstractDrawable quad = newQuad(p);
//		                polygons.add(quad);
		                
					}
				}	
		        
		        return co;
			}
			
			private void fillFromArray(DrawableVBO drawable, float[] x, float[] y, float[] z, ColorMapper colors, FloatVBO vbo) {
				FloatBuffer vertices= vbo.getVertices();
				IntBuffer indices = vbo.getIndices();
		        drawable.setHasColorBuffer(true);

		        int size = 0;
		        Coord3d c = new Coord3d();
		        float minY = Float.MAX_VALUE;
		        float maxY = -Float.MAX_VALUE;
		        float minX = Float.MAX_VALUE;
		        float maxX = -Float.MAX_VALUE;
		        float minZ = Float.MAX_VALUE;
		        float maxZ = -Float.MAX_VALUE;
		        
		        for (int i = 0; i < x.length;i++) {
		        	float t = x[i];
		        	if (t < minX) minX = t;
		        	if (t > maxX) maxX = t;
		        }
		        
		        for (int i = 0; i < y.length;i++) {
		        	float t = y[i];
		        	if (t < minY) minY = t;
		        	if (t > maxY) maxY = t;
		        }
		        
		        for(int yi=0; yi<y.length-1; yi++){
					for(int xi=0; xi<x.length-1; xi++){
						// Compute quad making a polygon 
//						Point p[] = getRealQuadStandingOnPoint(xi, yi, x, y, z);
						// Store quad
						
						int pos = xi+yi*x.length;
						int posNext = xi+(yi+1)*x.length;
						
						
//						p[0] = new Point(new Coord3d(x[xi],   y[yi],   z[pos]    ));
//						p[1] = new Point(new Coord3d(x[xi+1], y[yi],   z[pos+1]  ));
//						p[2] = new Point(new Coord3d(x[xi+1], y[yi+1], z[posNext+1]));
//						p[3] = new Point(new Coord3d(x[xi],   y[yi+1], z[posNext]  ));
						
						indices.put(size++);
						c.x = x[xi];
						c.y = y[yi+1];
						c.z = z[posNext];
						
						if (c.z < minZ) minZ = c.z;
			        	if (c.z > maxZ) maxZ = c.z;
						
			            putCoord(vertices, c);
			            putColor(vertices, colors.getColor(c));
						
						indices.put(size++);
						c.x = x[xi];
						c.y = y[yi];
						c.z = z[pos];
						
						if (c.z < minZ) minZ = c.z;
			        	if (c.z > maxZ) maxZ = c.z;
						
			            putCoord(vertices, c);
			            putColor(vertices, colors.getColor(c));
			            
			            indices.put(size++);
						c.x = x[xi+1];
						c.y = y[yi];
						c.z = z[pos+1];
						
						if (c.z < minZ) minZ = c.z;
			        	if (c.z > maxZ) maxZ = c.z;
						
			            putCoord(vertices, c);
			            putColor(vertices, colors.getColor(c));
			            
			            
			            indices.put(size++);
						c.x = x[xi+1];
						c.y = y[yi+1];
						c.z = z[posNext+1];
						
						if (c.z < minZ) minZ = c.z;
			        	if (c.z > maxZ) maxZ = c.z;
						
			            putCoord(vertices, c);
			            putColor(vertices, colors.getColor(c));
			            
					}
				}	
		        
		        
		        
//		        for (Coord3d c : coordinates) {
//		            indices.put(size++);
//		            putCoord(vertices, c);
//		            bounds.add(c);
//		            if (colors != null) {
//		                putColor(vertices, colors.getColor(c));
//		            }
//		        }
		        vertices.rewind();
		        indices.rewind();
		        vbo.setBounds(new BoundingBox3d(minX, maxX, minY, maxY, minZ, maxZ));
		    }
		}

}
