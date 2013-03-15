/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp;

import java.util.HashMap;

import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.pearep.vmap.PeaRepDbType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class MGCPBuiltUpArea extends MGCPFeature implements ISimpleLandUseArea {

  // VMAP attributes
  private String dmr, dms, name, nfi, nfn, hgt;
  private long bac, acc, fun, fuc, ppt, smc, ord;
  private double area;

  public MGCPBuiltUpArea(IPolygon poly) {
    super();
    this.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.setArea(poly.area());

  }

  /**
   * @param type
   */
  public MGCPBuiltUpArea(IPolygon poly, HashMap<String, Object> attributes,
      PeaRepDbType type) {
    this(poly);
    this.setAttributeMap(attributes);//

    this.name = (String) attributes.get("nam");

    this.bac = getLongAttribute("bac");
    this.acc = getLongAttribute("acc");
    this.dmr = (String) attributes.get("dmr");
    this.dms = (String) attributes.get("dms");
    this.fun = getLongAttribute("fun");
    this.fuc = getLongAttribute("fuc");
    this.nfi = (String) attributes.get("nfi");
    this.nfn = (String) attributes.get("nfn");
    this.hgt = (String) attributes.get("hgt");
    this.ppt = getLongAttribute("ppt");
    this.smc = getLongAttribute("smc");
    this.ord = getLongAttribute("ord");
    this.setAttributeMap(null);
  }

  @Override
  public int getType() {
    return MGCPLandUseType.BUILT_UP.ordinal();
  }

  @Override
  public void setType(int type) {
  }

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

  @Override
  public void setGeom(IGeometry geom) {
    super.setGeom(geom);
    this.setArea(geom.area());
  }

  public Long getSmc() {
    return this.smc;
  }

  public void setSmc(Long smc) {
    this.smc = smc;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getBac() {
    return this.bac;
  }

  public void setBac(Long bac) {
    this.bac = bac;
  }

  public String getDmr() {
    return this.dmr;
  }

  public void setDmr(String dmr) {
    this.dmr = dmr;
  }

  public String getDms() {
    return this.dms;
  }

  public void setDms(String dms) {
    this.dms = dms;
  }

  public Long getFuc() {
    return this.fuc;
  }

  public void setFuc(Long fuc) {
    this.fuc = fuc;
  }

  public Long getPpt() {
    return this.ppt;
  }

  public void setPpt(Long ppt) {
    this.ppt = ppt;
  }

  public String getNfi() {
    return this.nfi;
  }

  public void setNfi(String nfi) {
    this.nfi = nfi;
  }

  public String getNfn() {
    return this.nfn;
  }

  public void setNfn(String nfn) {
    this.nfn = nfn;
  }

  public double getArea() {
    return this.area;
  }

  public void setArea(double area) {
    this.area = area;
  }

  public String getHgt() {
    return this.hgt;
  }

  public void setHgt(String hgt) {
    this.hgt = hgt;
  }

  public Long getAcc() {
    return this.acc;
  }

  public void setAcc(Long acc) {
    this.acc = acc;
  }

  public Long getFun() {
    return this.fun;
  }

  public void setFun(Long fun) {
    this.fun = fun;
  }

  public Long getOrd() {
    return this.ord;
  }

  public void setOrd(Long ord) {
    this.ord = ord;
  }

}
