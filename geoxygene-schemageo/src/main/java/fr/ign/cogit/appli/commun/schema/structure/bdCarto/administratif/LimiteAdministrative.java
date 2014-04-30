package fr.ign.cogit.appli.commun.schema.structure.bdCarto.administratif;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;


public abstract class LimiteAdministrative extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des g�om�tries multiples (plusieurs tron�ons) */
	//	  private GM_Curve geometrie = null;
	/** Renvoie le GM_LineString qui d�finit la g�om�trie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** D�finit le GM_LineString qui d�finit la g�om�trie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	/////////////// ATTRIBUTS //////////////////
	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String precision;
	public String getPrecision() {return this.precision; }
	public void setPrecision (String Precision) {precision = Precision; }

}