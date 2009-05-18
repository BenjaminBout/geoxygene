/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.feature;

import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.spatial.toporoot.TP_Object;

/**
 *
 * Feature g�n�rique.
 * Les attributs sont repr�sent�s dans une table et ne peuvent
 * pas �tre acc�d�s autrement -(pas de getter ou setter sp�cifique � un attribut
 * comme getNbVoies() pour les TronconRoute.java par exemple).
 * 
 * Un defaultFeature est cependant associ� � un FeatureType avec toutes les
 * descriptions des ses attributs, types de valeurs etc. C'est au d�veloppeur de
 * s'assurer que le defaultFeature reste conforme � la d�finition de son featureType.
 * Au premier chargement, s'il n'y a pas de featuretype renseign�, un nouveau featureType
 * est g�n�r� automatiquement gr�ce aux colonnes de la table. Mais cela ne constitue pas
 * un sch�ma conceptuel, il doit donc �tre pr�cis� manuellement d�s que possible pour
 * les utilisations ult�rieures (notamment pour identifier les relatios entre objets etc.)
 * 
 * Stockage et chargement par OJB : un seul mapping pour toutes les tables
 * 
 * Ou plus simple : chargement par JDBC en utilisant soit une connexion neuve soit la
 * java.sql.Connexion de Geodatabase (Geodatabase.getConnexion())
 * 
 * @author Sandrine Balley
 * @author Nathalie Abadie
 * @author Julien Perret
 */

public class DefaultFeature extends FT_Feature {
	/**
	 * Constructeur vide
	 */
	public DefaultFeature() {super();}
	/**
	 * Constructeur � partir d'une g�om�trie
	 * @param geometry g�om�trie de l'objet
	 */
	public DefaultFeature(GM_Object geometry) {super(geometry);}
	private FeatureType featureType;
	/**
	 * nom table et colonnes. contient une "lookup table" reliant le num�ro de l'attribut
	 * dans la table attributes[] du defaultFeature, son nom de colonne et son nom d'attributeType.
	 */
	private SchemaDefaultFeature schema;
	private Object[] attributes;
	/**
	 * Renvoie un tableau contenant les valeurs des attributs de l'objet
	 * @return un tableau contenant les valeurs des attributs de l'objet
	 */
	public Object[] getAttributes() {return attributes;}
	/**
	 * Renvoie l'attribut de position <code>n</code> dans le tableau d'attributs
	 * @param rang le rang de l'attribut
	 * @return l'attribut de position <code>n</code> dans le tableau d'attributs
	 */
	public Object getAttribute(int rang){return this.attributes[rang];}
	@Override
	public Object getAttribute(String nom){
		/**
		 * on regarde en priorit� si le nom correspond � un nom d'attributeType (m�tadonn�es de niveau conceptuel)
		 */
		String[] tabNoms;
		for(Integer key:getSchema().getAttLookup().keySet()) {
			tabNoms = this.getSchema().getAttLookup().get(key);
			if ((tabNoms!=null)&&(tabNoms[1]!=null)&&(tabNoms[1].equals(nom))) return this.getAttribute(key);
		}
		/**
		 * si on n'a pas trouv� au niveau conceptuel, on regarde s'il correspond � un nom de colonne (m�tadonn�es de niveau logique)
		 */
		for(Integer key:getSchema().getAttLookup().keySet()) {
			tabNoms = this.getSchema().getAttLookup().get(key);
			if ((tabNoms!=null)&&(tabNoms[0]!=null)&&(tabNoms[0].equals(nom))) return this.getAttribute(key);
		}
		if (logger.isDebugEnabled()) logger.warn("!!! le nom '"+nom+"' ne correspond pas � un attribut de ce feature !!!");
		return null;
	}
	@Override
	public Object getAttribute(AttributeType attribute) {return getAttribute(attribute.getMemberName());}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Object[] attributes) {this.attributes = attributes;}
	/**
	 * met la valeur value dans la case rang de la table d'attributs.
	 * Pour �viter toute erreur, mieux vaut utiliser setAttribute(String nom, Object value)
	 * qui va chercher dans le schema l'emplacement correct de l'attribut.
	 * @param rang
	 * @param value
	 */
	public void setAttribute(int rang, Object value){this.attributes[rang]=value;}
	/**
	 * Va voir dans la lookup table de feature.sch�ma dans quelle case se place l'attribut
	 * puis le met dans la table d'attributs.
	 * @param nom nom de l'attribut
	 * @param value valeur � affecter � l'attribut
	 */
	public void setAttribute(String nom, Object value){
		/**
		 * on regarde en priorit� si le nom correspond � un nom d'attributeType (m�tadonn�es de niveau conceptuel)
		 */
		String[] tabNoms;
		for(Integer key:getSchema().getAttLookup().keySet()) {
			tabNoms = this.getSchema().getAttLookup().get(key);
			if ((tabNoms!=null)&&(tabNoms[1]!=null)) {
				if (tabNoms[1].equals(nom)){
					this.setAttribute(key, value);
					return;
				}
			}			
		}
		/**
		 * si on n'a pas trouv� au niveau conceptuel, on regarde s'il correspond � un nom de colonne (m�tadonn�es de niveau logique)
		 */
		for(Integer key:getSchema().getAttLookup().keySet()) {
			tabNoms = this.getSchema().getAttLookup().get(key);
			if ( (tabNoms!=null) && (tabNoms[0]!=null) ) {
				if (tabNoms[0].equals(nom)){
					if (logger.isDebugEnabled()) logger.debug("setAttribute "+nom+" =?= "+tabNoms[0]);
					this.setAttribute(key, value);
					return;
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.warn("!!! le nom '"+nom+"' ne correspond pas � un attribut de ce feature !!!");
			for(Integer key:getSchema().getAttLookup().keySet()) {
				tabNoms = this.getSchema().getAttLookup().get(key);
				if (tabNoms==null) logger.debug("Attribut "+key+" nul");
				else logger.debug("Attribut "+key+" = "+tabNoms[0]+" - "+tabNoms[1]);
			}
		}
		return;
	}
	/**
	 * @return the featureType
	 */
	public FeatureType getScFeatureType() {return featureType;}
	/**
	 * @param featureType the featureType to set
	 */
	public void setScFeatureType(FeatureType featureType) {this.featureType = featureType;}
	/**
	 * @return the table
	 */
	public SchemaDefaultFeature getSchema() {return schema;}
	/**
	 * @param schema
	 */
	public void setSchema(SchemaDefaultFeature schema) {this.schema = schema;}
	@Override
	public void setAttribute(AttributeType attribute, Object valeur) {
		// FIXME changer le comportement !!!!
		if (attribute.getMemberName().equals("geom")) {
			logger.warn("WARNING : Pour affecter la primitive g�om�trique par d�faut, veuillez utiliser "
					+ "la m�thode FT_Feature.getGeom() et non pas MdFeature.getAttribute(AttributeType attribute)");
			this.setGeom((GM_Object) valeur);
		} else if (attribute.getMemberName().equals("topo")) {
			logger.warn("WARNING : Pour affecter la primitive topologique par d�faut, veuillez utiliser "
					+ "la m�thode FT_Feature.getTopo() et non pas MdFeature.getAttribute(AttributeType attribute)");
			this.setTopo((TP_Object) valeur);
		}
		else {
			this.setAttribute(attribute.getMemberName(), valeur);
			/*
			try {
				String nomFieldMaj2;
				if (attribute.getNomField().length() == 0) {nomFieldMaj2 = attribute.getNomField();}
				else {nomFieldMaj2 = Character.toUpperCase(attribute.getNomField().charAt(0))+attribute.getNomField().substring(1);}
				String nomSetFieldMethod = "set" + nomFieldMaj2;
				Method methodSetter = this.getClass().getDeclaredMethod(nomSetFieldMethod, valeur.getClass());
				// Method methodGetter =
				// this.getClass().getSuperclass().getDeclaredMethod(
				// nomGetFieldMethod,
				// null);
				valeur = methodSetter.invoke(this, valeur);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			*/
		}
	}
}
