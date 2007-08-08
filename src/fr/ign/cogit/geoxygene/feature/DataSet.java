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

package fr.ign.cogit.geoxygene.feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;


/** Classe m�re pour tout jeu de donn�es.
 *  Un DataSet peut par exemple correspondre � une zone d'une BD, ou seulement un th�me.
 *  Un DataSet est constitu� de mani�re r�cursive d'un ensemble de jeux de donn�es, 
 *  et d'un ensemble de populations, elles m�mes constitu�es d'un ensemble d'�l�ments.
 * 
 * @author S�bastien Musti�re
 * @version 1.1
 *  
 * 9.02.2006 : extension de la m�thode chargeExtractionThematiqueEtSpatiale (grosso)
 *  
 */

 public class DataSet  {

 	
	protected int id;
	/** Renvoie l'identifiant */
	public int getId() {return id;}
	/** Affecte un identifiant. */
	public void setId (int Id) {id = Id;}
	
    /** Param�tre statique de connexion � la BD */
    public static Geodatabase db;



///////////////////////////////////////////////////////
//      Constructeurs / Chargement / persistance     
///////////////////////////////////////////////////////

    /** Constructeur par d�faut. */
    public DataSet() {this.ojbConcreteClass = this.getClass().getName();}

    /** Constructeur par d�faut, recopiant les champs de m�tadonn�es du DataSet en param�tre sur le nouveau */
    public DataSet(DataSet DS) {
        this.ojbConcreteClass = this.getClass().getName();
        if (DS == null) return;
        this.setNom(DS.getNom());
        this.setTypeBD(DS.getTypeBD());
        this.setModele(DS.getModele());
        this.setZone(DS.getZone());
        this.setDate(DS.getDate());
        this.setCommentaire(DS.getCommentaire());
    }

    /** Chargement des instances des populations persistantes d'un jeu de donn�es. */
    public void chargeElements() {
        if (!this.getPersistant()) {
            System.out.println("----- ATTENTION : Probleme au chargement du jeu de donnees "+this.getNom()); 
            System.out.println("----- Impossible de charger les elements d'un jeu de donnees non persistant"); 
            return;
        }

        // chargement recursif des dataset composants this
        Iterator itDS = this.getComposants().iterator();
        while (itDS.hasNext() ) {
            DataSet DS = (DataSet)itDS.next();
            if ( DS.getPersistant() ) DS.chargeElements() ;
        }

       
        // chargement recursif des populations de this
        System.out.println("");
        System.out.println("###### Chargement des elements du DataSet "+this.getNom());
        Iterator itPop = this.getPopulations().iterator();
        while ( itPop.hasNext() ) {
            Population pop = (Population)itPop.next();
            if ( pop.getPersistant() ) pop.chargeElements();
        }
    }

    /** Chargement des instances des populations persistantes d'un jeu de donn�es qui
     *  intersectent une g�om�trie donn�e (extraction g�om�trique). */
    public void chargeElementsPartie(GM_Object geom) {
        if (!this.getPersistant()) {
            System.out.println("----- ATTENTION : Probleme au chargement du jeu de donnees "+this.getNom()); 
            System.out.println("----- Impossible de charger les elements d'un jeu de donnees non persistant"); 
            return;
        }
        // chargement recursif des dataset composants this
        Iterator itDS = this.getComposants().iterator();
        while (itDS.hasNext() ) {
            DataSet DS = (DataSet)itDS.next();
            if ( DS.getPersistant() ) DS.chargeElementsPartie(geom) ;
        }
        
        // chargement recursif des populations de this
        System.out.println("");
        System.out.println("###### Chargement des elements du DataSet "+this.getNom());
        Iterator itPop = this.getPopulations().iterator();
        while ( itPop.hasNext() ) {
            Population pop = (Population)itPop.next();
            if ( pop.getPersistant() ) pop.chargeElementsPartie(geom);
        }
    }
    
	/** Chargement des instances des populations persistantes d'un jeu de donn�es qui
	 *  intersectent une g�om�trie donn�e. 
	 *  ATTENTION: les tables qui stockent les �l�ments doivent avoir �t� index�es dans Oracle.
	 *  ATTENTION AGAIN: seules les populations avec une g�om�trie sont charg�es.
	 */
	public void chargeElementsPartie(Extraction zoneExtraction) {
		chargeElementsPartie(zoneExtraction.getGeom());
	} 
    
	/**M�thode de chargement pour les test. Elle est un peu tordue
	 * dans le param�trage mais permet de ne charger que ce qu'on veut.
	 * Elle permet de charger les instances des populations persistantes 
	 * d'un jeu de donn�es qui :
	 * - intersectent une g�om�trie donn�e (extraction g�om�trique),
	 * - ET qui appartiennent � certains th�mes et populations pr�cis�s en entr�e.
	 *
	 * @param geom : D�finit la zone d'extraction.
	 * 
	 * @param themes : D�finit les sous-DS du DS � charger. Pour le DS lui-m�me,
	 * et pour chaque sous-DS, on pr�cise �galement quelles populations 
	 * sont charg�es. Ce param�tre est une liste de liste de String 
	 * compos�e comme suit (si la liste est nulle on charge tout) :
	 * 1/ Le premier �l�ment est soit null (on charge alors toutes les populations 
	 * directement sous le DS), soit une liste contenant les noms des populations
	 * directement sous le DS que l'on charge (si la liste est vide, on ne charge rien).
	 * 
	 * 2/ Tous les autres �l�ments sont des listes (une pour chaque sous-DS) qui 
	 * contiennent chacune d'abord le nom d'un sous-DS que l'on veut charger,
	 * puis soit rien d'autre si on charge toutes les populations du sous-DS,
	 * soit le nom des populations du sous-DS que l'on veut charger.
	 * 
	 * NB: Attention aux majuscules et aux accents.
	 * 
	 * EXEMPLE de parametre themes pour un DS repr�entant la BDCarto, et
	 * sp�cifiant qu'on ne veut charger que les troncon et les noeud du th�me
	 * routier, et les troncons du th�me hydro, mais tout le th�me ferr�.
	 * theme = {null, liste1, liste2, liste3}, avec :  
	 * - null car il n'y a pas de population directement sous le DS BDCarto,
	 * - liste1 = {"Routier", "Tron�ons de route", "Noeuds routier"},
	 * - liste2 = {"Hydrographie", "Tron�ons de cours d'eau"},
	 * - liste3 = {"Ferr�"}.
	 *  
	 */
	public void chargeExtractionThematiqueEtSpatiale(GM_Object geom, List themes) {
		if (!this.getPersistant()) {
			System.out.println("----- ATTENTION : Probleme au chargement du jeu de donnees "+this.getNom()); 
			System.out.println("----- Impossible de charger les elements d'un jeu de donnees non persistant"); 
			return;
		}
		
		List populationsACharger, themeACharger, extraitThemes ;
		Iterator itThemes, itPopulationsACharger;
		String nom;
		boolean aCharger;
		

		// chargement recursif des dataset composants this
		Iterator itDS = this.getComposants().iterator();
		while (itDS.hasNext() ) {
			DataSet DS = (DataSet)itDS.next();
			populationsACharger = null;
			if (themes == null) aCharger = true;
			else {
				itThemes = themes.iterator();
				themeACharger = (List)itThemes.next();
				if (!itThemes.hasNext() ) aCharger = true;
				else {
					aCharger = false;
					while (itThemes.hasNext()) {
						themeACharger = (List)itThemes.next();
						if ( DS.getNom().equals(themeACharger.get(0)) ) {
							aCharger = true;
							if (themeACharger.size() == 1) {
								populationsACharger = null;
								break;
							} 
							extraitThemes = new ArrayList(themeACharger);
							extraitThemes.remove(0);
							populationsACharger = new ArrayList();
							populationsACharger.add(extraitThemes);
							break;
						} 
					}
				}
			}
			if ( aCharger && DS.getPersistant() ) DS.chargeExtractionThematiqueEtSpatiale(geom,populationsACharger) ;
		}
        
        
		// chargement des populations de this (directement sous this)
		if (themes == null) populationsACharger = null;
		else {
			itThemes = themes.iterator(); 
			populationsACharger = (List)itThemes.next();
		}
		System.out.println("");
		System.out.println("###### Chargement des elements du DataSet "+this.getNom());
		Iterator itPop = this.getPopulations().iterator();
		while ( itPop.hasNext() ) {
			Population pop = (Population)itPop.next();
			if ( populationsACharger == null ) aCharger = true;
			else {
				aCharger = false;
				itPopulationsACharger = populationsACharger.iterator();
				while(itPopulationsACharger.hasNext()){
					nom = (String)itPopulationsACharger.next();
					if (pop.getNom().equals(nom)) {
						aCharger = true;
						break;
					}
				}
			}
			if ( aCharger && pop.getPersistant() ){
				if (geom!=null)pop.chargeElementsPartie(geom);
				else pop.chargeElements();
			}
		}

	}
    
    /** Pour un jeu de donn�es persistant, d�truit le jeu de donn�es, ses th�mes et ses objets populations -
     * ATTENTION : ne d�truit pas les �l�ments des populations (pour cela vider les tables Oracle)
     */
    public void detruitJeu() {
        if (!this.getPersistant()) {
            System.out.println("----- ATTENTION : Probleme � la destruction du jeu de donnees "+this.getNom()); 
            System.out.println("----- Le jeu de donn�es n'est pas persistant"); 
            return;
        }
        // destruction des populations de this
        Iterator itPop = this.getPopulations().iterator();
        while ( itPop.hasNext() ) {
            Population pop = (Population)itPop.next();
            if ( pop.getPersistant() ) pop.detruitPopulation();
        }

        // destruction recursive des dataset composants this
        System.out.println(" ");
        Iterator itDS = this.getComposants().iterator();
        while (itDS.hasNext() ) {
            DataSet DS = (DataSet)itDS.next();
            if ( DS.getPersistant() ) DS.detruitJeu() ;
        }
        
		// destruction des zones d'extraction associ�es � this
		System.out.println(" ");
		Iterator itExt = this.getExtractions().iterator();
		while (itExt.hasNext() ) {
			Extraction ex = (Extraction)itExt.next();
			System.out.println("###### Destruction de la zone d'extraction "+ex.getNom());
			db.deletePersistent(ex);
		}

		//destruction de this
        System.out.println("###### Destruction du DataSet "+this.getNom());
        db.deletePersistent(this);
    }
    
    /** Bool�en sp�cifiant si le th�me est persistant ou non (vrai par d�faut).  
     *  NB : si un jeu de donn�es est non persistant, tous ses th�mes sont non persistants.
     *  Mais si un jeu de donn�es est persistant, certains de ses th�mes peuvent ne pas l'�tre.
     * 
	 * ATTENTION: pour des raisons propres � OJB, m�me si la classe DataSet est concr�te,
 	 * il n'est pas possible de cr�er un objet PERSISTANT de cette classe, 
 	 * il faut utiliser les sous-classes.
     */
    // NB pour codeurs : laisser 'true' par d�faut. Sinon, comme cet attribut n'est pas persistant, 
    // cela pose des probl�mes au chargement (un th�me persistant charg� a son attribut persistant � false.
    protected boolean persistant = true;
    public boolean getPersistant() {return persistant;}
    public void setPersistant(boolean b) {persistant = b;}
    
///////////////////////////////////////////////////////
//          Metadonn�es     
///////////////////////////////////////////////////////
     /** Nom de la classe concr�te de this : pour OJB, ne pas manipuler directement */
     protected String ojbConcreteClass;
     public String getOjbConcreteClass() {return ojbConcreteClass;}
     public void setOjbConcreteClass(String S) {ojbConcreteClass = S;}
     
     /** Nom du jeu de donn�es */
     protected String nom;
     public String getNom() {return nom; }
     public void setNom (String S) {nom = S; }

     /** Type de BD (BDcarto, BDTopo...). */
     protected String typeBD;
     public String getTypeBD() {return typeBD; }
     public void setTypeBD (String S) {typeBD = S; }
     
     /** Mod�le utilis� (format shape, structur�...). */
     protected String modele;
     public String getModele() {return modele; }
     public void setModele(String S) {modele = S; }

     /** Zone g�ographique couverte. */
     protected String zone;
     public String getZone() {return zone; }
     public void setZone(String S) {zone = S; }

     /** Date des donn�es. */
     protected String date;
     public String getDate() {return date; }
     public void setDate(String S) {date = S; }

     /** Commentaire quelconque. */
     protected String commentaire;
     public String getCommentaire() {return commentaire; }
     public void setCommentaire(String S) {commentaire = S; }

     
///////////////////////////////////////////////////////
//          Th�mes du jeu de donn�es
///////////////////////////////////////////////////////
    /** Un DataSet se d�compose r�cursivement en un ensemble de DataSet. 
     *  Le lien de DataSet vers lui-m�me est un lien 1-n.
     *  Les m�thodes get (sans indice) et set sont n�cessaires au mapping. 
     *  Les autres m�thodes sont l� seulement pour faciliter l'utilisation de la relation.
     *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes. 
     *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
     *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
     */
    protected List composants = new ArrayList();   

    /** R�cup�re la liste des DataSet composant this. */
    public List getComposants() {return composants ; } 
    /** D�finit la liste des DataSet composant le DataSet, et met � jour la relation inverse. */
    public void setComposants(List L) {
        List old = new ArrayList(composants);
        Iterator it1 = old.iterator();
        while ( it1.hasNext() ) {
            DataSet O = (DataSet)it1.next();
            O.setAppartientA(null);
        }
        Iterator it2 = L.iterator();
        while ( it2.hasNext() ) {
            DataSet O = (DataSet)it2.next();
            O.setAppartientA(this);
        }
    }
    /** R�cup�re le i�me �l�ment de la liste des DataSet composant this. */
    public DataSet getComposant(int i) {return (DataSet)composants.get(i) ; }  
    /** Ajoute un objet � la liste des DataSet composant le DataSet, et met � jour la relation inverse. */
    public void addComposant(DataSet O) {
        if ( O == null ) return;
        composants.add(O) ;
        O.setAppartientA(this) ;
    }
    /** Enl�ve un �l�ment de la liste DataSet composant this, et met � jour la relation inverse. */
    public void removeComposant(DataSet O) {
        if ( O == null ) return;
        composants.remove(O) ; 
        O.setAppartientA(null);
    }
    /** Vide la liste des DataSet composant this, et met � jour la relation inverse. */
    public void emptyComposants() {
        List old = new ArrayList(composants);
        Iterator it = old.iterator(); 
        while ( it.hasNext() ) {
            DataSet O = (DataSet)it.next();
            O.setAppartientA(null);
        }
    }
    /** Recup�re le DataSet composant de this avec le nom donn�. */
    public DataSet getComposant(String nom) {
        DataSet th;
        Iterator it = this.getComposants().iterator();
        while ( it.hasNext() ) {
            th = (DataSet)it.next();
            if ( th.getNom().equals(nom) ) return th;
        }
        System.out.println("----- ATTENTION : DataSet composant #"+nom+"# introuvable dans le DataSet "+this.getNom());
        return null;
    }

    
    /** Relation inverse � Composants */
    private DataSet appartientA;
    /** R�cup�re le DataSet dont this est composant. */
    public DataSet getAppartientA() {return appartientA;  }
    /** D�finit le DataSet dont this est composant., et met � jour la relation inverse. */
    public void setAppartientA(DataSet O) {
        DataSet old = appartientA;
        appartientA = O;  
        if ( old  != null ) old.getComposants().remove(this);
        if ( O != null ) {
            appartientAID = O.getId();
            if ( !(O.getComposants().contains(this)) ) O.getComposants().add(this);            
        } else appartientAID = 0;
    }
    private int appartientAID;
    /** Ne pas utiliser, necessaire au mapping OJB */
    public void setAppartientAID(int I) {appartientAID = I;}
    /** Ne pas utiliser, necessaire au mapping OJB */
    public int getAppartientAID() {return appartientAID;}
    

    /** Liste des population du DataSet. 
     *  Les m�thodes get (sans indice) et set sont n�cessaires au mapping. 
     *  Les autres m�thodes sont l� seulement pour faciliter l'utilisation de la relation.
     *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes. 
     *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
     *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
     */
    protected List populations = new ArrayList();   

    /** R�cup�re la liste des populations en relation. */
    public List getPopulations() {return populations ; } 
    /** D�finit la liste des populations en relation, et met � jour la relation inverse. */
    public void setPopulations (List L) {
        List old = new ArrayList(populations);
        Iterator it1 = old.iterator();
        while ( it1.hasNext() ) {
            Population O = (Population)it1.next();
            O.setDataSet(null);
        }
        Iterator it2 = L.iterator();
        while ( it2.hasNext() ) {
            Population O = (Population)it2.next();
            O.setDataSet(this);
        }
    }
    /** R�cup�re le i�me �l�ment de la liste des populations en relation. */
    public Population getPopulation(int i) {return (Population)populations.get(i) ; }  
    /** Ajoute un objet � la liste des populations en relation, et met � jour la relation inverse. */
    public void addPopulation(Population O) {
        if ( O == null ) return;
        populations.add(O) ;
        O.setDataSet(this) ;
    }
    /** Enl�ve un �l�ment de la liste des populations en relation, et met � jour la relation inverse. */
    public void removePopulation(Population O) {
        if ( O == null ) return;
        populations.remove(O) ; 
        O.setDataSet(null);
    }
    /** Vide la liste des populations en relation, et met � jour la relation inverse. */
    public void emptyPopulations() {
        List old = new ArrayList(populations);
        Iterator it = old.iterator(); 
        while ( it.hasNext() ) {
            Population O = (Population)it.next();
            O.setDataSet(null);
        }
    }
    /** Recup�re la population avec le nom donn�. */
    public Population getPopulation(String nom) {
        Population th;
        Iterator it = this.getPopulations().iterator();
        while ( it.hasNext() ) {
            th = (Population)it.next();
            if ( th.getNom().equals(nom) ) return th;
        }
        System.out.println("=============== ATTENTION : population '"+nom+"' introuvable ==============");
        return null;
    }
    
    
    
	/** Liste des zones d'extraction d�finies pour ce DataSt */
	protected List extractions = new ArrayList();   

	/** R�cup�re la liste des extractions en relation. */
	public List getExtractions() {return extractions; } 
	/** D�finit la liste des extractions en relation. */
	public void setExtractions(List L) {
		extractions = L;
	}
	/** Ajoute un �l�ment de la liste des extractions en relation. */
	public void addExtraction(Extraction O) {
		extractions.add(O) ;
	}
    
    
}
