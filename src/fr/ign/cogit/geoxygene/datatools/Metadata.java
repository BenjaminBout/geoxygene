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
 
package fr.ign.cogit.geoxygene.datatools;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

/**
 * Classe pour d�crire les m�tadonn�es des classes java persistantes.
 * S'il s'agit de classes g�ographiques, des m�tadonn�es sur la g�om�trie sont renseign�es.
 * Cette classe est instanci�e � l'initialisation de Geodatabase.
 * (on cr�e une liste de M�tadata, une valeur par classe persistante).
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 */


public class Metadata {

    /////////////////////////////////////////////////////////////////
    /// attributs ///////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////
    /** Le nom de la classe persistante. */
    protected String _className;
    
    /** La table du SGBD. */
    protected String _tableName;
    
    /** La colonne o� est stock�e la g�om�trie (mono-repr�sentation !).*/
    protected String _geomColumnName;
    
    /** La colonne o� est stock� la clef (ne g�re pas les clefs complexes !). */
    protected String _idColumnName;
    
	/** Le nom de l'attribut qui "mappe" la clef (ne g�re pas les clefs complexes !). */
	protected String _idFieldName;
    
    /** L'identifant du syst�me de coordonn�es. */
    protected int _SRID;
    
    /** L'enveloppe de la couche. */
    protected GM_Envelope _envelope;    // 
    
    /** La tolerance sur les coordonn�es.
        _tolerance[0] = tolerance sur X, etc.*/
    protected double[] _tolerance;      
    
    /** Dimension des coordonn�es (2D ou 3D). */
    protected int _dimension;
    

    
    
    /////////////////////////////////////////////////////////////////
    /// get /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////
    /** Le nom de la classe persistante. */
    public String getClassName() {return _className;}
    
    /** La classe java persistante; */
    public Class getJavaClass()  {
        try {
            return Class.forName(_className);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /** La table du SGBD. */
    public String getTableName() {return _tableName;}
    
    /** La colonne o� est stock�e la g�om�trie (mono-repr�sentation !). */
    public String getGeomColumnName() {return _geomColumnName;}
    
    /** La colonne o� est stock� la clef (ne g�re pas les clefs complexes !). */
    public String getIdColumnName() {return _idColumnName;}
    
	/** Le nom de l'attribut qui "mappe" la clef (ne g�re pas les clefs complexes !). */
	public String getIdFieldName() {return _idFieldName;}
    
    /** L'identifant du syst�me de coordonn�es. 
        Vaut 0 s'il n'est pas affect�, ou si la classe n'est pas g�ographique.*/
    public int getSRID() {return _SRID;}
    
    /** L'enveloppe de la couche. 
        Vaut null si la classe n'est pas g�ographique. */
    public GM_Envelope getEnvelope() {return _envelope;} 
    
    /** La tolerance sur les coordonn�es.
        _tolerance[0] = tolerance sur X, etc.
        Vaut null si la classe n'est pas g�ographique. */
    public double[] getTolerance() {return _tolerance;}
    
    /** La tolerance sur les coordonn�es.
        getTolerance(i) = tolerance sur X, etc.
        Vaut null si la classe n'est pas g�ographique. */
    public double getTolerance(int i) {return _tolerance[i];}
    
    /** Dimension des coordonn�es (2D ou 3D). 
        Vaut null si la classe n'est pas g�ographique. */
    public int getDimension() {return _dimension;}
    

    
    /////////////////////////////////////////////////////////////////
    /// set /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////    
    public void setClassName (String ClassName) {_className = ClassName;}
    
    public void setTableName (String TableName) {_tableName = TableName;}
    
    public void setGeomColumnName (String GeomColumnName) {_geomColumnName = GeomColumnName;}
    
    public void setIdColumnName (String IdColumnName) {_idColumnName = IdColumnName;}
    
	public void setIdFieldName (String IdFieldName) {_idFieldName = IdFieldName;}
    
    public void setSRID (int SRID) {_SRID = SRID;}
    
    public void setEnvelope(GM_Envelope Envelope) {_envelope = Envelope;}
    
    public void setTolerance (double[] Tolerance) {_tolerance = Tolerance;}
    
    public void setTolerance (int i, double Tolerance) {_tolerance[i] = Tolerance;}
    
    public void setDimension(int Dimension) { _dimension = Dimension;}
    
}
