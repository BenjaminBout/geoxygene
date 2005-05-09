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

package fr.ign.cogit.geoxygene.spatial.coordgeom;

/** NON IMPLEMENTE.
  * Courbe qui est � une distance constante d'une courbe de base.
  * 
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

class GM_OffsetCurve extends GM_CurveSegment {
    
    /**
      * Courbe de base � partir de laquelle est g�n�r�e self.
      */
    protected GM_CurveSegment baseCurve;
    public GM_CurveSegment getBaseCurve () {
        return this.baseCurve;
    }
    protected void setBaseCurve (GM_CurveSegment value) {
        this.baseCurve = value; 
    }
    public int cardBaseCurve () {
        if ( this.baseCurve == null ) return 0;
        else return 1;
    }

    /**
      * Distance de self � la courbe de base. (NORME : cet attribut est de type Length.)
      */
    protected double distance;
    public double getDistance () {
        return this.distance;
    }

    /**
      * Inutile en 2D.
      */
/*    protected Vecteur refDirection;
    public Vecteur getRefDirection () {
        return this.refDirection;
    }
*/    
    /** NON IMPLEMENTE. */
    // implemente une methode de GM_CurveSegment
    public DirectPositionList coord() {
        return null;
    }
    
    
    /** NON IMPLEMENTE. */
    // implemente une methode de GM_CurveSegment
    public GM_CurveSegment reverse() {
                return null;
    }
    
}
