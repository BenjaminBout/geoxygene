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

package fr.ign.cogit.geoxygene.util.loader;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;

/**
 * Genere les identifiant d'une table (colonne COGITID).
 * Dans l'ideal, plusieurs types de generation devraient etre possibles :
 * Simple (i.e. de 1 a N), unicite sur toutes les tables geographiques de la base,
 * empreinte numerique, recopie d'une autre colonne ...
 * Pour l'instant seuls simple,
 * et unicite sur toutes les tables geographiques de la base (par l'algo du max) fonctionnent.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */

public class GenerateIds {

	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	private Geodatabase data;
	private String tableName;
	private int maxID = 0;  // identifiant maximum des cogitid dans tout le jeu
	private boolean unique;  // veut-on des identifiants uniques sur toute la base ?

	private final static String ORACLE_COLUMN_QUERY = "SELECT TABLE_NAME FROM USER_SDO_GEOM_METADATA";
	private final static String POSTGIS_COLUMN_QUERY = "SELECT F_TABLE_NAME FROM GEOMETRY_COLUMNS";



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	public GenerateIds (Geodatabase Data, String TableName, boolean Unique) {
		data = Data;
		tableName = TableName;
		unique = Unique;
	}



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void genere() {
		dropColumnID();
		addColumnID();
		if (unique) {
			if (data.getDBMS() == Geodatabase.ORACLE) maxCOGITID(ORACLE_COLUMN_QUERY);
			else if (data.getDBMS() == Geodatabase.POSTGIS) maxCOGITID(POSTGIS_COLUMN_QUERY);
		}
		if (data.getDBMS() == Geodatabase.ORACLE) genereIDOracle();
		else if (data.getDBMS() == Geodatabase.POSTGIS) genereIDPostgres();
	}



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// ajoute une colonne "COGITID" et appelle la methode genereID
	void addColumnID () {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			String query = "ALTER TABLE "+tableName+" ADD COGITID INTEGER";
			stm.executeUpdate(query);
			System.out.println(tableName+" : colonne CogitID creee");
			stm.close();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// supprime la colonne cogitid
	void dropColumnID () {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			try {
				String query = "ALTER TABLE "+tableName+" DROP COLUMN COGITID";
				stm.executeUpdate(query);
				System.out.println(tableName+" : colonne CogitID effacee");
			} catch (Exception ee) { // pas de colonne cogitid !!
				conn.commit();
			}
			stm.close();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// genere les identifiants dans la colonne COGITID, puis cree une cle primaire sur cette colonne
	void genereIDOracle() {
		try {
			Connection conn = data.getConnection();
			Statement stm = conn.createStatement();
			String query = "SELECT COUNT(*) FROM "+tableName;
			ResultSet rs = stm.executeQuery(query);
			while (rs.next()) {
				int nbCount = ((BigDecimal)rs.getObject(1)).intValue();
				System.out.println(nbCount+" objets dans la table "+tableName+" ... generation des identifiants ...");
			}

			// creation de la procedure PL/SQL de generation des cl�s
			// A FAIRE : y a moyen de faire plus simple : utiliser s�quence ?
			// Ou utiliser la fonction 'cursor for update' et 'current of'
			String proc = "CREATE OR REPLACE PROCEDURE genere_cogitid AS";
			proc=proc+" BEGIN";
			proc=proc+" DECLARE";
			proc=proc+" i integer := "+maxID+";";
			proc=proc+" cursor c is select rowid from "+tableName+";";
			proc=proc+" therowid rowid;";
			proc=proc+" BEGIN";
			proc=proc+" if i is null then i := 0; end if;";
			proc=proc+" open c;";
			proc=proc+" LOOP";
			proc=proc+" fetch c into therowid;";
			proc=proc+" exit when c%notfound;";
			proc=proc+" i := i+1;";
			proc=proc+" update "+tableName+" set cogitid=i where rowid=therowid;";
			proc=proc+" END LOOP;";
			proc=proc+" close c;";
			proc=proc+" END;";
			proc=proc+" END genere_cogitid;";
			stm.execute(proc);

			// execution de la procedure
			CallableStatement cstm = conn.prepareCall ("begin GENERE_COGITID; end;");
			cstm.execute();
			cstm.close();

			// on enleve si ancienne cle primaire
			try {
				String update = "ALTER TABLE "+tableName+" DROP PRIMARY KEY";
				stm.executeUpdate(update);
				System.out.println("cle primaire sur "+tableName+" supprimee");
			} catch (Exception e1) {
				System.out.println("aucune cle primaire sur "+tableName);
			}

			// ajout de la cle primaire
			String update = "ALTER TABLE "+tableName+" ADD PRIMARY KEY (COGITID)";
			stm.executeUpdate(update);
			System.out.println("cle primaire sur "+tableName+" ajoutee (colonne COGITID)");

			// fin
			stm.close();
			conn.commit();
		} catch (Exception e) {
			System.out.println(tableName);
			e.printStackTrace();
		}
	}



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// genere les identifiants dans la colonne COGITID, puis cree une cle primaire sur cette colonne
	void genereIDPostgres() {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			String query = "SELECT COUNT(*) FROM "+tableName;
			ResultSet rs = stm.executeQuery(query);
			while (rs.next()) {
				int nbCount = ((Number)rs.getObject(1)).intValue();
				System.out.println(nbCount+" objets dans la table "+tableName+" ... generation des identifiants ...");
			}

			// Cr�ation d'une s�quence
			try {
				String update = "create SEQUENCE seq_genere_cogitid";
				stm.executeUpdate(update);
			} catch (Exception ee) {
				// La s�quence existe d�j� !
				conn.commit();
			}
			conn.commit();

			// Si le maxID vaut 0 (il n'y a pas encore d'identifiant dans la base), on le force � 1
			if (maxID==0) maxID=1;
			// Affectation du maxID � la s�quence
			// On a pas besoin de l'affecter � maxID+1 puisque l'on utilise toujours nextval pour affecter les identifiants
			query = "SELECT setval ('seq_genere_cogitid', "+maxID+")";
			rs = stm.executeQuery(query);
			while (rs.next()) { }
			conn.commit();

			// Mise � jour de la table � l'aide de la sequence
			String update = "update "+tableName+" set cogitid = nextval('seq_genere_cogitid')";
			stm.executeUpdate(update);
			conn.commit();

			// on enleve si ancienne cle primaire
			// Arnaud 28 oct : modif
			query = "select con.conname, con.contype from pg_constraint con, pg_class cl";
			query = query+" where con.conrelid = cl.oid";
			query = query+" and cl.relname='"+tableName+"'";
			rs = stm.executeQuery(query);
			String conName = "";
			while (rs.next()) {
				String conType = rs.getString(2);
				if (conType.compareToIgnoreCase("p") == 0)
					conName = rs.getString(1);
			}
			if (conName.compareTo("") != 0) {
				update = "ALTER TABLE "+tableName+" DROP CONSTRAINT "+conName;
				stm.executeUpdate(update);
				System.out.println("cle primaire sur "+tableName+" supprim� : "+conName);
			}

			// ajout de la cle primaire
			update = "ALTER TABLE "+tableName+" ADD PRIMARY KEY (COGITID)";
			stm.executeUpdate(update);
			System.out.println("cle primaire sur "+tableName+" ajoutee (colonne COGITID)");

			// fin
			stm.close();
			conn.commit();
		} catch (Exception e) {
			System.out.println(tableName);
			e.printStackTrace();
		}
	}



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// recherche du COGITID maximum parmi les tables g�ographiques (variable globale maxID)
	public void maxCOGITID(String query) {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			List<String> listOfTables = new ArrayList<String>();
			while (rs.next())
				listOfTables.add(rs.getString(1));
			Iterator<String> it = listOfTables.iterator();
			while (it.hasNext()) {
				String aTableName = it.next();
				try {
					query = "SELECT MAX(COGITID) FROM "+aTableName;
					rs= stm.executeQuery(query);
					int max = 0;
					while (rs.next())
						max = ((Number)rs.getObject(1)).intValue();
					if (max > maxID) maxID = max;
				} catch (Exception ee) {    // pas de colonne cogitID
					conn.commit();
				}
			}
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
