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

package fr.ign.cogit.geoxygene.util.graphcut;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import fr.ign.cogit.geoxygene.api.texture.Tile;
import fr.ign.cogit.geoxygene.util.ImageUtil;

/**
 * @author JeT
 *         a Tile is a small image used to fill textures with
 *         it contains a mask extracted from the transparency 0: pixel 255:no
 *         pixel
 */
public class DefaultTile implements Tile {

    private BufferedImage image = null;
    private List<Point> borders = null;
    private int size = 0; // number of lighten mask pixels
    private BufferedImage mask = null;
    private BufferedImage border = null;
    private final int transparencyThreshold = 127;

    /**
     * Constructor
     */
    public DefaultTile() {
    }

    /**
     * Constructor
     */
    public DefaultTile(BufferedImage image) {
        this.setImage(image);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.util.graphcut.Tile#setImage(java.awt.image.BufferedImage)
     */
    @Override
    public void setImage(BufferedImage image) {
        this.image = ImageUtil.convert(image, BufferedImage.TYPE_4BYTE_ABGR);
        this.computeMaskAndBorders();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.util.graphcut.Tile#getImage()
     */
    @Override
    public BufferedImage getImage() {
        return this.image;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.util.graphcut.Tile#getWidth()
     */
    @Override
    public int getWidth() {
        return this.image == null ? 0 : this.image.getWidth();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.util.graphcut.Tile#getHeight()
     */
    @Override
    public int getHeight() {
        return this.image == null ? 0 : this.image.getHeight();
    }

    /**
     * read an image as tile
     * 
     * @param filename
     *            file to read
     * @return a newly created tile
     * @throws IOException
     *             on IO Error
     */
    public static Tile read(String filename) throws IOException {
        File f = new File(filename);
        return read(f);
    }

    /**
     * read an image as tile
     * 
     * @param filename
     *            file to read
     * @return a newly created tile
     * @throws IOException
     *             on IO Error
     */
    public static Tile read(File f) throws IOException {
        DefaultTile tile = new DefaultTile();
        tile.setImage(ImageIO.read(f));
        return tile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.util.graphcut.Tile#getBorders()
     */
    @Override
    public List<Point> getBorders() {
        return this.borders;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.util.graphcut.Tile#getMask()
     */
    @Override
    public BufferedImage getMask() {
        return this.mask;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.util.graphcut.Tile#getBorder()
     */
    @Override
    public BufferedImage getBorder() {
        return this.border;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.util.graphcut.Tile#getSize()
     */
    @Override
    public int getSize() {
        return this.size;
    }

    /**
     * create the mask and border of the tile.
     * the tile image transparency is set to binary values (0 || 255)
     * image alpha = 255 (opaque) when mask = 0
     * image alpha = 0 (transparent) when mask = 255
     */
    private void computeMaskAndBorders() {
        if (this.image == null) {
            return;
        }
        int w = this.image.getWidth();
        int h = this.image.getHeight();
        this.borders = new ArrayList<Point>();
        this.size = 0;
        this.mask = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        this.border = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        byte[] imagePixels = ((DataBufferByte) this.image.getRaster().getDataBuffer()).getData();
        byte[] maskPixels = ((DataBufferByte) this.mask.getRaster().getDataBuffer()).getData();
        byte[] borderPixels = ((DataBufferByte) this.border.getRaster().getDataBuffer()).getData();

        for (int y = 0, lMask = 0; y < h; y++) {
            for (int x = 0; x < w; x++, lMask++) {
                int lImage = lMask * 4;
                boolean in = this.imagePixelIsInMask(imagePixels, lImage);

                // mask
                if (in) {
                    this.size++;
                    maskPixels[lMask] = MASK_IN; // mask black : visible pixel
                    imagePixels[lImage] = (byte) 255; // alpha = 255 : pure opaque pixel
                    // border
                    //                    if (x == 0) {
                    if (x == 0 || y == 0 || x == w - 1 || y == h - 1 || !this.imagePixelIsInMask(imagePixels, lImage - 4)
                            || !this.imagePixelIsInMask(imagePixels, lImage + 4) || !this.imagePixelIsInMask(imagePixels, lImage - 4 * w)
                            || !this.imagePixelIsInMask(imagePixels, lImage + 4 * w)) {
                        this.borders.add(new Point(x, y));
                        borderPixels[lMask] = MASK_IN;

                    } else {
                        borderPixels[lMask] = MASK_OUT;
                    }
                } else {
                    borderPixels[lMask] = MASK_OUT;
                    maskPixels[lMask] = MASK_OUT; // mask white : invisible pixel
                    imagePixels[lImage] = (byte) 0; // alpha = 0 : pure transparent pixel 
                }
            }
        }
    }

    /**
     * @param imagePixels
     * @param lImage
     * @return
     */
    private boolean imagePixelIsInMask(byte[] imagePixels, int lImage) {
        return (imagePixels[lImage] & 0xFF) >= this.transparencyThreshold;
    }
}