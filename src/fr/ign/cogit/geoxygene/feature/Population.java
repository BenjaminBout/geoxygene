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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 *  Une population repr�sente TOUS les objets d'une classe h�ritant de FT_Feature.
 *
 *  <P> Les objets qui la composent peuvent avoir une g�ometrie ou non.
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
 * TODO Finir les annotations pour la persistance
 * 
 * @author S�bastien Musti�re
 * @author Sandrine Balley
 * @author Julien Perret 
 */
@Entity
public class Population<Feat extends FT_Feature> extends FT_FeatureCollection<Feat> {
	/** logger*/
	//static Logger logger=Logger.getLogger(Population.class.getName());
	/** Identifiant. Correspond au "cogitID" des tables du SGBD.*/
	protected int id;
	/** Renvoie l'identifiant. NB: l'ID n'est remplit automatiquement que si la population est persistante */
	@Id
	public int getId() {return id;}
	/** Affecte une valeur a l'identifiant */
	public void setId (int I) {id = I;}
	///////////////////////////////////////////////////////
	//      Constructeurs / Chargement / persistance
	///////////////////////////////////////////////////////
	/** Constructeur par d�faut. Sauf besoins particuliers, utiliser plut�t l'autre constructeur */
	public Population() {}
	/**
	 * Constructeur � partir du nom de la population
	 * @param nom nom de la population.
	 */
	public Population(String nom) {this.setNom(nom);}
	/**
	 * Constructeur d'une population.
	 *  Une population peut �tre persistante ou non (la population elle-m�me est alors rendue persistante dans ce constructeur).
	 *  Une population a un nom logique (utile pour naviguer entre populations).
	 *  Les �lements d'une population se r�alisent dans une classe contr�te (classeElements).
	 *  <p>
	 *  <b>NB :</b> lors la construction, auncun �l�ment n'est affect�e � la population, cela doit �tre fait
	 *  � partir d'elements peristant avec chargeElements, ou a partir d'objets Java avec les setElements
	 * @param persistance si vrai, alors la population est persistante
	 * @param nomLogique nom de la population
	 * @param classeElements classe des �l�ments de la population
	 * @param drapeauGeom vrai si les �l�ments de la population portent une g�om�trie, faux sinon
	 */
	@SuppressWarnings("unchecked")
	public Population(boolean persistance, String nomLogique, Class<?> classeElements, boolean drapeauGeom) {
		this.setPersistant(persistance);
		this.setNom(nomLogique);
		this.setClasse((Class<Feat>)classeElements);
		this.flagGeom = drapeauGeom;
		if (persistance) DataSet.db.makePersistent(this);
	}
	/**
	 * Constructeur le plus adapt� � l'utilisation des Populations dot�es d'un
	 * lien vers le FeatureType correspondant.
	 * 
	 * @param ft
	 */
	public Population(FeatureType ft) {
		this.setFeatureType(ft);
		this.setNom(ft.getTypeName());
		this.setNomClasse(ft.getNomClasse());
	}
	/**
	 * Constructeur d'une population. Une population peut �tre persistante ou
	 * non (la population elle-m�me est alors rendue persistante dans ce
	 * constructeur). Une population a un nom logique (utile pour naviguer entre
	 * populations). Les �lements d'une population se r�alisent dans une classe
	 * contr�te (nom_classe_elements). 
	 * <p>
	 * <b>NB :</b> lors la construction, auncun �l�ment
	 * n'est affect� � la population, cela doit �tre fait � partir d'elements
	 * peristant avec chargeElements, ou a partir d'objets Java avec les
	 * setElements
	 */
	public Population(FeatureType ft, boolean persistance, boolean drapeauGeom) {
		this.setFeatureType(ft);
		this.setNom(ft.getTypeName());
		this.setNomClasse(ft.getNomClasse());
		this.setPersistant(persistance);
		this.flagGeom = drapeauGeom;
		if (persistance) DataSet.db.makePersistent(this);
	}
	/**
	 * @param ft
	 * @param persistance
	 */
	public Population(FeatureType ft, boolean persistance) {
		/** attention nom de classe sans package, ca ne marche pas* */
		this.setFeatureType(ft);
		this.setNom(ft.getTypeName());
		this.setNomClasse(ft.getNomClasse());
		this.setPersistant(persistance);
		this.flagGeom = true;
		if (persistance) DataSet.db.makePersistent(this);
	}

	/** Chargement des �l�ments persistants d'une population.
	 *  Tous les �l�ments de la table correspondante sont charg�s.
	 */
	public void chargeElements() {
		if (logger.isInfoEnabled()) logger.info("-- Chargement des elements de la population  "+this.getNom());
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom());
			logger.warn("-----             La population n'est pas persistante");
			return;
		}
		try {elements = DataSet.db.loadAll(classe);}
		catch (Exception e) {
			logger.error("----- ATTENTION : Chargement impossible de la population "+this.getNom());
			logger.error("-----             Sans doute un probleme avec le SGBD, ou table inexistante, ou pas de mapping ");
			//e.printStackTrace();
			return;
		}
		if (logger.isInfoEnabled()) logger.info("-- "+this.size()+" instances chargees dans la population");
	}
	/** Chargement des �l�ments persistants d'une population qui intersectent une g�om�trie donn�e.
	 *  ATTENTION: la table qui stocke les �l�ments doit avoir �t� index�e dans le SGBD.
	 *  ATTENTION AGAIN: seules les populations avec une g�om�trie sont charg�es.
	 */
	public void chargeElementsPartie(GM_Object geom) {
		if (logger.isInfoEnabled()) logger.info("-- Chargement des elements de la population  "+this.getNom());
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom());
			logger.warn("-----             La population n'est pas persistante");
			return;
		}
		if (!this.hasGeom()) {
			logger.warn("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom());
			logger.warn("-----             Les �l�ments de la population n'ont pas de g�om�trie");
			return;
		}
		try {
			elements = DataSet.db.loadAllFeatures(this.getClasse(), geom).getElements();
		} catch (Exception e) {
			logger.error("----- ATTENTION : Chargement impossible de la population "+this.getNom());
			logger.error("-----             La classe n'est peut-�tre pas index�e dans le SGBD");
			logger.error("-----             ou table inexistante, ou pas de mapping ou probleme avec le SGBD ");
			return;
		}
		if (logger.isInfoEnabled()) logger.info("   "+this.size()+" instances chargees dans la population");
	}
	/** Chargement des �l�ments persistants d'une population.
	 *  Tous les �l�ments de la table correspondante sont charg�s.
	 *  Les donn�es doivent d'abord avoir �t� index�es.
	 *  PB: TRES LENT !!!!!!!
	 */
	public void chargeElementsProches(Population<Feat> pop, double dist) {
		if (logger.isInfoEnabled()) {
			logger.info("-- Chargement des elements de la population  "+this.getNom());
			logger.info("-- � moins de "+dist+" de ceux de la population   "+pop.getNom());
		}
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom());
			logger.warn("-----             La population n'est pas persistante");
			return;
		}
		try {
			Iterator<Feat> itPop = pop.getElements().iterator();
			Collection<Feat> selectionTotale = new HashSet<Feat>();
			while (itPop.hasNext()) {
				Feat objet = itPop.next();
				FT_FeatureCollection<Feat> selection = DataSet.db.loadAllFeatures(classe, objet.getGeom(), dist);
				selectionTotale.addAll(selection.getElements());
			}
			elements = new ArrayList<Feat>(selectionTotale);
		} catch (Exception e) {
			logger.error("----- ATTENTION : Chargement impossible de la population "+this.getNom());
			logger.error("-----             Sans doute un probleme avec le SGBD, ou table inexistante, ou pas de mapping ");
			e.printStackTrace();
			return;
		}
		if (logger.isInfoEnabled()) logger.info("-- "+this.size()+" instances chargees dans la population");
	}

	/** Renvoie une population avec tous les �l�ments de this
	 *  situ�s � moins de "dist" des �l�ments de la population
	 *  Travail sur un index en m�moire (pas celui du SGBD).
	 *  Rmq : Fonctionne avec des objets de g�om�trie quelconque
	 */
	public Population<Feat> selectionElementsProchesGenerale(Population<Feat> pop, double dist) {
		Population<Feat> popTemporaire = new Population<Feat>();
		Population<Feat> popResultat = new Population<Feat>(false, this.getNom(), this.getClasse(),true);
		Set<Feat> selectionUnObjet, selectionTotale = new HashSet<Feat>();

		popTemporaire.addCollection(this);
		popTemporaire.initSpatialIndex(Tiling.class, true, 20);
		if (logger.isDebugEnabled()) logger.debug("Fin indexation "+(new Time(System.currentTimeMillis())).toString());
		Iterator<Feat> itPop = pop.getElements().iterator();
		while (itPop.hasNext()) {
			Feat objet = itPop.next();
			GM_Envelope enveloppe = objet.getGeom().envelope();
			double xmin = enveloppe.getLowerCorner().getX()-dist;
			double xmax = enveloppe.getUpperCorner().getX()+dist;
			double ymin = enveloppe.getLowerCorner().getY()-dist;
			double ymax = enveloppe.getUpperCorner().getY()+dist;
			enveloppe = new GM_Envelope(xmin,xmax,ymin,ymax);
			FT_FeatureCollection<Feat> selection = popTemporaire.select(enveloppe);
			Iterator<Feat> itSel = selection.getElements().iterator();
			selectionUnObjet = new HashSet<Feat>();
			while (itSel.hasNext()) {
				Feat objetSel = itSel.next();
				//if (Distances.premiereComposanteHausdorff((GM_LineString)objetSel.getGeom(),(GM_LineString)objet.getGeom())<dist)
				if (objetSel.getGeom().distance(objet.getGeom())<dist) selectionUnObjet.add(objetSel);
			}
			popTemporaire.getElements().removeAll(selectionUnObjet);
			selectionTotale.addAll(selectionUnObjet);
		}
		popResultat.setElements(selectionTotale);
		return popResultat;
	}
	/** Renvoie une population avec tous les �l�ments de this
	 *  situ�s � moins de "dist" des �l�ments de la population pop.
	 */
	public Population<Feat> selectionLargeElementsProches(Population<Feat> pop, double dist) {
		Population<Feat> popTemporaire = new Population<Feat>();
		Population<Feat> popResultat = new Population<Feat>(false, this.getNom(), this.getClasse(),true);

		popTemporaire.addCollection(this);
		popTemporaire.initSpatialIndex(Tiling.class, true);
		Iterator<Feat> itPop = pop.getElements().iterator();
		while (itPop.hasNext()) {
			Feat objet = itPop.next();
			GM_Envelope enveloppe = objet.getGeom().envelope();
			double xmin = enveloppe.getLowerCorner().getX()-dist;
			double xmax = enveloppe.getUpperCorner().getX()+dist;
			double ymin = enveloppe.getLowerCorner().getY()-dist;
			double ymax = enveloppe.getUpperCorner().getY()+dist;
			enveloppe = new GM_Envelope(xmin,xmax,ymin,ymax);
			FT_FeatureCollection<Feat> selection = popTemporaire.select(enveloppe);
			popTemporaire.getElements().removeAll(selection.getElements());
			popResultat.addCollection(selection);
		}
		return popResultat;
	}
	/** Chargement des �l�ments persistants d'une population qui intersectent une zone d'extraction donn�e.
	 *  ATTENTION: la table qui stocke les �l�ments doit avoir �t� index�e dans le SGBD.
	 *  ATTENTION AGAIN: seules les populations avec une g�om�trie sont charg�es.
	 */
	public void chargeElementsPartie(Extraction zoneExtraction) {chargeElementsPartie(zoneExtraction.getGeom());}
	/** Detruit la population si elle est persistante,
	 *  MAIS ne d�truit pas les �l�ments de cette population (pour cela vider la table correspondante dans le SGBD).
	 */
	public void detruitPopulation() {
		if (!this.getPersistant()) return;
		if (logger.isInfoEnabled()) logger.info("Destruction de la population des "+this.getNom());
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
	@ManyToOne
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
	@Transient
	public int getDataSetID() {return dataSetID;}

	//////////////////////////////////////////////////
	// Methodes surchargeant des trucs de FT_FeatureCollection, avec une gestion de la persistance

	/** 
	 * Enl�ve, ET DETRUIT si il est persistant, un �l�ment de la liste des elements de la population,
	 * met �galement � jour la relation inverse, et eventuellement l'index.
	 * <p>
	 * <b>NB :</b> diff�rent de remove (h�rit� de FT_FeatureCollection) qui ne d�truit pas l'�l�ment.
	 */
	public void enleveElement(Feat O) {
		super.remove(O);
		if ( this.getPersistant() ) DataSet.db.deletePersistent(O);
	}
	private static int idNouvelElement = 1;
	/** 
	 * Cr�e un nouvel �l�ment de la population, instance de sa classe par d�faut, et l'ajoute � la population.
	 * <p>
	 *  Si la population est persistante, alors le nouvel �l�ment est rendu persistant dans cette m�thode
	 * <b>NB :</b> diff�rent de add (h�rit� de FT_FeatureCollection) qui ajoute un �l�ment d�j� existant.
	 */
	public Feat nouvelElement() {return nouvelElement(null);}
	/**
	 * Cr�e un nouvel �l�ment de la population (avec la g�o�mtrie geom),
	 *  instance de sa classe par d�faut, et l'ajoute � la population.
	 * <p>
	 *  Si la population est persistante, alors le nouvel �l�ment est rendu persistant dans cette m�thode
	 *  <b>NB :</b> diff�rent de add (h�rit� de FT_FeatureCollection) qui ajoute un �l�ment d�j� existant.
	 */
	public Feat nouvelElement(GM_Object geom) {
		try {
			Feat elem = this.getClasse().newInstance();
			elem.setId(++idNouvelElement);
			elem.setGeom(geom);
			//elem.setPopulation(this);
			super.add(elem);
			if ( this.getPersistant() ) DataSet.db.makePersistent(elem);
			return elem;
		} catch (Exception e) {
			logger.error("ATTENTION : Probl�me � la cr�ation d'un �l�ment de la population "+this.getNom());
			logger.error("            Soit la classe des �l�ments est non valide : "+this.getNomClasse());
			logger.error("               Causes possibles : la classe n'existe pas? n'est pas compil�e? est abstraite?");
			logger.error("            Soit probl�me � la mise � jour de l'index ");
			logger.error("               Causes possibles : mise � jour automatique de l'index, mais l'objet n'a pas encore de g�o�mtrie");
			return null;
		}
	}
	/**
	 * Cr�e un nouvel �l�ment de la population, instance de sa classe par d�faut, et l'ajoute � la population.
	 *  La cr�ation est effectu�e � l'aide du constructeur sp�cifi� par les tableaux signature(classe des
	 *  objets du constructeur), et param (objets eux-m�mes).
	 *  <p>
	 *  Si la population est persistante, alors le nouvel �l�ment est rendu persistant dans cette m�thode
	 *  <p>
	 * <b>NB :</b> diff�rent de add (h�rit� de FT_FeatureCollection) qui ajoute un �l�ment d�j� existant.
	 * @param signature
	 * @param param
	 * @return a new Feature
	 */
	public Feat nouvelElement(Class<?>[] signature, Object[] param) {
		try {
			Feat elem = this.getClasse().getConstructor(signature).newInstance(param);
			super.add(elem);
			if ( this.getPersistant() ) DataSet.db.makePersistent(elem);
			return elem;
		} catch (Exception e) {
			logger.error("ATTENTION : Probl�me � la cr�ation d'un �l�ment de la population "+this.getNom());
			logger.error("            Classe des �l�ments non valide : "+this.getNomClasse());
			logger.error("            Causes possibles : la classe n'existe pas? n'est pas compil�e?");
			return null;
		}
	}

	//////////////////////////////////////////////////
	// Copie de population
	/** 
	 * Copie la population pass�e en argument dans la population trait�e (this).
	 * <p>
	 * <b>NB :<b>
	 * <ul>
	 * <li> 1/ ne copie pas l'eventuelle indexation spatiale,
	 * <li> 2/ n'affecte pas la population au DataSet de la population � copier.
	 * <li> 3/ mais recopie les autres infos: �lements, classe, FlagGeom, Nom et NomClasse
	 * </ul>
	 * @param populationACopier
	 */
	public void copiePopulation(Population<Feat> populationACopier){
		this.setElements(populationACopier.getElements());
		this.setClasse(populationACopier.getClasse());
		this.setFlagGeom(populationACopier.getFlagGeom());
		this.setNom(populationACopier.getNom());
		this.setNomClasse(populationACopier.getNomClasse());
	}
	// //////////////////////////////////////////////////////////////////////////////
	/**
	 * Compl�te Population.chargeElements().
	 * - On v�rifie que la population correspond � une classe du sch�ma conceptuel du DataSet.
	 *   Si non, on initie les populations du DataSet en y incluant celle-ci.
	 * - Chaque FT_Feature charg� est renseign� avec sa population (donc son featureType).
	 */
	public void chargeElementsAvecMetadonnees(){
		if (logger.isInfoEnabled()) logger.info("-- Chargement des elements de la population  "+this.getNom());
		//		MdDataSet datasetContexte = (MdDataSet)this.getDataSet();
		//
		////		 je cherche via le featureType et le schema Conceptuel si on est dans le cadre d'un
		////		 dataset particulier. Si oui je me raccroche aux populations existantes de ce dataset
		//
		//
		//		if (datasetContexte==null){
		//			if (this.getFeatureType().getSchema()!=null){
		//				if (this.getFeatureType().getSchema().getDataset()!=null){
		//					datasetContexte = this.getFeatureType().getSchema().getDataset();
		//					// ce dataset avait-il d�j� des populations ?
		//
		//				}
		//				else System.out.println("Vous �tes hors du contexte d'un MdDataSet");
		//			}
		//			else System.out.println("Vous �tes hors du contexte d'un SchemaConceptuelJeu");
		//		}
		//
		//		//
		//		// j'ai trouv� le MdDataSet dans lequel je travaille.
		//		// Je regarde si ses populations ont �t� initialis�es. Si oui,
		//		// je prends la place de l'une d'elles. Si non, je les initialise
		//		// et je prends la place de l'une d'elles.
		//		//
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Aucune instance n'est chargee dans la population "+this.getNom());
			logger.warn("-----             La population n'est pas persistante");
			return;
		}
		try {
			if (logger.isTraceEnabled()) logger.trace("debut");
			FT_FeatureCollection<Feat> coll = DataSet.db.loadAllFeatures(this.getFeatureType());
			if (logger.isTraceEnabled()) logger.trace("milieu");
			this.addUniqueCollection(coll);
			if (logger.isTraceEnabled()) logger.trace("fin");

		} catch (Exception e) {
			logger.error("----- ATTENTION : Chargement impossible de la population "+this.getNom());
			logger.error("-----             Sans doute un probleme avec ORACLE, ou table inexistante, ou pas de mapping ");
			e.printStackTrace();
			return;
		}
		if (logger.isInfoEnabled()) logger.info("-- "+this.size()+" instances chargees dans la population");
	}
}
