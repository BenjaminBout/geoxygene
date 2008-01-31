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
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.geometrie.Rectangle;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Classe des arcs de la carte topo.
 * Les arcs ont pour g�om�trie une GM_LineString, et peuvent �tre orient�s.
 * Des m�thodes sont pr�vues pour une gestion de r�seau, de graphe, et de carte topologique.
 * 
 * English: arcs of a topological map
 * @author  Musti�re/Bonin
 * @version 1.0
 */

public class Arc  extends ElementCarteTopo   {

    public Arc() {
    }
    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                                      G�om�trie
/////////////////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie le GM_LineString qui d�finit la g�om�trie de self */
    public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** D�finit le GM_LineString qui d�finit la g�om�trie de self */
    public void setGeometrie(GM_LineString geometrie) {this.setGeom(geometrie);}
	/** Renvoie la liste de DirectPosition qui d�finit les coordonn�es de self */
    public DirectPositionList getCoord() {return geom.coord();}
	/** D�finit la liste de DirectPosition qui d�finit les coordonn�es de self */
    public void setCoord(DirectPositionList dpl) {geom = new GM_LineString(dpl);}

    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                          Gestion de la topologie arc / noeuds  
/////////////////////////////////////////////////////////////////////////////////////////////////
    private Noeud noeudIni;
	/** Renvoie le noeud initial de self */
    public Noeud getNoeudIni() {return this.noeudIni;}
	/** D�finit le noeud initial de self. 
	 * NB: met � jour la relation inverse "sortants" de noeud
	 */
    public void setNoeudIni(Noeud noeud)  {
       if (noeud != null) {
            this.noeudIni = noeud;
            if (!noeud.getSortants().contains(this))
            	noeud.addSortant(this);
        } else {
			if (this.getNoeudIni() != null ) this.getNoeudIni().getSortants().remove(noeud); 
            noeudIni = null;
        }
    }
    
    private Noeud noeudFin;
	/** Renvoie le noeud final de self */
    public Noeud getNoeudFin() {return this.noeudFin;}
	/** D�finit le noeud final de self.
	 * NB: met � jour la relation inverse "entrants" de noeud
	 */
    public void setNoeudFin(Noeud noeud)  {
        if (noeud != null) {
            this.noeudFin = noeud;
            if (!noeud.getEntrants().contains(this))
            	noeud.addEntrant(this);
        } else {
			if (this.getNoeudFin() != null ) this.getNoeudFin().getEntrants().remove(noeud); 
            noeudFin = null;
        }
    }
    
    /** Renvoie le noeud initial et final de self */
    public List noeuds() {
        List noeuds = new ArrayList();
        if ( !(noeudIni == null) ) noeuds.add(noeudIni);
        if ( !(noeudFin == null) ) noeuds.add(noeudFin);
        return noeuds;
    }

    /** Projete le point P sur l'arc et d�coupe l'arc en 2 avec ce point projet�.
     * NB: si la projection tombe sur une extr�mit� de l'arc : ne fait rien.
     */
    public void projeteEtDecoupe(GM_Point P) {
		DirectPositionList listePoints = this.getGeometrie().coord();
		DirectPositionList ptsAvant, ptsApres;
		Arc arcAvant, arcApres;
		double d , dmin;
		DirectPosition pt, ptmin;
		int positionMin = 0;
		Noeud nouveauNoeud;
		
		if (this.getCarteTopo() == null) return;
		
		Population popNoeuds = this.getCarteTopo().getPopNoeuds();
		Population popArcs = this.getCarteTopo().getPopArcs();
		int i;
		
        if (listePoints.size() < 2 ) return;
		ptmin = Operateurs.projection(P.getPosition(),listePoints.get(0),listePoints.get(1));
		dmin = Distances.distance(P.getPosition(),ptmin);
		for (i=1;i<listePoints.size()-1;i++) {
			pt = Operateurs.projection(P.getPosition(),listePoints.get(i),listePoints.get(i+1));
			d = Distances.distance(P.getPosition(),pt);
			if ( d < dmin ) {
				ptmin = pt;
				dmin = d;
				positionMin = i;
			}
		}
		if (Distances.distance(ptmin, listePoints.get(0)) == 0) return;
		if (Distances.distance(ptmin, listePoints.get(listePoints.size()-1)) == 0) return;
		
		// cr�ation du nouveau noeud
		nouveauNoeud = (Noeud)popNoeuds.nouvelElement();
		nouveauNoeud.setGeometrie(new GM_Point(ptmin));

		// cr�ation des nouveaux arcs
		ptsAvant = new DirectPositionList();
		ptsApres = new DirectPositionList();

		for (i=0;i<=positionMin;i++) ptsAvant.add(listePoints.get(i));
		ptsAvant.add(ptmin);
		arcAvant = (Arc)popArcs.nouvelElement();
		arcAvant.setGeometrie(new GM_LineString(ptsAvant));
		
		if ( Distances.distance(ptmin, listePoints.get(positionMin+1)) != 0) ptsApres.add(ptmin);
		for (i=positionMin+1;i<listePoints.size();i++) ptsApres.add(listePoints.get(i));
		arcApres = (Arc)popArcs.nouvelElement();
		arcApres.setGeometrie(new GM_LineString(ptsApres));
		
		// instanciation de la topologie et des attributs
		this.getNoeudIni().getSortants().remove(this);
		arcAvant.setNoeudIni(this.getNoeudIni());
		arcAvant.setNoeudFin(nouveauNoeud);
		arcAvant.setCorrespondants(this.getCorrespondants());
		arcAvant.setOrientation(this.getOrientation());
		
		arcApres.setNoeudIni(nouveauNoeud);
		this.getNoeudFin().getEntrants().remove(this);
		arcApres.setNoeudFin(this.getNoeudFin());
		arcApres.setCorrespondants(this.getCorrespondants());
		arcApres.setOrientation(this.getOrientation());
		
		//destruction de l'ancien arc
		this.setNoeudIni(null);
		this.setNoeudFin(null);
		popArcs.enleveElement(this);
    }
    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                          Gestion de la topologie arc / faces
/////////////////////////////////////////////////////////////////////////////////////////////////
    /** Renvoie la face � gauche et � droite de self */
    public List faces() {
        List faces = new ArrayList();
        if(this.getFaceGauche()!=null) faces.add(this.getFaceGauche());
		if(this.getFaceDroite()!=null) faces.add(this.getFaceDroite());
        return faces;
    }

    private Face faceGauche;
	/** Renvoie la face � gauche de self */
    public Face getFaceGauche() {return this.faceGauche;}
	/** D�finit la face � gauche de self.
	 * NB: met � jour la relation inverse "arsc directs" de face
	 */
    public void setFaceGauche(Face face) {
        if (face != null) {
            this.faceGauche = face;
            if (!face.getArcsDirects().contains(this))
            	face.addArcDirect(this);
        } else {
			if (this.getFaceGauche() != null ) this.getFaceGauche().getArcsDirects().remove(face); 
            faceGauche = null;
        }            
    }
    
    private Face faceDroite;
	/** Renvoie la face � droite de self */
    public Face getFaceDroite() {return this.faceDroite;}
	/** D�finit la face � droite de self.
	 * NB: met � jour la relation inverse "arsc indirects" de face
	 */
    public void setFaceDroite(Face face)  {
        if (face != null) {
            this.faceDroite = face;
            if (!face.getArcsIndirects().contains(this))
                    face.addArcIndirect(this);
        } else {
			if (this.getFaceDroite() != null ) this.getFaceDroite().getArcsIndirects().remove(face); 
            faceDroite = null;
        }
    }
    
    
    /** Recherche du cycle du r�seau � droite de l'arc en se basant sur la topologie de RESEAU uniquement.
     *  Renvoie une liste (ArrayList) de 3 �l�ments : 
     *    - get(0): Liste des arcs dans l'ordre de parcours du cycle 
     *              Liste class�e dans le sens anti-trigonometrique (sauf pour la face exterieure).
     *              (liste de type "ArrayList", contenant elle-m�me des "Arc").
     *    - get(1): Liste des orientations des arc : true si l'arc � sa face � droite, false sinon
     *              (liste de type "ArrayList", contenant elle-m�me des objets "Boolean").
     *    - get(2) : La g�om�trie du polygone faisant le tour du cycle
     *               (de type "GM_Polygon)
     *  NB: la liste retourn�e est �gale � null si on n'a pas trouv� de cycle 
     *          (cas pouvant arriver si la topologie arcs/noeuds n'est pas compl�te.
     *  NB: ne n�cessite PAS d'avoir une topologie arcs/faces instanci�e. 
     *  NB: n�cessite d'avoir une topologie arcs/noeuds instanci�e. 
     *  NB: un cycle passe 2 fois (une fois dans chaque sens) par les cul-de-sac si il y en a.
     */
    public List cycleADroite() {
       Arc arcEnCours;
       boolean sensEnCours;
       List arcOriente ;
       GM_LineString contour = new GM_LineString();;
       List arcsDuCycle = new ArrayList();
       List orientationsDuCycle = new ArrayList();
       List resultat = new ArrayList();
       int i;

       // intialisation avec le premier arc du cycle (this) qui est par d�finition dans le bon sens 
       arcEnCours = this;
       sensEnCours = true;
       
       // on parcours le cycle dans le sens anti-trigonometrique, 
       // jusqu'� revenir sur this en le parcourant dans le bon sens 
       // (pr�cision utile � la gestion des cul-de-sac).
       while ( true ) {
           // ajout de l'arc en cours au cycle...
           arcsDuCycle.add(arcEnCours);
           orientationsDuCycle.add(new Boolean(sensEnCours));
           if (sensEnCours) {  // arc dans le bon sens
               for(i=0;i<arcEnCours.getGeometrie().sizeControlPoint()-1;i++) {
                    contour.addControlPoint(arcEnCours.getGeometrie().getControlPoint(i));
               }
               arcOriente = arcEnCours.arcSuivantFin();
           }
           else { // arc dans le sens inverse 
               for(i=arcEnCours.getGeometrie().sizeControlPoint()-1;i>0;i--) {
                    contour.addControlPoint(arcEnCours.getGeometrie().getControlPoint(i));
               }
               arcOriente = arcEnCours.arcSuivantDebut();
           }
           if ( arcOriente == null ) {return null;}

           // au suivant...
           arcEnCours = (Arc)arcOriente.get(0); //l'arc
           sensEnCours = !((Boolean)arcOriente.get(1)).booleanValue(); //le sens de l'arc par rapport au cycle

           //c'est fini ?
           if ( arcEnCours==this && sensEnCours ) break; 
       }
               
       // ajout du dernier point pour finir la boucle du polygone
       contour.addControlPoint(contour.startPoint());

       resultat.add(arcsDuCycle);
       resultat.add(orientationsDuCycle);
       resultat.add(new GM_Polygon(contour));
       return resultat;
    }
       

    /** Recherche du cycle du r�seau � gauche de l'arc en se basant sur la topologie de RESEAU uniquement.
     *  Renvoie une liste (ArrayList) de 3 �l�ments : 
     *    - get(0): Liste des arcs dans l'ordre de parcours du cycle. 
     *              Liste class�e dans le sens trigonometrique (sauf pour la face exterieure).
     *              (liste de type "ArrayList", contenant elle-m�me des "Arc").
     *    - get(1): Liste des orientations des arc : true si l'arc � sa face � gauche, false sinon
     *              (liste de type "ArrayList", contenant elle-m�me des objets "Boolean").
     *    - get(2) : La g�om�trie du polygone faisant le tour du cycle
     *               (de type "GM_Polygon)
     *  NB: la liste retourn�e est �gale � null si on n'a pas trouv� de cycle 
     *          (cas pouvant arriver si la topologie arcs/noeuds n'est pas compl�te.
     *  NB: ne n�cessite PAS d'avoir une topologie arcs/faces instanci�e. 
     *  NB: n�cessite d'avoir une topologie arcs/noeuds instanci�e. 
     *  NB: un cycle passe 2 fois (une fois dans chaque sens) par les cul-de-sac si il y en a.
     */
    public List cycleAGauche() {
       Arc arcEnCours;
       boolean sensEnCours;
       List arcOriente ;
       GM_LineString contour = new GM_LineString();;
       List arcsDuCycle = new ArrayList();
       List orientationsDuCycle = new ArrayList();
       List resultat = new ArrayList();
       int i;

       // intialisation avec le premier arc du cycle (this) qui est par d�finition dans le bon sens 
       arcEnCours = this;
       sensEnCours = true;
       
       // on parcours le cycle dans le sens anti-trigonometrique, 
       // jusqu'� revenir sur this en le parcourant dans le bon sens 
       // (pr�cision utile � la gestion des cul-de-sac).
       while ( true ) {
           // ajout de l'arc en cours au cycle...
           arcsDuCycle.add(arcEnCours);
           orientationsDuCycle.add(new Boolean(sensEnCours));
           if (sensEnCours) {  // arc dans le bon sens
               for(i=0;i<arcEnCours.getGeometrie().sizeControlPoint()-1;i++) {
                    contour.addControlPoint(arcEnCours.getGeometrie().getControlPoint(i));
               }
               arcOriente = arcEnCours.arcPrecedentFin();
           }
           else { // arc dans le sens inverse 
               for(i=arcEnCours.getGeometrie().sizeControlPoint()-1;i>0;i--) {
                    contour.addControlPoint(arcEnCours.getGeometrie().getControlPoint(i));
               }
               arcOriente = arcEnCours.arcPrecedentDebut();
           }
           if ( arcOriente == null ) {return null;}

           // au suivant...
           arcEnCours = (Arc)arcOriente.get(0); //l'arc
           sensEnCours = !((Boolean)arcOriente.get(1)).booleanValue(); //le sens de l'arc par rapport au cycle

           //c'est fini ?
           if ( arcEnCours==this && sensEnCours ) break; 
       }
               
       // ajout du dernier point pour finir la boucle du polygone
       contour.addControlPoint(contour.startPoint());

       resultat.add(arcsDuCycle);
       resultat.add(orientationsDuCycle);
       resultat.add(new GM_Polygon(contour));
       return resultat;
    }
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                      Gestion de type carte topopolgique 
/////////////////////////////////////////////////////////////////////////////////////////////////
// Les arcs sont class�s autour d'un noeud en fonction de leur g�om�trie. 
// Ceci permet en particulier de parcourir facilement les cycles d'un graphe.
/////////////////////////////////////////////////////////////////////////////////////////////////

    /** Arc suivant self � son noeud final, au sens des cartes topologiques.
     *  L'arc suivant est l'arc incident au noeud final de self, 
     *  et suivant self dans l'ordre trigonom�trique autour de ce noeud final.
     *
     *  NB: renvoie une liste de 2 �l�ments :
     *      element 1, liste.get(0) = l'arc
     *      element 2, liste.get(1) = Boolean, true si entrant, false si sortant
     *  NB: calcul r�alis� pour chaque appel de la m�thode.
     *  NB : l'arcSuivant peut �tre self, en cas de cul de sac sur le noeud final.
     */
    public List arcSuivantFin() {
        if ( this.getNoeudFin() == null ) return null;
        List arcs = (List)this.getNoeudFin().arcsClasses().get(0);
        List arcsOrientation = (List)this.getNoeudFin().arcsClasses().get(1);
        Iterator itArcs = arcs.iterator();
        Iterator itArcsOrientation = arcsOrientation.iterator();
        Boolean orientationEntrant;
        Arc arc;
        List resultat = new ArrayList();

        // On parcours la liste des arcs autour du noeud final
        // Quand on y rencontre this en tant qu'entrant, on renvoie le suivant dans la liste
        // NB: cette notion d'entrant est n�cesaire pour bien g�rer les boucles
        while( itArcs.hasNext() ) {
            arc = (Arc) itArcs.next();
            orientationEntrant = (Boolean)itArcsOrientation.next();
            if ( (arc == this) && orientationEntrant.booleanValue() ) {
                if ( itArcs.hasNext() ) {
                    resultat.add((Arc)itArcs.next());
                    resultat.add((Boolean)itArcsOrientation.next());
                }
                else {
                    resultat.add((Arc)arcs.get(0));
                    resultat.add((Boolean)arcsOrientation.get(0));
                }
                return resultat;
            }
        }
        return null;
    }

    /** Arc suivant self � son noeud initial, au sens des cartes topologiques.
     *  L'arc suivant est l'arc incident au noeud initial de self, 
     *  et suivant self dans l'ordre trigonom�trique autour de ce noeud initial.
     *
     *  NB: renvoie une liste de 2 �l�ments :
     *      element 1, liste.get(0) = l'arc
     *      element 2, liste.get(1) = Boolean, true si entrant, false si sortant
     *  NB: calcul r�alis� pour chaque appel de la m�thode.
     *  NB : l'arcSuivant peut �tre self, en cas de cul de sac sur le noeud initial.
     */
    public List arcSuivantDebut() {
        if ( this.getNoeudIni() == null ) return null;
        List arcs = (List)this.getNoeudIni().arcsClasses().get(0);
        List arcsOrientation = (List)this.getNoeudIni().arcsClasses().get(1);
        Iterator itArcs = arcs.iterator();
        Iterator itArcsOrientation = arcsOrientation.iterator();
        Boolean orientationEntrant;
        Arc arc;
        List resultat = new ArrayList();

        // On parcours la liste des arcs autour du noeud initial
        // Quand on y rencontre this en tant que sortant, on renvoie le suivant dans la liste
        // NB: cette notion de sortant est n�cesaire pour bien g�rer les boucles.
        while( itArcs.hasNext() ) {
            arc = (Arc) itArcs.next();
            orientationEntrant = (Boolean)itArcsOrientation.next();
            if ( (arc == this) && !orientationEntrant.booleanValue() ) {
                if ( itArcs.hasNext() ) {
                    resultat.add((Arc)itArcs.next());
                    resultat.add((Boolean)itArcsOrientation.next());
                }
                else {
                    resultat.add((Arc)arcs.get(0));
                    resultat.add((Boolean)arcsOrientation.get(0));
                }
                return resultat;
            }
        }
        return null;
    }
        
    /** Arc pr�c�dant self � son noeud final, au sens des cartes topologiques.
     *  L'arc pr�c�dent est l'arc incident au noeud final de self, 
     *  et pr�c�dant self dans l'ordre trigonom�trique autour de ce noeud final.
     *
     *  NB: renvoie une liste de 2 �l�ments :
     *      element 1, liste.get(0) = l'arc
     *      element 2, liste.get(1) = Boolean, true si entrant, false si sortant
     *  NB: calcul r�alis� pour chaque appel de la m�thode.
     *  NB : l'arc pr�c�dent peut �tre self, en cas de cul de sac sur le noeud final.
     */
    public List arcPrecedentFin() {
        if ( this.getNoeudFin() == null ) return null;
        List arcs = (List)this.getNoeudFin().arcsClasses().get(0);
        List arcsOrientation = (List)this.getNoeudFin().arcsClasses().get(1);
        Iterator itArcs = arcs.iterator();
        Iterator itArcsOrientation = arcsOrientation.iterator();
        Boolean orientationEntrant, orientationPrecedent;
        Arc arc, arcPrecedent;
        List resultat = new ArrayList();

        // On parcours la liste des arcs autour du noeud final
        // Quand on y rencontre this en tant qu'entrant, on renvoie le pr�c�dant dans la liste
        // NB: cette notion de pr�c�dant est n�cesaire pour bien g�rer les boucles.
        arc = (Arc) itArcs.next();
        orientationEntrant = (Boolean)itArcsOrientation.next();
        if ((arc == this) && orientationEntrant.booleanValue() ) {
            resultat.add((Arc)arcs.get(arcs.size()-1));
            resultat.add((Boolean)arcsOrientation.get(arcs.size()-1));
            return resultat;
        }
        while( itArcs.hasNext() ) {
            arcPrecedent = arc;
            orientationPrecedent = orientationEntrant;
            arc = (Arc) itArcs.next();
            orientationEntrant = (Boolean)itArcsOrientation.next();
            if ( (arc == this)  && orientationEntrant.booleanValue() ) {
                resultat.add(arcPrecedent);
                resultat.add(orientationPrecedent);
                return resultat;
            } 
        }
        return null;
    }

    /** Arc pr�c�dent self � son noeud initial, au sens des cartes topologiques.
     *  L'arc pr�c�dent est l'arc incident au noeud initial de self, 
     *  et pr�c�dent self dans l'ordre trigonom�trique autour de ce noeud initial.
     *
     *  NB: renvoie une liste de 2 �l�ments :
     *      element 1, liste.get(0) = l'arc
     *      element 2, liste.get(1) = Boolean, true si entrant, false si sortant
     *  NB: calcul r�alis� pour chaque appel de la m�thode.
     *  NB : l'arc pr�c�dent peut �tre self, en cas de cul de sac sur le noeud initial.
     */
    public List arcPrecedentDebut() {
        if ( this.getNoeudIni() == null ) return null;
        List arcs = (List)this.getNoeudIni().arcsClasses().get(0);
        List arcsOrientation = (List)this.getNoeudIni().arcsClasses().get(1);
        Iterator itArcs = arcs.iterator();
        Iterator itArcsOrientation = arcsOrientation.iterator();
        Boolean orientationEntrant, orientationPrecedent;
        Arc arc, arcPrecedent;
        List resultat = new ArrayList();

        // On parcours la liste des arcs autour du noeud initial
        // Quand on y rencontre this en tant que sortant, on renvoie le pr�c�dant dans la liste
        // NB: cette notion de pr�c�dant est n�cesaire pour bien g�rer les boucles.
        arc = (Arc) itArcs.next();
        orientationEntrant = (Boolean)itArcsOrientation.next();
        if ((arc == this) && !orientationEntrant.booleanValue() ) {
            resultat.add((Arc)arcs.get(arcs.size()-1));
            resultat.add((Boolean)arcsOrientation.get(arcs.size()-1));
            return resultat;
        }
        while( itArcs.hasNext() ) {
            arcPrecedent = arc;
            orientationPrecedent = orientationEntrant;
            arc = (Arc) itArcs.next();
            orientationEntrant = (Boolean)itArcsOrientation.next();
            if ( (arc == this)  && !orientationEntrant.booleanValue() ) {
                resultat.add(arcPrecedent);
                resultat.add(orientationPrecedent);
                return resultat;
            } 
        }
        return null;
    }
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                              Gestion d'un r�seau orient� 
/////////////////////////////////////////////////////////////////////////////////////////////////
// NB: ne pas confondre orientation d�finie par l'attribut "orientation" (trait� ici),
//      et l'orientation d�finie implicitement par le sens de stockage de la g�om�trie    
    
    private int orientation = 2;
        /** Renvoie l'orientation. L'orientation vaut 2 dans les deux sens, -1 en sens indirect et 1 en sens direct */
    public int getOrientation() {return this.orientation;}
        /** D�finit l'orientation. L'orientation vaut 2 dans les deux sens, -1 en sens indirect et 1 en sens direct */
    public void setOrientation(int orientation) {this.orientation = orientation;}

    
    /* Noeuds initiaux de l'arc, au sens de son orientation. 
     * Si l'arc est en double sens (orientation = 2), les deux noeuds extr�mit�s sont renvoy�s.
     * Sinon, un seul noeud est renvoy�.
     * NB: � ne pas confondre avec getNoeudIni, qui renvoie le noeud initial au sens du stockage
     */
    public List inisOrientes() {
        List noeuds = new ArrayList();
        if ( (orientation == 2 || orientation == 1) && !(noeudIni == null) ) noeuds.add(noeudIni);
        if ( (orientation == 2 || orientation == -1)  && !(noeudFin == null) ) noeuds.add(noeudFin);
        return noeuds;
    }
     
    /* Noeuds finaux de l'arc, au sens de son orientation. 
     * Si l'arc est en double sens (orientation = 2), les deux noeuds extr�mit�s sont renvoy�s.
     * Sinon, un seul noeud est renvoy�.
     * NB: � ne pas confondre avec getNoeudFin, qui renvoie le noeud final au sens du stockage
     */
    public List finsOrientes() {
        List noeuds = new ArrayList();
        if ( (orientation == 2 || orientation == -1) && !(noeudIni == null) ) noeuds.add(noeudIni);
        if ( (orientation == 2 || orientation == 1)  && !(noeudFin == null) ) noeuds.add(noeudFin);
        return noeuds;
    }

    
//	///////////////////////////////////////////////////////////////////////////////////////////////
//								  Gestion des poids pour les plus courts chemins
//	///////////////////////////////////////////////////////////////////////////////////////////////
	private double poids = 0;
		/** Renvoie le poids de l'arc, pour les calculs de plus court chemin */
	public double getPoids() {return this.poids;}
		/** D�finit le poids de l'arc, pour les calculs de plus court chemin */
	public void setPoids(double d) {this.poids = d;}
    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                              Gestion des groupes
/////////////////////////////////////////////////////////////////////////////////////////////////

    /* Groupes auquels self appartient */
    private Collection listeGroupes = new ArrayList();
	/** Renvoie la liste des groupes de self*/
    public Collection getListeGroupes() {return this.listeGroupes;}
	/** D�finit la liste des groupes de self*/
    public void setListegroupes(Collection liste) {this.listeGroupes = liste;}
	/** Ajoute un groupe � self*/ 
    public void addGroupe(Groupe groupe) {
    	if (groupe != null && !listeGroupes.contains(groupe)) {
            this.listeGroupes.add(groupe);	
            if (!groupe.getListeArcs().contains(this))
                    groupe.addArc(this);
    	}
    }
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                      Pour la gestion des requetes spatiales 
/////////////////////////////////////////////////////////////////////////////////////////////////
    private Rectangle rectangleEnglobant = null;
    
    /** Rectangle englobant de l'arc, orient� le long des axes des x,y.
     * NB: le rectangle est calcul� au premier appel de cette fonction.
     * Si l'arc est modifi�, la valeur n'est pas mise � jour : il faut le faire explicitement au besoin avec calculeRectangleEnglobant.
     */
    public Rectangle getRectangleEnglobant() {
        if (this.rectangleEnglobant == null) this.calculeRectangleEnglobant();
        return this.rectangleEnglobant;
    }
    /** Calcule le rectangle englobant x,y en fonction de la g�om�trie */
    public void calculeRectangleEnglobant() {
        this.rectangleEnglobant = Rectangle.rectangleEnglobant(this.getGeometrie());
    }

    protected boolean proche(Arc arc, double distance) {
        return arc.getRectangleEnglobant().intersecte(this.getRectangleEnglobant().dilate(distance));
    }
     
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////
//                      Op�rateurs de calculs sur les arcs 
/////////////////////////////////////////////////////////////////////////////////////////////////
    /** Distance euclidienne entre le noeud et self */
    public double distance (Noeud noeud) {
            return (Distances.distance(noeud.getCoord(),this.getGeometrie()));
    }

    /** Premi�re composante de la distance de Hausdorff de self vers l'arc. 	 
     * Elle est calculee comme le maximum des distances d'un point intermediaire
     * de self � l'arc. Cette approximation peut diff�rer sensiblement 
     * de la definition theorique. 
     * NB : d�fini en th�orie � 3D, mais non v�rifi� en profondeur */
    public double premiereComposanteHausdorff (Arc arc) {
            return (Distances.premiereComposanteHausdorff(this.getGeometrie(), arc.getGeometrie()));
    }

    /** Distance de Hausdorff entre self et l'arc. 
     * Elle est calculee comme le maximum des distances d'un point intermediaire
     * d'une des lignes a l'autre ligne. Dans certains cas cette definition
     * differe de la definition theorique car la distance de Hausdorff ne se
     * realise pas necessairement sur un point intermediaire. Mais cela est rare
     * sur des donn�es r�el. Cette implementation est un bon compromis entre
     * simplicit� et pr�cision.
     * NB : d�fini en th�orie � 3D, mais non v�rifi� en profondeur */
    public double hausdorff (Arc arc) {
            return (Distances.hausdorff(this.getGeometrie(), arc.getGeometrie()));
    }

    /** Longueur euclidienne de l'arc. Est calcul� en 3D si la g�om�trie est d�finie en 3D */
    public double longueur() {
            return this.getGeometrie().length();
    }
    
}
