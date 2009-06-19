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

package fr.ign.cogit.geoxygene.tutorial.exemple.appariement;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.AppariementIO;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.tutorial.data.BdCartoTrRoute;
import fr.ign.cogit.geoxygene.tutorial.data.BdTopoTrRoute;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewer;

/**  Exemple d'appariement entre donn�es routi�res 
 * 
 *  @author Eric Grosso - IGN / Laboratoire COGIT
 */
public class TestAppariementRoutier {


	public static void main(String[] args) {
	
		//Initialisation de la connexion � la base de donn�es
		Geodatabase geodb = GeodatabaseOjbFactory.newInstance();

		//Chargement des donn�es
		
		//Donn�es BDCarto
		FT_FeatureCollection<BdCartoTrRoute> tronconsBDC = geodb.loadAllFeatures(BdCartoTrRoute.class);
		//Donn�es BDTopo
		FT_FeatureCollection<BdTopoTrRoute> tronconsBDT = geodb.loadAllFeatures(BdTopoTrRoute.class);		
		
		
		//Appariement
		
		//Initialisation des param�tres
		ParametresApp param = Parametres.parametresDefaut(tronconsBDC, tronconsBDT);
		
		//Lance les traitement et r�cup�re les liens d'appariement
		List<ReseauApp> cartesTopo = new ArrayList<ReseauApp>();
		EnsembleDeLiens liens = AppariementIO.AppariementDeJeuxGeo(param, cartesTopo);
		
		//R�cup�ration des r�seaux (ReseauApp h�rite de CarteTopo)
		ReseauApp carteTopoBDC = cartesTopo.get(0);
		ReseauApp carteTopoBDT = cartesTopo.get(1);

		//Classement des arcs selon le resultat (�valuation des r�sultats)
		List<String> valeursClassement = new ArrayList<String>();
		valeursClassement.add("Appari�");
		valeursClassement.add("Incertitude");
		valeursClassement.add("Non appari�");

		//R�cup�ration des arcs et des noeuds puis classement en "appari�s", "incertains" ou "non appari�s" (BD r�f�rence)
		List<ReseauApp> cartesTopoBDCValuees = AppariementIO.scindeSelonValeursResultatsAppariement(carteTopoBDC, valeursClassement);
		Population<Arc> arcsBDCApparies = cartesTopoBDCValuees.get(0).getPopArcs();
		Population<Arc> arcsBDCIncertains = cartesTopoBDCValuees.get(1).getPopArcs();
		Population<Arc> arcsBDCNonApparies = cartesTopoBDCValuees.get(2).getPopArcs();
		Population<Noeud> noeudsBDCApparies = cartesTopoBDCValuees.get(0).getPopNoeuds();
		Population<Noeud> noeudsBDCIncertains = cartesTopoBDCValuees.get(1).getPopNoeuds();
		Population<Noeud> noeudsBDCNonApparies = cartesTopoBDCValuees.get(2).getPopNoeuds();

		//R�cup�ration des arcs et des noeuds puis classement en "appari�s", "incertains" ou "non appari�s" (BD comparaison)
		List<ReseauApp> cartesTopoBDTValuees = AppariementIO.scindeSelonValeursResultatsAppariement(carteTopoBDC, valeursClassement);
		Population<Arc> arcsBDTApparies = cartesTopoBDTValuees.get(0).getPopArcs();
		Population<Arc> arcsBDTIncertains = cartesTopoBDTValuees.get(1).getPopArcs();
		Population<Arc> arcsBDTNonApparies = cartesTopoBDTValuees.get(2).getPopArcs();
		Population<Noeud> noeudsBDTApparies = cartesTopoBDTValuees.get(0).getPopNoeuds();
		Population<Noeud> noeudsBDTIncertains = cartesTopoBDTValuees.get(1).getPopNoeuds();
		Population<Noeud> noeudsBDTNonApparies = cartesTopoBDTValuees.get(2).getPopNoeuds();

		
		//R�cup�ration des liens puis classement en surs, incertains et tr�s incertains
		List<Double> valeursClassementL = new ArrayList<Double>();
		valeursClassementL.add(new Double(0.5));
		valeursClassementL.add(new Double(1));
		
		List<EnsembleDeLiens> liensClasses = liens.classeSelonSeuilEvaluation(valeursClassementL);
		EnsembleDeLiens liensNuls = liensClasses.get(0);
		EnsembleDeLiens liensIncertains = liensClasses.get(1);
		EnsembleDeLiens liensSurs = liensClasses.get(2);
		
		//Affichage
		//Initiatlisation des visualisateurs
		//BDTopo
		ObjectViewer viewerCarto = new ObjectViewer();
		//BDCarto
		ObjectViewer viewerTopo = new ObjectViewer();
		//Objets appari�s et non appari�s
		ObjectViewer viewerApp = new ObjectViewer();
		//Appariement et �valuation des liens
		ObjectViewer viewerEval = new ObjectViewer();
		
		//FENETRE BD REFERENCE
		viewerCarto.addFeatureCollection(tronconsBDC,"BDCarto : Tron�ons routiers");
		viewerCarto.addFeatureCollection(carteTopoBDC.getPopArcs(),"Topologie : Arcs");
		viewerCarto.addFeatureCollection(carteTopoBDC.getPopNoeuds(),"R�seau : Noeuds");

		//FENETRE BD COMPARAISON
		viewerTopo.addFeatureCollection(tronconsBDT,"BDTopo : Tron�ons routiers");
		viewerTopo.addFeatureCollection(carteTopoBDT.getPopArcs(),"Topologie : Arcs");
		viewerTopo.addFeatureCollection(carteTopoBDT.getPopNoeuds(),"R�seau : Noeuds");

		//FENETRE APPARIEMENT
		viewerApp.addFeatureCollection(carteTopoBDC.getPopArcs(),"Arcs BDCarto");
		viewerApp.addFeatureCollection(carteTopoBDC.getPopNoeuds(),"Noeuds BDCarto");
		viewerApp.addFeatureCollection(carteTopoBDT.getPopArcs(),"Topologie : Arcs");
		viewerApp.addFeatureCollection(carteTopoBDT.getPopNoeuds(),"Noeuds BDTopo");
		viewerApp.addFeatureCollection(liensNuls,"Liens tr�s peu surs");
		viewerApp.addFeatureCollection(liensIncertains,"Liens incertains");
		viewerApp.addFeatureCollection(liensSurs,"Liens surs");		

		//FENETRE EVALUATION
		viewerEval.addFeatureCollection(arcsBDCApparies,"Arcs BDCarto appari�es");
		viewerEval.addFeatureCollection(arcsBDCIncertains,"Arcs BDCarto incertains");
		viewerEval.addFeatureCollection(arcsBDCNonApparies,"Arcs BDCarto non appari�s");
		viewerEval.addFeatureCollection(noeudsBDCApparies,"Noeuds BDCarto appari�s");
		viewerEval.addFeatureCollection(noeudsBDCIncertains,"Noeuds BDCarto incertains");
		viewerEval.addFeatureCollection(noeudsBDCNonApparies,"Noeuds BDCarto non appari�s");

		viewerEval.addFeatureCollection(arcsBDTApparies,"Arcs BDTopo appari�es");
		viewerEval.addFeatureCollection(arcsBDTIncertains,"Arcs BDTopo incertains");
		viewerEval.addFeatureCollection(arcsBDTNonApparies,"Arcs BDTopo non appari�s");
		viewerEval.addFeatureCollection(noeudsBDTApparies,"Noeuds BDTopo appari�s");
		viewerEval.addFeatureCollection(noeudsBDTIncertains,"Noeuds BDTopo incertains");
		viewerEval.addFeatureCollection(noeudsBDTNonApparies,"Noeuds BDTopo non appari�s");

	}
	
}