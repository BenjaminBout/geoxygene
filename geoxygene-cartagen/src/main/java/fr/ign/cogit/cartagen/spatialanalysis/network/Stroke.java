/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadStrokesNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class Stroke extends AbstractFeature {
  private static Logger logger = Logger.getLogger(Stroke.class.getName());

  private StrokesNetwork network;
  private ArrayList<ArcReseau> features;
  private ILineString geomStroke;
  private int id;
  private ArcReseau root;

  public StrokesNetwork getNetwork() {
    return this.network;
  }

  public void setNetwork(StrokesNetwork network) {
    this.network = network;
  }

  public ArrayList<ArcReseau> getFeatures() {
    return this.features;
  }

  public void setFeatures(ArrayList<ArcReseau> features) {
    this.features = features;
  }

  public ILineString getGeomStroke() {
    return this.geomStroke;
  }

  public void setGeomStroke(ILineString geomStroke) {
    this.geomStroke = geomStroke;
    this.setGeom(geomStroke);
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public void setRoot(ArcReseau root) {
    this.root = root;
  }

  public ArcReseau getRoot() {
    return this.root;
  }

  @Override
  public boolean equals(Object arg0) {
    Stroke other = (Stroke) arg0;
    if (!other.network.equals(this.network)) {
      return false;
    }
    if (!other.features.equals(this.features)) {
      return false;
    }
    return super.equals(arg0);
  }

  @Override
  public int hashCode() {
    return this.id;
  }

  @Override
  public String toString() {
    String text = "Stroke " + this.getId() + " composed of "
        + this.features.size() + " sections";
    return text;
  }

  public Stroke(StrokesNetwork network, ArcReseau root) {
    this.network = network;
    this.setRoot(root);
    this.features = new ArrayList<ArcReseau>();
    this.features.add(root);
    this.id = root.getId();
  }

  public Stroke(RoadStrokesNetwork network, ArrayList<ArcReseau> features,
      ILineString geomStroke) {
    this.features = features;
    this.geomStroke = geomStroke;
    this.geom = geomStroke;
    this.network = network;
    this.setRoot(features.get(0));
    this.id = this.getRoot().getId();
  }

  /**
   * <p>
   * From a network segment passed in the constructor, a stroke is built on one
   * side. This method has to be called for each side for the stroke to be
   * completely built.
   * 
   * @param side : the chosen side (initial or final) true is initial and false
   *          is final
   * @param attributeNames : the names of the attribute used for attribute
   *          continuity.
   * @param angle : the limit deviation angle for continuity
   * @param somme : the limit difference of deviation angles sum for consecutive
   *          points.
   * 
   */
  public void buildOneSide(boolean side, HashSet<String> attributeNames,
      double deviatAngle, double deviatSum, boolean noStop) {

    // get the following network segments of the root of this stroke
    NoeudReseau node = null;
    if (side) {
      node = this.getRoot().getNoeudInitial();
    } else {
      node = this.getRoot().getNoeudFinal();
    }
    HashSet<ArcReseau> followers = Stroke.getFollowing(this.getRoot(), node);

    // loop while there is a segment with a good continuity
    ArcReseau next = this.getRoot();
    boolean continuity = true;
    while (continuity) {
      // get the best candidate among the followers (the one with best
      // continuity)
      ArcReseau best = null;
      if (noStop) {
        best = this.chooseNextSegmentNoStop(next, followers, attributeNames,
            deviatAngle, deviatSum);
      } else {
        best = this.chooseNextSegment(next, followers, attributeNames,
            deviatAngle, deviatSum);
      }

      // if best is null, break
      if (best == null) {
        break;
      }

      // add this to the 2 sets (the network one and the stroke one)
      if (side) {
        this.features.add(0, best);
      } else {
        this.features.add(best);
      }
      this.network.getGroupedFeatures().add(best);

      // get the followers of 'best'
      followers.clear();
      NoeudReseau nextNode = best.getNoeudInitial();
      if (node.equals(nextNode))
        nextNode = best.getNoeudFinal();
      followers = Stroke.getFollowing(best, nextNode);

      // if there is no follower, break
      if (followers.size() == 0) {
        break;
      }
      // update the 'next' segment with 'best'
      next = best;
      node = nextNode;
    }// while(continuity)
  }

  protected ArcReseau chooseNextSegment(ArcReseau arc,
      HashSet<ArcReseau> followers, HashSet<String> attributeNames,
      double deviatAngle, double deviatSum) {
    // first, filter the followers
    this.filterFollowers(arc, followers);
    if (followers.size() == 0) {
      return null;
    }
    // then, filter the followers from the attributes
    this.filterByAttributeContinuity(arc, followers, attributeNames);
    if (followers.size() == 0) {
      return null;
    }

    boolean continuity = true;
    ArcReseau bestSegment = null;
    // if there is only one follower left, check its continuity
    if (followers.size() == 1) {
      // get the initial geometry
      ICurve tempGeom = arc.getGeom();

      // get the follower and its geometry
      ArcReseau follower = followers.iterator().next();
      ICurve geomFoll = follower.getGeom();

      // test the good continuity between the two geometry
      if (this.isGoodContinuity(tempGeom, geomFoll, deviatAngle, deviatSum)) {
        bestSegment = follower;
        continuity = true;
      } else {
        continuity = false;
      }
    } else {
      // get the initial geometry
      ICurve tempGeom = arc.getGeom();
      // Loop on the followers to choose the best continuity
      double minDiff = Math.PI;
      for (ArcReseau follower : followers) {
        // get the follower geometry
        ICurve geomFoll = follower.getGeom();
        // get the continuity difference with this follower
        double diffContinuity = this.goodContinuityDifference(tempGeom,
            geomFoll, deviatAngle, deviatSum);
        if (diffContinuity > -1.0) {
          // il y a un tronçon de bonne continuité
          continuity = true;
          if (diffContinuity < minDiff) {
            // this is the current best continuity
            // update the difference
            minDiff = diffContinuity;
            // change the bestSegment
            bestSegment = follower;
          }// if (diffContinuity < minDiff)
        }// if (diffContinuity > -1.0)
      }// loop on the followers
    }

    if ((continuity) && !this.features.contains(bestSegment)) {
      return bestSegment;
    }
    return null;
  }

  protected double goodContinuityDifference(ICurve tempGeom, ICurve geomFoll,
      double deviatAngle, double deviatSum) {
    // first convert the parameters into radians
    double angleThresh = deviatAngle / 180.0 * Math.PI;
    double sumThresh = deviatAngle / 180.0 * Math.PI;
    // search for the intersection point between the two geometries
    DirectPosition coordIni1 = (DirectPosition) tempGeom.startPoint();
    DirectPosition coordFin1 = (DirectPosition) tempGeom.endPoint();
    DirectPosition coordIni2 = (DirectPosition) geomFoll.startPoint();
    DirectPosition coordFin2 = (DirectPosition) geomFoll.endPoint();
    boolean interGeom1 = true, interGeom2 = true;

    DirectPosition coordInter = null;
    if (coordIni2.equals(coordIni1)) {
      coordInter = coordIni1;
      interGeom1 = true;
      interGeom2 = true;
    }
    if (coordFin2.equals(coordIni1)) {
      coordInter = coordIni1;
      interGeom1 = true;
      interGeom2 = false;
    }
    if (coordFin2.equals(coordFin1)) {
      coordInter = coordFin1;
      interGeom1 = false;
      interGeom2 = false;
    }
    if (coordIni2.equals(coordFin1)) {
      coordInter = coordIni2;
      interGeom1 = false;
      interGeom2 = true;
    }

    // if there is a topological problem, return false
    if (coordInter == null) {
      return -1.0;
    }

    // count vertices in each geometry : indeed, if one or the other has only 2
    // vertices, the angle continuity cannot be tested.
    int nbVert1 = tempGeom.numPoints();
    int nbVert2 = geomFoll.numPoints();

    // on définit les marks à utiliser sur les géométries
    DirectPosition v1g1, v2g1 = null, v1g2, v2g2 = null;

    // get the first vertex on geometry 1
    if (interGeom1) {
      v1g1 = (DirectPosition) tempGeom.coord().get(1);
    } else {
      v1g1 = (DirectPosition) tempGeom.coord().get(nbVert1 - 2);
    }

    // get the first vertex on geometry 2
    if (interGeom2) {
      v1g2 = (DirectPosition) geomFoll.coord().get(1);
    } else {
      v1g2 = (DirectPosition) geomFoll.coord().get(nbVert2 - 2);
    }

    // if nbVert1 > 2, get the second vertex in geometry 1
    if (nbVert1 > 2) {
      if (interGeom1) {
        v2g1 = (DirectPosition) tempGeom.coord().get(2);
      } else {
        v2g1 = (DirectPosition) tempGeom.coord().get(nbVert1 - 3);
      }
    }

    // si nbVert2 > 2, get the second vertex in geometry 2
    if (nbVert2 > 2) {
      if (interGeom2) {
        v2g2 = (DirectPosition) geomFoll.coord().get(2);
      } else {
        v2g2 = (DirectPosition) geomFoll.coord().get(nbVert2 - 3);
      }
    }

    // now, compute interAngle between geom and geomFoll
    double interAngle = Angle.angleTroisPoints(v1g1, coordInter, v1g2)
        .getValeur();
    // put the angle between -Pi and Pi
    if (interAngle > Math.PI)
      interAngle = interAngle - 2 * Math.PI;

    // case where both geometries have only 2 vertices
    if ((v2g1 == null) && (v2g2 == null)) {
      // then, there is good continuity if the angle is < 45°
      if ((interAngle < (-angleThresh)) || (interAngle > angleThresh)) {
        return Math.PI - Math.abs(interAngle);
      }
      return -1.0;
    }

    // case where geom has 2 vertices
    else if (v2g1 == null) {
      double angleTotalDiff = 0.0;
      // on calcule angleGeom2
      double angleGeom2 = Angle.angleTroisPoints(coordInter, v1g2, v2g2)
          .getValeur();
      // on calcule l'écart entre les angles
      double angleDiff = Math.max(angleGeom2, interAngle)
          - Math.min(angleGeom2, interAngle);
      if (angleDiff > Math.PI) {
        angleTotalDiff = Math.abs(angleDiff - 2 * Math.PI);
      } else {
        angleTotalDiff = angleDiff;
      }
      // il y a bonne continuité si l'angle est < 45° et la différence des
      // angles < à 30°
      if (((interAngle < (-angleThresh)) || (interAngle > angleThresh))
          && (angleTotalDiff < sumThresh)) {
        return 2 * angleTotalDiff;
      }
      return -1.0;
    }

    // case where geomFoll has 2 vertices
    else if (v2g2 == null) {
      double angleTotalDiff = 0.0;
      // on calcule angleGeom2
      double angleGeom1 = Angle.angleTroisPoints(v2g1, v1g1, coordInter)
          .getValeur();
      // on calcule l'écart entre les angles
      double angleDiff = Math.max(angleGeom1, interAngle)
          - Math.min(angleGeom1, interAngle);
      if (angleDiff > Math.PI) {
        angleTotalDiff = Math.abs(angleDiff - 2 * Math.PI);
      } else {
        angleTotalDiff = angleDiff;
      }
      // il y a bonne continuité si l'angle est < 45° et la différence des
      // angles < à 30°
      if (((interAngle < (-angleThresh)) || (interAngle > angleThresh))
          && (angleTotalDiff < sumThresh)) {
        return 2 * angleTotalDiff;
      }
      return -1.0;
    }
    // general case
    else {
      double angleTotalDiff1 = 0.0;
      double angleTotalDiff2 = 0.0;
      // on calcule angleGeom1
      double angleGeom1 = Angle.angleTroisPoints(v2g1, v1g1, coordInter)
          .getValeur();
      // on calcule angleGeom2
      double angleGeom2 = Angle.angleTroisPoints(coordInter, v1g2, v2g2)
          .getValeur();
      // on calcule l'écart entre les angles 1 et inter
      double angleDiff1 = Math.max(angleGeom1, interAngle)
          - Math.min(angleGeom1, interAngle);
      if (angleDiff1 > Math.PI) {
        angleTotalDiff1 = Math.abs(angleDiff1 - 2 * Math.PI);
      } else {
        angleTotalDiff1 = angleDiff1;
      }
      // on calcule l'écart entre les angles
      double DiffAngles2 = Math.max(angleGeom2, interAngle)
          - Math.min(angleGeom2, interAngle);
      if (DiffAngles2 > Math.PI) {
        angleTotalDiff2 = Math.abs(DiffAngles2 - 2 * Math.PI);
      } else {
        angleTotalDiff2 = DiffAngles2;
      }
      // il y a bonne continuité si l'angle est < 45° et les différences des
      // angles < à 30°
      if (((interAngle < (-angleThresh)) || (interAngle > angleThresh))
          && (angleTotalDiff1 < sumThresh) && (angleTotalDiff2 < sumThresh)) {
        return angleTotalDiff1 + angleTotalDiff2;
      }
      return -1.0;
    }
  }

  private boolean isGoodContinuity(ICurve tempGeom, ICurve geomFoll,
      double deviatAngle, double deviatSum) {
    // first convert the parameters into radians
    double angleThresh = deviatAngle / 180.0 * Math.PI;
    double sumThresh = deviatAngle / 180.0 * Math.PI;
    // search for the intersection point between the two geometries
    DirectPosition coordIni1 = (DirectPosition) tempGeom.startPoint();
    DirectPosition coordFin1 = (DirectPosition) tempGeom.endPoint();
    DirectPosition coordIni2 = (DirectPosition) geomFoll.startPoint();
    DirectPosition coordFin2 = (DirectPosition) geomFoll.endPoint();
    boolean interGeom1 = true, interGeom2 = true;

    DirectPosition coordInter = null;
    if (coordIni2.equals(coordIni1)) {
      coordInter = coordIni1;
      interGeom1 = true;
      interGeom2 = true;
    }
    if (coordFin2.equals(coordIni1)) {
      coordInter = coordIni1;
      interGeom1 = true;
      interGeom2 = false;
    }
    if (coordFin2.equals(coordFin1)) {
      coordInter = coordFin1;
      interGeom1 = false;
      interGeom2 = false;
    }
    if (coordIni2.equals(coordFin1)) {
      coordInter = coordIni2;
      interGeom1 = false;
      interGeom2 = true;
    }

    // if there is a topological problem, return false
    if (coordInter == null) {
      return false;
    }

    // count vertices in each geometry : indeed, if one or the other has only 2
    // vertices, the angle continuity cannot be tested.
    int nbVert1 = tempGeom.numPoints();
    int nbVert2 = geomFoll.numPoints();

    // define the node to compute the angle
    DirectPosition v1g1, v2g1 = null, v1g2, v2g2 = null;

    // get the first vertex on geometry 1
    if (interGeom1) {
      v1g1 = (DirectPosition) tempGeom.coord().get(1);
    } else {
      v1g1 = (DirectPosition) tempGeom.coord().get(nbVert1 - 2);
    }

    // get the first vertex on geometry 2
    if (interGeom2) {
      v1g2 = (DirectPosition) geomFoll.coord().get(1);
    } else {
      v1g2 = (DirectPosition) geomFoll.coord().get(nbVert2 - 2);
    }

    // if nbVert1 > 2, get the second vertex in geometry 1
    if (nbVert1 > 2) {
      if (interGeom1) {
        v2g1 = (DirectPosition) tempGeom.coord().get(2);
      } else {
        v2g1 = (DirectPosition) tempGeom.coord().get(nbVert1 - 3);
      }
    }

    // si nbVert2 > 2, get the second vertex in geometry 2
    if (nbVert2 > 2) {
      if (interGeom2) {
        v2g2 = (DirectPosition) geomFoll.coord().get(2);
      } else {
        v2g2 = (DirectPosition) geomFoll.coord().get(nbVert2 - 3);
      }
    }

    // now, compute interAngle between geom and geomFoll
    double interAngle = Angle.angleTroisPoints(v1g1, coordInter, v1g2)
        .getValeur();
    // put the angle between -Pi and Pi
    if (interAngle > Math.PI)
      interAngle = interAngle - 2 * Math.PI;

    // case where both geometries have only 2 vertices
    if ((v2g1 == null) && (v2g2 == null)) {
      // then, there is good continuity if the angle is < 45°
      if ((interAngle < (-angleThresh)) || (interAngle > angleThresh)) {
        return true;
      }
      return false;
    }

    // case where geom has 2 vertices
    else if (v2g1 == null) {
      double angleTotalDiff = 0.0;
      // on calcule angleGeom2
      double angleGeom2 = Angle.angleTroisPoints(coordInter, v1g2, v2g2)
          .getValeur();
      // on calcule l'écart entre les angles
      double angleDiff = Math.max(angleGeom2, interAngle)
          - Math.min(angleGeom2, interAngle);
      if (angleDiff > Math.PI) {
        angleTotalDiff = Math.abs(angleDiff - 2 * Math.PI);
      } else {
        angleTotalDiff = angleDiff;
      }
      // il y a bonne continuité si l'angle est < 45° et la différence des
      // angles < à 30°
      if (((interAngle < (-angleThresh)) || (interAngle > angleThresh))
          && (angleTotalDiff < sumThresh)) {
        return true;
      }
      return false;
    }

    // case where geomFoll has 2 vertices
    else if (v2g2 == null) {
      double angleTotalDiff = 0.0;
      // on calcule angleGeom2
      double angleGeom1 = Angle.angleTroisPoints(v2g1, v1g1, coordInter)
          .getValeur();
      // on calcule l'écart entre les angles
      double angleDiff = Math.max(angleGeom1, interAngle)
          - Math.min(angleGeom1, interAngle);
      if (angleDiff > Math.PI) {
        angleTotalDiff = Math.abs(angleDiff - 2 * Math.PI);
      } else {
        angleTotalDiff = angleDiff;
      }
      // il y a bonne continuité si l'angle est < 45° et la différence des
      // angles < à 30°
      if (((interAngle < (-angleThresh)) || (interAngle > angleThresh))
          && (angleTotalDiff < sumThresh)) {
        return true;
      }
      return false;
    }

    // general case
    else {
      double angleTotalDiff1 = 0.0;
      double angleTotalDiff2 = 0.0;
      // on calcule angleGeom1
      double angleGeom1 = Angle.angleTroisPoints(v2g1, v1g1, coordInter)
          .getValeur();
      // on calcule angleGeom2
      double angleGeom2 = Angle.angleTroisPoints(coordInter, v1g2, v2g2)
          .getValeur();
      // on calcule l'écart entre les angles 1 et inter
      double angleDiff1 = Math.max(angleGeom1, interAngle)
          - Math.min(angleGeom1, interAngle);
      if (angleDiff1 > Math.PI) {
        angleTotalDiff1 = Math.abs(angleDiff1 - 2 * Math.PI);
      } else {
        angleTotalDiff1 = angleDiff1;
      }
      // on calcule l'écart entre les angles
      double DiffAngles2 = Math.max(angleGeom2, interAngle)
          - Math.min(angleGeom2, interAngle);
      if (DiffAngles2 > Math.PI) {
        angleTotalDiff2 = Math.abs(DiffAngles2 - 2 * Math.PI);
      } else {
        angleTotalDiff2 = DiffAngles2;
      }
      // il y a bonne continuité si l'angle est < 45° et les différences des
      // angles < à 30°
      if (((interAngle < (-angleThresh)) || (interAngle > angleThresh))
          && (angleTotalDiff1 < sumThresh) && (angleTotalDiff2 < sumThresh)) {
        return true;
      }
      return false;
    }

  }

  protected ArcReseau chooseNextSegmentNoStop(ArcReseau arc,
      HashSet<ArcReseau> followers, HashSet<String> attributeNames,
      double deviatAngle, double deviatSum) {

    // first, no stop case
    if (followers.size() == 1) {
      ArcReseau follower = followers.iterator().next();
      if (!this.features.contains(follower))
        return follower;
      else
        return null;
    }

    // first, filter the followers
    this.filterFollowers(arc, followers);
    if (followers.size() == 0) {
      return null;
    }
    // then, filter the followers from the attributes
    this.filterByAttributeContinuity(arc, followers, attributeNames);
    if (followers.size() == 0) {
      return null;
    }

    boolean continuity = true;
    ArcReseau bestSegment = null;
    // if there is only one follower left, choose it as the best follower
    if (followers.size() == 1) {
      ArcReseau follower = followers.iterator().next();
      bestSegment = follower;
      continuity = this.isGoodContinuity(arc.getGeom(), follower.getGeom(),
          deviatAngle, deviatSum);
    } else {
      // get the initial geometry
      ICurve tempGeom = arc.getGeom();

      // Loop on the followers to choose the best continuity
      double minDiff = Math.PI;
      for (ArcReseau follower : followers) {
        // get the follower geometry
        ICurve geomFoll = follower.getGeom();
        // get the continuity difference with this follower
        double diffContinuity = this.goodContinuityDifference(tempGeom,
            geomFoll, deviatAngle, deviatSum);

        if (diffContinuity > -1.0) {
          // il y a un tronçon de bonne continuité
          continuity = true;
          if (diffContinuity < minDiff) {
            // this is the current best continuity
            // update the difference
            minDiff = diffContinuity;
            // change the bestSegment
            bestSegment = follower;
          }// if (diffContinuity < minDiff)
        }// if (diffContinuity > -1.0)
      }// loop on the followers
    }

    if ((continuity) && !this.features.contains(bestSegment)) {
      return bestSegment;
    }
    return null;
  }

  protected void filterFollowers(ArcReseau arc, HashSet<ArcReseau> followers) {
    HashSet<ArcReseau> loopFoll = new HashSet<ArcReseau>();
    // loop on the followers to filter them
    loopFoll.addAll(followers);
    for (ArcReseau a : loopFoll) {
      if (a.equals(arc)) {
        // remove it from the set
        followers.remove(a);
        continue;
      }

      // check that a does not belong to another stroke
      if (this.network.getGroupedFeatures().contains(a)) {
        // remove it from the set
        followers.remove(a);
        continue;
      }
      // check if it belongs to this stroke
      if (this.features.contains(a)) {
        // remove it from the set
        followers.remove(a);
      }

      // check if it belongs to the network
      if (!this.network.getFeatures().contains(a)) {
        // remove it from the set
        followers.remove(a);
      }
    }
  }

  protected void filterByAttributeContinuity(ArcReseau arc,
      HashSet<ArcReseau> followers, HashSet<String> attributeNames) {
    if (this.getNetwork().isAttributesDeclared())
      try {
        filterByAttributeContinuityDeclared(arc, followers, attributeNames);
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    else
      filterByAttributeContinuityFT(arc, followers, attributeNames);
  }

  /**
   * Searches the attribute of each feature with the declared getters in the
   * class.
   * @param arc
   * @param followers
   * @param attributeNames
   * @throws NoSuchMethodException
   * @throws SecurityException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   */
  private void filterByAttributeContinuityDeclared(ArcReseau arc,
      HashSet<ArcReseau> followers, HashSet<String> attributeNames)
      throws SecurityException, NoSuchMethodException,
      IllegalArgumentException, IllegalAccessException,
      InvocationTargetException {
    HashSet<ArcReseau> loopFoll = new HashSet<ArcReseau>();

    // loop on the attribute names to filter the followers
    for (String attribute : attributeNames) {
      // get the method from the attribute name
      String methName = "get" + attribute.substring(0, 1).toUpperCase()
          + attribute.substring(1);
      Method meth = arc.getClass().getDeclaredMethod(methName);
      // get the attribute value for 'arc'
      Object value = meth.invoke(arc);

      if (followers.size() != 0) {
        // loop on the followers to filter them
        loopFoll.addAll(followers);
        for (ArcReseau a : loopFoll) {
          // get the value of a for attribute
          Object valueA = meth.invoke(a);
          if (!value.equals(valueA)) {
            // remove 'a' from the followers set
            followers.remove(a);
          }
        }// loop on the followers
      }
    }// loop on the attributes
  }

  /**
   * Searches the attribute of each feature in its feature type with the
   * getAttribute() method.
   * @param arc
   * @param followers
   * @param attributeNames
   */
  private void filterByAttributeContinuityFT(ArcReseau arc,
      HashSet<ArcReseau> followers, HashSet<String> attributeNames) {
    HashSet<ArcReseau> loopFoll = new HashSet<ArcReseau>();

    // loop on the attribute names to filter the followers
    for (String attribute : attributeNames) {

      // get the attribute value for 'arc'
      Object value = arc.getAttribute(attribute);

      if (followers.size() != 0) {
        // loop on the followers to filter them
        loopFoll.addAll(followers);
        for (ArcReseau a : loopFoll) {
          // get the value of a for attribute
          Object valueA = a.getAttribute(attribute);
          if (!value.equals(valueA)) {
            // remove 'a' from the followers set
            followers.remove(a);
          }
        }// loop on the followers
      }
    }// loop on the attributes
  }

  protected static HashSet<ArcReseau> getFollowing(ArcReseau arc,
      NoeudReseau node) {
    HashSet<ArcReseau> followers = new HashSet<ArcReseau>();
    followers.addAll(node.getArcsEntrants());
    followers.addAll(node.getArcsSortants());
    followers.remove(arc);
    return followers;
  }

  /**
   * Build the stroke geometry from the list of features composing the stroke.
   */
  public void buildGeomStroke() {
    ArrayList<ILineString> geoms = new ArrayList<ILineString>();
    for (ArcReseau arc : this.features) {
      geoms.add((ILineString) arc.getGeom());
    }
    this.setGeomStroke(this.joinStrokeFeatures(geoms));
    if (this.getGeomStroke() == null) {
      System.out.println(this.features);
    }
  }

  private ILineString joinStrokeFeatures(ArrayList<ILineString> lines) {
    IDirectPositionList pointsFinaux = new DirectPositionList();
    if (lines.size() == 0) {
      Stroke.logger
          .warning("ATTENTION. Erreur à la compilation de lignes : aucune ligne en entrée");
      return null;
    }
    if (lines.size() == 1) {
      return lines.get(0);
    }

    // get the root geom index in the list
    int index = 0;
    for (ILineString line : lines) {
      if (line.equals(this.root.getGeom())) {
        index = lines.indexOf(line);
      }
    }
    // cut the list in two lists around the root
    List<ILineString> before = lines.subList(0, index);
    List<ILineString> after = lines.subList(index + 1, lines.size());
    ILineString lineBefore = null, lineAfter = null;

    if (index != 0) {
      lineBefore = Operateurs.compileArcs(before);
      if (Distances.proche(this.root.getGeom().startPoint(), lineBefore
          .startPoint(), 0)) {
        lineBefore.reverse();
      }
      pointsFinaux.addAll(lineBefore.getControlPoint());
      pointsFinaux.remove(this.root.getGeom().startPoint());
    }

    pointsFinaux.addAll(((ILineString) this.root.getGeom()).getControlPoint());

    if (index != lines.size() - 1) {
      lineAfter = Operateurs.compileArcs(after);
      pointsFinaux.remove(this.root.getGeom().endPoint());

      if (Distances.proche(this.root.getGeom().endPoint(),
          lineAfter.endPoint(), 0)) {
        lineAfter.reverse();
      }
      pointsFinaux.addAll(lineAfter.getControlPoint());
    }
    return new GM_LineString(pointsFinaux);
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return null;
  }

  /**
   * Computes the total length of a stroke.
   * @return
   */
  public double getLength() {
    double length = 0.0;
    for (ArcReseau arc : getFeatures())
      length += arc.getGeom().length();
    return length;
  }
}