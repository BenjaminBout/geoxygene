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

import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;

/** NON IMPLEMENTE.
  * Classe m�re pour les surfaces param�tr�es par des courbes.
  * 
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

class GM_ParametricCurveSurface extends GM_SurfacePatch {
    
    protected String horizontalCurveType;
    public String getHorizontalCurveType () {
        return this.horizontalCurveType;
    }

    protected String verticalCurveType;
    public String getVerticalCurveType () {
        return this.verticalCurveType;
    }


    public GM_Curve horizontalCurve( double t) {
        return null;
    }

    public GM_Curve verticalCurve( double s) {
        return null;
    }

    public DirectPosition surface(double s,double t) {
        return null;
    }
    

    // Impl�mentation d'une m�thode abstraite de GM_SurfacePatch.
    public GM_SurfacePatch reverse() {
        return null;
    }
    
}
