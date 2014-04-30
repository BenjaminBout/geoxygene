package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Equipement routier.
 * <BR> <STRONG> Type </STRONG>:
 *      Objet simple
 * <BR> <STRONG> Localisation </STRONG>:
 *      Ponctuelle.
 * <BR> <STRONG> Liens </STRONG>:
 *      //Compose (lien inverse) "Route numerot�e ou nomm�e"
 * <BR> <STRONG> D�finition </STRONG>:
 *      La classe des �quipements routiers regroupe : <UL>
 * <LI>     les aires de repos et les aires de service sur le r�seau de type autoroutier ; </LI>
 * <LI>     les tunnels routiers d'une longueur inf�rieure � 200 m�tres s'ils ne correspondent pas � une intersection avec d'autres tron�ons des r�seaux routier et ferr� (sinon ce sont des franchissements) ; </LI>
 * <LI>     les gares de p�age.  </LI> </UL>
 * <BR> <STRONG> Compatibilit� entre attributs </STRONG> :
 * <UL>
 * <LI> compatiblite sur les toponymes ? </LI>
 * </UL>
 * 
 */

public abstract class EquipementRoutier extends ElementBDCarto {

	//     protected GM_Point geometrie = null;
	/** Renvoie la g�om�trie de self */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** D�finit la g�om�trie de self */
	public void setGeometrie(GM_Point geometrie) {this.geom = geometrie;}

	/** Nature
	 * <BR> <STRONG> D�finition </STRONG>:
	 *      Nature.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Cha�ne de caract�res.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- aire de service  </LI>
	 * <LI>     2- aire de repos  </LI>
	 * <LI>     5- tunnel de moins de 200 m�tres  </LI>
	 * <LI>     7- gare de p�age </LI>
	 * </UL>
	 */
	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	/** Toponyme.
	 * <BR> <STRONG> D�finition </STRONG>:
	 *   Un �quipement porte en g�n�ral un toponyme.
	 *   Il est compos� de trois parties pouvant �ventuellement ne porter aucune valeur (n'existe pas) :
	 *   un terme g�n�rique ou une d�signation, texte d'au plus 40 caract�res.
	 *   un article, texte d'au plus cinq caract�res ;
	 *   un �l�ment sp�cifique, texte d'au plus 80 caract�res ;
	 * <BR> <STRONG> Type </STRONG>:
	 *      Cha�ne de caract�res.
	 */
	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

	/** Un troncon de route permet d'acc�der � n �quipements routier ,
	 *  par l'interm�diaire de la classe-relation Accede.
	 *  1 objet �quipement peut etre en relation avec 0 ou n "objets-relation" Accede.
	 *  1 "objet-relation" Accede est en relation avec 1 objet �quipement.
	 *
	 *  Les m�thodes get (sans indice) et set sont n�cessaires au mapping.
	 *  Les autres m�thodes sont l� seulement pour faciliter l'utilisation de la relation.
	 *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes.
	 *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
	 *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
	 */
	protected List<Accede> accedent = new ArrayList<Accede>();

	/** R�cup�re la liste des Accede en relation. */
	public List<Accede> getAccedent() {return accedent; }
	/** D�finit la liste des Accede en relation, et met � jour la relation inverse. */
	public void setAccedent(List<Accede> L) {
		List<Accede> old = new ArrayList<Accede>(accedent);
		Iterator<Accede> it1 = old.iterator();
		while ( it1.hasNext() ) {
			Accede O = it1.next();
			O.setEquipement(null);
		}
		Iterator<Accede> it2 = L.iterator();
		while ( it2.hasNext() ) {
			Accede O = it2.next();
			O.setEquipement(this);
		}
	}
	/** R�cup�re le i�me �l�ment de la liste des Accede en relation. */
	public Accede getAccede(int i) {return accedent.get(i) ; }
	/** Ajoute un objet � la liste des objets en relation, et met � jour la relation inverse. */
	public void addAccede(Accede O) {
		if ( O == null ) return;
		accedent.add(O) ;
		O.setEquipement(this) ;
	}
	/** Enl�ve un �l�ment de la liste des Accede en relation, et met � jour la relation inverse. */
	public void removeAccede(Accede O) {
		if ( O == null ) return;
		accedent.remove(O) ;
		O.setEquipement(null);
	}
	/** Vide la liste des Accede en relation, et met � jour la relation inverse. */
	public void emptyAccedent() {
		List<Accede> old = new ArrayList<Accede>(accedent);
		Iterator<Accede> it = old.iterator();
		while ( it.hasNext() ) {
			Accede O = it.next();
			O.setEquipement(null);
		}
	}



}
