/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut G�ographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut G�ographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.LienReseaux;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;

/**
 * Face d'un graphe � apparier.
 * La notion de face est tr�s peu utilis�e pour l'appariement de r�seaux,
 * mais elle l'est n�anmoins pour des cas particuliers, comme la gestion des rond-points
 * dans le r�seau routier.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class FaceApp extends Face {

	/** Evaluation du r�sultat de l'appariement sur la face. */
	private String resultatAppariement;
	public String getResultatAppariement () {return resultatAppariement;}
	public void setResultatAppariement (String resultat) {resultatAppariement = resultat;}

	/** Liens qui r�f�rencent l'objet appari� */
	private List<LienReseaux> liens = new ArrayList<LienReseaux>();
	public List<LienReseaux> getLiens() {return liens;}
	public void setLiens(List<LienReseaux> liens) { this.liens=liens; }
	public void addLiens(LienReseaux liensReseaux) { this.liens.add(liensReseaux); }

	/** Renvoie les liens de l'objet qui appartiennent � la liste liensPertinents */
	public List<LienReseaux> getLiens(List<LienReseaux> liensPertinents) {
		List<LienReseaux> listeTmp = new ArrayList<LienReseaux>(this.getLiens());
		listeTmp.retainAll(liensPertinents);
		return listeTmp;
	}

}