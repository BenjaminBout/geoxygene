/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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

package fr.ign.cogit.geoxygene.util.viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.JFileChooser;

/**
  * This class defines actions for open file (menu bar and toolbar).
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

class GeOxygeneViewerOpenFileAction implements ActionListener {
		
	private ObjectViewerInterface objectViewerInterface;
	
	public GeOxygeneViewerOpenFileAction(ObjectViewerInterface objViewerInterface) {
		this.objectViewerInterface = objViewerInterface;
	}
	
	public void actionPerformed(ActionEvent e) {
		final JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new ShapefileFileFilter());
		fc.addChoosableFileFilter(new GIFImageFileFilter());
		fc.addChoosableFileFilter(new JPEGImageFileFilter());

		int returnVal = fc.showOpenDialog(objectViewerInterface);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String fileExtension=Utils.getExtension(file);
			String filename = file.getAbsolutePath();
			System.out.println("\nLoading " + filename + " ...");
			try {
				if (fileExtension.equals("shp")) {
					int dotIndex = filename.lastIndexOf(".");
					filename = filename.substring(0, dotIndex);
					objectViewerInterface.addAShapefileTheme( new URL("file:///" + filename));
				} else if (fileExtension.equals("jpg") || 
							fileExtension.equals("jpeg") ||
							fileExtension.equals("gif")) {
					objectViewerInterface.addAnImageTheme (filename, 0,0,1,1);
				}
			} catch (Exception ex) {
			}
		}
	}
}


