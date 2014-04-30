package fr.ign.cogit.appli.commun.schema.shp.bdcarto.equipements;

import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;


/** Classe geographique. Classe generee automatiquement par le chargeur de la plate-forme Oxygene*/

public abstract class ConstructionElevee extends fr.ign.cogit.appli.commun.schema.shp.bdcarto.ElementBDCarto {

	/** Renvoie la g�om�trie de l'objet, cast�e plus pr�cis�ment qu'avec la m�thode getGeom() */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** D�finit la g�om�trie de l'objet, cast�e plus pr�cis�ment qu'avec la m�thode setGeom() */
	public void setGeometrie(GM_Point G) {this.geom = G;}

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

}
