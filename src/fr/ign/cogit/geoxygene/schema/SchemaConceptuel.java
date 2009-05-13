package fr.ign.cogit.geoxygene.schema;

import java.util.List;

import fr.ign.cogit.geoxygene.feature.type.GF_FeatureType;

/**
 * 
 * @author Abadie, Balley
 * Interface pour tout sch�ma conceptuel.
 * (Application schema dans les normes ISO)
 * 
 * Il n'a pas de type de donn�es standard ApplicationSchema.
 * Nous le d�finissons comme un ensemble d'�l�ments standards
 * d�finis dans le package fr.ign.cogit.appli.commun.metadonnees.schemaConceptuel.interfacesISO.
 */
public interface SchemaConceptuel {


	/**
	 * D�signation usuelle du sch�ma
	 */
	public String getNomSchema();
	public void setNomSchema(String nom);


	/**
	 * Description du sch�ma
	 */
	public String getDefinition();
	public void setDefinition(String def);

	/**
	 * Liste des classes appartenant au sch�ma
	 */
	public List<GF_FeatureType> getFeatureTypes();
	public void setFeatureTypes(List<GF_FeatureType> ftList);
	public void addFeatureType(GF_FeatureType ft);
	public void removeFeatureTypeFromSchema(GF_FeatureType ft);
	public GF_FeatureType getFeatureTypeByName(String name);

	/*
	 * ******************************************************************
	 * M�thodes pour manipuler mon sch�ma
	 * ******************************************************************
	 */

	//m�thodes enlev�es, descendues dans schemaISOJeu et SchemaISOProduit

	/*
	 * ******************************************************************
	 * M�thodes pour lister les diff�rents �l�ments du sch�ma
	 * ******************************************************************
	 */

	//	m�thodes enlev�es, descendues dans schemaISOJeu et SchemaISOProduit

	/*
	 * ******************************************************************
	 * M�thodes pour sauvegarder mon sch�ma
	 * ******************************************************************
	 */

	/*	m�thodes enlev�es car elles obligeaient un import de classe "outil"
	 * dans le modele. Les m�thodes save et delete sont implementees de fa�on
	 * statique dans SchemaPersistentOJB
	 */

	public void initNM();

}
