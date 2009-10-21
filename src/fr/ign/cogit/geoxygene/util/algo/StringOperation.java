/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.util.algo;
import java.util.Vector;

/**
 *
 * transformations de chaines de caract�res
 */
public class StringOperation {

	/** Index du 1er caractere accentu� **/
	private static final int MIN = 192;
	/** Index du dernier caractere accentu� **/
	private static final int MAX = 255;
	/** Vecteur de correspondance entre accent / sans accent **/
	private static final Vector<String> map = initMap();

	/** Initialisation du tableau de correspondance entre les caract�res accentu�s
	 * et leur homologues non accentu�s
	 */
	private static Vector<String> initMap()
	{  Vector<String> Result         = new Vector<String>();
	java.lang.String car  = null;

	car = new java.lang.String("A"); //$NON-NLS-1$
	Result.add( car );            /* '\u00C0'   �   alt-0192  */
	Result.add( car );            /* '\u00C1'   �   alt-0193  */
	Result.add( car );            /* '\u00C2'   �   alt-0194  */
	Result.add( car );            /* '\u00C3'   �   alt-0195  */
	Result.add( car );            /* '\u00C4'   �   alt-0196  */
	Result.add( car );            /* '\u00C5'   �   alt-0197  */
	car = new java.lang.String("AE"); //$NON-NLS-1$
	Result.add( car );            /* '\u00C6'   �   alt-0198  */
	car = new java.lang.String("C"); //$NON-NLS-1$
	Result.add( car );            /* '\u00C7'   �   alt-0199  */
	car = new java.lang.String("E"); //$NON-NLS-1$
	Result.add( car );            /* '\u00C8'   �   alt-0200  */
	Result.add( car );            /* '\u00C9'   �   alt-0201  */
	Result.add( car );            /* '\u00CA'   �   alt-0202  */
	Result.add( car );            /* '\u00CB'   �   alt-0203  */
	car = new java.lang.String("I"); //$NON-NLS-1$
	Result.add( car );            /* '\u00CC'   �   alt-0204  */
	Result.add( car );            /* '\u00CD'   �   alt-0205  */
	Result.add( car );            /* '\u00CE'   �   alt-0206  */
	Result.add( car );            /* '\u00CF'   �   alt-0207  */
	car = new java.lang.String("D"); //$NON-NLS-1$
	Result.add( car );            /* '\u00D0'   �   alt-0208  */
	car = new java.lang.String("N"); //$NON-NLS-1$
	Result.add( car );            /* '\u00D1'   �   alt-0209  */
	car = new java.lang.String("O"); //$NON-NLS-1$
	Result.add( car );            /* '\u00D2'   �   alt-0210  */
	Result.add( car );            /* '\u00D3'   �   alt-0211  */
	Result.add( car );            /* '\u00D4'   �   alt-0212  */
	Result.add( car );            /* '\u00D5'   �   alt-0213  */
	Result.add( car );            /* '\u00D6'   �   alt-0214  */
	car = new java.lang.String("*"); //$NON-NLS-1$
	Result.add( car );            /* '\u00D7'   �   alt-0215  */
	car = new java.lang.String("0"); //$NON-NLS-1$
	Result.add( car );            /* '\u00D8'   �   alt-0216  */
	car = new java.lang.String("U"); //$NON-NLS-1$
	Result.add( car );            /* '\u00D9'   �   alt-0217  */
	Result.add( car );            /* '\u00DA'   �   alt-0218  */
	Result.add( car );            /* '\u00DB'   �   alt-0219  */
	Result.add( car );            /* '\u00DC'   �   alt-0220  */
	car = new java.lang.String("Y"); //$NON-NLS-1$
	Result.add( car );            /* '\u00DD'   �   alt-0221  */
	car = new java.lang.String("�"); //$NON-NLS-1$
	Result.add( car );            /* '\u00DE'   �   alt-0222  */
	car = new java.lang.String("B"); //$NON-NLS-1$
	Result.add( car );            /* '\u00DF'   �   alt-0223  */
	car = new java.lang.String("a"); //$NON-NLS-1$
	Result.add( car );            /* '\u00E0'   �   alt-0224  */
	Result.add( car );            /* '\u00E1'   �   alt-0225  */
	Result.add( car );            /* '\u00E2'   �   alt-0226  */
	Result.add( car );            /* '\u00E3'   �   alt-0227  */
	Result.add( car );            /* '\u00E4'   �   alt-0228  */
	Result.add( car );            /* '\u00E5'   �   alt-0229  */
	car = new java.lang.String("ae"); //$NON-NLS-1$
	Result.add( car );            /* '\u00E6'   �   alt-0230  */
	car = new java.lang.String("c"); //$NON-NLS-1$
	Result.add( car );            /* '\u00E7'   �   alt-0231  */
	car = new java.lang.String("e"); //$NON-NLS-1$
	Result.add( car );            /* '\u00E8'   �   alt-0232  */
	Result.add( car );            /* '\u00E9'   �   alt-0233  */
	Result.add( car );            /* '\u00EA'   �   alt-0234  */
	Result.add( car );            /* '\u00EB'   �   alt-0235  */
	car = new java.lang.String("i"); //$NON-NLS-1$
	Result.add( car );            /* '\u00EC'   �   alt-0236  */
	Result.add( car );            /* '\u00ED'   �   alt-0237  */
	Result.add( car );            /* '\u00EE'   �   alt-0238  */
	Result.add( car );            /* '\u00EF'   �   alt-0239  */
	car = new java.lang.String("d"); //$NON-NLS-1$
	Result.add( car );            /* '\u00F0'   �   alt-0240  */
	car = new java.lang.String("n"); //$NON-NLS-1$
	Result.add( car );            /* '\u00F1'   �   alt-0241  */
	car = new java.lang.String("o"); //$NON-NLS-1$
	Result.add( car );            /* '\u00F2'   �   alt-0242  */
	Result.add( car );            /* '\u00F3'   �   alt-0243  */
	Result.add( car );            /* '\u00F4'   �   alt-0244  */
	Result.add( car );            /* '\u00F5'   �   alt-0245  */
	Result.add( car );            /* '\u00F6'   �   alt-0246  */
	car = new java.lang.String("/"); //$NON-NLS-1$
	Result.add( car );            /* '\u00F7'   �   alt-0247  */
	car = new java.lang.String("0"); //$NON-NLS-1$
	Result.add( car );            /* '\u00F8'   �   alt-0248  */
	car = new java.lang.String("u"); //$NON-NLS-1$
	Result.add( car );            /* '\u00F9'   �   alt-0249  */
	Result.add( car );            /* '\u00FA'   �   alt-0250  */
	Result.add( car );            /* '\u00FB'   �   alt-0251  */
	Result.add( car );            /* '\u00FC'   �   alt-0252  */
	car = new java.lang.String("y"); //$NON-NLS-1$
	Result.add( car );            /* '\u00FD'   �   alt-0253  */
	car = new java.lang.String("�"); //$NON-NLS-1$
	Result.add( car );            /* '\u00FE'   �   alt-0254  */
	car = new java.lang.String("y"); //$NON-NLS-1$
	Result.add( car );            /* '\u00FF'   �   alt-0255  */
	Result.add( car );            /* '\u00FF'       alt-0255  */

	return Result;
	}

	/** Transforme une chaine pouvant contenir des accents dans une version sans accent
	 *  @param chaine Chaine a convertir sans accent
	 *  @return Chaine dont les accents ont �t� supprim�
	 **/
	public static java.lang.String sansAccent(java.lang.String chaine)
	{  java.lang.StringBuffer Result  = new StringBuffer(chaine);

	for(int bcl = 0 ; bcl < Result.length() ; bcl++)
	{   int carVal = chaine.charAt(bcl);
	if( carVal >= MIN && carVal <= MAX )
	{  // Remplacement
		java.lang.String newVal = map.get( carVal - MIN );
		Result.replace(bcl, bcl+1,newVal);
	}
	}
	return Result.toString();
	}
}
