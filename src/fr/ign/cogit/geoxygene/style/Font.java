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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

/**
 * @author Julien Perret
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Font {
	
	private String fontFamily = "Default";
	/**
	 * Renvoie la valeur de l'attribut fontFamily.
	 * @return la valeur de l'attribut fontFamily
	 */
	public String getFontFamily() {return this.fontFamily;}
	/**
	 * Affecte la valeur de l'attribut fontFamily.
	 * @param fontFamily l'attribut fontFamily � affecter
	 */
	public void setFontFamily(String fontFamily) {this.fontFamily = fontFamily;}
	
	private int fontStyle = java.awt.Font.PLAIN;
	/**
	 * Renvoie la valeur de l'attribut fontStyle.
	 * @return la valeur de l'attribut fontStyle
	 */
	public int getFontStyle() {return this.fontStyle;}
	/**
	 * Affecte la valeur de l'attribut fontStyle.
	 * @param fontStyle l'attribut fontStyle � affecter
	 */
	public void setFontStyle(int fontStyle) {this.fontStyle = fontStyle;}
	
	private int fontWeight = java.awt.Font.PLAIN;
	/**
	 * Renvoie la valeur de l'attribut fontWeight.
	 * @return la valeur de l'attribut fontWeight
	 */
	public int getFontWeight() {return this.fontWeight;}
	/**
	 * Affecte la valeur de l'attribut fontWeight.
	 * @param fontWeight l'attribut fontWeight � affecter
	 */
	public void setFontWeight(int fontWeight) {this.fontWeight = fontWeight;}
	
	private int fontSize = 10;
	/**
	 * Renvoie la valeur de l'attribut fontSize.
	 * @return la valeur de l'attribut fontSize
	 */
	public int getFontSize() {return this.fontSize;}
	/**
	 * Affecte la valeur de l'attribut fontSize.
	 * @param fontSize l'attribut fontSize � affecter
	 */
	public void setFontSize(int fontSize) {this.fontSize = fontSize;}

    @XmlElements({
        @XmlElement(name = "SvgParameter", type = SvgParameter.class),
        @XmlElement(name = "CssParameter", type = SvgParameter.class)
    })
	private List<SvgParameter> svgParameters = new ArrayList<SvgParameter>();
	/**
	 * Renvoie la valeur de l'attribut cssParameters.
	 * @return la valeur de l'attribut cssParameters
	 */
	public List<SvgParameter> getSvgParameters() {return this.svgParameters;}
	/**
	 * Affecte la valeur de l'attribut cssParameters.
	 * Four types of CssParameter are allowed, 'font-family', 'font-style', 'font-weight', and 'font-size'.
	 * (cf. OGC 02-070 p.46)
	 * @param svgParameters l'attribut cssParameters � affecter
	 */
	public void setSvgParameters(List<SvgParameter> svgParameters) {
		this.svgParameters = svgParameters;
		for (SvgParameter parameter:svgParameters) {
			if (parameter.getName().equalsIgnoreCase("font-family")) {
				this.setFontFamily(parameter.getValue());
			} else if (parameter.getName().equalsIgnoreCase("font-style")) {
				this.setFontStyle(Integer.parseInt(parameter.getValue()));
			} else if (parameter.getName().equalsIgnoreCase("font-weight")) {
				this.setFontWeight(Integer.parseInt(parameter.getValue()));
			} else if (parameter.getName().equalsIgnoreCase("font-size")) {
				this.setFontSize(Integer.parseInt(parameter.getValue()));
			}
		}
	}

	/**
	 * @return une police AWT �quivalent � la police courante
	 */
	public java.awt.Font toAwfFont() {
		return new java.awt.Font(getFontFamily(),getFontStyle()|getFontWeight(),getFontSize());
//		new java.awt.Font("Default",java.awt.Font.PLAIN,10);
	}
}
