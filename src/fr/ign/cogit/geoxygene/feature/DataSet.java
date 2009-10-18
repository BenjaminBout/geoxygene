/**
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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.metadata.MetadataManager;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.feature.type.GF_Constraint;
import fr.ign.cogit.geoxygene.schema.Produit;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.SchemaConceptuelJeu;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Classe m�re pour tout jeu de donn�es. Un DataSet peut par exemple
 * correspondre � une zone d'une BD, ou seulement un th�me. Un DataSet est
 * constitu� de mani�re r�cursive d'un ensemble de jeux de donn�es, et d'un
 * ensemble de populations, elles m�mes constitu�es d'un ensemble d'�l�ments.
 * 
 * TODO Finir les annotations pour la persistance
 * 
 * @author S�bastien Musti�re
 * @author Eric Grosso
 * @author Sandrine Balley
 * @author Julien Perret
 */
@Entity
public class DataSet {
	/** logger*/
	static Logger logger=Logger.getLogger(DataSet.class.getName());
	/** l'identifiant */
	protected int id;
	/**
	 * Renvoie l'identifiant
	 * @return l'identifiant
	 */
	@Id
	public int getId() {return id;}
	/** 
	 * Affecte un identifiant.
	 * @param Id un identifiant
	 */
	public void setId(int Id) {id = Id;}
	/**
	 * Param�tre statique de connexion � la BD.
	 * <p>
	 * Ce param�tre est tr�s utilis� dans G�Oxyg�ne
	 * TODO Remplacer cet attribut statique non prot�t� par un singleton
	 */
	public static Geodatabase db;

	// /////////////////////////////////////////////////////
	// Constructeurs / Chargement / persistance
	// /////////////////////////////////////////////////////

	/** Constructeur par d�faut. */
	public DataSet() {this.ojbConcreteClass = this.getClass().getName();}
	/**
	 * Constructeur par d�faut, recopiant les champs de m�tadonn�es du DataSet
	 * en param�tre sur le nouveau
	 */
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

	/**
	 * Chargement des instances des populations persistantes d'un jeu de
	 * donn�es.
	 */
	public void chargeElements() {
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Probleme au chargement du jeu de donnees " + this.getNom());
			logger.warn("----- Impossible de charger les elements d'un jeu de donnees non persistant");
			return;
		}
		// chargement recursif des dataset composants this
		for (DataSet DS:this.getComposants()) if (DS.getPersistant()) DS.chargeElements();
		// chargement recursif des populations de this
		logger.info("###### Chargement des elements du DataSet " + this.getNom());
		for (Population<? extends FT_Feature> pop:this.getPopulations()) if (pop.getPersistant()) pop.chargeElements();
	}
	/**
	 * Chargement des instances des populations persistantes d'un jeu de donn�es
	 * qui intersectent une g�om�trie donn�e (extraction g�om�trique).
	 * @param geom g�om�trie utilis�e pour l'extraction g�om�trique.
	 */
	public void chargeElementsPartie(GM_Object geom) {
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Probleme au chargement du jeu de donnees " + this.getNom());
			logger.warn("----- Impossible de charger les elements d'un jeu de donnees non persistant");
			return;
		}
		// chargement recursif des dataset composants this
		for (DataSet DS:this.getComposants()) if (DS.getPersistant()) DS.chargeElementsPartie(geom);
		// chargement recursif des populations de this
		logger.info("###### Chargement des elements du DataSet " + this.getNom());
		for (Population<? extends FT_Feature> pop:this.getPopulations()) if (pop.getPersistant()) pop.chargeElementsPartie(geom);
	}
	/**
	 * Chargement des instances des populations persistantes d'un jeu de donn�es
	 * qui intersectent une g�om�trie donn�e. ATTENTION: les tables qui stockent
	 * les �l�ments doivent avoir �t� index�es dans Oracle. ATTENTION AGAIN:
	 * seules les populations avec une g�om�trie sont charg�es.
	 * @param zoneExtraction zone utilis�e pour l'extraction g�om�trique
	 */
	public void chargeElementsPartie(Extraction zoneExtraction) {chargeElementsPartie(zoneExtraction.getGeom());}
	/**
	 * M�thode de chargement pour les test. Elle est un peu tordue dans le
	 * param�trage mais permet de ne charger que ce qu'on veut. Elle permet de
	 * charger les instances des populations persistantes d'un jeu de donn�es
	 * qui : - intersectent une g�om�trie donn�e (extraction g�om�trique), - ET
	 * qui appartiennent � certains th�mes et populations pr�cis�s en entr�e.
	 * 
	 * @param geom D�finit la zone d'extraction.
	 * 
	 * @param themes D�finit les sous-DS du DS � charger. Pour le DS lui-m�me, et
	 *            pour chaque sous-DS, on pr�cise �galement quelles populations
	 *            sont charg�es. Ce param�tre est une liste de liste de String
	 *            compos�e comme suit (si la liste est nulle on charge tout) :<ul>
	 * <li> 1/ Le premier �l�ment est soit null (on charge alors toutes les 
	 * populations directement sous le DS), soit une liste contenant les noms 
	 * des populations directement sous le DS que l'on charge 
	 * (si la liste est vide, on ne charge rien).
	 * <li> 2/ Tous les autres �l�ments sont des listes (une pour chaque sous-DS) 
	 * qui contiennent chacune d'abord le nom d'un sous-DS que l'on veut charger,
	 * puis soit rien d'autre si on charge toutes les populations du sous-DS,
	 * soit le nom des populations du sous-DS que l'on veut charger.
	 * </ul>
	 * 
	 * <b>NB :</b> Attention aux majuscules et aux accents.
	 * <p>
	 * <b>EXEMPLE</b> de parametre themes pour un DS repr�entant la BDCarto, et
	 * sp�cifiant qu'on ne veut charger que les troncon et les noeud du th�me
	 * routier, et les troncons du th�me hydro, mais tout le th�me ferr�.
	 * <p> 
	 * <b>theme = {null, liste1, liste2, liste3}</b>, avec :
	 * <ul>
	 * <li> null car il n'y a pas de population directement sous le DS BDCarto,
	 * <li> liste1 = {"Routier","Tron�ons de route", "Noeuds routier"}, 
	 * <li> liste2 = {"Hydrographie","Tron�ons de cours d'eau"}, 
	 * <li> liste3 = {"Ferr�"}.
	 * </ul
	 */
	public void chargeExtractionThematiqueEtSpatiale(GM_Object geom, List<List<String>> themes) {
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Probleme au chargement du jeu de donnees " + this.getNom());
			logger.warn("----- Impossible de charger les elements d'un jeu de donnees non persistant");
			return;
		}

		List<String> themeACharger, extraitThemes;
		List<List<String>> populationsACharger;
		List<String> populationsACharger2;
		Iterator<List<String>> itThemes;
		Iterator<String> itPopulationsACharger;
		boolean aCharger;

		// chargement recursif des dataset composants this
		for (DataSet DS:this.getComposants()) {
			populationsACharger = null;
			if (themes == null) aCharger = true;
			else {
				itThemes = themes.iterator();
				themeACharger = itThemes.next();
				if (!itThemes.hasNext()) aCharger = true;
				else {
					aCharger = false;
					while (itThemes.hasNext()) {
						themeACharger = itThemes.next();
						if (DS.getNom().equals(themeACharger.get(0))) {
							aCharger = true;
							if (themeACharger.size() == 1) {
								populationsACharger = null;
								break;
							}
							extraitThemes = new ArrayList<String>(themeACharger);
							extraitThemes.remove(0);
							populationsACharger = new ArrayList<List<String>>();
							populationsACharger.add(extraitThemes);
							break;
						}
					}
				}
			}
			if (aCharger && DS.getPersistant())
				DS.chargeExtractionThematiqueEtSpatiale(geom, populationsACharger);
		}
		/** chargement des populations de this (directement sous this)*/
		if (themes == null) populationsACharger2 = null;
		else {
			itThemes = themes.iterator();
			populationsACharger2 = itThemes.next();
		}
		logger.info("###### Chargement des elements du DataSet " + this.getNom());
		for (Population<? extends FT_Feature> pop:this.getPopulations()) {
			if (populationsACharger2 == null) aCharger = true;
			else {
				aCharger = false;
				itPopulationsACharger = populationsACharger2.iterator();
				while (itPopulationsACharger.hasNext()) {
					String nomPopulation = itPopulationsACharger.next();
					if (pop.getNom().equals(nomPopulation)) {
						aCharger = true;
						break;
					}
				}
			}
			if (aCharger && pop.getPersistant()) {
				if (geom != null) pop.chargeElementsPartie(geom);
				else pop.chargeElements();
			}
		}
	}

	/**
	 * Pour un jeu de donn�es persistant, d�truit le jeu de donn�es, ses th�mes
	 * et ses objets populations.
	 * <p>
	 * <b>ATTENTION :</b> ne d�truit pas les �l�ments des populations
	 * (pour cela vider les tables Oracle).
	 */
	public void detruitJeu() {
		if (!this.getPersistant()) {
			logger.warn("----- ATTENTION : Probleme � la destruction du jeu de donnees " + this.getNom());
			logger.warn("----- Le jeu de donn�es n'est pas persistant");
			return;
		}
		// destruction des populations de this
		for (Population<? extends FT_Feature> pop:this.getPopulations()) if (pop.getPersistant()) pop.detruitPopulation();
		// destruction recursive des dataset composants this
		for (DataSet DS:this.getComposants()) if (DS.getPersistant()) DS.detruitJeu();
		// destruction des zones d'extraction associ�es � this
		for (Extraction ex:this.getExtractions()) {
			logger.info("###### Destruction de la zone d'extraction " + ex.getNom());
			db.deletePersistent(ex);
		}
		// destruction de this
		logger.info("###### Destruction du DataSet " + this.getNom());
		db.deletePersistent(this);
	}

	/**
	 *  NB pour codeurs : laisser 'true' par d�faut. Sinon, comme cet attribut 
	 *  n'est pas persistant, cela pose des probl�mes au chargement 
	 *  (un th�me persistant charg� a son attribut persistant � false.
	 */
	protected boolean persistant = true;
	/**
	 * Bool�en sp�cifiant si le th�me est persistant ou non (vrai par d�faut).
	 * <p>
	 * <b>NB :</b> si un jeu de donn�es est non persistant, tous ses th�mes sont non
	 * persistants. Mais si un jeu de donn�es est persistant, certains de ses
	 * th�mes peuvent ne pas l'�tre.
	 * <p>
	 * <b>ATTENTION :</b> pour des raisons propres � OJB, m�me si la classe DataSet est
	 * concr�te, il n'est pas possible de cr�er un objet PERSISTANT de cette
	 * classe, il faut utiliser les sous-classes.
	 * @return vrai si le jeu de donn� est persistant, faux sinon
	 */
	public boolean getPersistant() {return persistant;}
	/**
	 * Bool�en sp�cifiant si le th�me est persistant ou non (vrai par d�faut).
	 * <p>
	 * <b>NB :</b> si un jeu de donn�es est non persistant, tous ses th�mes sont non
	 * persistants. Mais si un jeu de donn�es est persistant, certains de ses
	 * th�mes peuvent ne pas l'�tre.
	 * <p>
	 * <b>ATTENTION :</b> pour des raisons propres � OJB, m�me si la classe DataSet est
	 * concr�te, il n'est pas possible de cr�er un objet PERSISTANT de cette
	 * classe, il faut utiliser les sous-classes.
	 * @param b vrai si le jeu de donn� est persistant, faux sinon
	 */
	public void setPersistant(boolean b) {persistant = b;}

	// /////////////////////////////////////////////////////
	// Metadonn�es
	// /////////////////////////////////////////////////////
	/**
	 * Nom de la classe concr�te de this : pour OJB, ne pas manipuler
	 * directement
	 */
	protected String ojbConcreteClass;
	public String getOjbConcreteClass() {return ojbConcreteClass;}
	public void setOjbConcreteClass(String S) {ojbConcreteClass = S;}
	
	/** Nom du jeu de donn�es */
	protected String nom = "";
	public String getNom() {return nom;}
	public void setNom(String S) {nom = S;}
	
	/** Type de BD (BDcarto, BDTopo...). */
	protected String typeBD = "";
	public String getTypeBD() {return typeBD;}
	public void setTypeBD(String S) {typeBD = S;}
	
	/** Mod�le utilis� (format shape, structur�...). */
	protected String modele = "";
	public String getModele() {return modele;}
	public void setModele(String S) {modele = S;}

	/** Zone g�ographique couverte. */
	protected String zone = "";
	public String getZone() {return zone;}
	public void setZone(String S) {zone = S;}

	/** Date des donn�es. */
	protected String date = "";
	public String getDate() {return date;}
	public void setDate(String S) {date = S;}

	/** Commentaire quelconque. */
	protected String commentaire = "";
	public String getCommentaire() {return commentaire;}
	public void setCommentaire(String S) {commentaire = S;}

	// /////////////////////////////////////////////////////
	// Th�mes du jeu de donn�es
	// /////////////////////////////////////////////////////
	/**
	 * Un DataSet se d�compose r�cursivement en un ensemble de DataSet. Le lien
	 * de DataSet vers lui-m�me est un lien 1-n. Les m�thodes get (sans indice)
	 * et set sont n�cessaires au mapping. Les autres m�thodes sont l� seulement
	 * pour faciliter l'utilisation de la relation. ATTENTION: Pour assurer la
	 * bidirection, il faut modifier les listes uniquement avec ces methodes.
	 * NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas
	 * "null". Pour casser toutes les relations, faire setListe(new ArrayList())
	 * ou emptyListe().
	 */
	protected List<DataSet> composants = new ArrayList<DataSet>();
	/** R�cup�re la liste des DataSet composant this. */
	@OneToMany
	public List<DataSet> getComposants() {return composants;}
	/**
	 * D�finit la liste des DataSet composant le DataSet, et met � jour la
	 * relation inverse.
	 */
	public void setComposants(List<DataSet> L) {
		emptyComposants();
		for(DataSet dataset:L) dataset.setAppartientA(this);
	}

	/** R�cup�re le i�me �l�ment de la liste des DataSet composant this. */
	public DataSet getComposant(int i) {return composants.get(i);}
	/**
	 * Ajoute un objet � la liste des DataSet composant le DataSet, et met �
	 * jour la relation inverse.
	 */
	public void addComposant(DataSet O) {
		if (O == null) return;
		composants.add(O);
		O.setAppartientA(this);
	}
	/**
	 * Enl�ve un �l�ment de la liste DataSet composant this, et met � jour la
	 * relation inverse.
	 */
	public void removeComposant(DataSet O) {
		if (O == null) return;
		composants.remove(O);
		O.setAppartientA(null);
	}
	/**
	 * Vide la liste des DataSet composant this, et met � jour la relation
	 * inverse.
	 */
	public void emptyComposants() {
		List<DataSet> old = new ArrayList<DataSet>(composants);
		for(DataSet dataset:old) dataset.setAppartientA(null);
		composants.clear();
	}
	/** Recup�re le DataSet composant de this avec le nom donn�. 
	 * @param nomComposant nom du dataset � r�cup�rer
	 * @return le DataSet composant de this avec le nom donn�.
	 */
	public DataSet getComposant(String nomComposant) {
		for(DataSet dataset:this.getComposants()) if (dataset.getNom().equals(nomComposant)) return dataset;
		logger.warn("----- ATTENTION : DataSet composant #" + nomComposant + "# introuvable dans le DataSet " + this.getNom());
		return null;
	}

	/** Relation inverse � Composants */
	private DataSet appartientA;
	/** R�cup�re le DataSet dont this est composant. */
	@ManyToOne
	public DataSet getAppartientA() {return appartientA;}
	/**
	 * D�finit le DataSet dont this est composant., et met � jour la relation inverse.
	 */
	public void setAppartientA(DataSet O) {
		DataSet old = appartientA;
		appartientA = O;
		if (old != null) old.getComposants().remove(this);
		if (O != null) {
			appartientAID = O.getId();
			if (!(O.getComposants().contains(this))) O.getComposants().add(this);
		} else appartientAID = 0;
	}
	private int appartientAID;
	/** Ne pas utiliser, necessaire au mapping OJB */
	public void setAppartientAID(int I) {appartientAID = I;}
	/** Ne pas utiliser, necessaire au mapping OJB */
	@Transient
	public int getAppartientAID() {return appartientAID;}

	/**
	 * Liste des population du DataSet. Les m�thodes get (sans indice) et set
	 * sont n�cessaires au mapping. Les autres m�thodes sont l� seulement pour
	 * faciliter l'utilisation de la relation. 
	 * <p>
	 * <b>ATTENTION :</b> Pour assurer la bidirection, il faut modifier 
	 * les listes uniquement avec ces methodes.
	 * <p>
	 * <b>NB :</b> si il n'y a pas d'objet en relation, la liste est vide mais n'est pas
	 * "null". Pour casser toutes les relations, faire setListe(new ArrayList())
	 * ou emptyListe().
	 */
	protected List<Population<? extends FT_Feature>> populations = new ArrayList<Population<? extends FT_Feature>>();
	/** R�cup�re la liste des populations en relation. */
	@OneToMany
	public List<Population<? extends FT_Feature>> getPopulations() {return populations;}
	/**
	 * D�finit la liste des populations en relation, et met � jour la relation
	 * inverse.
	 */
	public void setPopulations(List<Population<? extends FT_Feature>> L) {
		List<Population<? extends FT_Feature>> old = new ArrayList<Population<? extends FT_Feature>>(populations);
		for (Population<? extends FT_Feature> pop:old) pop.setDataSet(null);
		for (Population<? extends FT_Feature> pop:L) pop.setDataSet(this);
	}
	/** R�cup�re le i�me �l�ment de la liste des populations en relation. */
	public Population<? extends FT_Feature> getPopulation(int i) {return populations.get(i);}
	/**
	 * Ajoute un objet � la liste des populations en relation, et met � jour la
	 * relation inverse.
	 */
	public void addPopulation(Population<? extends FT_Feature> O) {
		if (O == null) return;
		populations.add(O);
		O.setDataSet(this);
	}
	/**
	 * Enl�ve un �l�ment de la liste des populations en relation, et met � jour
	 * la relation inverse.
	 * @param O �l�ment � enlever
	 */
	public void removePopulation(Population<? extends FT_Feature> O) {
		if (O == null) return;
		populations.remove(O);
		O.setDataSet(null);
	}
	/**
	 * Vide la liste des populations en relation, et met � jour la relation
	 * inverse.
	 */
	public void emptyPopulations() {
		List<Population<? extends FT_Feature>> old = new ArrayList<Population<? extends FT_Feature>>(populations);
		for (Population<? extends FT_Feature> pop:old) pop.setDataSet(null);
		populations.clear();
	}

	/** Recup�re la population avec le nom donn�.
	 * @param nomPopulation nom de la population � r�cup�rer
	 * @return la population avec le nom donn�.
	 */
	public Population<? extends FT_Feature> getPopulation(String nomPopulation) {
		for (Population<? extends FT_Feature> pop:this.getPopulations()) if (pop.getNom().equals(nomPopulation))	return pop;
		//if (logger.isDebugEnabled()) logger.debug("=============== ATTENTION : population '" + nom + "' introuvable ==============");
		return null;
	}

	/** Liste des zones d'extraction d�finies pour ce DataSt */
	protected List<Extraction> extractions = new ArrayList<Extraction>();
	/** R�cup�re la liste des extractions en relation. */
	//@OneToMany
	@Transient
	public List<Extraction> getExtractions() {return extractions;}
	/** D�finit la liste des extractions en relation. */
	public void setExtractions(List<Extraction> L) {	extractions = L;}
	/** Ajoute un �l�ment de la liste des extractions en relation. */
	public void addExtraction(Extraction O) {extractions.add(O);}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * M�thodes permettant de cr�er un jeu de donn�es: <ul> <li> reli� � un
	 * produit, donc potentiellement � de nombreuses m�tadonn�es </li> <li>
	 * reli� � 0 ou 1 sch�maConceptuelJeu (un sch�maConceptuelJeu est associ� �
	 * 0 ou 1 jeu)</li> <li> compos� de Populations dot�es de m�tadonn�es </li>
	 * </ul>
	 * 
	 * Comme indiqu� dans la classe Population, les populations d'un DataSet ne
	 * sont pas destin�es � �tre persistantes. Elles peuvent �tre initialis�es �
	 * partir du sch�ma conceptuel, qui lui est persitent, gr�ce � la m�thode
	 * DataSet.initPopulations()
	 */
	// //////////////////////////////////////////////////////////////////////////////////////////////

	/** *reference statique au repository OJB */
	public static MetadataManager metadataManager;
	/***************************************************************************
	 * Partie Description du DataSet : produit et sch�ma de donn�es
	 **************************************************************************/
	protected Produit produit;
	/**
	 * @return the produit
	 */
	//@OneToOne
	@Transient
	public Produit getProduit() {return produit;}
	/**
	 * @param produit the produit to set
	 */
	public void setProduit(Produit produit) {this.produit = produit;}
	/**
	 * Schema conceptuel correspondant au jeu de donnees
	 */
	protected SchemaConceptuelJeu schemaConceptuel;
	/**
	 * Affecte le schema conceptuel correspondant au jeu de donnees
	 * @param schema le schema conceptuel correspondant au jeu de donnees
	 */
	public void setSchemaConceptuel(SchemaConceptuelJeu schema) {this.schemaConceptuel = schema;}
	/**
	 * Renvoie le schema conceptuel correspondant au jeu de donnees
	 * @return le schema conceptuel correspondant au jeu de donnees
	 */
	//@OneToOne
	@Transient
	public SchemaConceptuelJeu getSchemaConceptuel() {return this.schemaConceptuel;}
	/**
	 * Liste des contraintes (int�grit�) s'appliquant � ce jeu
	 */
	public List<GF_Constraint> contraintes;
	/**
	 * Renvoie la liste des contraintes d'integrite s'appliquant a ce jeu de donnees
	 * @return liste des contraintes d'integrite s'appliquant a ce jeu de donnees
	 */
	//@OneToMany
	@Transient
	public List<GF_Constraint> getContraintes() {return contraintes;}
	/**
	 * Affecte la liste des contraintes d'integrite s'appliquant a ce jeu de donnees
	 * @param contraintes la liste des contraintes d'integrite s'appliquant a ce jeu de donnees
	 */
	public void setContraintes(List<GF_Constraint> contraintes) {this.contraintes = contraintes;}

	/***************************************************************************
	 * Partie Utilisation du DataSet : les donn�es peuvent �tre acced�es via des
	 * FT_Collection et via des Populations.
	 **************************************************************************/
	/**
	 * initialise la liste des populations du jeu en fonction du sch�ma
	 * conceptuel. Les donn�es ne sont pas charg�es.
	 */
	public void initPopulations() {
		SchemaConceptuelJeu schema = this.getSchemaConceptuel();
		List<Population<? extends FT_Feature>> listPop = new ArrayList<Population<? extends FT_Feature>>();
		for (int i = 0; i < schema.getFeatureTypes().size(); i++) {
			listPop.add(new Population<FT_Feature>((FeatureType) schema.getFeatureTypeI(i)));
		}
		this.setPopulations(listPop);
	}

	/**
	 * @param nomFeatureType nom du featuretype
	 * @return population dont le featuretype correspond au nom donn�
	 */
	public Population<? extends FT_Feature> getPopulationByFeatureTypeName(String nomFeatureType) {
		for (int i = 0; i < this.getPopulations().size(); i++) {
			if (this.getPopulations().get(i).getFeatureType().getTypeName().equals(nomFeatureType)) {
				return this.getPopulations().get(i);
			}
		}
		logger.error("La Population " + nomFeatureType + " n'a pas �t� trouv�e.");
		return null;
	}

	private static DataSet dataSet = null;
	/**
	 * @return une instance du singleton DataSet
	 */
	public static DataSet getInstance() {
		if (dataSet==null) synchronized(DataSet.class) {if (dataSet==null) dataSet=new DataSet();}
		return dataSet;
	}
}
