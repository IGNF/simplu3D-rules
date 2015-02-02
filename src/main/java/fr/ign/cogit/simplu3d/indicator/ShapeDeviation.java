package fr.ign.cogit.simplu3d.indicator;

import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToTriangle;
import fr.ign.cogit.sig3d.calculation.OrientedBoundingBox;
import fr.ign.cogit.sig3d.indicator.Volume;
import fr.ign.cogit.simplu3d.model.application.AbstractBuilding;

public class ShapeDeviation {

  private double value;

  /**
   * Rapport entre le volume d'un objet de le volume de sa boite orientée
   * @param bP
   */
  public ShapeDeviation(AbstractBuilding bP) {

    value = 1;

    OrientedBoundingBox oBB = new OrientedBoundingBox(bP.getGeom());

    if (oBB.getPoly() != null) {

      double zMin = oBB.getzMin();
      double zMax = oBB.getzMax();

      double volArea = oBB.getPoly().area() * (zMax - zMin);

      if (volArea == 0) {
        return;
      }

      
      double vBati = (new Volume(FromPolygonToTriangle.convertAndTriangle(bP.getToit()
          .getLod2MultiSurface().getList()))).getValue();

      value = vBati / volArea;

      if (value > 1) {

        System.out.println("Why ?" + value);
      }

    }

  }

  public Double getValue() {
    // TODO Auto-generated method stub
    return value;
  }

}
