package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu;

import java.util.List;

import fr.ign.cogit.geoxygene.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.feature.type.GF_AssociationType;
import fr.ign.cogit.geoxygene.feature.type.GF_Constraint;
import fr.ign.cogit.geoxygene.feature.type.GF_FeatureType;




/**
 * @author Sandrine Balley
 *
 */
public class AssociationRole implements GF_AssociationRole {

	/**
	 * 
	 */
	public AssociationRole() {super();}

	public AssociationRole(FeatureType ft, AssociationType fa){
		this.featureType=ft;
		this.associationType=fa;
	}

	public AssociationRole(fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationRole fc){
		this.id = fc.getId();
		this.cardMax = fc.getCardMax();
		this.cardMin = fc.getCardMin();
		this.definition = fc.getDefinition();
		this.memberName = fc.getMemberName();
		this.nomFieldAsso = fc.getNomFieldAsso();
		this.nomFieldClasse = fc.getNomFieldClasse();
		this.isComponent = fc.getIsComponent();
		this.isComposite = fc.getIsComposite();
	}

	/**Identifiant d'un objet*/
	protected int id;
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}

	/**nom du role */
	protected String memberName;
	public String getMemberName() {return this.memberName;}
	public void setMemberName(String memberName) {this.memberName = memberName;}

	/**definition du role*/
	protected String definition;
	public String getDefinition() {return this.definition;}
	public void setDefinition(String Definition) {this.definition = Definition;}

	/**lien vers le featureType concern�*/
	protected FeatureType featureType;
	public GF_FeatureType getFeatureType() {return this.featureType;}
	public void setFeatureType(GF_FeatureType featureType) {
		this.featureType = (FeatureType)featureType;
		if (this.featureType!=null){
			if(!this.featureType.getRoles().contains(this)){
				this.featureType.addRole(this);
			}
		}
	}

	/**lien vers le featureAssociation concern�*/
	protected AssociationType associationType;
	public GF_AssociationType getAssociationType(){return this.associationType;}
	public void setAssociationType(GF_AssociationType associationType) {this.associationType = (AssociationType)associationType;}

	protected String cardMin;
	public String getCardMin() {return this.cardMin;}
	public void setCardMin(String CardMin) {this.cardMin = CardMin;}

	protected String cardMax;
	public String getCardMax() {return this.cardMax;}
	public void setCardMax(String CardMax) {this.cardMax = CardMax;}


	/**boolean egal a 1 si le role a valeur de role "composant" dans une agregation*/
	protected boolean isComponent;
	public boolean getIsComponent(){return this.isComponent;}
	public void setIsComponent(boolean value){this.isComponent = value;}

	/**boolean egal a 1 si le role a valeur de role "composite" dans une agregation*/
	protected boolean isComposite;
	public boolean getIsComposite(){return this.isComposite;}
	public void setIsComposite(boolean value){this.isComposite = value;}

	/**nom du champ Java representant le role dans la classe Java correspondante*/
	protected String nomFieldClasse;
	public String getNomFieldClasse(){return nomFieldClasse;}
	public void setNomFieldClasse(String value){this.nomFieldClasse = value;}

	/**nom du champ Java representant le role dans l'eventuelle classe-association Java correspondante */
	protected String nomFieldAsso;
	public String getNomFieldAsso(){return nomFieldAsso;}
	public void setNomFieldAsso(String value){this.nomFieldAsso = value;}



	/**
	 * Non standard
	 * Utils aux applications de transformation de sch�ma
	 * caract�re implicite ou explicite de l'�l�ment : cf AssociationType.isExplicite
	 **/
	protected boolean isExplicite;
	/** Renvoie le caractere explicite ou implicite */
	public boolean getIsExplicite(){return this.isExplicite;}
	/** Affecte le caract�re implicite ou explicite */
	public void setIsExplicite(boolean value){this.isExplicite = value;}



	/**
	 * commodit� pour retrouver � quel sch�ma appartient ce role
	 * (en passant par le featureType auquel il se rapporte)
	 * @return SchemaConceptuelJeu
	 */
	public SchemaConceptuelJeu getSchemaConceptuel() {
		if (this.getFeatureType()!=null){
			return ((FeatureType)this.getFeatureType()).getSchema();
		}
		else return null;
	}



	/***methodes heritees de l'interface pas encore pr�cis�es***/


	public List<GF_Constraint> getConstraint() {
		return null;
	}

	public void setConstraint(List<GF_Constraint> L) {
	}

	public void addConstraint(GF_Constraint value) {
	}

	public int sizeConstraint() {
		return 0;
	}















}
