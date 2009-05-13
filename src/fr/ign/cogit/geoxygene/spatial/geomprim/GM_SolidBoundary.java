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

/** NON UTILISE.
 * Repr�sente la fronti�re d'un GM_Solid.
 * Un GM_SolidBoundary est constitu� de 0 ou 1 shell ext�rieur et d'une liste de shells int�rieurs
 * pour �ventuellement repr�senter les solides � trous.
 * (le cas de 0 shell ext�rieur est pr�vu par le mod�le mais je n'ai pas compris pourquoi).
 * <P> A revoir : red�finir les constructeurs, et tester.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_SolidBoundary extends GM_PrimitiveBoundary {

	//////////////////////////////////////////////////////////////////////////////////
	// Attribut "exterior" et accesseurs /////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	/** Shell ext�rieur. */
	protected GM_Shell exterior;

	/** Renvoie le shell ext�rieur. */
	public GM_Shell getExterior () {return this.exterior;}

	/** Affecte un shell ext�rieur. */
	protected void setExterior (GM_Shell value) { this.exterior = value;}

	/** Renvoie 1 si un shell ext�rieur a �t� affect�, 0 sinon. */
	public int sizeExterior () {
		if ( this.exterior == null ) return 0;
		return 1;
	}




	//////////////////////////////////////////////////////////////////////////////////
	// Attribut "interior" et accesseurs /////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	/** Liste des shells int�rieurs. */
	protected List<GM_Shell> interior = new ArrayList<GM_Shell>();

	/** Renvoie la liste des shells int�rieurs */
	public List<GM_Shell> getInterior () {return this.interior;}

	/** Renvoie le shell int�rieur de rang i. */
	public GM_Shell getInterior (int i) {return this.interior.get(i);}

	/** Affecte une valeur au shell int�rieur de rang i. */
	protected void setInterior (int i, GM_Shell value) {this.interior.set(i, value);}

	/** Ajoute un shell int�rieur en fin de liste. */
	protected void addInterior (GM_Shell value) {this.interior.add(value);}

	/** Ajoute un shell int�rieur au rang i. */
	protected void addInterior (int i, GM_Shell value) {this.interior.add(i, value);}

	/** Efface le shell int�rieur de valeur "value". */
	protected void removeInterior (GM_Shell value)  {this.interior.remove(value);}

	/** Efface le shell int�rieur de rang i. */
	protected void removeInterior (int i)  {this.interior.remove(i);}

	/** Renvoie le nombre de shells int�rieurs. */
	public int sizeInterior () {return this.interior.size();}




	/////////////////////////////////////////////////////////////////////////////////////////
	// Constructeurs ////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Permet de cr�er un GM_SolidBoundary ne poss�dant pas de trous
	 */
	public GM_SolidBoundary(ArrayList<GM_OrientableSurface> lOS) {
		GM_Shell inShell = new GM_Shell(lOS);
		this.exterior = inShell;

	}
}
