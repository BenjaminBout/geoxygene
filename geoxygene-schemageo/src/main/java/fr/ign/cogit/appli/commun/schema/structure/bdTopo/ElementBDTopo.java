package fr.ign.cogit.appli.commun.schema.structure.bdTopo;

import fr.ign.cogit.geoxygene.feature.FT_Feature;

/** Classe m�re pour toute classe d'�l�ments de la BDTopo Pays
 * AU FORMAT DE TRAVAIL pour l'appariement.
 */

public abstract class ElementBDTopo extends FT_Feature {

	protected String source;
	public String getSource() {return this.source; }
	public void setSource (String Source) {source = Source; }

}
