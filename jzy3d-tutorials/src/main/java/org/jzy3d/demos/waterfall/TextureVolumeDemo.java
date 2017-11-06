package org.jzy3d.demos.waterfall;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.plot3d.primitives.textured.DrawableTexture;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.textures.ColorMappedTexture;

import com.jogamp.opengl.util.GLBuffers;

public class TextureVolumeDemo extends AbstractAnalysis {
    public static void main(String[] args) throws Exception {
        AnalysisLauncher.open(new TextureVolumeDemo());
    }

    @Override
    public void init() {
    	 
         Coord3d position = new Coord3d(2.0, 2.0, 2.0);

         float width = 2.0f;
         
         List<Coord2d> zmapping  = new ArrayList<Coord2d>(4);
         zmapping.add( new Coord2d(position.x-width, position.y-width) );
         zmapping.add( new Coord2d(position.x+width, position.y-width) );
         zmapping.add( new Coord2d(position.x+width, position.y+width) );
         zmapping.add( new Coord2d(position.x-width, position.y+width) );
         
         
         
         chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType());
        
         chart.getView();
    	
    	ByteBuffer[] ims = makeImagesX();
    	float offset = 0;
    	for (ByteBuffer b : ims) {
    		ColorMappedTexture t = new ColorMappedTexture(b,256,256);
    		DrawableTexture north = new DrawableTexture(t, PlaneAxis.Z, offset, zmapping);
            north.setAlphaFactor(1f);
    		 chart.getScene().getGraph().add(north);
    		 offset+=4.0/ims.length;    	
    	}
       
    	ByteBuffer[] imsy = makeImagesY();
    	
    	zmapping  = new ArrayList<Coord2d>(4);
        zmapping.add( new Coord2d(position.x-width, position.y-width) );
        zmapping.add( new Coord2d(position.x+width, position.y-width) );
        zmapping.add( new Coord2d(position.x+width, position.y+width) );
        zmapping.add( new Coord2d(position.x-width, position.y+width) );
    	offset = 0;
    	for (ByteBuffer b : imsy) {
    		ColorMappedTexture t = new ColorMappedTexture(b,109,256);
    		DrawableTexture north = new DrawableTexture(t, PlaneAxis.Y, offset, zmapping);
            north.setAlphaFactor(1f);
    		 chart.getScene().getGraph().add(north);
    		 offset+=4.0/imsy.length;    	
    	}
    	
ByteBuffer[] imsz = makeImagesZ();
    	
    	zmapping  = new ArrayList<Coord2d>(4);
        zmapping.add( new Coord2d(position.x-width, position.y-width) );
        zmapping.add( new Coord2d(position.x+width, position.y-width) );
        zmapping.add( new Coord2d(position.x+width, position.y+width) );
        zmapping.add( new Coord2d(position.x-width, position.y+width) );
    	offset = 0;
    	for (ByteBuffer b : imsz) {
    		ColorMappedTexture t = new ColorMappedTexture(b,109,256);
    		DrawableTexture north = new DrawableTexture(t, PlaneAxis.X, offset, zmapping);
            north.setAlphaFactor(1f);
    		 chart.getScene().getGraph().add(north);
    		 offset+=4.0/imsz.length;    	
    	}
    }

    private ByteBuffer[] makeImages() {
    	int iDepth = 109;
    	int iWidth = 256;
    	int iHeight = 256;
    	 byte[] chBuffer = read("C:\\Users\\max_b\\Desktop\\head256x256x109");

    	 ByteBuffer[] buffers = new ByteBuffer[iDepth];
    	 for (int j = 0; j < iDepth; j++) {
    		 // Holds the RGBA buffer
    		 ByteBuffer image = GLBuffers.newDirectByteBuffer( iWidth * iHeight * 4);

    		 for (int nIndx = iHeight * iWidth * j; nIndx < iHeight * iWidth *(j+1); ++nIndx) {
    			 //        	 image.put((byte)100);
    			 //        	 image.put((byte)100);
    			 //        	 image.put((byte)100);
    			 //        	 image.put((byte)100);
    			 image.put((byte)(255 - chBuffer[nIndx]));
    			 byte b = chBuffer[nIndx];
    			 image.put((byte)(255 - chBuffer[nIndx]));
    			 image.put((byte)(255 - chBuffer[nIndx]));
    			 image.put((byte)(chBuffer[nIndx]));
    		 }

    		 image.flip();
    		 
    		 buffers[j] = image;
    	 }
    	 
    	 return buffers;

    }
    
    private ByteBuffer[] makeImages3() {
    	int iDepth = 109;
    	int iWidth = 256;
    	int iHeight = 256;
    	 byte[] chBuffer = read("C:\\Users\\max_b\\Desktop\\head256x256x109");
    	 int[] shape = {iDepth,iHeight, iWidth};

//    	 ByteBuffer[] buffers = new ByteBuffer[iDepth];
//
//    	 int[] shape = {iDepth,iHeight, iWidth};   	 
//    	 for (int i = 0; i < shape[0]; i++) {
//    		 ByteBuffer image = GLBuffers.newDirectByteBuffer( iHeight * iWidth * 4);
//    		 for (int j = 0; j < shape[1]; j++) {
//    			 for (int k = 0; k < shape[2]; k++) {
//    				 int pos = k+((shape[2])*j)+(shape[1]*shape[2]*i); 
////    				 System.out.println(pos);
//    				 byte b = chBuffer[pos];
//    				 image.put((byte)(255 - b));
//    				 image.put((byte)(255 - b));
//    				 image.put((byte)(255 - b));
//    				 image.put((byte)(b));
//    			 }	
//    		 }
//    		 image.flip();
//
//    		 buffers[i] = image;
//    	 }
    	 
//    	 ByteBuffer[] buffers = new ByteBuffer[iHeight];
//    	 for (int j = 0; j < shape[1]; j++) {
//    		 ByteBuffer image = GLBuffers.newDirectByteBuffer( iDepth * iWidth * 4);
//    		 for (int i = 0; i < shape[0]; i++) {
//    			 for (int k = 0; k < shape[2]; k++) {
//    				 int pos = k+((shape[2])*j)+(shape[1]*shape[2]*i); 
//    				 byte b = chBuffer[pos];
//    				 image.put((byte)(255 - b));
//
//    				 image.put((byte)(255 - b));
//    				 image.put((byte)(255 - b));
//    				 image.put((byte)(b));
//    			 }	
//    		 }
//    		 image.flip();
//
//    		 buffers[j] = image;
//    	 }
    	 
    	 ByteBuffer[] buffers = new ByteBuffer[iHeight];
    	 for (int k = 0; k < shape[2]; k++) {
    		 ByteBuffer image = GLBuffers.newDirectByteBuffer( iDepth * iWidth * 4);
    		 for (int i = 0; i < shape[0]; i++) {
    			 for (int j = 0; j < shape[1]; j++) {
    				 int pos = k+((shape[2])*j)+(shape[1]*shape[2]*i); 
    				 byte b = chBuffer[pos];
    				 image.put((byte)(255 - b));

    				 image.put((byte)(255 - b));
    				 image.put((byte)(255 - b));
    				 image.put((byte)(b));
    			 }	
    		 }
    		 image.flip();

    		 buffers[k] = image;
    	 }
    	 
    	 return buffers;

    }
    
    private ByteBuffer[] makeImagesX() {
    	int iDepth = 109;
    	int iWidth = 256;
    	int iHeight = 256;
    	 byte[] chBuffer = read("C:\\data\\head256x256x109");
    	 int[] shape = {iDepth,iHeight, iWidth};

    	 ByteBuffer[] buffers = new ByteBuffer[iDepth];
 
    	 for (int i = 0; i < shape[0]; i++) {
    		 ByteBuffer image = GLBuffers.newDirectByteBuffer( iHeight * iWidth * 4);
    		 for (int j = 0; j < shape[1]; j++) {
    			 for (int k = 0; k < shape[2]; k++) {
    				 int pos = k+((shape[2])*j)+(shape[1]*shape[2]*i); 
    				 byte b = chBuffer[pos];
    				 image.put((byte)(255 - b));
    				 image.put((byte)(255 - b));
    				 image.put((byte)(255 - b));
    				 image.put((byte)(b/10));
    			 }	
    		 }
    		 image.flip();

    		 buffers[i] = image;
    	 }
    	 
    	 return buffers;

    }
    
    private ByteBuffer[] makeImagesY() {
    	int iDepth = 109;
    	int iWidth = 256;
    	int iHeight = 256;
    	 byte[] chBuffer = read("C:\\data\\head256x256x109");
    	 int[] shape = {iDepth,iHeight, iWidth};

    	 ByteBuffer[] buffers = new ByteBuffer[iHeight];
    	 for (int j = 0; j < shape[1]; j++) {
    		 ByteBuffer image = GLBuffers.newDirectByteBuffer( iDepth * iWidth * 4);
    		 for (int i = 0; i < shape[0]; i++) {
    			 for (int k = 0; k < shape[2]; k++) {
    				 int pos = k+((shape[2])*j)+(shape[1]*shape[2]*i); 
    				 byte b = chBuffer[pos];
    				 image.put((byte)(255 - b));

    				 image.put((byte)(255 - b));
    				 image.put((byte)(255 - b));
    				 image.put((byte)(b/10));
    			 }	
    		 }
    		 image.flip();

    		 buffers[j] = image;
    	 }
    	 
    	 return buffers;

    }
    
    private ByteBuffer[] makeImagesZ() {
    	int iDepth = 109;
    	int iWidth = 256;
    	int iHeight = 256;
    	 byte[] chBuffer = read("C:\\data\\head256x256x109");
    	 int[] shape = {iDepth,iHeight, iWidth};

    	 ByteBuffer[] buffers = new ByteBuffer[iHeight];
    	 for (int k = 0; k < shape[2]; k++) {
    		 ByteBuffer image = GLBuffers.newDirectByteBuffer( iDepth * iWidth * 4);
    		 for (int i = 0; i < shape[0]; i++) {
    			 for (int j = 0; j< shape[1]; j++) {
    				 int pos = k+((shape[2])*j)+(shape[1]*shape[2]*i); 
    				 byte b = chBuffer[pos];
    				 image.put((byte)(255 - b));

    				 image.put((byte)(255 - b));
    				 image.put((byte)(255 - b));
    				 image.put((byte)(b/10));
    			 }	
    		 }
    		 image.flip();

    		 buffers[k] = image;
    	 }
    	 
    	 return buffers;

    }
    
    /** Read the given binary file, and return its contents as a byte array.*/
    byte[] read(String aInputFileName){
        File file = new File(aInputFileName);
        byte[] result = new byte[(int)file.length()];
        try {
            InputStream input = null;
            try {
                int totalBytesRead = 0;
                input = new BufferedInputStream(new FileInputStream(file));
                while(totalBytesRead < result.length){
                    int bytesRemaining = result.length - totalBytesRead;
                    //input.read() returns -1, 0, or more :
                    int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                    if (bytesRead > 0){
                        totalBytesRead = totalBytesRead + bytesRead;
                    }
                }
                /*
                 the above style is a bit tricky: it places bytes into the 'result' array;
                 'result' is an output parameter;
                 the while loop usually has a single iteration only.
                */
            }
            finally {
                input.close();
            }
        }
        catch (FileNotFoundException ex) {
        }
        catch (IOException ex) {
        }
        return result;
    }
}