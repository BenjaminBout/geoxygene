/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.util.conversion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.prj.PrjFileReader;
import org.geotools.data.shapefile.shp.ShapeType;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Classe permettant de lire des shapefiles et de cr�er une population de DefautFeatures.
 * Le sch�ma et le FeatureType associ� sont cr��s au passage.
 * Il existe deux principales possibilit�s pour l'utiliser :
 * <ul>
 * <li> de fa�on <b>asynchrone</b>. Pour ce, il faut cr�er un objet ShapefileReader et
 * ex�cuter la m�thode read dessus. Cel� lance un nouveau processus qui lit les features
 * et ajoute les objets � la population au fur et � mesure.
 * <li> de fa�on <b>synchrone</b>. Pour ce, utiliser une des m�thodes statiques read ou chooseAndReadShapefile
 * </ul>
 * @author Julien Perret
 *
 */
public class ShapefileReader implements Runnable {
	private final static Logger logger=Logger.getLogger(ShapefileReader.class.getName());
	String shapefileName;
	String populationName;
	DataSet dataset;
	SchemaDefaultFeature schemaDefaultFeature;
	FeatureSource<SimpleFeatureType, SimpleFeature> source;
	Population<DefaultFeature> population;
	Reader reader = null;
	public Reader getReader() {return this.reader;}
	/**
	 * Renvoie la population dans laquelle les objets sont charg�s.
	 * @return la population dans laquelle les objets sont charg�s
	 */
	public Population<DefaultFeature> getPopulation() {return this.population;}
	/**
	 * Constructeur de shapefileReader. L'utilisation de ce constructeur
	 * n'a de sens que si l'on souhaite utiliser le chargement asynchrone.
	 * Pour utiliser ce dernier, on contruit un objet et on lance le chargement
	 * en utilisant la m�thode <code> read </code>.
	 * <p>
	 * Pour utiliser le chargement synchrone, utiliser l'une des m�thode statique <code> read </code>. 
	 * 
	 * @see #read()
	 * @see #read(String)
	 * @see #read(String, String, DataSet, boolean)
	 * @see #chooseAndReadShapefile()
	 * @see #initSchema(String, SchemaDefaultFeature, Population, boolean)
	 * 
	 * @param shapefileName nom du fichier � charger
	 * @param populationName nom de la population � cr�er et � l'int�rieur de laquelle les objets sont ajout�s
	 * @param dataset nom du dataset auquel la population est ajout�e
	 * @param initSpatialIndex vrai si l'on veut cr�er un index spatial sur la population et le mettre � jour
	 * pendant l'ajout des objets
	 */
	public ShapefileReader(String shapefileName, String populationName, DataSet dataset, boolean initSpatialIndex) {
		this.shapefileName=shapefileName;
		this.populationName=populationName;
		this.dataset=dataset;
		this.population = new Population<DefaultFeature>(populationName);
		if (dataset!=null) dataset.addPopulation(population);
		this.schemaDefaultFeature = new SchemaDefaultFeature();
		/** Initialise le sch�ma */
		this.reader = initSchema(shapefileName, schemaDefaultFeature, population, initSpatialIndex);
	}
	/**
	 * Utilis�e pour lancer le chargement asynchrone.
	 * @see #ShapefileReader(String, String, DataSet, boolean)
	 */
	public void read() {new Thread(this).start();}	
	/**
	 * Lit les features contenus dans le fichier en param�tre.
	 * Ce chargement est synchrone
	 * <p>
	 * Pour utiliser le chargement asynchrone, utiliser le constructeur. 
	 * 
	 * @see #read()
	 * @see #read(String, String, DataSet, boolean)
	 * @see #chooseAndReadShapefile()
	 * 
	 * @param shapefileName un shapefile
	 * @return une population contenant les features contenues dans le fichier.
	 */
	public static Population<DefaultFeature> read(String shapefileName) {return read(shapefileName, shapefileName.substring(shapefileName.lastIndexOf("/")+1,shapefileName.lastIndexOf(".")), null, false);}
	/**
	 * Lit les features contenus dans le fichier en param�tre et ajoute la population charg�e � un dataset.
	 * Ce chargement est synchrone
	 * Pour utiliser le chargement asynchrone, utiliser le constructeur. 
	 * Si le param�tre initSpatialIndex est vrai, alors on initialise aussi l'index spatial de la population.
	 * 
	 * @see #read()
	 * @see #read(String)
	 * @see #chooseAndReadShapefile()
	 * @see #initSchema(String, SchemaDefaultFeature, Population, boolean)
	 * @see #read(Reader, SchemaDefaultFeature, Population)
	 * 
	 * @param shapefileName un shapefile
	 * @param populationName non de la population
	 * @param dataset jeu de donn�es auquel ajouter la population
	 * @param initSpatialIndex si ce boolean est vrai, alors on initialise la population.
	 * @return une population contenant les features contenues dans le fichier.
	 */
	public static Population<DefaultFeature> read(String shapefileName, String populationName, DataSet dataset, boolean initSpatialIndex) {
		// creation de la collection de features
		Population<DefaultFeature> population = new Population<DefaultFeature>(populationName);
		if (dataset!=null) dataset.addPopulation(population);
		try {
			SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
			/** Initialise le sch�ma */
			Reader reader = initSchema(shapefileName, schemaDefaultFeature, population, initSpatialIndex);
			/** Parcours de features du fichier et cr�ation de Default features �quivalents */
			read(reader, schemaDefaultFeature, population);
		} catch (MalformedURLException e1) {
			logger.error("L'URL du fichier "+shapefileName+" est mal form�e. Il n'a pas �t� charg� et le r�sultat est null.");
			return null;
		} catch (IOException e) {
			logger.error("Probl�me pendant la lecture du fichier "+shapefileName+". Il n'a pas �t� charg� et le r�sultat est null.");
			return null;
		}
		return population;
	}
	/**
	 * Ouvre une fenetre (JFileChooser) afin de choisir le fichier et le charge.
	 * Ce chargement est synchrone
	 * Pour utiliser le chargement asynchrone, utiliser le constructeur. 
	 * 
	 * @see #read()
	 * @see #read(String)
	 * @see #read(String, String, DataSet, boolean)
	 * 
	 * @return une population contenant les features contenues dans le fichier.
	 */
	public static Population<DefaultFeature> chooseAndReadShapefile() {
		JFileChooser choixFichierShape = new JFileChooser();
		/** Cr�e un filtre qui n'accepte que les fichier shp ou les r�pertoires */
		choixFichierShape.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File f) {return (f.isFile() && (f.getAbsolutePath().endsWith(".shp") || f.getAbsolutePath().endsWith(".SHP")) || f.isDirectory());}
			@Override
			public String getDescription() {return "fichiers ESRI shapefile";}
		});
		choixFichierShape.setFileSelectionMode(JFileChooser.FILES_ONLY);
		choixFichierShape.setMultiSelectionEnabled(false);
		JFrame frame = new JFrame();
		frame.setVisible(true);
		int returnVal = choixFichierShape.showOpenDialog(frame);
		frame.dispose();
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			if (logger.isDebugEnabled()) logger.debug("You chose to open this file: " + choixFichierShape.getSelectedFile().getAbsolutePath());
			return read(choixFichierShape.getSelectedFile().getAbsolutePath());
		}
		return null;
	}
	/**
	 * Initialise le sch�ma utilis� pour les nouveaux features.
	 * @see #read(Reader, SchemaDefaultFeature, Population)
	 * @param shapefileName nom du fichier � lire
	 * @param schemaDefaultFeature sch�ma � initialiser
	 * @param population population � peupler avec les features
	 * @param initSpatialIndex vrai si on souhaite initialiser l'index spatial de la population
	 */
	public static Reader initSchema(String shapefileName, SchemaDefaultFeature schemaDefaultFeature, Population<DefaultFeature> population, boolean initSpatialIndex) {
		Reader reader = new Reader(shapefileName);
		double minX=reader.getMinX();
		double maxX=reader.getMaxX();
		double minY=reader.getMinY();
		double maxY=reader.getMaxY();
		if (initSpatialIndex) population.initSpatialIndex(
				Tiling.class, 
				true, 
				new GM_Envelope(minX,maxX,minY,maxY),
				10);
		population.setCenter(new DirectPosition((maxX+minX)/2,(maxY+minY)/2));
		if(logger.isTraceEnabled()) logger.trace("index spatial initialis� avec "+minX+","+maxX+","+minY+","+maxY);
		/** Cr�er un featuretype de jeu correspondant */
		fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
		int nbFields = reader.getNbFields();
		Map<Integer,String[]> attLookup = new HashMap<Integer,String[]>();
		for (int i = 0 ; i < nbFields ; i++) {
			AttributeType type = new AttributeType();
			String nomField = reader.getFieldName(i);
			String memberName = reader.getFieldName(i);
			String valueType = reader.getFieldClass(i).getSimpleName();
			type.setNomField(nomField);
			type.setMemberName(memberName);
			type.setValueType(valueType);
			newFeatureType.addFeatureAttribute(type);
			attLookup.put(i, new String[]{nomField,memberName});
			if(logger.isDebugEnabled()) logger.debug("Ajout de l'attribut "+i+" = "+nomField);
		}
		/** Cr�ation d'un sch�ma associ� au featureType */
		newFeatureType.setGeometryType((reader.getShapeType()==null)?GM_Object.class:reader.getShapeType());
		if(logger.isDebugEnabled())logger.debug("shapeType = "+reader.getShapeType()+" type de g�om�trie = "+newFeatureType.getGeometryType());
		schemaDefaultFeature.setFeatureType(newFeatureType);
		newFeatureType.setSchema(schemaDefaultFeature);
		schemaDefaultFeature.setAttLookup(attLookup);
		population.setFeatureType(newFeatureType);
		if(logger.isDebugEnabled()) 
		    for(GF_AttributeType fa : newFeatureType.getFeatureAttributes()) {
			logger.debug("FeatureAttibute = "+fa.getMemberName()+"-"+fa.getValueType());
		    }
		return reader;
	}
	/**
	 * Lit la collection de features GeoTools <code> source </code> et cr�e des default features correspondant
	 * en utilisant le sch�ma <code> schema </code> et les ajoute � la population <code> population </code>.
	 * @param schema sch�ma des features � cr�er
	 * @param population population � laquelle ajouter les features cr��s
	 * @throws IOException renvoie une exception en cas d'erreur de lecture
	 */
	public static void read(Reader reader, SchemaDefaultFeature schema, Population<DefaultFeature> population) throws IOException {
		for (int indexFeature = 0 ; indexFeature < reader.getNbFeatures(); indexFeature++) {
			DefaultFeature defaultFeature = new DefaultFeature();
			defaultFeature.setFeatureType(schema.getFeatureType());
			defaultFeature.setSchema(schema);
			defaultFeature.setAttributes(reader.fieldValues[indexFeature]);
			try {
				if (reader.geometries[indexFeature]==null) {
					logger.error("G�om�trie nulle, objet ignor�");
				} else {
					defaultFeature.setGeom(AdapterFactory.toGM_Object(reader.geometries[indexFeature]));
					population.add(defaultFeature);
				}
			} catch (Exception e) {
				logger.error("Probl�me pendant la conversion de la g�om�trie. L'objet est ignor�");
			}
		}
	}
	@Override
	public void run() {
		try {read(reader, this.schemaDefaultFeature, this.population);}
		catch (IOException e) {
			logger.error("Probl�me pendant la lecture du fichier "+shapefileName+". Il n'a pas �t� charg� et le r�sultat est null.");
		}
	}
	/**
	 * @return
	 */
	public double getMaxX() {return reader.getMaxX();}
	public double getMinX() {return reader.getMinX();}

	public double getMaxY() {return reader.getMaxY();}
	public double getMinY() {return reader.getMinY();}
}
class Reader {
	private final static Logger logger=Logger.getLogger(Reader.class.getName());
	String shapefileName;
	double minX;
	double maxX;
	double minY;
	double maxY;
	int nbFields;
	int nbFeatures;
	Object[][] fieldValues;
	String[] fieldNames;
	Class<?>[] fieldClasses;
	Geometry[] geometries;
	Class<? extends GM_Object> shapeType;
	public Reader(String shapefileName) {
		this.shapefileName=shapefileName;
		org.geotools.data.shapefile.shp.ShapefileReader shapefileReader = null;
		org.geotools.data.shapefile.dbf.DbaseFileReader dbaseFileReader = null;
		PrjFileReader prjFileReader = null;
		ShpFiles shpf;
		try {
		    shpf = new ShpFiles(shapefileName);
		} catch (MalformedURLException e) {
			if (logger.isDebugEnabled()) logger.debug("URL "+shapefileName+" mal form�e.");
			return;
		} 
		try {
			shapefileReader = new org.geotools.data.shapefile.shp.ShapefileReader(shpf, true, false);
			dbaseFileReader = new org.geotools.data.shapefile.dbf.DbaseFileReader(shpf, true, Charset.defaultCharset() );
		} catch (FileNotFoundException e) {
			if (logger.isDebugEnabled()) logger.debug("fichier "+shapefileName+" non trouv�.");
			return;
		} catch (ShapefileException e) {
			if (logger.isDebugEnabled()) logger.debug("Erreur pendant la lecture du fichier shape "+shapefileName);
			return;
		} catch (IOException e) {
		    if (logger.isDebugEnabled()) logger.debug("Erreur pendant la lecture du fichier "+shapefileName);
		    return;
		}
		try {
		    prjFileReader = new PrjFileReader(shpf);
		} catch (FileNotFoundException e) {
		    if (logger.isDebugEnabled()) logger.debug("fichier prj "+shapefileName+" non trouv�.");
		} catch (ShapefileException e) {
		    if (logger.isDebugEnabled()) logger.debug("Erreur pendant la lecture du fichier prj "+shapefileName);
		} catch (IOException e) {
		    if (logger.isDebugEnabled()) logger.debug("Erreur pendant la lecture du fichier prj "+shapefileName);
		}

		minX=shapefileReader.getHeader().minX();
		maxX=shapefileReader.getHeader().maxX();
		minY=shapefileReader.getHeader().minY();
		maxY=shapefileReader.getHeader().maxY();
		shapeType = geometryType(shapefileReader.getHeader().getShapeType());
		nbFields = dbaseFileReader.getHeader().getNumFields();
		nbFeatures = dbaseFileReader.getHeader().getNumRecords();
		fieldValues = new Object[nbFeatures][nbFields];
		fieldNames = new String[nbFields];
		fieldClasses = new Class<?>[nbFields];
		for(int i = 0 ; i < nbFields ; i++) {
			fieldNames[i] = dbaseFileReader.getHeader().getFieldName(i);
			fieldClasses[i] = dbaseFileReader.getHeader().getFieldClass(i);
		}
		// FIXME g�re le SRID
		//System.out.println("code = "+prjFileReader.getCoodinateSystem().getName().getCode());
		//System.out.println("SRS="+CRS.toSRS(prjFileReader.getCoodinateSystem()));
		/*
		try {
			System.out.println("SRS="+CRS.lookupIdentifier(prjFileReader.getCoodinateSystem(),true));
			System.out.println("SRS="+CRS.lookupEpsgCode(prjFileReader.getCoodinateSystem(),true));
		} catch (FactoryException e1) {
			e1.printStackTrace();
		}
		*/
		geometries = new Geometry[nbFeatures];
		int indexFeatures = 0;
		try {
			while (shapefileReader.hasNext() && dbaseFileReader.hasNext()) {
				Object[] entry = dbaseFileReader.readEntry();
				Record record = shapefileReader.nextRecord();
				try{
					geometries[indexFeatures]=(Geometry)record.shape();
				} catch (Exception e) {
					geometries[indexFeatures]=null;
				}
				for(int index = 0 ; index < nbFields ; index++) fieldValues[indexFeatures][index]=entry[index];
				indexFeatures++;
			}
			shapefileReader.close();
			dbaseFileReader.close();
			if (prjFileReader!=null) prjFileReader.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	/**
	 * Renvoie la valeur de l'attribut minX.
	 * @return la valeur de l'attribut minX
	 */
	public double getMinX() {return this.minX;}
	/**
	 * Renvoie la valeur de l'attribut maxX.
	 * @return la valeur de l'attribut maxX
	 */
	public double getMaxX() {return this.maxX;}
	/**
	 * Renvoie la valeur de l'attribut minY.
	 * @return la valeur de l'attribut minY
	 */
	public double getMinY() {return this.minY;}
	/**
	 * Renvoie la valeur de l'attribut maxY.
	 * @return la valeur de l'attribut maxY
	 */
	public double getMaxY() {return this.maxY;}
	/**
	 * Renvoie la valeur de l'attribut nbFields.
	 * @return la valeur de l'attribut nbFields
	 */
	public int getNbFields() {return this.nbFields;}
	/**
	 * Renvoie la valeur de l'attribut nbFields.
	 * @return la valeur de l'attribut nbFields
	 */
	public int getNbFeatures() {return this.nbFeatures;}
	/**
	 * @param i
	 * @return
	 */
	public String getFieldName(int i) {return fieldNames[i];}
	/**
	 * @param i
	 * @return
	 */
	public Class<?> getFieldClass(int i) {return fieldClasses[i];}
	public Class<? extends GM_Object> getShapeType() {return shapeType;}
	/**
	 * @param name
	 * @return
	 */
	private static Class<? extends GM_Object> geometryType(ShapeType type) {
	    if(logger.isDebugEnabled()) logger.debug("shapeType = "+type);
		if (type.isPointType()) return GM_Point.class;
		if (type.isMultiPointType()) return GM_MultiPoint.class;
		if (type.isLineType()) return GM_MultiCurve.class;
		if (type.isPolygonType()) return GM_MultiSurface.class;
		return GM_MultiSurface.class;
	}
}
