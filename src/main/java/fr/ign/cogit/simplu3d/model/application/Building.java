package fr.ign.cogit.simplu3d.model.application;

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
 * @author Brasebin Micka�l
 * 
 * @version 1.0
 **/
public class Building extends AbstractBuilding {

  protected Building(){
  }
  public Building(IGeometry geom) {
    super(geom);
  }

  @Override
  public AbstractBuilding clone() {
    Building b = new Building((IGeometry) this.getGeom().clone());
    b.isNew = this.isNew;
    return b;
  }
}
