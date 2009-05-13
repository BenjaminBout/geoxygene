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

package fr.ign.cogit.geoxygene.spatial.geomcomp;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_CurveSegment;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_CurveBoundary;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;

/**
 * Complexe ayant toutes les propri�t�s g�om�triques d'une courbe.
 * C'est une liste de courbes orient�es (GM_OrientableCurve) de telle mani�re que le noeud final d'une courbe correspond au noeud initial de la courbe suivante dans la liste.
 * H�rite de GM_OrientableCurve, mais le lien n'appara�t pas explicitement (probl�me de double h�ritage en java). Les m�thodes et attributs ont �t� report�s.
 *
 *<P> ATTENTION : normalement, il faudrait remplir le set "element" (contrainte : toutes les primitives du generateur
 * sont dans le complexe). Ceci n'est pas impl�ment� pour le moment.
 *<P> A FAIRE AUSSI : iterateur sur "generator"
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 *
 */

public class GM_CompositeCurve extends GM_Composite {

	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Attribut "generator" et m�thodes pour le traiter ////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	/** Les GM_OrientableCurve constituant self. */
	protected List<GM_OrientableCurve> generator;

	/** Renvoie la liste des GM_OrientableCurve */
	public List<GM_OrientableCurve> getGenerator() {return generator;}

	/** Renvoie la GM_OrientableCurve de rang i */
	public GM_OrientableCurve getGenerator (int i) {return this.generator.get(i);}

	/** Affecte une GM_OrientableCurve au rang i. Attention : aucun contr�le de continuit� n'est effectu�. */
	public void setGenerator (int i, GM_OrientableCurve value) {this.generator.set(i, value);}

	/** Ajoute une GM_OrientableCurve en fin de liste. Attention : aucun contr�le de continuit� n'est effectu�. */
	public void addGenerator (GM_OrientableCurve value) {this.generator.add(value);}

	/** Ajoute une GM_OrientableCurve en fin de liste avec un contr�le de continuit� avec la tol�rance pass�e en param�tre.
	 * Envoie une exception en cas de probl�me. */
	public void addGenerator (GM_OrientableCurve value, double tolerance) throws Exception {
		DirectPosition pt1;
		DirectPosition pt2;
		if (generator.size() > 0) {
			GM_OrientableCurve laDerniereCourbe = this.getGenerator(generator.size()-1);
			pt1 = (laDerniereCourbe.boundary()).getEndPoint().getPosition();
			pt2 = (value.boundary()).getStartPoint().getPosition();
			if (pt1.equals(pt2,tolerance)) this.generator.add(value);
			else throw new Exception("Rupture de cha�nage avec la courbe pass�e en param�tre.");
		}
		else this.generator.add(value);
	}

	/** Ajoute une GM_OrientableCurve en fin de liste avec un contr�le de continuit� avec la tol�rance pass�e en param�tre.
	 * Eventuellement change le sens d'orientation de la courbe pour assurer la continuite.
	 * Envoie une exception en cas de probl�me. */
	public void addGeneratorTry (GM_OrientableCurve value, double tolerance) throws Exception {
		try {
			this.addGenerator(value,tolerance);
		} catch (Exception e1) {
			try {
				this.addGenerator(value.getNegative(),tolerance);
			} catch (Exception e2) {
				throw new Exception("Rupture de cha�nage avec la courbe pass�e en param�tre(apr�s avoir essay� les 2 orientations)");
			}
		}
	}

	/** Ajoute une GM_OrientableCurve au rang i. Attention : aucun contr�le de continuit� n'est effectu�. */
	public void addGenerator (int i, GM_OrientableCurve value) {this.generator.add(i, value);}

	/** Efface la (ou les) GM_OrientableCurve pass� en param�tre. Attention : aucun contr�le de continuit� n'est effectu�. */
	public void removeGenerator (GM_OrientableCurve value) throws Exception {
		if (this.generator.size() == 1) throw new Exception ( "Il n'y a qu'un objet dans l'association." );
		this.generator.remove(value);
	}

	/** Efface la GM_OrientableCurve de rang i. Attention : aucun contr�le de continuit� n'est effectu�. */
	public void removeGenerator (int i) throws Exception {
		if (this.generator.size() == 1) throw new Exception ( "Il n'y a qu'un objet dans l'association." );
		this.generator.remove(i);
	}

	/** Nombre de GM_OrientableCurve constituant self */
	public int sizeGenerator () {return this.generator.size();}




	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Constructeurs ///////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// les constructeurs sont calques sur ceux de GM_Curve
	/** Constructeur par d�faut */
	public GM_CompositeCurve() {
		generator = new ArrayList<GM_OrientableCurve>();
		primitive = new GM_Curve();
		proxy[0] = primitive;
		GM_OrientableCurve proxy1 = new GM_OrientableCurve();
		proxy1.orientation = -1;
		proxy1.proxy[0] = primitive;
		proxy1.proxy[1] = proxy1;
		proxy1.primitive = new GM_Curve(primitive);
		proxy[1] = proxy1;
	}

	/** Constructeur � partir d'une et d'une seule GM_OrientableCurve.
	 *  L'orientation vaut +1. */
	public GM_CompositeCurve(GM_OrientableCurve oCurve) {
		generator = new ArrayList<GM_OrientableCurve>();
		generator.add(oCurve);
		primitive = new GM_Curve();
		this.simplifyPrimitive();
		proxy[0] = primitive;
		GM_OrientableCurve proxy1 = new GM_OrientableCurve();
		proxy1.orientation = -1;
		proxy1.proxy[0] = primitive;
		proxy1.proxy[1] = proxy1;
		proxy1.primitive = new GM_Curve(primitive);
		proxy[1] = proxy1;
	}




	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Attributs et m�thodes h�rit�es de GM_OrientableCurve ////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// On simule l'heritage du modele en reportant les attributs et methodes
	// de GM_OrientableCurve
	// On n'a pas repris l'attribut "orientation" qui ne sert a rien ici.

	/** Primitive. Elle doit etre recalcul�e � chaque modification de self : fait dans getPrimitive(). */
	protected GM_Curve primitive;

	/** Renvoie la primitive de self. */
	// le calcul est fait en dynamique dans la methode privee simplifyPrimitve.
	public GM_Curve getPrimitive ()  {
		this.simplifyPrimitive();
		return this.primitive;
	}

	/**
	 * Attribut stockant les primitives orient�es de cette primitive.
	 * Proxy[0] est celle orient�e positivement.
	 * Proxy[1] est celle orient�e n�gativement.
	 * On acc�de aux primitives orient�es par getPositive() et getNegative().
	 */
	protected GM_OrientableCurve[] proxy = new GM_OrientableCurve[2];

	/** Renvoie la primitive orient�e positivement. */
	public GM_OrientableCurve getPositive() {
		this.simplifyPrimitive();
		return this.primitive;       // equivaut a return this.proxy[0]
	}

	/** Renvoie la primitive orient�e n�gativement. */
	public GM_OrientableCurve getNegative()  {
		this.simplifyPrimitive();
		return this.primitive.getNegative();
	}

	/** Red�finition de l'op�rateur "boundary" sur GM_OrientableCurve. Renvoie une GM_CurveBoundary, c'est-�-dire deux GM_Point.  */
	public GM_CurveBoundary boundary()  {
		this.simplifyPrimitive();
		return this.primitive.boundary();
	}




	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// M�thodes "validate" /////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// cette m�thode n'est pas dans la norme.
	/** V�rifie le cha�nage des composants. Renvoie TRUE s'ils sont cha�n�s, FALSE sinon. */
	public boolean validate(double tolerance) {
		for (int i=0; i<generator.size()-1; i++) {
			GM_OrientableCurve oCurve1 = generator.get(i);
			GM_Curve prim1 = oCurve1.getPrimitive();
			GM_OrientableCurve oCurve2 = generator.get(i+1);
			GM_Curve prim2 = oCurve2.getPrimitive();
			DirectPosition pt1 = prim1.endPoint();
			DirectPosition pt2 = prim2.startPoint();
			if (!pt1.equals(pt2,tolerance))
				return false;
		}
		return true;
	}

	/** Renvoie les coordonnees de la primitive.  */
	@Override
	public DirectPositionList coord() {
		return getPrimitive().coord();
	}

	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// M�thodes priv�es pour usage interne /////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	/** Calcule la primitive se self. */
	private void simplifyPrimitive()  {
		int n = generator.size();
		if (n > 0) {
			// vidage de la primitive
			GM_Curve prim = this.primitive;
			while (prim.sizeSegment() > 0)
				prim.removeSegment(0);
			for (int i=0; i<n; i++) {
				GM_OrientableCurve oCurve = generator.get(i);
				GM_Curve thePrimitive = oCurve.getPrimitive();
				for (int j=0; j<thePrimitive.sizeSegment(); j++) {
					GM_CurveSegment theSegment = thePrimitive.getSegment(j);
					(this.primitive).addSegment(theSegment);
				}
			}
		}
	}

}
