/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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
 
package fr.ign.cogit.geoxygene.util.index;

import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;


/**
 * Interface pour un index spatial.
 * Les selections se font au sens large : tout objet intersectant la zone d'extraction est renvoye. 
 * 
 * @author Thierry Badard, Arnaud Braun & S�bastien Musti�re
 * @version 1.0
 */

public interface SpatialIndex {
	
	/** Renvoie les param�tres de l'index.
	 * Ce que contient exactement cette liste peut �tre diff�rent pour chaque type d'index.
	 * 
	 * Pour un dallage: renvoie une ArrayList de 4 �l�ments
	 * - 1er  �l�ment : Class �gal � Dallage.class  
	 * - 2�me �l�ment : Boolean indiquant si l'index est en mode MAJ automatique ou non
	 * - 3�me �l�ment : GM_Envelope d�crivant les limites de la zone couverte
	 * - 4�me �l�ment : Integer exprimant le nombre de cases en X et Y.
	 *  
	 */
	public List getParametres();
		
	/** Indique si l'on a demande une mise a jour automatique. */
	public boolean hasAutomaticUpdate() ;
	
	/** Demande une mise a jour automatique.
	 * NB: Cette m�thode ne fait pas les �ventuelles MAJ qui
	 * auriant �te faites alors que le mode MAJ automatique n'�tait
	 * pas activ�.
	 */
	public void setAutomaticUpdate(boolean auto) ;
	
	/** Met a jour l'index avec le FT_Feature. 
	 * Si cas vaut +1 : on ajoute le feature.
	 * Si cas vaut -1 : on enleve le feature.
	 * Si cas vaut 0 : on modifie le feature.*/
	public void update (FT_Feature value, int cas) ;

	/** Selection dans le carre dont P est le centre, de cote D. 
	 * NB: D peut �tre nul. */
	public FT_FeatureCollection select (DirectPosition P, double D) ;
	
	/** Selection a l'aide d'un rectangle. */
	public FT_FeatureCollection select (GM_Envelope env) ;
    
	/** Selection des objets qui intersectent un objet geometrique quelconque. */
	public FT_FeatureCollection select (GM_Object geometry) ;

	/** Selection des objets qui croisent ou intersectent un objet geometrique quelconque.
	 * 
	 * @param strictlyCrosses
	 * Si c'est TRUE : ne retient que les  objets qui croisent (CROSS au sens JTS)
	 * Si c'est FALSE : ne retient que les  objets qui intersectent (INTERSECT au sens JTS)
	 * Exemple : si 1 ligne touche "geometry" juste sur une extr�mit�, 
	 * alors avec TRUE cela ne renvoie pas la ligne, avec FALSE cela la renvoie
	 */
	public FT_FeatureCollection select(GM_Object geometry, boolean strictlyCrosses) ;


	/** Selection a l'aide d'un objet geometrique quelconque et d'une distance. 
	 * NB: D peut �tre nul*/
	public FT_FeatureCollection select (GM_Object geometry, double distance) ;
	

}
