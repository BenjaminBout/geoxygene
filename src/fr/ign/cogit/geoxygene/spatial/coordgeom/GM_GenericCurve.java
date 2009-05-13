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

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import java.util.List;

/** NON UTILISE. Cette interface de la norme n'a plus de sens depuis qu'on a fait h�riter GM_CurveSegment de GM_Curve.
 *
 * <P> D�finition de la norme : les classes GM_Curve et GM_CurveSegment repr�sentent toutes deux des g�om�tries � une dimension, et partagent donc plusieurs signatures d'op�ration.
 * Celles-ci sont d�finies dans l'interface GM_GenericCurve.
 * La param�trisation employ�e dans les m�thodes se fait par la longueur de l'arc (absisse curviligne) ou par une autre param�trisation.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


interface GM_GenericCurve {

	/**
	 * Retourne le DirectPosition du premier point. Diff�rent de l'op�rateur "boundary" car renvoie la valeur du point et non pas l'objet g�om�trique repr�sentatif.
	 */
	DirectPosition startPoint();


	/**
	 * Retourne le DirectPosition du dernier point. Diff�rent de l'op�rateur "boundary" car renvoie la valeur du point et non pas l'objet g�om�trique repr�sentatif.
	 */
	DirectPosition endPoint();


	/** NON IMPLEMENTE.
	 * Renvoie un point � l'abcsisse curviligne s.
	 */
	// NORME : le param�tre en entree est de type Distance.
	DirectPosition param(double s);


	/** NON IMPLEMENTE.
	 * Vecteur tangent a la courbe, � l'abscisse curviligne  pass�e en param�tre. Le vecteur r�sultat est norm�.
	 */
	// NORME : le param�tre en entree est de type Distance.
	//     Vecteur tangent(double s);


	/**
	 * Renvoie O pour une GM_Curve.
	 * Pour un GM_CurveSegment, �gal au endParam du pr�cedent segment dans la segmentation (0 pour le premier segment).
	 */
	// NORME : le r�sultat est de type Distance.
	double startParam();


	/**
	 * Longueur de la courbe pour une GM_Curve. Pour un GM_CurveSegment, �gale � startParam plus la longueur du segment.
	 */
	// NORME : le r�sultat est de type Distance.
	double endParam();


	/** NON IMPLEMENTE.
	 * Renvoie le param�tre au point P (le param�tre �tant a priori la distance).
	 * Si P n'est pas sur la courbe, on cherche alors pour le calcul le point le plus proche de P sur la courbe
	 * (qui est aussi renvoy� en r�sultat).
	 * On renvoie en g�n�ral une seule distance, sauf si la courbe n'est pas simple.
	 */
	// NORME : le r�sultat est de type Distance.
	List<?> paramForPoint(DirectPosition P);


	/** NON IMPLEMENTE.
	 * Repr�sentation alternative d'une courbe comme l'image continue d'un intervalle de r�els,
	 * sans imposer que cette param�trisation repr�sente la longueur de la courbe,
	 * et sans imposer de restrictions entre la courbe et ses segments.
	 * Utilit� : pour les courbes param�tr�es,  pour construire une surface param�tr�e.
	 */
	DirectPosition constrParam(double cp);


	/** NON IMPLEMENTE.
	 * Param�tre au startPoint pour une courbe param�tr�e, c'est-�-dire : constrParam(startConstrParam())=startPoint().
	 */
	double startConstrParam();


	/** NON IMPLEMENTE.
	 * Param�tre au endPoint pour une courbe param�tr�e, c'est-�-dire : constrParam(endConstrParam())=endPoint().
	 */
	double endConstrParam();


	/** NON IMPLEMENTE.
	 * Longueur entre 2 points.
	 */
	// NORME : le r�sultat est de type Length.
	double length(GM_Position p1, GM_Position p2);


	/** NON IMPLEMENTE.
	 * Longueur d'une courbe param�tr�e "entre 2 r�els".
	 */
	// NORME : le r�sultat est de type Length.
	double length(double cparam1, double cparam2);


	/**
	 * Approximation lin�aire d'une courbe avec les points de contr�le.
	 * Le  param�tre spacing indique la distance maximum entre 2 points de contr�le;
	 * le param�tre  offset indique la distance maximum entre la polyligne g�n�r�e et la courbe originale.
	 * Si ces 2 param�tres sont � 0, alors aucune contrainte n'est impos�e.
	 * Le param�tre tol�rance permet d'�liminer les points cons�cutifs doublons qui peuvent appara�tre quand la courbe est compos�e de plusieurs segments.
	 */
	// NORME : spacing et offset sont de type Distance. tolerance n'est pas en param�tre.
	GM_LineString asLineString (double spacing, double offset, double tolerance) ;
}
