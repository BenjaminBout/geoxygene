package fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Liaisom maritime ou bac.
 * <BR> <STRONG> Type </STRONG>:
 *      Objet simple
 * <BR> <STRONG> Localisation </STRONG>:
 *      Lin�aire.
 * <BR> <STRONG> Liens </STRONG>:
 *      //Compose (lien inverse) "Route numerot�e ou nomm�e"
 * <BR> <STRONG> D�finition </STRONG>:
 *       Liaison maritime ou ligne de bac reliant deux embarcad�res.
 *       Sont retenus dans la BDCarto : <UL>
 *       <LI> tous les bacs et liaisons maritimes reliant deux embarcad�res situ�s sur le territoire de la BDCarto et ouverts au public (noeuds routiers de type 22), �
 *       l'exception des bacs fluviaux r�serv�s aux pi�tons ; </LI>
 *       <LI> toutes les liaisons maritimes r�guli�res effectuant le transport des passagers ou des v�hicules entre un embarcad�re situ� sur le territoire de la BDCarto
 *       (type 22) et un embarcad�re situ� hors du territoire BDCarto (type 23). </LI> </UL>
 *       Lorsqu'un embarcad�re est �galement noeud routier apportant une information particuli�re (rond-point?) c'est ce dernier qui est cod�. Les liaisons
 *       maritimes sont toujours connect�es au r�seau routier pour assurer la continuit� du r�seau. Quand il n'existe pas de route pour assurer cette connection,
 *       celle-ci est assur�e par un tron�on fictif (voir B-s-1-[31-2]).
 * <BR> <STRONG> Compatibilit� entre attributs </STRONG> :
 * <UL>
 * <LI> compatiblite sur les toponymes ? </LI>
 * </UL>
 * 
 * @author braun
 */

public abstract class LiaisonMaritime extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des g�o�mtries multiples (plusieurs tron�ons) */
	//     protected GM_Curve geometrie = null;
	/** Renvoie la g�om�trie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** D�finit la g�om�trie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	/** Ouverture.
	 * <BR> <STRONG> D�finition </STRONG>:
	 *      Ouverture.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Cha�ne de caract�res.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- toute l'ann�e   </LI>
	 * <LI>     6- en saison seulement </LI>
	 * </UL>
	 */
	protected String ouverture;
	public String getOuverture() {return ouverture; }
	public void setOuverture(String S) {ouverture = S; }

	/** Vocation.
	 * <BR> <STRONG> D�finition </STRONG>:
	 *      Vocation.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Cha�ne de caract�res.
	 * <BR> <STRONG> Valeurs </STRONG>: <UL>
	 * <LI>     1- pi�tons seulement    </LI>
	 * <LI>     2- pi�tons et automobiles </LI>
	 * </UL>
	 */
	protected String vocation;
	public String getVocation() {return vocation; }
	public void setVocation(String S) {vocation= S; }

	/** Dur�e.
	 * <BR> <STRONG> D�finition </STRONG>:
	 *      Dur�e de la travers�e en minutes, pouvant �ventuellement ne porter aucune valeur (inconnue).
	 *      Note : Quand il y a plusieurs temps de parcours pour une m�me liaison, c'est le temps le plus long qui est retenu.
	 * <BR> <STRONG> Type </STRONG>:
	 *      Entier > 0
	 */
	protected double duree;
	public double getDuree() {return duree; }
	public void setDuree(double D) {duree= D; }

	/** Toponyme.
	 * <BR> <STRONG> D�finition </STRONG>:
	 *      Texte d'au plus 80 caract�res, pouvant �ventuellement ne porter aucune valeur (inconnu),
	 *      sp�cifiant la localisation des embarcad�res de d�part et d'arriv�e (ex : " brest : le conquet ").
	 * <BR> <STRONG> Type </STRONG>:
	 *      Cha�ne de caract�res.
	 */
	protected String toponyme;
	public String getToponyme() {return toponyme; }
	public void setToponyme(String S) {toponyme = S; }




	/** Noeud initial de la liaison maritime.
	 * <BR> <STRONG> D�finition </STRONG>:
	 *      Relation topologique participant � la gestion de la logique de parcours du r�seau routier :
	 *      Elle pr�cise le noeud routier initial de la liaison maritime.
	 * <BR> <STRONG> Type </STRONG>:
	 *      NoeudRoutier.
	 * <BR> <STRONG> Cardinalit� de la relation </STRONG>:
	 *      1 liaison a 0 ou 1 noeud initial.
	 *      1 noeud a 0 ou n liasons sortants.
	 */

	protected NoeudRoutier noeudIni;
	/** R�cup�re le noeud initial. */
	public NoeudRoutier getNoeudIni() {return noeudIni;}
	/** D�finit le noeud initial, et met � jour la relation inverse. */
	public void setNoeudIni(NoeudRoutier O) {
		NoeudRoutier old = noeudIni;
		noeudIni = O;
		if ( old  != null ) old.getSortantsMaritime().remove(this);
		if ( O != null ) {
			noeudIniID = O.getId();
			if ( !(O.getSortantsMaritime().contains(this)) ) O.getSortantsMaritime().add(this);
		} else noeudIniID = 0;
	}
	/** Pour le mapping avec OJB, dans le cas d'une relation 1-n, du cote 1 de la relation */
	protected int noeudIniID;
	/** Ne pas utiliser, n�cessaire au mapping*/
	public void setNoeudIniID(int I) {noeudIniID = I;}
	/** Ne pas utiliser, n�cessaire au mapping*/
	public int getNoeudIniID() {return noeudIniID;}




	/** Noeud final de la liaison maritime.
	 * <BR> <STRONG> D�finition </STRONG>:
	 *      Relation topologique participant � la gestion de la logique de parcours du r�seau routier :
	 *      Elle pr�cise le noeud routier initial de la liaison maritime.
	 * <BR> <STRONG> Type </STRONG>:
	 *      NoeudRoutier.
	 * <BR> <STRONG> Cardinalit� de la relation </STRONG>:
	 *      1 liaison a 0 ou 1 noeud initial.
	 *      1 noeud a 0 ou n liaisons sortants.
	 */
	protected NoeudRoutier noeudFin;
	/** R�cup�re le noeud final. */
	public NoeudRoutier getNoeudFin() {return noeudFin;}
	/** D�finit le noeud final, et met � jour la relation inverse. */
	public void setNoeudFin(NoeudRoutier O) {
		NoeudRoutier old = noeudFin;
		noeudFin = O;
		if ( old  != null ) old.getEntrantsMaritime().remove(this);
		if ( O != null ) {
			noeudFinID = O.getId();
			if ( !(O.getEntrantsMaritime().contains(this)) ) O.getEntrantsMaritime().add(this);
		} else noeudFinID = 0;
	}
	/** Pour le mapping avec OJB, dans le cas d'une relation 1-n, du cote 1 de la relation */
	protected int noeudFinID;
	/** Ne pas utiliser, n�cessaire au mapping*/
	public void setNoeudFinID(int I) {noeudFinID = I;}
	/** Ne pas utiliser, n�cessaire au mapping*/
	public int getNoeudFinID() {return noeudFinID;}


}
