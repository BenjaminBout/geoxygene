package fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.divers;


import fr.ign.cogit.appli.commun.schema.shp.bdPaysShape2D.ElementBDPays;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public abstract class ToponymeCommunication extends ElementBDPays {

	/** Renvoie la g�om�trie de l'objet, cast�e plus pr�cis�ment qu'avec la m�thode getGeom() */
	public GM_Point getGeometrie() {return (GM_Point)geom;}
	/** D�finit la g�om�trie de l'objet, cast�e plus pr�cis�ment qu'avec la m�thode setGeom() */
	public void setGeometrie(GM_Point G) {this.geom = G;}

	protected String nom;
	public String getNom() {return this.nom; }
	public void setNom (String Nom) {nom = Nom; }

	protected String importance;
	public String getImportance() {return this.importance; }
	public void setImportance (String Importance) {importance = Importance; }

	protected String nature;
	public String getNature() {return this.nature; }
	public void setNature (String Nature) {nature = Nature; }

}
