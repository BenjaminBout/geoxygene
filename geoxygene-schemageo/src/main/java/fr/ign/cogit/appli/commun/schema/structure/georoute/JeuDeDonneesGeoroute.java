package fr.ign.cogit.appli.commun.schema.structure.georoute;

import fr.ign.cogit.appli.commun.schema.structure.georoute.routier.ThemeRoutier;
import fr.ign.cogit.geoxygene.feature.DataSet;


public class JeuDeDonneesGeoroute extends DataSet {

	/** Constructeur par d�faut */
	public JeuDeDonneesGeoroute() {this.ojbConcreteClass = this.getClass().getName();}

	/** Constructeur d'un jeu de donn�es G�oroute au format structur� :
	 * - un jeu de donn�es G�oroute contient les th�mes routier  :
	 * - ces th�mes ont des noms par d�faut � ne pas changer : "routier"
	 * - les populations de ces th�mes ont des noms logique par d�faut "Troncon de route", "Noeud routier", etc.
	 * - leurs �l�ments se r�alisent dans des classes contr�tes du package nom_package.
	 * - un jeu de donn�es peut �tre persistant ou non
	 * - un jeu de donn�es a un nom logique (utile pour naviguer entre jeux de donn�es).
	 */

	public JeuDeDonneesGeoroute(boolean persistance, String nom_logique, String nom_package) {
		this.ojbConcreteClass = this.getClass().getName(); // n�cessaire pour ojb
		this.setTypeBD("G�oroute");
		this.setModele("Structur�");
		this.setNom(nom_logique);
		if (persistance) DataSet.db.makePersistent(this);
		ThemeRoutier routier = new ThemeRoutier(persistance, nom_package+".routier", this);
		this.addComposant(routier);
		//ThemeDestination destination = new ThemeDestination(persistance, nom_package+".destination", this);
		//this.addComposant(destination);
	}



	public ThemeRoutier getThemeRoutier() {return (ThemeRoutier)this.getComposant("routier");}
	//public ThemeDestination getThemeDestination() {return (ThemeDestination)this.getComposant("destination");}

}

