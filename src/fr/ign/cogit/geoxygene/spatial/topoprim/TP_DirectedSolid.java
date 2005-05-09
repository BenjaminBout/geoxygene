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

package fr.ign.cogit.geoxygene.spatial.topoprim;

import java.util.List;


/**
  * Solide topologique orient�. Supporte la classe TP_Solid pour les TP_Expression.
  * Dans notre impl�mentation, l'identifiant d'un TP_DirectedTopo est celui de sa primitive avec le signe de l'orientation.
  * EXPLIQUER QUE C'EST PAS PERISTANT et que A PRIORI ca n'a pas de GEOMETRIE
  *
  * @author Thierry Badard, Arnaud Braun & Audrey Simon
  * @version 1.0
  * 
  */

class TP_DirectedSolid extends TP_DirectedTopo {
    


    
    /////////////////////////////////////////////////////////////////////////////////////
    // constructeur /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////        
    /** Constructeur par d�faut. */
    public TP_DirectedSolid() {
    }
        
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    // topo() ///////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////   
    /** Primitive de this. */
    protected TP_Solid topo;                    
    /** Primitive de this. */
    public TP_Solid topo () {
        return topo;
    }
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    // negate() /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////            
    /**  Renvoie le TP_DirectedSolid d'orientation oppos�e. */
    public TP_DirectedSolid negate()  { 
        if (orientation<0) return topo.proxy[0];
        else return topo.proxy[1];
    }
    

    
    /////////////////////////////////////////////////////////////////////////////////////
    // boundary() ///////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////         
    /** non impl�ment� (renvoie null). Renvoie la fronti�re de self.*/
    public TP_SolidBoundary boundary()  {
        return null;
    }
    

    
    /////////////////////////////////////////////////////////////////////////////////////
    // coboundary() /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////         
    /** Renvoie null. */
    public List coBoundary()  {
        return null;
    }
    
}
