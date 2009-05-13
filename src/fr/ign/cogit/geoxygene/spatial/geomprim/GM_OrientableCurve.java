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

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * Courbe orient�e. L'orientation traduit le sens de param�trisation.
 * Utilis�e comme fronti�re d'une surface, la surface dont la courbe est fronti�re est � gauche de la courbe.
 * Si l'orientation est +1, alors self est une GM_Curve, de primitive elle-m�me.
 * Si l'orientation est -1, alors self est une GM_OrientableCurve, de primitive une GM_Curve renvers�e par rapport � la courbe positive.
 *
 * <P> Utilisation : on ne construit pas une GM_OrientableCurve directement,
 * mais a partir d'une GM_Curve en utilisant getPositive() et getNegative().
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_OrientableCurve extends GM_OrientablePrimitive {


	/** Primitive */
	public GM_Curve primitive;

	/** Renvoie la primitive de self */
	public GM_Curve getPrimitive () {return this.primitive;}


	/** Attribut stockant les primitives orient�es de cette primitive.
	 * Proxy[0] est celle orient�e positivement.
	 * Proxy[1] est celle orient�e n�gativement.
	 * On acc�de aux primitives orient�es par getPositive() et getNegative().  */
	public GM_OrientableCurve[] proxy = new GM_OrientableCurve[2];


	/** Renvoie la primitive orient�e positivement correspondant � self.  */
	public GM_OrientableCurve getPositive () {
		return proxy[0];
	}


	/** Renvoie la primitive orient�e n�gativement correspondant � self.  */
	public GM_OrientableCurve getNegative() {
		GM_Curve proxy1prim = proxy[1].primitive;
		proxy1prim.getSegment().clear();
		GM_Curve proxy0 = (GM_Curve)proxy[1].proxy[0];
		int n = proxy0.sizeSegment();
		if (n>1)
			for (int i=0; i<n; i++) proxy1prim.addSegment(proxy0.getSegment(n-1-i).reverse());
		else if (n==1) // Braun - 14/06/02 : modif ajoutee suite a l'heritage de GM_CurveSegment sur GM_Curve
			proxy1prim.segment.add(proxy0.getSegment(0).reverse());
		return proxy[1];
	}


	/** Red�finition de l'op�rateur "boundary" sur GM_Object. Renvoie une GM_CurveBoundary, c'est-�-dire deux GM_Point.  */
	public GM_CurveBoundary boundary() {
		GM_Curve prim = this.getPrimitive();
		GM_CurveBoundary bdy = new GM_CurveBoundary(prim);
		return bdy;
	}


	/** Renvoie les coordonnees de la primitive.  */
	@Override
	public DirectPositionList coord() {
		return getPrimitive().coord();
	}

}
