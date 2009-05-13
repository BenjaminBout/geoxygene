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

package fr.ign.cogit.geoxygene.util.conversion;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Conversions entre les GM_Object GeOxygene et les Geometry JTS.
 *
 * @author Thierry Badard, Arnaud Braun & Christophe Pele & Julien Perret
 * @version 1.2
 * 2008/07/31 (Julien Perret) : ajout de la factory {@link AdapterFactory}
 * @version 1.3
 * 2008/07/31 (Julien Perret) : commentaires javadoc
 */

public class JtsGeOxygene
{
	/*------------------------------------------------------------*/
	/*-- Fields --------------------------------------------------*/
	/*------------------------------------------------------------*/

	//	private static int jtsSRID=0;
	private static PrecisionModel jtsPrecision=new PrecisionModel();
	//	private static GeometryFactory jtsGeomFactory=new GeometryFactory(JtsGeOxygene.jtsPrecision,JtsGeOxygene.jtsSRID);
	//	private static WKTReader jtsWktReader=new WKTReader(JtsGeOxygene.jtsGeomFactory);
	//	private static WKTWriter jtsWktWriter=new WKTWriter();

	/*------------------------------------------------------------*/
	/*-- Conversion beetween JTS and GeOxygene objects -----------*/
	/*------------------------------------------------------------*/

	/**
	 * Conversion d'une g�om�trie G�oxyg�ne {@link GM_Object} en g�om�trie JTS {@link Geometry}.
	 * @param geOxyGeom une g�om�trie G�oxyg�ne
	 * @return une g�om�trie JTS �quivalente
	 * @throws Exception renvoie une exception si la g�om�trie en entr�e n'est pas valide
	 */
	public static Geometry makeJtsGeom(GM_Object geOxyGeom)
	throws Exception
	{
		return makeJtsGeom(geOxyGeom,true);
	}

	/**
	 * Conversion d'une g�om�trie G�oxyg�ne {@link GM_Object} en g�om�trie JTS {@link Geometry}.
	 * @param geOxyGeom une g�om�trie G�oxyg�ne
	 * @param adapter si adapter est vrai, on utiliser la factory {@link AdapterFactory}, sinon, on passe par WKT
	 * @return une g�om�trie JTS �quivalente
	 * @throws Exception Exception renvoie une exception si la g�om�trie en entr�e n'est pas valide
	 */
	public static Geometry makeJtsGeom(GM_Object geOxyGeom,boolean adapter)
	throws Exception
	{
		if (adapter) return AdapterFactory.toGeometry(new GeometryFactory(JtsGeOxygene.jtsPrecision,geOxyGeom.getCRS()),geOxyGeom);

		GeometryFactory jtsGeomFactory=new GeometryFactory(JtsGeOxygene.jtsPrecision,geOxyGeom.getCRS());
		WKTReader jtsWktReader=new WKTReader(jtsGeomFactory);
		String wktGeom=WktGeOxygene.makeWkt(geOxyGeom);
		return jtsWktReader.read(wktGeom);
	}

	/**
	 * Conversion d'une g�om�trie JTS {@link Geometry} en g�om�trie G�oxyg�ne {@link GM_Object}.
	 * @param jtsGeom une g�om�trie JTS
	 * @return une g�om�trie G�oxyg�ne �quivalente
	 * @throws Exception Exception renvoie une exception si la g�om�trie en entr�e n'est pas valide
	 */
	public static GM_Object makeGeOxygeneGeom(Geometry jtsGeom)
	throws Exception
	{
		return makeGeOxygeneGeom(jtsGeom, true);
	}

	/**
	 * Conversion d'une g�om�trie JTS {@link Geometry} en g�om�trie G�oxyg�ne {@link GM_Object}.
	 * @param adapter si adapter est vrai, on utiliser la factory {@link AdapterFactory}, sinon, on passe par WKT
	 * @param jtsGeom une g�om�trie JTS
	 * @return une g�om�trie G�oxyg�ne �quivalente
	 * @throws Exception Exception renvoie une exception si la g�om�trie en entr�e n'est pas valide
	 */
	public static GM_Object makeGeOxygeneGeom(Geometry jtsGeom, boolean adapter)
	throws Exception
	{
		if (adapter) return AdapterFactory.toGM_Object(jtsGeom);

		WKTWriter jtsWktWriter=new WKTWriter();
		String wktResult=jtsWktWriter.write(jtsGeom);
		return WktGeOxygene.makeGeOxygene(wktResult);
	}

	/**
	 * Conversion d'une coordonn�e JTS {@link CoordinateSequence} en position G�oxyg�ne {@link DirectPosition}.
	 * @param jtsCoord une coordonn�e JTS
	 * @return une position G�oxyg�ne �quivalente
	 * @throws Exception Exception renvoie une exception si la g�om�trie en entr�e n'est pas valide
	 */
	public static DirectPosition makeDirectPosition(CoordinateSequence jtsCoord)
	throws Exception
	{
		GeometryFactory jtsGeomFactory=new GeometryFactory(JtsGeOxygene.jtsPrecision,0);
		Geometry jtsPoint=new Point(jtsCoord, jtsGeomFactory);
		GM_Point geOxyPoint=(GM_Point)JtsGeOxygene.makeGeOxygeneGeom(jtsPoint);
		DirectPosition geOxyDirectPos=geOxyPoint.getPosition();
		return geOxyDirectPos;
	}

	/**
	 * Conversion d'un tableau de coordonn�es JTS {@link CoordinateSequence} en liste de positions G�oxyg�ne {@link DirectPositionList}.
	 * @param jtsCoords un tableau de coordonn�es JTS
	 * @return une list de positions G�oxyg�ne �quivalente
	 * @throws Exception Exception renvoie une exception si la g�om�trie en entr�e n'est pas valide
	 */
	public static DirectPositionList makeDirectPositionList(CoordinateSequence[] jtsCoords)
	throws Exception
	{
		DirectPositionList list=new DirectPositionList();
		for (int i=0; i<jtsCoords.length; i++) {
			list.add(JtsGeOxygene.makeDirectPosition(jtsCoords[i]));
		}
		return list;
	}
}
