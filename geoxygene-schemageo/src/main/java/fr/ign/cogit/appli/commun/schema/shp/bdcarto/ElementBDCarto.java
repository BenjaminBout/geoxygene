package fr.ign.cogit.appli.commun.schema.shp.bdcarto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.Population;

/** Classe m�re pour toute classe d'�l�ments de la BDCarto V2 au format de livraison shape.
 * Document de r�f�rence: descriptif de livraison shapefile, edition 2, 2001.
 */

@SuppressWarnings("unchecked")

public abstract class ElementBDCarto extends FT_Feature {

	/** Identifiant donn� par la BDCarto */
	protected double id_bdcarto;
	/** Identifiant donn� par la BDCarto */
	public double getId_bdcarto() {return this.id_bdcarto; }
	/** Identifiant donn� par la BDCarto */
	public void setId_bdcarto (double Id_bdcarto) {id_bdcarto = Id_bdcarto; }


	public static ElementBDCarto recherche_element_avec_idbdc(Population pop, double id) {
		ElementBDCarto element;
		Iterator it = pop.getElements().iterator();
		while ( it.hasNext() ) {
			element = (ElementBDCarto)it.next();
			if ( element.getId_bdcarto() == id ) return element;
		}
		return null;
	}

	public static List search_list_idBDCarto (Population pop,int id){
		List list_idBDCarto = new ArrayList();
		ElementBDCarto element;
		Iterator it = pop.getElements().iterator();
		while ( it.hasNext() ) {
			element = (ElementBDCarto)it.next();
			if ( element.getId_bdcarto() == id ) list_idBDCarto.add(element);
		}
		if (list_idBDCarto == null) return null;
		return list_idBDCarto;
	}

}
