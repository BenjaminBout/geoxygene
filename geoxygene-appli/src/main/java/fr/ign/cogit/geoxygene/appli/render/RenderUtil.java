/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.apache.batik.gvt.GraphicsNode;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.filter.FilterFactoryImpl;
import org.geotools.renderer.lite.gridcoverage2d.GridCoverageRenderer;
import org.geotools.styling.StyleBuilder;

import com.vividsolutions.jts.geom.Envelope;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.stroke.TextStroke;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangulationJTS;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_Coverage;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.style.AnchorPoint;
import fr.ign.cogit.geoxygene.style.Displacement;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.GraphicFill;
import fr.ign.cogit.geoxygene.style.GraphicStroke;
import fr.ign.cogit.geoxygene.style.LabelPlacement;
import fr.ign.cogit.geoxygene.style.LinePlacement;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.Placement;
import fr.ign.cogit.geoxygene.style.PointPlacement;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.style.thematic.DiagramSymbolizer;
import fr.ign.cogit.geoxygene.style.thematic.ThematicClass;
import fr.ign.cogit.geoxygene.style.thematic.ThematicSymbolizer;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * @author Julien Perret
 * 
 */
public final class RenderUtil {
  /**
   * Private constructor. Should not be used.
   */
  private RenderUtil() {
  }
  
  /**
   * Draw a geometry on the given graphics.
   * @param geometry the geometry
   * @param viewport the viewport
   * @param graphics the graphics
   */
  @SuppressWarnings("unchecked")
  public static void draw(final IGeometry geometry, final Viewport viewport,
      final Graphics2D graphics, double opacity) {
    if (geometry.isPolygon()) {
      GM_Polygon polygon = (GM_Polygon) geometry;
      try {
        Shape shape = viewport.toShape(polygon.exteriorLineString());
        if (shape != null) {
          graphics.draw(shape);
        }
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
      for (int i = 0; i < polygon.sizeInterior(); i++) {
        try {
          Shape shape = viewport.toShape(polygon.interiorLineString(i));
          if (shape != null) {
            graphics.draw(shape);
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      }
    } else {
      if (geometry.isMultiSurface() || geometry.isMultiCurve()) {
        GM_Aggregate<IGeometry> aggregate = (GM_Aggregate<IGeometry>) geometry;
        for (IGeometry element : aggregate) {
          RenderUtil.draw(element, viewport, graphics, opacity);
        }
      } else {
        try {
          Shape shape = viewport.toShape(geometry);
          if (shape != null) {
            graphics.draw(shape);
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * @param geometry geometry to fill
   * @param viewport viewport
   * @param graphics graphics to draw into
   */
  @SuppressWarnings("unchecked")
  public static void fill(final IGeometry geometry, final Viewport viewport,
      final Graphics2D graphics, double opacity) {
    if (geometry.isPolygon()) {
      try {
        Shape shape = viewport.toShape(geometry);
        if (shape != null) {
          graphics.fill(shape);
        }
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    } else {
      if (geometry.isMultiSurface()) {
        GM_Aggregate<IGeometry> aggregate = (GM_Aggregate<IGeometry>) geometry;
        for (IGeometry element : aggregate) {
          RenderUtil.fill(element, viewport, graphics, opacity);
        }
      }
    }
  }
  
  public static void paint(Symbolizer symbolizer, IFeature feature, Viewport viewport, Graphics2D graphics, double opacity) {
      if (PointSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
          paint((PointSymbolizer) symbolizer, feature, viewport, graphics, opacity);
          return;
      }
      if (LineSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
          paint((LineSymbolizer) symbolizer, feature, viewport, graphics, opacity);
          return;
      }
      if (PolygonSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
          paint((PolygonSymbolizer) symbolizer, feature, viewport, graphics, opacity);
          return;
      }
      if (RasterSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
          paint((RasterSymbolizer) symbolizer, feature, viewport, graphics, opacity);
          return;
      }
      if (TextSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
          paint((TextSymbolizer) symbolizer, feature, viewport, graphics, opacity);
          return;
      }
      if (ThematicSymbolizer.class.isAssignableFrom(symbolizer.getClass())) {
          paint((ThematicSymbolizer) symbolizer, feature, viewport, graphics, opacity);
          return;
      }
      if (feature.getGeom() == null) {
          return;
      }
      if (feature.getGeom().isPolygon() || feature.getGeom().isMultiSurface()) {
          graphics.setColor(new Color(1f, 1f, 0f, 0.5f));
          RenderUtil.fill(feature.getGeom(), viewport, graphics, opacity);
      }
      java.awt.Stroke bs = new BasicStroke(
              2f, BasicStroke.CAP_SQUARE,
              BasicStroke.JOIN_MITER);
      graphics.setColor(new Color(1f, 1f, 0f, 1f));
      graphics.setStroke(bs);
      RenderUtil.draw(feature.getGeom(), viewport, graphics, opacity);
      try {
          graphics.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE,
                  BasicStroke.JOIN_MITER));
          for (IDirectPosition position : viewport
                  .toViewDirectPositionList(feature.getGeom().coord())) {
              GeneralPath shape = new GeneralPath();
              shape.moveTo(
                      position.getX() - 2,
                      position.getY() - 2);
              shape.lineTo(
                      position.getX() + 2,
                      position.getY() - 2);
              shape.lineTo(
                      position.getX() + 2,
                      position.getY() + 2);
              shape.lineTo(
                      position.getX() - 2,
                      position.getY() + 2);
              shape.lineTo(
                      position.getX() - 2,
                      position.getY() - 2);
              graphics.setColor(new Color(1f, 1f, 0f, 1f));
              graphics.fill(shape);
              graphics.setColor(Color.black);
              graphics.draw(shape);
          }
      } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
      }
  }

  
  public static void paint(PointSymbolizer symbolizer, IFeature feature, Viewport viewport, Graphics2D graphics, double opacity) {
      if (symbolizer.getGraphic() == null) {
        return;
      }
      Point2D point;
      IGeometry geometry = feature.getGeom();
      if (symbolizer.getGeometryPropertyName() != null
          && !symbolizer.getGeometryPropertyName().equalsIgnoreCase("geom")) { //$NON-NLS-1$
        geometry = (IGeometry) feature.getAttribute(symbolizer
            .getGeometryPropertyName());
      }
      if (geometry == null) {
        return;
      }
      try {
        point = viewport.toViewPoint(geometry.centroid());
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
        return;
      }
      for (Mark mark : symbolizer.getGraphic().getMarks()) {
        Shape markShape = mark.toShape();
        float size = symbolizer.getGraphic().getSize();
        double scale = 1;
        if (!symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
          try {
            scale = viewport.getModelToViewTransform().getScaleX();
          } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
          }
        }
        size *= scale;
        AffineTransform at = AffineTransform.getTranslateInstance(point.getX(),
            point.getY());
        at.rotate(symbolizer.getGraphic().getRotation());
        at.scale(size, size);
        markShape = at.createTransformedShape(markShape);

        if (symbolizer.getColorMap() != null) {
          try {
            graphics.setColor(getColorWithOpacity(new Color(symbolizer.getColorMap().getColor(
                (Double.parseDouble(feature.getAttribute(
                    symbolizer.getColorMap().getPropertyName()).toString())))), opacity));
          } catch (NumberFormatException e) {
          }
        } else {
            Color color = (mark.getFill() == null) ? Color.gray : mark.getFill()
                    .getColor();
          graphics.setColor(getColorWithOpacity(color, opacity));
        }
        graphics.fill(markShape);
        graphics.setStroke(mark.getStroke().toAwtStroke((float) scale));
        Color color = (mark.getStroke() == null) ? Color.black : mark
                .getStroke().getColor();
        graphics.setColor(getColorWithOpacity(color, opacity));
        graphics.draw(markShape);
      }
      for (ExternalGraphic theGraphic : symbolizer.getGraphic().getExternalGraphics()) {
        Image onlineImage = theGraphic.getOnlineResource();
        graphics.drawImage(onlineImage,
            (int) point.getX() - onlineImage.getWidth(null) / 2,
            (int) point.getY() - onlineImage.getHeight(null) / 2, null);
      }
    }
  public static void paint(LineSymbolizer symbolizer, IFeature feature, Viewport viewport, Graphics2D graphics, double opacity) {
    IGeometry geometry = feature.getGeom();
    if (symbolizer.getGeometryPropertyName() != null
        && !symbolizer.getGeometryPropertyName().equalsIgnoreCase("geom")) { //$NON-NLS-1$
      geometry = (IGeometry) feature.getAttribute(symbolizer
          .getGeometryPropertyName());
    }
    if (geometry == null) {
      return;
    }
    if (symbolizer.getStroke() == null) {
      return;
    }
    double scaleUOMToPixels = 1;
    if (!symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
      try {
        scaleUOMToPixels = viewport.getModelToViewTransform().getScaleX();
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    }
    graphics.setStroke(symbolizer.getStroke().toAwtStroke((float) scaleUOMToPixels));
    paintShadow(symbolizer, geometry, viewport, graphics, opacity);
    if (symbolizer.getStroke().getGraphicType() == null) {
      List<Shape> shapes = getShapeList(symbolizer, geometry, viewport, false);
      if (shapes != null) {
        if (symbolizer.getColorMap() != null) {
          try {
            graphics.setColor(getColorWithOpacity(new Color(symbolizer.getColorMap().getColor(
                (Double.parseDouble(feature.getAttribute(
                    symbolizer.getColorMap().getPropertyName()).toString())))), opacity));
          } catch (NumberFormatException e) {
          }
        } else {
          graphics.setColor(getColorWithOpacity(symbolizer.getStroke().getColor(), opacity));
        }
        for (Shape shape : shapes) {
          graphics.draw(shape);
        }
      }
    } else {
      if (symbolizer.getStroke().getGraphicType().getClass()
          .isAssignableFrom(GraphicFill.class)) {
        List<Shape> shapes = getShapeList(symbolizer, geometry, viewport, true);
        // GraphicFill
        List<Graphic> graphicList = ((GraphicFill) symbolizer.getStroke()
            .getGraphicType()).getGraphics();
        for (Graphic graphic : graphicList) {
          for (Shape shape : shapes) {
            graphicFillLineString(symbolizer, shape, graphic, viewport, graphics, opacity);
          }
        }
      } else {
        // GraphicStroke
        List<Shape> shapes = getShapeList(symbolizer, geometry, viewport, false);
        if (shapes != null) {
          List<Graphic> graphicList = ((GraphicStroke) symbolizer.getStroke()
                  .getGraphicType()).getGraphics();
          for (Graphic graphic : graphicList) {
            for (Shape shape : shapes) {
              graphicStrokeLineString(symbolizer, shape, graphic, viewport, graphics, opacity);
            }
          }
        }
      }
    }
  }

  /**
   * @param geometry a geometry
   * @param viewport the viewport in which to view it
   * @param fill true if the stroke width should be used to build the shapes, ie
   *          if they will be used for graphic fill
   * @return the list of awt shapes corresponding to the given geometry
   */
  @SuppressWarnings("unchecked")
  private static List<Shape> getShapeList(LineSymbolizer symbolizer, IGeometry geometry, Viewport viewport,
      boolean fill) {
    double scaleSymbolizerUOMToDataUOM = 1;
    if (symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
      try {
        scaleSymbolizerUOMToDataUOM = 1 / viewport.getModelToViewTransform().getScaleX();
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    }
    if (ICurve.class.isAssignableFrom(geometry.getClass())
        || IPolygon.class.isAssignableFrom(geometry.getClass())) {
      ICurve curve = ((ICurve.class.isAssignableFrom(geometry
          .getClass())) ? ((ICurve) geometry)
          : ((IPolygon) geometry).exteriorLineString());
      if (symbolizer.getPerpendicularOffset() != 0) {
        IMultiCurve<ILineString> offsetCurve = JtsAlgorithms.offsetCurve(curve,
            symbolizer.getPerpendicularOffset() * scaleSymbolizerUOMToDataUOM);
        List<Shape> shapes = new ArrayList<Shape>();
        for (ILineString l : offsetCurve) {
          shapes.addAll(getLineStringShapeList(symbolizer, l, viewport, fill, scaleSymbolizerUOMToDataUOM));
        }
        return shapes;
      }
      return getLineStringShapeList(symbolizer, curve, viewport, fill, scaleSymbolizerUOMToDataUOM);
    }
    if (geometry.isMultiCurve()) {
      List<Shape> shapes = new ArrayList<Shape>();
      for (IOrientableCurve line : (IMultiCurve<IOrientableCurve>) geometry) {
        if (symbolizer.getPerpendicularOffset() != 0) {
          IMultiCurve<ILineString> offsetCurve = JtsAlgorithms.offsetCurve(
              (ILineString) line, symbolizer.getPerpendicularOffset() * scaleSymbolizerUOMToDataUOM);
          for (ILineString l : offsetCurve) {
            shapes.addAll(getLineStringShapeList(symbolizer, l, viewport, fill, scaleSymbolizerUOMToDataUOM));
          }
        } else {
          shapes.addAll(getLineStringShapeList(symbolizer, line, viewport, fill, scaleSymbolizerUOMToDataUOM));
        }
      }
      return shapes;
    }
    if (geometry.isMultiSurface()) {
      List<Shape> shapes = new ArrayList<Shape>();
      for (IOrientableSurface surface : ((IMultiSurface<IOrientableSurface>) geometry)
          .getList()) {
        try {
          Shape shape = viewport.toShape(fill ? surface.buffer(symbolizer.getStroke()
              .getStrokeWidth() / 2) : surface);
          if (shape != null) {
            shapes.add(shape);
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      }
      return shapes;
    }
    return null;
  }

  /**
   * @param symbolizer a line symbolizer
   * @param line the geometry of the line
   * @param viewport the viewport used for rendering
   * @param fill true if the stroke width should be used to build the shapes, ie
   *          if they will be used for graphic fill
   * @param scale scale to go from the symbolizer's uom to the data uom
   * @return
   */
  private static List<Shape> getLineStringShapeList(LineSymbolizer symbolizer, IOrientableCurve line,
      Viewport viewport, boolean fill, double scale) {
    List<Shape> shapes = new ArrayList<Shape>();
    try {
      Shape shape = viewport.toShape(fill ? line.buffer(symbolizer.getStroke()
          .getStrokeWidth() * 0.5 * scale) : line);
      if (shape != null) {
        shapes.add(shape);
      }
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    return shapes;
  }

  @SuppressWarnings("unchecked")
  private static void paintShadow(LineSymbolizer symbolizer, IGeometry geometry, Viewport viewport,
      Graphics2D graphics, double opacity) {
    if (symbolizer.getShadow() != null) {
      Color shadowColor = getColorWithOpacity(symbolizer.getShadow().getColor(), opacity);
      double translate_x = -5;
      double translate_y = -5;
      if (symbolizer.getShadow().getDisplacement() != null) {
        translate_x = symbolizer.getShadow().getDisplacement().getDisplacementX();
        translate_y = symbolizer.getShadow().getDisplacement().getDisplacementY();
      }
      graphics.setColor(shadowColor);
      List<Shape> shapes = new ArrayList<Shape>();
      if (geometry.isLineString()) {
        try {
          Shape shape = viewport.toShape(geometry.translate(translate_x,
              translate_y, 0));
          if (shape != null) {
            shapes.add(shape);
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      } else {
        if (geometry.isMultiCurve()) {
          for (GM_OrientableCurve line : (GM_MultiCurve<GM_OrientableCurve>) geometry) {
            try {
              Shape shape = viewport.toShape(line.translate(translate_x,
                  translate_y, 0));
              if (shape != null) {
                shapes.add(shape);
              }
            } catch (NoninvertibleTransformException e) {
              e.printStackTrace();
            }
          }
        }
      }
      for (Shape shape : shapes) {
          graphics.draw(shape);
      }
    }
  }

  private static void graphicFillLineString(LineSymbolizer symbolizer, Shape shape, Graphic graphic,
      Viewport viewport, Graphics2D graphics, double opacity) {
    if (shape == null || viewport == null || graphic == null) {
      return;
    }
    float size = graphic.getSize();
    graphics.setClip(shape);
    for (ExternalGraphic external : graphic.getExternalGraphics()) {
      if (external.getFormat().contains("png") || external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
        Image image = external.getOnlineResource();
        graphicFillLineString(symbolizer, shape, image, size, graphics, opacity);
      } else {
        if (external.getFormat().contains("svg")) { //$NON-NLS-1$
          GraphicsNode node = external.getGraphicsNode();
          graphicFillLineString(symbolizer, shape, node, size, graphics, opacity);
        }
      }
      return;
    }
    List<Shape> shapes = new ArrayList<Shape>(graphic.getMarks().size());
    for (Mark mark : graphic.getMarks()) {
      shapes.add(mark.toShape());
      graphics.setColor(getColorWithOpacity(mark.getFill().getColor(), opacity));
    }
    double width = shape.getBounds2D().getWidth();
    double height = shape.getBounds2D().getHeight();
    int xSize = (int) Math.ceil(width / size);
    int ySize = (int) Math.ceil(height / size);
    AffineTransform scaleTransform = AffineTransform.getScaleInstance(size,
        size);
    for (int i = 0; i < xSize; i++) {
      for (int j = 0; j < ySize; j++) {
        AffineTransform transform = AffineTransform.getTranslateInstance(
            (i + 0.5) * size + shape.getBounds2D().getMinX(), (j + 0.5) * size
                + shape.getBounds2D().getMinY());
        transform.concatenate(scaleTransform);
        for (Shape markShape : shapes) {
          Shape tranlatedShape = transform.createTransformedShape(markShape);
          graphics.fill(tranlatedShape);
        }
      }
    }
  }

  private static void graphicFillLineString(LineSymbolizer symbolizer, Shape shape, GraphicsNode node,
      float size, Graphics2D graphics, double opacity) {
    AffineTransform translate = AffineTransform.getTranslateInstance(-node
        .getBounds().getMinX(), -node.getBounds().getMinY());
    node.setTransform(translate);
    BufferedImage buff = new BufferedImage((int) node.getBounds().getWidth(),
        (int) node.getBounds().getHeight(), BufferedImage.TYPE_INT_ARGB);
    node.paint((Graphics2D) buff.getGraphics());
    graphicFillLineString(symbolizer, shape, buff, size, graphics, opacity);
  }

  private static void graphicFillLineString(LineSymbolizer symbolizer, Shape shape, Image image, float size,
      Graphics2D graphics, double opacity) {
    Double width = new Double(Math.max(1, shape.getBounds2D().getWidth()));
    Double height = new Double(Math.max(1, shape.getBounds2D().getHeight()));
    Double shapeHeight = new Double(size);
    double factor = shapeHeight.doubleValue() / image.getHeight(null);
    Double shapeWidth = new Double(image.getWidth(null) * factor);
    AffineTransform transform = AffineTransform.getTranslateInstance(shape
        .getBounds2D().getMinX(), shape.getBounds2D().getMinY());
    Image scaledImage = image.getScaledInstance(shapeWidth.intValue(),
        shapeHeight.intValue(), Image.SCALE_FAST);
    BufferedImage buff = new BufferedImage(shapeWidth.intValue(),
        shapeHeight.intValue(), BufferedImage.TYPE_INT_ARGB);
    buff.getGraphics().drawImage(scaledImage, 0, 0, null);
    ParameterBlock p = new ParameterBlock();
    p.addSource(buff);
    p.add(width.intValue());
    p.add(height.intValue());
    RenderedOp im = JAI.create("pattern", p);//$NON-NLS-1$
    BufferedImage bufferedImage = im.getAsBufferedImage();
    graphics.drawImage(bufferedImage, transform, null);
    bufferedImage.flush();
    im.dispose();
    scaledImage.flush();
    buff.flush();
  }

  private static void graphicStrokeLineString(LineSymbolizer symbolizer, Shape shape, Graphic graphic,
      Viewport viewport, Graphics2D graphics, double opacity) {
    if (shape == null || viewport == null || graphic == null) {
      return;
    }
    float size = graphic.getSize();
    // graphics.setClip(shape);
    for (ExternalGraphic external : graphic.getExternalGraphics()) {
      if (external.getFormat().contains("png") || external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
        Image image = external.getOnlineResource();
        graphicStrokeLineString(symbolizer, shape, image, size, graphics, opacity);
      } else {
        if (external.getFormat().contains("svg")) { //$NON-NLS-1$
          GraphicsNode node = external.getGraphicsNode();
          graphicStrokeLineString(symbolizer, shape, node, size, graphics, opacity);
        }
      }
      return;
    }
    List<Shape> shapes = new ArrayList<Shape>();
    for (Mark mark : graphic.getMarks()) {
      shapes.add(mark.toShape());
      graphics.setColor(getColorWithOpacity(mark.getFill().getColor(), opacity));
    }
    List<AffineTransform> transforms = getGraphicStrokeLineStringTransforms(symbolizer, shape, size, 1, 1);
    for (AffineTransform t : transforms) {
      for (Shape markShape : shapes) {
        Shape tranlatedShape = t.createTransformedShape(markShape);
        graphics.fill(tranlatedShape);
      }
    }
  }

  private static void graphicStrokeLineString(LineSymbolizer symbolizer, Shape shape, GraphicsNode node,
      float size, Graphics2D graphics, double opacity) {
    double width = node.getBounds().getWidth();
    double height = node.getBounds().getHeight();
    List<AffineTransform> transforms = 
        getGraphicStrokeLineStringTransforms(symbolizer, shape, size, width, height);
    for (AffineTransform t : transforms) {
      AffineTransform tr = AffineTransform.getTranslateInstance(-node
          .getBounds().getMinX(), -node.getBounds().getMinY());
      t.concatenate(tr);
      node.setTransform(t);
      node.paint(graphics);
    }
  }

  private static void graphicStrokeLineString(LineSymbolizer symbolizer, Shape shape, Image image, float size,
      Graphics2D graphics, double opacity) {
    List<AffineTransform> transforms = 
        getGraphicStrokeLineStringTransforms(symbolizer, shape, size,
            image.getWidth(null), image.getHeight(null));
    for (AffineTransform t : transforms) {
      graphics.drawImage(image, t, null);
    }
  }

  private static List<AffineTransform> getGraphicStrokeLineStringTransforms(
          LineSymbolizer symbolizer, Shape shape, float size, double width, double height) {
    List<AffineTransform> transforms = new ArrayList<AffineTransform>();
    double shapeHeight = size;
    double factor = shapeHeight / height;
    double shapeWidth = width * factor;
    AffineTransform scaleTransform = AffineTransform.getScaleInstance(factor,
        factor);
    AffineTransform translation = AffineTransform.getTranslateInstance(-(0.5)
        * width, -(0.5) * height);
    GeneralPath path = (GeneralPath) shape;
    PathIterator pathIterator = path.getPathIterator(null);
    IDirectPositionList points = new DirectPositionList();
    while (!pathIterator.isDone()) {
      double[] coords = new double[6];
      int type = pathIterator.currentSegment(coords);
      if (type == PathIterator.SEG_LINETO || type == PathIterator.SEG_MOVETO) {
        points.add(new DirectPosition(coords[0], coords[1]));
      }
      pathIterator.next();
    }
    ILineString line = Operateurs.resampling(new GM_LineString(points),
        shapeWidth);
    for (int i = 0; i < line.sizeControlPoint() - 1; i++) {
      IDirectPosition p1 = line.getControlPoint(i);
      IDirectPosition p2 = line.getControlPoint(i + 1);
      IDirectPosition p = new DirectPosition((p1.getX() + p2.getX()) / 2,
          (p1.getY() + p2.getY()) / 2);
      AffineTransform transform = AffineTransform.getTranslateInstance(
          p.getX(), p.getY());
      transform.concatenate(scaleTransform);
      transform.concatenate(AffineTransform.getRotateInstance(new Angle(p1, p2)
          .getValeur()));
      transform.concatenate(translation);
      transforms.add(transform);
    }
    return transforms;
  }
  
  public static void paint(PolygonSymbolizer symbolizer, IFeature feature, Viewport viewport, Graphics2D graphics, double opacity) {
    if (feature.getGeom() == null || viewport == null) {
      return;
    }
    if (symbolizer.getShadow() != null) {
      Color shadowColor = getColorWithOpacity(symbolizer.getShadow().getColor(), opacity);
      double translate_x = -5;
      double translate_y = -5;
      if (symbolizer.getShadow().getDisplacement() != null) {
        translate_x = symbolizer.getShadow().getDisplacement().getDisplacementX();
        translate_y = symbolizer.getShadow().getDisplacement().getDisplacementY();
      }
      graphics.setColor(shadowColor);
      List<Shape> shapes = new ArrayList<Shape>();
      if (feature.getGeom().isPolygon()) {
        try {
          Shape shape = viewport.toShape(feature.getGeom().translate(
              translate_x, translate_y, 0));
          if (shape != null) {
            shapes.add(shape);
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      } else {
        if (GM_MultiSurface.class.isAssignableFrom(feature.getGeom().getClass())) {
          try {
            Shape shape = viewport.toShape(feature.getGeom().translate(
                translate_x, translate_y, 0));
            if (shape != null) {
              shapes.add(shape);
            }
          } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
          }
        }
      }
      for (Shape shape : shapes) {
        fillPolygon(symbolizer, shape, viewport, graphics, opacity);
      }
    }
    Color fillColor = null;
    float fillOpacity = 1f;
    if (symbolizer.getFill() != null) {
      fillColor = getColorWithOpacity(symbolizer.getFill().getColor(), opacity);
      fillOpacity = symbolizer.getFill().getFillOpacity() * (float) opacity;
    }
    if (symbolizer.getColorMap() != null
        && symbolizer.getColorMap().getInterpolate() != null) {
      double value = ((Number) feature.getAttribute(symbolizer.getColorMap()
          .getInterpolate().getLookupvalue())).doubleValue();
      int rgb = symbolizer.getColorMap().getColor(value);
      fillColor = getColorWithOpacity(new Color(rgb), opacity);
    }
    if (fillColor != null && fillOpacity > 0f) {
      graphics.setColor(fillColor);
      List<Shape> shapes = new ArrayList<Shape>();
      if (feature.getGeom().isPolygon()) {
        try {
          Shape shape = viewport.toShape(feature.getGeom());
          if (shape != null) {
            shapes.add(shape);
          }
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      } else {
        if (GM_MultiSurface.class.isAssignableFrom(feature.getGeom().getClass())) {
          for (IOrientableSurface surface : ((GM_MultiSurface<?>) feature
              .getGeom())) {
            try {
              Shape shape = viewport.toShape(surface);
              if (shape != null) {
                shapes.add(shape);
              }
            } catch (NoninvertibleTransformException e) {
              e.printStackTrace();
            }
          }
        }
      }
      for (Shape shape : shapes) {
        if (symbolizer.getFill() == null || symbolizer.getFill().getGraphicFill() == null) {
          fillPolygon(symbolizer, shape, viewport, graphics, opacity);
        } else {
          List<Graphic> graphicList = symbolizer.getFill().getGraphicFill()
              .getGraphics();
          for (Graphic graphic : graphicList) {
            graphicFillPolygon(symbolizer, shape, graphic, viewport, graphics, opacity);
          }
        }
      }
    }
    if (symbolizer.getStroke() != null) {
      if (symbolizer.getStroke().getStrokeLineJoin() == BasicStroke.JOIN_MITER) {
          symbolizer.getStroke().setStrokeLineCap(BasicStroke.CAP_SQUARE);
      } else if (symbolizer.getStroke().getStrokeLineJoin() == BasicStroke.JOIN_BEVEL) {
          symbolizer.getStroke().setStrokeLineCap(BasicStroke.CAP_BUTT);
      } else if (symbolizer.getStroke().getStrokeLineJoin() == BasicStroke.JOIN_ROUND) {
          symbolizer.getStroke().setStrokeLineCap(BasicStroke.CAP_ROUND);
      } else {
//        AbstractSymbolizer.logger.error("Stroke Line Join undefined."); //$NON-NLS-1$
      }
      float strokeOpacity = symbolizer.getStroke().getStrokeOpacity();
      if (symbolizer.getStroke().getGraphicType() == null && strokeOpacity > 0f) {
        // Solid color
        Color color = getColorWithOpacity(symbolizer.getStroke().getColor(), opacity);
        double scale = 1;
        if (!symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
          try {
            scale = viewport.getModelToViewTransform().getScaleX();
          } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
          }
        }
        BasicStroke bs = (BasicStroke) symbolizer.getStroke().toAwtStroke(
            (float) scale);
        graphics.setColor(color);
        if (feature.getGeom().isPolygon()) {
          drawPolygon(symbolizer, (GM_Polygon) feature.getGeom(), viewport, graphics,
              bs, opacity);
        } else {
          if (GM_MultiSurface.class.isAssignableFrom(feature.getGeom().getClass())) {
                for (IOrientableSurface surface : ((GM_MultiSurface<?>) feature
                .getGeom())) {
              drawPolygon(symbolizer, (GM_Polygon) surface, viewport, graphics, bs, opacity);
            }
          }
        }
      }
    }
  }

  private static void graphicFillPolygon(PolygonSymbolizer symbolizer, Shape shape, Graphic graphic,
      Viewport viewport, Graphics2D graphics, double opacity) {
    if (shape == null || viewport == null || graphic == null) {
      return;
    }
    float size = graphic.getSize();
    graphics.setClip(shape);
    for (ExternalGraphic external : graphic.getExternalGraphics()) {
      if (external.getFormat().contains("png") || external.getFormat().contains("gif")) { //$NON-NLS-1$ //$NON-NLS-2$
        Image image = external.getOnlineResource();
        graphicFillPolygon(symbolizer, shape, image, size, graphics, opacity);
      } else {
        if (external.getFormat().contains("svg")) { //$NON-NLS-1$
          GraphicsNode node = external.getGraphicsNode();
          graphicFillPolygon(symbolizer, shape, node, size, graphics, opacity);
        }
      }
      return;
    }
    int markShapeSize = 200;
    for (Mark mark : graphic.getMarks()) {
      Shape markShape = mark.toShape();
      AffineTransform translate = AffineTransform.getTranslateInstance(
          markShapeSize / 2, markShapeSize / 2);
      if (graphic.getRotation() != 0) {
        AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI
            * graphic.getRotation() / 180.0);
        translate.concatenate(rotate);
      }
      AffineTransform scaleTransform = AffineTransform.getScaleInstance(
          markShapeSize, markShapeSize);
      translate.concatenate(scaleTransform);
      Shape tranlatedShape = translate.createTransformedShape(markShape);
      BufferedImage buff = new BufferedImage(markShapeSize, markShapeSize,
          BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D) buff.getGraphics();
      g.setColor(getColorWithOpacity(mark.getFill().getColor(), opacity));
      g.fill(tranlatedShape);
      graphicFillPolygon(symbolizer, shape, buff, size, graphics, opacity);
    }
  }

  private static void graphicFillPolygon(PolygonSymbolizer symbolizer, Shape shape, Image image, float size,
      Graphics2D graphics, double opacity) {
    Double width = new Double(Math.max(1, shape.getBounds2D().getWidth()));
    Double height = new Double(Math.max(1, shape.getBounds2D().getHeight()));
    Double shapeHeight = new Double(size);
    double factor = shapeHeight / image.getHeight(null);
    Double shapeWidth = new Double(Math.max(image.getWidth(null) * factor, 1));
    AffineTransform transform = AffineTransform.getTranslateInstance(shape
        .getBounds2D().getMinX(), shape.getBounds2D().getMinY());
    Image scaledImage = image.getScaledInstance(shapeWidth.intValue(),
        shapeHeight.intValue(), Image.SCALE_FAST);
    BufferedImage buff = new BufferedImage(shapeWidth.intValue(),
        shapeHeight.intValue(), BufferedImage.TYPE_INT_ARGB);
    buff.getGraphics().drawImage(scaledImage, 0, 0, null);
    ParameterBlock p = new ParameterBlock();
    p.addSource(buff);
    p.add(width.intValue());
    p.add(height.intValue());
    RenderedOp im = JAI.create("pattern", p);//$NON-NLS-1$
    BufferedImage bufferedImage = im.getAsBufferedImage();
    graphics.drawImage(bufferedImage, transform, null);
    bufferedImage.flush();
    im.dispose();
    scaledImage.flush();
    buff.flush();
  }

  private static void graphicFillPolygon(PolygonSymbolizer symbolizer, Shape shape, GraphicsNode node, float size,
      Graphics2D graphics, double opacity) {
    AffineTransform translate = AffineTransform.getTranslateInstance(-node
        .getBounds().getMinX(), -node.getBounds().getMinY());
    node.setTransform(translate);
    BufferedImage buff = new BufferedImage((int) node.getBounds().getWidth(),
        (int) node.getBounds().getHeight(), BufferedImage.TYPE_INT_ARGB);
    node.paint((Graphics2D) buff.getGraphics());
    graphicFillPolygon(symbolizer, shape, buff, size, graphics, opacity);
  }

  private static void fillPolygon(PolygonSymbolizer symbolizer, Shape shape, Viewport viewport, Graphics2D graphics, double opacity) {
    if (shape == null || viewport == null) {
      return;
    }
    float[] symbolizerColorComponenents = symbolizer.getFill().getColor().getComponents(null);
    Color color = new Color(symbolizerColorComponenents[0],
            symbolizerColorComponenents[1],
            symbolizerColorComponenents[2],
            symbolizerColorComponenents[3] * (float) opacity);
    graphics.setColor(color);
    graphics.fill(shape);
   
  }

  private static void drawPolygon(PolygonSymbolizer symbolizer, GM_Polygon polygon, Viewport viewport,
      Graphics2D graphics, BasicStroke stroke, double opacity) {
    if (polygon == null || viewport == null) {
      return;
    }
    List<Shape> shapes = new ArrayList<Shape>(0);
    try {
      Shape shape = viewport.toShape(polygon.getExterior());
      if (shape != null) {
        shapes.add(shape);
      } else {
//        if (AbstractSymbolizer.logger.isTraceEnabled()) {
//          AbstractSymbolizer.logger.trace("null shape for " + polygon); //$NON-NLS-1$
//          AbstractSymbolizer.logger
//              .trace("ring = " + polygon.exteriorLineString()); //$NON-NLS-1$
//        }
      }
      for (IRing ring : polygon.getInterior()) {
        shape = viewport.toShape(ring);
        if (shape != null) {
          shapes.add(shape);
        } else {
//          if (AbstractSymbolizer.logger.isTraceEnabled()) {
//            AbstractSymbolizer.logger.trace("null shape for " + polygon); //$NON-NLS-1$
//            AbstractSymbolizer.logger.trace("ring = " + ring); //$NON-NLS-1$
//          }
        }
      }
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    for (Shape shape : shapes) {
      Shape outline = stroke.createStrokedShape(shape);
//      graphics.draw(shape);
      graphics.setColor(getColorWithOpacity(symbolizer.getStroke().getColor(), opacity));
      graphics.fill(outline);
    }
  }
  /**
   * A color with the specified opacity applied to the given color.
 * @param color the input color
 * @param opacity the opacity
 * @return a new color with the specified opacity applied to the given color
 */
private static Color getColorWithOpacity(Color color, double opacity) {
      float[] symbolizerColorComponenents = color.getComponents(null);
      return new Color(symbolizerColorComponenents[0],
              symbolizerColorComponenents[1],
              symbolizerColorComponenents[2],
              symbolizerColorComponenents[3] * (float) opacity);
  }
  /**
   * @param obj The FT_coverage to render
   * This method shall be reworked.
   */
  public static void paint(RasterSymbolizer symbolizer, IFeature obj, Viewport viewport, Graphics2D graphics, double opacity) {
      FT_Coverage fcoverage = (FT_Coverage) obj;
        try {
            GridCoverage2D coverage = fcoverage.coverage();
            IEnvelope view = viewport.getEnvelopeInModelCoordinates();
            Envelope renderEnvelope = new Envelope(view.minX(), view.maxX(),
                    view.minY(), view.maxY());
            GridCoverageRenderer renderer = new GridCoverageRenderer(
                    coverage.getCoordinateReferenceSystem(), renderEnvelope,
                    viewport.getLayerViewPanels().iterator().next()
                            .getVisibleRect(), null);
            org.geotools.styling.RasterSymbolizer s = new StyleBuilder().createRasterSymbolizer();
            s.setOpacity((new FilterFactoryImpl()).literal(opacity * symbolizer.getOpacity()));
            renderer.paint(graphics, coverage, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
      return;
  }
  
  public static void paint(TextSymbolizer symbolizer, IFeature feature, Viewport viewport, Graphics2D graphics, double opacity) {
    if (symbolizer.getLabel() == null) {
      return;
    }
    Object value = feature.getAttribute(symbolizer.getLabel());
    String text = (value == null) ? null : value.toString();
    if (text != null) {
      paint(symbolizer, text, feature.getGeom(), viewport, graphics, opacity);
    }
  }

  /**
   * @param symbolizer a text symbolizer
   * @param text non null text
   * @param geometry the geometry of the feature
   * @param viewport the viewport to paint in
   * @param graphics the graphics to paint with
   */
  @SuppressWarnings("unchecked")
  public static void paint(TextSymbolizer symbolizer, String text, IGeometry geometry, Viewport viewport, Graphics2D graphics, double opacity) {
    // Initialize the color with which to actually paint the text
    Color fillColor = getColorWithOpacity(Color.black, opacity);
    if (symbolizer.getFill() != null) {
      fillColor = getColorWithOpacity(symbolizer.getFill().getColor(), opacity);
    }
    //The scale
    double scaleUOMToPixels = 1;
    double scaleSymbolizerUOMToDataUOM = 1;
    if (!symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
      try {
        scaleUOMToPixels = viewport.getModelToViewTransform().getScaleX();
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    } else {
      try {
        scaleSymbolizerUOMToDataUOM = 1 / viewport.getModelToViewTransform().getScaleX();
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
    }
    // Initialize the font
    java.awt.Font awtFont = null;
    if (symbolizer.getFont() != null) {
      awtFont = symbolizer.getFont().toAwfFont((float) scaleUOMToPixels);
    }
    if (awtFont == null) {
      awtFont = new java.awt.Font("Default", java.awt.Font.PLAIN, 10); //$NON-NLS-1$
    }
    graphics.setFont(awtFont);
    // Initialize the color for the halo around the text
    Color haloColor = getColorWithOpacity(Color.WHITE, opacity);
    float haloRadius = 1.0f;
    if (symbolizer.getHalo() != null) {
      if (symbolizer.getHalo().getFill() != null) {
        haloColor = getColorWithOpacity(symbolizer.getHalo().getFill().getColor(), opacity);
      }
      haloRadius = symbolizer.getHalo().getRadius();
    }
    LabelPlacement labelPlacement = symbolizer.getLabelPlacement();
    if (labelPlacement != null && labelPlacement.getPlacement() != null) {
      Placement placement = labelPlacement.getPlacement();
      if (PointPlacement.class.isAssignableFrom(placement.getClass())) {
        PointPlacement pointPlacement = (PointPlacement) placement;
        try {
          paint(pointPlacement, text, fillColor, haloColor, haloRadius, geometry.centroid(), viewport, graphics, scaleUOMToPixels);
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
        }
      } else {
        if (LinePlacement.class.isAssignableFrom(placement.getClass())) {
          LinePlacement linePlacement = (LinePlacement) placement;
          float offset = linePlacement.getPerpendicularOffset() * (float) scaleSymbolizerUOMToDataUOM;
          IGeometry g = geometry;
          if (offset != 0.0f) {
            g = JtsAlgorithms.offsetCurve(geometry, offset);
          }
          if (IMultiCurve.class.isAssignableFrom(g.getClass())) {
            IMultiCurve<IOrientableCurve> multiCurve = (IMultiCurve<IOrientableCurve>) g;
            for (IOrientableCurve curve : multiCurve) {
              try {
                paint(linePlacement, text, fillColor, haloColor, haloRadius, curve, viewport, graphics, scaleUOMToPixels);
              } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
              }              
            }
          } else {
            try {
              paint(linePlacement, text, fillColor, haloColor, haloRadius, g, viewport, graphics, scaleUOMToPixels);
            } catch (NoninvertibleTransformException e) {
              e.printStackTrace();
            }            
          }
        }
      }
    }
  }

  /**
   * @param linePlacement
   * @param text
   * @param fillColor
   * @param haloColor
   * @param haloRadius
   * @param geometry
   * @param viewport
   * @param graphics
   * @param scale
   * @throws NoninvertibleTransformException
   */
  private static void paint(LinePlacement linePlacement, String text,
      Color fillColor, Color haloColor, float haloRadius, IGeometry geometry,
      Viewport viewport, Graphics2D graphics, double scale) throws NoninvertibleTransformException {
    if (linePlacement.isGeneralizeLine()) {
      // we have to generalize the geometry first
//      double sigma = 20;
      double sigma = graphics.getFontMetrics().getMaxAdvance() / scale;
      geometry = GaussianFilter.gaussianFilter(new GM_LineString(geometry.coord()), sigma, 1.0);
    }
    Shape lineShape = null;
    if (linePlacement.isAligned()) {
      // if the text should be aligned on the geometry
      lineShape = viewport.toShape(geometry);
    } else {
      // the expected behaviour here is not well specified
      // we decided to use the horizontal line cutting the envelope of the
      // geometry as the support line and to treat it as a standard text
      // symbolizer
      IEnvelope envelope = geometry.getEnvelope();
      double y = (envelope.minY() + envelope.maxY()) / 2;
      IDirectPosition p1 = new DirectPosition(envelope.minX(), y);
      IDirectPosition p2 = new DirectPosition(envelope.maxX(), y);
      lineShape = viewport.toShape(new GM_LineString(p1, p2));
    }
    if (lineShape == null) {
      // if there is no geometry, return
      return;
    }
    Stroke s = new TextStroke(text, graphics.getFont(), false, linePlacement
        .isRepeated(), false, linePlacement.getInitialGap() * (float) scale, linePlacement
        .getGap() * (float) scale);
    Shape textShape = s.createStrokedShape(lineShape);
    // halo
    if (haloColor != null) {
      graphics.setColor(haloColor);
      graphics.setStroke(new BasicStroke(haloRadius, BasicStroke.CAP_ROUND,
          BasicStroke.JOIN_ROUND));
      graphics.draw(textShape);
    }
    graphics.setColor(fillColor);
    graphics.fill(textShape);
  }
  /**
   * @param pointPlacement
   * @param text
   * @param fillColor
   * @param haloColor
   * @param haloRadius
   * @param position
   * @param viewport
   * @param graphics
   * @param scale
   * @throws NoninvertibleTransformException
   */
  private static void paint(PointPlacement pointPlacement, String text,
      Color fillColor, Color haloColor, float haloRadius,
      IDirectPosition position, Viewport viewport, Graphics2D graphics,
      double scale)
      throws NoninvertibleTransformException {
    FontRenderContext frc = graphics.getFontRenderContext();
    float rotation = (float) (pointPlacement.getRotation() * Math.PI / 180);
    GlyphVector gv = graphics.getFont().createGlyphVector(frc, text);
    Shape textShape = gv.getOutline();
    Rectangle2D bounds = textShape.getBounds2D();
    double width = bounds.getWidth();
    double height = bounds.getHeight();
    Point2D p = viewport.toViewPoint(position);
    AnchorPoint anchorPoint = pointPlacement.getAnchorPoint();
    float anchorPointX = (anchorPoint == null) ? 0.5f : anchorPoint.getAnchorPointX();
    float anchorPointY = (anchorPoint == null) ? 0.5f : anchorPoint.getAnchorPointY();
    Displacement displacement = pointPlacement.getDisplacement();
    float displacementX = (displacement == null) ?0.0f : displacement.getDisplacementX();
    float displacementY = (displacement == null) ?0.0f : displacement.getDisplacementY();
    float tx = (float) (p.getX() + displacementX * scale);
    float ty = (float) (p.getY() - displacementY * scale);
    AffineTransform t = AffineTransform.getTranslateInstance(tx, ty);
    t.rotate(rotation);
    t.translate(-width * anchorPointX, height * anchorPointY);
    textShape = t.createTransformedShape(textShape);
    // halo
    if (haloColor != null) {
      graphics.setColor(haloColor);
      graphics.setStroke(new BasicStroke(haloRadius, BasicStroke.CAP_ROUND,
          BasicStroke.JOIN_ROUND));
      graphics.draw(textShape);
    }
    graphics.setColor(fillColor);
    graphics.fill(textShape);
  }

    /**
     * @param symbolizer
     * @param feature
     * @param viewport
     * @param graphics
     * @param opacity
     */
    @SuppressWarnings({ "unchecked" })
    public static void paint(ThematicSymbolizer symbolizer, IFeature feature,
            Viewport viewport, Graphics2D graphics, double opacity) {
    if (feature.getGeom() == null || viewport == null) {
      return;
    }
    for (DiagramSymbolizer s : symbolizer.getSymbolizers()) {
      if (s.getDiagramType().equalsIgnoreCase("piechart")) { //$NON-NLS-1$
        // double size = 1.0;
        // for (DiagramSizeElement element : s.getDiagramSize()) {
        // if (element instanceof DiagramRadius) {
        // size = element.getValue();
        // }
        // }
        IDirectPosition position = symbolizer.getPoints().get(feature);
        Double size = symbolizer.getRadius().get(feature);
        if (position == null || size == null) {
          TriangulationJTS t = new TriangulationJTS("TRIANGLE"); //$NON-NLS-1$
          Chargeur.importAsNodes(feature, t);
          try {
            t.triangule("v"); //$NON-NLS-1$
          } catch (Exception e1) {
            e1.printStackTrace();
          }
          GM_MultiCurve<IOrientableCurve> contour = new GM_MultiCurve<IOrientableCurve>();
          if (feature.getGeom() instanceof IPolygon) {
            contour.add(((IPolygon) feature.getGeom())
                .exteriorLineString());
          } else {
            for (IPolygon surface : (IMultiSurface<IPolygon>) feature
                .getGeom()) {
              contour.add(surface.exteriorLineString());
            }
          }
          for (Arc a : t.getPopArcs()) {
            ((IPopulation<IFeature>) DataSet.getInstance().getPopulation(
                "Triangulation")).add(new DefaultFeature(a.getGeometrie())); //$NON-NLS-1$
          }
          double maxDistance = Double.MIN_VALUE;
          Noeud maxNode = null;
          for (Arc a : t.getPopVoronoiEdges().select(feature.getGeom())) {
            if (!a.getGeometrie().intersectsStrictement(feature.getGeom())) {
              ((Population<DefaultFeature>) DataSet.getInstance()
                  .getPopulation("MedialAxis")).add(new DefaultFeature(a //$NON-NLS-1$
                  .getGeometrie()));
            }
          }
          for (Noeud n : t.getPopVoronoiVertices().select(feature.getGeom())) {
            double d = n.getGeometrie().distance(contour);
            if (d > maxDistance) {
              maxDistance = d;
              maxNode = n;
            }
          }
          size = maxDistance;
          if (maxNode == null) {
//            AbstractSymbolizer.logger.info(feature.getGeom());
            return;
          }
          position = maxNode.getGeometrie().getPosition();
          symbolizer.getPoints().put(feature, position);
          symbolizer.getRadius().put(feature, maxDistance);
        }
        Point2D point = null;
        try {
          point = viewport.toViewPoint(position);
        } catch (NoninvertibleTransformException e) {
          e.printStackTrace();
          return;
        }
        double scale = 1;
        if (symbolizer.getUnitOfMeasure() != Symbolizer.PIXEL) {
          try {
            scale = viewport.getModelToViewTransform().getScaleX();
          } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
          }
        }
        size *= scale;
        double startAngle = 0.0;
        for (ThematicClass thematicClass : s.getThematicClass()) {
          double value = ((Number) thematicClass.getClassValue().evaluate(
              feature)).doubleValue();
          // AbstractSymbolizer.logger.info(thematicClass.getClassLabel() + " "
          // + value);
          if (value == 0) {
            continue;
          }
          double arcAngle = 3.6 * value;
          // AbstractSymbolizer.logger.info("\t" + startAngle + " - " +
          // arcAngle);
          graphics.setColor(getColorWithOpacity(thematicClass.getFill().getColor(), opacity));
          graphics.fillArc((int) (point.getX() - size),
              (int) (point.getY() - size), (int) (2 * size), (int) (2 * size),
              (int) startAngle, (int) arcAngle);
          graphics.setColor(Color.BLACK);
          graphics.drawArc((int) (point.getX() - size),
              (int) (point.getY() - size), (int) (2 * size), (int) (2 * size),
              (int) startAngle, (int) arcAngle);
          startAngle += arcAngle;
        }
      }
    }
  }
}