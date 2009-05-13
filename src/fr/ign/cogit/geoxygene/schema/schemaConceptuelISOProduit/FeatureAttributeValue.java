
package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit;

import fr.ign.cogit.geoxygene.feature.type.FC_FeatureAttributeValue;
import fr.ign.cogit.geoxygene.feature.type.GF_AttributeType;


/**
 * @author Sandrine Balley
 *
 */
public class FeatureAttributeValue implements FC_FeatureAttributeValue {

	/**
	 * Valeur d'attribut s'appliquant � un attribut de type �num�r�
	 */
	public FeatureAttributeValue() {super();}

	/**Identifiant d'un objet*/
	protected int id;
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}



	/**
	 * Non standard
	 * Utile aux applications de transformation de sch�ma
	 * caract�re implicite ou explicite de l'�l�ment : un featureAttributeValue
	 * implicite n'est jamais affect� � priori mais il peut l'�tre par le
	 * biais de calculs � partir d'�l�ments explicites
	 **/
	protected boolean isExplicite;
	/** Renvoie le caractere explicite ou implicite */
	public boolean getIsExplicite(){return this.isExplicite;}
	/** Affecte le caract�re implicite ou explicite */
	public void setIsExplicite(boolean value){this.isExplicite = value;}



	// ///////////////////////////////////////////////////////////////////
	// Modifications Nathalie
	// ///////////////////////////////////////////////////////////////////
	/** attribut auquel s'applique cette valeur**/
	protected AttributeType featureAttribute;
	/** Renvoie le feature attribute auquel s'applique cette valeur. */
	public GF_AttributeType getFeatureAttribute(){return this.featureAttribute;}
	/** Affecte un feature attribute auquel s'applique cette valeur. */
	public void setFeatureAttribute(GF_AttributeType FeatureAttribute){
		AttributeType old = this.featureAttribute;
		this.featureAttribute = (AttributeType)FeatureAttribute;
		if (old != null){old.getValuesDomain().remove(this);}
		if (FeatureAttribute != null){
			if (! FeatureAttribute.getValuesDomain().contains(this)){
				FeatureAttribute.getValuesDomain().add(this);
			}
		}
	}
	// /////////////////////////////////////////////////////////////////////

	/** label pour une valeur d'attribut**/
	protected String label;
	/** Renvoie la valeur d'attribut */
	public String getLabel(){return this.label;}
	/** Affecte la valeur d'attribut */
	public void setLabel(String Label){this.label = Label;}

	/** code pour une valeur d'attribut**/
	protected int code;
	/** Renvoie la valeur d'attribut */
	public int getcode(){return this.code;}
	/** Affecte la valeur d'attribut */
	public void setCode(int value){this.code = value;}

	/** definition de la valeur.*/
	protected String definition;
	/** Renvoie la d�finition. */
	public String getDefinition (){return this.definition;}
	/** Affecte une d�finition. */
	public void setDefinition (String Definition) {this.definition = Definition;}


	/**la d�finition semantique du featureType (sous la forme d'un String
	 ou d'une classe d'ontologie)
	 ou un pointeur vers cette d�finition (sous la forme d'une URI)*/
	protected Object definitionReference;
	/**
	 * @return the definitionReference
	 */
	public Object getDefinitionReference() {
		return definitionReference;
	}

	/**
	 * @param definitionReference the definitionReference to set
	 */
	public void setDefinitionReference(Object definitionReference) {
		this.definitionReference = definitionReference;
	}



}
