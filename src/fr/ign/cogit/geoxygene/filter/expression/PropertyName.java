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

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;

/**
 * @author Julien Perret
 *
 */
public class PropertyName implements Expression {
	static Logger logger=Logger.getLogger(PropertyName.class.getName());

	/**
	 * 
	 */
	public PropertyName() {}
	/**
	 * @param name
	 */
	public PropertyName(String name) {this.setPropertyName(name);}
	
	private String propertyName;
	/**
	 * @return
	 */
	public String getPropertyName() {return propertyName;}
	/**
	 * @param propertyName
	 */
	public void setPropertyName(String propertyName) {this.propertyName=propertyName;}
	
	@Override
	public Object evaluate(Object object) {
		String getterName = "get"+propertyName.substring(0, 1).toUpperCase()+propertyName.substring(1);
		if (object instanceof FT_Feature) {
			FT_Feature feature = (FT_Feature) object;
			AttributeType type = new AttributeType();
			type.setNomField(propertyName);
			type.setMemberName(propertyName);
			Object resultat = feature.getAttribute(type);
			if (resultat instanceof Number)
				return new BigDecimal(((Number)resultat).doubleValue());
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
				//e.printStackTrace();
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
}
