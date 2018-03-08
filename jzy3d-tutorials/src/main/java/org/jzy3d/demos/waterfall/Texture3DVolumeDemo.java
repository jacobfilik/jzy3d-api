package org.jzy3d.demos.waterfall;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.textures.SimpleAlphaMapper;
import org.jzy3d.plot3d.rendering.textures.Volume2DTexture;

import com.jogamp.opengl.util.GLBuffers;

public class Texture3DVolumeDemo extends AbstractAnalysis {
    public static void main(String[] args) throws Exception {
        AnalysisLauncher.open(new Texture3DVolumeDemo());
    }

    @Override
    public void init() {

    	chart = AWTChartComponentFactory.chart(Quality.Intermediate, getCanvasType());

    	chart.getView();
    	chart.getView().setBackgroundColor(Color.WHITE);

    	int[] shape = {99,99,90};
    	short[] s = null;
    	
    	try {
    		s = getData();
    	} catch (Exception e) {
    		e.printStackTrace();
			return;
		}
    	
    	ByteBuffer image = GLBuffers.newDirectByteBuffer(3 * 99 * 99 * 90);
    	
//    	byte[] b = new byte[s.length*3];
    	for (int i = 0; i < s.length; i++) {
    		image.put((byte)s[i]);
    		image.put((byte)s[i]);
    		image.put((byte)s[i]);
    	}
    	image.rewind();
//    	
//    	Arrays.fill(b, (byte)64);
    	
    	
    	
//    	byte[] b = new byte[shape[0]*shape[1]*shape[2]*4];
//    	Arrays.fill(b, (byte)64);
//    	for (int i = 0; i < s.length; i++) {
//    		b[i] = (byte)s[i];
//    	}
    	
    	Texture3D t3d = new Texture3D(image, shape);
//    	Texture3D t3d = new Texture3D(makeImage(), new int[] {33,33,30});
//    	Coord3d start = new Coord3d(5, 5, 5);
//    	Coord3d stop = new Coord3d(10, 10, 10);
//    	SimpleAlphaMapper am = new SimpleAlphaMapper(25,1000);
//    	ColorMapper mapper = new ColorMapper(new ColorMapRainbow(), 25, 128);
//    	Volume2DTexture volume = new Volume2DTexture(s,shape,start,stop,mapper,am);

    	chart.getScene().getGraph().add(t3d);
    	
    }
    
    private ByteBuffer makeImage() {
    	ByteBuffer image = GLBuffers.newDirectByteBuffer(3 * 33 * 33 * 30);
        int ss = 0, tt = 0, rr = 0;
        for (int s = 0; s < 33; s++)
            for (int t = 0; t < 33; t++)
                for (int r = 0; r < 30; r++) {
                    // image[r][t][s][0] = (GLubyte) (s * 17);
                    // image[r][t][s][1] = (GLubyte) (t * 17);
                    // image[r][t][s][2] = (GLubyte) (r * 17);
                    ss = s * 17;
                    tt = t * 17;
                    rr = r * 17;
                    // System.out.println("s" + ss + "." + ss//
                    // + "t" + tt + "." + tt//
                    // + "r" + rr + "." + rr);
                    image.put((byte) (ss * 17));
                    image.put((byte) tt);
                    image.put((byte) rr);
                }
        image.rewind();
        return image;
    }

    private short[] getData() throws Exception {
    	ObjectInputStream in = new ObjectInputStream(new FileInputStream("data/volume/volume.ser"));
        short[] inArray = (short[]) in.readObject();
        in.close();
        return inArray;
    }
}