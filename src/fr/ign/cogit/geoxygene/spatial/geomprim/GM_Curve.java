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

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_CurveSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;


/**
  * Courbe. L'orientation vaut n�cessairement +1, la primitive est self.
  * Une courbe est compos�e de un ou plusieurs segments de courbe. 
  * Chaque segment � l'int�rieur d'une courbe peut �tre d�fini avec une interpolation diff�rente. 
  * Dans la pratique nous n'utiliserons a priori que des polylignes comme segment(GM_LineString).
  * <P> Modification de la norme suite au retour d'utilisation : on fait h�riter GM_CurveSegment de GM_Curve.
  * Du coup, on n'impl�mente plus l'interface GM_GenericCurve.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */


public class GM_Curve   extends GM_OrientableCurve
                        /*implements GM_GenericCurve*/ {
    
                            
    //////////////////////////////////////////////////////////////////////////////////
    // Attribut "segment" et m�thodes pour le traiter ////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////                          
    /** Liste de tous les segment de courbe (GM_CurveSegment) constituant self. */
    protected List segment;
            
    /** Renvoie la liste des segments. */
    public List getSegment() {
        return this.segment;
    }
    
    /** Renvoie le segment de rang i */
    public GM_CurveSegment getSegment (int i) {
        if ((GM_CurveSegment.class).isAssignableFrom(this.getClass()))
            if (i != 0) {
                System.out.println("Recherche d'un segment avec i<>0 alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme");
                return null;
            } else return (GM_CurveSegment)this.segment.get(i);                
        else return (GM_CurveSegment)this.segment.get(i);      
    }
    
    /** Affecte un segment au i-�me rang de la liste */
    public void setSegment (int i, GM_CurveSegment value) {
        if ((GM_CurveSegment.class).isAssignableFrom(this.getClass()))
            if (i != 0)
                System.out.println("Affection d'un segment avec i<>0 alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La m�thode ne fait rien.");
            else this.segment.set(i, value);
        else this.segment.set(i, value);
    }
        
    /** Ajoute un segment en fin de liste sans v�rifier la continuit� du cha�nage.*/
    public void addSegment (GM_CurveSegment value)  {
        if ((GM_CurveSegment.class).isAssignableFrom(this.getClass()))
            if (sizeSegment() > 0)
                System.out.println("Ajout d'un segment alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La m�thode ne fait rien.");
            else this.segment.add(value);
        else this.segment.add(value);
    }
        
    /** A TESTER.
      * Ajoute un segment en fin de liste en v�rifiant la continuit� du cha�nage. 
      * Capte une exception en cas de probl�me. N�cessit� de passer une tol�rance en param�tre.
      */
    public void addSegment(GM_CurveSegment value, double tolerance) throws Exception {
        if ((GM_CurveSegment.class).isAssignableFrom(this.getClass()))
            if (sizeSegment() > 0)
                System.out.println("Ajout d'un segment alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La m�thode ne fait rien.");
            else this.segment.add(value);
        else {
            if (this.sizeSegment() == 0) this.segment.add(value);
            else {
                int n = this.sizeSegment();
                GM_CurveSegment lastSegment = this.getSegment(n-1);
                if (value.startPoint().equals(lastSegment.endPoint(),tolerance))
                    this.segment.add(value);
                else throw new Exception("Rupture de cha�nage avec le segment pass�e en param�tre");
            }
        }
    }
    
    /** A TESTER.
      * Ajoute un segment en fin de liste en v�rifiant la continuit� du cha�nage, et en retournant le segment si necessaire.
      * Capte une exception en cas de probl�me. N�cessit� de passer une tol�rance en param�tre.
      */
    public void addSegmentTry (GM_CurveSegment value, double tolerance) throws Exception {
        if ((GM_CurveSegment.class).isAssignableFrom(this.getClass()))
          if (sizeSegment() > 0)
                System.out.println("Ajout d'un segment alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La m�thode ne fait rien.");
            else this.segment.add(value);
        else {
            try {
                this.addSegment(value,tolerance); 
            } catch (Exception e1) {
                try {
                    this.addSegment(value.reverse(),tolerance); 
                } catch (Exception e2) {
                    throw new Exception("Rupture de cha�nage avec le segment pass�e en param�tre(apr�s avoir essay� de le retourner).");
                }
            }
        }
    }
        
    /** Ajoute un segment au i-�me rang de la liste, sans v�rifier la continuit� du cha�nage. */
    public void addSegment (int i, GM_CurveSegment value) {
        if ((GM_CurveSegment.class).isAssignableFrom(this.getClass()))
            if (i != 0)
                System.out.println("Ajout d'un segment avec i<>0 alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La m�thode ne fait rien.");
            else this.segment.add(value);                     
        else this.segment.add(i, value);
    }
        
    /** Efface de la liste le (ou les) segment pass� en param�tre */
    public void removeSegment (GM_CurveSegment value)  {
        if ((GM_CurveSegment.class).isAssignableFrom(this.getClass()))
            System.out.println("removeSegment() : Ne fait rien car un GM_CurveSegment ne contient qu'un segment qui est lui-meme.");            
        else this.segment.remove(value);
    }
        
    /** Efface le i-�me segment de la liste */
    public void removeSegment (int i) {
        if ((GM_CurveSegment.class).isAssignableFrom(this.getClass()))
            System.out.println("removeSegment() : Ne fait rien car un GM_CurveSegment ne contient qu'un segment qui est lui-meme.");
        else this.segment.remove(i);
    }    
    
    /** Renvoie le nombre de segment */
    public int sizeSegment () {
        return this.segment.size();
    }
        
    /** A TESTER.
      * V�rifie le cha�nage des segments. renvoie TRUE s'ils sont cha�n�s, FALSE sinon.
      * N�cessit� de d�finir une tol�rance.
      */
    public boolean validate (double tolerance) {
        if (this.sizeSegment() <= 1) return true;
        else {
            int n = this.sizeSegment();
            for (int i=0; i<n-1; i++) {
                GM_CurveSegment segment1 = this.getSegment(i);
                GM_CurveSegment segment2 = this.getSegment(i+1);
                if (!(segment2.startPoint().equals(segment1.endPoint(),tolerance)))
                    return false;
            }
            return true;
        }
    }
    

    
    
    
    //////////////////////////////////////////////////////////////////////////////////
    // Constructeurs /////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    /** Constructeur par d�faut */
    public GM_Curve() {
        segment = new ArrayList();
        orientation = +1;
        primitive = this;
        proxy[0] = this;
        GM_OrientableCurve proxy1 = new GM_OrientableCurve();
        proxy1.orientation = -1;
        proxy1.proxy[0] = this;
        proxy1.proxy[1] = proxy1;
        proxy1.primitive = new GM_Curve(this);
        proxy[1] = proxy1;
    }
           
    /** Constructeur � partir d'un et d'un seul GM_CurveSegment */
    public GM_Curve(GM_CurveSegment C) {
        this();
        segment.add(C);
    }
        
    /** Usage interne. Utilis� en interne (dans les constructeurs publics) pour construire la courbe oppos�,
     * qui est la primitive de proxy[1]. On d�finit ici les r�f�rences n�cessaires. Le but est de retrouver la propriete :
     * curve.getNegative().getPrimitive().getNegative().getPrimitive() = curve.
     * Les segment de la courbe sont calcule en dynamique lors de l'appel a la methode getNegative(). */
    public GM_Curve(GM_Curve curve) {
        segment = new ArrayList();
        orientation = +1;
        primitive = this;
        proxy[0] = this;
        GM_OrientableCurve proxy1 = new GM_OrientableCurve();
        proxy1.orientation = -1;
        proxy1.proxy[0] = this;
        proxy1.proxy[1] = proxy1;
        proxy1.primitive = curve;
        proxy[1] = proxy1;
    }
        
    

    

    //////////////////////////////////////////////////////////////////////////////////
    // Impl�mentation de GM_GenericCurve /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////    
    /** Retourne le DirectPosition du premier point. 
      * Diff�rent de l'op�rateur "boundary" car renvoie la valeur du point et non pas l'objet g�om�trique repr�sentatif.
      * M�thode d'impl�mentation de l'interface GM_GenericCurve. */
    public DirectPosition startPoint() {
        return this.getSegment(0).coord().get(0);
    }

    
    /**Retourne le DirectPosition du dernier point. 
      * Diff�rent de l'op�rateur "boundary" car renvoie la valeur du point et non pas l'objet g�om�trique repr�sentatif.
      * M�thode d'impl�mentation de l'interface GM_GenericCurve.*/
    public DirectPosition endPoint() {
        GM_CurveSegment lastSegment = this.getSegment(this.sizeSegment()-1);
        DirectPositionList pointArray = lastSegment.coord();
        return pointArray.get(pointArray.size()-1);
    }

    
    /** NON IMPLEMENTE - A FAIRE.
      * Renvoie un point � l'abcsisse curviligne s.
     */
/*    public DirectPosition param(double s) {
        return null;
    }
*/
    
    /** NON IMPLEMENTE.
      * Vecteur tangent a la courbe, � l'abscisse curviligne  pass�e en param�tre. Le vecteur r�sultat est norm�.
     */
/*    public Vecteur tangent(double s) {
        return null;
    }
*/
    
    /** NON IMPLEMENTE. Renvoie 0.0 .
      * M�thode d'impl�mentation de l'interface GM_GenericCurve.
      */
/*    public double startParam() {
        return 0.0;
    }
*/
    
    /** NON IMPLEMENTE.
      * Longueur de la courbe pour une GM_Curve.
     */
/*    double endParam() {
        return 0.0;
    }
*/
    
    /** NON IMPLEMENTE.
      * Renvoie le param�tre au point P (le param�tre �tant a priori la distance).
      * Si P n'est pas sur la courbe, on cherche alors pour le calcul le point le plus proche de P sur la courbe (qui est aussi renvoy� en r�sultat). 
      * On renvoie en g�n�ral une seule distance, sauf si la courbe n'est pas simple.
    */
/*    List paramForPoint(DirectPosition P) {
        return null;
    }
*/
    
    /** NON IMPLEMENTE.
      * Repr�sentation alternative d'une courbe comme l'image continue d'un intervalle de r�els, sans imposer que cette param�trisation repr�sente la longueur de la courbe, et sans imposer de restrictions entre la courbe et ses segments.
      * Utilit� : pour les courbes param�tr�es,  pour construire une surface param�tr�e.
      */
/*    DirectPosition constrParam(double cp) {
        return null;
    }
*/
    
    /** NON IMPLEMENTE.
      * Param�tre au startPoint pour une courbe param�tr�e, c'est-�-dire : constrParam(startConstrParam())=startPoint().
      * M�thode d'impl�mentation de l'interface GM_GenericCurve.
      * NON IMPLEMENTE
      */
/*    double startConstrParam() {
        return 0.0;
    }
*/
    
    /** NON IMPLEMENTE.
      * Param�tre au endPoint pour une courbe param�tr�e, c'est-�-dire : constrParam(endConstrParam())=endPoint().
      */
/*    double endConstrParam() {
        return 0.0;
    }
*/
        
    /** 
      * Longueur totale de la courbe. (code au niveau de GM_Object)
      */
   /* public double length()  {
    	System.out.println("appel ##");
        return SpatialQuery.length(this);
    }*/
    
    
    /** NON IMPLEMENTE.
      * Longueur entre 2 points.
    */
  /*  public double length(GM_Position p1, GM_Position p2) {
        return 0.0;
    }*/
 
    
    /** NON IMPLEMENTE.
      * Longueur d'une courbe param�tr�e "entre 2 r�els".
    */
/*    double length(double cparam1, double cparam2) {
        return 0.0;
    }
*/
    
    /**
      * Approximation lin�aire d'une courbe avec les points de contr�le. Elimine les points doublons cons�cutifs
      * (qui apparaissent quand la courbe est compos�e de plusieurs segments).
      * <P> Le param�tre spacing indique la distance maximum entre 2 points de contr�le ; 
      * le param�tre  offset indique la distance maximum entre la polyligne g�n�r�e et la courbe originale. 
      * Si ces 2 param�tres sont � 0, alors aucune contrainte n'est impos�e.
      * Dans l'IMPLEMENTATION ACTUELLE : on impose que ces param�tres soient � 0.
      * <P> Le param�tre tol�rance est n�cessaire pour �liminer les doublons. On peut passer 0.0.
      * <P> M�thode d'impl�mentation de l'interface GM_GenericCurve.
      */
    // Dans la norme, les param�tres spacing et offset sont de type Distance.
    // Dans la norme, il n'y a pas de param�tre tol�rance.
    public GM_LineString asLineString(double spacing, double offset, double tolerance)   {
        GM_LineString theLineString = null;
        if ((spacing != 0.0) || (offset != 0.0)) {
            System.out.println("GM_Curve::asLineString() : Spacing et Offset ne sont pas impl�ment�s. Passer (0.0, 0.0, tolerance) en param�tres");
            return null;
        } else {
            theLineString = new GM_LineString();
            DirectPositionList aListOfPoints;
            if (this.sizeSegment() > 0) {
                for (int i=0; i<this.sizeSegment(); i++) {
                    aListOfPoints = this.getSegment(i).coord();
                    if (aListOfPoints.size() > 0 ) {
                        aListOfPoints.initIterator();
                        while (aListOfPoints.hasNext()) {
                            DirectPosition pt1 = aListOfPoints.next();
                            if (theLineString.getControlPoint().size() > 0) {
                                DirectPosition pt2 = theLineString.getControlPoint().get(theLineString.getControlPoint().size()-1);
                                if (!pt1.equals(pt2,tolerance))
                                    theLineString.getControlPoint().add(pt1);
                            } else { 
                                theLineString.getControlPoint().add(pt1);
                            }
                        }
                    }
                }
            }
            return theLineString;
        }
    }
    
    
    
    

    //////////////////////////////////////////////////////////////////////////////////
    // M�thodes d'acc�s aux coordonn�es //////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    /** M�thode pour afficher les coordonn�es d'une courbe. */
    /*public String toString ()  {
        String result = new String();
        if (sizeSegment() == 0) {
            result = "GM_Curve : geometrie vide";
            return result;
        }
        for(int i=0; i<this.sizeSegment(); i++) {
            GM_CurveSegment theSegment = this.getSegment(i);
            DirectPositionList theList = theSegment.coord();
            if (theList.size() != 0) {
                result = result+theList.toString();
                result = result+"\n";
            } else result = "GM_CurveSegment vide\n";
        }
        return result.substring(0,result.length()-1);   // on enleve le dernier "\n";
    }  */      
        
    /** Renvoie la liste des coordonn�es d'une courbe sous forme d'une liste de DirectPosition . */
    public DirectPositionList coord ()  {
        DirectPositionList result = new DirectPositionList();
        if (sizeSegment() == 0) return result;
        for(int i=0; i<this.sizeSegment(); i++) {
            GM_CurveSegment theSegment = this.getSegment(i);
            DirectPositionList theList = theSegment.coord();
            theList.initIterator();
            while (theList.hasNext()) {
                DirectPosition thePoint = theList.next();
                result.add(thePoint);
            }
        }
        return result;
    }
    
}
