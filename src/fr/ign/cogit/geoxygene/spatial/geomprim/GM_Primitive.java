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

package fr.ign.cogit.geoxygene.spatial.geomprim;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_Complex;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Classe m�re abstraite pour les primitives g�om�triques (point, ligne, surface, solide).
 * Son but est d�finir l'op�ration de base "boundary()" qui lie les primitives de diff�rentes dimensions entre elles.
 * Cette op�ration est red�finie dans les sous-classes concr�tes pour assurer un bon typage.
 * Une primitive g�om�trique ne peut pas �tre d�compos�e en autres primitives,
 * m�me si elle d�coup�e en morceaux de courbes (curve segment) ou en morceaux de surface (surface patch) :
 * un curve segment et un surface patch ne peuvent pas exister en dehors du contexte d'une primitive.
 * GM_Complex et GM_Primitive partagent les m�mes propri�t�s, sauf qu'un complexe est ferm� par l'op�ration "boundary".
 * Par exemple pour une CompositeCurve, GM_Primitive::contains(endPoint) retourne FALSE,
 * alors que GM_Complex::contains(endPoint) retourne TRUE.
 * En tant que GM_Object ces 2 objets seront �gaux.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


abstract public class GM_Primitive extends GM_Object {


	// The "Interior to" association is not implemented

	// Le constructeur a partir d'une GM_Envelope est defini dans GM_Poygon


	/** Association avec les GM_Complex, instanci�e du cot� du complexe. */
	public Set<GM_Complex> complex = new HashSet<GM_Complex>();

	/** Renvoie le set des complexes auxquels appartient this */
	public Set<GM_Complex> getComplex() {return complex;}

	/** Nombre de complexes auxquels appartient this  */
	public int sizeComplex () {return this.complex.size();}

}
