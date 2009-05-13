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

package fr.ign.cogit.geoxygene.spatial.topoprim;

import java.util.ArrayList;
import java.util.List;


/**
 * Fronti�re d'une face topologique.
 * Constitu�e de plusieurs TP_Ring, l'un �tant l'ext�rieur, les autres l'int�rieur.
 * Un TP_Ring est orient� de telle sorte que la face est � sa gauche.
 * La fronti�re d'une face est une expression du type :
 * Face.Boundary() = b : TP_FaceBoundary = b.exterior + b.interior.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class TP_FaceBoundary extends TP_PrimitiveBoundary {

	/** Fronti�re ext�rieure. */
	protected TP_Ring exterior;

	/** Renvoie la fronti�re ext�rieure. */
	public TP_Ring getExterior () {
		return this.exterior;
	}

	/** Fronti�res int�rieures. */
	protected List<TP_Ring> interior;

	/** Renvoie l'anneau int�rieure de rang i. */
	public TP_Ring getInterior (int i) {
		return this.interior.get(i);
	}

	/** Ajoute un anneau int�rieur. */
	public void addInterior (TP_Ring ring) {
		this.interior.add(ring);
		for (int i=0; i<ring.sizeTerm(); i++) this.addTerm(ring.getTerm(i));
	}

	/** Nombre d'anneaux int�rieurs. */
	public int sizeInterior () {
		return this.interior.size();
	}

	/** Constructeur par d�faut. */
	public TP_FaceBoundary() {
		exterior = null;
		interior = new ArrayList<TP_Ring>();
	}

	/** Constructeur � partir d'un anneau ext�rieur. */
	public TP_FaceBoundary(TP_Ring ext) {
		exterior = ext;
		interior = new ArrayList<TP_Ring>();
		for (int i=0; i<ext.sizeTerm(); i++) this.addTerm(ext.getTerm(i));
	}

}
