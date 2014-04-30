package fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

@SuppressWarnings("unchecked")

public abstract class NoeudHydrographique extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	//    private GM_Point geometrie = null;
	/** Renvoie le GM_Point qui d�finit la g�om�trie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** D�finit le GM_Point qui d�finit la g�om�trie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	/////////////// ATTRIBUTS //////////////////
	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	protected double cote;
	public double getCote() {return this.cote; }
	public void setCote (double Cote) {cote = Cote; }

	// NB : impossible � remplir � partir des tables shape
	protected int classification;
	public int getClassification() {return this.classification; }
	public void setClassification(int S) {classification = S; }

	// NB : impossible � remplir � partir des tables shape
	protected int caractereTouristique;
	public int getCaractereTouristique() {return this.caractereTouristique; }
	public void setCaractereTouristique(int S) {caractereTouristique = S; }




	/////////////// RELATIONS //////////////////
	/** Liste (non ordonn�e) des arcs entrants de self
	 *  1 objet Noeud est en relation "entrants" avec n objets Tron�onHydrographique (n>0).
	 *  1 objet Tron�onHydrographique est en relation "fin" avec 1 objet Noeud.
	 *
	 *  Les m�thodes get (sans indice) et set sont n�cessaires au mapping.
	 *  Les autres m�thodes sont l� seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List entrants = new ArrayList();

	/** R�cup�re la liste des arcs entrants. */
	public List getEntrants() {return entrants ; }
	/** D�finit la liste des arcs entrants, et met � jour la relation inverse Fin. */
	public void setEntrants (List L) {
		List old = new ArrayList(entrants);
		Iterator it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it1.next();
			O.setFin(null);
		}
		Iterator it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it2.next();
			O.setFin(this);
		}
	}
	/** R�cup�re le i�me �l�ment de la liste des arcs entrants. */
	public TronconHydrographique getEntrant(int i) {return (TronconHydrographique)entrants.get(i) ; }
	/** Ajoute un objet � la liste des arcs entrants, et met � jour la relation inverse Fin. */
	public void addEntrant(TronconHydrographique O) {
		if ( O == null ) return;
		entrants.add(O) ;
		O.setFin(this) ;
	}
	/** Enl�ve un �l�ment de la liste des arcs entrants, et met � jour la relation inverse Fin. */
	public void removeEntrant(TronconHydrographique O) {
		if ( O == null ) return;
		entrants.remove(O) ;
		O.setFin(null);
	}
	/** Vide la liste des arcs entrants, et met � jour la relation inverse Fin. */
	public void emptyEntrants() {
		List old = new ArrayList(entrants);
		Iterator it = old.iterator();
		while ( it.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it.next();
			O.setFin(null);
		}
	}





	/** Liste (non ordonn�e) des arcs sortants de self
	 *  1 objet Noeud est en relation "sortants" avec n objets Tron�onHydrographique (n>0).
	 *  1 objet Tron�onHydrographique est en relation "ini" avec 1 objet Noeud.
	 *
	 *  Les m�thodes get (sans indice) et set sont n�cessaires au mapping.
	 *  Les autres m�thodes sont l� seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	private List sortants = new ArrayList();

	/** R�cup�re la liste des arcs sortants. */
	public List getSortants() {return sortants ; }
	/** D�finit la liste des arcs sortants, et met � jour la relation inverse Ini. */
	public void setSortants(List L) {
		List old = new ArrayList(sortants);
		Iterator it1 = old.iterator();
		while ( it1.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it1.next();
			O.setIni(null);
		}
		Iterator it2 = L.iterator();
		while ( it2.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it2.next();
			O.setIni(this);
		}
	}
	/** R�cup�re le i�me �l�ment de la liste des arcs sortants. */
	public TronconHydrographique getSortant(int i) {return (TronconHydrographique)sortants.get(i) ; }
	/** Ajoute un objet � la liste des arcs sortants, et met � jour la relation inverse Ini. */
	public void addSortant(TronconHydrographique O) {
		if ( O == null ) return;
		sortants.add(O) ;
		O.setIni(this) ;
	}
	/** Enl�ve un �l�ment de la liste des arcs sortants, et met � jour la relation inverse Ini. */
	public void removeSortant(TronconHydrographique O) {
		if ( O == null ) return;
		sortants.remove(O) ;
		O.setIni(null);
	}
	/** Vide la liste des arcs sortants, et met � jour la relation inverse Ini. */
	public void emptySortants() {
		List old = new ArrayList(sortants);
		Iterator it = old.iterator();
		while ( it.hasNext() ) {
			TronconHydrographique O = (TronconHydrographique)it.next();
			O.setIni(null);
		}
	}
}
