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

package fr.ign.cogit.geoxygene.spatial.geomprim;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_SurfacePatch;


/**
  * Surface, compos�e de morceaux de surface. L'orientation vaut n�cessairement +1.
  *
  * <P> Modification de la norme suite au retour d'utilisation : on fait h�riter GM_SurfacePatch de GM_Surface.
  * Du coup, on n'impl�mente plus l'interface GM_GenericSurface.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  *
  */

public class GM_Surface extends GM_OrientableSurface 
                        /*implements GM_GenericSurface*/ {
    

    //////////////////////////////////////////////////////////////////////////////////
    // Attribut "patch" et m�thodes pour le traiter //////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////    
    // A FAIRE : validate : pour que les patch soient contigue ?
    // pour cela il faut regarder que l'union est du type GM_Surface et non GM_MultiSurface
    
    /**Liste des morceaux constituant la surface.*/
    protected List patch;
    
    /** Renvoie la liste des patch. */
    public List getPatch () {return this.patch;}
    
    /** Renvoie  le patch de rang i. */
    public GM_SurfacePatch getPatch (int i)  {
        if ((GM_SurfacePatch.class).isAssignableFrom(this.getClass()))
            if (i != 0) {
                System.out.println("Recherche d'un patch avec i<>0 alors qu'un GM_SurfacePatch ne contient qu'un segment qui est lui-meme");
                return null;
            } else return (GM_SurfacePatch)this.patch.get(i);                
        else return (GM_SurfacePatch)this.patch.get(i); 
    }
    
    /** Affecte un patch au rang i. */
    public void setPatch (int i, GM_SurfacePatch value) {
        if ((GM_SurfacePatch.class).isAssignableFrom(this.getClass()))
            if (i != 0)
                System.out.println("Affection d'un patch avec i<>0 alors qu'un GM_SurfacePatch ne contient qu'un segment qui est lui-meme. La m�thode ne fait rien.");
            else this.patch.set(i, value);
        else this.patch.set(i, value);
    }
    
    /** Ajoute un patch en fin de liste. */
    public void addPatch (GM_SurfacePatch value) {
        if ((GM_SurfacePatch.class).isAssignableFrom(this.getClass()))
            if (sizePatch() > 0)
                System.out.println("Ajout d'un patch alors qu'un GM_SurfacePatch ne contient qu'un segment qui est lui-meme. La m�thode ne fait rien.");
            else this.patch.add(value);
        else this.patch.add(value);
    }
    
    /** Ajoute un patch au rang i. */
    public void addPatch (int i, GM_SurfacePatch value) {
        if ((GM_SurfacePatch.class).isAssignableFrom(this.getClass()))
            if (i != 0)
                System.out.println("Ajout d'un patch avec i<>0 alors qu'un GM_SurfacePatch ne contient qu'un segment qui est lui-meme. La m�thode ne fait rien.");
            else this.patch.add(value);                     
        else this.patch.add(i, value);
    }
    
    /** Efface le patch de valeur value. */
    public void removePatch (GM_SurfacePatch value)  {
        if ((GM_SurfacePatch.class).isAssignableFrom(this.getClass()))
            System.out.println("removePatch() : Ne fait rien car un GM_SurfacePatch ne contient qu'un segment qui est lui-meme.");            
        else this.patch.remove(value);
    }
    
    /** Efface le patch de rang i. */
    public void removePatch (int i) {
        if ((GM_SurfacePatch.class).isAssignableFrom(this.getClass()))
            System.out.println("removePatch() : Ne fait rien car un GM_SurfacePatch ne contient qu'un segment qui est lui-meme.");
        else this.patch.remove(i);
    }    
    
    /** Renvoie le nombre de patch. */
    public int sizePatch () {return this.patch.size();}
    
        
    
    
    //////////////////////////////////////////////////////////////////////////////////
    // Constructeurs /////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    /** Constructeur par d�faut */
    public GM_Surface () {
        patch = new ArrayList();
        orientation = +1;
        primitive = this;
        proxy[0] = this;
        GM_OrientableSurface proxy1 = new GM_OrientableSurface();
        proxy1.orientation = -1;
        proxy1.proxy[0] = this;
        proxy1.proxy[1] = proxy1;
        proxy1.primitive = new GM_Surface(this);
        proxy[1] = proxy1;
    }
    

    /** Constructeur � partir d'un et d'un seul surface patch */
    public GM_Surface(GM_SurfacePatch thePatch) {
        this();
        this.addPatch(thePatch);
    }
    
    
    /** Utilis� en interne (dans les constructeurs publics) pour construire la surface oppos�e,
     * qui est la primitive de proxy[1]. On d�finit ici les r�f�rences n�cessaires. Le but est de retrouver la propriete :
     * surface.getNegative().getPrimitive().getNegative().getPrimitive() = surface.
     * La frontiere de la surface est calculee en dynamique lors de l'appel a la methode getNegative(). */
    public GM_Surface(GM_Surface surface) {
        patch = new ArrayList();
        orientation = +1;
        primitive = this;
        proxy[0] = this;
        GM_OrientableSurface proxy1 = new GM_OrientableSurface();
        proxy1.orientation = -1;
        proxy1.proxy[0] = this;
        proxy1.proxy[1] = proxy1;
        proxy1.primitive = surface;
        proxy[1] = proxy1;
    }

        
    
    
    //////////////////////////////////////////////////////////////////////////////////
    // Impl�mentation de GM_GenericCurve /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////    
    /** NON IMPLEMENTE.
      * Vecteur normal � self au point pass� en param�tre. */
/*    public Vecteur upNormal(DirectPosition point) {
        return null;
    }
*/
    
    /** P�rim�tre. */
    public double perimeter()  {
//        return SpatialQuery.perimeter(this); (ancienne methode avec SDOAPI)
        return this.length();
    }
    
    // code au niveau de GM_Object
    /** Aire. */
   /* public double area()  {
        return SpatialQuery.area(this);
    }*/
    

        
    
    //////////////////////////////////////////////////////////////////////////////////
    // M�thodes d'acc�s aux coordonn�es //////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////    
    /** Renvoie la fronti�re ext�rieure sous forme d'une polyligne (on a lin�aris�). 
     * Ne fonctionne que si la surface est compos�e d'un et d'un seul patch, qui est un polygone. (sinon renvoie null). */
    public GM_LineString exteriorLineString() {
        if (this.sizePatch() == 1) {
            GM_Polygon poly = (GM_Polygon)this.getPatch(0);
            GM_Ring ext = poly.getExterior();
            if (ext != null) {
                GM_Curve c = (GM_Curve)ext.getPrimitive();
                GM_LineString ls = c.asLineString(0.0,0.0,0.0);
                return ls;
            } else {
                System.out.println("GM_Surface::exteriorLineString() : ATTENTION frontiere null");
                return null;               
            }            
        } else {
            System.out.println("GM_Surface::exteriorLineString() : cette m�thode ne fonctionne que si la surface est compos�e d'un seul patch.");
            return null;
        }
    }
        
   /** Renvoie la fronti�re ext�rieure sous forme d'une GM_Curve. 
     * Ne fonctionne que si la surface est compos�e d'un et d'un seul patch, qui est un polygone. (sinon renvoie null). */
    public GM_Curve exteriorCurve() {
        if (this.sizePatch() == 1) {
            GM_Polygon poly = (GM_Polygon)this.getPatch(0);   
            GM_Ring ext = poly.getExterior();
            if (ext != null) return  (GM_Curve)ext.getPrimitive();
            else {
                System.out.println("GM_Surface::exteriorCurve() : ATTENTION frontiere null");
                return null;
            } 
        } else {
            System.out.println("GM_Surface::exteriorCurve() : cette m�thode ne fonctionne que si la surface est compos�e d'un seul patch.");
            return null;
        }
    }
        
    /**  Renvoie la liste des coordonn�es de la fronti�re EXTERIEURE d'une surface,
       sous forme d'une DirectPositionList. */
      public DirectPositionList exteriorCoord ()  {
        GM_Curve c =  this.exteriorCurve();
        if (c != null) return c.coord();
        else return new DirectPositionList();
    }      
            
      
   /** Renvoie la fronti�re int�rieure de rang i sous forme d'une polyligne (on a lin�aris�). 
     * Ne fonctionne que si la surface est compos�e d'un et d'un seul patch, qui est un polygone (sinon renvoie null). */
    public GM_LineString interiorLineString(int i) {
        if (this.sizePatch() == 1) {
            GM_Polygon poly = (GM_Polygon)this.getPatch(0);
            GM_Ring inte = poly.getInterior(i);
           if (inte != null) {
               GM_Curve c = (GM_Curve)inte.getPrimitive();
               GM_LineString ls = c.asLineString(0.0,0.0,0.0);
               return ls;
           } else {
               System.out.println("GM_Surface::interiorLineString() : ATTENTION frontiere null");
               return null;               
           }            
        } else {
            System.out.println("GM_Surface::interiorLineString() : cette m�thode ne fonctionne que si la surface est compos�e d'un seul patch");
            return null;
        }
    }
        
   /** Renvoie la fronti�re int�rieure de rang i sous forme d'une GM_Curve. 
     * Ne fonctionne que si la surface est compos�e d'un et d'un seul patch, qui est un polygone (sinon renvoie null). */
    public GM_Curve interiorCurve(int i) {
        if (this.sizePatch() == 1) {
            GM_Polygon poly = (GM_Polygon)this.getPatch(0);
            GM_Ring inte = poly.getInterior(i);
            if (inte != null) return  (GM_Curve)inte.getPrimitive();
            else {
                System.out.println("GM_Surface::interiorCurve() : ATTENTION frontiere null");
                return null;
            } 
        } else {
            System.out.println("GM_Surface::interiorCurve() : cette m�thode ne fonctionne que si la surface est compos�e d'un seul patch");
            return null;
        }
    }
        
    /**  Renvoie la liste des coordonn�es de la fronti�re int�rieure de rang i d'une surface,
       sous forme d'un GM_PointArray. */
      public DirectPositionList interiorCoord (int i)  {
          GM_Curve c =  this.interiorCurve(i);
          if (c != null) return c.coord();
          else return new DirectPositionList();
    }  
                
    
    /**  Renvoie la liste des coordonn�es  d'une surface (exterieure et interieur)
       sous forme d'une  DirectPositionList. Toutes les coordonnees sont concatenees.*/
      public DirectPositionList coord ()  {
        if (this.sizePatch() == 1) {  
            GM_Polygon poly = (GM_Polygon)this.getPatch(0);
            DirectPositionList dpl = exteriorCurve().coord();
            for (int i=0; i<poly.sizeInterior(); i++)
                dpl.addAll(interiorCurve(i).coord());
           return dpl; 
        } else {
            System.out.println("GM_Surface::coord() : cette m�thode ne fonctionne que si la surface est compos�e d'un seul patch");
            return null;
        }
    }  
    
    /**  Affiche la liste des coordonn�es (interieures et exterieures) */
    /*public String toString() {
        String result = new String();
        result = result+"exterieur : \n";
        if (exteriorCurve() != null) result = result+exteriorCurve().toString();
        else result = result+"vide";
        if (this.sizePatch() == 1) {
            GM_Polygon poly = (GM_Polygon)this.getPatch(0);
            for (int i=0; i<poly.sizeInterior(); i++) {
                result = "\n"+result+"interieur numero "+i+"\n";
                if (interiorCurve(i) != null) result = result+interiorCurve(i).toString();
                else result = result+"vide";
            }
        } else {
            System.out.println("GM_Surface::toString() : cette m�thode ne fonctionne que si la surface est compos�e d'un seul patch");
        }
        return result;
    } */   

}
