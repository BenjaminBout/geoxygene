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

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.NoeudApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/** 
 * M�thodes pour le recalage d'un r�seau sur un autre.
 * NB : m�thodes r�alis�es pour un cas particulier et non retouch�es pour assurer une bonne g�n�ricit�.
 *  ////////////// A manier avec pr�caution  //////////////////.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0 
 * 
 */

public class Recalage {
	/** Recale la g�om�trie des arcs d'un graphe sur un autre graphe
	 *  une fois que ceux-ci ont �t� appari�s.
	 *  
	 *  Un lien (correspondant) est gard� entre les nouveaux arcs et leurs correspondants 
	 *  dans le r�seau de r�f�rence (accessible par arc.getCorrespondants()),
	 * 
	 *  IMPORTANT: ctARecaler doit �tre le r�seau 1 dans l'appariement,
	 *  et ctSurLaquelleRecaler le r�seau 2.
	 *  
	 *  NB: m�thode con�ue pour les cas relativement simples qui m�rite sans doute d'�tre affin�e.
	 * 
	 * @param ctARecaler
	 * Le r�seau � recaler
	 * 
	 * @param ctSurLaquelleRecaler
	 * Le r�seau sur lequel recaler
	 * 
	 * @param ctRecale
	 * Le r�seau recal� (en entr�e-sortie)
	 * 
	 * @param liens
	 * Des liens d'appariement entre les r�seaux "� recaler" et "sur lequel recaler" 
	 */
	public static CarteTopo recalage(ReseauApp ctARecaler, 
									 ReseauApp ctSurLaquelleRecaler,
									 EnsembleDeLiens liens) {
		CarteTopo ctRecale = new CarteTopo("reseau recal�");
		// On ajoute dans le r�seau recal� les arcs sur lesquels on recale qui sont appari�s. 
		Iterator itArcsSurLesquelsRecaler = ctSurLaquelleRecaler.getPopArcs().getElements().iterator();
		while (itArcsSurLesquelsRecaler.hasNext()) {
			ArcApp arc = (ArcApp) itArcsSurLesquelsRecaler.next();
			if (arc.getListeGroupes().size()==0) continue;
			if (!arc.aUnCorrespondantGeneralise(liens)) continue;
			Arc nouvelArc = (Arc)ctRecale.getPopArcs().nouvelElement();
			nouvelArc.setGeometrie(arc.getGeometrie());
			nouvelArc.setCorrespondants(arc.objetsGeoRefEnCorrespondance(liens));
		}
		
		// On ajoute dans le r�seau recal� les arcs � recaler qui ne sont pas appari�s, 
		// en modifiant la g�om�trie pour assurer un raccord amorti avec le reste.
		Iterator itArcsARecaler = ctARecaler.getPopArcs().getElements().iterator();
		while (itArcsARecaler.hasNext()) {
			ArcApp arc = (ArcApp) itArcsARecaler.next();
			if (arc.getLiens(liens.getElements()).size()!=0) continue;
			Arc nouvelArc = (Arc)ctRecale.getPopArcs().nouvelElement();
			nouvelArc.setGeometrie(new GM_LineString((DirectPositionList)arc.getGeom().coord().clone())); // vraie duplication de g�om�trie (un peu tordu, certes)
			geometrieRecalee(arc, nouvelArc, liens); 
		}
		return ctRecale;
	}
	
	/** Methode utilis�e par le recalage pour assurer le recalage.
	 *  Attention : cette m�thode n'est pas tr�s g�n�rique : elle suppose que l'on recale Ref sur Comp uniquement
	 *  et elle m�rite des affinements.
	 */
	private static void geometrieRecalee(ArcApp arcARecaler, Arc arcRecale, EnsembleDeLiens liens) {
		NoeudApp noeudARecalerIni = (NoeudApp)arcARecaler.getNoeudIni();
		NoeudApp noeudARecalerFin = (NoeudApp)arcARecaler.getNoeudFin();
		NoeudApp noeudRecaleIni, noeudRecaleFin; 
		List liensDuNoeudARecalerIni = noeudARecalerIni.getLiens(liens.getElements());
		List liensDuNoeudARecalerFin = noeudARecalerFin.getLiens(liens.getElements());
		GM_LineString nouvelleGeometrie  = new GM_LineString((DirectPositionList)arcARecaler.getGeom().coord().clone());
		GM_LineString geomTmp;
		double longueur, abscisse;  
		Vecteur decalage, decalageCourant;
		DirectPosition ptCourant;
		int i;
		
		if ( liensDuNoeudARecalerIni.size() == 1 ) {
			// si le noeud initial de l'arc � recal� est appari� avec le r�seau comp
			if ( ((LienReseaux)liensDuNoeudARecalerIni.get(0)).getNoeuds2().size() == 1 ) { 
				noeudRecaleIni = (NoeudApp)((LienReseaux)liensDuNoeudARecalerIni.get(0)).getNoeuds2().get(0);
				nouvelleGeometrie.setControlPoint(0, noeudRecaleIni.getGeometrie().getPosition());
				
				decalage = new Vecteur(noeudARecalerIni.getCoord(), noeudRecaleIni.getCoord() );
				geomTmp = new GM_LineString((DirectPositionList)nouvelleGeometrie.coord().clone());				
				longueur = nouvelleGeometrie.length();
				abscisse = 0;
				for(i=1;i<nouvelleGeometrie.coord().size()-1;i++) {
					ptCourant = geomTmp.coord().get(i);
					abscisse = abscisse+Distances.distance(geomTmp.getControlPoint(i), geomTmp.getControlPoint(i-1));  
					decalageCourant = decalage.multConstante(1-abscisse/longueur);
					nouvelleGeometrie.setControlPoint(i, decalageCourant.translate(ptCourant));
				}
			}
		} 

		// si le noeud final de l'arc � recal� est appari� avec le r�seau comp
		if ( liensDuNoeudARecalerFin.size() == 1 ) {
			if ( ((LienReseaux)liensDuNoeudARecalerFin.get(0)).getNoeuds2().size() == 1 ) {
				noeudRecaleFin = (NoeudApp)((LienReseaux)liensDuNoeudARecalerFin.get(0)).getNoeuds2().get(0);
				nouvelleGeometrie.setControlPoint(nouvelleGeometrie.coord().size()-1, noeudRecaleFin.getGeometrie().getPosition());
				
				decalage = new Vecteur(noeudARecalerFin.getCoord(), noeudRecaleFin.getCoord() );
				geomTmp = new GM_LineString((DirectPositionList)nouvelleGeometrie.coord().clone());				
				longueur = nouvelleGeometrie.length();
				abscisse = 0;
				for(i=nouvelleGeometrie.coord().size()-2;i>0;i--) {
					ptCourant = geomTmp.coord().get(i);
					abscisse = abscisse+Distances.distance(geomTmp.getControlPoint(i), geomTmp.getControlPoint(i+1));  
					decalageCourant = decalage.multConstante(1-abscisse/longueur);
					nouvelleGeometrie.setControlPoint(i, decalageCourant.translate(ptCourant));
				}
			}
		} 
		
		arcRecale.setGeom(nouvelleGeometrie);
	}

}