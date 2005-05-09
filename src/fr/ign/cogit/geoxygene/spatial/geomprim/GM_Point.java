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
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
  * Classe pour un objet g�om�trique constitu� de un point, et seulement un.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */


public class GM_Point extends GM_Primitive {
    
    /** DirectPosition du point (DirectPosition �tant la classe stockant les coordonn�es). */
    protected DirectPosition position;
        
    /** Renvoie le DirectPosition du point. */
    public DirectPosition getPosition () {return this.position;}
    
    /** Affecte un DirectPosition au point. Le DirectPosition et le GM_Point doivent avoir la m�me dimension.
      * @param pos DirectPosition : coordonn�es du point */
    public void setPosition (DirectPosition pos) {
        position = pos;
    }

    /** NON IMPLEMENTE (renvoie null).
      * Direction entre self et le GM_Point pass� en param�tre, en suivant une courbe qui d�pend du syt�me de coordonn�es (courbe g�od�sique par exemple). 
      * Le bearing retourn� est un vecteur.
      */
    /*    public Bearing bearing(GM_Point toPoint) {
        return null;
    }
    */   
     /** NON IMPLEMENTE (renvoie null).
      * Direction entre self et le DirectPosition pass� en param�tre, en suivant une courbe qui d�pend du syt�me de coordonn�es (courbe g�od�sique par exemple). 
      * Le bearing retourn� est un vecteur.
      */
    /*    public Bearing bearing(DirectPosition toPoint) {
        return null;
    }
    */    
    
    /** Constructeur par d�faut. */
    public GM_Point() {
        position = new DirectPosition();
    }
        
    /** Constructeur � partir de coordonn�es. 
      * @param pos DirectPosition : coordonn�es du point */
    public GM_Point(DirectPosition pos) {
        position = pos;
    }
            
    /** Affiche les coordonn�es du point (2D et 3D). */
    /*public String toString () {
         if (position != null) return getPosition().toString();
         else return "GM_Point : geometrie vide";
    }   */     
    
    /** Renvoie la liste des coordonn�es, qui est constitu�e d'un seul DirectPosition. */
    public DirectPositionList coord() {
        DirectPositionList dpl = new DirectPositionList();
        if (position != null) dpl.add(position);
        return dpl;
    }
    
}
