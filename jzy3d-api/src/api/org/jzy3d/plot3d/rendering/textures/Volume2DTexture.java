package org.jzy3d.plot3d.rendering.textures;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.plot3d.primitives.AbstractComposite;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.ColoredWireframePolygon;
import org.jzy3d.plot3d.primitives.textured.DrawableTexture;

import com.jogamp.opengl.util.GLBuffers;

public class Volume2DTexture extends AbstractComposite {

	private ColorMapper mapper;
	private IAlphaMapper aMapper;
	
	public Volume2DTexture(short[] data, int[] shape, Coord3d start, Coord3d stop,
			ColorMapper mapper, IAlphaMapper aMapper) {
		
		this.mapper = mapper;
		this.aMapper = aMapper;
		Coord3d dif = stop.sub(start);
		Coord3d centre = dif.div(2);
		float widthx = dif.x/2;
		float widthy = dif.y/2;
		float widthz = dif.z/2;
		
    	List<Coord2d> zmapping  = new ArrayList<Coord2d>(4);
    	zmapping.add(new Coord2d(centre.x-widthx, centre.y-widthy));
    	zmapping.add(new Coord2d(centre.x+widthx, centre.y-widthy));
    	zmapping.add(new Coord2d(centre.x+widthx, centre.y+widthy));
    	zmapping.add(new Coord2d(centre.x-widthx, centre.y+widthy));
		
		ByteBuffer[] ims = makeImagesX(data, shape);
		float offsetz = centre.z-widthz;
    	for (ByteBuffer b : ims) {
    		ColorMappedTexture t = new ColorMappedTexture(b,shape[1],shape[2]);
    		DrawableTexture north = new DrawableTexture(t, PlaneAxis.Z, offsetz, zmapping);
    		north.setAlphaFactor(1f);
    		add(north);
    		offsetz+=(widthz*2)/ims.length;    	
    	}

    	ByteBuffer[] imsz = makeImagesZ(data, shape);

    	List<Coord2d> xmapping  = new ArrayList<Coord2d>(4);
    	xmapping.add( new Coord2d(centre.y-widthy, centre.z-widthz) );
    	xmapping.add( new Coord2d(centre.y+widthy, centre.z-widthz) );
    	xmapping.add( new Coord2d(centre.y+widthy, centre.z+widthz) );
    	xmapping.add( new Coord2d(centre.y-widthy, centre.z+widthz) );
    	float offsetx = centre.x-widthx;
    	for (ByteBuffer b : imsz) {
    		ColorMappedTexture t = new ColorMappedTexture(b,shape[0],shape[1]);
    		DrawableTexture north = new DrawableTexture(t, PlaneAxis.X, offsetx, xmapping);
    		north.setAlphaFactor(0.5f);
    		add(north);
    		offsetx+=(widthx*2)/imsz.length;    	
    	}
    	
    	ByteBuffer[] imsy = makeImagesY(data, shape);

    	List<Coord2d> ymapping  = new ArrayList<Coord2d>(4);
    	ymapping.add( new Coord2d(centre.x-widthx, centre.z-widthz) );
    	ymapping.add( new Coord2d(centre.x+widthx, centre.z-widthz) );
    	ymapping.add( new Coord2d(centre.x+widthx, centre.z+widthz) );
    	ymapping.add( new Coord2d(centre.x-widthx, centre.z+widthz) );
    	float offsety = centre.y-widthy;
    	for (ByteBuffer b : imsy) {
    		ColorMappedTexture t = new ColorMappedTexture(b,shape[0],shape[2]);
    		DrawableTexture north = new DrawableTexture(t, PlaneAxis.Y, offsety, ymapping);
    		north.setAlphaFactor(0.5f);
    		add(north);
    		offsety+=(widthy*2)/imsy.length; 	
    	}	
	}
	
	private ByteBuffer[] makeImagesX(short[] buffer, int[] shape) {

    	 ByteBuffer[] buffers = new ByteBuffer[shape[0]];
 
    	 for (int i = 0; i < shape[0]; i++) {
    		 ByteBuffer image = GLBuffers.newDirectByteBuffer( shape[1] * shape[2] * 4);
    		 for (int j = 0; j < shape[1]; j++) {
    			 for (int k = 0; k < shape[2]; k++) {
    				 int pos = k+((shape[2])*j)+(shape[1]*shape[2]*i); 
    				 putValue(buffer, image, pos); 
    			 }	
    		 }
    		 image.rewind();

    		 buffers[i] = image;
    	 }
    	 
    	 return buffers;

    }
	
	private ByteBuffer[] makeImagesY(short[] buffer, int[] shape) {

    	 ByteBuffer[] buffers = new ByteBuffer[shape[1]];
    	 for (int j = 0; j < shape[1]; j++) {
    		 ByteBuffer image = GLBuffers.newDirectByteBuffer( shape[0] * shape[2] * 4);
    		 for (int i = 0; i < shape[0]; i++) {
    			 for (int k = 0; k < shape[2]; k++) {
    				 int pos = k+((shape[2])*j)+(shape[1]*shape[2]*i); 
    				 putValue(buffer, image, pos);
    			 }	
    		 }
    		 image.rewind();

    		 buffers[j] = image;
    	 }
    	 
    	 return buffers;

    }
    
    private ByteBuffer[] makeImagesZ(short[] buffer, int[] shape) {

    	 ByteBuffer[] buffers = new ByteBuffer[shape[2]];
    	 for (int k = 0; k < shape[2]; k++) {
    		 ByteBuffer image = GLBuffers.newDirectByteBuffer( shape[0] * shape[1] * 4);
    		 for (int i = 0; i < shape[0]; i++) {
    			 for (int j = 0; j< shape[1]; j++) {
    				 int pos = k+((shape[2])*j)+(shape[1]*shape[2]*i); 
    				 putValue(buffer, image, pos);
    			 }	
    		 }
    		 image.rewind();

    		 buffers[k] = image;
    	 }
    	 
    	 return buffers;

    }
	
	private void putValue(short[] buffer, ByteBuffer image, int pos) {
//    	System.out.println(pos);
    	float b = buffer[pos];
    	Color c = mapper.getColor(b);
		 image.put((byte)(c.r*255));
		 image.put((byte)(c.g*255));
		 image.put((byte)(c.b*255));
		 image.put((byte)(255*aMapper.getAlpha(b)));
		 
    }
	
	@Override
	public void setColorMapper(ColorMapper mapper) {
		for (AbstractDrawable d : getDrawables()) {
			if (d instanceof ColoredWireframePolygon) {
				((ColoredWireframePolygon) d).setColorMapper(mapper);
			}
		}
	}
}
