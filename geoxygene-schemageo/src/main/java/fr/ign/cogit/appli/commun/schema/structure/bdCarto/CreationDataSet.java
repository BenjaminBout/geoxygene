package fr.ign.cogit.appli.commun.schema.structure.bdCarto;

import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.appli.commun.schema.structure.bdCarto.administratif.Arrondissement;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.administratif.Canton;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.administratif.Commune;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.administratif.Departement;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.administratif.LimiteAdministrative;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.administratif.Region;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements.Aerodrome;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements.Cimetiere;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements.ConstructionElevee;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements.Digue;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements.EnceinteMilitaire;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements.LigneElectrique;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements.MetroAerien;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements.PisteAerodrome;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.equipements.TransportParCable;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ferre.LigneCheminDeFer;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ferre.NoeudFerre;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.ferre.TronconFerre;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.franchissement.Franchissement;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.franchissement.PassePar;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.habillage.ZoneOccupationDuSol;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro.CoursDEau;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro.Laisse;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro.NoeudHydrographique;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro.PointDEauIsole;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro.ToponymeHydrographieSurfacique;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro.TronconHydrographique;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.hydro.ZoneHydrographiqueDeTexture;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier.Accede;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier.CarrefourComplexe;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier.CommunicationRestreinte;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier.DebutSection;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier.EquipementRoutier;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier.ItineraireRoutier;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier.LiaisonMaritime;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier.NoeudRoutier;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier.Route;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.routier.TronconRoute;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.toponymes.Etablissement;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.toponymes.GrandeRandonnee;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.toponymes.MassifBoise;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.toponymes.PointRemarquableRelief;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.toponymes.ZoneDActivite;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.toponymes.ZoneDHabitat;
import fr.ign.cogit.appli.commun.schema.structure.bdCarto.toponymes.ZoneReglementeeTouristique;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.Population;

public class CreationDataSet  {

	/** Cr�ation des th�mes et des populations des th�mes
	 * d'un jeu de donn�es BDCarto suivant le mod�le structur� interne IGN (ou presque).
	 * 
	 * @param persistance : D�finit si le jeu de donn�es est persistant ou non.
	 * Si il est persistant, tous les th�mes et populations cr��es le sont
	 * aussi par d�faut, mais cela peut �tre chang� par la suite.
	 * 
	 * @param nom_package_jeu_complet : nom du package qui contient les classes concr�tes
	 * des populations du jeu de donn�es (chemin complet du package contenant
	 * les sous package des th�mes).
	 * 
	 * @param metadonnees : Liste de string (peut-�tre null) repr�sentant dans l'ordre:
	 * - le nom logique de la base (texte libre pour la d�co),
	 * - la date des donn�es
	 * - la zone couverte par les donn�es
	 * - un commentaire
	 * Tous les textes sont libres et d'au plus 255 caract�res
	 * 
	 */
	public static DataSet nouveauDataSet(boolean persistance, String nom_package_jeu_complet, List<String> metadonnees) {
		DataSet jeu = new DataSet();
		if (persistance) DataSet.db.makePersistent(jeu);

		//m�tadonn�es
		jeu.setTypeBD("BDCarto");
		jeu.setModele("Structur�");
		if (metadonnees != null) {
			Iterator<String> itMD = metadonnees.iterator();
			if (itMD.hasNext()) jeu.setNom(itMD.next());
			if (itMD.hasNext()) jeu.setDate(itMD.next());
			if (itMD.hasNext()) jeu.setZone(itMD.next());
			if (itMD.hasNext()) jeu.setCommentaire(itMD.next());
		}

		// cr�ation des th�mes
		ajouteThemeAdmin(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeEquipements(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeFerre(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeHabillage(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeHydrographie(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeRoutier(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeToponymes(jeu, nom_package_jeu_complet, persistance);
		ajouteThemeFranchissements(jeu, nom_package_jeu_complet, persistance);

		return jeu;
	}

	public static void ajouteThemeAdmin(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Th�me 'Administratif' de la BDCarto");
		theme.setNom("Administratif");
		String nom_package = nom_package_jeu_complet+".administratif";
		try{
			Population<?> pop;
			pop = new Population<Arrondissement>(persistance, "Arrondissements", Class.forName(nom_package+".Arrondissement"),true);
			theme.addPopulation(pop);
			pop = new Population<Canton>(persistance, "Cantons", Class.forName(nom_package+".Canton"),true);
			theme.addPopulation(pop);
			pop = new Population<Commune>(persistance, "Communes", Class.forName(nom_package+".Commune"),true);
			theme.addPopulation(pop);
			pop = new Population<Departement>(persistance, "D�partements", Class.forName(nom_package+".Departement"),true);
			theme.addPopulation(pop);
			pop = new Population<Region>(persistance, "R�gions", Class.forName(nom_package+".Region"),true);
			theme.addPopulation(pop);
			pop = new Population<LimiteAdministrative>(persistance, "Limites administratives", Class.forName(nom_package+".LimiteAdministrative"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Probl�me de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeEquipements(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Th�me 'Equipements' de la BDCarto");
		theme.setNom("Equipements");
		String nom_package = nom_package_jeu_complet+".equipements";
		try{
			Population<?> pop;
			pop = new Population<Aerodrome>(persistance, "A�rodromes", Class.forName(nom_package+".Aerodrome"),true);
			theme.addPopulation(pop);
			pop = new Population<Cimetiere>(persistance, "Cimeti�res", Class.forName(nom_package+".Cimetiere"),true);
			theme.addPopulation(pop);
			pop = new Population<ConstructionElevee>(persistance, "Constructions �lev�es", Class.forName(nom_package+".ConstructionElevee"),true);
			theme.addPopulation(pop);
			pop = new Population<EnceinteMilitaire>(persistance, "Enceintes militaires", Class.forName(nom_package+".EnceinteMilitaire"),true);
			theme.addPopulation(pop);
			pop = new Population<LigneElectrique>(persistance, "Lignes �lectriques", Class.forName(nom_package+".LigneElectrique"),true);
			theme.addPopulation(pop);
			pop = new Population<PisteAerodrome>(persistance, "Pistes d'a�rodrome", Class.forName(nom_package+".PisteAerodrome"),true);
			theme.addPopulation(pop);
			pop = new Population<Digue>(persistance, "Digues", Class.forName(nom_package+".Digue"),true);
			theme.addPopulation(pop);
			pop = new Population<TransportParCable>(persistance, "Transports par cable", Class.forName(nom_package+".TransportParCable"),true);
			theme.addPopulation(pop);
			pop = new Population<MetroAerien>(persistance, "M�tros a�riens", Class.forName(nom_package+".MetroAerien"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Probl�me de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeFerre(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Th�me 'R�seau ferr�' de la BDCarto");
		theme.setNom("Ferr�");
		String nom_package = nom_package_jeu_complet+".ferre";
		try{
			Population<?> pop;
			pop = new Population<NoeudFerre>(persistance, "Noeuds ferr�s", Class.forName(nom_package+".NoeudFerre"),true);
			theme.addPopulation(pop);
			pop = new Population<TronconFerre>(persistance, "Tron�ons ferr�s", Class.forName(nom_package+".TronconFerre"),true);
			theme.addPopulation(pop);
			pop = new Population<LigneCheminDeFer>(persistance, "Lignes de chemin de fer", Class.forName(nom_package+".LigneCheminDeFer"),false);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Probl�me de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeHabillage(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Th�me 'Habillage' de la BDCarto");
		theme.setNom("Habillage");
		String nom_package = nom_package_jeu_complet+".habillage";
		try{
			Population<?> pop;
			pop = new Population<ZoneOccupationDuSol>(persistance, "Zones d'occupation du sol", Class.forName(nom_package+".ZoneOccupationDuSol"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Probl�me de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeHydrographie(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Th�me 'Hydrographie' de la BDCarto");
		theme.setNom("Hydrographie");
		String nom_package = nom_package_jeu_complet+".hydro";
		try{
			Population<?> pop;
			pop = new Population<CoursDEau>(persistance, "Cours d'eau nomm�s", Class.forName(nom_package+".CoursDEau"),false);
			theme.addPopulation(pop);
			pop = new Population<Laisse>(persistance, "Laisses", Class.forName(nom_package+".Laisse"),true);
			theme.addPopulation(pop);
			pop = new Population<NoeudHydrographique>(persistance, "Noeuds hydrographiques", Class.forName(nom_package+".NoeudHydrographique"),true);
			theme.addPopulation(pop);
			pop = new Population<PointDEauIsole>(persistance, "Points d'eau isol�s", Class.forName(nom_package+".PointDEauIsole"),true);
			theme.addPopulation(pop);
			pop = new Population<ToponymeHydrographieSurfacique>(persistance, "Toponymes d'hydrographie surfacique", Class.forName(nom_package+".ToponymeHydrographieSurfacique"),true);
			theme.addPopulation(pop);
			pop = new Population<TronconHydrographique>(persistance, "Tron�ons hydrographiques", Class.forName(nom_package+".TronconHydrographique"),true);
			theme.addPopulation(pop);
			pop = new Population<ZoneHydrographiqueDeTexture>(persistance, "Zones hydrographiques de texture", Class.forName(nom_package+".ZoneHydrographiqueDeTexture"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Probl�me de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeRoutier(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Th�me 'R�seau routier et franchissements' de la BDCarto");
		theme.setNom("Routier");
		String nom_package = nom_package_jeu_complet+".routier";
		try{
			Population<?> pop;
			pop = new Population<Accede>(persistance, "Acc�de", Class.forName(nom_package+".Accede"),false);
			theme.addPopulation(pop);
			pop = new Population<CarrefourComplexe>(persistance, "Carrefours complexes", Class.forName(nom_package+".CarrefourComplexe"),false);
			theme.addPopulation(pop);
			pop = new Population<CommunicationRestreinte>(persistance, "Communications restreintes", Class.forName(nom_package+".CommunicationRestreinte"),false);
			theme.addPopulation(pop);
			pop = new Population<DebutSection>(persistance, "D�buts de section", Class.forName(nom_package+".DebutSection"),false);
			theme.addPopulation(pop);
			pop = new Population<EquipementRoutier>(persistance, "Equipements routiers", Class.forName(nom_package+".EquipementRoutier"),true);
			theme.addPopulation(pop);
			pop = new Population<ItineraireRoutier>(persistance, "Itin�raires routiers", Class.forName(nom_package+".ItineraireRoutier"),false);
			theme.addPopulation(pop);
			pop = new Population<LiaisonMaritime>(persistance, "Liaisons maritimes", Class.forName(nom_package+".LiaisonMaritime"),true);
			theme.addPopulation(pop);
			pop = new Population<NoeudRoutier>(persistance, "Noeuds routiers", Class.forName(nom_package+".NoeudRoutier"),true);
			theme.addPopulation(pop);
			pop = new Population<Route>(persistance, "Routes", Class.forName(nom_package+".Route"),false);
			theme.addPopulation(pop);
			pop = new Population<TronconRoute>(persistance, "Tron�ons de route", Class.forName(nom_package+".TronconRoute"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Probl�me de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeToponymes(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Th�me 'Toponymes' de la BDCarto");
		theme.setNom("Toponymes");
		String nom_package = nom_package_jeu_complet+".toponymes";
		try{
			Population<?> pop;
			pop = new Population<ZoneDHabitat>(persistance, "Zones d'habitat", Class.forName(nom_package+".ZoneDHabitat"),true);
			theme.addPopulation(pop);
			pop = new Population<ZoneDActivite>(persistance, "Zones d'activit�", Class.forName(nom_package+".ZoneDActivite"),true);
			theme.addPopulation(pop);
			pop = new Population<ZoneReglementeeTouristique>(persistance, "Zones r�glement�es d'int�r�t touristique", Class.forName(nom_package+".ZoneReglementeeTouristique"),true);
			theme.addPopulation(pop);
			pop = new Population<Etablissement>(persistance, "Etablissements", Class.forName(nom_package+".Etablissement"),true);
			theme.addPopulation(pop);
			pop = new Population<GrandeRandonnee>(persistance, "Sentiers de grande randonn�e", Class.forName(nom_package+".GrandeRandonnee"),true);
			theme.addPopulation(pop);
			pop = new Population<MassifBoise>(persistance, "Massifs bois�s", Class.forName(nom_package+".MassifBoise"),true);
			theme.addPopulation(pop);
			pop = new Population<PointRemarquableRelief>(persistance, "Points remarquables du relief", Class.forName(nom_package+".PointRemarquableRelief"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Probl�me de nom de package : "+nom_package_jeu_complet);
		}
	}

	public static void ajouteThemeFranchissements(DataSet jeu, String nom_package_jeu_complet, boolean persistance) {
		DataSet theme = new DataSet(jeu);
		if (persistance) DataSet.db.makePersistent(theme);
		jeu.addComposant(theme);
		theme.setTypeBD("Th�me franchissement de la BDCarto");
		theme.setNom("Franchissements");
		String nom_package = nom_package_jeu_complet+".franchissement";
		try{
			Population<?> pop;
			pop = new Population<PassePar>(persistance, "Passe par", Class.forName(nom_package+".PassePar"),false);
			theme.addPopulation(pop);
			pop = new Population<Franchissement>(persistance, "Franchissements", Class.forName(nom_package+".Franchissement"),true);
			theme.addPopulation(pop);
		}
		catch (Exception e) {
			System.out.println("Probl�me de nom de package : "+nom_package_jeu_complet);
		}
	}
}