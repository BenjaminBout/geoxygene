/**
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

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Surface;

/**
 * Portion homogene d'une GM_Surface.
 *
 * <P> Modification de la norme : cette classe herite de GM_Surface. Du coup on a fait sauter le lien d'implementation de GM_GenericSurface.
 * Un GM_SurfacePatch sera une GM_Surface composee d'un et d'un seul segment qui sera lui-meme.
 * Les methodes addSegment, removeSegment, etc... seront interdites.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

abstract public class GM_SurfacePatch   extends GM_Surface
/*implements GM_GenericSurface*/ {


	//////////////////////////////////////////////////////////////////////////////////
	// Attributs /////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	/** Mecanisme d'interpolation, selon une liste de codes.
	 * La liste de codes est la suivante :
	 * {none, planar, spherical, elliptical, conic, tin, parametricCurve, polynomialSpline, rationalSpline, triangulatedSpline}.    */
	protected String interpolation;

	/** Renvoie l'attribut interpolation. */
	public String getInterpolation () {
		return this.interpolation;
	}

	/** Continuite entre self et ses voisins qui partagent une frontiere commune.
	 * Vaut 0 par defaut. */
	protected int numDerivativesOnBoundary = 0;

	/** Renvoie l'attribut numDerivativesOnBoundary. */
	public int getNumDerivativesOnBoundary () {
		return this.numDerivativesOnBoundary;
	}





	//////////////////////////////////////////////////////////////////////////////////
	// Methodes (abstaites, implementee dans les sous-classes)////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	/** Renvoie un GM_SurfacePatch de sens oppose. Methode abstraite implementee dans les sous-classes */
	abstract public GM_SurfacePatch reverse() ;





	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	// Redefinition des methodes sur l'attribut "patch" //////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	// le but est d'interdire l'acces a certaines methodes pour un GM_SurfacePatch

	/** Renvoie le patch de rang i. Passer necessairement 0 en parametre car un GM_SurfacePatch ne contient qu'un patch qui est lui-meme. */
	@Override
	public GM_SurfacePatch getPatch (int i) {
		if (i != 0) {
			System.out.println("Recherche d'un segment avec i<>0 alors qu'un GM_SurfacePatch ne contient qu'un patch qui est lui-meme."); //$NON-NLS-1$
			return null;
		}
		return this.patch.get(i);
	}

	/** Ne fait rien car un GM_SurfacePatch ne contient qu'un patch qui est lui-meme. */
	@Override
	public void setPatch (int i, GM_SurfacePatch value) {
		System.out.println("Methode inapplicable sur un GM_SurfacePatch. La methode ne fait rien."); //$NON-NLS-1$
	}

	/** Ne fait rien car un GM_SurfacePatch ne contient qu'un patch qui est lui-meme. */
	@Override
	public void addPatch (GM_SurfacePatch value)  {
		System.out.println("Methode inapplicable sur un GM_SurfacePatch. La methode ne fait rien."); //$NON-NLS-1$
	}

	/** Ne fait rien car un GM_SurfacePatch ne contient qu'un patch qui est lui-meme. */
	@Override
	public void addPatch (int i, GM_SurfacePatch value) {
		System.out.println("Methode inapplicable sur un GM_SurfacePatch. La methode ne fait rien."); //$NON-NLS-1$
	}

	/** Ne fait rien car un GM_SurfacePatch ne contient qu'un patch qui est lui-meme. */
	@Override
	public void removePatch (GM_SurfacePatch value)  {
		System.out.println("Methode inapplicable sur un GM_SurfacePatch. La methode ne fait rien."); //$NON-NLS-1$
	}

	/** Ne fait rien car un GM_SurfacePatch ne contient qu'un patch qui est lui-meme.*/
	@Override
	public void removePatch (int i) {
		System.out.println("Methode inapplicable sur un GM_SurfacePatch. La methode ne fait rien."); //$NON-NLS-1$
	}

}
