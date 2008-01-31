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

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.GroupeApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.NoeudApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.IndicesForme;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Cette classe supporte les methodes d'entr�e pour executer l'appariement 
 * de r�seaux inspir� de la m�thode de [Devogele 97].
 * 
 * NB: Cette classe ne porte QUE les m�thodes concernant l'appariement de cartes topo.
 * Pour un appariement complet de jeux g�o (cr�ation carte topo, appariement, export), 
 * voir la classe appariementIO.
 * 
 * @author Mustiere / IGN Laboratoire COGIT
 * @version 1.0
 * 
 */

/////////////////////////////////////////////////////////////////////////////////////////////////
//NOTE AUX CODEURS, dans le code parfois : reseau 1 = reseau ref, reseau 2 = reseau comp.
/////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class Appariement {

	/** Appariement entre deux r�seaux repr�sent�s par des carte topo. 
	 * Processus largement inspir� de celui d�fini dans la th�se de Thomas Devog�le (1997).
	 * 
	 * Attention : les r�seaux pass�s en entr�e sont modifi�s durant le traitement : des groupes y sont ajout�s.
	 */
	public static EnsembleDeLiens appariementReseaux(ReseauApp reseau1, ReseauApp reseau2, ParametresApp param) {
		EnsembleDeLiens liensPreAppNN ;
		EnsembleDeLiens liensPreAppAA ;
		EnsembleDeLiens liensAppArcs ;
		EnsembleDeLiens liensAppNoeuds ;
		EnsembleDeLiens tousLiens ;

		// Indexation spatiale si cela n'a pas d�j� �t� fait :
		// dallage r�gulier avec en moyenne 20 objets par case
		if ( !reseau1.getPopArcs().hasSpatialIndex()) {
			int nb = (int)Math.sqrt(reseau1.getPopArcs().size()/20);
			if (nb == 0) nb=1;
			reseau1.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
		}

		if ( !reseau2.getPopNoeuds().hasSpatialIndex()) {
			int nb = (int)Math.sqrt(reseau2.getPopNoeuds().size()/20);
			if (nb == 0) nb=1;
			reseau2.getPopNoeuds().initSpatialIndex(Tiling.class, true, nb);
		}


		///////////// APPARIEMENT
		// Pr�appariement de noeuds � noeuds
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Pr�-appariement des noeuds "+(new Time(System.currentTimeMillis())).toString());
		liensPreAppNN = preAppariementNoeudNoeud(reseau1, reseau2, param);

		// Pr�appariement d'arcs � arcs
		if (liensPreAppNN.size() != 0 ) { // il est inutile de pre-appari�er les arcs si rien n'a �t� trouv� sur les noeuds
			// Preappariement des arcs entre eux (bas� principalement sur Hausdorf)
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Pr�-appariement des arcs "+(new Time(System.currentTimeMillis())).toString());
			liensPreAppAA = preAppariementArcArc(reseau1, reseau2, param);
		}
		else {
			liensPreAppAA = new EnsembleDeLiens(LienReseaux.class);
			liensPreAppAA.setNom("Pr�appariement des arcs");
		}

		// Appariement de chaque noeud de la BDref (ind�pendamment les uns des autres)
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Appariement des noeuds "+(new Time(System.currentTimeMillis())).toString());
		liensAppNoeuds = appariementNoeuds(reseau1, reseau2, liensPreAppNN, liensPreAppAA, param);

		nettoyageLiens(reseau1, reseau2, liensPreAppNN);

		if (param.varianteRedecoupageNoeudsNonApparies) {
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Projection plus forte pour les noeuds non appari�s "+(new Time(System.currentTimeMillis())).toString());
			decoupeNoeudsNonApparies(reseau1,reseau2,liensAppNoeuds,param);
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Nettoyage des liens "+(new Time(System.currentTimeMillis())).toString());
			nettoyageLiens(reseau1, reseau2);
			liensPreAppNN.setElements(new ArrayList());
			liensPreAppAA.setElements(new ArrayList());
			liensAppNoeuds.setElements(new ArrayList());
			System.gc();
			// Pr�appariement de noeuds � noeuds
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Pr�-appariement des noeuds "+(new Time(System.currentTimeMillis())).toString());
			liensPreAppNN = preAppariementNoeudNoeud(reseau1, reseau2, param);
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("  Nb de liens de pre-appariement noeud-noeud = "+liensPreAppNN.size());
			// Preappariement des arcs entre eux (bas� principalement sur Hausdroff)
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Pr�-appariement des arcs "+(new Time(System.currentTimeMillis())).toString());
			liensPreAppAA = preAppariementArcArc(reseau1, reseau2, param);
			// Appariement de chaque noeud de la BDref (ind�pendamment)
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Appariement des noeuds "+(new Time(System.currentTimeMillis())).toString());
			liensAppNoeuds = appariementNoeuds(reseau1, reseau2, liensPreAppNN, liensPreAppAA, param);
			nettoyageLiens(reseau1, reseau2, liensPreAppNN);
		}

		// Appariement de chaque arc du reseau 1 
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Appariement des arcs "+(new Time(System.currentTimeMillis())).toString());
		liensAppArcs = appariementArcs(reseau1, reseau2, liensPreAppAA, liensAppNoeuds, param);
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("");
		tousLiens = EnsembleDeLiens.compile(liensAppNoeuds, liensAppArcs);


		if (param.varianteRedecoupageArcsNonApparies) {
			// NB: pas optimal du tout, on recalcule tout apr�s avoir red�coup�.
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Red�coupage plus fort pour les arcs non appari�s "+(new Time(System.currentTimeMillis())).toString());
			decoupeNonApparies(reseau1,reseau2,tousLiens,param);
			nettoyageLiens(reseau1, reseau2);
			liensPreAppNN.setElements(new ArrayList());
			liensPreAppAA.setElements(new ArrayList());
			liensAppNoeuds.setElements(new ArrayList());
			// Pr�appariement de noeuds � noeuds
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Pr�-appariement des noeuds "+(new Time(System.currentTimeMillis())).toString());
			liensPreAppNN = preAppariementNoeudNoeud(reseau1, reseau2, param);
			// Preappariement des arcs entre eux (bas� principalement sur Hausdroff)
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Pr�-appariement des arcs "+(new Time(System.currentTimeMillis())).toString());
			liensPreAppAA = preAppariementArcArc(reseau1, reseau2, param);
			// Appariement de chaque noeud de la BDref (ind�pendamment)
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Appariement des noeuds "+(new Time(System.currentTimeMillis())).toString());
			liensAppNoeuds = appariementNoeuds(reseau1, reseau2, liensPreAppNN, liensPreAppAA, param);
			nettoyageLiens(reseau1, reseau2, liensPreAppNN);
			// Appariement de chaque arc de la BDref 
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Appariement des arcs "+(new Time(System.currentTimeMillis())).toString());
			liensAppArcs = appariementArcs(reseau1, reseau2, liensPreAppAA, liensAppNoeuds, param);
			tousLiens = EnsembleDeLiens.compile(liensAppNoeuds, liensAppArcs);
		}

		nettoyageLiens(reseau1, reseau2, liensPreAppAA);

		// Evaluation globale 
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("  -- Controle global des appariements et bilan "+(new Time(System.currentTimeMillis())).toString());
		controleGlobal(reseau1, reseau2, tousLiens, param);

		//return liens_AppArcs;
		return tousLiens;
	}

	/** Enl�ve tous les liens "liens" des cartes topo en entr�e, et vide les liens "liens" */
	public static void nettoyageLiens(ReseauApp reseau1, ReseauApp reseau2, EnsembleDeLiens liens) {
		nettoyageLiens(reseau1, liens);
		nettoyageLiens(reseau2, liens);
		liens.setElements(new ArrayList());
	}

	/** Enl�ve tous les liens "liens" des cartes topo en entr�e */
	private static void nettoyageLiens(ReseauApp res, EnsembleDeLiens liens) {
		Iterator it;
		it = res.getPopArcs().getElements().iterator();
		while (it.hasNext()) {
			ArcApp objet = (ArcApp) it.next();
			objet.getLiens().removeAll(liens.getElements());
		}
		it = res.getPopNoeuds().getElements().iterator();
		while (it.hasNext()) {
			NoeudApp objet = (NoeudApp) it.next();
			objet.getLiens().removeAll(liens.getElements());
		}
		it = res.getPopGroupes().getElements().iterator();
		while (it.hasNext()) {
			GroupeApp objet = (GroupeApp) it.next();
			objet.getLiens().removeAll(liens.getElements());
		}
	}

	/** Enl�ve tous les liens des cartes topo en entr�e, et d�truit les groupes */
	public static void nettoyageLiens(ReseauApp reseau1, ReseauApp reseau2) {
		nettoyageLiens(reseau1);
		nettoyageLiens(reseau2);
	}

	/** Enl�ve tous les liens de la carte topo en entr�e, et d�truit les groupes */
	public static void nettoyageLiens(ReseauApp res) {
		Iterator it;
		it = res.getPopArcs().getElements().iterator();
		while (it.hasNext()) {
			ArcApp objet = (ArcApp) it.next();
			objet.setLiens(new ArrayList());
		}
		it = res.getPopNoeuds().getElements().iterator();
		while (it.hasNext()) {
			NoeudApp objet = (NoeudApp) it.next();
			objet.setLiens(new ArrayList());
		}
		res.getPopGroupes().setElements(new ArrayList());
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Pr�appariement entre noeuds uniquement sur un crit�re de distance euclidienne, 
	 *  comme propos� dans [Devog�le 97].
	 *
	 *  On cr�e des liens 1-n (n candidats noeuds de BDcomp pour chaque noeud de BDref).
	 *  
	 *  Comme sugg�r� dans [Devog�le 97], la taille de la recherche peut varier selon 
	 *  le type du noeud de la BD de ref�rence (rond point, changement d'attribut...).  
	 *  NB1: On pr�f�re largement une taille de recherche sur-�valu�e que sous-�valu�e.
	 *  NB2: On ne traite pas les noeuds isol�s.
	 */
	public static EnsembleDeLiens preAppariementNoeudNoeud(CarteTopo reseau1, CarteTopo reseau2, ParametresApp param) {
		int nbCandidats = 0, nbRef=0;
		NoeudApp noeudRef, noeudComp;
		List candidats;
		EnsembleDeLiens liens;
		Iterator itNoeuds, itCandidats;
		LienReseaux lien;

		liens = new EnsembleDeLiens(LienReseaux.class);
		liens.setNom("Pr�appariement des noeuds");
		itNoeuds = reseau1.getListeNoeuds().iterator();
		while (itNoeuds.hasNext()) {
			noeudRef = (NoeudApp)itNoeuds.next();
			// On ne tient pas compte des noeuds isol�s 
			if ( noeudRef.getEntrants().size() == 0 && noeudRef.getSortants().size() == 0 ) continue;
			nbRef++;
			// D�termination des noeuds comp dans le rayon de recherche
			candidats = reseau2.getPopNoeuds().select(noeudRef.getGeometrie(), noeudRef.getTaille()).getElements();
			if (candidats.size() != 0) {
				lien = (LienReseaux)liens.nouvelElement();
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				nbCandidats = nbCandidats + candidats.size();
				itCandidats = candidats.iterator();
				while(itCandidats.hasNext()) {
					noeudComp = (NoeudApp)itCandidats.next();
					lien.addNoeuds2(noeudComp);
					noeudComp.addLiens(lien);
				}
			}
		}
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("  Bilan : "+nbCandidats+" noeuds comp candidats pour "+nbRef+" noeuds ref � traiter");
		return liens;
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////
	/** Pr�appariement entre arcs bas� sur la "demi-distance de Hausdorff" 
	 * (on ne prend en compte que la composante de r�seau 2 vers r�seau 1). 
	 * 
	 * Pour chaque arc du reseau 2, on garde les arcs du reseau 1 qui sont � la fois
	 * 1/ � moins distanceMax de l'arc comp
	 * 2/ � moins de D + distanceMin de l'arc comp, 
	 * 		D �tant la distance entre l'arc ref le plus proche de arc comp 
	 * 
	 * NB: ce pr�-appariement est diff�rent de ce qui est propos� dans [Devog�le 97], 
	 * pour minimiser la sensibilit� aux seuils.
	 * 
	 * On cr�e des liens 1-n: (1 arc de comp) - (n arcs de ref). 
	 * Un arc de ref peut �tre alors concern� par plusieurs liens diff�rents.
	 * Au total on a donc des relations n-m cod�es sous la forme de n relations 1-m
	 */
	public static EnsembleDeLiens preAppariementArcArc(CarteTopo reseau1, CarteTopo reseau2, ParametresApp param) {
		int nbCandidats = 0;
		ArcApp arcComp, arcRef;
		List arcsProches, candidats, distances = new ArrayList();;
		LienReseaux lien;
		EnsembleDeLiens liens; 
		Iterator itArcs, itArcsProches, itCandidats;
		double dmin, dmax, d;

		liens = new EnsembleDeLiens(LienReseaux.class);
		liens.setNom("Pr�appariement des arcs");
		itArcs = reseau2.getListeArcs().iterator();
		while (itArcs.hasNext()) {
			arcComp = (ArcApp)itArcs.next();
			// On recherche les arcs dans l'entourage proche, grosso modo
			arcsProches = reseau1.getPopArcs().select(arcComp.getGeometrie(), param.distanceArcsMax).getElements();
			if (arcsProches.size() == 0) continue;
			// On calcule leur distance � arccomp et on recherche le plus proche
			distances.clear();
			itArcsProches = arcsProches.iterator();
			arcRef = (ArcApp) itArcsProches.next();
			dmin = arcComp.premiereComposanteHausdorff(arcRef,param.distanceArcsMax);
			distances.add(new Double(dmin));
			while (itArcsProches.hasNext()) {
				arcRef = (ArcApp) itArcsProches.next();
				d = arcComp.premiereComposanteHausdorff(arcRef,param.distanceArcsMax);
				distances.add(new Double(d));
				if (d<dmin) dmin=d;
			}
			// On garde tous ceux assez proches
			dmax = Math.min(dmin+param.distanceArcsMin, param.distanceArcsMax);
			candidats = new ArrayList();
			for (int i=0;i<arcsProches.size();i++) {
				d = ((Double)distances.get(i)).doubleValue();
				if ( d<dmax ) candidats.add(arcsProches.get(i));
			}

			// Si pas de candidat pour l'arccomp, on s'arr�te l�
			if (candidats.size()==0) continue; 
			// Si il y a des candidats: on construit le lien de pr�-appariement			
			lien = (LienReseaux)liens.nouvelElement();
			arcComp.addLiens(lien);
			lien.addArcs2(arcComp);
			nbCandidats = nbCandidats + candidats.size();
			itCandidats = candidats.iterator();
			while (itCandidats.hasNext()) {
				arcRef = (ArcApp) itCandidats.next();
				lien.addArcs1(arcRef);
				arcRef.addLiens(lien);
			}
		}
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("  Bilan : "+nbCandidats+" arcs comp candidats pour "+reseau1.getListeArcs().size()+" arcs ref");
		return liens;
	}


	/** Appariement des Noeuds du reseau 1 avec les arcs et noeuds du reseau 1, 
	 *  comme propos� dans [Devog�le 97] + modif au filtrage Seb
	 *  On cr�e les liens qui vont bien si le noeud est appari�.
	 *  Une �valuation de l'appariement est stock�e sur les liens (note de 0, 0.5 ou 1)
	 *  Une explication plus d�taill�e du r�sultat est stock�e sur les noeuds ref et comp.
	 */
	public static EnsembleDeLiens appariementNoeuds(
			CarteTopo reseau1, CarteTopo reseau2, 
			EnsembleDeLiens liensPreAppNN, EnsembleDeLiens liensPreAppAA, 
			ParametresApp param) {
		int j, k, l;
		Population groupesComp = reseau2.getPopGroupes();
		List complets, complets2, incomplets, incomplets2,
			noeudsCompCandidats, noeudsIncomplets, groupesConnexes, entrants, liensDuNoeudRef;
		NoeudApp noeudRef, noeudComp, noeudDeb;
		GroupeApp groupeComp;
		int nbSansHomologue = 0, nbNonTraite = 0, nbPlusieursNoeudsComplets = 0,
			nbPlusieursGroupesComplets = 0, nbNoeudNoeud = 0, nbNoeudGroupe = 0,
			nbNoeudGroupeIncertain = 0, nbNoeudNoeudIncertain = 0;
		int correspondance;
		Iterator itNoeuds;
		LienReseaux lien;
		EnsembleDeLiens liens;

		liens = new EnsembleDeLiens(LienReseaux.class);
		liens.setNom("Appariement des noeuds");
		// On initialise le resultat � "non appari�" pour les noeuds comp
		itNoeuds = reseau2.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			noeudComp = (NoeudApp)itNoeuds.next();
			noeudComp.setResultatAppariement("Non appari�");
		}

		// On traite chaque noeud ref, un par un
		itNoeuds = reseau1.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			noeudRef = (NoeudApp)itNoeuds.next();
			noeudRef.setResultatAppariement("Bug: valeur non remplie"); // pour d�tecter les cas non trait�s
			// On ne traite pas les noeuds isol�s
			if ( noeudRef.arcs().size() == 0 ) {
				noeudRef.setResultatAppariement("Non trait� : noeud isol�");
				nbNonTraite ++;
				continue;
			}

			liensDuNoeudRef = new ArrayList(noeudRef.getLiens(liensPreAppNN.getElements()));

			//////////////////////////////////////////////////////////////////
			// Noeud ref qui n'a aucun noeud comp candidat dans le pr�-appariement
			if (liensDuNoeudRef.size() == 0) {
				noeudRef.setResultatAppariement("Non appari�, aucun candidat");
				nbSansHomologue++;
				continue;
			}

			//////////////////////////////////////////////////////////////////
			// Noeud ref qui a un ou plusieurs candidats (r�unis dans un seul lien par construction)
			//
			// On qualifie des noeuds comp candidats en comparant les entrants/sortants 
			// Un noeud comp est dit, par rapport au noeud ref, :
			//   - complet si on trouve une correspondance entre tous les incidents des noeuds ref et comp,
			//   - incomplet si on trouve une correspondance entre certains incidents
			//   - impossible si on ne trouve aucune correspondance
			noeudsCompCandidats = ((LienReseaux)liensDuNoeudRef.get(0)).getNoeuds2();
			complets = new ArrayList();
			incomplets = new ArrayList();
			Iterator itNoeudsComp = noeudsCompCandidats.iterator();
			while (itNoeudsComp.hasNext()) {
				noeudComp = (NoeudApp)itNoeudsComp.next();                
				correspondance = noeudRef.correspCommunicants(noeudComp, liensPreAppAA);
				if (  correspondance == 1 ) complets.add(noeudComp); 
				if (  correspondance == 0 ) incomplets.add(noeudComp);
			}

			////////////////////////////////////////////////////////////////////
			// Cas d'un appariement simple : 1 noeud BDref correspond � un noeud BDComp
			// C'est un appariement que l'on juge s�r.
			if ( complets.size() == 1 ) {
				noeudComp = (NoeudApp)complets.get(0);
				noeudRef.setResultatAppariement("Appari� avec un noeud");
				noeudComp.setResultatAppariement("Appari� avec un noeud");
				nbNoeudNoeud ++;
				lien = (LienReseaux)liens.nouvelElement();
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				lien.addNoeuds2(noeudComp);
				noeudComp.addLiens(lien);
				lien.setEvaluation(1);
				continue;
			}

			/////////////////////////////////////////////////////////////////////////
			// Cas o� plusieurs noeuds comp candidats sont complets.
			// Ce cas ne devrait en th�orie pas arriver d'apr�s Thomas, 
			// mais si en pratique les seuils sont larges, c'est un cas courant
			//
			// Ajout Seb pour filtrer ce sur-appariement : on choisit le plus proche (devrait �tre affin�) 
			if ( complets.size() > 1 ) {
				filtrageNoeudsComplets(complets, noeudRef); 
				if ( complets.size() == 1 ) {
					noeudRef.setResultatAppariement("Incertitude : choix fait parmi plusieurs noeuds complets");
					nbPlusieursNoeudsComplets++;
					lien = (LienReseaux)liens.nouvelElement();
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					noeudComp = (NoeudApp)complets.get(0);
					noeudComp.setResultatAppariement("Incertitude : choisi parmi plusieurs noeuds complets");
					lien.addNoeuds2(noeudComp);
					noeudComp.addLiens(lien);
					lien.setEvaluation(0.5);
					continue;
				}
				// Si il reste plusieurs noeuds complets apr�s le filtrage
				// NB : cas impossible dans l'�tat actuel du filtrage qui ne garde qu'un noeud complet
				noeudRef.setResultatAppariement("Incertitude : appari� avec plusieurs noeuds complets");
				nbPlusieursNoeudsComplets++; 
				lien = (LienReseaux)liens.nouvelElement();
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				for (j=0; j<complets.size(); j++) {
					noeudComp = (NoeudApp)complets.get(j);
					noeudComp.setResultatAppariement("Incertitude : plusieurs noeuds complets concurrents");
					lien.addNoeuds2(noeudComp);
					noeudComp.addLiens(lien);
				}
				lien.setEvaluation(0);
				continue;
			}

			///////////////////////////////////////////////////////////////////////////
			// Cas o� il n'y a pas de noeud comp complet, mais un seul incomplet
			// Appariement simple : 1 noeud ref correspond � un noeud comp, 
			// mais c'est un appariement que l'on juge incertain
			if ( incomplets.size() == 1 ) {
				noeudComp = (NoeudApp)incomplets.get(0);
				noeudRef.setResultatAppariement("Incertitude : appari� avec un noeud incomplet");
				noeudComp.setResultatAppariement("Incertitude : noeud incomplet");
				nbNoeudNoeudIncertain ++;
				lien = (LienReseaux)liens.nouvelElement();
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				lien.addNoeuds2(noeudComp);
				noeudComp.addLiens(lien);
				lien.setEvaluation(0.5);
				continue;
			}

			//////////////////////////////////////////////////////////////////
			// Le noeud ref n'a que des noeuds comp candidats impossibles  
			// i.e. ils n'ont aucun arc sortant appari� avec un des arcs sortant du noeudref
			if ( incomplets.size() == 0 ) {
				noeudRef.setResultatAppariement("Non appari�, tous candidats impossibles");
				nbSansHomologue++;
				continue;
			}

			//////////////////////////////////////////////////////////////////
			// Si il y a plusieurs noeuds incomplets, 
			// MAIS on autorise uniquement les appariement 1-1 et 1-0 aux noeuds
			// On choisit alors le noeud incomplet le plus proche
			if (param.varianteForceAppariementSimple) {
				noeudComp = noeudLePlusProche(incomplets, noeudRef);
				noeudRef.setResultatAppariement("Incertitude : appari� avec un noeud incomplet");
				noeudComp.setResultatAppariement("Incertitude : noeud incomplet");
				nbNoeudNoeudIncertain ++;
				lien = (LienReseaux)liens.nouvelElement();
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				lien.addNoeuds2(noeudComp);
				noeudComp.addLiens(lien);
				lien.setEvaluation(0.5);
				continue;
			}

			/////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////
			// DEBUT TRAITEMENT DES CARREFOURS COMPLEXES :
			// (quand un noeud ref correpsond � un ensemble de noeuds et arcs dans BDcomp)
			/////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////

			// On cr�e un nouveau groupe contenant tous les noeuds incomplets ainsi que
			// tous les arcs ayant aux 2 extr�mit�s un de ces noeuds
			groupeComp = (GroupeApp)groupesComp.nouvelElement();
			noeudsIncomplets = new ArrayList(incomplets); //ajout pour mieux g�rer le cas des groupes impossibles
			for (j=0;j<incomplets.size();j++) {
				noeudComp = (NoeudApp)incomplets.get(j);
				groupeComp.addNoeud(noeudComp);
				//noeudcomp.addGroupe(groupecomp);
				entrants = noeudComp.getEntrants();
				for (k=0;k<entrants.size();k++) {
					noeudDeb = (NoeudApp)((ArcApp)entrants.get(k)).getNoeudIni();
					for (l=0;l<incomplets.size();l++) {
						if ( noeudDeb == incomplets.get(l) ) {
							groupeComp.addArc((ArcApp)entrants.get(k));
							//((Arc_App)entrants.get(k)).addGroupe(groupecomp);
							break;
						}
					}
				}
			}

			///////////////////////////////////////////////////////////////////////////////
			// decomposition du groupe cr�� en groupes connexes
			groupesConnexes = groupeComp.decomposeConnexes();

			///////////////////////////////////////////////////////////////////////////////
			// Choix du groupe connexe � conserver :
			// Qualification des groupes candidats en comparant les entrants/sortants 
			// Un groupe comp est dit, par rapport au noeud ref, :
			//   - complet si on trouve une correspondance entre tous les incidents des noeuds ref et comp,
			//   - incomplet si on trouve une correspondance entre certains incidents
			//   - impossible si on ne trouve aucune correspondance
			// NB: methode strictement similaire � ce qui �tait r�alis� sur les noeuds
			complets = new ArrayList();
			incomplets = new ArrayList();

			for (j=0;j<groupesConnexes.size();j++) {
				groupeComp = (GroupeApp)groupesConnexes.get(j);
				correspondance = noeudRef.correspCommunicants(groupeComp, liensPreAppAA);
				if (  correspondance == 1 ) complets.add(groupeComp); 
				if (  correspondance == 0 ) incomplets.add(groupeComp);
			}

			///////////////////////////////////////////////////////////////////////////////
			// On a trouv� un unique groupe complet 
			// C'est un appariement que l'on juge s�r
			if ( complets.size() == 1 ) {
				// vidage des groupes rejet�s, pour faire propre
				for(j=0; j<incomplets.size();j++) {
					((GroupeApp)incomplets.get(j)).vide();
				}
				groupeComp = (GroupeApp)complets.get(0);
				// filtrage du groupe connexe choisi
				groupeComp.filtrageGroupePendantAppariementDesNoeuds(noeudRef, liensPreAppAA);
				if ( groupeComp.getListeNoeuds().size() == 0 ) {
					noeudRef.setResultatAppariement("Non appari�, unique groupe complet vid� au filtrage");
					nbSansHomologue = nbSansHomologue+1;
					continue;
				}
				if ( (groupeComp.getListeNoeuds().size() == 1) && (groupeComp.getListeArcs().size() == 0) ) {
					noeudComp = (NoeudApp)groupeComp.getListeNoeuds().get(0);
					noeudRef.setResultatAppariement("Appari� � un noeud");
					noeudComp.setResultatAppariement("Appari� � un noeud");
					groupeComp.vide();
					nbNoeudNoeud++;
					lien = new LienReseaux();
					liens.add(lien);
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					lien.addNoeuds2(noeudComp);
					noeudComp.addLiens(lien);
					lien.setEvaluation(0.5);
					continue;
				}
				else {
					noeudRef.setResultatAppariement("Appari� � un groupe");
					groupeComp.setResultatAppariement("Appari� � un noeud");
					nbNoeudGroupe++;
					for(j=0;j<groupeComp.getListeArcs().size() ;j++) {
						((ArcApp)groupeComp.getListeArcs().get(j)).setResultatAppariement("Appari� � un noeud, dans un groupe");
					}
					for(j=0;j<groupeComp.getListeNoeuds().size() ;j++) {
						((NoeudApp)groupeComp.getListeNoeuds().get(j)).setResultatAppariement("Appari� � un noeud, dans un groupe");
					}
					lien = new LienReseaux();
					liens.add(lien);
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					lien.addGroupes2(groupeComp);
					groupeComp.addLiens(lien);
					lien.setEvaluation(1);
					continue;
				}
			}

			///////////////////////////////////////////////////////////////////////////////
			// plusieurs groupes sont bien appari�s (complets) avec un noeud ref
			// On est sur qu'il y a sur-appariement (� raffiner dans le futur si possible)
			// Cas qui ne devrait pas arriver d'apr�s Thomas (si les seuils sont bien choisis !) 
			if ( complets.size() > 1 ) {
				// vidage des groupes rejet�s, pour faire propre
				for(j=0; j<incomplets.size();j++) {
					((GroupeApp)incomplets.get(j)).vide();
				}

				complets2 = new ArrayList();
				for (j=0; j<complets.size(); j++) {
					groupeComp = (GroupeApp)complets.get(j);
					groupeComp.filtrageGroupePendantAppariementDesNoeuds(noeudRef, liensPreAppAA);
					if ( groupeComp.getListeNoeuds().size() != 0 ) complets2.add(groupeComp);
				}
				complets = new ArrayList(complets2);

				// aucun goupe complet restant apr�s les filtrages
				if ( complets.size() == 0 ) {
					noeudRef.setResultatAppariement("Non appari�, groupes complets vid�s au filtrage");
					nbSansHomologue++;
					continue;
				}

				// un seul goupe complet restant apr�s les filtrages
				if ( complets.size() == 1 ) {
					lien = new LienReseaux();
					liens.add(lien);
					noeudRef.setResultatAppariement("Incertitude : appari� � un groupe complet, autres groupes complets vid�s au filtrage");
					nbNoeudGroupeIncertain++;
					groupeComp = (GroupeApp)complets.get(0);
					groupeComp.setResultatAppariement("Incertitude : seul groupe complet, autres groupes complets vid�s au filtrage");
					for(k=0;k<groupeComp.getListeArcs().size() ;k++) {
						((ArcApp)groupeComp.getListeArcs().get(k)).setResultatAppariement("Incertitude : dans un seul groupe complet, autres groupes complets vid�s au filtrage");
					}
					for(k=0;k<groupeComp.getListeNoeuds().size() ;k++) {
						((NoeudApp)groupeComp.getListeNoeuds().get(k)).setResultatAppariement("Incertitude : dans un seul groupe complet, autres groupes complets vid�s au filtrage");
					}
					lien.setEvaluation(0.5);
					lien.addGroupes2(groupeComp);
					groupeComp.addLiens(lien);
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					continue;
				}

				// plusieurs goupes complets restant apr�s les filtrages
				lien = new LienReseaux();
				liens.add(lien);
				noeudRef.setResultatAppariement("Incertitude : appari� � plusieurs groupes complets");
				nbPlusieursGroupesComplets++; 
				for (j=0; j<complets.size(); j++) {
					groupeComp = (GroupeApp)complets.get(j);
					groupeComp.setResultatAppariement("Incertitude : un de plusieurs groupes complets concurrents");
					for(k=0;k<groupeComp.getListeArcs().size() ;k++) {
						((ArcApp)groupeComp.getListeArcs().get(k)).setResultatAppariement("Incertitude : dans un de plusieurs groupes complets concurrents");
					}
					for(k=0;k<groupeComp.getListeNoeuds().size() ;k++) {
						((NoeudApp)groupeComp.getListeNoeuds().get(k)).setResultatAppariement("Incertitude : dans un de plusieurs groupes complets concurrents");
					}
					lien.addGroupes2(groupeComp);
					groupeComp.addLiens(lien);
				}
				lien.setEvaluation(0);
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				continue;
			}

			///////////////////////////////////////////////////////////////////////////////
			// On a trouv� un unique groupe incomplet 
			// C'est un appariement que l'on accepte mais qu'on l'on juge incertain
			if ( incomplets.size() == 1 ) {
				groupeComp = (GroupeApp)incomplets.get(0);
				groupeComp.filtrageGroupePendantAppariementDesNoeuds(noeudRef, liensPreAppAA);
				if ( groupeComp.getListeNoeuds().size() == 0 ) {
					noeudRef.setResultatAppariement("Non appari�, seul goupe incomplet vid� au filtrage");
					nbSansHomologue++;
					continue;
				}
				if ( (groupeComp.getListeNoeuds().size() == 1) && (groupeComp.getListeArcs().size() == 0) ) {
					noeudComp = (NoeudApp)groupeComp.getListeNoeuds().get(0);
					noeudRef.setResultatAppariement("Appari� � un noeud");
					noeudComp.setResultatAppariement("Appari� � un noeud");
					groupeComp.vide();
					nbNoeudNoeudIncertain++;
					lien = new LienReseaux();
					liens.add(lien);
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					lien.addNoeuds2(noeudComp);
					noeudComp.addLiens(lien);
					lien.setEvaluation(0.5);
					continue;
				}
				else {
					noeudRef.setResultatAppariement("Incertitude : appari� � un groupe incomplet");
					nbNoeudGroupeIncertain = nbNoeudGroupeIncertain + 1;
					groupeComp.setResultatAppariement("Incertitude : groupe incomplet");
					for(k=0;k<groupeComp.getListeArcs().size() ;k++) {
						((ArcApp)groupeComp.getListeArcs().get(k)).setResultatAppariement("Incertitude : dans groupe incomplet");
					}
					for(k=0;k<groupeComp.getListeNoeuds().size() ;k++) {
						((NoeudApp)groupeComp.getListeNoeuds().get(k)).setResultatAppariement("Incertitude : dans groupe incomplet");
					}
					//param.db.create(groupeComp);
					lien = new LienReseaux();
					liens.add(lien);
					lien.setEvaluation(0.5);
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					lien.addGroupes2(groupeComp);
					groupeComp.addLiens(lien);
					continue;
				}
			}

			///////////////////////////////////////////////////////////////////////////////
			// On a trouv� plusieurs groupes incomplets 
			// C'est un appariement que l'on accepte peut-�tre mais que l'on sait incoh�rent
			if ( incomplets.size() > 1 ) {
				incomplets2 = new ArrayList();
				for (j=0; j<incomplets.size(); j++) {
					groupeComp = (GroupeApp)incomplets.get(j);
					groupeComp.filtrageGroupePendantAppariementDesNoeuds(noeudRef, liensPreAppAA);
					if ( groupeComp.getListeNoeuds().size() != 0 ) incomplets2.add(groupeComp);
				}
				incomplets = new ArrayList(incomplets2);

				// aucun goupe incomplet restant apr�s le filtrage
				if ( incomplets.size() == 0 ) {
					noeudRef.setResultatAppariement("Non appari�, groupes incomplets vid�s au filtrage");
					nbSansHomologue = nbSansHomologue+1;
					continue;
				}

				// un seul goupe incomplet restant apr�s le filtrage
				if ( incomplets.size() == 1 ) {
					lien = new LienReseaux();
					liens.add(lien);
					groupeComp = (GroupeApp)incomplets.get(0);
					groupeComp.setResultatAppariement("Incertitude : incomplet, autres groupes incomplets vid�s au filtrage");
					for(k=0;k<groupeComp.getListeArcs().size() ;k++) {
						((ArcApp)groupeComp.getListeArcs().get(k)).setResultatAppariement("Incertitude : dans groupe incomplet, autres groupes incomplets vid�s au filtrage");
					}
					for(k=0;k<groupeComp.getListeNoeuds().size() ;k++) {
						((NoeudApp)groupeComp.getListeNoeuds().get(k)).setResultatAppariement("Incertitude : dans groupe incomplet, autres groupes incomplets vid�s au filtrage");
					}
					lien.addGroupes2(groupeComp);
					groupeComp.addLiens(lien);
					noeudRef.setResultatAppariement("Incertitude : appari� � groupe incomplet, autres groupes incomplets vid�s au filtrage");
					nbNoeudGroupeIncertain++;
					lien.addNoeuds1(noeudRef);
					noeudRef.addLiens(lien);
					lien.setEvaluation(0.5);
					continue;
				}

				// plusieurs goupes incomplets restant apr�s les filtrages
				lien = new LienReseaux();
				liens.add(lien);
				for (j=0; j<incomplets.size(); j++) {
					groupeComp = (GroupeApp)incomplets.get(j);
					groupeComp.setResultatAppariement("Incertitude : parmi plusieurs groupes incomplets");
					for(k=0;k<groupeComp.getListeArcs().size() ;k++) {
						((ArcApp)groupeComp.getListeArcs().get(k)).setResultatAppariement("Incertitude : dans un de plusieurs groupes incomplets");
					}
					for(k=0;k<groupeComp.getListeNoeuds().size() ;k++) {
						((NoeudApp)groupeComp.getListeNoeuds().get(k)).setResultatAppariement("Incertitude : dans un de plusieurs groupes incomplets");
					}
					lien.addGroupes2(groupeComp);
					groupeComp.addLiens(lien);
				}
				noeudRef.setResultatAppariement("Incertitude : appari� � plusieurs groupes incomplets");
				nbPlusieursGroupesComplets++; 
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				lien.setEvaluation(0);
				continue;
			}

			if ( incomplets.size() == 0 ) { // ajout Seb 31/05/05
				noeudRef.setResultatAppariement("Incertitude : groupes candidats impossibles, choix fait parmis les noeuds incomplets");
				nbNoeudNoeudIncertain++; 
				lien = new LienReseaux();
				liens.add(lien);
				lien.addNoeuds1(noeudRef);
				noeudRef.addLiens(lien);
				lien.setEvaluation(0.5);
				for (j=0; j<noeudsIncomplets.size(); j++) {
					noeudComp = (NoeudApp)noeudsIncomplets.get(j);
					noeudComp.setResultatAppariement("Incertitude : groupes candidats impossibles, choix fait parmis les noeuds incomplets");
					lien.addNoeuds2(noeudComp);
					noeudComp.addLiens(lien);
				}
				continue;
			}
		}

		// pour le r�seau routier uniquement: on traite le cas des rond-points
		// si un noeud est appari� avec une partie de rond point, 
		// il devient appari� avec l'ensemble du rond point
		if ( param.varianteChercheRondsPoints) rondsPoints(reseau1, reseau2, liens, liensPreAppAA, param); 


		// Fin, affichage du bilan
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("  Bilan des noeuds:");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    Appariement jug�s corrects : ");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbNoeudNoeud+" noeuds 1 appari�s avec un seul noeud");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbNoeudGroupe+" noeuds 1 appari�s avec un groupe");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    Appariement jug�s incertains : ");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbNoeudNoeudIncertain+" noeuds 1 appari�s avec un noeud incomplet");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbPlusieursNoeudsComplets+" noeuds 1 avec plusieurs noeuds complets");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbNoeudGroupeIncertain+" noeuds 1 appari�s avec un groupe incomplet");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    Appariement jug�s incoh�rents : ");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbSansHomologue+" noeuds 1 sans homolgues trouv�s dans le pr�appariement");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbPlusieursGroupesComplets+" noeuds 1 avec plusieurs homologues groupes");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    Noeuds non trait�s : ");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbNonTraite+" noeuds 1 isol�s");
		return liens;
	}

	// pour le r�seau routier uniquement: on traite le cas des rond-points
	// si un noeud est appari� avec une partie de rond point, 
	// il devient appari� avec l'ensemble du rond point
	private static void rondsPoints(CarteTopo reseau1, CarteTopo reseau2, EnsembleDeLiens liens_Noeuds, EnsembleDeLiens liensPreAppAA, ParametresApp param) {
		double compacite;
		Iterator itGroupesDuNoeud, itNoeudsDuRondPoint, itNoeudsDuRondPoint2, itArcsDuRondPoint, itLiensDuNoeudsDuRondPoint;
		List noeudsDuRondPoint, liensDuNoeudDuRondPoint;
		LienReseaux lien;
		NoeudApp noeud, noeud2, noeudRef;
		GroupeApp rondPoint;
		ArcApp arc2;
		Iterator itFaces = reseau2.getPopFaces().getElements().iterator();

		while (itFaces.hasNext()) {
			Face face = (Face) itFaces.next();
			compacite = IndicesForme.indiceCompacite(face.getGeometrie());
			if ( compacite < 0.95) continue;
			if (face.getGeometrie().area() > 8000 ) continue; //rond point de diametre>100m
			noeudsDuRondPoint = face.noeuds();
			itNoeudsDuRondPoint = noeudsDuRondPoint.iterator();
			while (itNoeudsDuRondPoint.hasNext()) {
				noeud = (NoeudApp) itNoeudsDuRondPoint.next();
				liensDuNoeudDuRondPoint = noeud.getLiens(liens_Noeuds.getElements());
				itLiensDuNoeudsDuRondPoint = liensDuNoeudDuRondPoint.iterator();
				// Pour un lien d'un noeud du rond point, 2 cas possibles: 
				// 1/ avant ce lien ne pointait que vers ce noeud
				while (itLiensDuNoeudsDuRondPoint.hasNext()) {
					lien = (LienReseaux)itLiensDuNoeudsDuRondPoint.next();
					noeudRef = (NoeudApp)lien.getNoeuds1().get(0);
					if ( lien.getNoeuds2().size() == 1 ) { // cas 1
						rondPoint = (GroupeApp)reseau2.getPopGroupes().nouvelElement();
						rondPoint.addAllNoeuds(noeudsDuRondPoint);
						rondPoint.addAllArcs(face.arcs());
						lien.getNoeuds2().clear();
						noeud.getLiens().remove(lien);
						lien.addGroupes2(rondPoint);
						rondPoint.addLiens(lien);
						if (noeudRef.correspCommunicants(rondPoint, liensPreAppAA)==1) { 
							lien.setEvaluation(1);
							rondPoint.setResultatAppariement("Appari� � un noeud (rond-point)");
							for(int k=0;k<rondPoint.getListeArcs().size() ;k++) {
								((ArcApp)rondPoint.getListeArcs().get(k)).setResultatAppariement("Appari� � un noeud, dans un rond-point");
							}
							for(int k=0;k<rondPoint.getListeNoeuds().size() ;k++) {
								((NoeudApp)rondPoint.getListeNoeuds().get(k)).setResultatAppariement("Appari� � un noeud, dans un rond-point");
							}
						}
						else {
							rondPoint.setResultatAppariement("Incertitude: Appari� � un noeud (rond-point incomplet)");
							for(int k=0;k<rondPoint.getListeArcs().size() ;k++) {
								((ArcApp)rondPoint.getListeArcs().get(k)).setResultatAppariement("Incertitude: Appari� � un noeud, dans un rond-point incomplet");
							}
							for(int k=0;k<rondPoint.getListeNoeuds().size() ;k++) {
								((NoeudApp)rondPoint.getListeNoeuds().get(k)).setResultatAppariement("Incertitude: Appari� � un noeud, dans un rond-point incomplet");
							}
						}
						continue;					
					}
				}
				// 2/ avant ce lien pointait vers une partie du rond point
				itGroupesDuNoeud = noeud.getListeGroupes().iterator();
				while (itGroupesDuNoeud.hasNext()) {
					GroupeApp groupe = (GroupeApp) itGroupesDuNoeud.next();
					List liens = groupe.getLiens(liens_Noeuds.getElements());
					if (liens.size() == 0) continue;
					if ( groupe.getLiens().size() == 0 ) continue;
					if ( liens_Noeuds.getElements().contains(groupe.getLiens().get(0)) ) {
						itNoeudsDuRondPoint2 = noeudsDuRondPoint.iterator();
						while (itNoeudsDuRondPoint2.hasNext()) {
							noeud2 = (NoeudApp)itNoeudsDuRondPoint2.next();
							if ( !groupe.getListeNoeuds().contains(noeud2) ) groupe.addNoeud(noeud2); 
						}
						itArcsDuRondPoint = face.arcs().iterator();
						while (itArcsDuRondPoint.hasNext()) {
							arc2 = (ArcApp)itArcsDuRondPoint.next();
							if ( !groupe.getListeArcs().contains(arc2) ) groupe.addArc(arc2);
						} 
					}
					lien = (LienReseaux)liens.get(0);
					noeudRef = (NoeudApp)lien.getNoeuds1().get(0);
					if (noeudRef.correspCommunicants(groupe, liensPreAppAA)==1) { 
						lien.setEvaluation(1);
						groupe.setResultatAppariement("Appari� � un noeud (rond-point)");
						for(int k=0;k<groupe.getListeArcs().size() ;k++) {
							((ArcApp)groupe.getListeArcs().get(k)).setResultatAppariement("Appari� � un noeud, dans un rond-point");
						}
						for(int k=0;k<groupe.getListeNoeuds().size() ;k++) {
							((NoeudApp)groupe.getListeNoeuds().get(k)).setResultatAppariement("Appari� � un noeud, dans un rond-point");
						}
					}
					else {
						groupe.setResultatAppariement("Incertitude: Appari� � un noeud (rond-point incomplet)");
						for(int k=0;k<groupe.getListeArcs().size() ;k++) {
							((ArcApp)groupe.getListeArcs().get(k)).setResultatAppariement("Incertitude: Appari� � un noeud, dans un rond-point incomplet");
						}
						for(int k=0;k<groupe.getListeNoeuds().size() ;k++) {
							((NoeudApp)groupe.getListeNoeuds().get(k)).setResultatAppariement("Incertitude: Appari� � un noeud, dans un rond-point incomplet");
						}
					}

				}
			}
		}
	}

	/** on ne garde que le noeud comp le plus proche */
	private static void filtrageNoeudsComplets(List complets, NoeudApp noeudRef) {
		int i;
		NoeudApp noeudComp, noeudPlusProche;
		double d, dmin;

		if ( complets.size() <= 1 ) return;
		noeudPlusProche = (NoeudApp)complets.get(0);
		dmin = noeudPlusProche.distance(noeudRef);
		for(i=1;i<complets.size() ;i++) {
			noeudComp = (NoeudApp)complets.get(i);
			d = noeudComp.distance(noeudRef);
			if ( d < dmin) {
				dmin = d;
				noeudPlusProche = noeudComp;
			}
		}
		complets.clear();
		complets.add(noeudPlusProche);
	}

	/** renvoie le noeud de 'noeuds' le plus proche de 'noeudRef' */
	private static NoeudApp noeudLePlusProche(List noeuds, NoeudApp noeudRef) {
		if ( noeuds.size() < 1 ) return null;
		Iterator itNoeuds = noeuds.iterator();
		NoeudApp plusProche = (NoeudApp) itNoeuds.next();
		double dmin = plusProche.distance(noeudRef);
		while (itNoeuds.hasNext()) {
			NoeudApp noeud = (NoeudApp) itNoeuds.next();
			double d = noeud.distance(noeudRef);
			if ( d < dmin) {
				dmin = d;
				plusProche = noeud;
			}
		}
		return plusProche;
	}

	/** Appariement des arcs, s'appuyant sur un appariement pr�alable des noeuds,
	 *  et sur un pr�-appariement des arcs.
	 *  S'appuie essentiellement sur la notion de 'plus proche chemin d'un arc', d�fini
	 *  comme le chemin minimisant la surface entre le chemin et l'arc.
	 */
	public static EnsembleDeLiens appariementArcs(CarteTopo reseau1, 
			CarteTopo reseau2, 
			EnsembleDeLiens liensPreAppAA, 
			EnsembleDeLiens liensAppNoeuds, 
			ParametresApp param) {

		GroupeApp tousCandidats, pccMin1, pccMin2; 
		ArcApp arcRef, arcComp;
		NoeudApp noeudComp;
		List tousArcs = new ArrayList();
		List noeudsDebutIn, noeudsDebutOut, noeudsFinIn,noeudsFinOut;
		double longMaxRecherche ;
		int nbSansHomologuePbNoeud = 0, nbSansHomologuePbPCC = 0,
		nbOkUneSerie = 0, nbOkPlusieursSeries = 0,
		nbDouteuxPbNoeud = 0, nbDouteuxPbSens = 0, nbTot;
		double longSansHomologuePbNoeud = 0, longSansHomologuePbPCC = 0,
		longOkUneSerie = 0, longOkPlusieursSeries = 0,
		longDouteuxPbNoeud = 0, longDouteuxPbSens = 0, longTot;
		LienReseaux lien;
		EnsembleDeLiens liensArcsArcs = new EnsembleDeLiens(LienReseaux.class);
		Iterator itArcs, itTousArcsComp;

		liensArcsArcs.setNom("Appariement des arcs");

		// on �tudie tous les arc ref, un par un, ind�pendamment les uns des autres
		itArcs = reseau1.getPopArcs().getElements().iterator();
		while (itArcs.hasNext()) {
			arcRef = (ArcApp)itArcs.next();
			arcRef.setResultatAppariement("Bug: valeur non remplie"); // pour v�rifier que tous les cas sont bien trait�s

			///////// ETUDE DES EXTREMITES DE L'ARC ///////// 

			// Probl�me de topologie ?			
			if ( (arcRef.getNoeudIni() == null) || (arcRef.getNoeudFin() == null) ) {
				// Cas 1 : l'arc n'a pas de noeud � une de ses extr�mit�s
				arcRef.setResultatAppariement("Non appari�, arc sans extr�mit�");
				nbSansHomologuePbNoeud++;
				longSansHomologuePbNoeud = longSansHomologuePbNoeud+arcRef.longueur();
				continue; 
			}

			// On ne sait pas traiter les boucles (CODE A AFFINER POUR FAIRE CELA) 
			if ( (arcRef.getNoeudIni() == arcRef.getNoeudFin() ) ) {
				arcRef.setResultatAppariement("Non appari�, on ne sait pas traiter les boucles");
				nbSansHomologuePbPCC++;
				longSansHomologuePbPCC = longSansHomologuePbPCC+arcRef.longueur();
				continue;
			}


			// Recherche des noeuds en correspondance avec les extr�mit�s de l'arc,
			// que ce soit en entree ou en sortie pour l'arc (au sens de la circulation)
			List noeudsInOut = arcRef.noeudsEnCorrespondanceAuxExtremites(liensAppNoeuds, liensPreAppAA); 
			noeudsDebutIn = (List)noeudsInOut.get(0);
			noeudsDebutOut = (List)noeudsInOut.get(1);
			noeudsFinIn = (List)noeudsInOut.get(2);
			noeudsFinOut = (List)noeudsInOut.get(3);

			if ( ( (noeudsFinIn.size() == 0) && (noeudsFinOut.size() == 0))
					|| ( (noeudsDebutIn.size() == 0) && (noeudsDebutOut.size() == 0)) ) {
				// Cas 2 : un noeud extr�mit� n'est pas appari� 
				arcRef.setResultatAppariement("Non appari�, noeud extr�mit� non appari�");
				nbSansHomologuePbNoeud++;
				longSansHomologuePbNoeud = longSansHomologuePbNoeud+arcRef.longueur();
				continue; 
			}


			///////// CALCUL DES PLUS COURTS CHEMINS ///////// 

			// creation d'un groupe "tousCandidats" avec tous les arcs candidats 
			// issus du pr�-appariement
			tousArcs = arcRef.arcsCompEnCorrespondance(liensPreAppAA);
			tousCandidats = (GroupeApp)reseau2.getPopGroupes().nouvelElement();
			tousCandidats.setListeArcs(tousArcs);
			itTousArcsComp = tousArcs.iterator();
			while (itTousArcsComp.hasNext()) {
				arcComp = (ArcApp) itTousArcsComp.next();
				arcComp.addGroupe(tousCandidats);
			}
			tousCandidats.ajouteNoeuds();
			// Pour �viter les d�bordements, on ne cherche que les pcc pas trop grands
			//longMaxRecherche = arcRef.getGeometrie().length()*5*param.coefficentPoidsLongueurDistance;
			longMaxRecherche = arcRef.getGeometrie().length()*param.distanceArcsMax;

			//calcul du poids des arcs
			calculePoids(arcRef, tousCandidats, param);

			// Recherche du PCC dans un sens, et dans l'autre si l'arc est en double sens
			pccMin1 = tousCandidats.plusCourtChemin(noeudsDebutOut, noeudsFinIn, longMaxRecherche);
			pccMin2 = tousCandidats.plusCourtChemin(noeudsFinOut, noeudsDebutIn, longMaxRecherche);
			tousCandidats.videEtDetache();

			///////// ANALYSE DES PLUS COURTS CHEMINS ///////// 

			// cas 3 : on n'a trouv� aucun plus court chemin, dans aucun sens
			if ( (pccMin1 == null) && (pccMin2 == null) ) {
				arcRef.setResultatAppariement("Non appari�, pas de plus court chemin");
				nbSansHomologuePbPCC++;
				longSansHomologuePbPCC = longSansHomologuePbPCC+arcRef.longueur();
				continue;
			}

			// cas 4 : on a trouv� un pcc dans un seul sens: celui direct
			if (pccMin1 == null) {
				lien = (LienReseaux)liensArcsArcs.nouvelElement();
				lien.addArcs1(arcRef);
				arcRef.addLiens(lien);
				if ( pccMin2.getListeArcs().size() == 0 ) { 
					// cas 4a : on a trouv� un pcc mais il est r�duit � un point
					noeudComp = (NoeudApp)pccMin2.getListeNoeuds().get(0);
					lien.addNoeuds2(noeudComp);
					lien.setEvaluation(0.5);
					noeudComp.addLiens(lien);
					arcRef.setResultatAppariement("Incertitude : Appari� � un noeud comp");
					noeudComp.setResultatAppariement("Incertitude : Appari� � un arc");
					pccMin2.videEtDetache();
					nbDouteuxPbNoeud++;
					longDouteuxPbNoeud= longDouteuxPbNoeud+arcRef.longueur();
					continue;					
				}
				pccMin2.enleveExtremites();
				lien.addGroupes2(pccMin2);
				pccMin2.addLiens(lien);
				if ( arcRef.getOrientation() == -1 ) {
					// cas 4b : on a trouv� un pcc dans un seul sens, et c'est normal
					lien.setEvaluation(1);
					arcRef.setResultatAppariement("Appari� � un arc ou une suite d'arcs");
					pccMin2.setResultatAppariementGlobal("Appari� � un arc ref");
					nbOkUneSerie++;
					longOkUneSerie= longOkUneSerie+arcRef.longueur();
				}
				else {
					// cas 4c : on a trouv� un pcc dans un seul sens, mais ce n'est pas normal
					lien.setEvaluation(0.5);
					arcRef.setResultatAppariement("Incertitude : Appari� que dans un sens");
					pccMin2.setResultatAppariementGlobal("Incertitude : Appari� que dans un sens");
					nbDouteuxPbSens++;
					longDouteuxPbSens= longDouteuxPbSens+arcRef.longueur();
				}
				continue;
			}

			// cas 4 : on a trouv� un pcc dans un seul sens: celui indirect
			if (pccMin2 == null) {
				lien = (LienReseaux)liensArcsArcs.nouvelElement();
				lien.addArcs1(arcRef);
				arcRef.addLiens(lien);
				if ( pccMin1.getListeArcs().size() == 0 ) { 
					// cas 4a : on a trouv� un pcc mais il est r�duit � un point
					noeudComp = (NoeudApp)pccMin1.getListeNoeuds().get(0);
					lien.addNoeuds2(noeudComp);
					lien.setEvaluation(0.5);
					noeudComp.addLiens(lien);
					arcRef.setResultatAppariement("Incertitude : Appari� � un noeud comp");
					noeudComp.setResultatAppariement("Incertitude : Appari� � un arc");
					pccMin1.videEtDetache();
					nbDouteuxPbNoeud++;
					longDouteuxPbNoeud= longDouteuxPbNoeud+arcRef.longueur();
					continue;					
				}
				pccMin1.enleveExtremites();
				lien.addGroupes2(pccMin1);
				pccMin1.addLiens(lien);
				// pour noter le r�sultat sur les objets concern�s
				if ( arcRef.getOrientation() == 1 ) {
					// cas 4a : on a trouv� un pcc dans un seul sens, et c'est normal
					lien.setEvaluation(1);
					arcRef.setResultatAppariement("Appari� � un arc ou une suite d'arcs");
					pccMin1.setResultatAppariementGlobal("Appari� � un arc ref");
					nbOkUneSerie++;
					longOkUneSerie= longOkUneSerie+arcRef.longueur();
				}
				else {
					// cas 4b : on a trouv� un pcc dans un seul sens, mais ce n'est pas normal
					lien.setEvaluation(0.5);
					arcRef.setResultatAppariement("Incertitude : Appari� que dans un sens");
					pccMin1.setResultatAppariementGlobal("Incertitude : Appari� que dans un sens");
					nbDouteuxPbSens++;
					longDouteuxPbSens= longDouteuxPbSens+arcRef.longueur();
				}
				continue;
			}

			// cas 5 : on a trouv� un pcc dans les 2 sens, et c'est le m�me 
			if ( pccMin1.contientMemesArcs(pccMin2) ) {
				pccMin2.videEtDetache();
				lien = (LienReseaux)liensArcsArcs.nouvelElement();
				lien.addArcs1(arcRef);
				arcRef.addLiens(lien);
				if ( pccMin1.getListeArcs().size() == 0 ) { 
					// cas 5a : on a trouv� un pcc mais il est r�duit � un point
					noeudComp = (NoeudApp)pccMin1.getListeNoeuds().get(0);
					lien.addNoeuds2(noeudComp);
					lien.setEvaluation(0.5);
					noeudComp.addLiens(lien);
					arcRef.setResultatAppariement("Incertitude : Appari� � un noeud comp");
					noeudComp.setResultatAppariement("Incertitude : Appari� � un arc");
					pccMin1.videEtDetache();
					nbDouteuxPbNoeud++;
					longDouteuxPbNoeud= longDouteuxPbNoeud+arcRef.longueur();
					continue;					
				}
				pccMin1.enleveExtremites();
				// cas 5b : on a trouv� un pcc dans les 2 sens, non r�duit � un point 
				lien.addGroupes2(pccMin1);
				lien.setEvaluation(1);
				pccMin1.addLiens(lien);
				arcRef.setResultatAppariement("Appari� � un arc");
				pccMin1.setResultatAppariementGlobal("Appari� � un arc");
				nbOkUneSerie++;
				longOkUneSerie= longOkUneSerie+arcRef.longueur();
				continue;
			}

			// cas 6 : on a trouv� un pcc dans les 2 sens, mais ce n'est pas le m�me
			//			cas d'arcs en parral�le 
			lien = (LienReseaux)liensArcsArcs.nouvelElement();
			lien.addArcs1(arcRef);
			arcRef.addLiens(lien);
			if ( pccMin1.getListeArcs().size() == 0 ) { 
				// cas 6a : on a trouv� un pcc mais il est r�duit � un point
				noeudComp = (NoeudApp)pccMin1.getListeNoeuds().get(0);
				lien.addNoeuds2(noeudComp);
				lien.setEvaluation(0.5);
				noeudComp.addLiens(lien);
				arcRef.setResultatAppariement("Incertitude : Appari� � un noeud comp");
				noeudComp.setResultatAppariement("Incertitude : Appari� � un arc");
				pccMin1.videEtDetache();
			}
			else {
				// cas 6b : on a trouv� un pcc non r�duit � un point
				pccMin1.enleveExtremites();
				lien.setEvaluation(1);
				lien.addGroupes2(pccMin1);
				pccMin1.addLiens(lien);
				arcRef.setResultatAppariement("Appari� � plusieurs arcs en parall�le");
				pccMin1.setResultatAppariementGlobal("Appari� � un arc");
			}
			if ( pccMin2.getListeArcs().size() == 0 ) { 
				// cas 6a : on a trouv� un pcc mais il est r�duit � un point
				noeudComp = (NoeudApp)pccMin2.getListeNoeuds().get(0);
				lien.addNoeuds2(noeudComp);
				lien.setEvaluation(0.5);
				noeudComp.addLiens(lien);
				arcRef.setResultatAppariement("Incertitude : Appari� � un noeud comp");
				noeudComp.setResultatAppariement("Incertitude : Appari� � un arc");
				pccMin2.videEtDetache();
				nbDouteuxPbNoeud++;
				longDouteuxPbNoeud= longDouteuxPbNoeud+arcRef.longueur();
			}
			else {
				// cas 6b : on a trouv� un pcc non r�duit � un point
				pccMin2.enleveExtremites();
				lien.addGroupes2(pccMin2);
				pccMin2.addLiens(lien);
				arcRef.setResultatAppariement("Appari� � plusieurs arcs en parall�le");
				pccMin2.setResultatAppariementGlobal("Appari� � un arc");
				if (lien.getEvaluation() == 1 )	{
					nbOkPlusieursSeries++;
					longOkPlusieursSeries= longOkPlusieursSeries+arcRef.longueur();
				} 
				else {
					nbDouteuxPbNoeud++;
					longDouteuxPbNoeud= longDouteuxPbNoeud+arcRef.longueur();
				} 
			}
		}

		// Fin, affichage du bilan
		longTot = 	longSansHomologuePbNoeud + longSansHomologuePbPCC + longOkUneSerie + 
				longOkPlusieursSeries + longDouteuxPbNoeud + longDouteuxPbSens ;
		nbTot = reseau1.getPopArcs().getElements().size();
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("  Bilan des arcs:");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    (Longueur totale du r�seau 1 : "+Math.round(longTot/1000)+" km, si l'unit� des donn�es est le m�tre)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    Appariement jug�s corrects : ");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbOkUneSerie+" arcs 1 appari�s avec un ou plusieurs arc comp en s�rie ("+nbOkUneSerie*100/nbTot+"%nb, "+Math.round(longOkUneSerie*100/longTot)+"%long)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbOkPlusieursSeries+" arcs 1 appari�s avec 2 ensembles de arc comp en parral�le ("+nbOkPlusieursSeries*100/nbTot+"%nb, "+Math.round(longOkPlusieursSeries*100/longTot)+"%long)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    Appariement jug�s incertains : ");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbDouteuxPbSens+" arcs 1 appari�s dans un seul sens ("+nbDouteuxPbSens*100/nbTot+"%nb, "+Math.round(longDouteuxPbSens*100/longTot)+"%long)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbDouteuxPbNoeud+" arcs 1 appari�s avec un noeud ("+nbDouteuxPbNoeud*100/nbTot+"%nb, "+Math.round(longDouteuxPbNoeud*100/longTot)+"%long)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    Arcs non appari�s : ");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbSansHomologuePbNoeud+" arcs 1 sans homolgues (un des noeuds n'est pas appari�) ("+nbSansHomologuePbNoeud*100/nbTot+"%nb, "+Math.round(longSansHomologuePbNoeud*100/longTot)+"%long)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("      "+nbSansHomologuePbPCC+" arcs 1 sans homolgues (pas de plus court chemin trouv�) ("+nbSansHomologuePbPCC*100/nbTot+"%nb, "+Math.round(longSansHomologuePbPCC*100/longTot)+"%long)");

		return liensArcsArcs;
	}

	/** Affectation des poids au arcs pour le calcul de 'plus proche chemin' */
	private static void	calculePoids(ArcApp arcRef, GroupeApp tousCandidats, ParametresApp param) {
		double poids;		
		Iterator itCandidats = tousCandidats.getListeArcs().iterator();
		while (itCandidats.hasNext()) {
			ArcApp arcComp = (ArcApp) itCandidats.next();
			//	poids = arcComp.longueur() + param.coefficentPoidsLongueurDistance*Operateurs.premiere_composante_hausdorff(arcComp.getGeometrie(),arcRef.getGeometrie()); ancienne version
			poids = Distances.ecartSurface(arcComp.getGeometrie(),arcRef.getGeometrie());
			arcComp.setPoids(poids);
		}
	}

	/** Controle de l'enemble des appariements (et non plus un � un) :
	 * recherche des arcs ou noeuds du r�seau 2 appari�s avec plusieurs objets du r�seau 1 
	 * */
	private static void controleGlobal(CarteTopo reseau1, CarteTopo reseau2, EnsembleDeLiens liens, ParametresApp param) {
		List liensObjet;
		Iterator itGroupes, itArcs, itNoeuds, itLiens;
		GroupeApp groupe;
		int nb, nbSansCorresp, nbDouteux, nbOK ;
		double longTotal, longSansCorresp, longDouteux, longOK;
		ArcApp arcComp; 

		////////////////////////////////////////////////////////////
		//////////// Controle global des arcs comp //////////////
		// on recherche les arcs comp appari�s avec plusieurs objets ref
		// pour les marquer comme douteux

		nbSansCorresp = 0; nbDouteux = 0; nbOK = 0;
		longSansCorresp = 0; longDouteux = 0; longOK = 0;
		itArcs = reseau2.getPopArcs().getElements().iterator();
		while (itArcs.hasNext()) {
			arcComp = (ArcApp) itArcs.next();
			// On r�cup�re tous les liens concern�s par l'arc 
			liensObjet = new ArrayList();
			liensObjet.addAll(arcComp.getLiens(liens.getElements()));
			itGroupes = arcComp.getListeGroupes().iterator();
			while (itGroupes.hasNext()) {
				groupe = (GroupeApp) itGroupes.next();
				liensObjet.addAll(groupe.getLiens(liens.getElements()));
			}
			// cas o� l'arc n'est concern� par aucun lien 
			if ( liensObjet.size() == 0) {
				arcComp.setResultatAppariement("Non appari�");
				nbSansCorresp++;
				longSansCorresp=longSansCorresp+arcComp.longueur();
				continue;
			} 
			// cas o� l'arc est concern� par un seul lien
			if ( liensObjet.size() == 1) {
				if ( ((LienReseaux)liensObjet.get(0)).getEvaluation()==1) {
					nbOK++;
					longOK=longOK+arcComp.longueur();
				} 
				else { 
					nbDouteux++; 
					longDouteux=longDouteux+arcComp.longueur();
				}
				continue;
			} 
			//cas o� l'arc est concern� par plusieurs liens
			if ( param.varianteFiltrageImpassesParasites ) {
				// on regarde si l'arc est une petite impasse dans le r�seau de comparaison appari�.
				// Si oui, on l'enl�ve des appariements, c'est s�rement un parasite;
				if (arcComp.longueur() < param.distanceNoeudsMax) { // est-il petit?
					// est-il une impasse au d�but?  
					Noeud iniComp = arcComp.getNoeudIni();
					Iterator itArcsIncidents = iniComp.arcs().iterator();
					boolean impasse = true;
					while (itArcsIncidents.hasNext()) {
						ArcApp arc = (ArcApp) itArcsIncidents.next();
						if (arc == arcComp) continue;
						if ( arc.aUnCorrespondantGeneralise(liens)) {
							impasse = false;
							break;
						}
					}
					if (impasse) { //on nettoie
						arcComp.setResultatAppariement("Non appari�: impasse filtr�e");
						itGroupes = arcComp.getListeGroupes().iterator();
						while (itGroupes.hasNext()) {
							groupe = (GroupeApp) itGroupes.next();
							groupe.getListeArcs().remove(arcComp);
							groupe.getListeNoeuds().remove(arcComp.getNoeudIni());
							if (groupe.getListeArcs().size() == 0 ) liens.removeAll(groupe.getLiens(liens.getElements()));
						}
						arcComp.getListeGroupes().clear();
						continue;
					}

					// est-il une impasse � la fin?  
					Noeud finComp = arcComp.getNoeudFin();
					itArcsIncidents = finComp.arcs().iterator();
					impasse = true;
					while (itArcsIncidents.hasNext()) {
						ArcApp arc = (ArcApp) itArcsIncidents.next();
						if (arc == arcComp) continue;
						if ( arc.aUnCorrespondantGeneralise(liens)) {
							impasse = false;
							break;
						}
					}
					if (impasse) { //on nettoie
						arcComp.setResultatAppariement("Non appari�: impasse filtr�e");
						itGroupes = arcComp.getListeGroupes().iterator();
						while (itGroupes.hasNext()) {
							groupe = (GroupeApp) itGroupes.next();
							groupe.getListeArcs().remove(arcComp);
							groupe.getListeNoeuds().remove(arcComp.getNoeudFin());
							if (groupe.getListeArcs().size() == 0 ) liens.removeAll(groupe.getLiens(liens.getElements()));
						}
						arcComp.getListeGroupes().clear();
						continue;
					}
				}
			}

			// il faut le marquer comme incertain
			nbDouteux++; 
			longDouteux=longDouteux+arcComp.longueur();
			itLiens = liensObjet.iterator();
			while (itLiens.hasNext()) {
				LienReseaux lien = (LienReseaux) itLiens.next();
				lien.affecteEvaluationAuxObjetsLies(0.5, "Incertitude: 1 arc comp li� � plusieurs objets ref diff�rents");
				arcComp.setResultatAppariement("Incertitude: appari� � plusieurs objets de r�f�rence");
			}
			continue;
		}

		nb = nbDouteux+nbOK+nbSansCorresp;
		longTotal=longDouteux+longOK+longSansCorresp;
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("  Arcs du r�seau 2 ("+nb+"):");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    arcs appari�s et jug�s OK : "+nbOK+" ("+(nbOK*100/nb)+"%, "+Math.round(longOK*100/longTotal)+"%long)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    arcs appari�s et jug�s douteux : "+nbDouteux+" ("+(nbDouteux*100/nb)+"%, "+Math.round(longDouteux*100/longTotal)+"%long)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    arcs non appari�s  : "+nbSansCorresp+" ("+(nbSansCorresp*100/nb)+"%, "+Math.round(longSansCorresp*100/longTotal)+"%long)");


		////////////////////////////////////////////////////////////
		//////////// Controle global des noeuds comp //////////////
		// on recherche les noeuds comp appari�s avec plusieurs objets ref
		// pour les marquer comme douteux

		nbSansCorresp = 0;
		nbDouteux = 0;
		nbOK = 0;
		itNoeuds = reseau2.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			NoeudApp noeud = (NoeudApp) itNoeuds.next();
			//on r�cup�re tous les liens concern�s par le noeud
			liensObjet = new ArrayList();
			liensObjet.addAll(noeud.getLiens());
			itGroupes = noeud.getListeGroupes().iterator();
			while (itGroupes.hasNext()) {
				groupe = (GroupeApp) itGroupes.next();
				liensObjet.addAll(groupe.getLiens());
			}
			liensObjet.retainAll(liens.getElements());

			// cas o� le noeud n'est concern� par aucun lien 
			if ( liensObjet.size() == 0) {
				noeud.setResultatAppariement("Non appari�");
				nbSansCorresp = nbSansCorresp+1;
				continue;
			} 

			// cas o� le noeud est concern� par un seul lien
			if ( liensObjet.size() == 1) {
				if ( ((LienReseaux)liensObjet.get(0)).getEvaluation()==1) nbOK++;
				else nbDouteux++;
				continue;
			} 

			//cas o� le noeud est concern� par plusieurs liens
			// il faut le marquer comme incertain si il est concern� par au moins un un noeud ref 
			// (et non par des arcRefs, car cela peut �tre normal dans le cas des doubles voies par exemple)
			itLiens = liensObjet.iterator();
			boolean OK = true;
			while (itLiens.hasNext()) {
				LienReseaux lien = (LienReseaux) itLiens.next();
				if (lien.getNoeuds1().size() != 0 ) {
					OK = false;
					break;
				}
			}
			if (OK) nbOK++;
			else {
				nbDouteux++; 
				itLiens = liensObjet.iterator();
				while (itLiens.hasNext()) {
					LienReseaux lien = (LienReseaux) itLiens.next();
					lien.affecteEvaluationAuxObjetsLies(0.5, "Incertitude: 1 noeud comp li� � plusieurs objets ref diff�rents");
					noeud.setResultatAppariement("Incertitude: appari� � plusieurs objets de r�f�rence");
				}
			}
		}  
		nb = nbDouteux+nbOK+nbSansCorresp;
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("  Noeuds du r�seau 2 ("+nb+"):");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    noeuds appari�s et jug�s OK : "+nbOK+" ("+(nbOK*100/nb)+"%)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    noeuds appari�s et jug�s douteux : "+nbDouteux+" ("+(nbDouteux*100/nb)+"%)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    noeuds non appari�s : "+nbSansCorresp+" ("+(nbSansCorresp*100/nb)+"%)");


		////////////////////////////////////////////////////////
		//////////// Controle global des arcs ref //////////////
		// On ne fait que compter pour �valuer le r�sultat
		nbSansCorresp = 0; nbDouteux = 0; nbOK = 0;
		longSansCorresp = 0; longDouteux = 0; longOK = 0;

		itArcs = reseau1.getPopArcs().getElements().iterator();
		while (itArcs.hasNext()) {
			ArcApp arc = (ArcApp) itArcs.next();
			if (arc.getResultatAppariement().startsWith("Non appari�")) {
				nbSansCorresp++;
				longSansCorresp = longSansCorresp+arc.longueur();
				continue;	
			}
			if (arc.getResultatAppariement().startsWith("Incertitude")) {
				nbDouteux++;
				longDouteux= longDouteux+arc.longueur();
				continue;				
			}
			if (arc.getResultatAppariement().startsWith("Appari�")) {
				nbOK++;
				longOK= longOK+arc.longueur();
				continue;				
			}
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("Valeur impr�vue de r�sultat d'arc : "+arc.getResultatAppariement());
		}

		nb = nbDouteux+nbOK+nbSansCorresp;
		longTotal=longDouteux+longOK+longSansCorresp;
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("  Arcs du r�seau 1 ("+nb+"):");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    arcs appari�s et jug�s OK : "+nbOK+" ("+(nbOK*100/nb)+"%, "+Math.round(longOK*100/longTotal)+"%long)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    arcs appari�s et jug�s douteux : "+nbDouteux+" ("+(nbDouteux*100/nb)+"%, "+Math.round(longDouteux*100/longTotal)+"%long)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    arcs non appari�s : "+nbSansCorresp+" ("+(nbSansCorresp*100/nb)+"%, "+Math.round(longSansCorresp*100/longTotal)+"%long)");

		/////////////////////////////////////////////
		//////////// cas des noeudss ref ////////////
		// On ne fait que compter pour �valuer le r�sultat
		nbSansCorresp = 0; 	nbDouteux = 0; 	nbOK = 0;
		itNoeuds = reseau1.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			NoeudApp noeud = (NoeudApp) itNoeuds.next();
			if (noeud.getResultatAppariement().startsWith("Non appari�")) {
				nbSansCorresp++;
				continue;	
			}
			if (noeud.getResultatAppariement().startsWith("Incertitude")) {
				nbDouteux++;
				continue;				
			}
			if (noeud.getResultatAppariement().startsWith("Appari�")) {
				nbOK++;
				continue;				
			}
			if ( param.debugAffichageCommentaires > 1 )  System.out.println("Valeur impr�vue de r�sultat de noeud : "+noeud.getResultatAppariement());
		}
		nb = nbDouteux+nbOK+nbSansCorresp;
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("  Noeuds du r�seau 1 ("+nb+"):");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    noeuds appari�s et jug�s OK : "+nbOK+" ("+(nbOK*100/nb)+"%)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    noeuds appari�s et jug�s douteux : "+nbDouteux+" ("+(nbDouteux*100/nb)+"%)");
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("    noeuds non appari�s : "+nbSansCorresp+" ("+(nbSansCorresp*100/nb)+"%)");
	}

	/** Les noeuds de r�f�rence non appari�s par les 'liens' sont projet�s sur le r�seau comp 
	 * de mani�re � introduire un noeud dans le res�au Comp aux endroits qui pourraient correspondre 
	 * � ces noeuds Ref non appari�s.
	 */
	public static void decoupeNoeudsNonApparies(ReseauApp ref, ReseauApp comp, EnsembleDeLiens liens,  ParametresApp param) {
		List noeudsNonApparies = new ArrayList();
		Iterator itNoeuds = ref.getPopNoeuds().getElements().iterator();
		while (itNoeuds.hasNext()) {
			NoeudApp noeud = (NoeudApp) itNoeuds.next();
			if ( noeud.getLiens(liens.getElements()).size() == 0) noeudsNonApparies.add(noeud.getGeometrie());
		}
		if ( param.debugAffichageCommentaires > 1 )  System.out.println("Nb de noeuds non appari�s : "+noeudsNonApparies.size());
		comp.projete(noeudsNonApparies, param.varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc, param.varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud);
	}

	/** D�coupe les arcs de r�f�rence non appari�s par les 'liens' de mani�re 
	 *  � introduire un noeud dans le res�au Ref aux endroits o� il s'�loigne du r�seau Comp.
	 *  
	 *  Remarque: utilis� pour les GR par exemple pour traiter le cas des GR hors sentier.
	 */
	public static void decoupeNonApparies(ReseauApp ref, ReseauApp comp, EnsembleDeLiens liens,  ParametresApp param) {

		double distanceMaxNoeudArc = param.projeteNoeud2surReseau1_DistanceNoeudArc; //param.distanceArcsMax;
		double distanceMaxProjectionNoeud = param.projeteNoeud2surReseau1_DistanceProjectionNoeud; 
		ArcApp arcComp, arcDecoupe;
		Iterator itArcsDecoupes,itArcsDecoupants;
		FT_FeatureCollection arcsCompProches ;
		List pointsDeDecoupage;
		DirectPosition pt, ptDecoupage;
		int indiceDernierPtProche ;
		int i;
		double longProche;
		boolean proche;

		itArcsDecoupes = ref.getPopArcs().getElements().iterator();
		pointsDeDecoupage = new ArrayList();
		while (itArcsDecoupes.hasNext()) {
			arcDecoupe = (ArcApp) itArcsDecoupes.next();
			if (arcDecoupe.getLiens(liens.getElements()).size() != 0) continue;

			arcsCompProches = comp.getPopArcs().select(arcDecoupe.getGeometrie(), distanceMaxNoeudArc); 
			if (arcsCompProches.size() == 0) continue;

			itArcsDecoupants = arcsCompProches.getElements().iterator();
			while (itArcsDecoupants.hasNext()) {
				arcComp = (ArcApp)itArcsDecoupants.next();

				// recherche en partant du d�but				
				indiceDernierPtProche= 0;
				longProche = 0;
				proche = false;
				for(i=0;i<arcComp.getGeometrie().getControlPoint().size();i++) {
					pt = arcComp.getGeometrie().getControlPoint(i);
					if ( Distances.distance(pt, arcDecoupe.getGeometrie() ) < distanceMaxNoeudArc ) {
						if (proche) longProche = longProche+Distances.distance(arcComp.getGeometrie().getControlPoint(i), arcComp.getGeometrie().getControlPoint(i-1));
						indiceDernierPtProche = i;
						proche = true;  
					} 
					else {
						if (proche) break;
					}
				}
				if ( indiceDernierPtProche!= 0 &&
						indiceDernierPtProche!=arcComp.getGeometrie().getControlPoint().size()-1 &&
						longProche > distanceMaxProjectionNoeud ) {
					ptDecoupage = Operateurs.projection(arcComp.getGeometrie().getControlPoint(indiceDernierPtProche), arcDecoupe.getGeometrie());					 
					pointsDeDecoupage.add(new GM_Point(ptDecoupage));				
				}

				// recherche en partant de la fin				
				indiceDernierPtProche= arcComp.getCoord().size()-1;
				longProche = 0;
				proche = false;
				for(i=arcComp.getGeometrie().getControlPoint().size()-1;i>=0;i--) {
					pt = arcComp.getGeometrie().getControlPoint(i);
					if ( Distances.distance(pt, arcDecoupe.getGeometrie() ) < distanceMaxNoeudArc ) {
						if (proche) longProche = longProche+Distances.distance(arcComp.getGeometrie().getControlPoint(i), arcComp.getGeometrie().getControlPoint(i+1));
						indiceDernierPtProche = i;
						proche = true;  
					} 
					else {
						if (proche) break;
					}
				}
				if ( indiceDernierPtProche!= 0 &&
						indiceDernierPtProche!=arcComp.getGeometrie().getControlPoint().size()-1 &&
						longProche > distanceMaxProjectionNoeud ) {
					ptDecoupage = Operateurs.projection(arcComp.getGeometrie().getControlPoint(indiceDernierPtProche), arcDecoupe.getGeometrie());					 
					pointsDeDecoupage.add(new GM_Point(ptDecoupage));				
				}
			}
		}
		ref.projete(pointsDeDecoupage, distanceMaxNoeudArc, distanceMaxNoeudArc);
		param.distanceNoeudsMax = 51;
		ref.instancieAttributsNuls(param);
		comp.projete(pointsDeDecoupage, distanceMaxNoeudArc, distanceMaxNoeudArc);
	}
}