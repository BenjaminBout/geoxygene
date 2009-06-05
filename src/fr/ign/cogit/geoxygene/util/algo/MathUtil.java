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
package fr.ign.cogit.geoxygene.util.algo;

import java.util.List;

/**
 * Fonctions math�matiques utilitaires
 * @author Julien Perret
 */
public class MathUtil {

	/**
	 * Moyenne d'une liste
	 * @param liste liste de doubles
	 * @return moyenne des �l�ments d'une liste
	 */
	public static double moyenne(List<Double> liste) {
		if (liste.isEmpty()) return 0.0;
		double somme = 0.0;
		for(Double val:liste) somme+=val;
		return somme/liste.size();
	}

	/**
	 * Moyenne des carr�s d'une liste
	 * @param liste liste de doubles
	 * @return moyenne des carr�s des �l�ments d'une liste
	 */
	public static double moyenneCarres(List<Double> liste) {
		if (liste.isEmpty()) return 0.0;
		double somme = 0.0;
		for(Double val:liste) somme+=val*val;
		return somme/liste.size();
	}

	/**
	 * Ecart type d'une liste.
	 * Cette fonction ne prends que la liste des �l�ments comme param�tre.
	 * Si vous connaissez d�j� la moyenne des �l�ments de la liste, utilisez l'autre fonction et passez la en param�tre.
	 * @param liste liste de doubles
	 * @return �cart type des �l�ments d'une liste
	 */
	public static double ecartType(List<Double> liste) {
		if (liste.isEmpty()) return 0.0;
		return ecartType(liste,moyenne(liste));
	}

	/**
	 * Ecart type d'une liste.
	 * Cette fonction prend en param�tre la moyennes des �l�ments de la liste.
	 * Elle est essentiellement utilitaire mais peux servir si on connait d�j� la moyenne des �l�ments de la liste pass�e en param�tre.
	 * Sinon, passer par l'autre fonction.
	 * @param liste liste de doubles
	 * @param moyenne moyennes des �l�ments de la liste
	 * @return �cart type des �l�ments d'une liste
	 */
	public static double ecartType(List<Double> liste, double moyenne) {
		if (liste.isEmpty()) return 0.0;
		double somme=0.0;
		for(double d:liste) {double e=d-moyenne;somme+=e*e;}
		return Math.sqrt(somme/liste.size());
	}

	/**
	 * Minimum d'une liste
	 * @param liste liste de doubles
	 * @return plus petite valeur des �l�ments d'une liste
	 */
	public static double min(List<Double> liste) {
		if (liste.isEmpty()) return 0.0;
		double min=Double.MAX_VALUE;
		for(Double val:liste) min=Math.min(min, val);
		return min;
	}

	/**
	 * Maximum d'une liste
	 * @param liste liste de doubles
	 * @return plus grande valeur des �l�ments d'une liste
	 */
	public static double max(List<Double> liste) {
		if (liste.isEmpty()) return 0.0;
		double max=-Double.MAX_VALUE;
		for(Double val:liste) max=Math.max(max, val);
		return max;
	}

	/**
	 * La valeur m�diane d'une liste n'est d�finie que si la liste contient au moins un �l�ment.
	 * @param liste liste de doubles
	 * @return Valeur m�diane de la liste si elle n'est pas vide. 0 sinon.
	 */
	public static double mediane(List<Double> liste) {
		if (liste.isEmpty()) return 0.0;
		Double[] listeTriee = liste.toArray(new Double[0]);
		java.util.Arrays.sort(listeTriee);
		return listeTriee[liste.size()/2];
	}

}
