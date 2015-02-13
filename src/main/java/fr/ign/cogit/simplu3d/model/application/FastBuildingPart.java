package fr.ign.cogit.simplu3d.model.application;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.0
 **/
public class FastBuildingPart extends BuildingPart {

  private double z;

  public FastBuildingPart(IPolygon poly, double z) {
    super();
    this.setFootprint(poly);
    this.isNew = true;

    IPolygon polyClone = (IPolygon) poly.clone();

    IDirectPositionList dpl = polyClone.exteriorCoord();

    dpl.inverseOrdre();

    for (IDirectPosition dp : dpl) {
      dp.setZ(z);
    }

    this.setGeom(polyClone);

    this.z = z;

  }

  public boolean prospect(IGeometry geom, double slope, double hIni) {


    double distance = this.getFootprint().distance(this.getGeom());

    double zMin = Double.POSITIVE_INFINITY;

    for (IDirectPosition dp : geom.coord()) {

      zMin = Math.min(dp.getZ(), zMin);

    }

    return distance * slope + hIni > (z - zMin);

  }

}
