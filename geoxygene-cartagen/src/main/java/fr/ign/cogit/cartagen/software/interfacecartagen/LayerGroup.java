/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen;

import java.awt.BasicStroke;
import java.awt.Color;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;

import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.GeneralisationLegend;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.LayerManager;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.LoadedLayer;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Symbolisation;
import fr.ign.cogit.cartagen.software.interfacecartagen.menus.DataThemesGUIComponent;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolsUtil;

/**
 * for storing the the data en layers then we can use it to fill the interface
 * by passing the layer manager
 * @author kjaara
 * 
 */

public class LayerGroup extends AbstractLayerGroup {

  protected LoadedLayer layerBuilding;
  public static String LAYER_BUILDING = "layerBuilding";
  protected LoadedLayer layerTown;
  public static String LAYER_TOWN = "layerTown";
  protected LoadedLayer layerBlock;
  public static String LAYER_BLOCK = "layerBlock";
  protected LoadedLayer layerUrbanAlignment;
  public static String LAYER_URBAN_ALIGNMENT = "layerUrbanAlignment";

  protected LoadedLayer layerRoadLine;
  public static String LAYER_ROAD_LINE = "layerRoadLine";
  protected LoadedLayer layerRoadNode;
  public static String LAYER_ROAD_NODE = "layerRoadNode";
  protected LoadedLayer layerPath;
  public static String LAYER_PATH = "layerPath";
  protected LoadedLayer layerBridgePoint;
  public static String LAYER_BRIDGE_PT = "bridgePoint";

  protected LoadedLayer layerWaterLine;
  public static String LAYER_WATER_LINE = "layerWaterLine";
  protected LoadedLayer layerWaterArea;
  public static String LAYER_WATER_AREA = "layerWaterArea";
  protected LoadedLayer layerIslands;
  public static String LAYER_ISLANDS = "layerIslands";

  protected LoadedLayer layerRailwayLine;
  public static String LAYER_RAILWAY_LINE = "layerRailwayLine";

  protected LoadedLayer layerElectricityLine;
  public static String LAYER_ELECRICITY_LINE = "layerElectricityLine";

  protected LoadedLayer layerContourLine;
  public static String LAYER_CONTOUR_LINE = "layerContourLine";
  protected LoadedLayer layerSpotHeight;
  public static String LAYER_SPOT_HEIGHT = "layerSpotHeight";
  protected LoadedLayer layerDEMPixel;
  public static String LAYER_DEM_PIXEL = "layerDEMPixel";
  protected LoadedLayer layerReliefTriangle;
  public static String LAYER_RELIEF_TRIANGLE = "layerReliefTriangle";
  protected LoadedLayer layerReliefElemLine;
  public static String LAYER_RELIEF_ELEM_LINE = "layerReliefElemLine";

  protected LoadedLayer layerLandUseArea;
  public static String LAYER_LAND_USE_AREA = "layerLandUseArea";

  protected LoadedLayer layerAdminUnit;
  public static String LAYER_ADMIN_UNIT = "layerAdminUnit";
  protected LoadedLayer layerAdminLimit;
  public static String LAYER_ADMIN_LIMIT = "layerAdminLimit";

  protected LoadedLayer layerMask;
  public static String LAYER_MASK = "layerMask";

  protected LoadedLayer layerNetworkFace;
  public static String LAYER_NETWORK_FACE = "layerNetworkFace";

  private LoadedLayer layerSpecialPoint;
  public static String LAYER_SPECIALPOINT = "layerSpecialPoint";

  protected LoadedLayer layerAirports;
  public static String LAYER_AIRPORT = "layerAirports";
  protected LoadedLayer layerRunways;
  public static String LAYER_RUNWAY = "layerRunways";

  protected LoadedLayer layerPOI;
  public static String LAYER_POI = "layerPOI";

  // add a special points to correspondant layer

  /**
   * Getter for the interface layers
   */

  @Override
  public LoadedLayer getLayer(String layer) {
    if (layer.equals(LayerGroup.LAYER_BUILDING)) {
      return this.layerBuilding;
    } else if (layer.equals(LayerGroup.LAYER_TOWN)) {
      return this.layerTown;
    } else if (layer.equals(LayerGroup.LAYER_BLOCK)) {
      return this.layerBlock;
    } else if (layer.equals(LayerGroup.LAYER_URBAN_ALIGNMENT)) {
      return this.layerUrbanAlignment;
    } else if (layer.equals(LayerGroup.LAYER_ROAD_LINE)) {
      return this.layerRoadLine;
    } else if (layer.equals(LayerGroup.LAYER_ROAD_NODE)) {
      return this.layerRoadNode;
    } else if (layer.equals(LayerGroup.LAYER_PATH)) {
      return this.layerPath;
    } else if (layer.equals(LayerGroup.LAYER_WATER_LINE)) {
      return this.layerWaterLine;
    } else if (layer.equals(LayerGroup.LAYER_WATER_AREA)) {
      return this.layerWaterArea;
    } else if (layer.equals(LayerGroup.LAYER_ISLANDS)) {
      return this.layerIslands;
    } else if (layer.equals(LayerGroup.LAYER_RAILWAY_LINE)) {
      return this.layerRailwayLine;
    } else if (layer.equals(LayerGroup.LAYER_ELECRICITY_LINE)) {
      return this.layerElectricityLine;
    } else if (layer.equals(LayerGroup.LAYER_CONTOUR_LINE)) {
      return this.layerContourLine;
    } else if (layer.equals(LayerGroup.LAYER_SPOT_HEIGHT)) {
      return this.layerSpotHeight;
    } else if (layer.equals(LayerGroup.LAYER_DEM_PIXEL)) {
      return this.layerDEMPixel;
    } else if (layer.equals(LayerGroup.LAYER_RELIEF_TRIANGLE)) {
      return this.layerReliefTriangle;
    } else if (layer.equals(LayerGroup.LAYER_RELIEF_ELEM_LINE)) {
      return this.layerReliefElemLine;
    } else if (layer.equals(LayerGroup.LAYER_LAND_USE_AREA)) {
      return this.layerLandUseArea;
    } else if (layer.equals(LayerGroup.LAYER_ADMIN_UNIT)) {
      return this.layerAdminUnit;
    } else if (layer.equals(LayerGroup.LAYER_ADMIN_LIMIT)) {
      return this.layerAdminLimit;
    } else if (layer.equals(LayerGroup.LAYER_MASK)) {
      return this.layerMask;
    } else if (layer.equals(LayerGroup.LAYER_NETWORK_FACE)) {
      return this.layerNetworkFace;
    } else if (layer.equals(LayerGroup.LAYER_SPECIALPOINT)) {
      return this.layerSpecialPoint;
    } else if (layer.equals(LayerGroup.LAYER_AIRPORT)) {
      return this.layerAirports;
    } else if (layer.equals(LayerGroup.LAYER_RUNWAY)) {
      return this.layerRunways;
    } else if (layer.equals(LayerGroup.LAYER_POI)) {
      return this.layerPOI;
    } else if (layer.equals(LayerGroup.LAYER_BRIDGE_PT)) {
      return this.layerBridgePoint;
    } else {
      return null;
    }
  }

  /**
   * 
   * fill the LayerGroupe from the dataSet
   */
  @Override
  public void loadLayers(CartAGenDataSet dataSet, boolean areLayersSymbolised) {

    this.symbolisationDisplay = areLayersSymbolised;

    this.layerBuilding = new LoadedLayer(dataSet.getBuildings());
    this.layerTown = new LoadedLayer(dataSet.getTowns());
    this.layerBlock = new LoadedLayer(dataSet.getBlocks());
    this.layerUrbanAlignment = new LoadedLayer(dataSet.getUrbanAlignments());

    this.layerRoadLine = new LoadedLayer(dataSet.getRoads());
    this.layerRoadNode = new LoadedLayer(dataSet.getRoadNodes());
    this.layerBridgePoint = new LoadedLayer(dataSet.getBridgePoints());
    this.layerPath = new LoadedLayer(dataSet.getPaths());

    this.layerWaterLine = new LoadedLayer(dataSet.getWaterLines());
    this.layerWaterArea = new LoadedLayer(dataSet.getWaterAreas());
    this.layerIslands = new LoadedLayer(dataSet.getIslands());

    this.layerRailwayLine = new LoadedLayer(dataSet.getRailwayLines());

    this.layerElectricityLine = new LoadedLayer(dataSet.getElectricityLines());

    this.layerContourLine = new LoadedLayer(dataSet.getContourLines());
    this.layerSpotHeight = new LoadedLayer(dataSet.getSpotHeights());
    this.layerDEMPixel = new LoadedLayer(dataSet.getDEMPixels());
    this.layerReliefTriangle = new LoadedLayer(dataSet.getReliefField()
        .getTriangles());
    this.layerReliefElemLine = new LoadedLayer(dataSet.getReliefLines());

    this.layerLandUseArea = new LoadedLayer(dataSet.getLandUseAreas());

    this.layerAdminUnit = new LoadedLayer(dataSet.getAdminUnits());
    this.layerAdminLimit = new LoadedLayer(dataSet.getAdminLimits());

    this.layerMask = new LoadedLayer(dataSet.getMasks());

    this.layerNetworkFace = new LoadedLayer(dataSet.getFacesReseau());
    this.layerSpecialPoint = new LoadedLayer(dataSet.getSpecialPoints());

    this.layerAirports = new LoadedLayer(dataSet.getAirports());
    this.layerRunways = new LoadedLayer(dataSet.getRunways());
    this.layerPOI = new LoadedLayer(dataSet.getPOIs());

  }

  /**
   * load one layer
   * @param pop
   */
  @Override
  public LoadedLayer replaceOneLayer(LayerManager layerManager,
      CartAGenDataSet dataSet, String layerName) {
    if (layerName.equals(LayerGroup.LAYER_SPECIALPOINT)) {
      layerManager.removeLayer(this.layerSpecialPoint);
      this.layerSpecialPoint = new LoadedLayer(dataSet.getSpecialPoints());
      return this.layerSpecialPoint;
    }
    return null;
  }

  /**
   * add a layer into the interface
   */
  @Override
  public void loadInterfaceWithOneLayer(LayerManager layerManager,
      String layerName, AbstractButton b) {

    if (layerName.equals(LayerGroup.LAYER_SPECIALPOINT)) {
      this.layerSpecialPoint.emptyDisplayCache();
      layerManager.addLayer(this.layerSpecialPoint);
      AbstractButton accidentCheckbox = new JCheckBox();
      accidentCheckbox.setSelected(true);

      layerManager.addSymbolisedLayer(this.layerSpecialPoint,
          Symbolisation.specialPoint(this.symbolisationDisplay), b);

    }

  }

  /**
   * add a layer into the interface
   */
  public void loadInterfaceWithOneLayer(LayerManager layerManager,
      String layerName, AbstractButton b, LoadedLayer loadedLayer) {

    if (layerName.equals(LayerGroup.LAYER_SPECIALPOINT)) {
      loadedLayer.emptyDisplayCache();

      layerManager.addLayer(this.layerSpecialPoint);
      AbstractButton accidentCheckbox = new JCheckBox();
      accidentCheckbox.setSelected(true);

      layerManager.addSymbolisedLayer(this.layerSpecialPoint,
          Symbolisation.specialPoint(this.symbolisationDisplay), b);

    }

  }

  /**
   * add the layers and symbolised layers into the interface
   */
  @Override
  public void loadInterfaceWithLayers(LayerManager layerManager) {
    SymbolGroup symbGroup = SymbolsUtil.getSymbolGroup(
        SourceDLM.SPECIAL_CARTAGEN, Legend.getSYMBOLISATI0N_SCALE());

    this.loadInterfaceWithLayers(layerManager,
        SymbolList.getSymbolList(symbGroup));
  }

  /**
   * add the layers and symbolised layers into the interface
   */
  @Override
  public void loadInterfaceWithLayers(LayerManager layerManager,
      SymbolList symbols) {

    // LES COUCHES DE BASE

    layerManager.addLayer(this.layerBuilding);
    layerManager.addLayer(this.layerTown);
    layerManager.addLayer(this.layerBlock);
    layerManager.addLayer(this.layerUrbanAlignment);
    layerManager.addLayer(this.layerRoadLine);
    layerManager.addLayer(this.layerRoadNode);
    layerManager.addLayer(this.layerBridgePoint);
    layerManager.addLayer(this.layerPath);
    layerManager.addLayer(this.layerWaterLine);
    layerManager.addLayer(this.layerWaterArea);
    layerManager.addLayer(this.layerIslands);
    layerManager.addLayer(this.layerRailwayLine);
    layerManager.addLayer(this.layerElectricityLine);
    layerManager.addLayer(this.layerContourLine);
    layerManager.addLayer(this.layerSpotHeight);
    layerManager.addLayer(this.layerDEMPixel);
    layerManager.addLayer(this.layerReliefTriangle);
    layerManager.addLayer(this.layerReliefElemLine);
    layerManager.addLayer(this.layerLandUseArea);
    layerManager.addLayer(this.layerAdminUnit);
    layerManager.addLayer(this.layerAdminLimit);
    layerManager.addLayer(this.layerMask);
    layerManager.addLayer(this.layerNetworkFace);
    layerManager.addLayer(this.layerSpecialPoint);
    layerManager.addLayer(this.layerAirports);
    layerManager.addLayer(this.layerRunways);

    // LES COUCHES SYMBOLISEES

    // masques
    layerManager.addSymbolisedLayer(this.layerMask, Symbolisation.ligne(
        GeneralisationLegend.MASQUE_COULEUR,
        GeneralisationLegend.MASQUE_LARGEUR, this), this.cVoirMasque);

    // triangles du relief
    layerManager
        .addSymbolisedLayer(
            this.layerReliefTriangle,
            GeneralisationSymbolisation.triangle(Color.LIGHT_GRAY),
            DataThemesGUIComponent.getInstance().getReliefMenu().mReliefTrianglesVoir);

    // ombrage opaque
    layerManager.addSymbolisedLayer(this.layerReliefTriangle,
        GeneralisationSymbolisation.triangleOmbrageOpaque(Color.CYAN, 1, -1.0,
            -1.0, -1.0, -1.0, -1.0), this.cVoirOmbrageOpaque);

    // MNT
    layerManager.addSymbolisedLayer(this.layerDEMPixel,
        GeneralisationSymbolisation.pixelsDegade(), this.cVoirMNTDegrade);

    // hypsometrie
    layerManager.addSymbolisedLayer(this.layerReliefTriangle,
        GeneralisationSymbolisation.triangleHypsometrie(),
        this.cVoirHypsometrie);

    // dessin de l'occupation du sol
    layerManager
        .addSymbolisedLayer(this.layerLandUseArea,
            GeneralisationSymbolisation.zonesArboreesBDTopo(this),
            this.cVoirOccSol);

    // ville
    layerManager.addSymbolisedLayer(this.layerTown, Symbolisation.surface(
        GeneralisationLegend.VILLE_SURFACE_COULEUR,
        GeneralisationLegend.VILLE_CONTOUR_COULEUR,
        GeneralisationLegend.VILLE_CONTOUR_LARGEUR, this), this.cVoirVille);

    // ilots
    layerManager.addSymbolisedLayer(this.layerBlock, Symbolisation.surface(
        GeneralisationLegend.ILOT_SURFACE_COULEUR,
        GeneralisationLegend.ILOT_CONTOUR_COULEUR,
        GeneralisationLegend.ILOT_CONTOUR_LARGEUR, this), this.cVoirIlot);

    // ilots grises
    layerManager.addSymbolisedLayer(this.layerBlock,
        GeneralisationSymbolisation.ilotColore(), this.cVoirIlot);

    // Airports
    layerManager.addSymbolisedLayer(this.layerAirports, Symbolisation.surface(
        GeneralisationLegend.AIRPORT_SURFACE_COULEUR,
        GeneralisationLegend.AIRPORT_CONTOUR_COULEUR,
        GeneralisationLegend.AIRPORT_CONTOUR_LARGEUR, this), this.cVoirAirport);
    layerManager.addSymbolisedLayer(this.layerRunways, Symbolisation.surface(
        GeneralisationLegend.RUNWAY_SURFACE_COULEUR,
        GeneralisationLegend.RUNWAY_CONTOUR_COULEUR,
        GeneralisationLegend.RUNWAY_CONTOUR_LARGEUR, this), this.cVoirAirport);

    // ombrage transparent
    layerManager.addSymbolisedLayer(this.layerReliefTriangle,
        GeneralisationSymbolisation.triangleOmbrageTransparent(Color.CYAN, 120,
            1, -1.0, -1.0, -1.0, -1.0, -1.0), this.cVoirOmbrageTransparent);

    // triangles analyse
    layerManager
        .addSymbolisedLayer(
            this.layerReliefTriangle,
            GeneralisationSymbolisation.triangleContientBatiments(),
            DataThemesGUIComponent.getInstance().getReliefMenu().mReliefTrianglesVoirContientBatiments);

    // courbes de niveau
    layerManager.addSymbolisedLayer(this.layerContourLine,
        GeneralisationSymbolisation.courbeDeNiveau(this), this.cVoirCN);

    // points cotes
    layerManager.addSymbolisedLayer(this.layerSpotHeight, Symbolisation
        .pointRond(GeneralisationLegend.PTS_COTES_COULEUR,
            GeneralisationLegend.PTS_COTES_LARGEUR, this), this.cVoirPointCote);

    // relief elements
    layerManager.addSymbolisedLayer(this.layerReliefElemLine, Symbolisation
        .ligne(GeneralisationLegend.RELIEF_ELEM_LINE_COULEUR,
            GeneralisationLegend.RELIEF_ELEM_LINE_LARGEUR, this),
        this.cVoirReliefElem);

    // ADMIN
    layerManager.addSymbolisedLayer(this.layerIslands, Symbolisation.surface(
        null, GeneralisationLegend.ISLAND_OUTLINE_COLOR,
        GeneralisationLegend.ISLAND_OUTLINE_WIDTH), this.cVoirRH);
    layerManager.addSymbolisedLayer(this.layerAdminUnit, Symbolisation
        .dottedArea(GeneralisationLegend.ADMIN_COULEUR,
            GeneralisationLegend.ADMIN_LARGEUR,
            GeneralisationLegend.ADMIN_POINTILLES), this.cVoirAdmin);
    layerManager.addSymbolisedLayer(this.layerAdminLimit, Symbolisation
        .dottedLine(GeneralisationLegend.ADMIN_COULEUR,
            GeneralisationLegend.ADMIN_LARGEUR,
            GeneralisationLegend.ADMIN_POINTILLES), this.cVoirAdmin);

    // batiments
    layerManager.addSymbolisedLayer(this.layerBuilding, Symbolisation.surface(
        GeneralisationLegend.COULEUR_INTERIEUR_BATIMENTS,
        GeneralisationLegend.COULEUR_BORD_BATIMENTS,
        GeneralisationLegend.LARGEUR_BORD_BATIMENTS_MM, BasicStroke.CAP_SQUARE,
        BasicStroke.JOIN_MITER, this), this.cVoirBati);

    // reseau hydrographique
    // trace les troncons
    layerManager.addSymbolisedLayer(this.layerWaterLine, Symbolisation.ligne(
        GeneralisationLegend.RES_EAU_COULEUR,
        GeneralisationLegend.RES_EAU_LARGEUR, this), this.cVoirRH);
    // trace les surfaces
    layerManager.addSymbolisedLayer(this.layerWaterArea, Symbolisation.surface(
        GeneralisationLegend.SURFACE_EAU_COULEUR,
        GeneralisationLegend.SURFACE_EAU_COULEUR_CONTOUR,
        GeneralisationLegend.SURFACE_EAU_LARGEUR_CONTOUR, this), this.cVoirRH);

    // reseau routier

    // to know if we are using the new loading function of Kusay or the old one
    // we examine symbols list, if its not empty => we are using the new one
    if (!(symbols.getGroup() == SymbolGroup.SPECIAL_Cartagen)) {

      layerManager.addSymbolisedLayer(this.layerRoadLine,
          GeneralisationSymbolisation.symboliseSeparator(this, symbols),
          this.cVoirRR);

      for (int i = 0; i < 10; i++) {

        layerManager.addSymbolisedLayer(this.layerRoadLine,
            GeneralisationSymbolisation.symboliseDessous(i, this, symbols),
            this.cVoirRR);
        layerManager.addSymbolisedLayer(this.layerRoadNode,
            GeneralisationSymbolisation.noeudRouteDessousNew(i, this),
            this.cVoirRR);

        layerManager.addSymbolisedLayer(this.layerRoadLine,
            GeneralisationSymbolisation.symboliseDessus(i, this, symbols),
            this.cVoirRR);
        layerManager.addSymbolisedLayer(this.layerRoadNode,
            GeneralisationSymbolisation.noeudRouteDessusNew(i, this),
            this.cVoirRR);
      }

    } else { // "symbols" list is empty => we are using the old loading function

      // dessin des dessous de routes
      layerManager
          .addSymbolisedLayer(this.layerRoadLine, GeneralisationSymbolisation
              .troncon(GeneralisationLegend.ROUTIER_COULEUR_DESSOUS, this),
              this.cVoirRR);
      // dessin des dessous de noeuds
      layerManager
          .addSymbolisedLayer(this.layerRoadNode, GeneralisationSymbolisation
              .noeud(GeneralisationLegend.ROUTIER_COULEUR_DESSOUS, this),
              this.cVoirRR);
      // draw the paths' back
      layerManager.addSymbolisedLayer(this.layerPath, Symbolisation.ligne(
          GeneralisationLegend.ROUTIER_COULEUR_DESSOUS,
          GeneralisationLegend.ROUTIER_LARGEUR_DESSOUS_0), this.cVoirRR);

      layerManager.addSymbolisedLayer(this.layerRoadLine,
          GeneralisationSymbolisation.tronconRouteDessus(0,
              GeneralisationLegend.ROUTIER_COULEUR_0,
              GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_0, this),
          this.cVoirRR);
      layerManager.addSymbolisedLayer(this.layerRoadNode,
          GeneralisationSymbolisation.noeudRouteDessus(0,
              GeneralisationLegend.ROUTIER_COULEUR_0,
              GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_0, this),
          this.cVoirRR);

      // dessus
      layerManager.addSymbolisedLayer(this.layerRoadLine,
          GeneralisationSymbolisation.tronconRouteDessus(0,
              GeneralisationLegend.ROUTIER_COULEUR_0,
              GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_0, this),
          this.cVoirRR);
      layerManager.addSymbolisedLayer(this.layerRoadNode,
          GeneralisationSymbolisation.noeudRouteDessus(0,
              GeneralisationLegend.ROUTIER_COULEUR_0,
              GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_0, this),
          this.cVoirRR);

      layerManager.addSymbolisedLayer(this.layerRoadLine,
          GeneralisationSymbolisation.tronconRouteDessus(1,
              GeneralisationLegend.ROUTIER_COULEUR_1,
              GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_1, this),
          this.cVoirRR);
      layerManager.addSymbolisedLayer(this.layerRoadNode,
          GeneralisationSymbolisation.noeudRouteDessus(1,
              GeneralisationLegend.ROUTIER_COULEUR_1,
              GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_1, this),
          this.cVoirRR);

      layerManager.addSymbolisedLayer(this.layerRoadLine,
          GeneralisationSymbolisation.tronconRouteDessus(2,
              GeneralisationLegend.ROUTIER_COULEUR_2,
              GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_2, this),
          this.cVoirRR);
      layerManager.addSymbolisedLayer(this.layerRoadNode,
          GeneralisationSymbolisation.noeudRouteDessus(2,
              GeneralisationLegend.ROUTIER_COULEUR_2,
              GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_2, this),
          this.cVoirRR);

      layerManager.addSymbolisedLayer(this.layerRoadLine,
          GeneralisationSymbolisation.tronconRouteDessus(3,
              GeneralisationLegend.ROUTIER_COULEUR_3,
              GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_3, this),
          this.cVoirRR);
      layerManager.addSymbolisedLayer(this.layerRoadNode,
          GeneralisationSymbolisation.noeudRouteDessus(3,
              GeneralisationLegend.ROUTIER_COULEUR_3,
              GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_3, this),
          this.cVoirRR);

      layerManager.addSymbolisedLayer(this.layerRoadLine,
          GeneralisationSymbolisation.tronconRouteDessus(4,
              GeneralisationLegend.ROUTIER_COULEUR_4,
              GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_4, this),
          this.cVoirRR);
      layerManager.addSymbolisedLayer(this.layerRoadNode,
          GeneralisationSymbolisation.noeudRouteDessus(4,
              GeneralisationLegend.ROUTIER_COULEUR_4,
              GeneralisationLegend.ROUTIER_LARGEUR_DESSUS_4, this),
          this.cVoirRR);

      // separateur
      layerManager.addSymbolisedLayer(this.layerRoadLine,
          GeneralisationSymbolisation.tronconRouteDessus(4,
              GeneralisationLegend.ROUTIER_COULEUR_SEPARATEUR_4,
              GeneralisationLegend.ROUTIER_LARGEUR_SEPARATEUR_4, this),
          this.cVoirRR);
      layerManager.addSymbolisedLayer(this.layerRoadNode,
          GeneralisationSymbolisation.noeudRouteDessus(4,
              GeneralisationLegend.ROUTIER_COULEUR_SEPARATEUR_4,
              GeneralisationLegend.ROUTIER_LARGEUR_SEPARATEUR_4, this),
          this.cVoirRR);

      layerManager.addSymbolisedLayer(this.layerRoadNode, Symbolisation
          .pointRond(Color.RED, 5), DataThemesGUIComponent.getInstance()
          .getRoadNetMenu().mNoeudsResRoutierVoir);

      layerManager
          .addSymbolisedLayer(
              this.layerRoadLine,
              GeneralisationSymbolisation.tronconRouteDecale(),
              DataThemesGUIComponent.getInstance().getRoadNetMenu().mRoutierVoirRouteDecalee);
    }
    // bridge points
    layerManager.addSymbolisedLayer(this.layerBridgePoint,
        GeneralisationSymbolisation.symboliseBridge(this, (float) 0.2),
        this.cVoirRR);

    // reseau ferroviaire
    layerManager.addSymbolisedLayer(this.layerRailwayLine, Symbolisation.ligne(
        GeneralisationLegend.RES_FER_COULEUR,
        GeneralisationLegend.RES_FER_LARGEUR, this), this.cVoirRF);
    // reseau electrique
    layerManager.addSymbolisedLayer(this.layerElectricityLine, Symbolisation
        .ligne(GeneralisationLegend.RES_ELEC_COULEUR,
            GeneralisationLegend.RES_ELEC_LARGEUR, this), this.cVoirRE);

    // alignements urbains
    layerManager.addSymbolisedLayer(this.layerUrbanAlignment, Symbolisation
        .surface(GeneralisationLegend.ALIGNEMENT_SURFACE_COULEUR,
            GeneralisationLegend.ALIGNEMENT_CONTOUR_COULEUR,
            GeneralisationLegend.ALIGNEMENT_CONTOUR_LARGEUR, this),
        this.cVoirAlign);

    // LES COUCHES D'OBJETS STRUCTURELS (POINTS, SEGMENTS)

    // vecteurs pentes MNT
    layerManager
        .addSymbolisedLayer(this.layerReliefTriangle,
            GeneralisationSymbolisation.triangleVecteurPente(Color.RED,
                Color.CYAN), DataThemesGUIComponent.getInstance()
                .getReliefMenu().cVoirVecteurPente);

    // LES COUCHES DE TEXTE

    // textes batiments
    // id
    layerManager.addSymbolisedLayer(this.layerBuilding, Symbolisation
        .texte("getId"),
        DataThemesGUIComponent.getInstance().getBuildingMenu().mIdBatiVoir);
    // taux superposition
    layerManager.addSymbolisedLayer(this.layerBuilding, Symbolisation
        .texte("getOverlappingRate"), DataThemesGUIComponent.getInstance()
        .getBuildingMenu().mVoirTauxSuperposition);
    // aire
    layerManager.addSymbolisedLayer(this.layerBuilding, Symbolisation
        .texte("getArea"), DataThemesGUIComponent.getInstance()
        .getBuildingMenu().mVoirAire);
    // aire but
    layerManager.addSymbolisedLayer(this.layerBuilding, Symbolisation
        .texte("getGoalArea"), DataThemesGUIComponent.getInstance()
        .getBuildingMenu().mVoirAireBut);

    // altitude
    layerManager.addSymbolisedLayer(this.layerBuilding, Symbolisation
        .texte("getElevation"), DataThemesGUIComponent.getInstance()
        .getBuildingMenu().mVoirAltitude);

    // orientation generale
    layerManager
        .addSymbolisedLayer(
            this.layerBuilding,
            Symbolisation.texte("getGeneralOrientationDegree"),
            DataThemesGUIComponent.getInstance().getBuildingMenu().mVoirOrientationGenerale);

    // rosace orientations murs
    layerManager.addSymbolisedLayer(this.layerBuilding,
        GeneralisationSymbolisation.rosaceOrientation(), DataThemesGUIComponent
            .getInstance().getBuildingMenu().mVoirRosaceOrientationMurs);

    // rosace encombrement
    layerManager
        .addSymbolisedLayer(
            this.layerBuilding,
            GeneralisationSymbolisation.batimentRosaceEncombrement(),
            DataThemesGUIComponent.getInstance().getBuildingMenu().mVoirRosaceEncombrement);

    // valeur orientation murs
    layerManager
        .addSymbolisedLayer(
            this.layerBuilding,
            Symbolisation.texte("getSidesOrientationDegree"),
            DataThemesGUIComponent.getInstance().getBuildingMenu().mVoirOrientationMurs);

    // elongation
    layerManager.addSymbolisedLayer(this.layerBuilding, Symbolisation
        .texte("getElongation"), DataThemesGUIComponent.getInstance()
        .getBuildingMenu().mVoirElongation);

    // convexite
    layerManager.addSymbolisedLayer(this.layerBuilding, Symbolisation
        .texte("getConvexite"), DataThemesGUIComponent.getInstance()
        .getBuildingMenu().mVoirConvexite);

    // lg plus petit cote
    layerManager.addSymbolisedLayer(this.layerBuilding, Symbolisation
        .texte("getLongueurPlusPetitCote"), DataThemesGUIComponent
        .getInstance().getBuildingMenu().mVoirLgPlusPetitCote);

    // textes ilots
    // id
    layerManager.addSymbolisedLayer(this.layerBlock, Symbolisation
        .texte("getId"),
        DataThemesGUIComponent.getInstance().getBlockMenu().mIdIlotVoir);

    // densite initiale
    layerManager.addSymbolisedLayer(this.layerBlock, Symbolisation
        .texte("getInitialDensity"), DataThemesGUIComponent.getInstance()
        .getBlockMenu().mVoirDensiteInitiale);

    // densite simulee
    layerManager.addSymbolisedLayer(this.layerBlock, Symbolisation
        .texte("getSimulatedDensity"), DataThemesGUIComponent.getInstance()
        .getBlockMenu().mVoirDensiteSimulee);

    // moyenne taux superposition batiments
    layerManager
        .addSymbolisedLayer(
            this.layerBlock,
            Symbolisation.texte("getBuidlingsOverlappingRateMean"),
            DataThemesGUIComponent.getInstance().getBlockMenu().mVoirTauxSuperpositionBatiments);

    // cout suppression batiments
    layerManager
        .addSymbolisedLayer(
            this.layerBlock,
            GeneralisationSymbolisation.ilotCoutsSuppressionBatiments(),
            DataThemesGUIComponent.getInstance().getBlockMenu().mVoirCoutSuppressionBatiments);

    // textes villes
    // id
    layerManager.addSymbolisedLayer(this.layerTown, Symbolisation
        .texte("getId"),
        DataThemesGUIComponent.getInstance().getTownMenu().mIdVilleVoir);
    // aire but
    layerManager.addSymbolisedLayer(this.layerTown, Symbolisation
        .texte("getArea"),
        DataThemesGUIComponent.getInstance().getTownMenu().mVoirAireVille);

    // textes reseau routier
    // degre noeuds

    layerManager.addSymbolisedLayer(this.layerRoadNode, Symbolisation
        .texte("getDegree"), DataThemesGUIComponent.getInstance()
        .getRoadNetMenu().mDegreNoeudsResRoutierVoir);

    // empatement troncons
    layerManager.addSymbolisedLayer(this.layerRoadLine, Symbolisation
        .texte("getEmpatement"), DataThemesGUIComponent.getInstance()
        .getRoadNetMenu().mRoutierVoirEmpatementTroncons);

    // sinuosite troncons
    layerManager.addSymbolisedLayer(this.layerRoadLine, Symbolisation
        .texte("getSinuosity"), DataThemesGUIComponent.getInstance()
        .getRoadNetMenu().mRoutierVoirSinuositeTroncons);

    // textes reseau hydrographique
    layerManager.addSymbolisedLayer(this.layerWaterLine, Symbolisation
        .texte("getId"),
        DataThemesGUIComponent.getInstance().getHydroNetMenu().mIdHydroVoir);

    // taux superposition routier

    layerManager
        .addSymbolisedLayer(
            this.layerWaterLine,
            Symbolisation.texte("getTauxSuperpositionRoutier"),
            DataThemesGUIComponent.getInstance().getHydroNetMenu().mVoirTauxSuperpositionRoutier);

    // voir textes pente des triangles
    layerManager.addSymbolisedLayer(this.layerReliefTriangle, Symbolisation
        .texte("getAnglePente"), DataThemesGUIComponent.getInstance()
        .getReliefMenu().mReliefTrianglesVoirTexteAnglePente);

    layerManager
        .addSymbolisedLayer(
            this.layerReliefTriangle,
            Symbolisation.texte("getOrientationVecteurPente"),
            DataThemesGUIComponent.getInstance().getReliefMenu().mReliefTrianglesVoirTexteAngleOrientationPente);

  }

  /**
   * to remove one layer
   */
  @Override
  public void removeLayer(LayerManager layerManager, String lAYER_SPECIALPOINT) {
    LoadedLayer loadedLayer = this.getLayer(lAYER_SPECIALPOINT);
    layerManager.removeLayer(loadedLayer);

  }

  /**
   * to remove one layer
   */

  public void removeAllLayers() {
    this.layerBuilding.emptyDisplayCache();

    this.layerTown.emptyDisplayCache();

    this.layerBlock.emptyDisplayCache();

    this.layerUrbanAlignment.emptyDisplayCache();

    this.layerRoadLine.emptyDisplayCache();

    this.layerRoadNode.emptyDisplayCache();

    this.layerPath.emptyDisplayCache();

    this.layerWaterLine.emptyDisplayCache();

    this.layerWaterArea.emptyDisplayCache();

    this.layerIslands.emptyDisplayCache();

    this.layerRailwayLine.emptyDisplayCache();

    this.layerElectricityLine.emptyDisplayCache();

    this.layerContourLine.emptyDisplayCache();

    this.layerSpotHeight.emptyDisplayCache();

    this.layerDEMPixel.emptyDisplayCache();

    this.layerReliefTriangle.emptyDisplayCache();

    this.layerReliefElemLine.emptyDisplayCache();

    this.layerLandUseArea.emptyDisplayCache();

    this.layerAdminUnit.emptyDisplayCache();

    this.layerAdminLimit.emptyDisplayCache();

    this.layerMask.emptyDisplayCache();

    this.layerNetworkFace.emptyDisplayCache();

    this.layerSpecialPoint.emptyDisplayCache();

    this.layerAirports.emptyDisplayCache();

    this.layerRunways.emptyDisplayCache();

    this.layerPOI.emptyDisplayCache();

  }
}
