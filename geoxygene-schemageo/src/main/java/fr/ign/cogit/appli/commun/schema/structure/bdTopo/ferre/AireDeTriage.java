package fr.ign.cogit.appli.commun.schema.structure.bdTopo.ferre;


import fr.ign.cogit.appli.commun.schema.structure.bdTopo.ElementBDTopo;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public abstract class AireDeTriage extends ElementBDTopo {

	/** Renvoie la g�om�trie de l'objet, cast�e plus pr�cis�ment qu'avec la m�thode getGeom() */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** D�finit la g�om�trie de l'objet, cast�e plus pr�cis�ment qu'avec la m�thode setGeom() */
	public void setGeometrie(GM_Polygon G) {this.geom = G;}

	protected double z_moyen;
	public double getZ_moyen() {return this.z_moyen; }
	public void setZ_moyen (double Z_moyen) {z_moyen = Z_moyen; }

}
