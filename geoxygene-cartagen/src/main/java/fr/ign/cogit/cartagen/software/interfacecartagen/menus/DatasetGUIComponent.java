/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.menus;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.dataset.GeographicClass;
import fr.ign.cogit.cartagen.software.dataset.PostgisDB;
import fr.ign.cogit.cartagen.software.dataset.ShapeFileDB;
import fr.ign.cogit.cartagen.software.interfacecartagen.annexes.CartAGenProgressBar;
import fr.ign.cogit.cartagen.software.interfacecartagen.annexes.ExportFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.dataloading.ImportDataFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.XMLFileFilter;
import fr.ign.cogit.cartagen.util.FileUtil;
import fr.ign.cogit.cartagen.util.LastSessionParameters;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.util.ReflectionUtil;

/**
 * This menu allows to manipulate the CartAGen datasets, import data or export
 * data, or manage persistent enrichments.
 * @author GTouya
 * 
 */
public class DatasetGUIComponent extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private List<File> recentDocs;
  private String lblRecentDocs;

  public DatasetGUIComponent(String title) {
    super(title);
    this.internationalisation();
    this.loadRecentDocs();

    this.add(new JMenuItem(new NewDocumentAction()));
    this.add(new JMenuItem(new OpenDocumentAction()));
    this.add(new JMenuItem(new SaveDocumentAction()));
    JMenu recentDocsMenu = new JMenu(this.lblRecentDocs);
    for (File recentDoc : this.recentDocs) {
      recentDocsMenu.add(new JMenuItem(new OpenDocumentAction(recentDoc)));
    }
    this.add(recentDocsMenu);
    this.addSeparator();
    this.add(new JMenuItem(new ImportDataShape1SimpleAction()));
    JMenu importDataMenu = new JMenu("Import Data");
    importDataMenu.add(new JMenuItem(new ImportDataShape1Action()));
    importDataMenu.add(new JMenuItem(new ImportDataXMLAction()));

    JMenu exportDataMenu = new JMenu("Export Data");
    exportDataMenu.add(new JMenuItem(new ExportDataToShapeAction()));
    JMenu addDataMenu = new JMenu("Add Data");
    JMenu overwriteMenu = new JMenu("Overwrite Data");
    overwriteMenu.add(new JMenuItem(new OverwriteFromShapeAction()));
    this.add(importDataMenu);
    this.add(exportDataMenu);
    this.add(addDataMenu);
    this.add(overwriteMenu);
    this.add(new JMenuItem(new SetCurrentDatasetAction()));
    this.addSeparator();
    this.add(new JMenuItem(new StartEditAction()));
    this.add(new JMenuItem(new CommitAction()));
    this.add(new JMenuItem(new BackTrackAction()));
    this.add(new JMenuItem(new EditPersistentClassesAction()));
  }

  /**
   * Set the internationalised Strings of the component.
   */
  private void internationalisation() {
    this.lblRecentDocs = I18N.getString("DatasetMenu.lblRecentDocs");
  }

  private void loadRecentDocs() {
    this.recentDocs = new ArrayList<File>();
    LastSessionParameters params = LastSessionParameters.getInstance();
    if (params.hasParameter("Recent CartAGenDoc 1")) {
      String path = (String) params.getParameterValue("Recent CartAGenDoc 1");
      this.recentDocs.add(new File(path));
    }
    if (params.hasParameter("Recent CartAGenDoc 2")) {
      String path = (String) params.getParameterValue("Recent CartAGenDoc 2");
      this.recentDocs.add(new File(path));
    }
    if (params.hasParameter("Recent CartAGenDoc 3")) {
      String path = (String) params.getParameterValue("Recent CartAGenDoc 3");
      this.recentDocs.add(new File(path));
    }
    if (params.hasParameter("Recent CartAGenDoc 4")) {
      String path = (String) params.getParameterValue("Recent CartAGenDoc 4");
      this.recentDocs.add(new File(path));
    }
    if (params.hasParameter("Recent CartAGenDoc 5")) {
      String path = (String) params.getParameterValue("Recent CartAGenDoc 5");
      this.recentDocs.add(new File(path));
    }
  }

  private void saveRecentDocs() throws TransformerException, IOException {
    LastSessionParameters params = LastSessionParameters.getInstance();
    for (int i = 1; i <= this.recentDocs.size(); i++) {
      params.setParameter("Recent CartAGenDoc " + i, this.recentDocs.get(i - 1)
          .getPath(), new HashMap<String, String>());
    }
  }

  /**
   * Create a new empty dataset, with its zone details, in which data can be
   * added later.
   * @author GTouya
   * 
   */
  class NewDocumentAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      String name = JOptionPane
          .showInputDialog("Enter a name for the new CartAGen Document");

      CartAGenDocOld doc = CartAGenDocOld.getInstance();
      doc.setName(name);
      doc.setPostGisDb(PostgisDB.get(name, true));
    }

    public NewDocumentAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Create a new empty CartAGen document");
      this.putValue(Action.NAME, "New CartAGen Document");
    }
  }

  /**
   * Opens a new document previously stored in a XML file.
   * @author GTouya
   * 
   */
  class OpenDocumentAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private File docFile;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartagenApplication appl = CartagenApplication.getInstance();
      File file = this.docFile;
      if (this.docFile == null) {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("src/main/resources/xml/"));
        fc.setFileFilter(new XMLFileFilter());
        int returnVal = fc.showOpenDialog(appl.getFrame());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
          return;
        }
        file = fc.getSelectedFile();
      }

      try {
        CartAGenDocOld.loadDocFromXml(file);
        CartagenApplication.getInstance().getFrame().getVisuPanel()
            .centerAndZoom(CartAGenDocOld.getInstance().getDisplayEnvelope());
        CartagenApplication.getInstance().setLayerGroup(
            CartAGenDocOld.getInstance().getLayerGroup());
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .loadLayers(
                CartAGenDocOld.getInstance().getCurrentDataset(),
                CartagenApplication.getInstance().getLayerGroup().symbolisationDisplay);
        CartagenApplication
            .getInstance()
            .getLayerGroup()
            .loadInterfaceWithLayers(
                CartagenApplication.getInstance().getFrame().getLayerManager(),
                CartAGenDocOld.getInstance().getCurrentDataset().getSymbols());
      } catch (ParserConfigurationException e) {
        e.printStackTrace();
      } catch (SAXException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }

    public OpenDocumentAction() {
      this.putValue(Action.SHORT_DESCRIPTION, "Open a saved CartAGen document");
      this.putValue(Action.NAME, "Open CartAGen Document");
    }

    public OpenDocumentAction(File file) {
      this.putValue(Action.NAME, file.getPath());
      this.docFile = file;
    }
  }

  /**
   * Action that allows to open a CartAGen dataset previously stored in XML into
   * the current document. The new dataset is put as current dataset.
   * @author GTouya
   * 
   */
  class ImportDataXMLAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private CartAGenDB database;
    private int scale;
    private CartagenApplication appl;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      this.appl = CartagenApplication.getInstance();
      JFileChooser fc = new JFileChooser();
      fc.setCurrentDirectory(new File(DatasetGUIComponent.class
          .getResource("/src/main/resources/xml/").getPath()
          .replaceAll("%20", " ")));
      fc.setFileFilter(new XMLFileFilter());
      int returnVal = fc.showOpenDialog(this.appl.getFrame());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File file = fc.getSelectedFile();
      this.database = null;
      this.scale = 25000;
      try {
        Class<? extends CartAGenDB> classObj = CartAGenDB.readType(file);
        this.database = classObj.getConstructor(File.class).newInstance(file);
        this.scale = CartAGenDB.readScale(file);
      } catch (ParserConfigurationException e) {
        e.printStackTrace();
      } catch (SAXException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }

      if (this.database == null) {
        return;
      }

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          CartAGenProgressBar bar = CartAGenProgressBar.getInstance();
          bar.setTitle("Data loading from Shapefiles");
          bar.setVisible(true);
        }
      });
      // now clear the Generalisation Dataset
      CartAGenDataSet dataset = new CartAGenDataSet();
      // and apply the new CartAGenDataSet
      dataset.setCartAGenDB(this.database);
      // add the database to the document
      CartAGenDocOld.getInstance().addDatabase(this.database.getName(),
          this.database);
      // put the new dataset as the current one
      CartAGenDocOld.getInstance().setCurrentDataset(dataset);
      // populate the generalisation dataset from the cartagen dataset
      this.database.populateDataset(this.scale);
      while (!((ShapeFileDB) this.database).getTask().exit) {
      }
      String systemPath = ((ShapeFileDB) this.database).getSystemPath();
      CartagenApplication.getInstance().setCheminDonnees(systemPath);
      this.appl.initialiserPositionGeographique(false);
      CartagenApplication.getInstance().initGeneralisation();

    }

    public ImportDataXMLAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Open a CartAGen dataset stored in XML");
      this.putValue(Action.NAME, "From XML");
    }

    public CartAGenDB getDatabase() {
      return this.database;
    }

    public void setDatabase(CartAGenDB dataset) {
      this.database = dataset;
    }

    public int getScale() {
      return this.scale;
    }

    public void setScale(int scale) {
      this.scale = scale;
    }

    public CartagenApplication getAppl() {
      return this.appl;
    }

    public void setAppl(CartagenApplication appl) {
      this.appl = appl;
    }
  }

  /**
   * Action that allows to save the CartAGen document to XML.
   * @author GTouya
   * 
   */
  class SaveDocumentAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartagenApplication appl = CartagenApplication.getInstance();
      CartAGenDocOld doc = CartAGenDocOld.getInstance();
      doc.saveWindow(appl);
      File file = doc.getXmlFile();
      if (file == null) {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("src/main/resources/xml/"));
        int returnVal = fc.showSaveDialog(appl.getFrame());
        if (returnVal != JFileChooser.APPROVE_OPTION) {
          return;
        }
        file = fc.getSelectedFile();
        doc.setXmlFile(file);
      }

      try {
        doc.saveToXml(file);
        // update the recent docs list
        if (DatasetGUIComponent.this.recentDocs.contains(file)) {
          DatasetGUIComponent.this.recentDocs.remove(file);
          DatasetGUIComponent.this.recentDocs.add(0, file);
        } else {
          DatasetGUIComponent.this.recentDocs.add(0, file);
          if (DatasetGUIComponent.this.recentDocs.size() == 6) {
            DatasetGUIComponent.this.recentDocs.remove(5);
          }
        }
        DatasetGUIComponent.this.saveRecentDocs();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (TransformerException e) {
        e.printStackTrace();
      }
    }

    public SaveDocumentAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Save the CartAGen dataset to XML");
      this.putValue(Action.NAME, "Save Document");
    }
  }

  // /////////////////////////////////////////////////////////////////
  // /// IMPORT/EXPORT DATA ACTIONS /////
  // /////////////////////////////////////////////////////////////////

  /**
   * Action that allows to import data from shapefiles using the simple system
   * developed during the training course of M. Vieira.
   * @author GTouya
   * 
   */
  class ImportDataShape1Action extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      if (CartAGenDocOld.getInstance().getName() == null) {
        JOptionPane.showMessageDialog(CartagenApplication.getInstance()
            .getFrame(), "No CartAGen document open, create or load one first",
            "Warning", JOptionPane.WARNING_MESSAGE);
        return;
      }
      ImportDataFrame frame = ImportDataFrame.getInstance(false);
      frame.setVisible(true);
    }

    public ImportDataShape1Action() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Import from Shapefile using the plug-in from M. Vieira");
      this.putValue(Action.NAME, "From Shapefile");
    }

  }

  /**
   * Action that allows to import data from shapefiles using the simple system
   * developed during the training course of M. Vieira. This is a simple copy of
   * it that gives a name by default for the cartagen document in the case of
   * not having one.
   * @author KJaara
   * 
   */
  class ImportDataShape1SimpleAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      if (CartAGenDocOld.getInstance().getName() == null) {
        /*
         * JOptionPane.showMessageDialog(CartagenApplication.getInstance()
         * .getFrame(), "No CartAGen document open, create or load one first",
         * "Warning", JOptionPane.WARNING_MESSAGE);
         */

        CartAGenDocOld doc = CartAGenDocOld.getInstance();
        doc.setName("Kusay");
        doc.setPostGisDb(PostgisDB.get("Kusay", true));

        // return;
      }

      /*
       * if (CartAGenDoc.getInstance().getName() == null) {
       * JOptionPane.showMessageDialog(CartagenApplication.getInstance()
       * .getFrame(), "No CartAGen document open, create or load one first",
       * "Warning", JOptionPane.WARNING_MESSAGE); return; }
       */
      ImportDataFrame frame = ImportDataFrame.getInstance(false);
      frame.setVisible(true);
    }

    public ImportDataShape1SimpleAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Import from Shapefile using the plug-in from M. Vieira");
      this.putValue(Action.NAME, "Simple import From Shapefile");
    }

  }

  /**
   * Action that allows to import data from shapefiles using the symbolisation
   * files, developed by Kusay Jaara.
   * @author GTouya
   * 
   */
  class ImportDataShape2Action extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      /*
       * LoadingFrameMultiBD form = new LoadingFrameMultiBD(); form.pack();
       * 
       * form.setLocation(500,300); form.setVisible(true);
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * 
       * CartagenApplication.getInstance().loadAndEnrichData2(form.bdsource,form.
       * echelle); CartagenApplication.getInstance().initGeneralisation();
       */
    }

    public ImportDataShape2Action() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Import from Shapefile using the Symbol files");
      this.putValue(Action.NAME, "Import From Shapefile");
    }
  }

  /**
   * Action that allows to export data from the CartAGen classes to shapefiles.
   * 
   * @author GTouya
   * 
   */
  class ExportDataToShapeAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      ExportFrame.get().setVisible(true);
    }

    public ExportDataToShapeAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Export from CartAGen classes to Shapefiles");
      this.putValue(Action.NAME, "Export To Shapefile");
    }
  }

  /**
   * Action that allows to overwrite some of the CartAGen classes by the initial
   * shapefiles of the DB.
   * 
   * @author GTouya
   * 
   */
  class OverwriteFromShapeAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      OverwriteFromShapeFrame frame = new OverwriteFromShapeFrame();
      frame.setVisible(true);
    }

    public OverwriteFromShapeAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Overwrite some CartAGen classes from initial Shapefiles");
      this.putValue(Action.NAME, "Overwrite from Shapefile");
    }

    class OverwriteFromShapeFrame extends JFrame implements ActionListener {

      /****/
      private static final long serialVersionUID = 1L;
      private Map<JCheckBox, GeographicClass> checks = new HashMap<JCheckBox, GeographicClass>();

      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("cancel")) {
          this.setVisible(false);
        } else if (e.getActionCommand().equals("ok")) {
          CartAGenDB db = CartAGenDocOld.getInstance().getCurrentDataset()
              .getCartAGenDB();
          for (JCheckBox check : this.checks.keySet()) {
            if (check.isSelected()) {
              GeographicClass geoClass = this.checks.get(check);
              db.overwrite(geoClass);
            }
          }
          this.setVisible(false);
        }
      }

      public OverwriteFromShapeFrame() {
        super("Shapefiles to overwrite");
        this.setSize(200, 400);
        // System.out.println(CartAGenDocOld.getInstance().getCurrentDataset());
        // System.out.println(CartAGenDocOld.getInstance().getCurrentDataset()
        // .getCartAGenDB());
        // System.out.println(CartAGenDocOld.getInstance().getCurrentDataset()
        // .getCartAGenDB().getClasses());
        for (GeographicClass geoClass : CartAGenDocOld.getInstance()
            .getCurrentDataset().getCartAGenDB().getClasses()) {
          JCheckBox check = new JCheckBox(geoClass.getName());
          this.checks.put(check, geoClass);
          this.getContentPane().add(check);
        }
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(this);
        okBtn.setActionCommand("ok");
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(this);
        cancelBtn.setActionCommand("cancel");
        JPanel pButtons = new JPanel();
        pButtons.add(okBtn);
        pButtons.add(cancelBtn);
        pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));
        this.getContentPane().add(pButtons);
        this.getContentPane().setLayout(
            new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.pack();
      }
    }
  }

  /**
   * Set the current dataset (the one that is used in generalisation algorithms)
   * from one of the datasets related to the databases of the document.
   * 
   * @author GTouya
   * 
   */
  class SetCurrentDatasetAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      SetCurrentDatasetFrame frame = new SetCurrentDatasetFrame();
      frame.setVisible(true);
    }

    public SetCurrentDatasetAction() {
      this.putValue(
          Action.SHORT_DESCRIPTION,
          "Set the current dataset from one of the datasets related to the databases of the document");
      this.putValue(Action.NAME, "Set current dataset");
    }
  }

  class SetCurrentDatasetFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JComboBox databases;
    private CartAGenDataSet current;

    public SetCurrentDatasetFrame() {
      super("Set Current Dataset");
      this.setSize(400, 100);

      // a panel to choose the current database among the databases of the
      // document
      JPanel p1 = new JPanel();
      this.databases = new JComboBox(CartAGenDocOld.getInstance()
          .getDatabases().keySet().toArray());
      this.databases.setPreferredSize(new Dimension(150, 20));
      this.databases.setMaximumSize(new Dimension(150, 20));
      this.databases.setMinimumSize(new Dimension(150, 20));
      p1.add(new JLabel("Databases of the document: "));
      p1.add(this.databases);
      p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));

      // ok/cancel panel
      JPanel p2 = new JPanel();
      JButton btnOK = new JButton("OK");
      btnOK.addActionListener(this);
      btnOK.setActionCommand("ok");
      btnOK.setPreferredSize(new Dimension(100, 50));
      JButton btnCancel = new JButton("Cancel");
      btnCancel.addActionListener(this);
      btnCancel.setActionCommand("cancel");
      btnCancel.setPreferredSize(new Dimension(100, 50));
      p2.add(btnOK);
      p2.add(btnCancel);
      p2.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
      p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));

      // final layout
      this.getContentPane().add(p1);
      this.getContentPane().add(p2);
      this.getContentPane().setLayout(
          new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("cancel")) {
        this.setVisible(false);
      } else if (e.getActionCommand().equals("ok")) {
        this.current = CartAGenDocOld.getInstance().getDatabases()
            .get(this.databases.getSelectedItem()).getDataSet();
        CartAGenDocOld.getInstance().setCurrentDataset(this.current);
        this.setVisible(false);
      }
    }

  }

  /**
   * Action that allows to start an edit session to modify and store the
   * modifications on the persistent objects.
   * 
   * @author GTouya
   * 
   */
  class StartEditAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      if (CartAGenDocOld.getInstance().getPostGisSession() == null) {
        // open a connection with the current PostGISDB
        Configuration hibConfig = new Configuration();
        hibConfig = hibConfig.configure(new File(PostgisDB
            .getDefaultConfigPath()));
        hibConfig.setProperty("hibernate.connection.url", PostgisDB.getUrl());

        // get the persistent classes
        for (Class<?> classObj : CartAGenDocOld.getInstance()
            .getCurrentDataset().getCartAGenDB().getPersistentClasses()) {
          // add the persistent class to the hibernate configuration
          hibConfig.addAnnotatedClass(classObj);
          // get the annotated interfaces or superclasses of the persistent
          // class
          Set<Class<?>> superclasses = ReflectionUtil
              .getSuperClassesAndInterfaces(classObj);

          // add the annotated interfaces to the hibernate configuration
          for (Class<?> c : superclasses) {
            if (c.isAnnotationPresent(Entity.class)
                || c.isAnnotationPresent(MappedSuperclass.class)) {
              hibConfig.addAnnotatedClass(c);
            }
          }
        }

        // start the transaction
      //  Session session = hibConfig.buildSessionFactory().openSession(
        //      PostgisDB.getConnection());
        Session session = hibConfig.buildSessionFactory().openSession();
        CartAGenDocOld.getInstance().setPostGisSession(session);
        session.beginTransaction();
      } else {
        CartAGenDocOld.getInstance().getPostGisSession().beginTransaction();
      }

    }

    public StartEditAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Start an edit session on the PostGIS DB that stores persistent objects");
      this.putValue(Action.NAME, "Start Persistent Edit");
    }
  }

  /**
   * Action that allows to commit the modification made on persistent object
   * during an edit session.
   * 
   * @author GTouya
   * 
   */
  class CommitAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      int result = JOptionPane.showConfirmDialog(CartagenApplication
          .getInstance().getFrame(), "Are you sure you want to Commit Edits ?");
      if (result != JOptionPane.OK_OPTION) {
        return;
      }

      CartAGenDataSet dataset = CartAGenDocOld.getInstance()
          .getCurrentDataset();
      // loop on the persistent classes to save their features in the current DB
      for (Class<?> classObj : CartAGenDocOld.getInstance().getCurrentDataset()
          .getCartAGenDB().getPersistentClasses()) {
        if (!classObj.isAnnotationPresent(Entity.class)) {
          continue;
        }
        // get all the features of classObj in the current dataset
        IPopulation<? extends IGeneObj> pop = dataset.getCartagenPop(
            dataset.getPopNameFromClass(classObj), "");
        for (IGeneObj obj : pop) {
          try {
            obj.updateRelationIds();
          } catch (SecurityException e) {
            e.printStackTrace();
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (NoSuchMethodException e) {
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          } catch (NoSuchFieldException e) {
            e.printStackTrace();
          }
          CartAGenDocOld.getInstance().getPostGisSession().saveOrUpdate(obj);
        }
      }
      // commit the transaction than close the session
      CartAGenDocOld.getInstance().getPostGisSession().getTransaction()
          .commit();
    }

    public CommitAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Commit the modifications on persistent objects in the PostGIS DB");
      this.putValue(Action.NAME, "Commit Persistent Edits");
    }
  }

  /**
   * Action that allows to backtrack the modifications made on persistent
   * objects during an edit session and close the session.
   * 
   * @author GTouya
   * 
   */
  class BackTrackAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      int result = JOptionPane.showConfirmDialog(CartagenApplication
          .getInstance().getFrame(),
          "Are you sure you want to Backtrack Edits (edits will be lost) ?");
      if (result != JOptionPane.OK_OPTION) {
        return;
      }

      // rollback the transaction than close the session
      CartAGenDocOld.getInstance().getPostGisSession().getTransaction()
          .rollback();
    }

    public BackTrackAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Backtrack the modifications on persistent objects and close the session");
      this.putValue(Action.NAME, "Backtrack Persistent Edits");
    }
  }

  /**
   * Action that allows to add or remove a class to the persistent set of a
   * database opened in the current document.
   * 
   * @author GTouya
   * 
   */
  class EditPersistentClassesAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      EditPersistentClassesFrame frame;
      try {
        frame = new EditPersistentClassesFrame();
        frame.setVisible(true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public EditPersistentClassesAction() {
      this.putValue(
          Action.SHORT_DESCRIPTION,
          "Edit (add or remove) the persistent classes of a CartAGenDatabase of the current document");
      this.putValue(Action.NAME, "Edit Persistent Classes");
    }
  }

  /**
   * Frame that allows to edit the persistent classes in the databases of the
   * document
   * @author gtouya
   * 
   */
  class EditPersistentClassesFrame extends JFrame implements ActionListener,
      ItemListener, ChangeListener {

    private static final long serialVersionUID = 1L;
    private JComboBox cbDatabases, cbClasses;
    private JList persistList;
    private CartAGenDB currentDb;
    private Set<Class<?>> existingClasses;
    private Set<Class<?>> additionalClasses;
    private Map<String, Class<? extends IFeature>> mapNameClass;
    private JCheckBox chkPersist;

    public EditPersistentClassesFrame() {
      super("Set Current Dataset");
      this.setSize(500, 300);
      this.getExistingClasses();
      // System.out.println(this.existingClasses.size());

      // a panel to choose the current database among the databases of the
      // document
      JPanel p1 = new JPanel();
      // the list with the persistent classes of the selected database
      this.persistList = new JList();
      this.persistList.setPreferredSize(new Dimension(150, 400));
      this.persistList.setMaximumSize(new Dimension(150, 400));
      this.persistList.setMinimumSize(new Dimension(150, 400));
      this.persistList
          .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      // the combo box with the databases
      this.cbDatabases = new JComboBox(CartAGenDocOld.getInstance()
          .getDatabases().values().toArray());
      this.cbDatabases.setPreferredSize(new Dimension(150, 20));
      this.cbDatabases.setMaximumSize(new Dimension(150, 20));
      this.cbDatabases.setMinimumSize(new Dimension(150, 20));
      this.cbDatabases.addItemListener(this);
      this.cbDatabases.setSelectedIndex(0);
      this.currentDb = (CartAGenDB) this.cbDatabases.getSelectedItem();
      this.chkPersist = new JCheckBox("Persistent?");
      this.chkPersist.addChangeListener(this);
      p1.add(new JScrollPane(this.persistList));
      p1.add(Box.createHorizontalGlue());
      p1.add(new JLabel("Databases of the document: "));
      p1.add(this.cbDatabases);
      p1.add(Box.createHorizontalGlue());
      p1.add(this.chkPersist);
      p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));

      // button panel
      JPanel p2 = new JPanel();
      this.cbClasses = new JComboBox();
      this.cbClasses.setPreferredSize(new Dimension(150, 20));
      this.cbClasses.setMaximumSize(new Dimension(150, 20));
      this.cbClasses.setMinimumSize(new Dimension(150, 20));
      JButton btnAdd = new JButton("Add");
      btnAdd.addActionListener(this);
      btnAdd.setActionCommand("add");
      btnAdd.setPreferredSize(new Dimension(100, 50));
      JButton btnRemove = new JButton("Remove");
      btnRemove.addActionListener(this);
      btnRemove.setActionCommand("remove");
      btnRemove.setPreferredSize(new Dimension(100, 50));
      JButton btnAddAll = new JButton("Add All");
      btnAddAll.addActionListener(this);
      btnAddAll.setActionCommand("add all");
      btnAddAll.setPreferredSize(new Dimension(100, 50));
      p2.add(this.cbClasses);
      p2.add(Box.createHorizontalGlue());
      p2.add(btnAdd);
      p2.add(Box.createHorizontalGlue());
      p2.add(btnAddAll);
      p2.add(Box.createHorizontalGlue());
      p2.add(btnRemove);
      p2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));

      // final layout
      this.getContentPane().add(p1);
      this.getContentPane().add(p2);
      this.getContentPane().setLayout(
          new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.updatePersistentClasses();
      this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("remove")) {
        // loop on the selected classes in the JList
        for (Object className : this.persistList.getSelectedValues()) {
          // get the Class object from its simple name
          Class<? extends IFeature> classObj = this.mapNameClass.get(className);
          // remove it from the DB storage set
          this.currentDb.getPersistentClasses().remove(classObj);
        }
        this.updatePersistentClasses();
      } else if (e.getActionCommand().equals("add")) {
        Object className = this.cbClasses.getSelectedItem();
        Class<? extends IFeature> classObj = this.mapNameClass.get(className);
        this.currentDb.getPersistentClasses().add(classObj);
        this.updatePersistentClasses();
      } else if (e.getActionCommand().equals("add all")) {
        // loop on all the classes in the DB that aren't persistent
        for (int i = 0; i < this.cbClasses.getModel().getSize(); i++) {
          Object className = this.cbClasses.getModel().getElementAt(i);
          Class<? extends IFeature> classObj = this.mapNameClass.get(className);
          this.currentDb.getPersistentClasses().add(classObj);
        }
        this.updatePersistentClasses();
      }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
      this.currentDb = (CartAGenDB) this.cbDatabases.getSelectedItem();
      this.chkPersist.setSelected(this.currentDb.isPersistent());
      this.updatePersistentClasses();
    }

    private void updatePersistentClasses() {
      DefaultListModel model1 = new DefaultListModel();
      DefaultComboBoxModel cbModel = this.buildComboModelFromProject();
      for (Class<?> persistClass : this.currentDb.getPersistentClasses()) {
        if (this.existingClasses.contains(persistClass)) {
          continue;
        }
        model1.addElement(persistClass.getSimpleName());
        cbModel.removeElement(persistClass.getSimpleName());
      }
      cbModel.removeElement("");
      this.persistList.setModel(model1);
      this.cbClasses.setModel(cbModel);
      this.pack();
    }

    private DefaultComboBoxModel buildComboModelFromProject() {
      DefaultComboBoxModel cbModel = new DefaultComboBoxModel();
      ArrayList<String> classesList = new ArrayList<String>();
      for (Class<?> addClass : this.additionalClasses) {
        classesList.add(addClass.getSimpleName());
      }
      Collections.sort(classesList);
      for (String className : classesList) {
        cbModel.addElement(className);
      }
      return cbModel;
    }

    @SuppressWarnings("unchecked")
    private void getExistingClasses() {
      this.existingClasses = new HashSet<Class<?>>();
      this.additionalClasses = new HashSet<Class<?>>();
      this.mapNameClass = new HashMap<String, Class<? extends IFeature>>();
      // get the directory of the package of this class
      Package pack = this.getClass().getPackage();
      String name = pack.getName();
      name = name.replace('.', '/');
      name.replaceAll("%20", " ");
      if (!name.startsWith("/")) {
        name = "/" + name;
      }
      URL pathName = this.getClass().getResource(name);
      File directory = new File(pathName.getFile());
      // get the parent directories to get fr.ign.cogit.cartagen package
      while (!directory.getName().equals("cartagen")) {
        directory = directory.getParentFile();
      }
      String cleanDir = directory.toString().replaceAll("%20", " ");
      directory = new File(cleanDir);
      List<File> files = FileUtil.getAllFilesInDir(directory);
      for (File file : files) {
        if (!file.getName().endsWith(".class")) {
          continue;
        }
        if (file.getName().substring(0, file.getName().length() - 6)
            .equals("GothicObjectDiffusion")) {
          continue;
        }
        String path = file.getPath().substring(file.getPath().indexOf("fr"));
        String classname = FileUtil.changeFileNameToClassName(path);
        try {
          // Try to create an instance of the object
          Class<?> classObj = Class.forName(classname);
          if (classObj.isInterface()) {
            continue;
          }
          if (classObj.isLocalClass()) {
            continue;
          }
          if (classObj.isMemberClass()) {
            continue;
          }
          if (classObj.isEnum()) {
            continue;
          }
          if (Modifier.isAbstract(classObj.getModifiers())) {
            continue;
          }
          // test if the class inherits from IGeneObj
          if (IGeneObj.class.isAssignableFrom(classObj)) {
            Class<? extends IGeneObj> c = (Class<? extends IGeneObj>) classObj;
            this.existingClasses.add(c);
            this.mapNameClass.put(c.getSimpleName(), c);
          } else if (classObj.isAnnotationPresent(Entity.class)) {
            Class<? extends IFeature> c = (Class<? extends IGeneObj>) classObj;
            this.additionalClasses.add(c);
            this.mapNameClass.put(c.getSimpleName(), c);
          }
        } catch (ClassNotFoundException cnfex) {
          cnfex.printStackTrace();
        }
      }

      // now do the same for the Open Source CartAGen
      pack = IGeneObj.class.getPackage();
      name = pack.getName().replace('.', '/');
      name.replaceAll("%20", " ");
      if (!name.startsWith("/")) {
        name = "/" + name;
      }
      pathName = IGeneObj.class.getResource(name);
      directory = new File(pathName.getFile());
      // get the parent directories to get fr.ign.cogit.cartagen package
      while (!directory.getName().equals("cartagen")) {
        directory = directory.getParentFile();
      }
      cleanDir = directory.toString().replaceAll("%20", " ");
      directory = new File(cleanDir);
      files = FileUtil.getAllFilesInDir(directory);
      for (File file : files) {
        if (!file.getName().endsWith(".class")) {
          continue;
        }
        String path = file.getPath().substring(file.getPath().indexOf("fr"));
        String classname = FileUtil.changeFileNameToClassName(path);
        try {
          // Try to create an instance of the object
          Class<?> classObj = Class.forName(classname);
          if (classObj.isInterface()) {
            continue;
          }
          if (classObj.isLocalClass()) {
            continue;
          }
          if (classObj.isMemberClass()) {
            continue;
          }
          if (classObj.isEnum()) {
            continue;
          }
          if (Modifier.isAbstract(classObj.getModifiers())) {
            continue;
          }
          // test if the class inherits from IGeneObj
          if (IGeneObj.class.isAssignableFrom(classObj)) {
            Class<? extends IGeneObj> c = (Class<? extends IGeneObj>) classObj;
            this.existingClasses.add(c);
            this.mapNameClass.put(c.getSimpleName(), c);
          } else if (classObj.isAnnotationPresent(Entity.class)) {
            Class<? extends IFeature> c = (Class<? extends IGeneObj>) classObj;
            this.additionalClasses.add(c);
            this.mapNameClass.put(c.getSimpleName(), c);
          }
        } catch (ClassNotFoundException cnfex) {
          cnfex.printStackTrace();
        }
      }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      if (this.currentDb == null) {
        return;
      }
      this.currentDb.setPersistent(this.chkPersist.isSelected());
      if (this.chkPersist.isSelected()) {
        // fill the persistent classes with the existing IGeneObj classes
        this.currentDb.getPersistentClasses().clear();
        this.currentDb.getPersistentClasses()
            .addAll(
                this.currentDb.getGeneObjImpl().filterClasses(
                    this.existingClasses));
      } else {
        // remove the existing IGeneObj classes from the persistent classes
        this.currentDb.getPersistentClasses().removeAll(this.existingClasses);
      }
    }

  }

}
