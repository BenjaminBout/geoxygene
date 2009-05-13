package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit;

import fr.ign.cogit.geoxygene.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.feature.type.GF_InheritanceRelation;



/**
 * @author Sandrine Balley
 *
 */
public class InheritanceRelation implements GF_InheritanceRelation {


	public InheritanceRelation() {
		super();
	}

	/**Identifiant d'un objet*/
	protected int id;
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}

	/**Nom de la g�n�ralisation ou de la sp�cialisation. */
	protected String name;
	/** Renvoie le nom. */
	public String getName () {return this.name;}
	/** Affecte un nom. */
	public void setName(String Name) {this.name = Name;}



	/** Classe m�re de la relation d'heritage. */
	protected FeatureType superType;
	/** Renvoie la classe m�re de la relation d'h�ritage. */
	public GF_FeatureType getSuperType() { return superType; }

	/** Affecte une classe m�re � la relation d'h�ritage. */
	public void setSuperType(GF_FeatureType SuperType) {
		FeatureType old=this.superType;
		this.superType = (FeatureType)SuperType;
		if (old!=null){old.getSpecialization().remove(this);}
		if (SuperType!=null){
			if (!SuperType.getSpecialization().contains(this))
				SuperType.addSpecialization(this);}
	}


	/** Classe fille de la relation d'heritage. */
	protected FeatureType subType;
	/** Renvoie la classe fille de la relation d'h�ritage. */
	public GF_FeatureType getSubType() { return subType; }

	/** Affecte une classe fille � la relation d'h�ritage. */
	public void setSubType(GF_FeatureType SubType) {
		FeatureType old = this.subType;
		this.subType = (FeatureType)SubType;
		if (old!=null) {old.getGeneralization().remove(this);}
		if (SubType!=null){
			if (!SubType.getGeneralization().contains(this))
				SubType.addGeneralization(this);}
	}

	/** Description.*/
	protected String description;
	/** Renvoie la description. */
	public String getDescription () {return this.description;}
	/** Affecte une description. */
	public void setDescription (String Description) {this.description = Description;}


	/** TRUE si une instance de l'hyperclasse doit �tre au plus dans une sous-classe, FALSE sinon. */
	protected boolean uniqueInstance;
	/** Renvoie l'attribut uniqueInstance. */
	public boolean getUniqueInstance () {return this.uniqueInstance;}
	/** Affecte l'attribut uniqueInstance.. */
	public void setUniqueInstance (boolean UniqueInstance) {this.uniqueInstance = UniqueInstance;}


	/**booleen indiquant si l'heritage est represente par un "extends" en Java(1) ou s'il n'est pas explicitement represente(0)*/
	protected boolean boolExtends;
	public boolean getBoolExtends(){return this.boolExtends;}
	public void setBoolExtends(boolean value){this.boolExtends = value;}

	/** proprietaire de l'�l�ment (producteur=1, utilisateur=2)**/
	protected int proprietaire;
	/** Renvoie le proprietaire */
	public int getProprietaire(){return this.proprietaire;}
	/** Affecte le proprietaire */
	public void setProprietaire(int value){this.proprietaire = value;}



}


