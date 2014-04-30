package fr.ign.cogit.appli.commun.schema.shp.bdcarto.equipements;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;


/** Classe geographique. Classe generee automatiquement par le chargeur de la plate-forme Oxygene*/

public abstract class EnceinteMilitaire extends fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto {

	/** Renvoie la g�om�trie de l'objet, cast�e plus pr�cis�ment qu'avec la m�thode getGeom() */
	public GM_Polygon getGeometrie() {return (GM_Polygon)geom;}
	/** D�finit la g�om�trie de l'objet, cast�e plus pr�cis�ment qu'avec la m�thode setGeom() */
	public void setGeometrie(GM_Polygon G) {this.geom = G;}

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

	protected String toponyme;
	public String getToponyme() {return this.toponyme; }
	public void setToponyme (String Toponyme) {toponyme = Toponyme; }

}
