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

package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * Selection Mode. Allow the user to select features.
 * 
 * @author Julien Perret
 * 
 */
public class SelectionMode extends AbstractMode {

  /** Logger. */
  private static final Logger LOGGER = Logger.getLogger(SelectionMode.class
      .getName());

  /**
   * @param theMainFrame the main frame
   * @param theModeSelector the mode selector
   */
  public SelectionMode(final MainFrame theMainFrame,
      final MainFrameToolBar theModeSelector) {
    super(theMainFrame, theModeSelector);
  }

  @Override
  protected final JButton createButton() {
    return new JButton(new ImageIcon(this.getClass().getResource(
        "/images/icons/16x16/selection2.png"))); //$NON-NLS-1$
  }

  /**
   * Selection radius. Modifications @author amaudet : A radius based on pixels
   * seems better than a radius based on map-metres.
   */
  // private final double selectionRadius = 10.0;
  private double getSelectionRadius() {
    double scale = this.mainFrame.getSelectedProjectFrame().getLayerViewPanel()
        .getViewport().getScale();
    return 10.0 / scale;
  }

  @Override
  public final void leftMouseButtonClicked(final MouseEvent e,
      final ProjectFrame frame) {

    LOGGER
        .debug("--------------------------------------------------------------------------------");
    LOGGER.debug("click event : select");

    try {
      DirectPosition p = frame.getLayerViewPanel().getViewport()
          .toModelDirectPosition(e.getPoint());
      Set<IFeature> features = new HashSet<IFeature>();
      for (Layer layer : frame.getLayerViewPanel().getRenderingManager()
          .getLayers()) {
        if (layer.isVisible() && layer.isSelectable()) {
          for (IFeature feature : layer.getFeatureCollection().select(p,
              this.getSelectionRadius())) {
            if (feature.isDeleted()) {
              continue;
            }
            features.add(feature);
          }
        }
      }
      LayerViewPanel lvPanel = frame.getLayerViewPanel();
      lvPanel.getSelectedFeatures().addAll(features);
      LOGGER.debug("Number of selected features = " + features.size());
      for (IFeature feature : lvPanel.getSelectedFeatures()) {
        LOGGER.debug("\t" + feature);
      }
      lvPanel.getRenderingManager().render(
          lvPanel.getRenderingManager().getSelectionRenderer());
      lvPanel.superRepaint();

    } catch (NoninvertibleTransformException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  public final void rightMouseButtonClicked(final MouseEvent e,
      final ProjectFrame frame) {
    try {
      DirectPosition p = frame.getLayerViewPanel().getViewport()
          .toModelDirectPosition(e.getPoint());
      Set<IFeature> features = new HashSet<IFeature>();
      for (Layer layer : frame.getLayerViewPanel().getRenderingManager()
          .getLayers()) {
        if (layer.isVisible() && layer.isSelectable()) {
          features.addAll(layer.getFeatureCollection().select(p,
              this.getSelectionRadius()));
        }
      }
      LayerViewPanel lvPanel = frame.getLayerViewPanel();
      if (features.isEmpty()) {
        lvPanel.getSelectedFeatures().clear();
      } else {
        lvPanel.getSelectedFeatures().removeAll(features);
      }
      lvPanel.getRenderingManager().render(
          lvPanel.getRenderingManager().getSelectionRenderer());
      lvPanel.superRepaint();
    } catch (NoninvertibleTransformException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  protected String getToolTipText() {
    return I18N.getString("SelectionMode.ToolTip"); //$NON-NLS-1$
  }
}
