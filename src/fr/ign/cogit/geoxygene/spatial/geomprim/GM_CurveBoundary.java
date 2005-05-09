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

package fr.ign.cogit.geoxygene.spatial.geomprim;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

/**
  * Fronti�re d'une courbe orient�e, d�finie par une r�f�rence vers un point initial 
  * et une r�f�rence vers un point final.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

public class GM_CurveBoundary extends GM_PrimitiveBoundary {
      
    /** Le point initial. */
    protected GM_Point startPoint;
    
    /** Renvoie le point initial.*/
    public GM_Point getStartPoint () {return this.startPoint;}

    /** Le point final.*/
    protected GM_Point endPoint;
    
    /** Renvoie le point final.*/
    public GM_Point getEndPoint () {return this.endPoint;}
    
    /** Constructeur par d�faut. */
    public GM_CurveBoundary () { }
    
    /** Constructeur � partir d'une GM_Curve. */
    // c'est ce qui est utilis� dans GM_OrientableCurve::boundary();
    public GM_CurveBoundary(GM_Curve c) {
        DirectPosition startPt = c.startPoint();
        startPoint = new GM_Point(startPt);
        DirectPosition endPt = c.endPoint();
        endPoint = new GM_Point(endPt);
    }
}
