/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.render.texture;

import java.awt.image.BufferedImage;

import com.jhlabs.image.LinearColormap;
import com.jhlabs.image.TextureFilter;

import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.texture.PerlinNoiseTexture;
import fr.ign.cogit.geoxygene.util.ImageUtil;

/**
 * @author JeT
 * 
 */
public class PerlinNoiseTextureTask extends
    AbstractTextureTask<PerlinNoiseTexture> {

  /**
   * @param texture
   */
  public PerlinNoiseTextureTask(PerlinNoiseTexture texture) {
    super(texture);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.task.Task#isProgressable()
   */
  @Override
  public boolean isProgressable() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.task.Task#isPausable()
   */
  @Override
  public boolean isPausable() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.task.Task#isStoppable()
   */
  @Override
  public boolean isStoppable() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    this.setState(TaskState.INITIALIZING);
    this.setState(TaskState.RUNNING);
    try {
      TextureFilter filter = new TextureFilter();
      filter.setColormap(new LinearColormap(this.getTexture().getColor1()
          .getRGB(), this.getTexture().getColor2().getRGB()));
      filter.setScale(this.getTexture().getScale());
      filter.setStretch(this.getTexture().getStretch());
      filter.setAmount(this.getTexture().getAmount());
      filter.setAngle(this.getTexture().getAngle());
      BufferedImage imgTexture = ImageUtil
          .createBufferedImage(this.getTexture().getTextureWidth(), this
              .getTexture().getTextureHeight());
      filter.filter(imgTexture, imgTexture);
      this.getTexture().setTextureImage(imgTexture);
      this.setState(TaskState.FINISHED);
    } catch (Exception e) {
      e.printStackTrace();
      this.setState(TaskState.ERROR);
    }
  }

}