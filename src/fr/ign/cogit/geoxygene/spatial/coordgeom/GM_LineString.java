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

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import java.util.List;

//import operateur.OpDirectPosition;

/**
  * Polyligne. 
  * L'attribut "interpolation" est �gal � "linear".
  * 
  * @author Thierry Badard & Arnaud Braun
  * @version 1.1
  * 
  * 19.02.2007 : correction de bug constructeur � partir d'une liste de DirectPosition
  *  
  */

public class GM_LineString extends GM_CurveSegment {
    
    //////////////////////////////////////////////////////////////////////////
    // Attribut "controlPoint" et m�thodes pour le traiter ///////////////////
    //////////////////////////////////////////////////////////////////////////
    /** Points pour le dessin de la polyligne : s�quence de DirectPosition. 
        Le premier point est le startPoint de la polyligne. */
    protected DirectPositionList controlPoint;
    
    /** Renvoie la liste conbtrolPoint. Equivalent de samplePoint() et de coord(). A laisser ?*/
    public DirectPositionList getControlPoint() {
        return controlPoint;
    }
                        
    /** Renvoie le DirectPosition de rang i. */
    public DirectPosition getControlPoint (int i) {
        return (DirectPosition)this.controlPoint.get(i);
    }
        
    /** Affecte un DirectPosition au i-�me rang de la liste. */
    public void setControlPoint (int i, DirectPosition value) {
        this.controlPoint.set(i, value);
    }
        
    /** Ajoute un DirectPosition en fin de liste */
    public void addControlPoint (DirectPosition value) {
        this.controlPoint.add(value);
    }
        
    /** Ajoute un DirectPosition au i-�me rang de la liste. */
   public void addControlPoint (int i, DirectPosition value) {
        this.controlPoint.add(i, value);
    }
        
    /** Efface de la liste le DirectPosition pass� en param�tre. */
    public void removeControlPoint (DirectPosition value)  {
       this.controlPoint.remove(value);
    }
        
    /** Efface le i-�me DirectPosition de la liste. */
    public void removeControlPoint (int i)  {
        this.controlPoint.remove(i);
    }
        
    /** Renvoie le nombre de DirectPosition. */
    public int sizeControlPoint () {
        return this.controlPoint.size();
    }
    

    

    //////////////////////////////////////////////////////////////////////////
    // Constructeurs /////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    /** Constructeur par d�faut.*/
    public GM_LineString() {
        super();
        this.segment.add(this);
        controlPoint = new DirectPositionList();
        interpolation = "linear";
    }
        

    /** Constructeur � partir d'une liste de DirectPosition.*/
    public GM_LineString(DirectPositionList points) {
        super();
        this.segment.add(this);
        controlPoint = new DirectPositionList();
        controlPoint.addAll(points);
        interpolation = "linear";        
    }
 
    //////////////////////////////////////////////////////////////////////////
    // M�thode de la norme ///////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    /** A FAIRE. Renvoie null.
      * D�compose une polyligne en une s�quence de segments.
      */
    public List asGM_LineSegment() {
        return null;
    }
    
                
    

    //////////////////////////////////////////////////////////////////////////
    // Impl�mentation de m�thodes abstraites /////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    /**  Renvoie la liste ordonn�e des points de contr�le (idem que coord()).   */
    public DirectPositionList coord() {
        return (this.controlPoint);
    }
    
    /** Renvoie un GM_CurveSegment de sens oppos�. */
    public GM_CurveSegment reverse() {
        GM_LineString result = new GM_LineString();
        int n = controlPoint.size();
        for (int i=0; i<n; i++) result.getControlPoint().add((DirectPosition)controlPoint.get(n-1-i));
        return result;
    }

}
