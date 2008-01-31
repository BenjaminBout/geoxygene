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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Classe des noeuds de la carte topo.
 * Les arcs ont pour g�om�trie un GM_Point.
 * 
 * English: nodes of topological map
 * @author  Musti�re/Bonin
 * @version 1.0
 */

public class Noeud extends ElementCarteTopo {

	public Noeud() { 
	}

/////////////////////////////////////////////////////////////////////////////////////////////////
//	G�om�trie
/////////////////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie le GM_Point qui d�finit la g�om�trie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** D�finit le GM_Point qui d�finit la g�om�trie de self */
	public void setGeometrie(GM_Point geometrie) {this.setGeom(geometrie);}
	/** Renvoie le DirectPosition qui d�finit les coordonn�es de self */
	public DirectPosition getCoord() {return (DirectPosition) this.getGeometrie().getPosition();}
	/** D�finit le DirectPosition qui d�finit les coordonn�es de self */
	public void setCoord(DirectPosition dp) {geom = new GM_Point(dp);}




/////////////////////////////////////////////////////////////////////////////////////////////////
//	Topologie de r�seau arcs / noeuds
/////////////////////////////////////////////////////////////////////////////////////////////////
	private List entrants = new ArrayList();
	/** Renvoie la liste (non ordonn�e) des arcs entrants de self. 
	 *  La distinction entrant/sortant s'entend au sens du codage de la g�om�trie. 
	 *  (et non au sens de l'orientation du graphe, comme avec les attributs entrantsOrientation)
	 */
	public List getEntrants() {
		return entrants;
	}
	/** Ajoute un arc entrant � la liste des arcs entrants de self */
	public void addEntrant (Arc arc) {
		if (arc != null && !entrants.contains(arc)) {
			entrants.add(arc);
			if (arc.getNoeudFin() != this)
				arc.setNoeudFin(this);
		}
	}
	/** Enl�ve un arc entrant � la liste des arcs entrants de self */
	public void enleveEntrant (Arc arc) {
		if (arc == null) return;
		if (!entrants.contains(arc)) return;
		entrants.remove(arc);
		arc.setNoeudFin(null);
	}

	private List sortants = new ArrayList();
	/** Renvoie la liste (non ordonn�e) des arcs sortants de self    
	 *  La distinction entrant/sortant s'entend au sens du codage de la g�om�trie. 
	 *  (et non au sens de l'orientation du graphe, comme avec les attributs entrantsOrientation)
	 */
	public List getSortants() {
		return sortants;
	}
	/** Ajoute un arc sortant � la liste des arcs sortants de self */
	public void addSortant (Arc arc) {
		if (arc != null && !sortants.contains(arc)) {
			sortants.add(arc);
			if (arc.getNoeudIni() != this)
				arc.setNoeudIni(this);
		}
	}
	/** Enl�ve un arc sortant � la liste des arcs entrants de self */
	public void enleveSortant (Arc arc) {
		if (arc == null) return;
		if (!sortants.contains(arc)) return;
		sortants.remove(arc);
		arc.setNoeudIni(null);
	}

	/** Renvoie la liste (non ordonn�e) de tous les arcs entrants et sortants de self.
	 * NB : si un arc est � la fois entrant et sortant (boucle), il est 2 fois dans la liste
	 */    
	public List arcs() {
		List Arcs = new ArrayList();
		Arcs.addAll(this.getSortants());
		Arcs.addAll(this.getEntrants());
		return Arcs;        
	}

	/** Renvoie la liste des noeuds voisins de self dans le r�seau 
	 *  sans tenir compte de l'orientation (i.e. tous les arcs sont consid�r�s en double sens) */
	public List voisins() {
		List voisins = new ArrayList();
		List entrants = this.getEntrants();
		Arc arc;
		Iterator iterentrants = entrants.iterator();
		while(iterentrants.hasNext()) {
			arc = (Arc) iterentrants.next();
			voisins.add(arc.getNoeudIni());
		}
		List sortants = this.getSortants();        
		Iterator itersortants = sortants.iterator();
		while(itersortants.hasNext()) {
			arc = (Arc) itersortants.next();
			voisins.add(arc.getNoeudFin());
		}
		return voisins;
	}

/////////////////////////////////////////////////////////////////////////////////////////////////
//	Gestion de graphe noeuds / faces
/////////////////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie la liste des faces s'appuyant sur self */
	public List faces() {
		HashSet faces = new HashSet();
		List arcs = this.arcs();
		Arc arc;
		Iterator iterarcs = arcs.iterator();
		while (iterarcs.hasNext()) {
			arc = (Arc) iterarcs.next();
			faces.addAll(arc.faces());
		}
		return new ArrayList(faces);
	}


/////////////////////////////////////////////////////////////////////////////////////////////////
//	Gestion de r�seau orient�
/////////////////////////////////////////////////////////////////////////////////////////////////

	/** les entrants du noeud, au sens de l'orientation, 
	 * (alors que pour getEntrants c'est au sens de la g�om�trie) **/
	public List entrantsOrientes() {
		List arcsEntrants = this.getEntrants();
		List arcsSortants = this.getSortants();
		List arcs = new ArrayList();
		Arc arc;
		int i;

		for (i=0; i<arcsEntrants.size(); i++) {
			arc = (Arc)arcsEntrants.get(i);
			if ( ( arc.getOrientation() == 1 ) || ( arc.getOrientation() == 2 ) ) arcs.add(arc);
		}
		for (i=0; i<arcsSortants.size(); i++) {
			arc = (Arc)arcsSortants.get(i);
			if ( ( arc.getOrientation() == -1 ) || ( arc.getOrientation() == 2 ) ) arcs.add(arc);
		}
		return arcs;
	}

	/** les sortants du noeud, au sens de l'orientation, 
	 * (alors que pour getSortants c'est au sens de la g�om�trie) **/
	public List sortantsOrientes() {
		List arcsEntrants = this.getEntrants();
		List arcsSortants = this.getSortants();
		List arcs = new ArrayList();
		Arc arc;
		int i;

		for (i=0; i<arcsEntrants.size(); i++) {
			arc = (Arc)arcsEntrants.get(i);
			if ( ( arc.getOrientation() == -1 ) || ( arc.getOrientation() == 2 ) ) arcs.add(arc);
		}
		for (i=0; i<arcsSortants.size(); i++) {
			arc = (Arc)arcsSortants.get(i);
			if ( ( arc.getOrientation() == 1 ) || ( arc.getOrientation() == 2 ) ) arcs.add(arc);
		}
		return arcs;
	}


/////////////////////////////////////////////////////////////////////////////////////////////////
//	Gestion de type carte topologique
/////////////////////////////////////////////////////////////////////////////////////////////////
//	Les arcs sont class�s autour d'un noeud en fonction de leur g�om�trie. 
//	Ceci permet en particulier de parcourir facilement les cycles d'un graphe.
/////////////////////////////////////////////////////////////////////////////////////////////////

	/** Arcs incidents � un noeuds, class�s en tournant autour du noeud dans l'ordre trigonom�trique,
	 *  et qualifi�s d'entrants ou sortants, au sens de la g�o�mtrie (utile particuli�rement � la gestion des boucles).
	 *
	 *  NB : renvoie une liste de liste:
	 *      Liste.get(0) = liste des arcs (de la classe 'Arc')
	 *      Liste.get(1) = liste des orientations de type Boolean, 
	 *                    true = entrant, false = sortant)
	 *  NB : Classement effectu� sur la direction donn�e par le premier point de l'arc apr�s le noeud.
	 *  NB : Le premier arc est celui dont la direction est la plus proche de l'axe des X, en tournant dans le sens trigo.
	 *  NB : Ce classement est recalcul� en fonction de la g�om�trie � chaque appel de la m�thode.
	 */
	public List arcsClasses() {
		List arcsClasses = new ArrayList();
		List arcsClassesOrientation = new ArrayList();
		List arcsEntrants = new ArrayList(this.getEntrants());
		List arcsSortants = new ArrayList(this.getSortants());
		List arcs = new ArrayList();
		List angles = new ArrayList();
		List orientations = new ArrayList();
		List resultat = new ArrayList();
		Arc arc;
		Angle angle;
		double angleMin, angleCourant;
		int imin;
		Iterator itArcs;
		int i;

		// recherche de l'angle de d�part de chaque arc sortant
		itArcs = arcsSortants.iterator();
		while ( itArcs.hasNext() ) {
			arc = (Arc)itArcs.next();
			angle = new Angle((DirectPosition)arc.getCoord().get(0),(DirectPosition)arc.getCoord().get(1));
			arcs.add(arc);
			angles.add(angle);
			orientations.add(new Boolean(false));
		}
		// recherche de l'angle de d�part de chaque arc entrant
		itArcs = arcsEntrants.iterator();
		while ( itArcs.hasNext() ) {
			arc = (Arc)itArcs.next();
			angle = new Angle((DirectPosition)arc.getCoord().get(arc.getCoord().size()-1),
					(DirectPosition)arc.getCoord().get(arc.getCoord().size()-2));
			arcs.add(arc);
			angles.add(angle);
			orientations.add(new Boolean(true));
		}
		// classement des arcs 
		while ( !(arcs.isEmpty()) ) {
			angleMin = ((Angle)angles.get(0)).getAngle();
			imin = 0;
			for(i=1;i<arcs.size() ;i++) {
				angleCourant = ((Angle)angles.get(i)).getAngle();
				if ( angleCourant < angleMin ) {
					angleMin = angleCourant;
					imin = i;
				}
			}
			arcsClasses.add(arcs.get(imin));
			arcsClassesOrientation.add(orientations.get(imin));
			arcs.remove(imin);
			angles.remove(imin);
			orientations.remove(imin);
		}
		//retour du r�sultat
		resultat.add(arcsClasses);
		resultat.add(arcsClassesOrientation);
		return resultat;
	}


/////////////////////////////////////////////////////////////////////////////////////////////////
//	Gestion des groupes
/////////////////////////////////////////////////////////////////////////////////////////////////
	/** Groupes auquels self appartient */
	private List listeGroupes = new ArrayList();
	/** Renvoie la liste des groupes de self*/
	public List getListeGroupes() {return this.listeGroupes;}
	/** D�finit la liste des groupes de self*/
	public void setListeGroupes(List liste) {this.listeGroupes = liste;}
	/** Ajoute un groupe � self*/ 
	public void addGroupe(Groupe groupe) {
		if (groupe != null && !listeGroupes.contains(groupe)) {
			this.listeGroupes.add(groupe);	
			if (!groupe.getListeNoeuds().contains(this))
				groupe.addNoeud(this);
		}
	}

	/** Liste des noeuds voisins de self au sein d'un groupe. 
	 *  Renvoie une liste vide si il n'y en a pas */
	public List voisins(Groupe groupe) {
		List arcsDuGroupe = new ArrayList();
		List arcsVoisins = new ArrayList();
		Arc arcVoisin;
		List noeudsDuGroupe = new ArrayList();
		List noeudsVoisins = new ArrayList();
		Noeud noeudVoisin;
		int i;

		noeudsDuGroupe = groupe.getListeNoeuds(); 
		arcsDuGroupe = groupe.getListeArcs();  

		// gestion des arcs entrants
		arcsVoisins = this.getEntrants();
		for (i=0;i<arcsVoisins.size();i++){
			arcVoisin = (Arc)arcsVoisins.get(i);
			if ( arcsDuGroupe.contains(arcVoisin) ) {
				noeudVoisin = arcVoisin.getNoeudIni();  
				if ( noeudVoisin == null ) continue;
				if ( noeudsDuGroupe.contains(noeudVoisin) && !noeudsVoisins.contains(noeudVoisin) ) {
					noeudsVoisins.add(noeudVoisin);
				}
			}
		}
		// gestion des arcs sortants
		arcsVoisins = this.getSortants();
		for (i=0;i<arcsVoisins.size();i++){
			arcVoisin = (Arc)arcsVoisins.get(i);
			if ( arcsDuGroupe.contains(arcVoisin) ) {
				noeudVoisin = arcVoisin.getNoeudFin();  
				if ( noeudVoisin == null ) continue;
				if ( noeudsDuGroupe.contains(noeudVoisin) && !noeudsVoisins.contains(noeudVoisin) ) {
					noeudsVoisins.add(noeudVoisin);
				}
			}
		}
		return noeudsVoisins;
	};


/////////////////////////////////////////////////////////////////////////////////////////////////
//	Op�rateurs de calculs sur les noeuds 
/////////////////////////////////////////////////////////////////////////////////////////////////
	/** Distance euclidienne. Valable pour des coordonn�es en 2 ou 3D. */
	public double distance (Noeud noeud) {
		return Distances.distance(this.getCoord(), noeud.getCoord());
	}

	/** Distance euclidienne dans le plan (x,y). */
	public double distance2D (Noeud noeud) {
		return Distances.distance2D(this.getCoord(), noeud.getCoord());
	}

	/** Distance euclidienne � un arc. */
	public double distance (Arc arc) {
		return Distances.distance(this.getCoord(), arc.getGeometrie());
	}


///////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////
//	Diff�rentes variantes du plus court chemin
///////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////

	// attributs internes utiles pour les calculs de plus court chemin 
	/** utilisation interne : ne pas utiliser */
	private double _distance;
	/** utilisation interne : ne pas utiliser */
	private Arc _arcPrecedent;
	/** utilisation interne : ne pas utiliser */
	private Noeud _noeudPrecedent;

	/** Plus court chemin de this vers arriv�e, en tenant compte du sens de circulation.
	 * Le pcc s'appuie sur l'attribut 'poids' des arcs, qui doit �tre rempli auparavant.
	 *   
	 * @param maxLongueur
	 *   Pour optimiser: on arr�te de chercher et on renvoie null si il n'y a pas de pcc
	 *   de taille inf�rieure � maxLongueur (inactif si maxLongueur = 0).
	 *
	 * @return
	 * 	Renvoie un groupe, qui contient (dans l'ordre) les noeuds et arcs du plus court chemin.
	 *  Cas particuliers : 
	 *    Si this = arriv�e, renvoie un groupe contenant uniquement self;
	 *    Si this et arriv�e sont sur 2 composantes connexes distinctes (pas de pcc), renvoie null.
	 *    
	 * NB : l'attribut orientation DOIT etre renseign�.
	 * NB : ce groupe contient le noeud de d�part et le noeud d'arriv�e.
	 */
	public Groupe plusCourtChemin(Noeud arrivee, double maxLongueur) {
		List noeudsFinaux = new ArrayList();
		List arcsFinaux = new ArrayList();
		List noeudsVoisins = new ArrayList();
		List arcsVoisins = new ArrayList();
		List distancesVoisins = new ArrayList();
		List traites = new ArrayList();
		List aTraiter = new ArrayList();
		int i;
		Arc arcVoisin;
		Noeud noeudVoisin, plusProche, suivant;
		double dist;

		try {
			if ( this.getCarteTopo() == null ) { 
				System.out.println("ATTENTION : le noeud "+this+" ne fait pas partie d'une carte topo");
				System.out.println("            Impossible de calculer un plus court chemin ");
				return null;
			}
			if ( this.getCarteTopo().getPopGroupes() == null ) { 
				System.out.println("ATTENTION : le noeud "+this+" fait partie d'une carte topo sans population de groupes");
				System.out.println("            Impossible de calculer un plus court chemin ");
				return null;
			}
			Groupe plusCourtChemin = (Groupe)this.getCarteTopo().getPopGroupes().nouvelElement() ;

			if ( this == arrivee ) {
				plusCourtChemin.addNoeud(this);
				this.addGroupe(plusCourtChemin);
				return plusCourtChemin;
			}
			this._distance = 0;
			this.chercheArcsNoeudsVoisins(noeudsVoisins, distancesVoisins, arcsVoisins);

			for (i=0; i<noeudsVoisins.size(); i++) {
				noeudVoisin = (Noeud)noeudsVoisins.get(i);
				arcVoisin = (Arc)arcsVoisins.get(i);
				dist = ((Double)distancesVoisins.get(i)).doubleValue(); 
				noeudVoisin._distance = dist;
				noeudVoisin._arcPrecedent = arcVoisin;
				noeudVoisin._noeudPrecedent = this;
			}
			aTraiter.addAll(noeudsVoisins);

			// Phase "avant" 
			while (aTraiter.size() != 0 ) {
				// choisi le noeud � marquer comme trait� parmi les voisins
				plusProche = (Noeud)aTraiter.get(0);
				for (i=1; i<aTraiter.size(); i++) {
					if ( ((Noeud)aTraiter.get(i))._distance < plusProche._distance ) {
						plusProche = (Noeud)aTraiter.get(i);
					}
				}
				traites.add(plusProche);
				aTraiter.remove(plusProche);
				if ( plusProche == arrivee ) break; //il s'agit du noeud d'arriv�e
				if ( maxLongueur != 0 ) {
					if ( plusProche._distance > maxLongueur ) return null; // heuristique pour stopper la recherche 
				}
				plusProche.chercheArcsNoeudsVoisins(noeudsVoisins, distancesVoisins, arcsVoisins);
				for (i=0; i<noeudsVoisins.size(); i++) {
					noeudVoisin = (Noeud)noeudsVoisins.get(i);
					arcVoisin = (Arc)arcsVoisins.get(i);
					dist = ((Double)distancesVoisins.get(i)).doubleValue(); 
					if ( traites.contains(noeudVoisin) ) continue; // Noeud d�j� trait�
					if ( aTraiter.contains(noeudVoisin) ) { // Noeud d�j� atteint, on voit si on a trouv� un chemin plus court pour y acc�der
						if ( noeudVoisin._distance > (plusProche._distance+dist) ) {
							noeudVoisin._distance = plusProche._distance+dist;
							noeudVoisin._arcPrecedent = arcVoisin;
							noeudVoisin._noeudPrecedent = plusProche;
						}
						continue;
					}
					// Nouveau noeud atteint, on l'initialise
					noeudVoisin._distance = plusProche._distance+dist;
					noeudVoisin._arcPrecedent = arcVoisin;
					noeudVoisin._noeudPrecedent = plusProche;
					aTraiter.add(noeudVoisin);
				}
			}

			// Phase "arriere" 
			if ( ! traites.contains(arrivee) ) return null;
			suivant = arrivee;
			while (true) {
				arcsFinaux.add(0, suivant._arcPrecedent);
				((Arc)suivant._arcPrecedent).addGroupe(plusCourtChemin);
				suivant = (Noeud)suivant._noeudPrecedent;
				if ( suivant == this ) break;
				noeudsFinaux.add(0, suivant);
				suivant.addGroupe(plusCourtChemin);
			}

			noeudsFinaux.add(0, this);
			this.addGroupe(plusCourtChemin);
			noeudsFinaux.add(arrivee);
			arrivee.addGroupe(plusCourtChemin);

			plusCourtChemin.setListeArcs(arcsFinaux);
			plusCourtChemin.setListeNoeuds(noeudsFinaux);
			return plusCourtChemin;

		} catch (Exception e) {
			System.out.println("----- ERREUR dans calcul de plus court chemin. ");
			e.printStackTrace();
			return null;
		}
	}

	// M�thode utile au plus court chemin
	private void chercheArcsNoeudsVoisins(List noeudsVoisins, List distancesVoisins, 
			List arcsVoisins) {
		List arcsEntrants = new ArrayList();   // au sens de la geometrie
		List arcsSortants = new ArrayList();   // au sens de la geometrie
		List arcsSortants2 = new ArrayList();   // au sens de la circulation
		List noeudsSortants2 = new ArrayList(); // au sens de la circulation
		List distancesSortants2 = new ArrayList(); // au sens de la circulation
		Noeud noeud;
		Arc arc;
		Double distance;
		int i, j;

		noeudsVoisins.clear();
		distancesVoisins.clear();
		arcsVoisins.clear();

		try{
			arcsEntrants = this.getEntrants();
			arcsSortants = this.getSortants();
		} catch (Exception e) {e.printStackTrace();}

		// transformation du sens g�om�trique au sens de circulation
		for (i=0; i<arcsEntrants.size(); i++) {
			arc = (Arc)arcsEntrants.get(i);
			if ( ( arc.getOrientation() == -1 ) || ( arc.getOrientation() == 2 ) ) {
				if ( arc.getNoeudIni() != null ) {
					arcsSortants2.add(arc);
					noeudsSortants2.add((Noeud)arc.getNoeudIni());
					distancesSortants2.add(new Double(arc.getPoids()));
				}
			}
		}
		for (i=0; i<arcsSortants.size(); i++) {
			arc = (Arc)arcsSortants.get(i);
			if ( ( arc.getOrientation() == 1 ) || ( arc.getOrientation() == 2 ) ) {
				if ( arc.getNoeudFin() != null ) {
					arcsSortants2.add(arc);
					noeudsSortants2.add((Noeud)arc.getNoeudFin());
					distancesSortants2.add(new Double(arc.getPoids()));
				}
			}
		}

		// en choisissant l'arc le plus court, si il existe des arcs parall�les (m�mes noeuds ini et fin)
		for (i=0; i<noeudsSortants2.size(); i++) {
			// choix du plus court arc menant au noeud sortant
			noeud = (Noeud)noeudsSortants2.get(i);
			if ( noeudsVoisins.contains(noeud) ) continue;
			arc = (Arc)arcsSortants2.get(i);
			distance = (Double)distancesSortants2.get(i); 
			for (j=i+1;j<noeudsSortants2.size(); j++) {
				if ( noeud == (Noeud)noeudsSortants2.get(j) ) {
					if ( ((Double)distancesSortants2.get(j)).doubleValue() < distance.doubleValue() ) {
						distance = (Double)distancesSortants2.get(j);
						arc = (Arc)arcsSortants2.get(j);
					}
				}
			}
			arcsVoisins.add(arc);
			noeudsVoisins.add(noeud);
			distancesVoisins.add(distance);
		}
	}


	/** Plus court chemin de this vers arriv�e, en tenant compte du sens de circulation,  
	 * au sein d'un groupe d'arcs et de noeuds.
	 * Le pcc s'appuie sur l'attribut 'poids' des arcs, qui doit �tre rempli auparavant.
	 * 
	 * @param maxLongueur
	 *    Pour optimiser: on arr�te de chercher et on renvoie null si il n'y a pas de pcc
	 *    de taille inf�rieure � maxLongueur (inactif si maxLongueur = 0).
	 *    
	 * @return: 
	 * 	Renvoie un groupe, qui contient (dans l'ordre) les noeuds et arcs du plus court chemin.
	 *  Cas particuliers : 
	 *    Si this = arriv�e, renvoie un groupe contenant uniquement self;
	 *    Si this et arriv�e sont sur 2 composantes connexes distinctes (pas de pcc), renvoie null.
	 *    
	 * NB : l'attribut orientation DOIT etre renseign�.
	 * NB : ce groupe contient le noeud de d�part et le noeud d'arriv�e.
	 */
	public Groupe plusCourtChemin(Noeud arrivee, Groupe groupe, double maxLongueur) {
		List noeudsFinaux = new ArrayList();
		List arcsFinaux = new ArrayList();
		List noeudsVoisins = new ArrayList();
		List arcsVoisins = new ArrayList();
		List distancesVoisins = new ArrayList();
		List traites = new ArrayList();
		List aTraiter = new ArrayList();
		int i;
		Arc arcVoisin;
		Noeud noeudVoisin, plusProche, suivant;
		double dist;

		try {
			if ( this.getCarteTopo() == null ) { 
				System.out.println("ATTENTION : le noeud "+this+" ne fait pas partie d'une carte topo");
				System.out.println("            Impossible de calculer un plus court chemin ");
				return null;
			}
			if ( this.getCarteTopo().getPopGroupes() == null ) { 
				System.out.println("ATTENTION : le noeud "+this+" fait partie d'une carte topo sans population de groupes");
				System.out.println("            Impossible de calculer un plus court chemin ");
				return null;
			}
			Groupe plusCourtChemin = (Groupe)this.getCarteTopo().getPopGroupes().nouvelElement() ;

			if ( this == arrivee ) {
				plusCourtChemin.addNoeud(this);
				this.addGroupe(plusCourtChemin);
				return plusCourtChemin;
			}
			this._distance = 0;
			this.chercheArcsNoeudsVoisins(groupe, noeudsVoisins, distancesVoisins, arcsVoisins);
			for (i=0; i<noeudsVoisins.size(); i++) {
				noeudVoisin = (Noeud)noeudsVoisins.get(i);
				arcVoisin = (Arc)arcsVoisins.get(i);
				dist = ((Double)distancesVoisins.get(i)).doubleValue(); 
				noeudVoisin._distance = dist;
				noeudVoisin._arcPrecedent = arcVoisin;
				noeudVoisin._noeudPrecedent = this;
			}
			aTraiter.addAll(noeudsVoisins);

			// Phase "avant" 
			while (aTraiter.size() != 0 ) {

				// choisi le noeud � marquer comme trait� parmi les voisins
				plusProche = (Noeud)aTraiter.get(0);
				for (i=1; i<aTraiter.size(); i++) {
					if ( ((Noeud)aTraiter.get(i))._distance < plusProche._distance ) {
						plusProche = (Noeud)aTraiter.get(i);
					}
				}

				traites.add(plusProche);
				aTraiter.remove(plusProche);
				if ( plusProche == arrivee ) break; //il s'agit du noeud d'arriv�e
				if ( maxLongueur != 0 ) {
					if ( plusProche._distance > maxLongueur ) return null; // heuristique pour stopper la recherche 
				}

				plusProche.chercheArcsNoeudsVoisins(groupe, noeudsVoisins, distancesVoisins, arcsVoisins);
				for (i=0; i<noeudsVoisins.size(); i++) {
					noeudVoisin = (Noeud)noeudsVoisins.get(i);
					arcVoisin = (Arc)arcsVoisins.get(i);
					dist = ((Double)distancesVoisins.get(i)).doubleValue(); 
					if ( traites.contains(noeudVoisin) ) continue; // Noeud d�j� trait�
					if ( aTraiter.contains(noeudVoisin) ) { // Noeud d�j� atteint, on voit si on a trouv� un chemin plus court pour y acc�der
						if ( noeudVoisin._distance > (plusProche._distance+dist) ) {
							noeudVoisin._distance = plusProche._distance+dist;
							noeudVoisin._arcPrecedent = arcVoisin;
							noeudVoisin._noeudPrecedent = plusProche;
						}
						continue;
					}
					// Nouveau noeud atteint, on l'initialise
					noeudVoisin._distance = plusProche._distance+dist;
					noeudVoisin._arcPrecedent = arcVoisin;
					noeudVoisin._noeudPrecedent = plusProche;
					aTraiter.add(noeudVoisin);
				}
			}

			// Phase "arriere" 
			if ( ! traites.contains(arrivee) ) return null;
			suivant = arrivee;
			while (true) {
				arcsFinaux.add(0, suivant._arcPrecedent);
				suivant._arcPrecedent.addGroupe(plusCourtChemin);
				suivant = (Noeud)suivant._noeudPrecedent;
				if ( suivant == this ) break;
				noeudsFinaux.add(0, suivant);
				suivant.addGroupe(plusCourtChemin);
			}

			noeudsFinaux.add(0, this);
			this.addGroupe(plusCourtChemin);
			noeudsFinaux.add(arrivee);
			arrivee.addGroupe(plusCourtChemin);

			plusCourtChemin.setListeArcs(arcsFinaux);
			plusCourtChemin.setListeNoeuds(noeudsFinaux);
			return plusCourtChemin;
		} catch (Exception e) {
			System.out.println("----- ERREUR dans calcul de plus court chemin. ");
			e.printStackTrace();
			return null;
		}
	}

	// M�thode utile au plus court chemin
	private void chercheArcsNoeudsVoisins(Groupe groupe, List noeudsVoisins, 
			List distancesVoisins, List arcsVoisins) {
		List arcsEntrants = new ArrayList();   // au sens de la geometrie
		List arcsSortants = new ArrayList();   // au sens de la geometrie
		List arcsSortants2 = new ArrayList();   // au sens de la circulation
		List noeudsSortants2 = new ArrayList(); // au sens de la circulation
		List distancesSortants2 = new ArrayList(); // au sens de la circulation
		Noeud noeud;
		Arc arc;
		Double distance;
		int i, j;

		noeudsVoisins.clear();
		distancesVoisins.clear();
		arcsVoisins.clear();

		arcsEntrants = this.getEntrants();
		arcsSortants = this.getSortants();

		// transformation du sens g�om�trique au sens de circulation
		for (i=0; i<arcsEntrants.size(); i++) {
			arc = (Arc)arcsEntrants.get(i);
			if ( groupe.getListeArcs().contains(arc) ) {
				if ( ( arc.getOrientation() == -1 ) || ( arc.getOrientation() == 2 ) ) {
					if ( arc.getNoeudIni() != null ) {
						arcsSortants2.add(arc);
						noeudsSortants2.add((Noeud)arc.getNoeudIni());
						distancesSortants2.add(new Double(arc.getPoids()));
					}
				}
			}
		}
		for (i=0; i<arcsSortants.size(); i++) {
			arc = (Arc)arcsSortants.get(i);
			if ( groupe.getListeArcs().contains(arc) ) {
				if ( ( arc.getOrientation() == 1 ) || ( arc.getOrientation() == 2 ) ) {
					if ( arc.getNoeudFin() != null ) {
						arcsSortants2.add(arc);
						noeudsSortants2.add((Noeud)arc.getNoeudFin());
						distancesSortants2.add(new Double(arc.getPoids()));
					}
				}
			}
		}

		// en choisissant l'arc le plus court, si il existe des arcs parall�les (m�mes noeuds ini et fin)
		for (i=0; i<noeudsSortants2.size(); i++) {
			// choix du plus court arc menant au noeud sortant
			noeud = (Noeud)noeudsSortants2.get(i);
			if ( noeudsVoisins.contains(noeud) ) continue;
			arc = (Arc)arcsSortants2.get(i);
			distance = (Double)distancesSortants2.get(i); 
			for (j=i+1;j<noeudsSortants2.size(); j++) {
				if ( noeud == (Noeud)noeudsSortants2.get(j) ) {
					if ( ((Double)distancesSortants2.get(j)).doubleValue() < distance.doubleValue() ) {
						distance = (Double)distancesSortants2.get(j);
						arc = (Arc)arcsSortants2.get(j);
					}
				}
			}
			arcsVoisins.add(arc);
			noeudsVoisins.add(noeud);
			distancesVoisins.add(distance);
		}
	}


///////////////////////////////////////////////////////
//	DIVERS
///////////////////////////////////////////////////////

	/** Direction (Angle entre 0 et 2PI) de l'arc � la sortie du noeud this.
	 * Cette direction est calcul�e � partir d'une partie de l'arc d'une certaine 
	 * longueur (param�tre), et en r�-�chantillonant l'arc (param�tre).
	 * Si l'arc n'a pas pour noeud initial ou final this: renvoie null.
	 * 
	 * @param longueurEspaceTravail :
	 * Longueur curviligne qui d�termine l'espace de travail autour du noeud, 
	 * Si elle est �gale � 0: les deux premiers points de l'arc sont consid�r�s.
	 *
	 * @param pasEchantillonage :
	 * Avant le calcul de la direction moyenne des points, la ligne est r��chantillon�e � ce pas. 
	 * Si �gal � 0: aucun �chantillonage n'est effectu�
	 *   
	 */
	public Angle directionArc(Arc arc, double longueurEspaceTravail, double pasEchantillonage) {
		DirectPositionList listePts, arcEchantillone;
		int nbPts;
		if ( arc.getNoeudFin() == this ) {
			listePts = Operateurs.derniersPoints(arc.getGeometrie(),longueurEspaceTravail);
			if ( listePts.size() < 2 ) {
				nbPts = arc.getGeometrie().coord().size();
				listePts.add(arc.getGeometrie().coord().get(nbPts-2)); 
			}
		}
		else if ( arc.getNoeudIni() == this ) {
			listePts = Operateurs.premiersPoints(arc.getGeometrie(),longueurEspaceTravail);
			if ( listePts.size() < 2 ) {
				listePts.add(arc.getGeometrie().coord().get(1)); 
			}
		} 
		else return null;

		if (pasEchantillonage == 0) arcEchantillone = listePts;
		else  arcEchantillone = Operateurs.echantillonePasVariable(new GM_LineString(listePts),pasEchantillonage).coord();
		return Operateurs.directionPrincipaleOrientee(arcEchantillone);
	}

}