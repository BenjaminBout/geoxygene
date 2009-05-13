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

package fr.ign.cogit.geoxygene.style;

/**
 * @author Julien Perret
 *
 */
public class TextSymbolizer extends AbstractSymbolizer {
	@Override
	public boolean isTextSymbolizer() {return true;}
	
	private String label;
	/**
	 * Renvoie la valeur de l'attribut label.
	 * @return la valeur de l'attribut label
	 */
	public String getLabel() {return this.label;}
	/**
	 * Affecte la valeur de l'attribut label.
	 * @param label l'attribut label � affecter
	 */
	public void setLabel(String label) {this.label = label;}
	
	private Font font;
	/**
	 * Renvoie la valeur de l'attribut font.
	 * @return la valeur de l'attribut font
	 */
	public Font getFont() {return this.font;}
	/**
	 * Affecte la valeur de l'attribut font.
	 * @param font l'attribut font � affecter
	 */
	public void setFont(Font font) {this.font = font;}
	
	private LabelPlacement labelPlacement;
	/**
	 * Renvoie la valeur de l'attribut labelPlacement.
	 * @return la valeur de l'attribut labelPlacement
	 */
	public LabelPlacement getLabelPlacement() {return this.labelPlacement;}
	/**
	 * Affecte la valeur de l'attribut labelPlacement.
	 * @param labelPlacement l'attribut labelPlacement � affecter
	 */
	public void setLabelPlacement(LabelPlacement labelPlacement) {this.labelPlacement = labelPlacement;}
	
	private Halo halo;
	/**
	 * Renvoie la valeur de l'attribut halo.
	 * @return la valeur de l'attribut halo
	 */
	public Halo getHalo() {return this.halo;}
	/**
	 * Affecte la valeur de l'attribut halo.
	 * @param halo l'attribut halo � affecter
	 */
	public void setHalo(Halo halo) {this.halo = halo;}
	
	private Fill fill;
	/**
	 * Renvoie la valeur de l'attribut fill.
	 * @return la valeur de l'attribut fill
	 */
	public Fill getFill() {return this.fill;}

	/**
	 * Affecte la valeur de l'attribut fill.
	 * @param fill l'attribut fill � affecter
	 */
	public void setFill(Fill fill) {this.fill = fill;}
}
