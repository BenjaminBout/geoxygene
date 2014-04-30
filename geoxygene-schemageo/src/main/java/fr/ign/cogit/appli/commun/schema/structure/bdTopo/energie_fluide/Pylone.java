package fr.ign.cogit.appli.commun.schema.structure.bdTopo.energie_fluide;


import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ElementBDTopo;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class Pylone extends ElementBDTopo {

	/** Renvoie la g�om�trie de l'objet, cast�e plus pr�cis�ment qu'avec la m�thode getGeom() */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** D�finit la g�om�trie de l'objet, cast�e plus pr�cis�ment qu'avec la m�thode setGeom() */
	public void setGeometrie(GM_Point G) {this.geom = G;}


}
