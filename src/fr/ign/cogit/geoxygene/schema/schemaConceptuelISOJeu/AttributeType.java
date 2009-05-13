package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.feature.type.FC_FeatureAttributeValue;
import fr.ign.cogit.geoxygene.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.feature.type.GF_Constraint;
import fr.ign.cogit.geoxygene.feature.type.GF_FeatureType;

/**
 * @author Sandrine Balley
 * @author Nathalie Abadie
 */
public class AttributeType implements GF_AttributeType {
	/**
	 * Constructeur
	 */
	public AttributeType() {
		super();
		this.valuesDomain = new ArrayList<FC_FeatureAttributeValue>();
	}

	/**
	 * Constructeur � partir d'un AttributeType schemaConceptuelISOProduit
	 * @param ori un AttributeType schemaConceptuelISOProduit
	 */
	public AttributeType(fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType ori) {
		this.id = ori.getId();
		this.elementSchemaProduitOrigine = ori;
		this.memberName = ori.getMemberName();
		this.cardMax = ori.getCardMax();
		this.cardMin = ori.getCardMin();
		this.definition = ori.getDefinition();
		this.domainOfValues = ori.getDomainOfValues();
		this.valueDomainType = ori.getValueDomainType();
		this.valueType = ori.getValueType();
		this.valuesDomain = new ArrayList<FC_FeatureAttributeValue>();
		this.nomField = ori.getNomField();
		this.isExplicite = ori.getIsExplicite();
	}

	/**Identifiant d'un objet*/
	protected int id;
	/** 
	 * Renvoie l'identifiant d'un objet
	 * @return l'Identifiant d'un objet
	 */
	public int getId() {return id;}
	/**
	 * Affecte l'identifiant d'un objet
	 * @param id l'identifiant d'un objet
	 */
	public void setId(int id) {this.id = id;}
	/**nom de la propriete*/
	protected String memberName;
	/** 
	 * Renvoie le nom de la propri�t�.
	 * @return le nom de la propri�t�.
	 */
	public String getMemberName() {return this.memberName;}
	/** 
	 * Affecte un nom de propri�t�.
	 * @param MemberName un nom de propri�t�
	 */
	public void setMemberName(String MemberName) {this.memberName = MemberName;}
	/** Type de l'attribut. */
	protected String valueType;
	/** Renvoie le type de l'attribut. */
	public String getValueType() {return this.valueType;}
	/** Affecte un type � l'attribut. */
	public void setValueType(String ValueType) {this.valueType = ValueType;}
	/** Type de domaine de valeur 0 pour non �num�r� et 1 pour �num�r�. */
	protected boolean valueDomainType;
	/** Renvoie le type de domaine de valeur de l'attribut. */
	public boolean getValueDomainType(){return this.valueDomainType;}
	/** Affecte un type � l'attribut. */
	public void setValueDomainType(boolean ValueDomainType) {this.valueDomainType = ValueDomainType;}
	/** Definition du domaine de valeur POUR LES ENUMERES SEULEMENT. */
	protected List<FC_FeatureAttributeValue> valuesDomain;
	/** FC_featureType auquel se rapporte l'attribut* */
	protected GF_FeatureType featureType;
	/** definition de l'attribut. */
	protected String definition;
	/** Renvoie la d�finition. */
	public String getDefinition() {return this.definition;}
	/** Affecte une d�finition. */
	public void setDefinition(String Definition) {this.definition = Definition;}
	/**la d�finition semantique du featureType (sous la forme d'un String
	 ou d'une classe d'ontologie)
	 ou un pointeur vers cette d�finition (sous la forme d'une URI)*/
	protected Object definitionReference;
	/**
	 * @return the definitionReference
	 */
	public Object getDefinitionReference() {return definitionReference;}
	/**
	 * @param definitionReference the definitionReference to set
	 */
	public void setDefinitionReference(Object definitionReference) {this.definitionReference = definitionReference;}
	/** Nombre de valeurs minimal de l'attribut */
	protected int cardMin;
	/** Renvoie le nombre de valeurs minimal de l'attribut. */
	public int getCardMin() {return this.cardMin;}
	/** Affecte un nombre de valeurs minimal � l'attribut. */
	public void setCardMin(int CardMin) {this.cardMin = CardMin;}
	/** Nombre de valeurs maximal de l'attribut */
	protected int cardMax;
	/** Renvoie le nombre de valeurs maximal de l'attribut. */
	public int getCardMax() {return this.cardMax;}
	/** Affecte un nombre de valeurs maximal � l'attribut. */
	public void setCardMax(int CardMax) {this.cardMax = CardMax;}
	/** le domaine de valeurs (�num�r� ou non) sous forme de cha�ne de caract�res
	 * exemples : "positif", "toute extension de GM_Object", "oui/non/ind�termin�"
	 **/
	protected String domainOfValues;
	/** Renvoie le domaine de valeurs. */
	public String getDomainOfValues() {return domainOfValues;}
	/** Affecte un domaine de valeurs. */
	public void setDomainOfValues(String domain) {this.domainOfValues = domain;}
	/**
	 * le nom du champ correspondant a l'attribut dans la classe ou
	 * classe-association Java correspondante
	 */
	protected String nomField;
	/**
	 * @return le nom du champ correspondant a l'attribut dans la classe ou
	 * classe-association Java correspondante
	 */
	public String getNomField() {return this.nomField;}
	/**
	 * Affecte le nom du champ correspondant a l'attribut dans la classe ou
	 * classe-association Java correspondante
	 * @param value le nom du champ correspondant a l'attribut dans la classe ou
	 * classe-association Java correspondante
	 */
	public void setNomField(String value) {this.nomField = value;}
	/** booleen indiquant si l'attribut est de type spatial */
	protected boolean isSpatial;
	public boolean getIsSpatial() {return isSpatial;}
	public void setIsSpatial(boolean isSpatial) {this.isSpatial = isSpatial;}
	/** booleen indiquant si l'attribut est de type topologique */
	protected boolean isTopologic;
	public boolean getIsTopologic() {return isTopologic;}
	public void setTopologic(boolean isTopologic) {this.isTopologic = isTopologic;}
	/**L'�l�ment de sch�ma conceptuel de produit dont provient cet attribut*/
	protected fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType elementSchemaProduitOrigine;
	/**
	 * @return the elementSchemaProduitOrigine
	 */
	public fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType getElementSchemaProduitOrigine() {return elementSchemaProduitOrigine;}
	/**
	 * @param elementSchemaProduitOrigine the elementSchemaProduitOrigine to set
	 */
	public void setElementSchemaProduitOrigine(fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType elementSchemaProduitOrigine) {this.elementSchemaProduitOrigine = elementSchemaProduitOrigine;}
	/**
	 * Non standard
	 * Utile aux applications de transformation de sch�ma
	 * caract�re implicite ou explicite de l'�l�ment : un attributeType implicite
	 * n'a pas de valeur � priori mais celle-ci pourra �tre d�riv�e
	 * d'elements explicites par le biais de transformations
	 **/
	protected boolean isExplicite;
	/** Renvoie le caractere explicite ou implicite */
	public boolean getIsExplicite() {return this.isExplicite;}
	/** Affecte le caract�re implicite ou explicite */
	public void setIsExplicite(boolean value) {this.isExplicite = value;}
	/**
	 * commodit� pour retrouver � quel sch�ma conceptuel appartient cet attribut
	 * (en passant par le featureType qu'il caract�rise).
	 **/
	public SchemaConceptuelJeu getSchemaConceptuel() {
		if (this.getFeatureType()!=null) return ((FeatureType)this.getFeatureType()).getSchema();
		return null;
	}
	
	// //////////////////////////////////////////////////////
	// Modifications Nathalie
	// //////////////////////////////////////////////////////
	/** Renvoie les valeurs possibles d'un attribut enumere. */
	public List<FC_FeatureAttributeValue> getValuesDomain() {return this.valuesDomain;}
	/** Renvoie les valeurs possibles d'un attribut enumere sous forme de liste de chaines de caract�res. */
	public List<String> getValuesDomainToString() {
		List<String> valeurs = new ArrayList<String>();
		Iterator<FC_FeatureAttributeValue> it = this.valuesDomain.iterator();
		while(it.hasNext()){
			FeatureAttributeValue val = (FeatureAttributeValue)it.next();
			valeurs.add(val.getLabel());
		}
		return valeurs;
	}
	/** Affecte une liste de valeurs possibles. */
	public void setValuesDomain(List<FC_FeatureAttributeValue> ValuesDomain) {
		List<FC_FeatureAttributeValue> old = new ArrayList<FC_FeatureAttributeValue>(this.valuesDomain);
		Iterator<FC_FeatureAttributeValue> it1 = old.iterator();
		while (it1.hasNext()){
			FeatureAttributeValue ancienneValeur = (FeatureAttributeValue)it1.next();
			ancienneValeur.setFeatureAttribute(null);
		}
		Iterator<FC_FeatureAttributeValue> it2 = ValuesDomain.iterator();
		while (it2.hasNext()){
			FeatureAttributeValue maValeur = (FeatureAttributeValue)it2.next();
			maValeur.setFeatureAttribute(this);
		}
	}
	/** Ajoute une valeur � la liste des valeurs possibles de l'attribut. */
	public void addValuesDomain(FC_FeatureAttributeValue value) {
		if (value==null){return;}
		this.valuesDomain.add(value);
		if (!(value.getFeatureAttribute()==this)){
			value.setFeatureAttribute(this);
		}
	}
	/** Enleve une valeur a la liste des valeurs possibles de l'attribut. */
	public void removeValuesDomain(FC_FeatureAttributeValue value){
		if (value==null){return;}
		this.valuesDomain.remove(value);
		value.setFeatureAttribute(null);
	}

	////////////////////////////////////////////////////////
	// Champs et Methodes herites et utilises
	////////////////////////////////////////////////////////
	/** Renvoie le feature type auquel est rattach� la propri�t�. */
	public GF_FeatureType getFeatureType() {return this.featureType;}
	/** Affecte un feature type � la propri�t�. */
	public void setFeatureType(GF_FeatureType FeatureType) {this.featureType = FeatureType;}

	// m�thodes h�rit�es non utilis�es
	/**
	 * Attribut en caract�risant un autre. La relation inverse n'est pas
	 * impl�ment�e.
	 */
	protected GF_AttributeType characterize;
	/** Renvoie l'attribut que self caract�rise. */
	public GF_AttributeType getCharacterize() {return this.characterize;}
	/** Affecte un attribut que self caract�rise. */
	public void setCharacterize(GF_AttributeType Characterize) {this.characterize = Characterize;}
	/** 
	 * Renvoie la liste des contraintes.
	 * TODO non impl�ment�
	 */
	public List<GF_Constraint> getConstraint() {return null;}
	/**
	 * Affecte une liste de contraintes
	 * TODO non impl�ment�
	 */
	public void setConstraint(List<GF_Constraint> L) {}
	/** Ajoute une contrainte. */
	public void addConstraint(GF_Constraint value) {}
	/** 
	 * Renvoie le nombre de contraintes.
	 * TODO non impl�ment�
	 */
	public int sizeConstraint() {return 0;}
	@Override
	public String toString() {
		String resultat = "AttributeType "+this.getMemberName()+" avec une valeur de type "+this.getValueType();
		return resultat;
	}
}
