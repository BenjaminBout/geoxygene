package fr.ign.cogit.appli.commun.schema.structure.georoute.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.georoute.ElementGeoroute;
import fr.ign.cogit.appli.commun.schema.structure.georoute.destination.Acces;
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
 *      Un n�ud du r�seau routier correspond � une extr�mit� de tron�on de route. Il traduit une modification des
 *      conditions de circulation.
 *      Pour les objets dans la partie interurbaine, les crit�res de s�lection sont ceux de la BDCARTO V2 (cf.
 *      "Sp�cifications de contenu BDCARTO v2.3").
 */



public abstract class NoeudRoutier extends ElementGeoroute {

	//     private GM_Point geometrie = null;
	/** Renvoie la g�om�trie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** D�finit la g�om�trie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	/** Nature de l'intersection.
	 * <BR> <STRONG> D�finition </STRONG>:
	 *      Nature de l'intersection.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Cha�ne de caract�res.
	 * <BR> <STRONG> Valeurs </STRONG>:
	 *      - intersection simple : Endroit de l'espace routier o� les routes se rejoignent ou se coupent au
	 *        m�me niveau. On place �galement des intersections simples dans les extr�mit�s d'impasses.
	 *        Toute portion de l'espace routier symbolisant un choix d'au moins trois directions et de diam�tre inf�rieur � 30 m�tres lorsqu'on l'assimile � un
	 *        cercle.
	 *      - rond-point simple : Endroit de l'espace routier o� les routes se rejoignent au m�me niveau, de
	 *         forme non exclusivement circulaire, poss�dant un terre-plein central infranchissable et ceintur� par une chauss�e � sens unique. Les v�hicules ne
	 *           s'y croisent pas.
	 *      - barri�re de p�age :  Lieu o� l'on acquitte un droit de passage sur une voie publique ou un pont.
	 *      - Changement d'attribut :  �l�ment signifiant un changement de valeur d'attribut (et notamment de
	 *           commune) sur un tron�on de route. Il s'agit d'un objet virtuel. On prend en compte les attributs suivants : classement physique, niveau au
	 *           franchissement, restriction d'acc�s, position par rapport au sol, nombres de
	 *           voies, INSEE commune gauche et droite ainsi que nom rue droite et
	 *           gauche. Les autre attributs ne changent de valeur qu'� une intersection.. Un
	 *           n�ud ayant cette nature est appel� NCVA (voir glossaire)
	 *      - noeud d'acc�s : Objet virtuel utilis� pour mettre en relation un �quipement (th�me
	 *           destination) avec le r�seau routier lorsqu'il n'existe pas d'autres N�uds du
	 *           R�seau Routier � l'emplacement de l'acc�s. Les bornes postales au niveau
	 *      - franchissement : - en urbain - Un am�nagement routier est consid�r� comme ponctuel si sa longueur
	 *           est inf�rieure � 50 m�tres. L'objet ponctuel "franchissement" est plac� �
	 *           l'endroit du franchissement. Pour les am�nagements de plus de 50 m�tres,
	 *           on place un objet ponctuel "franchissement" � l'emplacement r�el du
	 *           franchissement (cf. attribut "position par rapport au sol" de la classe
	 *           "tron�on de route"). Dans le cas d'une intersection simple sur un
	 *           franchissement, on place seulement un franchissement sur le carrefour :
	 *           On saisit �galement les franchissements sur les voies ferr�es et l'hydrographie. On place alors simplement un franchissement entre deux
	 *           tron�ons de route, les tron�ons portant le niveau 0 (pas d'information sur le fait qu'on passe sur de l'hydro ou une voie ferr�e) : rivi�re
	 *                  - en interurbain - Un am�nagement routier est consid�r� comme ponctuel si sa longueur est
	 *           inf�rieure � 200 m�tres. On ne saisit pas les franchissements sur les voies ferr�es ou sur l'hydrographie.
	 */
	public String nature;
	public String getNature() {return nature;}
	public void setNature(String nature) {this.nature = nature;}

	/** Toponyme.
	 * <BR> <STRONG> D�finition </STRONG>:
	 * D�nomination usuelle du carrefour ou du franchissement. Il est compos� d'un terme g�n�rique (rond-point de, place de, �) et d'un ou plusieurs
	 * noms propres ou communs. Il est �crit en majuscules et sans accent. Les abr�viations utilis�es pour le terme g�n�rique sont standardis�es
	 * Il peut prendre les valeurs suivantes : <UL>
	 * <LI>     - sans objet : sur NCVA et N�uds d'Acc�s </LI>
	 * <LI>     - inconnu </LI>
	 * <LI>     - "" : l'intersection ne porte pas de nom </LI>
	 * </UL>
	 * <BR> <STRONG> Type </STRONG>:
	 *      Cha�ne de caract�res.
	 */
	public String nom;
	public String getNom() {return nom;}
	public void setNom(String nom) {this.nom = nom;}



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
	 *      Elle pr�cise le noeud routier initial d'un tron�on de route.
	 * <BR> <STRONG> Type des �l�ments de la liste </STRONG>:
	 *      TronconRoute.
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
		List<TronconRoute> old = new ArrayList<TronconRoute>(sortants);
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
	 */
	protected List<TronconRoute> entrants = new ArrayList<TronconRoute>();

	/** R�cup�re la liste des arcs entrants. */
	public List<TronconRoute> getEntrants() {return entrants ; }
	/** D�finit la liste des arcs entrants, et met � jour la relation inverse NoeudFin. */
	public void setEntrants(List<TronconRoute> L) {
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



	/** Liste (non ordonn�e) des non communication concern�es par self
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
	 */
	protected List<NonCommunication> nonCommunications = new ArrayList<NonCommunication>();

	/** R�cup�re la liste des nonCommunications. */
	public List<NonCommunication> getNonCommunications() {return nonCommunications ; }
	/** D�finit la liste des nonCommunication, et met � jour la relation inverse. */
	public void setNonCommunications(List<NonCommunication> L) {
		List<NonCommunication> old = new ArrayList<NonCommunication>(nonCommunications);
		Iterator<NonCommunication> it1 = old.iterator();
		while ( it1.hasNext() ) {
			NonCommunication O = it1.next();
			O.setNoeud(null);
		}
		Iterator<NonCommunication> it2 = L.iterator();
		while ( it2.hasNext() ) {
			NonCommunication O = it2.next();
			O.setNoeud(this);
		}
	}
	/** R�cup�re le i�me �l�ment de la liste des nonCommunications. */
	public NonCommunication getNonCommunication(int i) {return nonCommunications.get(i) ; }
	/** Ajoute un objet � la liste des arcs entrants, et met � jour la relation inverse NoeudFin. */
	public void addNonCommunication(NonCommunication O) {
		if ( O == null ) return;
		nonCommunications.add(O) ;
		O.setNoeud(this) ;
	}
	/** Enl�ve un �l�ment de la liste des nonCommunications, et met � jour la relation inverse NoeudFin. */
	public void removeNonCommunication(NonCommunication O) {
		if ( O == null ) return;
		nonCommunications.remove(O) ;
		O.setNoeud(null);
	}
	/** Vide la liste des nonCommunications, et met � jour la relation inverse NoeudFin. */
	public void emptyNonCommunications() {
		List<NonCommunication> old = new ArrayList<NonCommunication>(nonCommunications);
		Iterator<NonCommunication> it = old.iterator();
		while ( it.hasNext() ) {
			NonCommunication O = it.next();
			O.setNoeud(null);
		}
	}


	protected Acces acces;
	/** R�cup�re l'acces en relation */
	public Acces getAcces() {return acces;}
	/** D�finit l'acces en relation, et met � jour la relation inverse. */
	public void setAcces(Acces O) {
		Acces old = acces;
		acces = O;
		if ( old != null ) old.setNoeud(null);
		if ( O != null ) {
			accesID = O.getId();
			if ( O.getNoeud() != this ) O.setNoeud(this);
		} else accesID = 0;
	}
	protected int accesID;
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public int getAccesID() {return accesID;}
	/** Ne pas utiliser, necessaire au mapping OJB dans le cas d'un lien 1-1 */
	public void setAccesID (int I) {accesID = I;}




}
