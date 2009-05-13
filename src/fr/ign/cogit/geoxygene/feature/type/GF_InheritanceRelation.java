/*
 * Cr�� le 30 sept. 2004
 *
 * Pour changer le mod�le de ce fichier g�n�r�, allez � :
 * Fen�tre&gt;Pr�f�rences&gt;Java&gt;G�n�ration de code&gt;Code et commentaires
 */
package fr.ign.cogit.geoxygene.feature.type;


/**
 * @author Balley
 *
 * GF_InheritanceRelation propos� par le General Feature Model de la norme ISO1909.
 * Une relation d'h�ritage a exactement un subType et une superType.
 */
public interface GF_InheritanceRelation {



	/** Renvoie le nom. */
	public String getName () ;
	/** Affecte un nom. */
	public void setName(String Name);



	/** Renvoie la description. */
	public String getDescription ();
	/** Affecte une description. */
	public void setDescription (String Description);



	/** Renvoie l'attribut uniqueInstance.
	 * Dans la norme, cet attribut indique si une instance du supertype peut �tre
	 * instance de plusieurs subtypes. La norme est ici ambig�e puisqu'elle indique
	 * par ailleurs qu'une relation d'heritage ne comporte qu'un subtype : cela
	 * signifierait que l'attribut uniqueInstance est superflu au niveau de la
	 * relation d'h�ritage mais devrait �tre d�fini au niveau du superType.
	 * 
	 * "UniqueInstance is a Boolean variable, where .TRUE. means that an instance of the supertype shall not
	 * be an instance of more than one of the subtypes, whereas .FALSE. means that an instance of the
	 * supertype may be an instance of more than one subtype."*/

	public boolean getUniqueInstance ();
	/** Affecte l'attribut uniqueInstance.. */
	public void setUniqueInstance (boolean UniqueInstance);



	/** Renvoie la classe m�re de la relation d'h�ritage. */
	public GF_FeatureType getSuperType();
	/** Affecte une classe m�re � la relation d'h�ritage. */
	public void setSuperType(GF_FeatureType SuperType);


	/** Renvoie la classe fille de la relation d'h�ritage. */
	public GF_FeatureType getSubType();
	/** Affecte une classe fille � la relation d'h�ritage. */
	public void setSubType(GF_FeatureType SubType);



}
