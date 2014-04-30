package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Noeud du r�seau routier.
 * <BR> <STRONG> Type </STRONG>:
 *      Objet simple
 * <BR> <STRONG> Localisation </STRONG>:
 *      Ponctuelle.
 * <BR> <STRONG> Liens </STRONG>:
 *      //Compose (lien inverse) "Route numerot�e ou nomm�e"
 * <BR> <STRONG> D�finition </STRONG>:
 *      Un noeud du r�seau routier correspond � une extr�mit� de tron�on de route ou de liaison maritime ;
 *      il traduit une modification des conditions de circulation : ce peut �tre une intersection, un obstacle ou un changement de valeur d'attribut.
 *      Il n'y a pas � proprement parler de s�lection des noeuds routiers :
 *      elle est d�duite de celle des tron�ons de route et des liaisons maritimes et bacs. Les carrefours am�nag�s d'une extension sup�rieure � 100
 *      m�tres et les ronds-points d'un diam�tre sup�rieur � 50 m�tres sont des noeuds avec une nature sp�cifique ;
 *      si leur extension est inf�rieure ils sont consid�r�s comme des carrefours simples.
 *      D'autre part, si leur extension est sup�rieure � 100 m�tres, ils sont �galement d�taill�s en plusieurs carrefours simples au m�me titre que les
 *      �changeurs (ils ont alors 2 descriptions : une g�n�ralis�e et une d�taill�e).
 * <BR> <STRONG> Compatibilit� entre attributs </STRONG> :
 * <UL>
 * <LI> compatiblite sur les toponymes ? </LI>
 * </UL>
 * 
 * @author braun
 */

public abstract class NoeudRoutier extends ElementBDCarto {

	//     protected GM_Point geometrie = null;
	/** Renvoie la g�om�trie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** D�finit la g�om�trie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	/** Type de noeud.
	 * <BR> <STRONG> D�finition </STRONG>:
	 *      Type de noeud.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Cha�ne de caract�res.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     11- carrefour simple, cul de sac, carrefour am�nag� d'une extension inf�rieure � 100 m�tres, ou rond-point d'un diam�tre inf�rieur � 50 m�tres </LI>
	 * <LI>     12- intersection repr�sentant un carrefour am�nag� d'une extension sup�rieure � 100 m�tres sans toboggan ni passage inf�rieur </LI>
	 * <LI>     14- intersection repr�sentant un rond-point (giratoire) d'un diam�tre sup�rieur � 100 m�tres d'axe � axe </LI>
	 * <LI>     15- carrefour am�nag� avec passage inf�rieur ou toboggan quelle que soit son extension </LI>
	 * <LI>     16- intersection repr�sentant un �changeur complet </LI>
	 * <LI>     17- intersection repr�sentant un �changeur partiel </LI>
	 * <LI>     18- rond-point (giratoire) d'un diam�tre compris entre 50 et 100 m�tres </LI>
	 * <LI>     22- embarcad�re de bac ou liaison maritime </LI>
	 * <LI>     23- embarcad�re de liaison maritime situ� hors du territoire BDCarto, positionn� de fa�on fictive en limite de ce territoire </LI>
	 * <LI>     31- barri�re interdisant la communication libre entre deux portions de route, r�guli�rement ou irr�guli�rement entretenue </LI>
	 * <LI>     32- barri�re de douane (hors CEE) </LI>
	 * <LI>     40- changement d'attribut </LI>
	 * <LI>     45- noeud cr�� par l'intersection entre une route nationale et la limite de d�partement quand il n'existe pas de noeud au lieu de l'intersection ou noeud cr�� pour d�couper des grands tron�ons de route (ex : autoroute).</LI>
	 * <LI>     50- noeud de communication restreinte (voir B-rs-4) : noeud cr�� quand il n?existe pas de noeud correspondant aux valeurs ci-dessus au lieu de la restriction.</LI>
	 * </UL>
	 */
	public String type;
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }


	/** Toponyme.
	 * <BR> <STRONG> D�finition </STRONG>:
	 * Un noeud du r�seau routier peut porter un toponyme, si l'un au moins des tron�ons connect�s appartient au r�seau class�, et si le noeud appartient � l'un des types suivants :
	 * carrefour simple,
	 * carrefour am�nag� d'une extension sup�rieure � 100 m,
	 * rond-point d'un diam�tre sup�rieur � 100 m,
	 * rond-point,
	 * carrefour am�nag� avec passage inf�rieur ou tobbogan,
	 * �changeur complet ou partiel.
	 * Un noeud composant un carrefour complexe ne porte g�n�ralement pas de toponyme.
	 * Le toponyme est compos� de trois parties pouvant ne porter aucune valeur (n'existe pas dans les cas ci-dessus et sans objet sinon) :
	 * un terme g�n�rique ou une d�signation, texte d'au plus 40 caract�res.
	 * un article, texte d'au plus cinq caract�res ;
	 * un �l�ment sp�cifique, texte d'au plus 80 caract�res ;
	 * <BR> <STRONG> Type </STRONG>:
	 *      Cha�ne de caract�res.
	 */

	public String toponyme;
	public String getToponyme() { return toponyme; }
	public void setToponyme(String toponyme) { this.toponyme = toponyme; }


	/** Cote.
	 * <BR> <STRONG> D�finition </STRONG>:
	 * Nombre entier donnant l'altitude en m�tres. Cet attribut peut ne porter aucune valeur.
	 * <BR> <STRONG> Type </STRONG>:
	 *      entier > 0.
	 *      NB : 9999 correspond � une cote inconnue.
	 */
	public int cote;
	public int getCote() { return cote; }
	public void setCote(int I) { cote = I;  }


	/** Liste (non ordonn�e) des arcs sortants de self
	 * <BR> <STRONG> D�finition </STRONG>:
	 *  1 objet Noeud est en relation "sortants" avec n objets Tron�onRoutier.
	 *  1 objet Tron�onRoutier est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les m�thodes get (sans indice) et set sont n�cessaires au mapping.
	 *  Les autres m�thodes sont l� seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant � la gestion de la logique de parcours du r�seau routier :
	 *      Elle pr�cise lengthnoeud routier initial d'un tron�on de route.
	 * <BR> <STRONG> Type des �l�ments de la liste </STRONG>:
	 *      TronconRoute.
	 * <BR> <STRONG> Cardinalit� de la relation </STRONG>:
	 *      1 tron�on a 0 ou 1 noeud initial NoeudIni.
	 *      1 noeud � 0 ou n tron�ons sortants.
	 */
	protected List<TronconRoute> sortants = new ArrayList<TronconRoute>();

	/** R�cup�re la liste des arcs sortants. */
	public List<TronconRoute> getSortants() {return sortants ; }
	/** D�finit la liste des arcs sortants, et met � jour la relation inverse Ini. */
	public void setSortants(List<TronconRoute> L) {
		List<TronconRoute> old = new ArrayList<TronconRoute>(sortants);
		Iterator<TronconRoute> it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconRoute O = it1.next();
			O.setNoeudIni(null);
		}
		Iterator<TronconRoute> it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconRoute O = it2.next();
			O.setNoeudIni(this);
		}
	}
	/** R�cup�re le i�me �l�ment de la liste des arcs sortants. */
	public TronconRoute getSortant(int i) {return sortants.get(i) ; }
	/** Ajoute un objet � la liste des arcs sortants, et met � jour la relation inverse Ini. */
	public void addSortant(TronconRoute O) {
		if ( O == null ) return;
		sortants.add(O) ;
		O.setNoeudIni(this) ;
	}
	/** Enl�ve un �l�ment de la liste des arcs sortants, et met � jour la relation inverse Ini. */
	public void removeSortant(TronconRoute O) {
		if ( O == null ) return;
		sortants.remove(O) ;
		O.setNoeudIni(null);
	}
	/** Vide la liste des arcs sortants, et met � jour la relation inverse Ini. */
	public void emptySortants() {
		List <TronconRoute>old = new ArrayList<TronconRoute>(sortants);
		Iterator<TronconRoute> it = old.iterator();
		while ( it.hasNext() ) {
			TronconRoute O = it.next();
			O.setNoeudIni(null);
		}
	}


	/** Liste (non ordonn�e) des arcs entrants de self
	 * <BR> <STRONG> D�finition </STRONG>:
	 *  1 objet Noeud est en relation "entrants" avec n objets Tron�onRoutier.
	 *  1 objet Tron�onRoutier est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les m�thodes get (sans indice) et set sont n�cessaires au mapping.
	 *  Les autres m�thodes sont l� seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant � la gestion de la logique de parcours du r�seau routier :
	 *      Elle pr�cise lengthnoeud routier initial d'un tron�on de route.
	 * <BR> <STRONG> Type des �l�ments de la liste </STRONG>:
	 *      TronconRoute.
	 * <BR> <STRONG> Cardinalit� de la relation </STRONG>:
	 *      1 tron�on a 0 ou 1 noeud initial NoeudFin.
	 *      1 noeud � 0 ou n tron�ons entrants.
	 */
	protected List<TronconRoute> entrants = new ArrayList<TronconRoute>();

	/** R�cup�re la liste des arcs entrants. */
	public List<TronconRoute> getEntrants() {return entrants ; }
	/** D�finit la liste des arcs entrants, et met � jour la relation inverse NoeudFin. */
	public void setEntrants(List <TronconRoute>L) {
		List<TronconRoute> old = new ArrayList<TronconRoute>(entrants);
		Iterator<TronconRoute> it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconRoute O = it1.next();
			O.setNoeudFin(null);
		}
		Iterator<TronconRoute> it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconRoute O = it2.next();
			O.setNoeudFin(this);
		}
	}
	/** R�cup�re le i�me �l�ment de la liste des arcs entrants. */
	public TronconRoute getEntrant(int i) {return entrants.get(i) ; }
	/** Ajoute un objet � la liste des arcs entrants, et met � jour la relation inverse NoeudFin. */
	public void addEntrant(TronconRoute O) {
		if ( O == null ) return;
		entrants.add(O) ;
		O.setNoeudFin(this) ;
	}
	/** Enl�ve un �l�ment de la liste des arcs entrants, et met � jour la relation inverse NoeudFin. */
	public void removeEntrant(TronconRoute O) {
		if ( O == null ) return;
		entrants.remove(O) ;
		O.setNoeudFin(null);
	}
	/** Vide la liste des arcs entrants, et met � jour la relation inverse NoeudFin. */
	public void emptyEntrants() {
		List<TronconRoute> old = new ArrayList<TronconRoute>(entrants);
		Iterator<TronconRoute> it = old.iterator();
		while ( it.hasNext() ) {
			TronconRoute O = it.next();
			O.setNoeudFin(null);
		}
	}


	/** Lien bidirectionnel persistant la carrefourComplexe dont il est composant.
	 *  1 objet CarrefourComplexe est en relation avec 1 ou n objets noeuds.
	 *  1 objet Noeud est en relation avec 0 ou 1 objet carrefourComplexe.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas �tre utilis�es
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected CarrefourComplexe carrefourComplexe;
	/** R�cup�re le carrefourComplexe dont il est composant. */
	public CarrefourComplexe getCarrefourComplexe() {return carrefourComplexe;  }
	/** D�finit le carrefourComplexe dont il est composant, et met � jour la relation inverse. */
	public void setCarrefourComplexe(CarrefourComplexe O) {
		CarrefourComplexe old = carrefourComplexe;
		carrefourComplexe = O;
		if ( old  != null ) old.getNoeuds().remove(this);
		if ( O != null ) {
			carrefourComplexeID = O.getId();
			if ( !(O.getNoeuds().contains(this)) ) O.getNoeuds().add(this);
		} else carrefourComplexeID = 0;
	}
	protected int carrefourComplexeID;
	/** Ne pas utiliser, necessaire au mapping OJB */
	public void setCarrefourComplexeID(int I) {carrefourComplexeID = I;}
	/** Ne pas utiliser, necessaire au mapping OJB */
	public int getCarrefourComplexeID() {return carrefourComplexeID;}



	/** Une communication restreinte concerne un noeud.
	 *  1 objet CommunicationRestreinte est en relation avec 1 Noeud.
	 *  1 objet Noeud est en relation avec 0 ou 1 objet CommunicationRoutiere.
	 *
	 *  Les methodes ...ID sont utiles uniquement au mapping et ne doivent pas �tre utilis�es
	 *
	 *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null.
	 *  Pour casser une relation: faire setObjet(null);
	 */
	protected CommunicationRestreinte communication;
	/** R�cup�re l'objet en relation. */
	public CommunicationRestreinte getCommunication() {return communication;  }
	/** D�finit l'objet en relation, et met � jour la relation inverse. */
	public void setCommunication(CommunicationRestreinte O) {
		CommunicationRestreinte old = communication;
		communication = O;
		if ( old != null ) old.setNoeud(null);
		if ( O != null ) {
			communicationID = O.getId();
			if ( O.getNoeud() != this ) O.setNoeud(this);
		} else communicationID = 0;
	}
	protected int communicationID;
	/** Ne pas utiliser, necessaire au mapping OJB */
	public int getCommunicationID() {return communicationID;}
	/** Ne pas utiliser, necessaire au mapping OJB */
	public void setCommunicationID (int I) {communicationID = I;}



	/** Liste (non ordonn�e) des liaisons maritimes sortants de self
	 * <BR> <STRONG> D�finition </STRONG>:
	 *  1 objet Noeud est en relation "sortantsMaritime" avec 0 ou n objets liaisons maritimes.
	 *  1 objet liaison est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les m�thodes get (sans indice) et set sont n�cessaires au mapping.
	 *  Les autres m�thodes sont l� seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant � la gestion de la logique de parcours du r�seau routier :
	 *      Elle pr�cise lengthnoeud routier initial d'un tron�on de route.
	 * <BR> <STRONG> Type des �l�ments de la liste </STRONG>:
	 *      TronconRoute.
	 * <BR> <STRONG> Cardinalit� de la relation </STRONG>:
	 *      1 tron�on a 0 ou 1 noeud initial NoeudIni.
	 *      1 noeud � 0 ou n tron�ons sortants.
	 */
	protected List<LiaisonMaritime> sortantsMaritime = new ArrayList<LiaisonMaritime>();

	/** R�cup�re la liste des arcs Maritime sortants. */
	public List<LiaisonMaritime> getSortantsMaritime() {return sortantsMaritime ; }
	/** D�finit la liste des arcs Maritime sortants, et met � jour la relation inverse Ini. */
	public void setSortantsMaritime(List<LiaisonMaritime> L) {
		List <LiaisonMaritime>old = new ArrayList<LiaisonMaritime>(sortantsMaritime);
		Iterator<LiaisonMaritime> it1 = old.iterator();
		while ( it1.hasNext() ) {
			LiaisonMaritime O = it1.next();
			O.setNoeudIni(null);
		}
		Iterator<LiaisonMaritime> it2 = L.iterator();
		while ( it2.hasNext() ) {
			LiaisonMaritime O = it2.next();
			O.setNoeudIni(this);
		}
	}
	/** R�cup�re le i�me �l�ment de la liste des arcs Maritime sortants. */
	public LiaisonMaritime getSortantMaritime(int i) {return sortantsMaritime.get(i) ; }
	/** Ajoute un objet � la liste des arcs sortants, et met � jour la relation inverse Ini. */
	public void addSortantMaritime(LiaisonMaritime O) {
		if ( O == null ) return;
		sortantsMaritime.add(O) ;
		O.setNoeudIni(this) ;
	}
	/** Enl�ve un �l�ment de la liste des arcs Maritime sortants, et met � jour la relation inverse Ini. */
	public void removeSortantMaritime(LiaisonMaritime O) {
		if ( O == null ) return;
		sortantsMaritime.remove(O) ;
		O.setNoeudIni(null);
	}
	/** Vide la liste des arcs Maritime sortants, et met � jour la relation inverse Ini. */
	public void emptySortantsMaritime() {
		List<LiaisonMaritime> old = new ArrayList<LiaisonMaritime>(sortantsMaritime);
		Iterator<LiaisonMaritime> it = old.iterator();
		while ( it.hasNext() ) {
			LiaisonMaritime O = it.next();
			O.setNoeudIni(null);
		}
	}


	/** Liste (non ordonn�e) des liaisons maritimes entrants de self
	 * <BR> <STRONG> D�finition </STRONG>:
	 *  1 objet Noeud est en relation "entrantsMaritime" avec 0 ou n objets liaisons maritimes.
	 *  1 objet liaison est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les m�thodes get (sans indice) et set sont n�cessaires au mapping.
	 *  Les autres m�thodes sont l� seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 *      Relation topologique participant � la gestion de la logique de parcours du r�seau routier :
	 *      Elle pr�cise lengthnoeud routier initial d'un tron�on de route.
	 * <BR> <STRONG> Type des �l�ments de la liste </STRONG>:
	 *      TronconRoute.
	 * <BR> <STRONG> Cardinalit� de la relation </STRONG>:
	 *      1 tron�on a 0 ou 1 noeud initial NoeudFin.
	 *      1 noeud � 0 ou n tron�ons entrants.
	 */
	protected List<LiaisonMaritime> entrantsMaritime = new ArrayList<LiaisonMaritime>();

	/** R�cup�re la liste des arcs Maritime entrants. */
	public List<LiaisonMaritime> getEntrantsMaritime() {return entrantsMaritime ; }
	/** D�finit la liste des arcs Maritime entrants, et met � jour la relation inverse NoeudFin. */
	public void setEntrantsMaritime(List<LiaisonMaritime> L) {
		List<LiaisonMaritime> old = new ArrayList<LiaisonMaritime>(entrantsMaritime);
		Iterator<LiaisonMaritime> it1 = old.iterator();
		while ( it1.hasNext() ) {
			LiaisonMaritime O = it1.next();
			O.setNoeudFin(null);
		}
		Iterator<LiaisonMaritime> it2 = L.iterator();
		while ( it2.hasNext() ) {
			LiaisonMaritime O = it2.next();
			O.setNoeudFin(this);
		}
	}
	/** R�cup�re le i�me �l�ment de la liste des arcs Maritime entrants. */
	public LiaisonMaritime getEntrantMaritime(int i) {return entrantsMaritime.get(i) ; }
	/** Ajoute un objet � la liste des arcs entrants, et met � jour la relation inverse NoeudFin. */
	public void addEntrantMaritime(LiaisonMaritime O) {
		if ( O == null ) return;
		entrantsMaritime.add(O) ;
		O.setNoeudFin(this) ;
	}
	/** Enl�ve un �l�ment de la liste des arcs Maritime entrants, et met � jour la relation inverse NoeudFin. */
	public void removeEntrantMaritime(LiaisonMaritime O) {
		if ( O == null ) return;
		entrantsMaritime.remove(O) ;
		O.setNoeudFin(null);
	}
	/** Vide la liste des arcs Maritime entrants, et met � jour la relation inverse NoeudFin. */
	public void emptyEntrantsMaritime() {
		List<LiaisonMaritime> old = new ArrayList<LiaisonMaritime>(entrantsMaritime);
		Iterator<LiaisonMaritime> it = old.iterator();
		while ( it.hasNext() ) {
			LiaisonMaritime O = it.next();
			O.setNoeudFin(null);
		}
	}



}
