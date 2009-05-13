package fr.ign.cogit.geoxygene.schema.util.persistance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AssociationRole;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AssociationType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureAttributeValue;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.InheritanceRelation;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.SchemaConceptuelJeu;

/**
 * 
 * @author nfabadie
 *
 *Gere la persistence des elements de schema conceptuel
 */

public abstract class SchemaPersistentOJB {



	/**M�thode qui supprime d�finitivement un objet de la base*/
	public void deletePersistent () {
		//		Initialisation d'une connexion avec le SGBD et le mapping
		Geodatabase bd = DataSet.db;
		//		Si ma connexion est d�finie et ma base ouverte...
		if (bd != null && bd.isOpen()){
			//			Suppression de l'objet persistent
			bd.deletePersistent(this);}
		else //L'objet ne pourra �tre effac�: envoi d'un message d'erreur
		{System.err.println
			("Attention : effacement d'un objet persistant alors qu'aucune "
					+ "transaction n'est en cours. L'objet n'a pas �t� effac� de la base.");}
	}//Fin de la m�thode deletePersistent


	/**M�thode qui rend un objet persistent*/
	public void makePersistent(){
		//		Initialisation d'une connexion avec le SGBD et le mapping
		Geodatabase bd = DataSet.db;
		//		Sauvegarde de l'objet dans la base
		if (bd!= null && bd.isOpen()){
			bd.makePersistent(this);}
		else {System.out.println("Probl�mes de connexion � la BD!");}
	}//Fin de la m�thode makePersistent




	/**
	 * M�thode qui efface un SchemaConceptuelJeu de la base Oracle
	 */
	public static void deleteSchema(SchemaConceptuelJeu schema){

		if (DataSet.db != null && DataSet.db.isOpen()){

			//Rassemblement de toutes les informations sur le sch�ma
			List<FeatureType> ftListLocal = new ArrayList<FeatureType>();
			List<GF_FeatureType> ftList;
			List<AttributeType> attList;
			List<FeatureAttributeValue> valList;
			List<AssociationType> assoList;
			List<AssociationRole> roleList;
			List<InheritanceRelation> heritList;

			//Suppression de tous les �l�ments du sch�ma

			attList=schema.getFeatureAttributes();
			Iterator<AttributeType> iTatt = attList.iterator();
			while (iTatt.hasNext()){
				AttributeType att = iTatt.next();
				DataSet.db.deletePersistent(att);
			}

			valList=schema.getFeatureAttributeValues();
			Iterator<FeatureAttributeValue> iTval = valList.iterator();
			while (iTval.hasNext()){
				FeatureAttributeValue val = iTval.next();
				DataSet.db.deletePersistent(val);
			}

			assoList=schema.getFeatureAssociations();
			Iterator<AssociationType> iTasso = assoList.iterator();
			while (iTasso.hasNext()){
				AssociationType asso = iTasso.next();
				DataSet.db.deletePersistent(asso);
			}

			roleList=schema.getAssociationRoles();
			Iterator<AssociationRole> iTrole = roleList.iterator();
			while (iTrole.hasNext()){
				AssociationRole role = iTrole.next();
				DataSet.db.deletePersistent(role);
			}

			heritList=schema.getInheritance();
			Iterator<InheritanceRelation> iTherit = heritList.iterator();
			while (iTherit.hasNext()){
				InheritanceRelation herit = iTherit.next();
				DataSet.db.deletePersistent(herit);
			}

			ftList=schema.getFeatureTypes();
			Iterator<GF_FeatureType> iTft = ftList.iterator();
			while (iTft.hasNext()){
				FeatureType ft = (FeatureType) iTft.next();
				DataSet.db.deletePersistent(ft);
				ftListLocal.add(ft);
			}

			//Suppression des objets en m�moire
			Iterator<FeatureType> iTftloc = ftListLocal.iterator();
			while (iTftloc.hasNext()){
				FeatureType ft = iTftloc.next();
				schema.removeFeatureType(ft);
			}

			//Suppression du sch�ma lui m�me
			DataSet.db.deletePersistent(schema);
		}
		else {
			{System.err.println
				("Attention : effacement d'un objet persistant alors qu'aucune "
						+ "transaction n'est en cours. L'objet n'a pas �t� effac� de la base.");}
		}
	}


	/**
	 * Efface un SchemaConceptuelJeu de la base Oracle
	 */
	public static void deleteSchema(fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.SchemaConceptuelProduit schema){

		//Rassemblement de toutes les informations sur le sch�ma
		List<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType> ftListLocal = new ArrayList<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType>();
		List<GF_FeatureType> ftList;
		List<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType> attList;
		List<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureAttributeValue> valList;
		List<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationType> assoList;
		List<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationRole> roleList;
		List<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.InheritanceRelation> heritList;

		//Suppression de tous les �l�ments du sch�ma

		attList=schema.getFeatureAttributes();
		Iterator<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType> iTatt = attList.iterator();
		while (iTatt.hasNext()){
			fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType att = iTatt.next();
			DataSet.db.deletePersistent(att);
		}

		valList=schema.getFeatureAttributeValues();
		Iterator<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureAttributeValue> iTval = valList.iterator();
		while (iTval.hasNext()){
			fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureAttributeValue val = iTval.next();
			DataSet.db.deletePersistent(val);
		}

		assoList=schema.getFeatureAssociations();
		Iterator<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationType> iTasso = assoList.iterator();
		while (iTasso.hasNext()){
			fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationType asso = iTasso.next();
			DataSet.db.deletePersistent(asso);
		}

		roleList=schema.getAssociationRoles();
		Iterator<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationRole> iTrole = roleList.iterator();
		while (iTrole.hasNext()){
			fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationRole role = iTrole.next();
			DataSet.db.deletePersistent(role);
		}

		heritList=schema.getInheritance();
		Iterator<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.InheritanceRelation> iTherit = heritList.iterator();
		while (iTherit.hasNext()){
			fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.InheritanceRelation herit = iTherit.next();
			DataSet.db.deletePersistent(herit);
		}

		ftList=schema.getFeatureTypes();
		Iterator<GF_FeatureType> iTft = ftList.iterator();
		while (iTft.hasNext()){
			fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType ft = (fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType) iTft.next();
			DataSet.db.deletePersistent(ft);
			ftListLocal.add(ft);
		}

		//Suppression des objets en m�moire
		Iterator<fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType> iTftloc = ftListLocal.iterator();
		while (iTftloc.hasNext()){
			fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType ft = iTftloc.next();
			schema.removeFeatureType(ft);
		}

		//Suppression du sch�ma lui m�me
		DataSet.db.deletePersistent(schema);
	}


}
