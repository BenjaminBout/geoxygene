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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.index.SpatialIndex;

/**
 * Collection (liste) de FT_Feature. Peut porter un index spatial.
 * 
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Julien Perret
 * 
 * @composed 0 - n FT_Feature
 */
public class FT_FeatureCollection<Feat extends FT_Feature> implements Collection<Feat> {
	static Logger logger = Logger.getLogger(FT_FeatureCollection.class.getName());
	protected List<ChangeListener> listenerList = new ArrayList<ChangeListener>();
	/**
	 * Ajoute un {@link ChangeListener}.
	 * <p>
	 * Adds a {@link ChangeListener}.
	 * @param l le {@link ChangeListener} � ajouter. the {@link ChangeListener} to be added.
	 */
	public void addChangeListener(ChangeListener l) {
		if (listenerList==null) {
			if (logger.isDebugEnabled()) logger.debug("bizarre");
			listenerList = new ArrayList<ChangeListener>();
		}
		listenerList.add(l);
	}
	/**
	 * Pr�vient tous les {@link ChangeListener} enregistr�s qu'un 
	 * �v�nement a eu lieu.
	 * <p>
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created.
	 */
	public void fireActionPerformed(ChangeEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.toArray();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-1; i>=0; i-=1) {
			((ChangeListener)listeners[i]).stateChanged(e);
		}
	}
	/**
	 * Constructeur 
	 */
	public FT_FeatureCollection() {}
	/**
	 * Constructeur recopiant une autre collection. ATTENTION: ne recopie pas
	 * l'�ventuel index spatial.
	 * @param listeACopier collection � recopier
	 */
	public FT_FeatureCollection(FT_FeatureCollection<Feat> listeACopier) {
		this.setFlagGeom(listeACopier.getFlagGeom());
		this.setFlagTopo(listeACopier.flagTopo);
		this.getElements().addAll(listeACopier.getElements());
	}
	/** 
	 * Constructeur � partir d'une collection de FT_Feature 
	 * @param collectionACopier collection � recopier
	 */
	public FT_FeatureCollection(Collection<Feat> collectionACopier) {this.getElements().addAll(collectionACopier);}
	// ---------------------------------------
	// --- Indicateurs de geometrie et topo --
	// ---------------------------------------
	/**
	 * Boolean indiquant si les FT_Feature portent une geometrie (true par
	 * defaut).
	 */
	protected boolean flagGeom = true;
	/** 
	 * Boolean indiquant si les FT_Feature portent une geometrie.
	 * @return vrai si les FT_Feature portent une geometrie.
	 */
	public boolean getFlagGeom() {return flagGeom;}
	/** 
	 * Boolean indiquant si les FT_Feature portent une geometrie. 
	 * @return vrai si les FT_Feature portent une geometrie.
	 */
	public boolean hasGeom() {return flagGeom;}
	/** 
	 * Boolean indiquant si les FT_Feature portent une geometrie. 
	 * @param Geom nouveau flag
	 */
	public void setFlagGeom(boolean Geom) {flagGeom = Geom;}
	/**
	 * Boolean indiquant si les FT_Feature portent une topologie (false par
	 * defaut).
	 */
	protected boolean flagTopo = false;
	/** 
	 * Boolean indiquant si les FT_Feature portent une topologie. 
	 * @return vrai si les FT_Feature portent une topologie.
	 */
	public boolean hasTopo() {return flagTopo;}
	/** 
	 * Boolean indiquant si les FT_Feature portent une topologie.
	 * @param Topo nouveau flag
	 */
	public void setFlagTopo(boolean Topo) {flagTopo = Topo;}
	// ---------------------------------------
	// --- Accesseurs ------------------------
	// ---------------------------------------
	/** 
	 * La liste des <code>Feature</code>s composant this. 
	 */
	protected List<Feat> elements = new ArrayList<Feat>();
	/**
	 * Renvoie la liste de <code>Feature</code>s composant this.
	 * @return la liste de <code>Feature</code>s composant this.
	 */
	public List<Feat> getElements() {return this.elements;}
	/**
	 * Affecte une liste de <code>Feature</code>s � this, et met � jour le lien inverse.
	 * Attention detruit l'index spatial si celui existait. Il faut donc le
	 * reinitialiser si on souhaite l'utiliser.
	 * @param liste liste de <code>Feature</code>s � affecter
	 */
	@SuppressWarnings("unchecked")
	public void setElements(Collection<Feat> liste) {
		List<Feat> old = new ArrayList<Feat>(elements);
		for(Feat O:old) {
			elements.remove(O);
			O.getFeatureCollections().remove(this);			
		}
		for(Feat O:liste) {
			elements.add(O);
			if (!O.getFeatureCollections().contains(this))
				O.getFeatureCollections().add((FT_FeatureCollection<FT_Feature>) this);
		}
		if (isIndexed) removeSpatialIndex();
	}
	/** 
	 * Renvoie le i-eme element de la liste des composants de this.
	 * @param i indice de l'�l�ment � renvoyer
	 * @return le i-eme element de la liste des composants de this.
	 */
	public Feat get(int i) {return this.elements.get(i);}
	/**
	 * Ajoute un element a la liste des composants de this, et met � jour le
	 * lien inverse.
	 */
	@SuppressWarnings("unchecked")
	public boolean add(Feat value) {
		if (value == null) return false;
		boolean result = this.elements.add(value);
		result = value.getFeatureCollections().add((FT_FeatureCollection<FT_Feature>) this) && result;
		if (isIndexed&&spatialindex.hasAutomaticUpdate()) spatialindex.update(value, +1);
		this.fireActionPerformed(new ChangeEvent(this));
		return result;
	}
	/**
	 * Ajoute les �l�ments d'une FT_FeatureCollection a la liste des composants
	 * de this, et met � jour le lien inverse.
	 */
	public void addCollection(FT_FeatureCollection<Feat> value) {
		if (value == null) return;
		for(Feat element:value.elements) this.add(element);
	}
	/**
	 * Efface de la liste l'element passe en parametre. Attention, si l'�l�ment
	 * est persistant, celui-ci n'est pas d�truit, le faire apr�s au besoin.
	 */
	public boolean remove(Feat value) {
		if (value == null) return false;
		boolean result = this.elements.remove(value);
		value.getFeatureCollections().remove(this);
		if (isIndexed && spatialindex.hasAutomaticUpdate())
			spatialindex.update(value, -1);
		//if (!result&&logger.isDebugEnabled()) logger.debug("La suppression de l'objet a �chou�e : "+value);
		return result;
	}
	/**
	 * Efface de la liste tous les �lements de la collection pass�e en
	 * param�tre. Attention, si l'�l�ment est persistant, celui-ci n'est pas
	 * d�truit, le faire apr�s au besoin.
	 */
	@Override
	public boolean removeAll(Collection<?> coll) {
		if (coll == null) return false;
		if (coll.size() == 0) return false;
		boolean result = true;
		for (Object o : coll) result = this.remove(o) && result;
		return result;
	}
	/**
	 * Efface toute la liste. Detruit l'index spatial si celui existe.
	 * 
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		for(Feat O:this) O.getFeatureCollections().remove(this);
		this.elements.clear();
		if (isIndexed) removeSpatialIndex();
	}
	@Override
	public int size() {return this.elements.size();}
	// ---------------------------------------
	// --- Calcul de l'emprise ---------------
	// ---------------------------------------
	/** Calcul l'emprise rectangulaire des geometries de la collection. */
	public GM_Envelope envelope() {
		if (this.hasGeom()) return this.getGeomAggregate().envelope();
		logger.warn("ATTENTION appel de envelope() sur une FT_FeatureCollection sans geometrie ! (renvoie null) ");
		return null;
	}
	/**
	 * Renvoie toutes les geometries sous la forme d'un GM_Aggregate.
	 * @return toutes les geometries sous la forme d'un GM_Aggregate.
	 */
	public GM_Aggregate<GM_Object> getGeomAggregate() {
		if (this.hasGeom()) {
			GM_Aggregate<GM_Object> aggr = new GM_Aggregate<GM_Object>();
			for(Feat f:this) aggr.add(f.getGeom());
			return aggr;
		}
		logger.warn("ATTENTION appel de getGeom() sur une FT_FeatureCollection sans geometrie ! (renvoie null) ");
		return null;
	}
	private DirectPosition center=null;
	/**
	 * Renvoie la position du centre de l'envelope de la collection.
	 * @return la position du centre de l'envelope de la collection
	 */
	public DirectPosition getCenter() {
		if (center==null) center = envelope().center(); 
		return this.center;
	}
	/**
	 * Affecte la position du centre de l'envelope de la collection.
	 * @param center la position du centre de l'envelope de la collection
	 */
	public void setCenter(DirectPosition center) {this.center = center;}
	// ---------------------------------------
	// --- Index spatial ---------------------
	// ---------------------------------------
	/** Index spatial. */
	private SpatialIndex<Feat> spatialindex;
	/** La collection possede-t-elle un index spatial ? */
	private boolean isIndexed = false;
	/**
	 * Index spatial.
	 * @return l'index spatial
	 */
	public SpatialIndex<Feat> getSpatialIndex() {return spatialindex;}
	/**
	 * La collection possede-t-elle un index spatial ?
	 * @return vrai si la collection poss�de un index spatial, faux sinon
	 */
	public boolean hasSpatialIndex() {return isIndexed;}
	/**
	 * Initialise un index spatial avec d�termination automatique des
	 * param�tres. Le boolean indique si on souhaite une mise a jour automatique
	 * de l'index.
	 * @param spatialIndexClass
	 *            Nom de la classe d'index.
	 * @param automaticUpdate
	 *            Sp�ciifie si l'index doit �tre mis � jour automatiquement
	 *            quand on modifie les objets de fc.
	 */
	@SuppressWarnings("unchecked")
	public void initSpatialIndex(Class<?> spatialIndexClass, boolean automaticUpdate) {
		if (!this.hasGeom()) {
			logger.warn("Attention initialisation d'index sur une liste ne portant pas de geometrie !");
			return;
		}
		try {
			spatialindex = (SpatialIndex<Feat>) spatialIndexClass.getConstructor(new Class[] {FT_FeatureCollection.class,Boolean.class}).newInstance(new Object[] {this, new Boolean(automaticUpdate)});
			isIndexed = true;
		} catch (Exception e) {
			logger.error("Probleme a l'initialisation de l'index spatial !");
			e.printStackTrace();
		}
	}
	/**
	 * Initialise un index spatial avec un parametre entier (utilise pour le
	 * dallage). Le boolean indique si on souhaite une mise a jour automatique
	 * de l'index.
	 * 
	 * @param spatialIndexClass
	 *            Nom de la classe d'index.
	 * @param automaticUpdate
	 *            Sp�ciifie si l'index doit �tre mis � jour automatiquement
	 *            quand on modifie les objets de fc.
	 * @param i
	 *            Nombre de dalles en X et en Y, du dallage.
	 */
	@SuppressWarnings("unchecked")
	public void initSpatialIndex(Class<?> spatialIndexClass, boolean automaticUpdate, int i) {
		if (!this.hasGeom()) {
			logger.warn("Attention initialisation d'index sur une liste ne portant pas de geometrie !");
			return;
		}
		try {
			spatialindex = (SpatialIndex<Feat>) spatialIndexClass.getConstructor(new Class[] { FT_FeatureCollection.class,Boolean.class, Integer.class }).newInstance(new Object[] { this, new Boolean(automaticUpdate), new Integer(i) });
			isIndexed = true;
		} catch (Exception e) {
			logger.error("Probleme a l'initialisation de l'index spatial !");
			e.printStackTrace();
		}
	}
	/**
	 * Initialise un index spatial d'une collection de FT_Feature, en prenant
	 * pour param�tre les limites de la zone et un entier (pour le dallage, cet
	 * entier est le nombre en X et Y de cases souhait�es sur la zone).
	 * 
	 * @param spatialIndexClass
	 *            Nom de la classe d'index.
	 * 
	 * @param automaticUpdate
	 *            Sp�ciifie si l'index doit �tre mis � jour automatiquement
	 *            quand on modifie les objets de fc.
	 * 
	 * @param enveloppe
	 *            Enveloppe d�crivant les limites de l'index spatial. NB: Tout
	 *            objet hors de ces limites ne sera pas trait� lors des requ�tes
	 *            spatiales !!!!!
	 * 
	 * @param i
	 *            Nombre de dalles en X et en Y, du dallage.
	 */
	@SuppressWarnings("unchecked")
	public void initSpatialIndex(Class<?> spatialIndexClass, boolean automaticUpdate, GM_Envelope enveloppe, int i) {
		if (!this.hasGeom()) {
			logger.warn("Attention initialisation d'index sur une liste ne portant pas de geometrie !");
			return;
		}
		try {
			spatialindex = (SpatialIndex<Feat>) spatialIndexClass
					.getConstructor(
							new Class[] { FT_FeatureCollection.class,
									Boolean.class, GM_Envelope.class,
									Integer.class }).newInstance(
							new Object[] { this, new Boolean(automaticUpdate),
									enveloppe, new Integer(i) });
			isIndexed = true;
		} catch (Exception e) {
			logger.error("Probleme a l'initialisation de l'index spatial !");
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialise un index spatial d'une collection de FT_Feature, en prenant
	 * pour param�tre ceux d'un index existant.
	 * 
	 * @param spIdx
	 *            un index spatial existant
	 */
	@SuppressWarnings("unchecked")
	public void initSpatialIndex(SpatialIndex<?> spIdx) {
		// enlev� : Class spatialIndexClass,
		if (!this.hasGeom()) {
			logger.warn("Attention initialisation d'index sur une liste ne portant pas de geometrie !");
			return;
		}
		try {
			spatialindex = spIdx.getClass()
					.getConstructor(
							new Class[] { FT_FeatureCollection.class,
									spIdx.getClass() }).newInstance(
							new Object[] { this, spIdx });
			isIndexed = true;
		} catch (Exception e) {
			logger.error("Probleme a l'initialisation de l'index spatial !");
			e.printStackTrace();
		}
	}
	
	/**
	 * D�truit l'index spatial.
	 */
	public void removeSpatialIndex() {
		spatialindex = null;
		isIndexed = false;
	}
	// ---------------------------------------
	// --- SELECTION AVEC L'Index spatial ----
	// ---------------------------------------
	/**
	 * Selection dans le carre dont P est le centre, de cote D.
	 * 
	 * @param P
	 *            le centre
	 * @param D
	 *            cote
	 * @return objets qui intersectent le carre dont P est le centre, de cote D.
	 */
	public FT_FeatureCollection<Feat> select(DirectPosition P, double D) {
		if (!isIndexed) {
			logger.warn("select() sur FT_FeatureCollection : l'index spatial n'est pas initialise (renvoie null)");
			return null;
		}
		return spatialindex.select(P, D);
	}
	
	/**
	 * Selection dans un rectangle.
	 * 
	 * @param env
	 *            rectangle
	 * @return objets qui intersectent un rectangle
	 */
	public FT_FeatureCollection<Feat> select(GM_Envelope env) {
		if (!isIndexed) {
			logger.warn("select() sur FT_FeatureCollection : l'index spatial n'est pas initialise (renvoie null)");
			return null;
		}
		return spatialindex.select(env);
	}
	
	/**
	 * Selection des objets qui intersectent un objet geometrique quelconque.
	 * 
	 * @param geometry
	 *            geometrie quelconque
	 * @return objets qui intersectent un objet geometrique quelconque.
	 */
	public FT_FeatureCollection<Feat> select(GM_Object geometry) {
		if (!isIndexed) {
			logger.warn("select() sur FT_FeatureCollection : l'index spatial n'est pas initialise (renvoie null)");
			return null;
		}
		return spatialindex.select(geometry);
	}
	
	/**
	 * Selection des objets qui croisent ou intersectent un objet geometrique
	 * quelconque.
	 * 
	 * @param geometry
	 *            un objet geometrique quelconque
	 * @param strictlyCrosses
	 *            Si c'est TRUE : ne retient que les objets qui croisent (CROSS
	 *            au sens JTS). Si c'est FALSE : ne retient que les objets qui
	 *            intersectent (INTERSECT au sens JTS) Exemple : si 1 ligne
	 *            touche "geometry" juste sur une extr�mit�, alors avec TRUE
	 *            cela ne renvoie pas la ligne, avec FALSE cela la renvoie
	 * @return objets qui intersectent strictement un objet geometrique
	 *         quelconque
	 */
	public FT_FeatureCollection<Feat> select(GM_Object geometry, boolean strictlyCrosses) {
		if (!isIndexed) {
			logger.warn("select() sur FT_FeatureCollection : l'index spatial n'est pas initialise (renvoie null)");
			return null;
		}
		return spatialindex.select(geometry, strictlyCrosses);
	}
	
	/**
	 * Selection a l'aide d'un objet geometrique quelconque et d'une distance.
	 * 
	 * @param geometry
	 *            geometrie quelconque.
	 * @param distance
	 *            distance maximum
	 * @return objets � moins d'une certaine distance d'un objet geometrique
	 *         quelconque.
	 */
	public FT_FeatureCollection<Feat> select(GM_Object geometry, double distance) {
		if (!isIndexed) {
			logger.warn("select() sur FT_FeatureCollection : l'index spatial n'est pas initialise (renvoie null)");
			return null;
		}
		return spatialindex.select(geometry, distance);
	}
	
	// ---------------------------------------
	// --- M�thodes n�cessaire pour impl�menter l'interface Collection
	// ---------------------------------------	
	/**
	 * Encapsulation de la methode contains() avec typage
	 * 
	 * @param value
	 *            valeur
	 * @return vrai si la collection contient la valeur, faux sinon
	 */
	public boolean contains(Feat value) {return this.elements.contains(value);}
	/**
	 * Ajoute un element a la liste des composants de this s'il n'est pas d�j�
	 * pr�sent, et met � jour le lien inverse.
	 * 
	 * @param feature
	 *            �l�ment � ajouter
	 */
	@SuppressWarnings("unchecked")
	public void addUnique(Feat feature) {
		if (feature == null) return;
		if (this.elements.contains(feature)) return;
		this.elements.add(feature);
		feature.getFeatureCollections().add((FT_FeatureCollection<FT_Feature>) this);
		if (isIndexed)
			if (spatialindex.hasAutomaticUpdate())
				spatialindex.update(feature, +1);
	}
	/**
	 * Efface de la liste l'element en position i. Attention, si l'�l�ment est
	 * persistant, celui-ci n'est pas d�truit, le faire apr�s au besoin.
	 * 
	 * @param i
	 *            indice de l'�l�ment � supprimer
	 */
	public void remove(int i) {
		if (i > this.size()) return;
		Feat value = this.get(i);
		this.elements.remove(value);
		value.getFeatureCollections().remove(this);
		if (isIndexed)
			if (spatialindex.hasAutomaticUpdate())
				spatialindex.update(value, -1);
	}
	/**
	 * Efface de la liste la collection pass�e en parametre. Attention, si
	 * l'�l�ment est persistant, celui-ci n'est pas d�truit, le faire apr�s au
	 * besoin.
	 * 
	 * @param value
	 *            collection d'�l�ments � effacer
	 */
	public void removeCollection(FT_FeatureCollection<Feat> value) {
		if (value == null) return;
		for(Feat f:value.getElements()) remove(f);
		}
	/**
	 * Ajoute les �l�ments d'une FT_FeatureCollection a la liste des composants
	 * de this, et met � jour le lien inverse.
	 * 
	 * @param value
	 *            collection d'�l�ments � ajouter
	 */
	public void addUniqueCollection(FT_FeatureCollection<? extends Feat> value) {
		Feat elem;
		if (value == null)
			return;
		Iterator<? extends Feat> iter = value.elements.iterator();
		while (iter.hasNext()) {
			elem = iter.next();
			this.addUnique(elem);
		}
	}
	@Override
	public Iterator<Feat> iterator() {return this.elements.iterator();}
	@Override
	public boolean contains(Object obj) {return this.elements.contains(obj);}
	@Override
	public boolean containsAll(Collection<?> coll) {
		Iterator<?> i = coll.iterator();
		while (i.hasNext()) {
			Object element = i.next();
			if (!contains(element))
				return false;
		}
		return true;
	}
	@Override
	public boolean isEmpty() {return this.elements.isEmpty();}
	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object obj) {return this.remove((Feat)obj);}
	@Override
	public boolean retainAll(Collection<?> coll) {
		List<Feat> toRemove = new ArrayList<Feat>();
		for (Feat feature : this.elements) {
			if (!coll.contains(feature))
				toRemove.add(feature);
		}
		return removeAll(toRemove);
	}
	@Override
	public Object[] toArray() {return this.elements.toArray();}
	@Override
	public boolean addAll(Collection<? extends Feat> c) {
		if (c == null) return false;
		Feat elem;
		Iterator<? extends Feat> iter = c.iterator();
		boolean result = true;
		while (iter.hasNext()) {
			elem = iter.next();
			result = add(elem) && result;
		}
		return result;
	}
	@Override
	public <T> T[] toArray(T[] a) {return this.elements.toArray(a);}
	// ---------------------------------------
	// --- M�thodes contenant les FeatureTypes
	// ---------------------------------------		
	/** Classe par d�faut des instances de la population.
	 *  Ceci est utile pour pouvoir savoir dans quelle classe cr�er de nouvelles instances.
	 */
	protected Class<Feat> classe;
	public Class<Feat> getClasse() {return classe; }
	public void setClasse (Class<Feat> C) {
		classe = C;
		this.nomClasse = classe.getName();
	}

	/** Nom complet (package+classe java) de la classe par d�faut des instances de la population.
	 *  Pertinent uniquement pour les population peristantes.
	 */
	protected String nomClasse = "";
	/** D�finit le nom complet (package+classe java) de la classe par d�faut des instances de la population.
	 *  CONSEIL : ne pas utiliser cette m�thode directement, remplir en utilisant setClasse().
	 *  Ne met pas � jour l'attribut classe.
	 *  Utile uniquement pour les population peristantes.
	 */
	public void setNomClasse (String S) {nomClasse = S;}
    /** 
     * R�cup�re le nom complet (package+classe java) de la classe par d�faut des instances de la population.
     * <p>
	 * Pertinent uniquement pour les population peristantes.
	 * <p>
	 * surcharge Population.getNomClasse() en passant par le featureType
	 */
	@Transient
	public String getNomClasse() {
		if (this.getFeatureType() != null) return this.getFeatureType().getNomClasse();
		return nomClasse;
	}
	/**
	 * M�thodes permettant de manipuler une population normale,
	 * avec lien vers le FeatureType correspondant. Ce type de Population n'a
	 * pas besoin d'�tre persistent (il peut l'�tre cependant) car les
	 * m�tadonn�es qu'il repr�sentent sont d�j� stock�es dans le sch�ma
	 * conceptuel du jeu.
	 */
	protected FeatureType featureType;
	/**
	 * @return Returns the featureType.
	 */
	@ManyToOne
	public FeatureType getFeatureType() {return featureType;}
	/**
	 * @param featureType The featureType to set.
	 */
	public void setFeatureType(FeatureType featureType) {this.featureType = featureType;}
}
