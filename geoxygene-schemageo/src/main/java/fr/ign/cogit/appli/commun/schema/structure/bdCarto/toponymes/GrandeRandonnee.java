package fr.ign.cogit.appli.commun.schema.structure.bdCarto.toponymes;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ElementBDCarto;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public abstract class GrandeRandonnee extends ElementBDCarto {

	/////////////// GEOMETRIE //////////////////
	/** Attention: on peut avoir des g�om�tries multiples (plusieurs tron�ons) */
	//	  private GM_Curve geometrie = null;
	/** Renvoie le GM_LineString qui d�finit la g�om�trie de self */
	public GM_LineString getGeometrie() {return (GM_LineString)geom;}
	/** D�finit le GM_LineString qui d�finit la g�om�trie de self */
	public void setGeometrie(GM_LineString G) {this.geom = G;}

	/////////////// ATTRIBUTS //////////////////
	protected String numero;
	public String getNumero() {return this.numero; }
	public void setNumero (String Numero) {numero = Numero; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

}