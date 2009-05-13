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

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * Param�tres de l'appariement.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 */
public class ParametresApp implements Cloneable {

	/////////////////////////////////////////////////////////////////////////////////
	/////////////   PARAMETRES SPECIFIANT QUELLE DONNEES SONT TRAITEES   ////////////
	/////////////////////////////////////////////////////////////////////////////////

	/** Liste des classes d'arcs de la BD 1 (la moins d�taill�e) concern�s par l'appariement */
	public List<FT_FeatureCollection<Arc>> populationsArcs1 = new ArrayList<FT_FeatureCollection<Arc>>();

	/** Liste des classes de noeuds de la BD 1 (la moins d�taill�e) concern�s par l'appariement */
	public List<FT_FeatureCollection<Noeud>> populationsNoeuds1 = new ArrayList<FT_FeatureCollection<Noeud>>();

	/** Liste des classes d'arcs de la BD 2 (la plus d�taill�e) concern�s par l'appariement */
	public List<FT_FeatureCollection<Arc>> populationsArcs2 = new ArrayList<FT_FeatureCollection<Arc>>();

	/** Liste des classes de noeuds de la BD 2 (la plus d�taill�e) concern�s par l'appariement */
	public List<FT_FeatureCollection<Noeud>> populationsNoeuds2 = new ArrayList<FT_FeatureCollection<Noeud>>();

	/** Prise en compte de l'orientation des arcs sur le terrain (sens de circulation).
	 * Si true : on suppose tous les arcs en double sens.
	 * Si false: on suppose tous les arcs en sens unique, celui d�fini par la g�om�trie.
	 * NB: ne pas confondre cette orientation 'g�ographique r�elle', avec l'orientation de la g�o�mtrie.
	 * 
	 * Utile ensuite pour l'appariement des arcs.
	 */
	public boolean populationsArcsAvecOrientationDouble = true;


	/////////////////////////////////////////////////////////////////////////////////
	/////////////////             TAILLES DE RECHERCHE        ///////////////////////
	/////////////////        Ecarts de distance autoris�s     ///////////////////////
	/////////////////      CE SONT LES PARAMETRES PRINCIPAUX  ///////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	/** Distance maximale autoris�e entre deux noeuds appari�s.
	 */
	public float distanceNoeudsMax = 150;

	/** Distance maximale autoris�e entre deux noeuds appari�s, quand le noeud du r�seau 1
	 *  est une impasse uniquement.
	 *  Ce param�tre permet de prendre en compte le fait que la localisation exacte des extr�mit�s d'impasse
	 *  est assez impr�cise dans certains r�seaux (o� commence exactement une rivi�re par exemple?).
	 * 
	 *  Si cette distance est strictement n�gative, alors ce param�tre n'est pas pris en compte,
	 *  et la distance maximale est la m�me pour tous les noeuds, y-compris aux extr�mit�s.
	 */
	public float distanceNoeudsImpassesMax = -1;

	/** Distance maximum autoris�e entre les arcs des deux r�seaux.
	 * La distance est d�finie au sens de la "demi-distance de Hausdorf" des arcs
	 * du r�seau 2 vers les arcs du r�seau 1.
	 */
	public float distanceArcsMax = 100;

	/** Distance minimum sous laquelle l'�cart de distance pour divers arcs du r�seaux 2
	 * (distance vers les arcs du r�seau 1) n'a plus aucun sens.
	 *  Cette distance est typiquement de l'ordre de la pr�cision g�om�trique du r��seau le moins pr�cis.
	 */
	public float distanceArcsMin = 30;


	/////////////////////////////////////////////////////////////////////////////////
	/////////////         TRAITEMENTS TOPOLOGIQUES A L'IMPORT       /////////////////
	/////////////////////////////////////////////////////////////////////////////////

	/** Les noeuds proches du r�seau 1 sont fusionn�s en un seul noeud
	 *  (proches = �loign�s de moins que ce param�tre).
	 *  Si ce param�tre est >0, les noeuds sont fusion�s � une position moyenne, et les arcs sont d�form�s pour suivre.
	 *  Si ce param�tre est =0, seul les noeuds strictement superpos�s sont fusionn�s.
	 *  Si ce param�tre est <0 (d�faut), aucune fusion n'est faite.
	 */
	public double topologieSeuilFusionNoeuds1 = -1;

	/** Les noeuds proches du r�seau 2 sont fusionn�s en un seul noeud
	 *  (proches = �loign�s de moins que ce param�tre).
	 *  Si ce param�tre est >0, les noeuds sont fusion�s � une position moyenne, et les arcs sont d�form�s pour suivre.
	 *  Si ce param�tre est =0, seul les noeuds strictement superpos�s sont fusionn�s.
	 *  Si ce param�tre est <0 (d�faut), aucune fusion n'est faite.
	 */
	public double topologieSeuilFusionNoeuds2 = -1;

	/** Les noeuds du r�seau 1 contenus dans une m�me surface de la population en param�tre
	 *  seront fusionn�s en un seul noeud pour l'appariement.
	 *  Ce param�tre peut �tre null (d�faut), auquel il est sans influence.
	 *  Exemple typique: on fusionne toutes les extr�mit�s de lignes ferr�s arrivant dans une m�me aire de triage,
	 *  si les aires de triage sont d�finies par des surfaces dans les donn�es.
	 */
	public Population<?> topologieSurfacesFusionNoeuds1 = null;

	/** Les noeuds du r�seau 2 contenus dans une m�me surface de la population en param�tre
	 *  seront fusionn�s en un seul noeud pour l'appariement.
	 *  Ce param�tre peut �tre null (d�faut), auquel il est sans influence.
	 *  Exemple typique: on fusionne toutes les extr�mit�s de lignes ferr�s arrivant dans une m�me aire de triage,
	 *  si les aires de triage sont d�finies par des surfaces dans les donn�es.
	 */
	public Population<?> topologieSurfacesFusionNoeuds2 = null;

	/** Doit-on eliminer pour l'appariement les noeuds du r�seau 1 qui n'ont que 2 arcs incidents
	 *  et fusionner ces arcs ?
	 */
	public boolean topologieElimineNoeudsAvecDeuxArcs1 = false;

	/** Doit-on eliminer pour l'appariement les noeuds du r�seau 2 qui n'ont que 2 arcs incidents
	 *  et fusionner ces arcs ?
	 */
	public boolean topologieElimineNoeudsAvecDeuxArcs2 = false;

	/** Doit-on rendre le r�seau 1 planaire (cr�er des noeuds aux intersections d'arcs)?
	 */
	public boolean topologieGraphePlanaire1 = false;

	/** Doit-on rendre le r�seau 2 planaire (cr�er des noeuds aux intersections d'arcs)?
	 */
	public boolean topologieGraphePlanaire2 = false;

	/** Fusion des arcs doubles.
	 * Si true: si le r�seau 1 contient des arcs en double (m�me g�om�trie exactement),
	 * alors on les fusionne en un seul arc pour l'appariement.
	 * Si false: rien n'est fait.
	 */
	public boolean topologieFusionArcsDoubles1 = false;

	/** Fusion des arcs doubles.
	 * Si true: si le r�seau 2 contient des arcs en double (m�me g�om�trie exactement),
	 * alors on les fusionne en un seul arc pour l'appariement.
	 * Si false: rien n'est fait.
	 */
	public boolean topologieFusionArcsDoubles2 = false;


	/////////////////////////////////////////////////////////////////////////////////
	//////////   TRAITEMENTS DE SURDECOUPAGE DES RESEAUX A L'IMPORT       ///////////
	/////////////////////////////////////////////////////////////////////////////////

	/** Doit on projeter les noeuds du r�seau 1 sur le r�seau 2 pour d�couper ce dernier ?
	 * Ce traitement r�alise un surd�coupage du r�seau 2 qui facilite l'appariement dans certains cas
	 * (par exemple si les r�seaux ont des niveaux de d�tail proches), mais qui va aussi un peu
	 * � l'encontre de la philosophie g�n�rale du processus d'appariement.
	 * A utiliser avec mod�ration donc.
	 */
	public boolean projeteNoeuds1SurReseau2 = false;

	/** Distance max de la projection des noeuds 2 sur le r�seau 1.
	 *  Utile uniquement si projeteNoeuds1SurReseau2 = true.
	 */
	public double projeteNoeuds1SurReseau2_DistanceNoeudArc = 0;

	/** Distance min entre la projection d'un noeud sur un arc et les extr�mit�s de cet arc
	 * pour cr�er un nouveau noeud sur le r�seau 2.
	 *  Utile uniquement si projeteNoeuds1SurReseau2 = true.
	 */
	public double projeteNoeuds1SurReseau2_DistanceProjectionNoeud = 0;

	/** Si true: on ne projete que les impasses du r�seau 1 sur le r�seau 2
	 *  Si false: on projete tous les noeuds du r�seau 1 sur le r�seau 2.
	 *  Utile uniquement si projeteNoeuds1SurReseau2 = true.
	 */
	public boolean projeteNoeuds1SurReseau2_ImpassesSeulement = false;


	/** Doit on projeter les noeuds du r�seau 2 sur le r�seau 1 pour d�couper ce dernier ?
	 * Ce traitement r�alise un surd�coupage du r�seau 1 qui facilite l'appariement dans certains cas
	 * (par exemple si les r�seaux ont des niveaux de d�tail proches), mais qui va aussi un peu
	 * � l'encontre de la philosophie g�n�rale du processus d'appariement.
	 * A utiliser avec mod�ration donc.
	 */
	public boolean projeteNoeud2surReseau1 = false;

	/** Distance max de la projection des noeuds 1 sur le r�seau 2.
	 *  Utile uniquement si projeteNoeuds2SurReseau1 = true.
	 */
	public double projeteNoeud2surReseau1_DistanceNoeudArc = 0;

	/** Distance min entre la projection d'un noeud sur un arc et les extr�mit�s de cet arc
	 * pour cr�er un nouveau noeud sur le r�seau 1.
	 *  Utile uniquement si projeteNoeuds2SurReseau1 = true.
	 */
	public double projeteNoeud2surReseau1_DistanceProjectionNoeud = 0;

	/** Si true: on ne projete que les impasses du r�seau 2 sur le r�seau 1
	 *  Si false: on projete tous les noeuds du r�seau 2 sur le r�seau 1.
	 *  Utile uniquement si projeteNoeuds1SurReseau2 = true.
	 */
	public boolean projeteNoeud2surReseau1_ImpassesSeulement = false;



	/////////////////////////////////////////////////////////////////////////////////
	/////////////            VARIANTES DU PROCESSUS GENERAL    //////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	/** Niveau de complexit�: recherche ou non de liens 1-n aux noeuds.
	 *  Si true: un noeud du r�seau 1 est toujours appari� avec au plus un noeud du r�seau 2 (1-1).
	 *  Si false (d�faut): on recherche liens 1-n aux noeuds .
	 * 
	 *  NB: dans le cas simple, le processus est �norm�ment simplifi� !!!!!!!!
	 *  Ceci peut �tre pertinent si les donn�es ont le m�me niveau de d�tail par exemple.
	 */
	public boolean varianteForceAppariementSimple = false;


	/** Appariement en deux passes qui tente un surd�coupage du r�seau pour les arcs non appari�s en premi�re passe.
	 * Si true: les arcs du r�seau 1 non appari�s dans une premi�re passe sont red�coup�s de mani�re
	 *          � introduire un noeud dans le res�au 1 aux endroits o� il s'�loigne trop du r�seau 2.
	 *          Le "trop" est �gal � projeteNoeud2surReseau1_DistanceProjectionNoeud.
	 * Si false (d�faut): processus en une seule passe.
	 * 
	 * NB: pour l'instant, apr�s ce re-d�coupage l'appariement est enti�rement refait, ce qui est long
	 * et tr�s loin d'�tre optimis�: code � revoir !!!
	 */
	public boolean varianteRedecoupageArcsNonApparies = false;

	/** Appariement en deux passes qui tente un surd�coupage du r�seau pour les noeuds non appari�s en premi�re passe.
	 * Si true: les noeuds du r�seau 1 non appari�s dans une premi�re passe sont projet�s au plus proche
	 * 			sur le r�seau 2 pour le d�couper
	 * Si false (d�faut): processus en une seule passe.
	 * 
	 * Il s'agit en fait de la m�me op�ration que celle qui est faite quand 'projeteNoeuds1SurReseau2'='true',
	 * mais uniquement pour les noeuds non appari�s en premi�re passe, et avec des seuils �ventuellement
	 * diff�rents et d�finis par les param�tres suivants :
	 * - redecoupageNoeudsNonAppariesDistanceNoeudArc.
	 * - redecoupageNoeudsNonAppariesDistanceProjectionNoeud
	 * 
	 * NB: pour l'instant, apr�s ce re-d�coupage l'appariement est enti�rement refait, ce qui est long
	 * et tr�s loin d'�tre optimal: � revoir � l'occasion,
	 */
	public boolean varianteRedecoupageNoeudsNonApparies = false;


	/** Distance max de la projection des noeuds du r�seau 1 sur le r�seau 2.
	 *  Utilis� uniquement si varianteRedecoupageNoeudsNonApparies = true.
	 */
	public double varianteRedecoupageNoeudsNonApparies_DistanceNoeudArc = 100;

	/** Distance min entre la projection d'un noeud sur un arc et les extr�mit�s de cet arc
	 *  pour cr�er un nouveau noeud sur le r�seau 2 ?
	 *  Utilis� uniquement si varianteRedecoupageNoeudsNonApparies = true.
	 */
	public double varianteRedecoupageNoeudsNonApparies_DistanceProjectionNoeud = 50;

	/** Quand un arc est appari� � un ensemble d'arcs, �limine de cet ensemble
	 *  les petites impasses qui cr�ent des aller-retour parasites (de longueur inf�rieure � distanceNoeuds).
	 *  NB: param�tre sp�cifique aux r�seaux simples, qui permet d'am�liorer le recalage.
	 */
	public boolean varianteFiltrageImpassesParasites = false;

	/** Recherche des ronds-points (faces circulaires) dans le r�seau 2, pour �viter
	 *  d'apparier un noeud du r�seau 1 � une partie seulement d'un rond-point
	 * (si une partie seulement est appari�e, tout le rond point devient appari�).
	 *  NB: Param�tre utile uniquement pour les r�seaux routiers a priori.
	 */
	public boolean varianteChercheRondsPoints = false;


	/////////////////////////////////////////////////////////////////////////////////
	/////////////        OPTIONS D'EXPORT                                ////////////
	/////////////////////////////////////////////////////////////////////////////////
	/** Si true, la g�om�trie des liens est calcul�e des objets2 vers les objets 1
	 * si false, c'est l'inverse
	 */
	public boolean exportGeometrieLiens2vers1 = true;

	/////////////////////////////////////////////////////////////////////////////////
	/////////////        OPTIONS DE DEBUG                                ////////////
	/////////////////////////////////////////////////////////////////////////////////
	/** Param�tre pour g�rer l'affichage des commentaires dans la fen�tre de contr�le
	 * Si c'est �gal � 0 : aucun commentaire n'est affich�
	 * Si c'est �gal � 1 : le d�but des grandes �tapes et les principaux r�sultats sont signal�s
	 * Si c'est �gal � 2 (debug) : tous les messages sont affich�s
	 */
	public int debugAffichageCommentaires = 1;

	/** Pour debug uniquement. Sur quels objets fait-on le bilan?
	 *  Si true (normal) : On fait le bilan au niveau des objets g�ographiques initiaux,
	 *  		  et on exporte des liens de la classe appariement.Lien entre objets g�ographiques initiaux;
	 *  Si false (pour �tude/d�bug): On fait le bilan au niveau des arcs des cartes topo ayant servi au calcul
	 *            et on exporte des liens de la classe appariementReseaux.LienReseaux entre objets des cartes topo.
	 */
	public boolean debugBilanSurObjetsGeo = true;

	/** Pour la repr�sentation graphique des liens d'appariement entre cartes topos.
	 *  Si true : On repr�sente les liens entre arcs par des tirets r�guliers,
	 *  Si false : On repr�sente les liens entre arcs par un trait reliant les milieux.
	 */
	public boolean debugTirets = true;

	/** Pour la repr�sentation graphique des liens d'appariement entre cartes topos.
	 * Si on repr�sente les liens entre arcs par des tirets r�guliers, pas entre les tirets.
	 */
	public double debugPasTirets = 50;

	/** Pour la repr�sentation graphique des liens d'appariement entre cartes topos.
	 *  Si true : On repr�sente les liens entre des objets et un noeud, avec un buffer autour de ces objets
	 *  Si false : On repr�sente les liens entre objets 2 � 2 par des trait reliant les milieux.
	 */
	public boolean debugBuffer = false;

	/** Pour la repr�sentation graphique des liens d'appariement entre cartes topos.
	 * pour la repr�setnation des liens d'appariement, taille du buffer autour des objets appari�s � un noeud.
	 */
	public double debugTailleBuffer = 10;

}