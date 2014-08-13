package fr.ign.cogit.geoxygene.appli.layer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import test.app.GLBezierShadingVertex;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.event.CompassPaintListener;
import fr.ign.cogit.geoxygene.appli.event.LegendPaintListener;
import fr.ign.cogit.geoxygene.appli.event.ScalePaintListener;
import fr.ign.cogit.geoxygene.appli.gl.GLPaintingVertex;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory.RenderingType;
import fr.ign.cogit.geoxygene.appli.mode.MainFrameToolBar;
import fr.ign.cogit.geoxygene.appli.render.LayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.SyncRenderingManager;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.ImageComparator;
import fr.ign.cogit.geoxygene.util.gl.GLContext;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLProgramAccessor;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;

/**
 * LayerViewGLPanel is the basic implementation of a GL Viewer. It adds a glass
 * Pane over the GL window in order to draw Swing drawings over the GL view. !!
 * Do not add children to this element, use setGLComponent() only !!
 * 
 * @author turbet
 * 
 */
public class LayerViewGLPanel extends LayerViewPanel implements ItemListener,
        ActionListener {

    private static final long serialVersionUID = -7181604491025859187L; // serializable
                                                                        // UID
    // private static final int GLASS_LAYER_INDEX = 10; // layer index on which
    // the overlay Swing stuff will be drawn
    // private static final int GL_LAYER_INDEX = 1; // layer index on which the
    // GL
    // stuff will be rendered
    private static Logger logger = Logger.getLogger(LayerViewGLPanel.class
            .getName());
    private SyncRenderingManager renderingManager = null;
    private LayerViewGLCanvas glCanvas = null; // canvas containing the GL
    private LayerViewGLCanvasType glType = null;
    private JToggleButton wireframeToggleButton = null;
    private JToggleButton fboToggleButton = null;
    private JButton antialiasingButton = null;
    private JButton clearCacheButton = null;
    private JButton awtComparButton = null;
    private JToolBar.Separator toolbarSeparator = null;
    private JMenu glMenu = null;
    private JMenuItem glInformationMenu = null;
    private boolean wireframe = false;
    private int antialiasing = 2;
    private boolean useFBO = true;

    public enum LayerViewGLCanvasType {
        GL1, GL4
    }

    // private final JLayeredPane layeredPane = null;
    // private final JPanel glPanel = null;
    // private final Component glassPanel = null;

    /**
     * 
     * @param sld
     * @param frame
     */
    public LayerViewGLPanel(final LayerViewGLCanvasType glType) {
        super();
        this.addPaintListener(new ScalePaintListener());
        this.addPaintListener(new CompassPaintListener());
        this.addPaintListener(new LegendPaintListener());
        // this.setBackground(new Color(255, 255, 220));
        this.renderingManager = new SyncRenderingManager(this,
                RenderingType.LWJGL);

        this.glCanvas = LayerViewPanelFactory
                .newLayerViewGLCanvas(this, glType);
        this.setGlType(glType);
        this.setLayout(new BorderLayout());
        // Attach LWJGL to the created canvas
        this.setGLComponent(this.glCanvas);

    }

    @Override
    public void displayGui() {
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getToolbarSeparator());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getWireframeButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getAntialiasingButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getFBOButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getClearCacheButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .add(this.getAWTComparButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .revalidate();
        this.getProjectFrame().getMainFrame().getMode().getToolBar().repaint();

        this.getProjectFrame().getMainFrame().getMenuBar()
                .add(this.getGLMenu());
        this.getProjectFrame().getMainFrame().getMenuBar().revalidate();
        this.getProjectFrame().getMainFrame().getMenuBar().repaint();

    }

    @Override
    public void hideGui() {
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getToolbarSeparator());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getWireframeButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getAntialiasingButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getFBOButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getClearCacheButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .remove(this.getAWTComparButton());
        this.getProjectFrame().getMainFrame().getMode().getToolBar()
                .revalidate();
        this.getProjectFrame().getMainFrame().getMode().getToolBar().repaint();

        this.getProjectFrame().getMainFrame().getMenuBar()
                .remove(this.getGLMenu());
        this.getProjectFrame().getMainFrame().getMenuBar().revalidate();
        this.getProjectFrame().getMainFrame().getMenuBar().repaint();
    }

    public boolean useFBO() {
        return this.useFBO;
    }

    public void setFBO(boolean useFBO) {
        this.useFBO = useFBO;
    }

    public int getAntialiasingSize() {
        return this.antialiasing;
    }

    public void setAntialiasing(int b) {
        this.antialiasing = b;
    }

    public boolean useWireframe() {
        return this.wireframe;
    }

    public void setWireframe(boolean b) {
        this.wireframe = b;
    }

    @Override
    public SyncRenderingManager getRenderingManager() {
        return this.renderingManager;
    }

    /**
     * activate the GL context
     */
    public void activateGLContext() {
        this.glCanvas.activateContext();
    }

    /**
     * @return the lwJGLCanvas
     */
    public LayerViewGLCanvas getLwJGLCanvas() {
        return this.glCanvas;
    }

    /**
     * @param lwJGLCanvas
     *            the lwJGLCanvas to set
     */
    public void setGLCanvas(LayerViewGLCanvas lwJGLCanvas) {
        this.glCanvas = lwJGLCanvas;
    }

    private JToolBar.Separator getToolbarSeparator() {
        if (this.toolbarSeparator == null) {
            this.toolbarSeparator = new JToolBar.Separator();
        }
        return this.toolbarSeparator;
    }

    private JToggleButton getWireframeButton() {
        if (this.wireframeToggleButton == null) {
            this.wireframeToggleButton = new JToggleButton();
            this.wireframeToggleButton.setIcon(new ImageIcon(
                    MainFrameToolBar.class
                            .getResource("/images/icons/16x16/wireframe.png")));
            this.wireframeToggleButton.setToolTipText(I18N
                    .getString("RenderingGL.ToggleWireframe"));
            this.wireframeToggleButton.setSelected(this.useWireframe());
            this.wireframeToggleButton.addItemListener(this);
        }
        return this.wireframeToggleButton;
    }

    private JToggleButton getFBOButton() {
        if (this.fboToggleButton == null) {
            this.fboToggleButton = new JToggleButton();
            this.fboToggleButton.setIcon(new ImageIcon(MainFrameToolBar.class
                    .getResource("/images/icons/16x16/fbo.png")));
            this.fboToggleButton.setToolTipText(I18N
                    .getString("RenderingGL.ToggleFBO"));
            this.fboToggleButton.setSelected(this.useFBO());
            this.fboToggleButton.addItemListener(this);
        }
        return this.fboToggleButton;
    }

    private JButton getAntialiasingButton() {
        if (this.antialiasingButton == null) {
            this.antialiasingButton = new JButton();
            this.antialiasingButton
                    .setIcon(new ImageIcon(
                            MainFrameToolBar.class
                                    .getResource("/images/icons/16x16/antialiasing.png")));
            this.antialiasingButton.setToolTipText(I18N
                    .getString("RenderingGL.ToggleAntialiasing"));

            this.antialiasingButton.setText(String.valueOf(this
                    .getAntialiasingSize()));
            this.antialiasingButton.addActionListener(this);
        }
        return this.antialiasingButton;
    }

    private JButton getAWTComparButton() {
        if (this.awtComparButton == null) {
            this.awtComparButton = new JButton();
            this.awtComparButton.setIcon(new ImageIcon(MainFrameToolBar.class
                    .getResource("/images/icons/16x16/compare.gif")));
            this.awtComparButton.setToolTipText(I18N
                    .getString("RenderingGL.ImageComparison"));
            this.awtComparButton.addActionListener(this);
        }
        return this.awtComparButton;
    }

    private JMenuItem getGLInformationMenu() {
        if (this.glInformationMenu == null) {
            this.glInformationMenu = new JMenuItem("Information");
            this.glInformationMenu.addActionListener(this);
        }
        return this.glInformationMenu;
    }

    private JMenu getGLMenu() {
        if (this.glMenu == null) {
            this.glMenu = new JMenu("GL");
            this.glMenu.add(this.getGLInformationMenu());
        }
        return this.glMenu;
    }

    private JButton getClearCacheButton() {
        if (this.clearCacheButton == null) {
            this.clearCacheButton = new JButton();
            this.clearCacheButton.setIcon(new ImageIcon(MainFrameToolBar.class
                    .getResource("/images/icons/16x16/clear.png")));
            this.clearCacheButton.setToolTipText(I18N
                    .getString("RenderingGL.ClearCache"));
            this.clearCacheButton.addActionListener(this);
        }
        return this.clearCacheButton;
    }

    @Override
    public final void repaint() {
        if (this.glCanvas != null) {
            this.glCanvas.repaint();
        }

    }

    public GLContext getGlContext() throws GLException {
        return this.glCanvas.getGlContext();
    }

    @Override
    public final void paintComponent(final Graphics g) {
        try {

            this.glCanvas.doPaint();
        } catch (Exception e1) {
            // e1.printStackTrace();
            logger.error(I18N.getString("LayerViewPanel.PaintError") + " " + e1.getMessage()); //$NON-NLS-1$
        }
    }

    /**
     * Repaint the panel using the repaint method of the super class
     * {@link JPanel}. Called in order to perform the progressive rendering.
     * 
     * @see #paintComponent(Graphics)
     */
    @Override
    public final void superRepaint() {
        Container parent = this.getParent();
        if (parent != null) {
            parent.repaint();
        }
    }

    /** Dispose panel and its rendering manager. */
    @Override
    public void dispose() {
        if (this.glCanvas != null) {
            try {
                if (this.glCanvas.getContext() != null) {
                    this.glCanvas.releaseContext();
                }
                this.glCanvas = null;
            } catch (Exception e) {
                logger.error("An error occurred releasing GL context "
                        + e.getMessage());
            }
        }

        if (this.getRenderingManager() != null) {
            this.getRenderingManager().dispose();
        }
        this.setViewport(null);
        // this.glPanel.setVisible(false);
        // TODO: properly close GL stuff
    }

    /**
     * Set the child Component where GL will be rendered
     * 
     * @param glComponent
     */
    protected void setGLComponent(final Component glComponent) {
        // this.add(glComponent, BorderLayout.CENTER);
        this.removeAll();
        this.add(glComponent, BorderLayout.CENTER);
        // glComponent.setBounds(0, 0, 800, 800);

    }

    @Override
    public synchronized void layerAdded(final Layer l) {
        if (this.getRenderingManager() != null) {
            this.getRenderingManager().addLayer(l);
        }
        try {
            IEnvelope env = l.getFeatureCollection().getEnvelope();
            if (env == null) {
                env = l.getFeatureCollection().envelope();
            }
            this.getViewport().zoom(env);
        } catch (NoninvertibleTransformException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void layerOrderChanged(final int oldIndex, final int newIndex) {
        this.repaint();
    }

    @Override
    public void layersRemoved(final Collection<Layer> layers) {
        this.repaint();
    }

    @Override
    public int print(final Graphics arg0, final PageFormat arg1, final int arg2)
            throws PrinterException {
        logger.error("LayerViewGlPanel::print(...) not implemented yet");
        return 0;
    }

    @Override
    public IEnvelope getEnvelope() {
        if (this.getRenderingManager().getLayers().isEmpty()) {
            return null;
        }
        List<Layer> copy = new ArrayList<Layer>(this.getRenderingManager()
                .getLayers());
        Iterator<Layer> layerIterator = copy.iterator();
        IEnvelope envelope = layerIterator.next().getFeatureCollection()
                .envelope();
        while (layerIterator.hasNext()) {
            IFeatureCollection<? extends IFeature> collection = layerIterator
                    .next().getFeatureCollection();
            if (collection != null) {
                IEnvelope env = collection.getEnvelope();
                if (envelope == null) {
                    envelope = env;
                } else {
                    envelope.expand(env);
                }
            }
        }
        return envelope;
    }

    @Override
    public void saveAsImage(final String fileName) {
        logger.error("LayerViewGLPanel::saveAsImage(...) not implemented yet");

    }

    public LayerViewGLCanvasType getGlType() {
        return this.glType;
    }

    private final void setGlType(LayerViewGLCanvasType glType) {
        this.glType = glType;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == this.getWireframeButton()) {
            this.setWireframe(this.getWireframeButton().isSelected());
            this.repaint();
        }
        if (e.getSource() == this.getFBOButton()) {
            this.setFBO(this.getFBOButton().isSelected());
            this.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.getGLInformationMenu()) {
            JOptionPane.showMessageDialog(SwingUtilities
                    .getWindowAncestor(this), new JTextArea(
                    this.getGLInformation(), 80, 40), "GL Information",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == this.getClearCacheButton()) {
            // dispose gl context to force program (shaders) reloading
            this.reset();
            // empty cache of all renderers
            for (LayerRenderer renderer : this.getRenderingManager()
                    .getRenderers()) {
                renderer.reset();
            }
            this.repaint();
        } else if (e.getSource() == this.getAWTComparButton()) {
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this));
            ImageComparator imageComparator = new ImageComparator(this);

            dialog.setSize(this.getWidth(), this.getHeight());
            dialog.setLocation(50, 50);
            // dialog.setModalityType(ModalityType.APPLICATION_MODAL);
            dialog.getContentPane().add(imageComparator.getGui());
            dialog.setVisible(true);
            imageComparator.update();
        } else if (e.getSource() == this.getAntialiasingButton()) {
            int antialiasingValue = 1;
            try {
                antialiasingValue = Integer.parseInt(this
                        .getAntialiasingButton().getText());
                antialiasingValue++;
                if (this.antialiasing >= 4) {
                    antialiasingValue = 0;
                }
                this.getAntialiasingButton().setText(
                        String.valueOf(antialiasingValue));
                this.setAntialiasing(antialiasingValue);
            } catch (Exception e2) {
                this.getAntialiasingButton().setText(String.valueOf(1));
                this.setAntialiasing(1);
            }
            this.repaint();
        } else {
            // old SLD events....
            this.repaint();
        }

    }

    private String getGLInformation() {
        StringBuilder str = new StringBuilder();
        str.append("GLInformations\n");
        Class<?> contextClass = org.lwjgl.opengl.GLContext.getCapabilities()
                .getClass();
        Field[] declaredFields = contextClass.getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                try {
                    str.append("\t" + field.getName() + " : "
                            + field.getBoolean(field) + "\n");
                } catch (Exception e) {
                    str.append("\t" + field.getName() + " : error: "
                            + e.getMessage() + "\n");
                }
            }
        }
        System.err.println(str.toString());
        return str.toString();
    }

    public void reset() {
        if (this.glCanvas != null) {
            this.glCanvas.reset();
        }

    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // private static GLContext glContext = null;

    public static final String m00ModelToViewMatrixUniformVarName = "m00";
    public static final String m02ModelToViewMatrixUniformVarName = "m02";
    public static final String m11ModelToViewMatrixUniformVarName = "m11";
    public static final String m12ModelToViewMatrixUniformVarName = "m12";
    public static final String screenWidthUniformVarName = "screenWidth";
    public static final String screenHeightUniformVarName = "screenHeight";
    public static final String globalOpacityUniformVarName = "globalOpacity";
    public static final String objectOpacityUniformVarName = "objectOpacity";
    public static final String colorTexture1UniformVarName = "colorTexture1";
    public static final String textureScaleFactorUniformVarName = "textureScaleFactor";
    public static final String antialiasingSizeUniformVarName = "antialiasingSize";

    public static final String paperTextureUniformVarName = "paperSampler";
    public static final String brushTextureUniformVarName = "brushSampler";
    public static final String brushWidthUniformVarName = "brushWidth";
    public static final String brushHeightUniformVarName = "brushHeight";
    public static final String brushStartWidthUniformVarName = "brushStartWidth";
    public static final String brushEndWidthUniformVarName = "brushEndWidth";
    // width of one brush pixel (mm)
    public static final String brushScaleUniformVarName = "brushScale";
    public static final String paperScaleUniformVarName = "paperScale";
    public static final String paperDensityUniformVarName = "paperDensity";
    public static final String brushDensityUniformVarName = "brushDensity";
    public static final String strokePressureUniformVarName = "strokePressure";
    public static final String sharpnessUniformVarName = "sharpness";
    public static final String strokePressureVariationAmplitudeUniformVarName = "pressureVariationAmplitude";
    public static final String strokePressureVariationWavelengthUniformVarName = "pressureVariationWavelength";
    public static final String strokeShiftVariationAmplitudeUniformVarName = "shiftVariationAmplitude";
    public static final String strokeShiftVariationWavelengthUniformVarName = "shiftVariationWavelength";
    public static final String strokeThicknessVariationAmplitudeUniformVarName = "thicknessVariationAmplitude";
    public static final String strokeThicknessVariationWavelengthUniformVarName = "thicknessVariationWavelength";

    public static final String basicProgramName = "Basic";
    public static final String linePaintingProgramName = "LinePainting";
    public static final String bezierLineProgramName = "BezierPainting";
    public static final String worldspaceColorProgramName = "WorldspaceColor";
    public static final String worldspaceTextureProgramName = "WorldspaceTexture";
    public static final String screenspaceColorProgramName = "ScreenspaceColor";
    public static final String screenspaceTextureProgramName = "ScreenspaceTexture";
    public static final String backgroundProgramName = "BackgroundTexture";
    public static final String screenspaceAntialiasedTextureProgramName = "ScreenspaceAntialiasedTexture";

    /**
     * This static method creates one GLContext containing all programs used to
     * render GeOxygene graphics elements
     * 
     * @return
     * @throws GLException
     */
    public static GLContext createNewGL4Context() throws GLException {
        GLContext glContext = new GLContext();

        final int worldspaceVertexShader = GLProgram
                .createVertexShader("./src/main/resources/shaders/worldspace.vert.glsl");
        final int screenspaceVertexShader = GLProgram
                .createVertexShader("./src/main/resources/shaders/screenspace.vert.glsl");
        glContext.addProgram(basicProgramName, new GLProgramAccessor() {

            @Override
            public GLProgram getGLProgram() throws GLException {
                return createBasicProgram();
            }
        });

        glContext.addProgram(worldspaceColorProgramName,
                new GLProgramAccessor() {

                    @Override
                    public GLProgram getGLProgram() throws GLException {
                        return createWorldspaceColorProgram(worldspaceVertexShader);
                    }
                });
        glContext.addProgram(worldspaceTextureProgramName,
                new GLProgramAccessor() {

                    @Override
                    public GLProgram getGLProgram() throws GLException {
                        return createWorldspaceTextureProgram(worldspaceVertexShader);
                    }
                });

        glContext.addProgram(screenspaceColorProgramName,
                new GLProgramAccessor() {

                    @Override
                    public GLProgram getGLProgram() throws GLException {
                        return createScreenspaceColorProgram(screenspaceVertexShader);
                    }
                });
        glContext.addProgram(screenspaceTextureProgramName,
                new GLProgramAccessor() {

                    @Override
                    public GLProgram getGLProgram() throws GLException {
                        return createScreenspaceTextureProgram(screenspaceVertexShader);
                    }
                });
        glContext.addProgram(screenspaceAntialiasedTextureProgramName,
                new GLProgramAccessor() {

                    @Override
                    public GLProgram getGLProgram() throws GLException {
                        return createScreenspaceAntialiasedProgram(screenspaceVertexShader);
                    }
                });

        // line painting
        glContext.addProgram(linePaintingProgramName, new GLProgramAccessor() {

            @Override
            public GLProgram getGLProgram() throws GLException {
                final int paintVertexShader = GLProgram
                        .createVertexShader("./src/main/resources/shaders/line.vert.glsl");
                final int paintFragmentShader = GLProgram
                        .createFragmentShader("./src/main/resources/shaders/line.frag.glsl");
                return createPaintProgram(paintVertexShader,
                        paintFragmentShader);
            }
        });

        // bezier line painting
        glContext.addProgram(bezierLineProgramName, new GLProgramAccessor() {

            @Override
            public GLProgram getGLProgram() throws GLException {
                final int bezierVertexShader = GLProgram
                        .createVertexShader("./src/main/resources/shaders/bezier.vert.glsl");
                final int bezierFragmentShader = GLProgram
                        .createFragmentShader("./src/main/resources/shaders/bezier.frag.glsl");
                return createBezierProgram(bezierVertexShader,
                        bezierFragmentShader);
            }
        });

        // background paper
        glContext.addProgram(backgroundProgramName, new GLProgramAccessor() {

            @Override
            public GLProgram getGLProgram() throws GLException {
                return createBackgroundTextureProgram();
            }
        });

        return glContext;
    }

    /**
     * line painting program
     */
    private static GLProgram createPaintProgram(int basicVertexShader,
            int basicFragmentShader) throws GLException {
        // basic program
        GLProgram paintProgram = new GLProgram(linePaintingProgramName);
        paintProgram.setVertexShader(basicVertexShader);
        paintProgram.setFragmentShader(basicFragmentShader);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexPositionVariableName,
                GLPaintingVertex.vertexPositionLocation);
        paintProgram.addInputLocation(GLPaintingVertex.vertexUVVariableName,
                GLPaintingVertex.vertexUVLocation);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexNormalVariableName,
                GLPaintingVertex.vertexNormalLocation);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexCurvatureVariableName,
                GLPaintingVertex.vertexCurvatureLocation);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexThicknessVariableName,
                GLPaintingVertex.vertexThicknessLocation);
        paintProgram.addInputLocation(GLPaintingVertex.vertexColorVariableName,
                GLPaintingVertex.vertexColorLocation);
        paintProgram.addInputLocation(GLPaintingVertex.vertexMaxUVariableName,
                GLPaintingVertex.vertexMaxULocation);
        paintProgram.addInputLocation(
                GLPaintingVertex.vertexPaperUVVariableName,
                GLPaintingVertex.vertexPaperUVLocation);
        paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(screenWidthUniformVarName);
        paintProgram.addUniform(screenHeightUniformVarName);
        paintProgram.addUniform(paperTextureUniformVarName);
        paintProgram.addUniform(brushTextureUniformVarName);
        paintProgram.addUniform(brushWidthUniformVarName);
        paintProgram.addUniform(brushHeightUniformVarName);
        paintProgram.addUniform(brushStartWidthUniformVarName);
        paintProgram.addUniform(brushEndWidthUniformVarName);
        paintProgram.addUniform(brushScaleUniformVarName);
        paintProgram.addUniform(paperScaleUniformVarName);
        paintProgram.addUniform(paperDensityUniformVarName);
        paintProgram.addUniform(brushDensityUniformVarName);
        paintProgram.addUniform(strokePressureUniformVarName);
        paintProgram.addUniform(sharpnessUniformVarName);
        paintProgram.addUniform(strokePressureVariationAmplitudeUniformVarName);
        paintProgram
                .addUniform(strokePressureVariationWavelengthUniformVarName);
        paintProgram.addUniform(strokeShiftVariationAmplitudeUniformVarName);
        paintProgram.addUniform(strokeShiftVariationWavelengthUniformVarName);
        paintProgram
                .addUniform(strokeThicknessVariationAmplitudeUniformVarName);
        paintProgram
                .addUniform(strokeThicknessVariationWavelengthUniformVarName);
        paintProgram.addUniform(globalOpacityUniformVarName);
        paintProgram.addUniform(objectOpacityUniformVarName);
        paintProgram.addUniform(textureScaleFactorUniformVarName);

        return paintProgram;
    }

    /**
     * line painting program
     */
    private static GLProgram createBezierProgram(int basicVertexShader,
            int basicFragmentShader) throws GLException {
        // basic program
        GLProgram paintProgram = new GLProgram(bezierLineProgramName);
        paintProgram.setVertexShader(basicVertexShader);
        paintProgram.setFragmentShader(basicFragmentShader);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexPositionVariableName,
                GLBezierShadingVertex.vertexPositionLocation);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexUVVariableName,
                GLBezierShadingVertex.vertexUVLocation);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexColorVariableName,
                GLBezierShadingVertex.vertexColorLocation);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexLineWidthVariableName,
                GLBezierShadingVertex.vertexLineWidthLocation);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexMaxUVariableName,
                GLBezierShadingVertex.vertexMaxULocation);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexP0VariableName,
                GLBezierShadingVertex.vertexP0Location);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexP1VariableName,
                GLBezierShadingVertex.vertexP1Location);
        paintProgram.addInputLocation(
                GLBezierShadingVertex.vertexP2VariableName,
                GLBezierShadingVertex.vertexP2Location);

        paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        paintProgram.addUniform(screenWidthUniformVarName);
        paintProgram.addUniform(screenHeightUniformVarName);
        paintProgram.addUniform(brushTextureUniformVarName);
        paintProgram.addUniform(brushWidthUniformVarName);
        paintProgram.addUniform(brushHeightUniformVarName);
        paintProgram.addUniform(brushScaleUniformVarName);
        paintProgram.addUniform(globalOpacityUniformVarName);
        paintProgram.addUniform(objectOpacityUniformVarName);
        paintProgram.addUniform(colorTexture1UniformVarName);

        return paintProgram;
    }

    /**
     * @throws GLException
     */
    private static GLProgram createBasicProgram() throws GLException {
        int basicVertexShader = GLProgram
                .createVertexShader("./src/main/resources/shaders/basic.vert.glsl");
        int basicFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/basic.frag.glsl");
        // basic program
        GLProgram basicProgram = new GLProgram(basicProgramName);
        basicProgram.setVertexShader(basicVertexShader);
        basicProgram.setFragmentShader(basicFragmentShader);
        basicProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        basicProgram.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        basicProgram.addInputLocation(GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        return basicProgram;
    }

    /**
     * @throws GLException
     */
    private static GLProgram createScreenspaceColorProgram(
            int screenspaceVertexShader) throws GLException {
        int screenspaceFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/polygon.color.frag.glsl");
        // basic program
        GLProgram screenspaceColorProgram = new GLProgram(
                screenspaceColorProgramName);
        screenspaceColorProgram.setVertexShader(screenspaceVertexShader);
        screenspaceColorProgram.setFragmentShader(screenspaceFragmentShader);
        screenspaceColorProgram.addInputLocation(
                GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        screenspaceColorProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        screenspaceColorProgram.addInputLocation(
                GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        screenspaceColorProgram.addUniform(globalOpacityUniformVarName);
        screenspaceColorProgram.addUniform(objectOpacityUniformVarName);

        return screenspaceColorProgram;
    }

    /**
     * @throws GLException
     */
    private static GLProgram createScreenspaceTextureProgram(
            int screenspaceVertexShader) throws GLException {
        int screenspaceFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/polygon.texture.frag.glsl");
        // basic program
        GLProgram screenspaceTextureProgram = new GLProgram(
                screenspaceTextureProgramName);
        screenspaceTextureProgram.setVertexShader(screenspaceVertexShader);
        screenspaceTextureProgram.setFragmentShader(screenspaceFragmentShader);
        screenspaceTextureProgram.addInputLocation(
                GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        screenspaceTextureProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        screenspaceTextureProgram.addInputLocation(
                GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        screenspaceTextureProgram.addUniform(globalOpacityUniformVarName);
        screenspaceTextureProgram.addUniform(objectOpacityUniformVarName);
        screenspaceTextureProgram.addUniform(colorTexture1UniformVarName);
        screenspaceTextureProgram.addUniform(textureScaleFactorUniformVarName);
        return screenspaceTextureProgram;
    }

    /**
     * @throws GLException
     */
    private static GLProgram createBackgroundTextureProgram()
            throws GLException {
        int backgroundVertexShader = GLProgram
                .createVertexShader("./src/main/resources/shaders/bg.vert.glsl");
        int backgroundFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/bg.frag.glsl");
        // basic program
        GLProgram backgroundTextureProgram = new GLProgram(
                backgroundProgramName);
        backgroundTextureProgram.setVertexShader(backgroundVertexShader);
        backgroundTextureProgram.setFragmentShader(backgroundFragmentShader);
        backgroundTextureProgram.addInputLocation(
                GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        backgroundTextureProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        backgroundTextureProgram.addUniform(colorTexture1UniformVarName);

        return backgroundTextureProgram;
    }

    /**
     * @param worldspaceVertexShader
     * @throws GLException
     */
    private static GLProgram createWorldspaceColorProgram(
            int worldspaceVertexShader) throws GLException {

        int colorFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/polygon.color.frag.glsl");
        // color program
        GLProgram colorProgram = new GLProgram(worldspaceColorProgramName);
        colorProgram.setVertexShader(worldspaceVertexShader);
        colorProgram.setFragmentShader(colorFragmentShader);
        colorProgram.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        colorProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        colorProgram.addInputLocation(GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        colorProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        colorProgram.addUniform(screenWidthUniformVarName);
        colorProgram.addUniform(screenHeightUniformVarName);
        colorProgram.addUniform(globalOpacityUniformVarName);
        colorProgram.addUniform(objectOpacityUniformVarName);
        colorProgram.addUniform(colorTexture1UniformVarName);

        return colorProgram;
    }

    /**
     * @param worldspaceVertexShader
     * @throws GLException
     */
    private static GLProgram createWorldspaceTextureProgram(
            int worldspaceVertexShader) throws GLException {

        int textureFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/polygon.texture.frag.glsl");
        // color program
        GLProgram textureProgram = new GLProgram(worldspaceTextureProgramName);
        textureProgram.setVertexShader(worldspaceVertexShader);
        textureProgram.setFragmentShader(textureFragmentShader);
        textureProgram.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        textureProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        textureProgram.addInputLocation(GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        textureProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(m02ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(m00ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(m11ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(m12ModelToViewMatrixUniformVarName);
        textureProgram.addUniform(screenWidthUniformVarName);
        textureProgram.addUniform(screenHeightUniformVarName);
        textureProgram.addUniform(globalOpacityUniformVarName);
        textureProgram.addUniform(objectOpacityUniformVarName);
        textureProgram.addUniform(colorTexture1UniformVarName);
        textureProgram.addUniform(textureScaleFactorUniformVarName);

        return textureProgram;
    }

    /**
     * @param worldspaceVertexShader
     * @throws GLException
     */
    private static GLProgram createScreenspaceAntialiasedProgram(
            int screenspaceVertexShader) throws GLException {

        int antialiasedFragmentShader = GLProgram
                .createFragmentShader("./src/main/resources/shaders/antialiased.frag.glsl");
        // color program
        GLProgram antialisedProgram = new GLProgram(
                screenspaceAntialiasedTextureProgramName);
        antialisedProgram.setVertexShader(screenspaceVertexShader);
        antialisedProgram.setFragmentShader(antialiasedFragmentShader);
        antialisedProgram.addInputLocation(GLSimpleVertex.vertexUVVariableName,
                GLSimpleVertex.vertexUVLocation);
        antialisedProgram.addInputLocation(
                GLSimpleVertex.vertexPositionVariableName,
                GLSimpleVertex.vertexPostionLocation);
        antialisedProgram.addInputLocation(
                GLSimpleVertex.vertexColorVariableName,
                GLSimpleVertex.vertexColorLocation);
        antialisedProgram.addUniform(globalOpacityUniformVarName);
        antialisedProgram.addUniform(objectOpacityUniformVarName);
        antialisedProgram.addUniform(colorTexture1UniformVarName);
        antialisedProgram.addUniform(textureScaleFactorUniformVarName);
        antialisedProgram.addUniform(antialiasingSizeUniformVarName);

        return antialisedProgram;
    }

}
