package org.jzy3d.demos.surface;

import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.rendering.view.Camera;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

/**
 * A {@link Quad} extends a {@link Polygon} in order to provide a specific
 * {@link draw()} method that relies on a dedicated GL2 call (GL_QUADS), and to
 * ensure the number of points is never greater than 4.
 * 
 * @see {@link Polygon}
 * 
 * @author Martin Pernollet
 */
public class QuadGLSL extends Polygon {
	/**
	 * Initializes an empty {@link Quad} with face status defaulting to true,
	 * and wireframe status defaulting to false.
	 */
	public QuadGLSL() {
		super();
	}
	
    @Override
    protected void begin(GL gl) {
        if (gl.isGL2()) {
            gl.getGL2().glBegin(GL2.GL_QUADS);
        } else {
            GLES2CompatUtils.glBegin(GL2.GL_QUADS);
        }
    }

    @Override
    public void draw(GL gl, GLU glu, Camera cam) {
    	doTransform(gl, glu, cam);
    	
    	float mvmatrix[] = new float[16];
    	float projmatrix[] = new float[16];
    	
    	gl.getGL2().glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
    	gl.getGL2().glGetFloatv(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);

    	int vertexShaderID = gl.getGL2().glCreateShader(GL2.GL_VERTEX_SHADER);
    	int fragmentShaderID = gl.getGL2().glCreateShader(GL2.GL_FRAGMENT_SHADER);
    	
    	String  vertex = "#version 330\n uniform mat4 gl_ModelViewMatrix; uniform mat4 gl_ProjectionMatrix; in vec4 vVertex; in vec4 vColor; out vec4 vVaryingColor; void main() { vVaryingColor=vec4(0,1,0,1); gl_Position=gl_ProjectionMatrix*gl_ModelViewMatrix*vVertex;}";
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
    	 
//do something
         if (mapper != null)
             mapper.preDraw(this);

         // Draw content of polygon
         if (facestatus) {
             applyPolygonModeFill(gl);
             if (wfstatus && polygonOffsetFillEnable)
                 polygonOffseFillEnable(gl);
             callPointsForFace(gl);
             if (wfstatus && polygonOffsetFillEnable)
                 polygonOffsetFillDisable(gl);
         }

         // Draw edge of polygon
         if (wfstatus) {
             applyPolygonModeLine(gl);
             if (polygonOffsetFillEnable)
                 polygonOffseFillEnable(gl);
             callPointForWireframe(gl);
             if (polygonOffsetFillEnable)
                 polygonOffsetFillDisable(gl);
         }

         if (mapper != null)
             mapper.postDraw(this);

         doDrawBounds(gl, glu, cam);
    	 
//    	 FloatBuffer mat;
//		gl.getGL2().glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mat);
//    	 
// 
//    	 GL2.GL_PROJECTION_MATRIX
//    	super.draw(gl, glu, cam);
    	
    	gl.getGL2().glUseProgramObjectARB(0);
    	//do something else
    }
    

	/** Add a point to the polygon. */
	@Override
    public void add(Point point) {
		if (points.size() == 4)
			throw new RuntimeException(
					"The Quad allready has 4 points registered");

		super.add(point);
	}

	@Override
    public String toString(int depth) {
		return Utils.blanks(depth) + "(Quad) #points:" + points.size();
	}
}
