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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;

/**
 * NON UTILISE. Object g�om�trique de base en 3D.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_Solid extends GM_Primitive {
	static Logger logger = Logger.getLogger(GM_Solid.class.getName());

	/**
	 * NON IMPLEMENTE (renvoie 0.0). Aire. Dans la norme, le r�sultat est de
	 * type Area.
	 * */
	@Override
	public double area() {
		logger.error("Non impl�ment�e, utiliser : return CalculSansJava3D.CalculAire(this); (renvoi 0.0)");
		return 0.0;
	}

	@Override
	public DirectPositionList coord() {
		List<GM_OrientableSurface> lFaces = this.getListeFacettes();

		int n = lFaces.size();

		DirectPositionList dPL = new DirectPositionList();

		for (int i = 0; i < n; i++) {
			GM_OrientableSurface os = lFaces.get(i);
			dPL.addAll(os.coord());

		}

		int nbInt = this.boundary().interior.size();

		for (int i = 0; i < nbInt; i++) {

			lFaces = this.boundary().interior.get(i).getlisteFaces();
			n = lFaces.size();

			for (int j = 0; j < n; j++) {
				GM_OrientableSurface os = lFaces.get(j);
				dPL.addAll(os.coord());

			}

		}

		return dPL;
	}

	/**
	 * NON IMPLEMETE (renvoie 0.0). Volume.
	 * Dans la norme, le r�sultat est de type Volume.
	 */
	public double volume() {

		logger.error("Non impl�ment�e, utiliser : return CalculSansJava3D.CalculVolume(this);");

		return 0.0;

	}

	/** Constructeur par d�faut. */
	public GM_Solid() {
	}

	/**
	 * Constructeur � partir de la fronti�re.
	 */
	public GM_Solid(GM_SolidBoundary bdy) {
		this.boundary = bdy;
	}

	/**
	 * NON IMPLEMENTE. Constructeur � partir d'une enveloppe .
	 * @param env une enveloppe
	 */
	public GM_Solid(GM_Envelope env) {
		logger.error("NON IMPLEMENTE");
	}

	/**
	 * Red�finition de l'op�rateur "boundary" sur GM_Object. Renvoie une
	 * GM_SolidBoundary, c'est-�-dire un shell ext�rieur et �ventuellement un
	 * (des) shell(s) int�rieur(s).
	 */
	public GM_SolidBoundary boundary() {
		return this.boundary;
	}

	/**
	 * Boundary auquel est li� le solide
	 */
	private GM_SolidBoundary boundary = null;

	/**
	 * Constructeur � partir d'une liste de faces ext�rieures
	 * @param lOS une liste de faces ext�rieures
	 */
	public GM_Solid(ArrayList<GM_OrientableSurface> lOS) {

		boundary = new GM_SolidBoundary(lOS);

	}

	/**
	 * Constructeur
	 * @param multiSurf multisurface
	 */
	public GM_Solid(GM_MultiSurface<? extends GM_OrientableSurface> multiSurf) {
		ArrayList<GM_OrientableSurface> lOS = new ArrayList<GM_OrientableSurface>();
		List<? extends GM_OrientableSurface> lGMObj = multiSurf.getList();
		int nbElements = lGMObj.size();
		for (int i = 0; i < nbElements; i++) lOS.add(lGMObj.get(i));
		boundary = new GM_SolidBoundary(lOS);
	}

	/**
	 * Renvoie la liste des faces ext�rieures d'un solide
	 * 
	 * @return la liste des faces ext�rieures d'un solide
	 */
	public ArrayList<GM_OrientableSurface> getListeFacettes() {
		return this.boundary().exterior.getlisteFaces();
	}
	
	/**
	 * Permet de renvoyer une chaine de caract�re d�crivant un solide
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		ArrayList<GM_OrientableSurface> lOS =  this.getListeFacettes();
		int nbElement =lOS.size();
		sb.append("Solid(");
		for(int i=0;i<nbElement;i++) {
			
			
			sb.append(lOS.get(i).toString());
			sb.append("\n");
		}
		sb.append(");");
		return sb.toString();
	}

}
