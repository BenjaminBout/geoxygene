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

package fr.ign.cogit.geoxygene.util.browser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

/**
  * Cette classe fournit l'impl�mentation de l'�couteur d'�v�nement pour les objets cliquables de type m�thode.
  * <BR/>Elle permet l'invocation g�n�rique des m�thodes, sans param�tres, en utilisant le package reflection du J2SDK. 
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

public class ObjectBrowserMethodListener implements ActionListener {
	
	/** Objet porteur de la m�thode qui doit �tre invoqu�e.*/
	private Object obj;
	/** M�thode qui doit �tre invoqu�e.*/
	private Method method;
	/** Nom de la m�thode qui doit �tre invoqu�e.*/
	private String methodName;

	/**
	 * Constructeur principal de l'�couteur d'�v�nement ObjectBrowserMethodListener.
	 * 
	 * @param obj l'objet porteur de la m�thode qui doit �tre invoqu�e.
	 * @param method la m�thode qui doit �tre invoqu�e.
	 */
	public ObjectBrowserMethodListener(Object obj, Method method) {
		this.obj = obj;
		this.method = method;
		this.methodName = method.getName();
	}

	/**
	 * Red�finition de la m�thode actionPerformed() fournie par l'interface ActionListener, afin de d�clencher 
	 * l'affichage de l'argument de retour de la m�thode (instance de la classe ObjectBrowserPrimitiveFrame).
	 */
	public void actionPerformed(ActionEvent e) {
		
		Object[] nulObjArray = {};
		Class methodReturnType = this.method.getReturnType();
		String returnedStringValue;

		if ((methodReturnType.getName() == "java.lang.String")
			|| (methodReturnType.isPrimitive())) {
			try {
				try {
					returnedStringValue =
						(this.method.invoke(this.obj, nulObjArray)).toString();
					ObjectBrowserPrimitiveFrame result =
						new ObjectBrowserPrimitiveFrame(
							methodName,
							returnedStringValue);
				} catch (NullPointerException npex) {
					ObjectBrowserNullPointerFrame nullFrame =
						new ObjectBrowserNullPointerFrame();
				}
			} catch (Exception ex) {
				ObjectBrowserIllegalAccessFrame illegalAccessFrame =
					new ObjectBrowserIllegalAccessFrame();
				//ex.printStackTrace();
			}
		} else {
			try {
				try {
					ObjectBrowser.browse(
						this.method.invoke(this.obj, nulObjArray));
				} catch (NullPointerException npex) {
					ObjectBrowserNullPointerFrame nullFrame =
						new ObjectBrowserNullPointerFrame();
				}
			} catch (Exception ex) {
				ObjectBrowserIllegalAccessFrame illegalAccessFrame =
					new ObjectBrowserIllegalAccessFrame();
				//ex.printStackTrace();
			}
		}

	}
}