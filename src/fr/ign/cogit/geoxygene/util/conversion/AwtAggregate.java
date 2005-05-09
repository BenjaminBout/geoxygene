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

package fr.ign.cogit.geoxygene.util.conversion;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AwtAggregate implements AwtShape
{
    private List awtShapeList;
    private GeneralPath path;
    
    public AwtAggregate()
    {
        this.awtShapeList=new ArrayList();
        this.path=new GeneralPath();
    }

    public GeneralPath getPath() {return path;}
        
    public void add(AwtShape shape)
    {
        awtShapeList.add(shape);
        path.append(shape.getBounds(), false);
    }
    
    public void draw(Graphics2D g)
    {
        Iterator i=awtShapeList.iterator();
        while (i.hasNext()) {
            AwtShape shape=(AwtShape)i.next();
            shape.draw(g);
        }
    }
    
    public Rectangle2D getBounds()
    {
        return path.getBounds();
    }
}
