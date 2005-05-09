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

/**
 * Classe m�re abstraite pour les primitives topologiques orient�es.
 * Dans notre impl�mentation 2 TP_DirectedTopo oppos�s ont des identifiants oppos�s.
 * Bien expliquer que dans la norme, ca n'herite pas de TP_Primitive, mais on a fait ca  a cause du double heritage
 * (heritage fictif)
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


abstract public class TP_DirectedTopo extends TP_Primitive {
    
    /** Orientation (+1 ou -1). */
    protected int orientation;    
    
    /** Renvoie l'orientation. */
    public int getOrientation () {return this.orientation;}

    /** Affecte une orientation */
    public void setOrientation(int Ori) {orientation=Ori;}
    
    /** Cr�e un TP_Expression � partir de self. 
      * Sert de constructeur pour les TP_Expression. */
    public TP_Expression asTP_Expression() {
        TP_Expression result = new TP_Expression(this);
        return result;
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////
    /// methodes de la norme supprimees pour simplification                              /
    /// cette methodes sont definies dans les sous-classes avec un bon typage en sortie //
    //////////////////////////////////////////////////////////////////////////////////////
    /** Primitive orient�e d'orientation oppos�e. */
    //abstract public TP_DirectedTopo negate(DataSource data) throws Exception ;
    
    /** Primitive de self (de type TP_Node, ou TP_Edge, ou TP_Face, ou TP_Solid). */
    //abstract public TP_Primitive topo (DataSource data) throws Exception ;
    
}
