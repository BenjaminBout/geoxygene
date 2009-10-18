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

package fr.ign.cogit.geoxygene.filter.expression;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.feature.FT_Feature;

/**
 * @author Julien Perret
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "PropertyName")
public class PropertyName extends Expression {
	static Logger logger=Logger.getLogger(PropertyName.class.getName());

	/**
	 * 
	 */
	public PropertyName() {}
	/**
	 * @param name
	 */
	public PropertyName(String name) {this.setPropertyName(name);}
	
	@XmlMixed
    private String[] propertyName = new String[1];
	/**
	 * @return
	 */
	public String getPropertyName() {return propertyName[0];}
	/**
	 * @param propertyName
	 */
	public void setPropertyName(String propertyName) {this.propertyName[0]=propertyName;}
	
	@Override
	public Object evaluate(Object object) {
		String getterName = "get"+this.getPropertyName().substring(0, 1).toUpperCase()+this.getPropertyName().substring(1);
		if (object instanceof FT_Feature) {
			FT_Feature feature = (FT_Feature) object;
			Object resultat = feature.getAttribute(this.getPropertyName());
			if (resultat instanceof Number) return new BigDecimal(((Number)resultat).doubleValue());
			//if (resultat instanceof Boolean) return new BigDecimal(((Boolean)resultat).booleanValue()?0:1);
			return resultat;
		}
		Class<?> classe = object.getClass();
		while (!classe.equals(Object.class)) {
			try {
				Method getter = classe.getMethod(getterName, new Class<?>[0]);
				Object resultat = getter.invoke(object, new Object[0]);
				if (resultat instanceof Number) return new BigDecimal(((Number)resultat).doubleValue());
				return resultat;
			} catch (SecurityException e) {
				logger.error("La m�thode "+getterName+" n'est pas autoris�e sur la classe "+object.getClass()+" / "+classe);
			} catch (NoSuchMethodException e) {
				logger.error("La m�thode "+getterName+" n'existe pas dans la classe "+object.getClass()+" / "+classe);
			} catch (IllegalArgumentException e) {
				logger.error("Arguments ill�gaux pour la m�thode "+getterName+" de la classe "+object.getClass()+" / "+classe);
			} catch (IllegalAccessException e) {
				logger.error("Acc�s ill�gal � la m�thode "+getterName+" de la classe "+object.getClass()+" / "+classe);
			} catch (InvocationTargetException e) {
				logger.error("Probl�me pendant l'invocation de la m�thode "+getterName+" sur la classe "+object.getClass()+" / "+classe);
			}
			classe = classe.getSuperclass();
		}
		logger.error("On a �chou� sur l'objet "+object+" avec le getter "+getterName);
		return null;
	}
	
	@Override
	public String toString() {return getPropertyName();}
}
