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

import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/** 
 *  Une population repr�sente TOUS les objets d'une classe h�ritant de FT_Feature.
 *
 *  <P> Les objets qui la compose peuvent avoir une g�ometrie ou non.
 *  La population peut �tre persistante ou non, associ�e � un index spatial ou non.
 *
 *  <P> NB: une population existe ind�pendamment des ses �l�ments. 
 *  Avant de charger ses �lements, la population existe mais ne contient aucun �l�ment.
 * 
 * <P> Difference avec FT_FeatureCollection : 
 * une Population est une FT_FeatureCollection possedant les proprietes suivantes.
 * <UL>
 * <LI> Lien vers DataSet. </LI>
 * <LI> Une population peut-etre persistante et exister independamment de ses elements. </LI>
 * <LI> Une population contient TOUS les elements de la classe. </LI>
 * <LI> Un element ne peut appartenir qu'a une seule population (mais a plusieurs FT_FeatureCollection). </LI>
 * <LI> Permet de gerer la persistence des elements de maniere efficace (via chargeElement(), nouvelElement(), etc.) </LI>
 * <LI> Possede quelques attributs (nom classe, etc.). </LI>
 * </UL>  
 * 
 * @author S�bastien Musti�re
 * @version 1.0
 * 
 */

public class Population extends FT_FeatureCollection {
    
    /** Identifiant. Correspond au "cogitID" des tables Oracle.*/
    protected int id;
    /** Renvoie l'identifiant. NB: l'ID n'est remplit automatiquement que si la population est persistante */
    public int getId() {return id;}
    /** Affecte une valeur a l'identifiant */
    public void setId (int I) {id = I;}

   
    ///////////////////////////////////////////////////////
    //      Constructeurs / Chargement / persistance     
    ///////////////////////////////////////////////////////
    /** Constructeur par d�faut. Sauf besoins particuliers, utiliser plut�t l'autre constructeur */
    public Population() {}
    
    /** Constructeur d'une population.
     *  Une population peut �tre persistante ou non (la population elle-m�me est alors rendue persistante dans ce constructeur).
     *  Une population a un nom logique (utile pour naviguer entre populations).
     *  Les �lements d'une population se r�alisent dans une classe contr�te (nom_classe_elements).
     *  NB: lors la construction, auncun �l�ment n'est affect�e � la population, cela doit �tre fait
     *  � partir d'elements peristant avec chargeElements, ou a partir d'objets Java avec les setElements
     */
    public Population(boolean persistance, String nom_logique, String nom_classe_elements, boolean drapeauGeom) {
        this.setPersistant(persistance);
        this.setNom(nom_logique);
        this.setNomClasse(nom_classe_elements);
        this.flagGeom = drapeauGeom;
        if (persistance) DataSet.db.makePersistent(this);
    }
    
    /** Constructeur d'une population dont les �l�ments ont g�om�trie.
     *  Une population peut �tre persistante ou non (la population elle-m�me est alors rendue persistante dans ce constructeur).
     *  Une population a un nom logique (utile pour naviguer entre populations).
     *  Les �lements d'une population se r�alisent dans une classe contr�te (nom_classe_elements).
     *  NB: lors la construction, auncun �l�ment n'est affect�e � la population, cela doit �tre fait
     *  � partir d'elements peristant avec chargeElements, ou a partir d'objets Java avec les setElements
     */
    public Population(boolean persistance, String nom_logique, String nom_classe_elements) {
        this.setPersistant(persistance);
        this.setNom(nom_logique);
        this.setNomClasse(nom_classe_elements);
        this.flagGeom = true;
        if (persistance) DataSet.db.makePersistent(this);
    }
    
    /** Chargement des �l�ments persistants d'une population. 
     *  Tous les �l�ments de la table correspondante sont charg�s.
     */
    public void chargeElements() { 
        System.out.println("");
        System.out.println("-- Chargement des elements de la population  "+this.getNom());

        if (!this.getPersistant()) {
            System.out.println("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom());
            System.out.println("-----             La population n'est pas persistante");
            return;
        }
        
        try {
            elements = DataSet.db.loadAllFeatures(classe).getElements();
        } catch (Exception e) {
            System.out.println("----- ATTENTION : Chargement impossible de la population "+this.getNom());
            System.out.println("-----             Sans doute un probleme avec ORACLE, ou table inexistante, ou pas de mapping ");
            e.printStackTrace();
            return;
        }
        
        System.out.println("-- "+this.size()+" instances chargees dans la population");
    }

    /** Chargement des �l�ments persistants d'une population qui intersectent une g�om�trie donn�e. 
     *  ATTENTION: la table qui stocke les �l�ments doit avoir �t� index�e dans Oracle.
     *  ATTENTION AGAIN: seules les populations avec une g�om�trie sont charg�es.
     */
    public void chargeElementsPartie(GM_Object geom) { 
        System.out.println("");
        System.out.println("-- Chargement des elements de la population  "+this.getNom());

        if (!this.getPersistant()) {
            System.out.println("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom());
            System.out.println("-----             La population n'est pas persistante");
            return;
        }
        
        if (!this.hasGeom()) {
            System.out.println("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom());
            System.out.println("-----             Les �l�ments de la population n'ont pas de g�om�trie");
            return;
        }
        
        try {
            elements = DataSet.db.loadAllFeatures(this.getClasse(), geom).getElements();
        } catch (Exception e) {
            System.out.println("----- ATTENTION : Chargement impossible de la population "+this.getNom());
            System.out.println("-----             La classe n'est peut-�tre pas index�e dans Oracle");
            System.out.println("-----             ou table inexistante, ou pas de mapping ou probleme avec ORACLE ");
            return;
        }
        
        System.out.println("   "+this.size()+" instances chargees dans la population");
    }
  
	/** Chargement des �l�ments persistants d'une population qui intersectent une zone d'extraction donn�e. 
	 *  ATTENTION: la table qui stocke les �l�ments doit avoir �t� index�e dans Oracle.
	 *  ATTENTION AGAIN: seules les populations avec une g�om�trie sont charg�es.
	 */
	public void chargeElementsPartie(Extraction zoneExtraction) {
		chargeElementsPartie(zoneExtraction.getGeom());
	} 
    
    /** Detruit la population si elle est persistante, 
     *  MAIS ne d�truit pas les �l�ments de cette population (pour cela vider la table Oracle correspondante).
     */
    public void detruitPopulation() {
        if (!this.getPersistant()) return;
        System.out.println("Destruction de la population des "+this.getNom());
        DataSet.db.deletePersistent(this);
    }

   
    ///////////////////////////////////////////////////////
    //          Attributs d�crivant la population
    ///////////////////////////////////////////////////////
    /** Nom logique des �l�ments de la population. 
     *  La seule contrainte est de ne pas d�passer 255 caract�res, les accents et espaces sont autoris�s.
     *  A priori, on met le nom des �l�ments au singulier.
     *  Exemple: "Tron�on de route"
     */
    protected String nom;
    public String getNom() {return nom; }
    public void setNom (String S) {nom = S; }

    /** Classe par d�faut des instances de la population.
     *  Ceci est utile pour pouvoir savoir dans quelle classe cr�er de nouvelles instances.
     */
    protected Class classe;
    public Class getClasse() {return classe; }
    public void setClasse (Class C) {
         classe = C; 
         this.nomClasse = classe.getName();
    }
    
    /** Nom complet (package+classe java) de la classe par d�faut des instances de la population.
     *  Pertinent uniquement pour les population peristantes.
     */
    protected String nomClasse;
    /** R�cup�re le nom complet (package+classe java) de la classe par d�faut des instances de la population.
     *  Pertinent uniquement pour les population peristantes.
     */
    public String getNomClasse() {return nomClasse; }
    /** D�finit le nom complet (package+classe java) de la classe par d�faut des instances de la population.
     *  CONSEIL : ne pas utiliser cette m�thode directement, remplir en utilisant setClasse().
     *  Met �galement � jour l'attribut classe.
     *  Utile uniquement pour les population peristantes.
     */
    public void setNomClasse (String S) {
         nomClasse = S; 
         try {
            this.classe = Class.forName(nomClasse);
         } catch (Exception e) {
            System.out.println("----- ATTENTION : Nom de classe #"+nomClasse+"# non valide");
            System.out.println("-----             Causes possibles : la classe n'existe pas ou n'est pas compilee");
         }
    }

    /** Bool�en sp�cifiant si la population est persistente ou non (vrai par d�faut).  */
    // NB pour d�velopeurs : laisser 'true' par d�faut. 
    // Sinon, cela pose des probl�mes au chargement (un th�me persistant charg� a son attribut persistant � false).    
    protected boolean persistant = true;
    /** Bool�en sp�cifiant si la population est persistente ou non (vrai par d�faut).  */
    public boolean getPersistant() {return persistant;}
    /** Bool�en sp�cifiant si la population est persistente ou non (vrai par d�faut).  */
    public void setPersistant(boolean b) {persistant = b;}
    
    
///////////////////////////////////////////////////////
//     Relations avec les th�mes et les �t�ments
///////////////////////////////////////////////////////
    /** DataSet auquel apparient la population (une population appartient � un seul DataSet). */
    protected DataSet dataSet;
    /** R�cup�re le DataSet de la population. */
    public DataSet getDataSet() {return dataSet;  }
    /** D�finit le DataSet de la population, et met � jour la relation inverse. */
    public void setDataSet(DataSet O) {
        DataSet old = dataSet;
        dataSet= O;  
        if ( old  != null ) old.getPopulations().remove(this);
        if ( O != null ) {
            dataSetID = O.getId();
            if ( !(O.getPopulations().contains(this)) ) O.getPopulations().add(this);            
        } else dataSetID = 0;
    }
    private int dataSetID;
    /** Ne pas utiliser, necessaire au mapping OJB */
    public void setDataSetID(int I) {dataSetID = I;}
    /** Ne pas utiliser, necessaire au mapping OJB */
    public int getDataSetID() {return dataSetID;}    


    //////////////////////////////////////////////////
    // Methodes surchargeant des trucs de FT_FeatureCollection, avec une gestion de la persistance
    
    /** Enl�ve, ET DETRUIT si il est persistant, un �l�ment de la liste des elements de la population, 
     *  met �galement � jour la relation inverse, et eventuellement l'index. 
     *  NB : diff�rent de remove (h�rit� de FT_FeatureCollection) qui ne d�truit pas l'�l�ment.
     */
    public void enleveElement(FT_Feature O) {
        super.remove(O);
        if ( this.getPersistant() ) DataSet.db.deletePersistent(O);
    }
    
    /** Cr�e un nouvel �l�ment de la population, instance de sa classe par d�faut, et l'ajoute � la population.
     *  Si la population est persistante, alors le nouvel �l�ment est rendu persistant dans cette m�thode
     *  NB : diff�rent de add (h�rit� de FT_FeatureCollection) qui ajoute un �l�ment d�j� existant.
     */
    public FT_Feature nouvelElement() {
        try {
            FT_Feature elem = (FT_Feature)this.getClasse().newInstance();
            //elem.setPopulation(this);
            super.add(elem);
            if ( this.getPersistant() ) DataSet.db.makePersistent(elem);
            return elem;
        } catch (Exception e) {
            System.out.println("ATTENTION : Probl�me � la cr�ation d'un �l�ment de la population "+this.getNom());
            System.out.println("            Soit la classe des �l�ments est non valide : "+this.getNomClasse());
			System.out.println("               Causes possibles : la classe n'existe pas? n'est pas compil�e? est abstraite?");
			System.out.println("            Soit probl�me � la mise � jour de l'index ");
			System.out.println("               Causes possibles : mise � jour automatique de l'index, mais l'objet n'a pas encore de g�o�mtrie");
            return null;
        }
    }

	/** Cr�e un nouvel �l�ment de la population (avec la g�o�mtrie geom), 
	 *  instance de sa classe par d�faut, et l'ajoute � la population.
	 *  
	 *  Si la population est persistante, alors le nouvel �l�ment est rendu persistant dans cette m�thode
	 *  NB : diff�rent de add (h�rit� de FT_FeatureCollection) qui ajoute un �l�ment d�j� existant.
	 */
	public FT_Feature nouvelElement(GM_Object geom) {
		try {
			FT_Feature elem = (FT_Feature)this.getClasse().newInstance();
			elem.setGeom(geom);
			//elem.setPopulation(this);
			super.add(elem);
			if ( this.getPersistant() ) DataSet.db.makePersistent(elem);
			return elem;
		} catch (Exception e) {
			System.out.println("ATTENTION : Probl�me � la cr�ation d'un �l�ment de la population "+this.getNom());
			System.out.println("            Soit la classe des �l�ments est non valide : "+this.getNomClasse());
			System.out.println("               Causes possibles : la classe n'existe pas? n'est pas compil�e? est abstraite?");
			System.out.println("            Soit probl�me � la mise � jour de l'index ");
			System.out.println("               Causes possibles : mise � jour automatique de l'index, mais l'objet n'a pas encore de g�o�mtrie");
			return null;
		}
	}
	/** Cr�e un nouvel �l�ment de la population, instance de sa classe par d�faut, et l'ajoute � la population.
	 *  La cr�ation est effectu�e � l'aide du constructeur sp�cifi� par les tableaux signature(classe des
	 *  objets du constructeur), et param (objets eux-m�mes). 
	  *  Si la population est persistante, alors le nouvel �l�ment est rendu persistant dans cette m�thode
	  *  NB : diff�rent de add (h�rit� de FT_FeatureCollection) qui ajoute un �l�ment d�j� existant.
	  */
 
	public FT_Feature nouvelElement(Class[] signature, Object[] param) {
		try {
			FT_Feature elem = (FT_Feature)this.getClasse().getConstructor(signature).newInstance(param);
			super.add(elem);
			if ( this.getPersistant() ) DataSet.db.makePersistent(elem);
			return elem;
		} catch (Exception e) {
			System.out.println("ATTENTION : Probl�me � la cr�ation d'un �l�ment de la population "+this.getNom());
			System.out.println("            Classe des �l�ments non valide : "+this.getNomClasse());
			System.out.println("            Causes possibles : la classe n'existe pas? n'est pas compil�e?");
			return null;
		}
	}

	//////////////////////////////////////////////////
	// Copie de population
	/** Copie la population pass�e en argument dans la population trait�e (this)
	 * NB: 1/ ne copie pas l'eventuelle indexation spatiale,
	 *     2/ n'affecte pas la population au DataSet de la population � copier.
	 * 	   3/ mais recopie les autres infos: �lements, classe, FlagGeom, Nom et NomClasse
	 */
	public void copiePopulation(Population populationACopier){
		this.setElements(populationACopier.getElements());
		this.setClasse(populationACopier.getClasse());
		this.setFlagGeom(populationACopier.getFlagGeom());
		this.setNom(populationACopier.getNom());
		this.setNomClasse(populationACopier.getNomClasse());
	} 

}
