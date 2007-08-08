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

package fr.ign.cogit.geoxygene.contrib.geometrie;

import java.util.Iterator;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * M�thodes statiques de calcul d'indices de forme (de lignes et de surfaces).
 * 
 * English : Measures of shapes
 * 
 * @author  Musti�re/Sheeren/Grosso
 */

public abstract class IndicesForme {

	//////////////////////////////////////////
	// INDICES SUR DES SURFACES
	//////////////////////////////////////////

	/** Indice de circularit� de Miller (pour des surfaces) 
	 * Valeur entre 0 et 1. 
	 * Vaut 1 si le polygone est un cercle, 0 si il est de surface nulle.
	 * D�finition = 4*pi*surface/perimetre^2
	 * Conseil : le seuil de 0.95 est adapt� pour des ronds points dans un r�seau routier.
	 */ 
	public static double indiceCompacite(GM_Polygon poly) {
		if (Operateurs.surface(poly) == 0) return 0;
		if (poly.coord().size() < 4) return 0;
		double perimetre = poly.perimeter();
		if ( perimetre == 0 ) return 0;
		double surface = poly.area();
		return 4*(Math.PI)*surface/Math.pow(perimetre,2);
	}
	
	/** Coefficient de compacit� de Gravelius (pour des surfaces) 
	 * Non born� : sup�rieur ou �gal � 1 (cercle) . 
	 * D�finition = perimetre/2*Racine(Pi*surface)
	 */ 
	public static double indiceCompaciteGravelius(GM_Polygon poly) {
		double perimetre = poly.length();
		double surface = poly.area();
		return perimetre/2*(Math.sqrt(Math.PI*surface));
	}

	/** Diam�tre d'une surface: plus grande distance entre 2 points de la
	 * fronti�re de la surface consid�r�e.
	 * 
	 * English: diameter of a surface
	 * 
	 * @param GM_Object A
	 * @return -1 si A n'est pas une surface, le diam�tre sinon
	 */
	public static double diametreSurface(GM_Object A) {
		if (A.area()==0) return -1;
		
		DirectPositionList pts = A.coord();
		double dist, diametre = 0; 
		
		Iterator itPts = pts.getList().iterator();
		while (itPts.hasNext()){
			DirectPosition dp = (DirectPosition)itPts.next(); 
			Iterator itPts2 = pts.getList().iterator();
			while (itPts2.hasNext()){
				DirectPosition dp2 = (DirectPosition)itPts2.next();
				dist = Distances.distance2D(dp,dp2);
				if ( dist > diametre ) diametre = dist;
			}
		}
		return diametre;
   }

	//////////////////////////////////////////
	// INDICES SUR DES LIGNES
	//////////////////////////////////////////
	/** M�thode qui d�termine si la liste de points pass�e en entr�e est rectiligne.
	 *  Une ligne est consid�r�e rectiligne si les angles entre les segments qui
	 *  la constitue ne sont pas trop forts (inf�rieur au seuil en param�tre en radians).
	 *  D�faut : d�pend de l'�chantillonage des courbes, des crit�res de courbure 
	 *  seraient plus stables.
	 *  
	 *  English: is the line straight?
	 */
	public static boolean rectiligne(GM_LineString ligne,double toleranceAngulaire){
		int i;
		Angle ecartTrigo;
		double angle;
		double angleMin = Math.PI - toleranceAngulaire;
		double angleMax = Math.PI + toleranceAngulaire;
		DirectPositionList listePts = ligne.getControlPoint();			
		for (i=0;i<listePts.size()-2;i++){
			ecartTrigo = Angle.angleTroisPoints(listePts.get(i),listePts.get(i+1),listePts.get(i+2));
			angle = ecartTrigo.getAngle();
			if ((angle>angleMax) || (angle<angleMin)) return false ;
		 }
		return true;
	}

}
