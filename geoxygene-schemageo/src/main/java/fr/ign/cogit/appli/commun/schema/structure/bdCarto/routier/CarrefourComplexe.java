package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;

public abstract class CarrefourComplexe extends ElementBDCarto {

	public String numero;
	public String getNumero() {return numero;}
	public void setNumero(String S) {this.numero = S;}

	public String nature;
	public String getNature() {return nature;}
	public void setNature(String S) {this.nature = S;}

	public String toponyme;
	public String getToponyme() {return toponyme;}
	public void setToponyme(String S) {this.toponyme = S;}

	/** Lien bidirectionnel persistant 1-n vers noeud routier.
	 *  1 objet Carrefour Complexe est en relation avec 1 ou n objets noeud.
	 *  1 objet noeud est en relation avec 0 ou 1 objet Carrefour complexe.
	 *
	 *  NB: un objet route ne doit pas �tre en relation plusieurs fois avec le m�me objet troncon :
	 *  il est impossible de bien g�rer des relations 1-n bidirectionnelles avec doublons.
	 *
	 *  Les m�thodes get (sans indice) et set sont n�cessaires au mapping.
	 *  Les autres m�thodes sont l� seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	protected List<NoeudRoutier> noeuds = new ArrayList<NoeudRoutier>();

	/** R�cup�re la liste des noeuds. */
	public List<NoeudRoutier> getNoeuds() {return noeuds ; }
	/** D�finit la liste des Noeuds, et met � jour la relation inverse. */
	public void setNoeuds (List <NoeudRoutier>L) {
		List<NoeudRoutier> old = new ArrayList<NoeudRoutier>(noeuds);
		Iterator<NoeudRoutier> it1 = old.iterator();
		while ( it1.hasNext() ) {
			NoeudRoutier O = it1.next();
			O.setCarrefourComplexe(null);
		}
		Iterator<NoeudRoutier> it2 = L.iterator();
		while ( it2.hasNext() ) {
			NoeudRoutier O = it2.next();
			O.setCarrefourComplexe(this);
		}
	}
	/** R�cup�re le i�me �l�ment composant. */
	public NoeudRoutier getNoeud(int i) {return noeuds.get(i) ; }
	/** Ajoute un objet � la liste des objets en relation, et met � jour la relation inverse. */
	public void addNoeud(NoeudRoutier O) {
		if ( O == null ) return;
		noeuds.add(O) ;
		O.setCarrefourComplexe(this) ;
	}
	/** Enl�ve un �l�ment de la liste noeuds, et met � jour la relation inverse. */
	public void removeNoeud(NoeudRoutier O) {
		if ( O == null ) return;
		noeuds.remove(O) ;
		O.setCarrefourComplexe(null);
	}
	/** Vide la liste des noeuds, et met � jour la relation inverse. */
	public void emptyNoeuds() {
		List<NoeudRoutier> old = new ArrayList<NoeudRoutier>(noeuds);
		Iterator<NoeudRoutier> it = old.iterator();
		while ( it.hasNext() ) {
			NoeudRoutier O = it.next();
			O.setCarrefourComplexe(null);
		}
	}

}
