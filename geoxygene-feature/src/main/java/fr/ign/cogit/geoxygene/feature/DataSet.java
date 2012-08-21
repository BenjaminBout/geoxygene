/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
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

import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.api.feature.IExtraction;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_Constraint;
import fr.ign.cogit.geoxygene.api.schema.IProduct;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.schema.Produit;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.SchemaConceptuelJeu;

/**
 * Classe m�re pour tout jeu de donn�es. Un DataSet peut par exemple
 * correspondre � une zone d'une BD, ou seulement un th�me. Un DataSet est
 * constitu� de mani�re r�cursive d'un ensemble de jeux de donn�es, et d'un
 * ensemble de populations, elles m�mes constitu�es d'un ensemble d'�l�ments.
 * TODO Finir les annotations pour la persistance
 * 
 * @author S�bastien Musti�re
 * @author Eric Grosso
 * @author Sandrine Balley
 * @author Julien Perret
 */
@Entity
public class DataSet implements IDataSet<SchemaConceptuelJeu> {
  /**
   * The logger.
   */
  private static final Logger LOGGER = Logger
      .getLogger(DataSet.class.getName());

  /**
   * @return The static logger.
   */
  public static final Logger getLogger() {
    return DataSet.LOGGER;
  }

  public static int STATICID = 0;
  /** l'identifiant */
  protected int id;

  @Override
  @Id
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int Id) {
    this.id = Id;
  }

  /**
   * param�tre statique de connexion � la BD.
   * <p>
   * Ce param�tre est tr�s utilis� dans GeOxygene TODO Remplacer cet attribut
   * statique non prot�t� par un singleton
   */
  public static Geodatabase db;

  // /////////////////////////////////////////////////////
  // Constructeurs / Chargement / persistance
  // /////////////////////////////////////////////////////

  /** Constructeur par d�faut. */
  public DataSet() {
    this.ojbConcreteClass = this.getClass().getName();
    this.setId(STATICID++);
  }

  /**
   * Constructeur par d�faut, recopiant les champs de m�tadonn�es du DataSet en
   * param�tre sur le nouveau.
   * <p>
   * Seules les populations sont copi�es (composants et extractions ignor�s).
   * @param DS dataset to copy
   */
  @SuppressWarnings("unchecked")
  public DataSet(DataSet DS) {
    this.ojbConcreteClass = this.getClass().getName();
    if (DS == null) {
      return;
    }
    this.setNom(DS.getNom());
    this.setTypeBD(DS.getTypeBD());
    this.setModele(DS.getModele());
    this.setZone(DS.getZone());
    this.setDate(DS.getDate());
    this.setCommentaire(DS.getCommentaire());
    // composants
    // populations
    for (IPopulation<? extends IFeature> p : DS.getPopulations()) {
      try {
        IPopulation<? extends IFeature> f = p.getClass().getConstructor(
            p.getClass()).newInstance(p);
        this.populations.add(f);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    // extractions
  }

  /**
   * Chargement des instances des populations persistantes d'un jeu de données.
   */
  @Override
  public void chargeElements() {
    if (!this.getPersistant()) {
      DataSet.LOGGER
          .warn("----- ATTENTION : Probleme au chargement du jeu de donnees " + this.getNom()); //$NON-NLS-1$
      DataSet.LOGGER
          .warn("----- Impossible de charger les elements d'un jeu de donnees non persistant"); //$NON-NLS-1$
      return;
    }
    // chargement recursif des dataset composants this
    for (IDataSet<?> DS : this.getComposants()) {
      if (DS.getPersistant()) {
        DS.chargeElements();
      }
    }
    // chargement recursif des populations de this
    DataSet.LOGGER.info("###### Chargement des elements du DataSet "
        + this.getNom());
    for (IPopulation<? extends IFeature> pop : this.getPopulations()) {
      if (pop.getPersistant()) {
        pop.chargeElements();
      }
    }
  }

  /**
   * Chargement des instances des populations persistantes d'un jeu de donn�es
   * qui intersectent une g�om�trie donn�e (extraction g�om�trique).
   * 
   * @param geom g�om�trie utilis�e pour l'extraction g�om�trique.
   */
  @Override
  public void chargeElementsPartie(IGeometry geom) {
    if (!this.getPersistant()) {
      DataSet.LOGGER
          .warn("----- ATTENTION : Probleme au chargement du jeu de donnees "
              + this.getNom());
      DataSet.LOGGER
          .warn("----- Impossible de charger les elements d'un jeu de donnees non persistant");
      return;
    }
    // chargement recursif des dataset composants this
    for (IDataSet<?> DS : this.getComposants()) {
      if (DS.getPersistant()) {
        DS.chargeElementsPartie(geom);
      }
    }
    // chargement recursif des populations de this
    DataSet.LOGGER.info("###### Chargement des elements du DataSet "
        + this.getNom());
    for (IPopulation<? extends IFeature> pop : this.getPopulations()) {
      if (pop.getPersistant()) {
        pop.chargeElementsPartie(geom);
      }
    }
  }

  /**
   * Chargement des instances des populations persistantes d'un jeu de donn�es
   * qui intersectent une g�om�trie donn�e. ATTENTION: les tables qui stockent
   * les �l�ments doivent avoir �t� index�es dans Oracle. ATTENTION AGAIN:
   * seules les populations avec une g�om�trie sont charg�es.
   * 
   * @param zoneExtraction zone utilis�e pour l'extraction g�om�trique
   */
  @Override
  public void chargeElementsPartie(IExtraction zoneExtraction) {
    this.chargeElementsPartie(zoneExtraction.getGeom());
  }

  /**
   * m�thode de chargement pour les test. Elle est un peu tordue dans le
   * param�trage mais permet de ne charger que ce qu'on veut. Elle permet de
   * charger les instances des populations persistantes d'un jeu de données qui
   * : - intersectent une géom�trie donn�e (extraction g�om�trique), - ET qui
   * appartiennent � certains th�mes et populations pr�cis�s en entr�e.
   * 
   * @param geom d�finit la zone d'extraction.
   * @param themes d�finit les sous-DS du DS � charger. Pour le DS lui-m�me, et
   *          pour chaque sous-DS, on pr�cise �galement quelles populations sont
   *          charg�es. Ce param�tre est une liste de liste de String compos�e
   *          comme suit (si la liste est nulle on charge tout) :
   *          <ul>
   *          <li>1/ Le premier �l�ment est soit null (on charge alors toutes
   *          les populations directement sous le DS), soit une liste contenant
   *          les noms des populations directement sous le DS que l'on charge
   *          (si la liste est vide, on ne charge rien).
   *          <li>2/ Tous les autres �l�ments sont des listes (une pour chaque
   *          sous-DS) qui contiennent chacune d'abord le nom d'un sous-DS que
   *          l'on veut charger, puis soit rien d'autre si on charge toutes les
   *          populations du sous-DS, soit le nom des populations du sous-DS que
   *          l'on veut charger.
   *          </ul>
   *          <b>NB :</b> Attention aux majuscules et aux accents.
   *          <p>
   *          <b>EXEMPLE</b> de parametre themes pour un DS repr�sentant la
   *          BDCarto, et sp�cifiant qu'on ne veut charger que les troncon et
   *          les noeud du th�me routier, et les troncons du th�me hydro, mais
   *          tout le th�me ferr�.
   *          <p>
   *          <b>theme = {null, liste1, liste2, liste3}</b>, avec :
   *          <ul>
   *          <li>null car il n'y a pas de population directement sous le DS
   *          BDCarto,
   *          <li>liste1 = {"Routier","Tronçons de route", "Noeuds routier"},
   *          <li>liste2 = {"Hydrographie","Tronçons de cours d'eau"},
   *          <li>liste3 = {"ferr�"}.
   *          </ul
   */
  @Override
  public void chargeExtractionThematiqueEtSpatiale(IGeometry geom,
      List<List<String>> themes) {
    if (!this.getPersistant()) {
      DataSet.LOGGER
          .warn("----- ATTENTION : Probleme au chargement du jeu de donnees "
              + this.getNom());
      DataSet.LOGGER
          .warn("----- Impossible de charger les elements d'un jeu de donnees non persistant");
      return;
    }

    List<String> themeACharger, extraitThemes;
    List<List<String>> populationsACharger;
    List<String> populationsACharger2;
    Iterator<List<String>> itThemes;
    Iterator<String> itPopulationsACharger;
    boolean aCharger;

    // chargement recursif des dataset composants this
    for (IDataSet<?> DS : this.getComposants()) {
      populationsACharger = null;
      if (themes == null) {
        aCharger = true;
      } else {
        itThemes = themes.iterator();
        themeACharger = itThemes.next();
        if (!itThemes.hasNext()) {
          aCharger = true;
        } else {
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
      if (aCharger && DS.getPersistant()) {
        DS.chargeExtractionThematiqueEtSpatiale(geom, populationsACharger);
      }
    }
    /** chargement des populations de this (directement sous this) */
    if (themes == null) {
      populationsACharger2 = null;
    } else {
      itThemes = themes.iterator();
      populationsACharger2 = itThemes.next();
    }
    DataSet.LOGGER.info("###### Chargement des elements du DataSet "
        + this.getNom());
    for (IPopulation<? extends IFeature> pop : this.getPopulations()) {
      if (populationsACharger2 == null) {
        aCharger = true;
      } else {
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
        if (geom != null) {
          pop.chargeElementsPartie(geom);
        } else {
          pop.chargeElements();
        }
      }
    }
  }

  /**
   * Pour un jeu de donn�es persistant, d�truit le jeu de donn�es, ses th�mes et
   * ses objets populations.
   * <p>
   * <b>ATTENTION :</b> ne d�truit pas les �l�ments des populations (pour cela
   * vider les tables Oracle).
   */
  @Override
  public void detruitJeu() {
    if (!this.getPersistant()) {
      DataSet.LOGGER
          .warn("----- ATTENTION : Probleme � la destruction du jeu de donnees " + this.getNom()); //$NON-NLS-1$
      DataSet.LOGGER.warn("----- Le jeu de donn�es n'est pas persistant"); //$NON-NLS-1$
      return;
    }
    // destruction des populations de this
    for (IPopulation<? extends IFeature> pop : this.getPopulations()) {
      if (pop.getPersistant()) {
        pop.detruitPopulation();
      }
    }
    // destruction recursive des dataset composants this
    for (IDataSet<?> DS : this.getComposants()) {
      if (DS.getPersistant()) {
        DS.detruitJeu();
      }
    }
    // destruction des zones d'extraction associ�es � this
    for (IExtraction ex : this.getExtractions()) {
      DataSet.LOGGER.info("###### Destruction de la zone d'extraction "
          + ex.getNom());
      DataSet.db.deletePersistent(ex);
    }
    // destruction de this
    DataSet.LOGGER.info("###### Destruction du DataSet " + this.getNom());
    DataSet.db.deletePersistent(this);
  }

  /**
   * NB pour codeurs : laisser 'true' par d�faut. Sinon, comme cet attribut
   * n'est pas persistant, cela pose des probl�mes au chargement (un th�me
   * persistant charg� a son attribut persistant � false.
   */
  protected boolean persistant = true;

  /**
   * Bool�en sp�cifiant si le th�me est persistant ou non (vrai par d�faut).
   * <p>
   * <b>NB :</b> si un jeu de donn�es est non persistant, tous ses th�mes sont
   * non persistants. Mais si un jeu de donn�es est persistant, certains de ses
   * th�mes peuvent ne pas l'�tre.
   * <p>
   * <b>ATTENTION :</b> pour des raisons propres � OJB, m�me si la classe
   * DataSet est concr�te, il n'est pas possible de cr�er un objet PERSISTANT de
   * cette classe, il faut utiliser les sous-classes.
   * 
   * @return vrai si le jeu de donn� est persistant, faux sinon
   */
  @Override
  public boolean getPersistant() {
    return this.persistant;
  }

  /**
   * Bool�en sp�cifiant si le th�me est persistant ou non (vrai par d�faut).
   * <p>
   * <b>NB :</b> si un jeu de donn�es est non persistant, tous ses th�mes sont
   * non persistants. Mais si un jeu de donn�es est persistant, certains de ses
   * th�mes peuvent ne pas l'�tre.
   * <p>
   * <b>ATTENTION :</b> pour des raisons propres � OJB, m�me si la classe
   * DataSet est concr�te, il n'est pas possible de cr�er un objet PERSISTANT de
   * cette classe, il faut utiliser les sous-classes.
   * 
   * @param b vrai si le jeu de donn� est persistant, faux sinon
   */
  @Override
  public void setPersistant(boolean b) {
    this.persistant = b;
  }

  // /////////////////////////////////////////////////////
  // Metadonn�es
  // /////////////////////////////////////////////////////
  /**
   * Nom de la classe concr�te de this : pour OJB, ne pas manipuler directement
   */
  protected String ojbConcreteClass;

  @Override
  public String getOjbConcreteClass() {
    return this.ojbConcreteClass;
  }

  @Override
  public void setOjbConcreteClass(String S) {
    this.ojbConcreteClass = S;
  }

  /** Nom du jeu de donn�es */
  protected String nom = ""; //$NON-NLS-1$

  @Override
  public String getNom() {
    return this.nom;
  }

  @Override
  public void setNom(String S) {
    this.nom = S;
  }

  /** Type de BD (BDcarto, BDTopo...). */
  protected String typeBD = ""; //$NON-NLS-1$

  @Override
  public String getTypeBD() {
    return this.typeBD;
  }

  @Override
  public void setTypeBD(String S) {
    this.typeBD = S;
  }

  /** Mod�le utilis� (format shape, structur�...). */
  protected String modele = ""; //$NON-NLS-1$

  @Override
  public String getModele() {
    return this.modele;
  }

  @Override
  public void setModele(String S) {
    this.modele = S;
  }

  /** Zone G�ographique couverte. */
  protected String zone = ""; //$NON-NLS-1$

  @Override
  public String getZone() {
    return this.zone;
  }

  @Override
  public void setZone(String S) {
    this.zone = S;
  }

  /** Date des donn�es. */
  protected String date = ""; //$NON-NLS-1$

  @Override
  public String getDate() {
    return this.date;
  }

  @Override
  public void setDate(String S) {
    this.date = S;
  }

  /** Commentaire quelconque. */
  protected String commentaire = ""; //$NON-NLS-1$

  @Override
  public String getCommentaire() {
    return this.commentaire;
  }

  @Override
  public void setCommentaire(String S) {
    this.commentaire = S;
  }

  // /////////////////////////////////////////////////////
  // th�mes du jeu de donn�es
  // /////////////////////////////////////////////////////
  /**
   * Un DataSet se d�compose r�cursivement en un ensemble de DataSet. Le lien de
   * DataSet vers lui-m�me est un lien 1-n. Les m�thodes get (sans indice) et
   * set sont n�cessaires au mapping. Les autres m�thodes sont l� seulement pour
   * faciliter l'utilisation de la relation. ATTENTION: Pour assurer la
   * bidirection, il faut modifier les listes uniquement avec ces methodes. NB:
   * si il n'y a pas d'objet en relation, la liste est vide mais n'est pas
   * "null". Pour casser toutes les relations, faire setListe(new ArrayList())
   * ou emptyListe().
   */
  protected List<IDataSet<?>> composants = new ArrayList<IDataSet<?>>(0);

  @Override
  @OneToMany
  public List<IDataSet<?>> getComposants() {
    return this.composants;
  }

  @Override
  public void setComposants(List<IDataSet<?>> L) {
    this.emptyComposants();
    for (IDataSet<?> dataset : L) {
      dataset.setAppartientA(this);
    }
  }

  @Override
  public IDataSet<?> getComposant(int i) {
    return this.composants.get(i);
  }

  @Override
  public void addComposant(IDataSet<?> O) {
    if (O == null) {
      return;
    }
    this.composants.add(O);
    O.setAppartientA(this);
  }

  @Override
  public void removeComposant(IDataSet<?> O) {
    if (O == null) {
      return;
    }
    this.composants.remove(O);
    O.setAppartientA(null);
  }

  @Override
  public void emptyComposants() {
    List<IDataSet<?>> old = new ArrayList<IDataSet<?>>(this.composants);
    for (IDataSet<?> dataset : old) {
      dataset.setAppartientA(null);
    }
    this.composants.clear();
  }

  @Override
  public IDataSet<?> getComposant(String nomComposant) {
    for (IDataSet<?> dataset : this.getComposants()) {
      if (dataset.getNom().equals(nomComposant)) {
        return dataset;
      }
    }
    DataSet.LOGGER.warn("----- ATTENTION : DataSet composant #" + nomComposant
        + "# introuvable dans le DataSet " + this.getNom());
    return null;
  }

  /** Relation inverse � Composants */
  private IDataSet<?> appartientA;

  @Override
  @ManyToOne
  public IDataSet<?> getAppartientA() {
    return this.appartientA;
  }

  @Override
  public void setAppartientA(IDataSet<?> O) {
    IDataSet<?> old = this.appartientA;
    this.appartientA = O;
    if (old != null) {
      old.getComposants().remove(this);
    }
    if (O != null) {
      this.appartientAID = O.getId();
      if (!(O.getComposants().contains(this))) {
        O.getComposants().add(this);
      }
    } else {
      this.appartientAID = 0;
    }
  }

  private int appartientAID;

  @Override
  public void setAppartientAID(int I) {
    this.appartientAID = I;
  }

  @Override
  @Transient
  public int getAppartientAID() {
    return this.appartientAID;
  }

  /**
   * Liste des population du DataSet. Les m�thodes get (sans indice) et set sont
   * n�cessaires au mapping. Les autres m�thodes sont l� seulement pour
   * faciliter l'utilisation de la relation.
   * <p>
   * <b>ATTENTION :</b> Pour assurer la bidirection, il faut modifier les listes
   * uniquement avec ces methodes.
   * <p>
   * <b>NB :</b> si il n'y a pas d'objet en relation, la liste est vide mais
   * n'est pas "null". Pour casser toutes les relations, faire setListe(new
   * ArrayList()) ou emptyListe().
   */
  protected List<IPopulation<? extends IFeature>> populations = new ArrayList<IPopulation<? extends IFeature>>(
      0);

  @Override
  @OneToMany
  public List<IPopulation<? extends IFeature>> getPopulations() {
    return this.populations;
  }

  @Override
  public void setPopulations(List<IPopulation<? extends IFeature>> L) {
    List<IPopulation<? extends IFeature>> old = new ArrayList<IPopulation<? extends IFeature>>(
        this.populations);
    for (IPopulation<? extends IFeature> pop : old) {
      pop.setDataSet(null);
    }
    for (IPopulation<? extends IFeature> pop : L) {
      pop.setDataSet(this);
    }
  }

  @Override
  public IPopulation<? extends IFeature> getPopulation(int i) {
    return this.populations.get(i);
  }

  @Override
  public void addPopulation(IPopulation<? extends IFeature> O) {
    if (O == null) {
      return;
    }
    this.populations.add(O);
    O.setDataSet(this);
  }

  @Override
  public void removePopulation(IPopulation<? extends IFeature> O) {
    if (O == null) {
      return;
    }
    this.populations.remove(O);
    O.setDataSet(null);
  }

  @Override
  public void emptyPopulations() {
    List<IPopulation<? extends IFeature>> old = new ArrayList<IPopulation<? extends IFeature>>(
        this.populations);
    for (IPopulation<? extends IFeature> pop : old) {
      pop.setDataSet(null);
    }
    this.populations.clear();
  }

  @Override
  public IPopulation<? extends IFeature> getPopulation(String nomPopulation) {
    for (IPopulation<? extends IFeature> pop : this.getPopulations()) {
      if (nomPopulation.equals(pop.getNom())) {
        return pop;
      }
    }
    for (IDataSet<?> comp : this.composants) {
        IPopulation<? extends IFeature> pop = comp.getPopulation(nomPopulation);
        if (pop != null) {
            return pop;
        }
    }
    // if (logger.isDebugEnabled())
    // logger.debug("=============== ATTENTION : population '" + nom +
    // "' introuvable ==============");
    return null;
  }

  /** Liste des zones d'extraction d�finies pour ce DataSt */
  protected List<IExtraction> extractions = new ArrayList<IExtraction>();

  @Override
  // @OneToMany
  @Transient
  public List<IExtraction> getExtractions() {
    return this.extractions;
  }

  @Override
  public void setExtractions(List<IExtraction> L) {
    this.extractions = L;
  }

  @Override
  public void addExtraction(IExtraction O) {
    this.extractions.add(O);
  }

  // ////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * m�thodes permettant de cr�er un jeu de donn�es:
   * <ul>
   * <li>reli� � un produit, donc potentiellement � de nombreuses m�tadonn�es</li>
   * <li>reli� � 0 ou 1 sch�maConceptuelJeu (un sch�maConceptuelJeu est associ�
   * � 0 ou 1 jeu)</li>
   * <li>compos� de Populations dot�es de m�tadonn�es</li>
   * </ul>
   * Comme indiqu� dans la classe Population, les populations d'un DataSet ne
   * sont pas destin�es � �tre persistantes. Elles peuvent �tre initialis�es �
   * partir du sch�ma conceptuel, qui lui est persitent, grâce � la m�thode
   * DataSet.initPopulations()
   */
  // //////////////////////////////////////////////////////////////////////////////////////////////

  /** *reference statique au repository OJB */
  // public static MetadataManager metadataManager;
  /***************************************************************************
   * Partie Description du DataSet : produit et sch�ma de donn�es
   **************************************************************************/
  protected Produit produit;

  @Override
  // @OneToOne
  @Transient
  public Produit getProduit() {
    return this.produit;
  }

  public void setProduit(IProduct<?> produit) {
    this.produit = (Produit) produit;
  }

  /**
   * Schema conceptuel correspondant au jeu de donnees
   */
  protected SchemaConceptuelJeu schemaConceptuel;

  public void setSchemaConceptuel(SchemaConceptuelJeu schema) {
    this.schemaConceptuel = schema;
  }

  @Override
  // @OneToOne
  @Transient
  public SchemaConceptuelJeu getSchemaConceptuel() {
    return this.schemaConceptuel;
  }

  /**
   * Liste des contraintes (int�grit�) s'appliquant � ce jeu
   */
  public List<GF_Constraint> contraintes;

  @Override
  // @OneToMany
  @Transient
  public List<GF_Constraint> getContraintes() {
    return this.contraintes;
  }

  @Override
  public void setContraintes(List<GF_Constraint> contraintes) {
    this.contraintes = contraintes;
  }

  @Override
  public void initPopulations() {
    SchemaConceptuelJeu schema = this.getSchemaConceptuel();
    List<IPopulation<? extends IFeature>> listPop = new ArrayList<IPopulation<? extends IFeature>>();
    for (int i = 0; i < schema.getFeatureTypes().size(); i++) {
      listPop.add(new Population<IFeature>(schema.getFeatureTypeI(i)));
    }
    this.setPopulations(listPop);
  }

  @Override
  public IPopulation<? extends IFeature> getPopulationByFeatureTypeName(
      String nomFeatureType) {
    for (int i = 0; i < this.getPopulations().size(); i++) {
      if (this.getPopulations().get(i).getFeatureType() == null)
        continue;
      if (this.getPopulations().get(i).getFeatureType().getTypeName().equals(
          nomFeatureType)) {
        return this.getPopulations().get(i);
      }
    }
    DataSet.LOGGER
        .error("La Population " + nomFeatureType + " n'a pas �t� trouv�e."); //$NON-NLS-1$//$NON-NLS-2$
    return null;
  }

  private static DataSet dataSet = null;

  /**
   * @return une instance du singleton DataSet
   */
  public static DataSet getInstance() {
    if (DataSet.dataSet == null) {
      synchronized (DataSet.class) {
        if (DataSet.dataSet == null) {
          DataSet.dataSet = new DataSet();
        }
      }
    }
    return DataSet.dataSet;
  }

  @Override
  public String toString() {
    return "DataSet " + this.id;
  }
}
