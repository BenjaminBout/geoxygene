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

import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.feature.Population;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;


/**
 * Classe des groupes de la carte topo.
 * Un groupe est une composition de noeuds, d'arcs et de faces.
 * 
 * English: a group is a set of nodes/arcs/faces of a topological map
 * @author  Musti�re/Bonin
 * @version 1.0
 */

public class Groupe  extends ElementCarteTopo   {

    public Groupe() {}

   
    ///////////////////////////////////////////////////
    // Pour les relations de composition : 
    // - un groupe contient PLUSIEURS noeuds, arcs et faces
    // - un groupe appartient � UNE carte topo
    ///////////////////////////////////////////////////
    
    /* Noeuds composants du groupe */
    private List listeNoeuds = new ArrayList();
	/** Renvoie la liste des noeuds de self*/
    public List getListeNoeuds() {return this.listeNoeuds;}
	/** D�finit la liste des noeuds de self*/
    public void setListeNoeuds(List liste) {this.listeNoeuds = liste;}
	/** Ajoute un noeud � self*/ 
    public void addNoeud(Noeud noeud) {
    	if (noeud != null && !listeNoeuds.contains(noeud)) {
            this.listeNoeuds.add(noeud);	
            if (!noeud.getListeGroupes().contains(this))
                    noeud.addGroupe(this);
    	}
    }
    /** Ajoute une liste de noeuds � self**/
	public void addAllNoeuds(List liste) {
		Iterator itObj = liste.iterator();
		while (itObj.hasNext()) {
			Noeud objet = (Noeud) itObj.next();
			this.addNoeud(objet);
		}
	}
    
    /* Arcs composants du groupe */
    private List listeArcs = new ArrayList();
	/** Renvoie la liste des arcs de self*/
    public List getListeArcs() {return this.listeArcs;}
	/** D�finit la liste des arcs de self*/
    public void setListeArcs(List liste) {this.listeArcs = liste;}
	/** Ajoute un arc de self*/
    public void addArc(Arc arc) {
        if (arc != null && !listeArcs.contains(arc)) {
            this.listeArcs.add(arc); 	
            if (!arc.getListeGroupes().contains(this))
                    arc.addGroupe(this);
    	}
    }
    /** Ajoute une liste d'arcs � self**/
	public void addAllArcs(List liste) {
		Iterator itObj = liste.iterator();
		while (itObj.hasNext()) {
			Arc objet = (Arc) itObj.next();
			this.addArc(objet);
		}
	}

    /* Faces composants du groupe */
    private List listeFaces = new ArrayList();
	/** Renvoie la liste des faces de self*/
    public List getListeFaces() {return this.listeFaces;}
	/** D�finit la liste des faces de self*/
    public void setListeFaces(List liste) {this.listeFaces = liste;}
	/** Ajoute une face � self*/
    public void addFace(Face face) {
        if (face != null && !listeFaces.contains(face)) {
            this.listeFaces.add(face);	
            if (!face.getListeGroupes().contains(this))
                    face.addGroupe(this);
        }
    }
    /** Ajoute une liste de faces � self**/
	public void addAllFaces(List liste) {
		Iterator itObj = liste.iterator();
		while (itObj.hasNext()) {
			Face objet = (Face) itObj.next();
			this.addFace(objet);
		}
	}
    
    ///////////////////////////////////////////////////
    // Pour les relations topologiques dans une vision Groupe = Hyper Noeud
    ///////////////////////////////////////////////////

    /** Arcs entrants dans le groupe, au sens de la g�om�trie (vision groupe = hyper-noeud) */
    public List getEntrants() {
        List arcs = new ArrayList();
        List arcsDuNoeud = new ArrayList();
        int i, j;
        
        for (i=0; i<this.getListeNoeuds().size();i++) {
            arcsDuNoeud = ((Noeud)this.getListeNoeuds().get(i)).getEntrants();
            for (j=0; j<arcsDuNoeud.size(); j++) {
                if (!this.getListeArcs().contains(arcsDuNoeud.get(j))) arcs.add(arcsDuNoeud.get(j));
            }
        }
        return arcs;
    }

    /** Arcs sortants du groupe, au sens de la g�om�trie (vision groupe = hyper-noeud) */
    public List getSortants() {
        List arcs = new ArrayList();
        List arcsDuNoeud = new ArrayList();
        int i, j;
        
        for (i=0; i<this.getListeNoeuds().size();i++) {
            arcsDuNoeud = ((Noeud)this.getListeNoeuds().get(i)).getSortants();
            for (j=0; j<arcsDuNoeud.size(); j++) {
                if (!this.getListeArcs().contains(arcsDuNoeud.get(j))) arcs.add(arcsDuNoeud.get(j));
            }
        }
        return arcs;
    }
    
    /** Arcs adjacents (entrants et sortants) de self (vision groupe = hyper-noeud). 
     * NB : si un arc est � la fois entrant et sortant (boucle), il est 2 fois dans la liste
     */    
    public List getAdjacents() {
        List arcs = new ArrayList();
        arcs.addAll(this.getSortants());
        arcs.addAll(this.getEntrants());
        return arcs;        
    }

    ///////////////////////////////////////////////////
    // Pour les relations topologiques dans une vision Groupe = Hyper Noeud,
    // en tenant compte du sens de circulation
    ///////////////////////////////////////////////////
    /** Arcs entrants dans le groupe, au sens de la g�om�trie (vision groupe = hyper-noeud) */
    public List entrantsOrientes() {
        List arcs = new ArrayList();
        List arcsDuNoeud = new ArrayList();
        int i, j;
        
        for (i=0; i<this.getListeNoeuds().size();i++) {
            arcsDuNoeud = ((Noeud)this.getListeNoeuds().get(i)).entrantsOrientes();
            for (j=0; j<arcsDuNoeud.size(); j++) {
                if (!this.getListeArcs().contains(arcsDuNoeud.get(j))) arcs.add(arcsDuNoeud.get(j));
            }
        }
        return arcs;
    }

    /** Arcs sortants du groupe, au sens de la g�om�trie (vision groupe = hyper-noeud) */
    public List sortantsOrientes() {
        List arcs = new ArrayList();
        List arcsDuNoeud = new ArrayList();
        int i, j;
        
        for (i=0; i<this.getListeNoeuds().size();i++) {
            arcsDuNoeud = ((Noeud)this.getListeNoeuds().get(i)).sortantsOrientes();
            for (j=0; j<arcsDuNoeud.size(); j++) {
                if (!this.getListeArcs().contains(arcsDuNoeud.get(j))) arcs.add(arcsDuNoeud.get(j));
            }
        }
        return arcs;
    }
    
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
    
    ///////////////////////////////////////////////////
    // M�thodes de base pour manipuler un groupe
    ///////////////////////////////////////////////////
    /** Pour vider un groupe, et mettre � jour les liens des objets simples vers ce groupe.
     * Vide mais ne d�truit pas le groupe: i.e. ne l'enl�ve pas de la carte topo.
     */
    public void vide() {
		Iterator itArcs = this.getListeArcs().iterator();
		while (itArcs.hasNext()) {
			Arc arc = (Arc) itArcs.next();
			arc.getListeGroupes().remove(this);
		}  
		Iterator itNoeuds = this.getListeNoeuds().iterator();
		while (itNoeuds.hasNext()) {
			Noeud noeud= (Noeud) itNoeuds.next();
			noeud.getListeGroupes().remove(this);
		}  
        this.getListeArcs().clear();
        this.getListeNoeuds().clear();
    }

	/** Pour vider un groupe, mettre � jour les liens des objets simples vers ce groupe,
	 *  et l'enlever des populations auxquelles il appartient.
	 * NB: ce groupe n'est pas vraiment detruit, il n'est pas rendu null ;
	 * NB: rien n'est g�r� au niveau de la persistance eventuelle.
	 */
	public void videEtDetache() {
		vide();
		Population groupes = this.getPopulation();
		if (groupes != null) groupes.remove(this);
	}

	/** Pour copier un groupe.
	 * NB 1 : on cr�e un nouveau groupe pointant 
	 * vers les m�mes objets composants.
	 * NB 2 : ce groupe n'est PAS ajout� � la carteTopo
	 */
	public Groupe copie() {
		//Groupe copie = new Groupe();
		Groupe  copie = (Groupe)this.getPopulation().nouvelElement();
		copie.addAllArcs(this.getListeArcs());
		copie.addAllNoeuds(this.getListeNoeuds());
		copie.addAllFaces(this.getListeFaces());
		//copie.setPopulation(this.getPopulation());
		return copie;
	}
    
    ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////
    // Op�rateurs de calculs sur les groupes
    ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////

    /** Decompose un groupe en plusieurs groupes connexes, et vide le groupe self. 
      * La liste en sortie contient des Groupes.
      * ATTENTION : LE GROUPE EN ENTREE EST VIDE AU COURS DE LA METHODE PUIS ENLEVE DE LA CARTE TOPO. 
      */
    public List decomposeConnexes() {
        List groupesConnexes = new ArrayList();
        Groupe groupeConnexe;
        Noeud amorce; 
        Arc arc;
        int i;
        
        try {
            if ( this.getPopulation() == null ) { 
                System.out.println("ATTENTION : le groupe "+this+" n'a pas de population associ�e");
                System.out.println("            Impossible de le d�composer en groupes connexes");
                return null;
            }
            if ( this.getCarteTopo() == null ) { 
                System.out.println("ATTENTION : le groupe "+this+" ne fait pas partie d'une carte topo");
                System.out.println("            Impossible de le d�composer en groupes connexes");
                return null;
            }
            if ( this.getCarteTopo().getPopArcs() == null ) { 
                System.out.println("ATTENTION : le groupe "+this+" fait partie d'une carte topo sans population d'arcs");
                System.out.println("            Impossible de le d�composer en groupes connexes");
                return null;
            }
            if ( this.getCarteTopo().getPopNoeuds() == null ) { 
                System.out.println("ATTENTION : le groupe "+this+" fait partie d'une carte topo sans population de noeuds");
                System.out.println("            Impossible de le d�composer en groupes connexes");
                return null;
            }
            
            while (this.getListeNoeuds().size() != 0) {
                groupeConnexe = (Groupe)this.getPopulation().nouvelElement();
                groupesConnexes.add(groupeConnexe);
                // le premier noeud de la liste des noeuds, vid�e au fur et � mesure, est l'amorce d'un nouveau groupe connexe
                amorce = (Noeud)this.getListeNoeuds().get(0); 
                groupeConnexe.ajouteVoisins(amorce, this);  //nb: m�thode r�cursive
                groupeConnexe.arcsDansGroupe(this); // recherche des arcs du groupe, situ�s entre 2 noeuds du goupe connexe
            }
            // vidage des arcs du groupe, pour faire propre (on a d�j� vid� les noeuds au fur et � mesure)
            for (i=0; i<this.getListeArcs().size(); i++) {
                arc = (Arc)this.getListeArcs().get(i);
                arc.getListeGroupes().remove(this);
            }
            this.getListeArcs().clear();
            this.getPopulation().enleveElement(this);

            return groupesConnexes;
        } catch (Exception e) {System.out.println("----- ERREUR dans d�composition en groupes connxes: ");
                               System.out.println("Source possible : Nom de la classe des groupes pas ou mal renseign� dans la carte topo");
                               return null;}
    }

        // Methode n�cessaire � DecomposeConnexe 
        // ajoute le noeud au groupe connexe, cherche ses voisins, puis l'enl�ve du goupe total
        private void ajouteVoisins(Noeud noeud, Groupe groupeTotal) {
            List noeudsVoisins = new ArrayList();
            int i;

            if ( this.getListeNoeuds().contains(noeud) ) return;
            this.addNoeud(noeud);
            noeud.addGroupe(this);
            noeudsVoisins = noeud.voisins(groupeTotal);
            groupeTotal.getListeNoeuds().remove(noeud);
            noeud.getListeGroupes().remove(groupeTotal);
            for (i=0; i<noeudsVoisins.size(); i++) {
                this.ajouteVoisins((Noeud)noeudsVoisins.get(i), groupeTotal);
            }
            return;
        }

        // Methode n�cessaire � DecomposeConnexe 
        // Recherche les arcs de groupeTotal ayant pour extr�mit� des noeuds de this.
        private void arcsDansGroupe(Groupe groupeTotal) {
            int i;
            Arc arc;
            for (i=0; i<groupeTotal.getListeArcs().size(); i++) {
                arc = (Arc)groupeTotal.getListeArcs().get(i);
                if ( this.getListeNoeuds().contains(arc.getNoeudIni()) || this.getListeNoeuds().contains(arc.getNoeudIni()) )  {
                        this.addArc(arc);
                        arc.addGroupe(this);
                }
            }
        }

	/** somme des longueurs des arcs du groupe. */
	public double longueur() {
		int i;
		double longueur = 0;
		for(i=0;i<this.getListeArcs().size() ;i++) {
			longueur = longueur + ((Arc)this.getListeArcs().get(i)).longueur();
		}
		return longueur;
	}
    

	/** Teste si le groupe contient exactement les m�mes arcs qu'un autre groupe.
	 *  NB: si des arcs sont en double dans un des groupes et pas dans l'autre, renvoie true quand m�me
	 */
	public boolean contientMemesArcs(Groupe groupe) {
		if (!groupe.getListeArcs().containsAll(this.getListeArcs()) ) return false;
		if (!this.getListeArcs().containsAll(groupe.getListeArcs()) ) return false;
		return true;
	}
    
	/** Pour un groupe dont on ne connait que les arcs : 
	 * ajoute les noeuds ini et fin de ses arcs dans le groupe.
	 * La topologie doit avoir �t� instanci�e.
	 */
	public void ajouteNoeuds() {
		int i;
		Noeud ini, fin;
		Arc arc;
        
		for (i=0; i<this.getListeArcs().size(); i++) {
			arc = (Arc)this.getListeArcs().get(i);
			ini = arc.getNoeudIni();
			fin = arc.getNoeudFin();
			if ( ini != null ) {
				if ( !this.getListeNoeuds().contains(ini) ) {
					this.addNoeud(ini);
					ini.addGroupe(this);
				}
			}
			if ( fin != null ) {
				if ( !this.getListeNoeuds().contains(fin) ) {
					this.addNoeud(fin);
					fin.addGroupe(this);
				}
			}
		}
	}	

}