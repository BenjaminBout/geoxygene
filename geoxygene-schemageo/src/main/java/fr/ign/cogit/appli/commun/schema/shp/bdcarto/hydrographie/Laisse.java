package fr.ign.cogit.appli.commun.schema.shp.bdcarto.hydrographie;

import fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class Laisse extends ElementBDCarto {

	/** Renvoie la g�om�trie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** D�finit la g�om�trie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	private String nature;
	public String getNature() {return this.nature; }
	public void setNature (String S) {nature = S; }

}
