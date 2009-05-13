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

/**
 * Repr�sente la fronti�re d'un GM_Surface.
 * Un GM_SurfaceBoundary consiste en un anneau ext�rieur, et �ventuellement des anneaux int�rieurs
 * pour repr�senter les surfaces � trous
 * (le cas de 0 anneeau ext�rieur est pr�vu par le mod�le mais je n'ai pas compris pourquoi).
 * <P> Pour construitre une GM_SurfaceBoundary, utiliser le constructeur GM_SurfaceBoundary(GM_Ring ring)
 * qui affecte la fronti�re ext�rieure,
 * puis s'il le faut ajouter des anneaux int�rieurs avec GM_SurfaceBoundary.addInterior(GM_Ring ring).
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_SurfaceBoundary extends GM_PrimitiveBoundary {


	//////////////////////////////////////////////////////////////////////////////////
	// Attribut "exterior" et accesseurs /////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	/** Anneau ext�rieur. */
	protected GM_Ring exterior;

	/** Renvoie l'anneau ext�rieur */
	public GM_Ring getExterior () { return this.exterior; }

	/** Affecte une valeur � l'anneau ext�rieur */
	protected void setExterior (GM_Ring value) { this.exterior = value; }

	/** Renvoie 1 si l'anneau ext�rieur est affect�, 0 sinon.
	 * Il para�t qu'il existe des cas o� on peut avoir une surface avec que des fronti�res int�rieures. */
	public int sizeExterior () {
		if ( this.exterior == null ) return 0;
		return 1;
	}



	//////////////////////////////////////////////////////////////////////////////////
	// Attribut "interior" et accesseurs /////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	/** Anneau(x) int�rieur(s) en cas de trou(s) : liste de GM_Ring */
	protected List<GM_Ring> interior;

	/** Renvoie la liste des anneaux int�rieurs */
	public List<GM_Ring> getInterior () {return this.interior;}

	/** Renvoie l'anneau int�rieur de rang i */
	public GM_Ring getInterior (int i) {return this.interior.get(i);}

	/** Affecte un GM_Ring au rang i */
	public void setInterior (int i, GM_Ring value) {this.interior.set(i, value);}

	/** Ajoute un GM_Ring en fin de liste */
	public void addInterior (GM_Ring value) {this.interior.add(value);}

	/** Ajoute un GM_ring au rang i */
	public void addInterior (int i, GM_Ring value) {this.interior.add(i, value);}

	/** Efface le (ou les) GM_Ring pass� en param�tre */
	public void removeInterior (GM_Ring value)  {this.interior.remove(value);}

	/** Efface le GM_Ring de rang i */
	public void removeInterior (int i)  {this.interior.remove(i);}

	/** Nombre d'anneaux int�rieurs */
	public int sizeInterior () {return this.interior.size();}



	/////////////////////////////////////////////////////////////////////////////////////////
	// Constructeurs ////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Constructeur par d�faut */
	public GM_SurfaceBoundary() {
		exterior = null;
		interior = new ArrayList<GM_Ring>();
	}

	/** Constructeur � partir d'un GM_Ring et d'un seul (pour des surfaces sans trou) */
	public GM_SurfaceBoundary(GM_Ring ring) {
		exterior = ring;
		interior = new ArrayList<GM_Ring>();
	}

}
