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

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeCurve;

/**
 * Repr�sente un composant d'une GM_SurfaceBoundary.
 * Un GM_Ring est une GM_CompositeCurve ferm�e, c'est-�-dire des r�f�rences vers des GM_OrientableCurve connect�es en un cycle.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 *
 */

public class GM_Ring extends GM_CompositeCurve {

	/** Constructeur par d�faut */
	public GM_Ring () {
		super();
	}


	/** Constructeur � partir d'une et d'une seule GM_OrientableCurve.
	 * Ne v�rifie pas la fermeture. */
	public GM_Ring(GM_OrientableCurve oriCurve) {
		super(oriCurve);
	}


	/**
	 * Constructeur � partir d'une et d'une seule GM_OrientableCurve.
	 * V�rifie la fermeture, d'o� le param�tre tol�rance. Exception si ca ne ferme pas.
	 * TODO : un nouveau type d'exception
	 * @param oriCurve
	 * @param tolerance
	 * @throws Exception
	 */
	public GM_Ring(GM_OrientableCurve oriCurve, double tolerance) throws Exception {
		super(oriCurve);
		GM_Curve c = oriCurve.getPrimitive();
		DirectPosition pt1 = c.startPoint();
		DirectPosition pt2 = c.endPoint();
		if (!pt1.equals(pt2,tolerance))
			throw new Exception("tentative de cr�er un GM_Ring avec une courbe non ferm�e");
	}


	/** 
	 * Constructeur � partir d'une courbe compos�e (cast).
	 * Ne v�rifie ni la fermeture, ni le chainage.
	 * @param compCurve
	 */
	public GM_Ring(GM_CompositeCurve compCurve) {
		super();
		this.generator = compCurve.getGenerator();
		this.primitive = compCurve.getPrimitive();
		this.proxy[0] = compCurve.getPositive();
		this.proxy[1] = compCurve.getNegative();
	}


	/** Constructeur � partir d'une courbe compos�e (cast).
	 * V�rifie la fermeture et le chainage sinon exception. */
	public GM_Ring(GM_CompositeCurve compCurve, double tolerance) throws Exception {
		super();
		this.generator = compCurve.getGenerator();
		this.primitive = compCurve.getPrimitive();
		this.proxy[0] = compCurve.getPositive();
		this.proxy[1] = compCurve.getNegative();
		if (!super.validate(tolerance))
			throw new Exception("new GM_Ring(): La courbe compos�e pass�e en param�tre n'est pas cha�n�e");
		if (!this.validate(tolerance))
			throw new Exception("new GM_Ring(): La courbe compos�e pass�e en param�tre ne ferme pas.");
	}


	/** M�thode pour v�rifier qu'on a un chainage, et que le point initial est bien �gal au point final.
	 * Surcharge de la m�thode validate sur GM_CompositeCurve.
	 * Renvoie TRUE si c'est le cas, FALSE sinon.*/
	@Override
	public boolean validate(double tolerance) {
		if (!super.validate(tolerance)) return false;
		GM_CurveBoundary bdy = this.boundary();
		if (bdy.getStartPoint().getPosition().equals(bdy.getEndPoint().getPosition(),tolerance)) return true;
		return false;
	}

	@Override
	public Object clone() {
		return new GM_Ring( new GM_LineString((DirectPositionList) coord().clone()) );
	}

}
