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

package fr.ign.cogit.geoxygene.spatial.topocomp;

import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_Complex;
import fr.ign.cogit.geoxygene.spatial.toporoot.TP_Object;

/** Classe non impl�ment�e.
 * Complexe topologique, en parall�le a GM_Complex.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class TP_Complex extends TP_Object
{
	/* declaration des variables */
	protected long TP_ComplexID;

	/* accesseurs en lecture et en ecriture */
	long getTP_ComplexID(){return TP_ComplexID;}
	void setTP_ComplexID(long id){TP_ComplexID=id;}

	// mettre le tableau des �l�ments

	protected GM_Complex geometry;
	public GM_Complex getGeometry () {
		return this.geometry;
	}
	protected void setGeometry (GM_Complex value) {
		this.geometry = value;
	}
	public int sizeGeometry () {
		if ( this.geometry == null ) return 0;
		return 1;
	}



	protected TP_Complex maximalComplex;
	public TP_Complex getMaximalComplex () {
		return this.maximalComplex;
	}
	protected void setMaximalComplex (TP_Complex value) {
		this.maximalComplex = value;
	}
	public int cardMaximalComplex () {
		if ( this.maximalComplex == null ) return 0;
		return 1;
	}



	/** Renvoie TRUE si le complexe est maximal.   */
	public boolean isMaximal()   {
		return false;
	}

	/** Renvoie TRUE si le complexe est connnect�.  */
	public boolean isConnected() {
		return false;
	}

	/** Renvoie la fronti�re de self.*/
	/* public TP_Boundary boundary(DataSource data) {
        return null;
    }*/

	/**
	 * @param data
	 * @return the coBoundary
	 */
	public List<?> coBoundary(Geodatabase data) {
		return null;
	}

	/**
	 * Constructeur � partir d'une g�om�trie.
	 * @param gc
	 */
	public TP_Complex (final GM_Complex gc)  {
	}
}
