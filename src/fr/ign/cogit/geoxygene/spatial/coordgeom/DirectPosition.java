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

//import SRC.SC_CRS -> non implemente;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
   

/**
 * Point connu par ses coordonn�es.
 * Les coordonn�es sont connues par un tableau, de longueur la dimension des g�om�tries (2D ou 3D).
 * Dans cette version, tous les DirectPosition sont en 3D.
 * Si on est en 2D, la 3ieme coordonn�e vaut NaN.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 * 19.02.2007 : correction de bug m�thode move(double offsetX, double offsetY, double offsetZ)
 *
 */


public class DirectPosition {
      
    //////////////////////////////////////////////////////////////////////////////////////////
    // Attribut CRS //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////    
    /** Identifiant du syst�me de coordonn�es de r�f�rence (CRS en anglais).
      * Lorsque les DirectPosition servent � d�finir un GM_Object, cet attribut doit �tre null.
      * En effet, il est alors port� par le GM_Object. */
    // Dans la norme ISO, cet attribut est une relation qui pointe vers la classe SC_CRS (non impl�ment�e)
    protected int CRS;  
    
    /** Renvoie l' identifiant du syst�me de coordonn�es de r�f�rence. */
    public int getCRS() {return this.CRS;}   
    
    /** Affecte une valeur au syst�me de coordonn�es de r�f�rence. */
    public void setCRS(int crs) {CRS = crs; }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Attribut coordinate et dimension //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////    
    /** Tableau des coordonn�es du point. */
    protected double[] coordinate = new double[3];
        
    /** Dimension des coordonn�es (2D ou 3D) - dimension = coordinate.length  */
    protected int dimension = 3;
    
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // Constructeurs /////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////        
    /** Constructeur par d�faut (3D): cr�e un tableau de coordon�es � 3 dimensions, vide.*/
    public DirectPosition() {
        coordinate[0] = Double.NaN;      
        coordinate[1] = Double.NaN;      
        coordinate[2] = Double.NaN;        
    }
            
    /** Constructeur d'un DirectPosition � n dimensions : cr�e un tableau de coordon�es � n dimensions, vide.*/
   /*public DirectPosition(int n) {
        coordinate = new double[n];
        dimension = n;
    }*/
    
    /** Constructeur � partir d'un tableau de coordonn�es. 
      * Si le tableau pass� en param�tre est 2D, la 3i�me coordonn�e du DirectPosition vaudra NaN.
      * Le tableau est recopi� et non pass� en r�f�rence. */
    public DirectPosition(double[] coord) {
        coordinate[0] = coord[0];
        coordinate[1] = coord[1];
        if (coord.length == 3) coordinate[2] = coord[2];
            else coordinate[2] = Double.NaN;
    }
        
    /** Constructeur � partir de 2 coordonn�es. */
    public DirectPosition(double X, double Y) {
        coordinate[0] = X;
        coordinate[1] = Y;
        coordinate[2] = Double.NaN;        
    }
        
    /** Constructeur � partir de 3 coordonn�es. */
    public DirectPosition(double X, double Y, double Z) {
        coordinate[0] = X;
        coordinate[1] = Y;
        coordinate[2] = Z;
    }
    
    
          
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // M�thodes get //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    /** Renvoie le tableau des coordonn�es. */
    public double[] getCoordinate() {
        return this.coordinate;
    }
        
    /** Renvoie la dimension (toujours 3). */
    public int getDimension () {
        return this.dimension;
    }
    
    /** Renvoie la i-�me coordonn�es (i=0 pour X, i=1 pour Y, i=3 pour Z). */
    public double getCoordinate(int i) {
         return this.coordinate[i];
    }
    
    /** Renvoie X (1�re coordonnee du tableau, indice 0). */
    public double getX() {
        return this.coordinate[0];
    }
        
    /** Renvoie Y (2i�me coordonn�e du tableau, indice 1). */
    public double getY() {
        return this.coordinate[1];
    }
        
    /** Renvoie Z (3i�me coordonn�e du tableau, indice 2). */
    public double getZ() {
        return this.coordinate[2];
    }
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // M�thodes set //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    /** Affecte les coordonn�es d'un tableau des coordonn�es (2D ou 3D). 
     * Si le tableau pass� en param�tre est 2D, la 3i�me coordonn�e du DirectPosition vaudra NaN.
     * Le tableau est recopi� et non pass� en r�f�rence. */
    public void setCoordinate(double[] coord)  {
        coordinate[0] = coord[0];
        coordinate[1] = coord[1];
        if (coord.length == 3) coordinate[2] = coord[2];
            else coordinate[2] = Double.NaN;
    }
        
    /** Affecte la position d'un point g�om�trique. Le point pass� en param�tre doit avoir la m�me dimension que this.*/
    public void setCoordinate(GM_Point thePoint) {
       DirectPosition pt = thePoint.getPosition();
       double[] coord = pt.getCoordinate();
       /*if (dimension == coord.length)
            for (int i=0; i<coord.length; i++) coordinate[i] = coord[i];*/
       setCoordinate(coord);
    }
        
    /** Affecte une valeur � la i-�me coordonn�es (i=0 pour X, i=1 pour Y, i=3 pour Z.). */
    public void setCoordinate(int i, double x)  { 
        coordinate[i] = x;
    }
        
    /** Affecte une valeur � X et Y. */
    public void setCoordinate(double x, double y) {
        coordinate[0] = x;
        coordinate[1] = y;
        coordinate[2] = Double.NaN;
    }
        
    /** Affecte une valeur � X, Y et Z. */
    public void setCoordinate(double x, double y, double z)  {
        coordinate[0] = x;
        coordinate[1] = y;
        coordinate[2] = z;
    }
    
    /** Affecte une valeur � X (1�re coordonn�e du tableau). */
    public void setX(double x) {
        coordinate[0] = x;
    }
        
    /** Affecte une valeur � Y (2i�me coordonn�e du tableau). */
    public void setY(double y) {
        coordinate[1] = y;
    }
        
    /** Affecte une valeur � Z (3i�me coordonn�e du tableau). */
    public void setZ(double z)  {
        coordinate[2] = z;
    }
    
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // M�thodes move /////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////    
    /** D�place le point suivant toutes les dimensions. Le point pass� en param�tre doit avoir la m�me dimension que this.*/
    public void move(DirectPosition offsetPoint)  {
        if (dimension == offsetPoint.getDimension())
            for (int i=0; i<dimension; i++) 
                coordinate[i] += offsetPoint.getCoordinate(i);
    }
        
    /** D�place le point suivant X et Y. */
    public void move(double offsetX, double offsetY) {
        coordinate[0] += offsetX;
        coordinate[1] += offsetY;
    }
        
    /** D�place le point suivant X, Y et Z.
	 * coordinate.length<3 -> coordinate.length<4
	 */
    public void move(double offsetX, double offsetY, double offsetZ) {
        if (coordinate.length<4) {
            coordinate[0] += offsetX;
            coordinate[1] += offsetY;
            coordinate[2] += offsetZ;
        }
    }
    
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // M�thode equals ////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////    
    /** Indique si self et le point pass� en param�tre sont �gaux, � une tol�rance pr�s.
      * Si les 2 points ont une troisi�me dimension affect�e, on teste cette dimension.
      * Tol�rance est un double qui doit �tre > 0. */
    public boolean equals(DirectPosition pt, double tolerance)  {
        double x1, x2;
        for (int i=0; i<=1; i++) {
            x1 = coordinate[i];
            x2 = pt.getCoordinate(i);
            if ( (x2 > x1+tolerance) || (x2 < x1-tolerance) ) return false;
        }
        if (!Double.isNaN(this.getZ()))
             if (!Double.isNaN(pt.getZ())) {
                x1 = coordinate[2];
                x2 = pt.getCoordinate(2);
                if ( (x2 > x1+tolerance) || (x2 < x1-tolerance) ) return false;      
             }
        return true;
    }    
    
    /** Indique si self et le point pass� en param�tre sont �gaux EN 2D, � une tol�rance pr�s.
      * Tol�rance est un double qui doit �tre > 0. */
    public boolean equals2D(DirectPosition pt, double tolerance)  {
        double x1, x2;
        for (int i=0; i<=1; i++) {
            x1 = coordinate[i];
            x2 = pt.getCoordinate(i);
            if ( (x2 > x1+tolerance) || (x2 < x1-tolerance) ) return false;
        }
        return true;
    }        
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // M�thode clone /////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////    
    /** Clone le point. */
    public Object clone() {
        DirectPosition dp = new DirectPosition(this.getCoordinate());
        return dp;
    }
        
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // M�thode toGM_Point ////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////      
    /** Cr��e un GM_Point � partir de this.*/
    public GM_Point toGM_Point() {
        return new GM_Point(this);
    }
    

    
    
    //////////////////////////////////////////////////////////////////////////////////////////
    // M�thode d'affichage ///////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////
    /** Affiche les coordonn�es du point (2D et 3D). */
    public String toString () {
         if (Double.isNaN(this.getZ()))
             return new String("DirectPosition - X : "+this.getX()+"     Y : "+this.getY());
         else 
             return new String("DirectPosition - X : "+this.getX()+"     Y : "+this.getY()+"     Z : "+this.getZ());
    }
    

}
