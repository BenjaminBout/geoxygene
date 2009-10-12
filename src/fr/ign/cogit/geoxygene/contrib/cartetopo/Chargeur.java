/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut G�ographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut G�ographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.contrib.cartetopo;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Primitive;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * Chargeur permettant de cr�er une carte topo � partir de classes de "FT_Feature"
 * @author S�bastien Musti�re
 * @author Olivier Bonin
 * @author Julien Perret
 */

public class Chargeur {
	static Logger logger=Logger.getLogger(Chargeur.class.getName());

	/** Charge en m�moire les �lements de la classe 'nomClasseGeo'
	 * et remplit la carte topo 'carte' avec des correspondants de ces �l�ments.
	 */
	public static void importClasseGeo(String nomClasseGeo, CarteTopo carte) {
		Class<?> clGeo;

		try {
			clGeo = Class.forName(nomClasseGeo);
		} catch (Exception e) {
			logger.warn("ATTENTION : La classe nomm�e "+nomClasseGeo+ " n'existe pas");
			logger.warn("            Impossible donc de l'importer");
			e.printStackTrace();
			return;
		}

		FT_FeatureCollection<?> listeFeatures = DataSet.db.loadAllFeatures(clGeo);
		importClasseGeo(listeFeatures,carte);
	}

	/** Remplit la carte topo 'carte' avec des correspondants des �l�ments de 'listeFeature'.
	 * @param listeFeatures �l�m�nts
	 * @param carte carte topo
	 */
	public static void importClasseGeo(FT_FeatureCollection<?> listeFeatures, CarteTopo carte) {
		importClasseGeo(listeFeatures, carte, false);
	}

	/** Remplit la carte topo 'carte' avec des correspondants des �l�ments de 'listeFeature'.
	 * @param listeFeatures �l�ments
	 * @param carte carte topo
	 * @param convert2d si vrai, alors convertir les g�om�tries en 2d
	 */
	public static void importClasseGeo(FT_FeatureCollection<?> listeFeatures, CarteTopo carte, boolean convert2d) {
		if(listeFeatures.isEmpty()) {logger.warn("Rien n'a �t� import� : la liste de features est vide");return;}
		if (listeFeatures.get(0).getGeom() instanceof GM_Point) {
			int nbElements = importClasseGeo(listeFeatures, carte.getPopNoeuds(), convert2d);
			if(logger.isDebugEnabled()) logger.debug("Nb de noeuds import�s : "+nbElements);
			return;
		}
		if ( (listeFeatures.get(0).getGeom() instanceof GM_LineString)  || (listeFeatures.get(0).getGeom() instanceof GM_MultiCurve<?>) ) {
			int nbElements = importClasseGeo(listeFeatures, carte.getPopArcs(), convert2d);
			if(logger.isDebugEnabled()) logger.debug("Nb d'arcs import�s    : "+nbElements);
			return;
		}
		if ( (listeFeatures.get(0).getGeom() instanceof GM_Polygon) || (listeFeatures.get(0).getGeom() instanceof GM_MultiSurface<?>) ) {
			int nbElements = importClasseGeo(listeFeatures, carte.getPopFaces(), convert2d);
			if(logger.isDebugEnabled()) logger.debug("Nb de faces import�es : "+nbElements);
			return;
		}
		logger.warn("Attention: rien n'a �t� import�. Features non g�r�s dont la g�om�trie est de type "+listeFeatures.get(0).getClass().getName());
	}

	/** Remplit la carte topo 'carte' avec des correspondants des �l�ments de 'listeFeature'.
	 * @param listeFeatures �l�ments
	 * @param convert2d si vrai, alors convertir les g�om�tries en 2d
	 * @param carte carte topo
	 */
	@SuppressWarnings("unchecked")
	private static int importClasseGeo(FT_FeatureCollection<?> listeFeatures, Population<?> population, boolean convert2d) {
		int nbElements=0;
		for(FT_Feature feature : listeFeatures) {
			if (feature.getGeom() instanceof GM_Primitive) {
				creeElement(feature, feature.getGeom(), population, convert2d);
				nbElements++;
			} else {
				for(GM_Object geom:((GM_Aggregate<GM_Object>)feature.getGeom())) {
					try {
						creeElement(feature, geom,  population, convert2d);
						nbElements++;
						} catch (Exception e) {e.printStackTrace();}
				}				
			}
		}
		return nbElements;
	}

	/**
	 * Cr�e un �l�ment de la carte topo comme correspondant de l'objet feature et la g�om�trie geom.
	 * @param geom g�om�trie du nouvel �l�ment
	 * @param population population � laquelle ajout le nouvel �l�ment
	 * @param convert2d si vrai alors la g�om�trie du nouvel �l�ment est convertie en 2d 
	 */
	private static void creeElement(FT_Feature feature, GM_Object geom, Population<?> population, boolean convert2d) {
		FT_Feature nouvelElement;
		try {
			nouvelElement = population.nouvelElement(convert2d ? AdapterFactory.to2DGM_Object(geom) : geom);
			nouvelElement.addCorrespondant(feature);
		} catch (Exception e) {e.printStackTrace();}
	}
}