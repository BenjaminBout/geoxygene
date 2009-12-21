/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
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

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.NoeudApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 *
 * méthodes d'import et export pour l'appariement sur des données Géographiques quelconques
 * (création des réseaux, lancement de l'appariement, export des résultats).
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class AppariementIO {
	private final static Logger logger=Logger.getLogger(AppariementIO.class.getName());

	/** Lancement de l'appariement de réseaux sur des objets Géographiques :
	 * 1- Transformation des données initales en deux graphes, en fonction des paramètres d'import.
	 * 2- Lancement du calcul d'appariement générique sur les deux réseaux.
	 * 3- Analyse et export des résultats éventuellement
	 * 
	 * @return  L'ensemble des liens en sortie (de la classe EnsembleDeLiens).
	 * 
	 * @param paramApp Les paramètres de l'appariement (seuls de distance, préparation topologique des données...)
	 * 
	 * @param cartesTopo Liste en entrée/sortie qui permet de Récupèrer en sortie les graphes intermédiaires créés pendant le calcul (de type Reseau_App, spécialisation de CarteTopo).
	 * - Si on veut Récupèrer les graphes : passer une liste vide - new ArrayList() - mais non nulle.
	 *    Elle contient alors en sortie 2 éléments : dans l'ordre les cartes topo de référence et comparaison.
	 *    Elle peut contenir un 3eme élément: le graphe ref recalé sur comp si cela est demandé dans les paramètres.
	 * - Si on ne veut rien Récupèrer : passer Null
	 */
	public static EnsembleDeLiens AppariementDeJeuxGeo(ParametresApp paramApp, List<ReseauApp> cartesTopo) {

		switch(paramApp.debugAffichageCommentaires) {
			case 0 : logger.setLevel(Level.ERROR); break;
			case 1 : logger.setLevel(Level.INFO); break;
			default : logger.setLevel(Level.DEBUG); break;
		}
		ReseauApp reseauRef, reseauComp;
		EnsembleDeLiens liens, liensGeneriques;

		if (logger.isInfoEnabled()) {
			logger.info("");
			logger.info("######## DEBUT DE L'APPARIEMENT DE RESEAUX #########");
			logger.info("  (1 = les données les moins détaillées ; 2 = les données les plus détaillées)");
			logger.info("");
		}
		////////////////////////////////////////////////
		// STRUCTURATION
		if (logger.isInfoEnabled()) {
			logger.info("STRUCTURATION DES DONNEES");
			logger.info("  Organisation des données en réseau et prétraitements topologiques");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("  DEBUT DE LA PHASE DE STRUCTURATION "+(new Time(System.currentTimeMillis())).toString());
			logger.debug("  création du réseau correspondant au jeu 1   "+(new Time(System.currentTimeMillis())).toString());
		}
		reseauRef = Import(paramApp, true);
		if ( cartesTopo != null ) cartesTopo.add(reseauRef);
		if (logger.isDebugEnabled()) logger.debug("  création du réseau correspondant au jeu 2   "+(new Time(System.currentTimeMillis())).toString());
		reseauComp = Import(paramApp, false);
		if ( cartesTopo != null ) cartesTopo.add(reseauComp);

		// NB: l'ordre dans lequel les projections sont faites n'est pas neutre
		if ( paramApp.projeteNoeud2surReseau1 ) {
			if (logger.isDebugEnabled()) logger.debug("  Projection du réseau 2 sur le réseau 1 "+(new Time(System.currentTimeMillis())).toString());
			reseauRef.projete(reseauComp, paramApp.projeteNoeud2surReseau1_DistanceNoeudArc, paramApp.projeteNoeud2surReseau1_DistanceProjectionNoeud, paramApp.projeteNoeud2surReseau1_ImpassesSeulement);
		}
		if ( paramApp.projeteNoeuds1SurReseau2 ) {
			if (logger.isDebugEnabled()) logger.debug("  Projection du réseau 1 sur le réseau 2 "+(new Time(System.currentTimeMillis())).toString());
			reseauComp.projete(reseauRef, paramApp.projeteNoeuds1SurReseau2_DistanceNoeudArc, paramApp.projeteNoeuds1SurReseau2_DistanceProjectionNoeud, paramApp.projeteNoeuds1SurReseau2_ImpassesSeulement);
		}

		if (logger.isDebugEnabled()) logger.debug("  Remplissage des attributs des arcs et noeuds des réseaux "+(new Time(System.currentTimeMillis())).toString());
		reseauRef.instancieAttributsNuls(paramApp);
		reseauComp.initialisePoids();

		if (logger.isInfoEnabled()) {
			logger.info("  Structuration initiale des données terminée : ");
			logger.info("     réseau 1 : "+reseauRef.getPopArcs().size()+" arcs et "+reseauRef.getPopNoeuds().size()+" noeuds.");
			logger.info("     réseau 2 : "+reseauComp.getPopArcs().size()+" arcs et "+reseauComp.getPopNoeuds().size()+" noeuds.");
		}
		if (logger.isDebugEnabled()) logger.debug("  FIN DE LA PHASE DE STRUCTURATION "+(new Time(System.currentTimeMillis())).toString());


		////////////////////////////////////////////////
		// APPARIEMENT
		if (logger.isInfoEnabled()) {
			logger.info("");
			logger.info("APPARIEMENT DES RESEAUX");
		}
		if (logger.isDebugEnabled()) logger.debug("  DEBUT DE LA PHASE D'APPARIEMENT DES RESEAUX "+(new Time(System.currentTimeMillis())).toString());
		liens = Appariement.appariementReseaux(reseauRef, reseauComp, paramApp);
		if (logger.isInfoEnabled()) {
			logger.info("  Appariement des réseaux terminé ");
			logger.info("  "+liens.size()+" liens d'appariement ont été trouvés (dans la structure de travail)");
		}
		if (logger.isDebugEnabled()) logger.debug("  FIN DE LA PHASE D'APPARIEMENT DES RESEAUX "+(new Time(System.currentTimeMillis())).toString());


		////////////////////////////////////////////////
		// EXPORT
		if (logger.isInfoEnabled()) {
			logger.info("");
			logger.info("BILAN ET EXPORT DES RESULTATS  ");
		}
		if (logger.isDebugEnabled()) logger.debug("  DEBUT DE LA PHASE D'EXPORT "+(new Time(System.currentTimeMillis())).toString());
		if (paramApp.debugBilanSurObjetsGeo ) {
			if (logger.isDebugEnabled()) logger.debug("  Transformation des liens de réseaux en liens génériques "+(new Time(System.currentTimeMillis())).toString());
			liensGeneriques= LienReseaux.exportLiensAppariement(liens, reseauRef, paramApp);
			Appariement.nettoyageLiens(reseauRef, reseauComp);
			if (logger.isInfoEnabled()) logger.info("######## FIN DE L'APPARIEMENT DE RESEAUX #########");
			return liensGeneriques;
		}
		else {
			if (logger.isDebugEnabled()) logger.debug("  Affectation d'une géométrie aux liens "+(new Time(System.currentTimeMillis())).toString());
			LienReseaux.exportAppCarteTopo(liens, paramApp);
			if (logger.isInfoEnabled()) logger.info("######## FIN DE L'APPARIEMENT DE RESEAUX #########");
			return liens;
		}
	}


	//////////////////////////////////////////////////////////////////////
	// 				        METHODES D'IMPORT							//
	//////////////////////////////////////////////////////////////////////
	/** création d'une carte topo à partir des objets Géographiques initiaux.
	 * 	 *
	 * @return
	 * Le réseau créé.
	 * 
	 * @param paramApp
	 * Les paramètres de l'appariement (seuls de distance, préparation topologique des données...)
	 * 
	 * true = on traite le réseau de référence
	 * false = on traite le réseau de comparaison
	 */
	private static ReseauApp Import(ParametresApp paramApp, boolean ref) {

		switch(paramApp.debugAffichageCommentaires) {
			case 0 : logger.setLevel(Level.ERROR); break;
			case 1 : logger.setLevel(Level.INFO); break;
			default : logger.setLevel(Level.DEBUG); break;
		}

		ReseauApp reseau;
		Iterator<FT_FeatureCollection<Arc>> itPopArcs;
		Iterator<?> itPopNoeuds, itElements;
		FT_FeatureCollection<?> popGeo;
		Population<?> popArcApp, popNoeudApp;
		FT_Feature element;
		ArcApp arc;
		NoeudApp noeud;

		if (ref) reseau = new ReseauApp("réseau de référence");
		else reseau = new ReseauApp("réseau de comparaison");
		popArcApp = reseau.getPopArcs();
		popNoeudApp = reseau.getPopNoeuds();

		///////////////////////////
		// import des arcs
		if ( ref ) itPopArcs = paramApp.populationsArcs1.iterator();
		else  itPopArcs = paramApp.populationsArcs2.iterator();
		while ( itPopArcs.hasNext() ) {
			popGeo = itPopArcs.next();
			//import d'une population d'arcs
			itElements = popGeo.getElements().iterator();
			while (itElements.hasNext() ) {
				element = (FT_Feature)itElements.next();
				arc = (ArcApp)popArcApp.nouvelElement();
				GM_LineString ligne = new GM_LineString((DirectPositionList)element.getGeom().coord().clone());
				arc.setGeometrie(ligne);
				if (paramApp.populationsArcsAvecOrientationDouble) arc.setOrientation(2);
				else arc.setOrientation(1);
				arc.addCorrespondant(element);

				// Le code ci-dessous permet un import plus fin mais a été réalisé
				// pour des données spécifiques et n'est pas encore codé très générique.
				// Il est donc commenté dans cette version du code.
				//				element = (FT_Feature)itElements.next();
				//				if ( ref && paramApp.filtrageRef ) {
				//					if ( filtrageTroncon(element) ) continue;
				//				}
				//				if ( !ref && paramApp.filtrageComp ) {
				//					if ( filtrageTroncon(element) ) continue;
				//				}
				//				arc = (Arc_App)popArcApp.nouvelElement();
				//				GM_LineString ligne = new GM_LineString((DirectPositionList)element.getGeom().coord().clone());
				//				arc.setGeometrie(ligne);
				//				if ( paramApp.orientationConstante) {
				//					if (paramApp.orientationDouble) arc.setOrientation(2);
				//					else arc.setOrientation(1);
				//				}
				//				else arc.setOrientation(orientationTroncon(element));
				//				arc.addCorrespondant(element);
			}
		}

		///////////////////////////
		// import des noeuds
		if ( ref ) itPopNoeuds = paramApp.populationsNoeuds1.iterator();
		else  itPopNoeuds = paramApp.populationsNoeuds2.iterator();
		while ( itPopNoeuds.hasNext() ) {
			popGeo = (Population<?>)itPopNoeuds.next();
			//import d'une population de noeuds
			itElements = popGeo.getElements().iterator();
			while (itElements.hasNext() ) {
				element = (FT_Feature)itElements.next();
				noeud = (NoeudApp)popNoeudApp.nouvelElement();
				//noeud.setGeometrie((GM_Point)element.getGeom());
				noeud.setGeometrie(new GM_Point((DirectPosition)((GM_Point)element.getGeom()).getPosition().clone()));
				noeud.addCorrespondant(element);
				noeud.setTaille(paramApp.distanceNoeudsMax);
				// Le code ci-dessous permet un import plus fin mais a été réalisé
				// pour des données spécifiques et n'est pas encore codé très générique.
				// Il est donc commenté dans cette version du code.
				//				if ( paramApp.distanceNoeudsConstante ) noeud.setTaille(paramApp.distanceNoeuds);
				//				else noeud.setTaille(tailleNoeud(element, paramApp));
			}
		}

		/////////////////////////////
		// Indexation spatiale des arcs et noeuds
		// On crée un dallage régulier avec en moyenne 20 objets par case
		if (logger.isDebugEnabled()) logger.debug("    Indexation spatiale des arcs et des noeuds "+(new Time(System.currentTimeMillis())).toString());
		int nb = (int)Math.sqrt(reseau.getPopArcs().size()/20);
		if (nb == 0) nb=1;
		reseau.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
		reseau.getPopNoeuds().initSpatialIndex(reseau.getPopArcs().getSpatialIndex());

		/////////////////////////////
		// Instanciation de la topologie

		// 1- création de la topologie arcs-noeuds, rendu du graphe planaire
		if ((ref && paramApp.topologieGraphePlanaire1) || (!ref && paramApp.topologieGraphePlanaire2)) { // cas où on veut une topologie planaire
			if (logger.isDebugEnabled()) logger.debug("    Rendu du graphe planaire et instanciation de la topologie arcs-noeuds "+(new Time(System.currentTimeMillis())).toString());
			// Debut Ajout 
			reseau.creeTopologieArcsNoeuds(0);
			reseau.creeNoeudsManquants(0);
			reseau.filtreDoublons(0);
			reseau.filtreArcsDoublons();
			// Fin Ajout
			reseau.rendPlanaire(0);
			reseau.filtreDoublons(0);
		}
		else { // cas où on ne veut pas nécessairement rendre planaire la topologie
			if (logger.isDebugEnabled()) logger.debug("    Instanciation de la topologie "+(new Time(System.currentTimeMillis())).toString());
			reseau.creeNoeudsManquants(0);
			reseau.filtreDoublons(0);
			reseau.creeTopologieArcsNoeuds(0);
		}

		// 2- On fusionne les noeuds proches
		if (ref) {
			if ( paramApp.topologieSeuilFusionNoeuds1 >= 0 ) {
				if (logger.isDebugEnabled()) logger.debug("    Fusion des noeuds proches "+(new Time(System.currentTimeMillis())).toString());
				reseau.fusionNoeuds(paramApp.topologieSeuilFusionNoeuds1);
			}
			if ( paramApp.topologieSurfacesFusionNoeuds1 != null ) {
				if (logger.isDebugEnabled()) logger.debug("    Fusion des noeuds dans une même surface "+(new Time(System.currentTimeMillis())).toString());
				reseau.fusionNoeuds(paramApp.topologieSurfacesFusionNoeuds1);
			}
		}
		else {
			if ( paramApp.topologieSeuilFusionNoeuds2 >= 0 ) {
				if (logger.isDebugEnabled()) logger.debug("    Fusion des noeuds proches "+(new Time(System.currentTimeMillis())).toString());
				reseau.fusionNoeuds(paramApp.topologieSeuilFusionNoeuds2);
			}
			if ( paramApp.topologieSurfacesFusionNoeuds2 != null ) {
				if (logger.isDebugEnabled()) logger.debug("    Fusion des noeuds dans une même surface "+(new Time(System.currentTimeMillis())).toString());
				reseau.fusionNoeuds(paramApp.topologieSurfacesFusionNoeuds2);
			}
		}

		// 3- On enlève les noeuds isolés
		if (logger.isDebugEnabled()) logger.debug("    Filtrage des noeuds isolés "+(new Time(System.currentTimeMillis())).toString());
		reseau.filtreNoeudsIsoles();

		// 4- On filtre les noeuds simples (avec 2 arcs incidents)
		if (( ref && paramApp.topologieElimineNoeudsAvecDeuxArcs1) ||
				( !ref && paramApp.topologieElimineNoeudsAvecDeuxArcs2)) {
			if (logger.isDebugEnabled()) logger.debug("    Filtrage des noeuds avec seulement 2 arcs incidents "+(new Time(System.currentTimeMillis())).toString());
			reseau.filtreNoeudsSimples();
		}

		// 5- On fusionne des arcs en double
		if (ref && paramApp.topologieFusionArcsDoubles1) {
			if (logger.isDebugEnabled()) logger.debug("    Filtrage des arcs en double "+(new Time(System.currentTimeMillis())).toString());
			reseau.filtreArcsDoublons();
		}
		if (!ref && paramApp.topologieFusionArcsDoubles2) {
			if (logger.isDebugEnabled()) logger.debug("    Filtrage des arcs en double "+(new Time(System.currentTimeMillis())).toString());
			reseau.filtreArcsDoublons();
		}

		// 6 - On crée la topologie de faces
		if ( !ref && paramApp.varianteChercheRondsPoints ) {
			if (logger.isDebugEnabled()) logger.debug("    création de la topologie de faces");
			reseau.creeTopologieFaces();
		}

		// 7 - On double la taille de recherche pour les impasses
		if ( paramApp.distanceNoeudsImpassesMax >=0 ) {
			if (ref) {
				if (logger.isDebugEnabled()) logger.debug("    Doublage du rayon de recherche des noeuds aux impasses");
				Iterator<?> itNoeuds = reseau.getPopNoeuds().getElements().iterator();
				while (itNoeuds.hasNext()) {
					NoeudApp noeud2 = (NoeudApp) itNoeuds.next();
					if ( noeud2.arcs().size() == 1 ) noeud2.setTaille(paramApp.distanceNoeudsImpassesMax);
				}
			}
		}

		return reseau;
	}


	//////////////////////////////////////////////////////////////////////
	// 						METHODES D'EXPORT							//
	//////////////////////////////////////////////////////////////////////

	/**  Methode utile principalement pour analyser les résultats d'un appariement,
	 * qui découpe un réseau en plusieurs réseaux selon les valeurs de l'attribut
	 * "Resultat_Appariement" des arcs et noeuds du réseau apparié.
	 */
	public static List<ReseauApp> scindeSelonValeursResultatsAppariement(ReseauApp reseauRef, List<String> valeursClassement) {
		List<ReseauApp> cartesTopoClassees = new ArrayList<ReseauApp>();
		Iterator<?> itArcs = reseauRef.getPopArcs().getElements().iterator();
		Iterator<?> itNoeuds = reseauRef.getPopNoeuds().getElements().iterator();
		ArcApp arc, arcClasse;
		NoeudApp noeud, noeudClasse;
		int i;

		for(i=0;i<valeursClassement.size();i++) {
			cartesTopoClassees.add(new ReseauApp("Eval "+valeursClassement.get(i)));
		}
		while (itArcs.hasNext()) {
			arc= (ArcApp) itArcs.next();
			for(i=0;i<valeursClassement.size();i++) {
				if ( arc.getResultatAppariement() == null ) continue;
				if (arc.getResultatAppariement().startsWith(valeursClassement.get(i))) {
					arcClasse = (ArcApp)cartesTopoClassees.get(i).getPopArcs().nouvelElement();
					arcClasse.setGeom(arc.getGeom());
					arcClasse.setResultatAppariement(arc.getResultatAppariement());
				}
			}
		}
		while (itNoeuds.hasNext()) {
			noeud= (NoeudApp) itNoeuds.next();
			for(i=0;i<valeursClassement.size();i++) {
				if ( noeud.getResultatAppariement() == null ) continue;
				if (noeud.getResultatAppariement().startsWith(valeursClassement.get(i))) {
					noeudClasse = (NoeudApp)cartesTopoClassees.get(i).getPopNoeuds().nouvelElement();
					noeudClasse.setGeom(noeud.getGeom());
					noeudClasse.setResultatAppariement(noeud.getResultatAppariement());
				}
			}
		}
		return cartesTopoClassees;
	}
}