/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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

package fr.ign.cogit.geoxygene.dico;

/**
  * M�taclasse pour les classes de r�les composant des GF_AssociationType.
  * Les r�les sont des propri�t�s sur les Feature Types.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

public class GF_AssociationRole extends GF_PropertyType {
    
    /** Type de l'attribut.*/
    protected String valueType;   
    /** Renvoie le type de l'attribut. */
    public String getValueType () {return this.valueType;}
    /** Affecte un type � l'attribut. */
    public void setValueType(String ValueType) {this.valueType = ValueType;}
    
    
    /** Nombre de valeurs minimal de l'attribut */
    protected int cardMin;
    /** Renvoie le nombre de valeurs minimal de l'attribut. */
    public int getCardMin () {return this.cardMin;}
    /** Affecte un nombre de valeurs minimal � l'attribut. */
    public void setCardMin (int CardMin) {this.cardMin = CardMin;}
    
    
    /** Nombre de valeurs maximal de l'attribut */
    protected int cardMax;
    /** Renvoie le nombre de valeurs maximal de l'attribut. */
    public int getCardMax () {return this.cardMax;}
    /** Affecte un nombre de valeurs maximal � l'attribut. */
    public void setCardMax (int CardMax) {this.cardMax = CardMax;}
    
    
	/** Association type auquel est rattach� la propri�t�. */
	protected GF_AssociationType associationType;
	/** Renvoie le feature type auquel est rattach� la propri�t�. */
	public GF_AssociationType getAssociationType() {return this.associationType;}
	/** Affecte un feature type � la propri�t�. */
	public void setAssociationType(GF_AssociationType AssociationType) {
		this.associationType = AssociationType;
		if (! AssociationType.getRoles().contains(this))
			AssociationType.addRole(this);  // gestion de la bi-direction
	}
  
}
