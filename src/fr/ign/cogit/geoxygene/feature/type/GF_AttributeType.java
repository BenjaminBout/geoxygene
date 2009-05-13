/*
 * Cr�� le 30 sept. 2004
 *
 * Pour changer le mod�le de ce fichier g�n�r�, allez � :
 * Fen�tre&gt;Pr�f�rences&gt;Java&gt;G�n�ration de code&gt;Code et commentaires
 */
package fr.ign.cogit.geoxygene.feature.type;


import java.util.List;


/**
 * @author Balley
 *
 * GF_AttributeType propos� par le General Feature Model de la norme ISO19109.
 * Nous y ajoutons le lien avec FC_FeatureAttributeValue defini dans la norme ISO 19110
 * pour preciser les domaines de valeurs enum�r�s.
 */
public interface GF_AttributeType extends GF_PropertyType{


	/** Renvoie le type de l'attribut. */
	public String getValueType ();
	/** Affecte un type � l'attribut. */
	public void setValueType(String ValueType);



	/** Renvoie le domaine de valeur. */
	public String getDomainOfValues ();
	/** Affecte un domaine de valeur. */
	public void setDomainOfValues(String DomainOfValues);



	/** Renvoie le nombre de valeurs minimal de l'attribut. */
	public int getCardMin ();
	/** Affecte un nombre de valeurs minimal � l'attribut. */
	public void setCardMin (int CardMin);



	/** Renvoie le nombre de valeurs maximal de l'attribut. */
	public int getCardMax ();
	/** Affecte un nombre de valeurs maximal � l'attribut. */
	public void setCardMax (int CardMax);




	/** Renvoie l'attribut que self caract�rise. */
	public GF_AttributeType getCharacterize();
	/** Affecte un attribut que self caract�rise. */
	public void setCharacterize(GF_AttributeType Characterize);




	/** Renvoie le type de domaine de valeur de l'attribut.
	 * (FALSE pour non enum�r�, TRUE pour �num�r�
	 * C'est une extension du GFM propos�e par la norme ISO 19110
	 **/
	public boolean getValueDomainType();
	/** Affecte un type � l'attribut. */
	public void setValueDomainType(boolean ValueDomainType);

	/** Renvoie les valeurs possibles d'un attribut enumere. */
	public List<FC_FeatureAttributeValue> getValuesDomain();

	/** Renvoie les valeurs possibles d'un attribut enumere sous forme de liste de
	 * chaines de caract�res.
	 * Peut servir avantageusement � affecter une valeur � l'attribut
	 * domainOfValues propos� par le GFM
	 **/
	public List<String> getValuesDomainToString();
	/** Affecte une liste de valeurs possibles. */
	public void setValuesDomain(List<FC_FeatureAttributeValue> ValuesDomain);
	/** Ajoute une valeur � la liste des valeurs possibles de l'attribut. */
	public void addValuesDomain(FC_FeatureAttributeValue value);
	/** Enleve une valeur a la liste des valeurs possibles de l'attribut. */
	public void removeValuesDomain(FC_FeatureAttributeValue value);


	/**renvoie la d�finition semantique de l'AttributeType (sous la forme d'un String
	 * ou d'une classe d'ontologie)
	 * ou un pointeur vers cette d�finition (sous la forme d'une URI)
	 * 
	 * Correspond � FC_DescriptionReference et FC_DescriptionSource propos�s
	 * dans la norme ISO19110
	 * @return Object
	 */
	public Object getDefinitionReference();
	public void setDefinitionReference(Object ref);

}
