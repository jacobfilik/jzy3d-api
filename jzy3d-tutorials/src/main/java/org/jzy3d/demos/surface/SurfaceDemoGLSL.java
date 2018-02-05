package org.jzy3d.demos.surface;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Quad;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axes.AxeXRectangleAnnotation.PolygonMode;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class SurfaceDemoGLSL extends AbstractAnalysis {
    public static void main(String[] args) throws Exception {
        AnalysisLauncher.open(new SurfaceDemoGLSL());
    }

    @Override
    public void init() {
        // Define a function to plot

    	QuadGLSL q = new QuadGLSL();
    	q.add(new Point(new Coord3d(0, 0, 0)));
    	q.add(new Point(new Coord3d(0, 1, 0)));
    	q.add(new Point(new Coord3d(1, 1, 1)));
    	q.add(new Point(new Coord3d(0, 0, 1)));
    	q.setColor(new Color(255,0,0));
    	q.setWireframeColor(new Color(255,0,0));

        // Create a chart
        chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType());
        chart.getScene().getGraph().add(q);
    }
}
