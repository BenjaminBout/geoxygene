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

package fr.ign.cogit.geoxygene.contrib.cartetopo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Classe repr�sentant un cycle dans une carte topologique.
 * @author Julien Perret
 *
 */
public class Cycle {
	protected List<Arc> arcs;
	protected List<Boolean> orientationsArcs;
	protected GM_LineString geometrie;
	protected boolean aGauche;
	
	/**
	 * Construit un cycle
	 * @param arcs liste des arcs du cycle
	 * @param orientationsArcs orientations respectives des arcs du cycle
	 * @param geometrie g�om�trie du cycle (GM_LineString)
	 * @param aGauche vrai si le cycle parcours son premier arcs par la gauche, faux sinon.
	 */
	public Cycle(List<Arc> arcs, List<Boolean> orientationsArcs, GM_LineString geometrie, boolean aGauche) {
		this.arcs=arcs;
		this.orientationsArcs=orientationsArcs;
		this.geometrie=geometrie;
		this.aGauche=aGauche;
	}
	/**
	 * R�cup�re la liste des arcs du cycle
	 * @return the arcs la liste des arcs du cycle
	 */
	public List<Arc> getArcs() {return arcs;}
	/**
	 * D�finit la liste des arcs du cycle
	 * @param arcs la liste des arcs du cycle
	 */
	public void setArcs(List<Arc> arcs) {this.arcs = arcs;}
	/**
	 * R�cup�re la liste des orientations du cycle
	 * @return la liste des orientations du cycle
	 */
	public List<Boolean> getOrientationsArcs() {return orientationsArcs;}
	/**
	 * D�finit la liste des orientations du cycle
	 * @param orientationsArcs les orientations des arcs du cycle
	 */
	public void setOrientationsArcs(List<Boolean> orientationsArcs) {this.orientationsArcs = orientationsArcs;}
	/**
	 * R�cup�re la g�om�trie du cycle
	 * @return la g�om�trie du cycle
	 */
	public GM_LineString getGeometrie() {return geometrie;}
	/**
	 * D�finit la g�om�trie du cycle
	 * @param geometrie la g�om�trie du cycle
	 */
	public void setGeometrie(GM_LineString geometrie) {this.geometrie = geometrie;}
	/**
	 * R�cup�re le sens de parcours du premier arc du cycle
	 * @return le sens de parcours du premier arc du cycle : vrai s'il est parcours par la gauche, faux sinon.
	 */
	public boolean isAGauche() {return aGauche;}
	/**
	 * D�finit le sens de parcours du premier arc du cycle
	 * @param gauche le sens de parcours du premier arc du cycle : vrai s'il est parcours par la gauche, faux sinon.
	 */
	public void setAGauche(boolean gauche) {aGauche = gauche;}
	/**
	 * Construit la liste des faces � l'int�rieur du cycle en parcourant ce cycle.
	 * Attention : Seules les faces touchant l'ext�rieur du cycle sont renvoy�es.
	 * NB : cette m�thode sert � optimiser un peu le calcul de la topologie des faces en �vitant des calculs d'intersection de g�omtries.
	 * @return la liste des faces � l'int�rieur du cycle
	 */
	public Collection<Face> getListeFacesInterieuresDuCycle() {
		Set<Face> listeFacesInterieuresDuCycle = new HashSet<Face>();
		for(int index=0;index<this.getArcs().size();index++) {
			Arc arc=this.getArcs().get(index);
			boolean orientation = this.getOrientationsArcs().get(index);
			if ( ( ( orientation && !this.isAGauche() ) || ( !orientation && this.isAGauche() ) )&&(arc.getFaceGauche()!=null)) listeFacesInterieuresDuCycle.add(arc.getFaceGauche());
			else if (arc.getFaceDroite()!=null) listeFacesInterieuresDuCycle.add(arc.getFaceDroite());
		}
		return listeFacesInterieuresDuCycle;
	}
	/**
	 * Construit la liste des faces � l'ext�rieur du cycle en parcourant ce cycle.
	 * Attention : Seules les faces touchant l'ext�rieur du cycle sont renvoy�es. 
	 * Le r�sultat est donc diff�rent des voisins du cycle.
	 * @return la liste des faces � l'ext�rieur du cycle
	 */
	public List<Face> getListeFacesExterieuresDuCycle() {
		List<Face> listeFacesExterieuresDuCycle = new ArrayList<Face>();
		for(Arc arc:this.getArcs()) listeFacesExterieuresDuCycle.addAll(arc.getNoeudIni().faces());
		listeFacesExterieuresDuCycle.remove(this.isAGauche()?this.getArcs().get(0).getFaceGauche():this.getArcs().get(0).getFaceDroite());
		return listeFacesExterieuresDuCycle;
	}
}
