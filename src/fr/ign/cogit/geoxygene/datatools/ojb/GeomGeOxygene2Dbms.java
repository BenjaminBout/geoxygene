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

package fr.ign.cogit.geoxygene.datatools.ojb;

import java.lang.reflect.Method;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * Conversion des geometries d'un SGBD (Oracle ou Postgis)
 * dans le format GeOxygene, et reciproquement.
 * Pour fonctionner, utiliser absolument la classe "GeOxygeneStatementManager" ( � configurer dans OJB.properties).
 * Permet d'utiliser le m�me convertisseur pour Oracle et Postgis, et ainsi les m�mes fichiers de mapping.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 *
 */

public class GeomGeOxygene2Dbms implements FieldConversion {
	
	
	private static final String POSTGRES_GEOM_CLASS_NAME = "org.postgis.PGgeometry";
	private static final String ORACLE_GEOM_CLASS_NAME = "oracle.sql.STRUCT";
	
	private final String GeomGeOxygene2Oracle_CLASS_NAME = 
		"fr.ign.cogit.geoxygene.datatools.oracle.GeomGeOxygene2Oracle";
	private final String GeomGeOxygene2Postgis_CLASS_NAME = 
		"fr.ign.cogit.geoxygene.datatools.postgis.GeomGeOxygene2Postgis";	
	
	private Method geomOracle2GeOxygeneMethod;
	private Method geomPostgis2GeOxygeneMethod;
	
	
	// Le constructeur initialise les m�thodes � appeler
	public GeomGeOxygene2Dbms () {
		
		// ORACLE
		try {
			Class geomGeOxygene2OracleClass = Class.forName(GeomGeOxygene2Oracle_CLASS_NAME);
			try {
				geomOracle2GeOxygeneMethod = geomGeOxygene2OracleClass.getMethod("sqlToJava", new Class[] {Object.class});	
			} catch (NoSuchMethodException nosuch1) {
				nosuch1.printStackTrace();
				System.exit(0);
			}
		} catch (ClassNotFoundException notfound1) {
			// On ne dit rien : Oracle n'a pas �t� compil� !
		}
		
		//	POSTGIS
		try {
			Class geomGeOxygene2PostgisClass = Class.forName(GeomGeOxygene2Postgis_CLASS_NAME);
			try {
				geomPostgis2GeOxygeneMethod = geomGeOxygene2PostgisClass.getMethod("sqlToJava", new Class[] {Object.class});		
			} catch (NoSuchMethodException nosuch2) {
				nosuch2.printStackTrace();
				System.exit(0);					
			}
		} catch (ClassNotFoundException notfound2) {
			System.out.println("## Le SGBD n'est ni Oracle, ni PostgreSQL  ##");
			System.exit(0);
		}
		
	}


	public Object sqlToJava (Object geom) {
		
		// ORACLE
		if (geom.getClass().getName().compareTo(ORACLE_GEOM_CLASS_NAME) == 0) {
			try {
				return geomOracle2GeOxygeneMethod.invoke(null, new Object[] {geom});
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("G�om�trie renvoy�e nulle");
				return null;
			}
		}
			
		// POSTGIS
		else if (geom.getClass().getName().compareTo(POSTGRES_GEOM_CLASS_NAME) == 0) {
			try {
				return geomPostgis2GeOxygeneMethod.invoke(null, new Object[] {geom});
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("G�om�trie renvoy�e nulle");
				return null;
			}
		}
		// SINON
		else {
			System.out.println("## Le SGBD n'est ni Oracle, ni PostgreSQL - valeur nulle retourn�e ##");
			return null;
		}
	
	}
	
	// Les m�thodes relatives� Oracle ou Postgis sont appel�e directement dans "GeOxygeneStatementManager"
	public Object javaToSql (Object geom) {	
		System.out.println("## WARNING ## Ne devrait pas �tre appel� !! Renvoie nulle");
		return null;
	}

}
