package projeto3d2;

import javax.vecmath.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.*;

public class FiguraRotativa extends Shape3D{
   public FiguraRotativa() {
    GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
    Point3d[] vertices = {new Point3d(0f,0f,0f), new Point3d(1f,0f,0f), new Point3d(1f,1f,0f), new Point3d(0f,0f,1f),
    		new Point3d(0.5f, 1f, 0.5f), new Point3d(0.5f, -1f, 0.5f)
    };
    int[] indices = {4,1,0, 1,2,4, 2,3,4, 3,0,4,
    		1,5,2, 5,2,3, 3,5,0, 0,1,5
    		};
    gi.setCoordinates(vertices);
    gi.setCoordinateIndices(indices);
    int[] stripCounts = {3,3,3,3,3,3,3,3};
    gi.setStripCounts(stripCounts);
    
    
    NormalGenerator ng = new NormalGenerator();
    ng.generateNormals(gi);
    
    this.setGeometry(gi.getGeometryArray());
  }
}
