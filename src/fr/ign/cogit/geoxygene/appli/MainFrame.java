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

package fr.ign.cogit.geoxygene.appli;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.print.PrinterJob;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.DefaultDesktopManager;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.fop.cli.Main;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.event.CoordPaintListener;
import fr.ign.cogit.geoxygene.appli.mode.ModeSelector;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * @author Julien Perret
 */
public class MainFrame extends JFrame {
  /**
   * serial uid.
   */
  private static final long serialVersionUID = 1L;
  /**
   * Logger of the application.
   */
  private static Logger logger = Logger.getLogger(MainFrame.class.getName());

  /**
   * Get the application logger.
   * 
   * @return the application logger
   */
  public static Logger getLogger() {
    return MainFrame.logger;
  }

  /**
   * The desktop pane containing the project frames.
   */
  private JDesktopPane desktopPane = new JDesktopPane() {
    private static final long serialVersionUID = 1L;
    {
      this.setDesktopManager(new DefaultDesktopManager());
    }
  };

  /**
   * Return the desktop pane containing the project frames.
   * 
   * @return the desktop pane containing the project frames
   */
  public final JDesktopPane getDesktopPane() {
    return this.desktopPane;
  }

  /**
   * The associated application.
   */
  private GeOxygeneApplication application;

  /**
   * Get the associated application.
   * 
   * @return the associated application
   */
  public final GeOxygeneApplication getApplication() {
    return this.application;
  }

  /**
   * The frame menu bar.
   */
  private JMenuBar menuBar;

  public JMenuBar getmenuBar() {
    return this.menuBar;
  }

  /**
   * The mode selector.
   */
  private ModeSelector modeSelector = null;

  /**
   * Return the current application mode.
   * 
   * @return the current application mode
   */
  public final ModeSelector getMode() {
    return this.modeSelector;
  }

  /**
   * The default width of the frame.
   */
  private final int defaultFrameWidth = 800;
  /**
   * The default height of the frame.
   */
  private final int defaultFrameHeight = 800;

  private static FileChooser fc = new FileChooser();

  public static FileChooser getFilechooser() {
    return MainFrame.fc;
  }

  /**
   * Constructor using a title and an associated application.
   * 
   * @param title the title of the frame
   * @param theApplication the associated application
   */
  public MainFrame(final String title, final GeOxygeneApplication theApplication) {
    super(title);
    this.application = theApplication;
    this.setIconImage(this.application.getIcon().getImage());
    this.setLayout(new BorderLayout());
    this.setResizable(true);
    this.setSize(this.defaultFrameWidth, this.defaultFrameHeight);
    this.setExtendedState(Frame.MAXIMIZED_BOTH);
    this.menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu(I18N.getString("MainFrame.File")); //$NON-NLS-1$
    JMenu viewMenu = new JMenu(I18N.getString("MainFrame.View")); //$NON-NLS-1$
    JMenu configurationMenu = new JMenu(
        I18N.getString("MainFrame.Configuration")); //$NON-NLS-1$
    JMenu helpMenu = new JMenu(I18N.getString("MainFrame.Help")); //$NON-NLS-1$
    JMenuItem openFileMenuItem = new JMenuItem(
        I18N.getString("MainFrame.OpenFile")); //$NON-NLS-1$
    openFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        ProjectFrame projectFrame = (ProjectFrame) MainFrame.this
            .getDesktopPane().getSelectedFrame();
        if (projectFrame == null) {
          if (MainFrame.this.getDesktopPane().getAllFrames().length != 0) {
            // TODO ask the user in which frame (s)he
            // wants to load into?
            projectFrame = (ProjectFrame) MainFrame.this.getDesktopPane()
                .getAllFrames()[0];
          } else {
            // TODO create a new project frame?
            MainFrame.getLogger().info(
                I18N.getString("MainFrame.NoFrameToLoadInto")); //$NON-NLS-1$
            return;
          }
        }
        File file = MainFrame.fc.getFile(MainFrame.this);
        if (file != null) {
          projectFrame.addLayer(file);
        }
      }
    });
    JMenuItem newProjectFrameMenuItem = new JMenuItem(
        I18N.getString("MainFrame.NewProject")); //$NON-NLS-1$
    newProjectFrameMenuItem
        .addActionListener(new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent e) {
            MainFrame.this.newProjectFrame();
          }
        });

    JMenuItem saveAsShpMenuItem = new JMenuItem(
        I18N.getString("MainFrame.SaveAsShp")); //$NON-NLS-1$
    saveAsShpMenuItem.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        ProjectFrame project = MainFrame.this.getSelectedProjectFrame();
        Set<Layer> selectedLayers = project.getLayerLegendPanel()
            .getSelectedLayers();
        if (selectedLayers.size() != 1) {
          MainFrame.logger.error("You must select one (and only one) layer."); //$NON-NLS-1$
          return;
        }
        Layer layer = selectedLayers.iterator().next();

        IFeatureCollection<? extends IFeature> layerfeatures = layer
            .getFeatureCollection();
        if (layerfeatures == null) {
          MainFrame.logger
              .error("The layer selected does not contain any feature."); //$NON-NLS-1$
          return;
        }
        JFileChooser chooser = new JFileChooser(MainFrame.fc
            .getPreviousDirectory());
        int result = chooser.showSaveDialog(MainFrame.this);
        if (result == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          if (file != null) {
            String fileName = file.getAbsolutePath();
            project.saveAsShp(fileName, layer);
          }
        }
      }
    });

    JMenuItem saveAsImageMenuItem = new JMenuItem(
        I18N.getString("MainFrame.SaveAsImage")); //$NON-NLS-1$
    saveAsImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        ProjectFrame projectFrame = (ProjectFrame) MainFrame.this
            .getDesktopPane().getSelectedFrame();
        if (projectFrame == null) {
          if (MainFrame.this.getDesktopPane().getAllFrames().length != 0) {
            projectFrame = (ProjectFrame) MainFrame.this.getDesktopPane()
                .getAllFrames()[0];
          } else {
            return;
          }
        }
        JFileChooser chooser = new JFileChooser(MainFrame.fc
            .getPreviousDirectory());
        int result = chooser.showSaveDialog(MainFrame.this);
        if (result == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          if (file != null) {
            String fileName = file.getAbsolutePath();
            projectFrame.saveAsImage(fileName);
          }
        }
      }
    });

    JMenuItem printMenu = new JMenuItem(I18N.getString("MainFrame.Print")); //$NON-NLS-1$
    printMenu.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        Thread th = new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              PrinterJob printJob = PrinterJob.getPrinterJob();
              printJob.setPrintable(MainFrame.this.getSelectedProjectFrame()
                  .getLayerViewPanel());
              PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
              if (printJob.printDialog(aset)) {
                printJob.print(aset);
              }
            } catch (java.security.AccessControlException ace) {
              JOptionPane.showMessageDialog(MainFrame.this
                  .getSelectedProjectFrame().getLayerViewPanel(),
                  I18N.getString("MainFrame.ImpossibleToPrint") //$NON-NLS-1$
                      + ";" //$NON-NLS-1$
                      + I18N.getString("MainFrame.AccessControlProblem") //$NON-NLS-1$
                      + ace.getMessage(), I18N
                      .getString("MainFrame.ImpossibleToPrint"), //$NON-NLS-1$
                  JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        });
        th.start();
      }
    });
    JMenuItem exitMenuItem = new JMenuItem(I18N.getString("MainFrame.Exit")); //$NON-NLS-1$
    exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        MainFrame.this.dispose();
        MainFrame.this.getApplication().exit();
      }
    });
    fileMenu.add(openFileMenuItem);
    fileMenu.add(newProjectFrameMenuItem);
    fileMenu.addSeparator();
    fileMenu.add(saveAsShpMenuItem);
    fileMenu.add(saveAsImageMenuItem);
    fileMenu.add(printMenu);
    fileMenu.addSeparator();
    fileMenu.add(exitMenuItem);
    this.menuBar.setFont(this.application.getFont());
    this.menuBar.add(fileMenu);
    this.menuBar.add(viewMenu);
    JMenuItem mScale6250 = new JMenuItem("1:6250"); //$NON-NLS-1$
    mScale6250.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        LayerViewPanel layerViewPanel = MainFrame.this
            .getSelectedProjectFrame().getLayerViewPanel();
        layerViewPanel.getViewport().setScale(
            1 / (6250 * LayerViewPanel.getMETERS_PER_PIXEL()));
        layerViewPanel.repaint();
      }
    });
    JMenuItem mScale12500 = new JMenuItem("1:12500"); //$NON-NLS-1$
    mScale12500.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        LayerViewPanel layerViewPanel = MainFrame.this
            .getSelectedProjectFrame().getLayerViewPanel();
        layerViewPanel.getViewport().setScale(
            1 / (12500 * LayerViewPanel.getMETERS_PER_PIXEL()));
        layerViewPanel.repaint();
      }
    });
    JMenuItem mScale25k = new JMenuItem("1:25k"); //$NON-NLS-1$
    mScale25k.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        LayerViewPanel layerViewPanel = MainFrame.this
            .getSelectedProjectFrame().getLayerViewPanel();
        layerViewPanel.getViewport().setScale(
            1 / (25000 * LayerViewPanel.getMETERS_PER_PIXEL()));
        layerViewPanel.repaint();
      }
    });
    JMenuItem mScale50k = new JMenuItem("1:50k"); //$NON-NLS-1$
    mScale50k.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        LayerViewPanel layerViewPanel = MainFrame.this
            .getSelectedProjectFrame().getLayerViewPanel();
        layerViewPanel.getViewport().setScale(
            1 / (50000 * LayerViewPanel.getMETERS_PER_PIXEL()));
        layerViewPanel.repaint();
      }
    });
    JMenuItem mScale100k = new JMenuItem("1:100k"); //$NON-NLS-1$
    mScale100k.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        LayerViewPanel layerViewPanel = MainFrame.this
            .getSelectedProjectFrame().getLayerViewPanel();
        layerViewPanel.getViewport().setScale(
            1 / (100000 * LayerViewPanel.getMETERS_PER_PIXEL()));
        layerViewPanel.repaint();
      }
    });
    JMenuItem mScaleCustom = new JMenuItem(
        I18N.getString("MainFrame.CustomScale")); //$NON-NLS-1$
    mScaleCustom.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        int scale = Integer.parseInt(JOptionPane.showInputDialog(I18N
            .getString("MainFrame.NewScale"))); //$NON-NLS-1$
        LayerViewPanel layerViewPanel = MainFrame.this
            .getSelectedProjectFrame().getLayerViewPanel();
        layerViewPanel.getViewport().setScale(
            1 / (scale * LayerViewPanel.getMETERS_PER_PIXEL()));
        layerViewPanel.repaint();
      }
    });
    viewMenu.add(mScale6250);
    viewMenu.add(mScale12500);
    viewMenu.add(mScale25k);
    viewMenu.add(mScale50k);
    viewMenu.add(mScale100k);
    viewMenu.add(mScaleCustom);
    viewMenu.addSeparator();

    JMenuItem mGoTo = new JMenuItem(I18N.getString("MainFrame.GoTo")); //$NON-NLS-1$
    mGoTo.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        LayerViewPanel layerViewPanel = MainFrame.this
            .getSelectedProjectFrame().getLayerViewPanel();

        String lat = JOptionPane.showInputDialog("Latitude"); //$NON-NLS-1$
        if (lat == null) {
          return;
        }
        double latitude = Double.parseDouble(lat);
        String lon = JOptionPane.showInputDialog("Longitude"); //$NON-NLS-1$
        if (lon == null) {
          return;
        }
        double longitude = Double.parseDouble(lon);
        try {
          layerViewPanel.getViewport().center(
              new DirectPosition(latitude, longitude));
        } catch (NoninvertibleTransformException e1) {
          e1.printStackTrace();
        }
        layerViewPanel.repaint();
      }
    });
    viewMenu.add(mGoTo);

    JMenuItem mCoord = new JCheckBoxMenuItem(
        I18N.getString("MainFrame.Coordinate")); //$NON-NLS-1$
    mCoord.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        LayerViewPanel layerViewPanel = MainFrame.this
            .getSelectedProjectFrame().getLayerViewPanel();
        if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {
          layerViewPanel.addMouseMotionListener(new CoordPaintListener());
        } else {
          for (MouseMotionListener m : layerViewPanel.getMouseMotionListeners()) {
            if (m.getClass().equals(CoordPaintListener.class)) {
              layerViewPanel.removeMouseMotionListener(m);
              layerViewPanel.repaint();
            }
          }
        }
      }
    });
    viewMenu.add(mCoord);

    this.menuBar.add(configurationMenu);
    this.menuBar.add(helpMenu);
    this.setJMenuBar(this.menuBar);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(this.desktopPane, BorderLayout.CENTER);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(final WindowEvent e) {
        MainFrame.this.getApplication().exit();
      }
    });
    this.modeSelector = new ModeSelector(this);
    this.desktopPane.addContainerListener(modeSelector);
    JMenuItem organizeMenuItem = new JMenuItem("Organize"); //$NON-NLS-1$
    organizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        ProjectFrame[] projectFrames = MainFrame.this.getAllProjectFrames();
        ;
        MainFrame.this.getDesktopPane().removeAll();
        GridLayout layout = new GridLayout(0, 2);
        MainFrame.this.getDesktopPane().setLayout(layout);
        for (ProjectFrame frame : projectFrames) {
          MainFrame.this.getDesktopPane().add(frame);
        }
        MainFrame.this.getDesktopPane().doLayout();
      }
    });
    configurationMenu.add(organizeMenuItem);
  }

  @Override
  public final void dispose() {
    for (JInternalFrame frame : this.desktopPane.getAllFrames()) {
      frame.dispose();
    }
    super.dispose();
  }

  /**
   * Return the selected (current) project frame.
   * 
   * @return the selected (current) project frame
   */
  public final ProjectFrame getSelectedProjectFrame() {
    if ((this.desktopPane.getSelectedFrame() == null)
        || !(this.desktopPane.getSelectedFrame() instanceof ProjectFrame)) {
      return null;
    }
    return (ProjectFrame) this.desktopPane.getSelectedFrame();
  }

  /**
   * Return all project frames.
   * 
   * @return an array containing all project frames available in the interface
   */
  public final ProjectFrame[] getAllProjectFrames() {
    List<ProjectFrame> projectFrameList = new ArrayList<ProjectFrame>();
    for (JInternalFrame frame : this.desktopPane.getAllFrames()) {
      if (frame instanceof ProjectFrame) {
        projectFrameList.add((ProjectFrame) frame);
      }
    }
    return projectFrameList.toArray(new ProjectFrame[0]);
  }

  /**
   * Create and return a new project frame.
   * 
   * @return the newly created project frame
   */
  public final ProjectFrame newProjectFrame() {
    ProjectFrame projectFrame = new ProjectFrame(this,
        this.application.getIcon());
    projectFrame.setSize(this.desktopPane.getSize());
    projectFrame.setVisible(true);
    this.desktopPane.add(projectFrame, JLayeredPane.DEFAULT_LAYER);
    try {
      projectFrame.setSelected(true);
    } catch (PropertyVetoException e) {
      e.printStackTrace();
    }
    projectFrame.setToolTipText(projectFrame.getTitle());
    return projectFrame;
  }
}
