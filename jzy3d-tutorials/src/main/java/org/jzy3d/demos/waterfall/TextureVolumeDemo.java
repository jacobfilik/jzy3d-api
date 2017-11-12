package org.jzy3d.demos.waterfall;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.textures.SimpleAlphaMapper;
import org.jzy3d.plot3d.rendering.textures.Volume2DTexture;

public class TextureVolumeDemo extends AbstractAnalysis {
    public static void main(String[] args) throws Exception {
        AnalysisLauncher.open(new TextureVolumeDemo());
    }

    @Override
    public void init() {

    	chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType());

    	chart.getView();

    	int[] shape = {99,99,90};
    	short[] s = null;
    	
    	try {
    		s = getData();
    	} catch (Exception e) {
    		e.printStackTrace();
			return;
		}
    	
    	Coord3d start = new Coord3d(5, 5, 5);
    	Coord3d stop = new Coord3d(10, 10, 10);
    	SimpleAlphaMapper am = new SimpleAlphaMapper(25,1000);
    	ColorMapper mapper = new ColorMapper(new ColorMapRainbow(), 25, 128);
    	Volume2DTexture volume = new Volume2DTexture(s,shape,start,stop,mapper,am);

    	chart.getScene().getGraph().add(volume);
    	
    }

    private short[] getData() throws Exception {
    	ObjectInputStream in = new ObjectInputStream(new FileInputStream("data/volume/volume.ser"));
        short[] inArray = (short[]) in.readObject();
        in.close();
        return inArray;
    }
}