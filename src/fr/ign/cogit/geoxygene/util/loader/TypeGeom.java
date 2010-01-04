/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
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

import java.util.Iterator;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


/**
 * Usage interne.
 * Homogeneise les geometries d'une table geographique :
 * les collections sont repassees en primitive.
 *
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */

public class TypeGeom {

	private Class<?> theClass;
	private Geodatabase db;

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	public TypeGeom(Geodatabase DB, Class<?> TheClass) {
		this.theClass = TheClass;
		this.db = DB;
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	public void multi () {
		try {
			this.db.begin();
			try {
				FT_FeatureCollection<FT_Feature> list;
				try {
					list = this.db.loadAllFeatures(this.theClass);
				} catch (java.lang.OutOfMemoryError mem) {
					System.out.println(" ##### ATTENTION MANQUE DE MEMOIRE POUR TRAITER LA CLASSE "+this.theClass.getName());
					System.out.println(" Pour cette classe, relancer le traitement avec l'option -Xmx ");
					list  = new FT_FeatureCollection<FT_Feature>();
				}
				Iterator<FT_Feature> iterator = list.iterator();
				int i=0 ;
				while (iterator.hasNext()) {
					i++;
					FT_Feature f = iterator.next();
					GM_Object o = f.getGeom();
					if (o instanceof GM_MultiSurface<?>) {
						FT_Feature f1 = f.cloneGeom();
						this.db.deletePersistent(f);
						this.multiSurface(f1);
					}
					else if (o instanceof GM_MultiCurve<?>) {
						FT_Feature f1 = f.cloneGeom();
						this.db.deletePersistent(f);
						this.multiCurve(f1);
					}
					else if (o instanceof GM_MultiPoint) {
						FT_Feature f1 = f.cloneGeom();
						this.db.deletePersistent(f);
						this.multiPoint(f1);
					}
					else if (!((o instanceof GM_Point) ||
							(o instanceof GM_LineString) ||
							(o instanceof GM_Polygon)))
						System.out.println("## ATTENTION ###"+o.getClass().getName()+" - id = "+f.getId());

					/* if ((i%1000) == 0) {
						System.out.println(i+" ...");
						db.checkpoint();
					}*/
				}
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
				//				e1.printStackTrace();
			}

			this.db.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}




	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	private void multiSurface (FT_Feature theFeature) {
		System.out.println(" GM_MultiSurface detecte dans "+this.theClass.getName()+" - id = "+theFeature.getId());
		try {
			GM_MultiSurface<GM_OrientableSurface> ms = (GM_MultiSurface<GM_OrientableSurface>)theFeature.getGeom();
			for(GM_Object p:ms) {
				FT_Feature newFeature = theFeature.cloneGeom();
				newFeature.setGeom(p);
				newFeature.setId(0);
				this.db.makePersistent(newFeature);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}





	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	private void multiCurve (FT_Feature theFeature) {
		System.out.println(" GM_MultiCurve detecte dans "+this.theClass.getName()+" - id = "+theFeature.getId());
		try {
			GM_MultiCurve<GM_OrientableCurve> mc = (GM_MultiCurve<GM_OrientableCurve>)theFeature.getGeom();
			for(GM_Object c:mc) {
				FT_Feature newFeature = theFeature.cloneGeom();
				newFeature.setGeom(c);
				newFeature.setId(0);
				this.db.makePersistent(newFeature);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	private void multiPoint (FT_Feature theFeature) {
		System.out.println(" GM_MultiPoint detecte dans "+this.theClass.getName()+" - id = "+theFeature.getId());
		try {
			GM_MultiPoint mp = (GM_MultiPoint)theFeature.getGeom();
			for(GM_Object p:mp) {
				FT_Feature newFeature = theFeature.cloneGeom();
				newFeature.setGeom(p);
				newFeature.setId(0);
				this.db.makePersistent(newFeature);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
