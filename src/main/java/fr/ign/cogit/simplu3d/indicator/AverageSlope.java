package fr.ign.cogit.simplu3d.indicator;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTMArea;
import fr.ign.cogit.simplu3d.model.application.SubParcel;
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
public class AverageSlope {

  public static double averageSlope(SubParcel p, DTMArea dtm)
      throws Exception {

    Vecteur vTot = new Vecteur(0, 0, 0);

    for (IOrientableSurface o : p.getLod2MultiSurface()) {

      double area3DTemp = dtm.calcul3DArea(o);

      ApproximatedPlanEquation ep = new ApproximatedPlanEquation(o);

      Vecteur v = ep.getNormale();

      v.normalise();

      if (v.getZ() < 0) {
        v.multConstante(-area3DTemp);
      } else {
        v.multConstante(area3DTemp);
      }

      vTot = vTot.ajoute(v);

    }

    double z = vTot.getZ();

    vTot.setZ(0);

    double norme = vTot.norme();

    if (norme == 0) {
      return 0;
    }

    return Math.PI / 2 - Math.atan(z / norme);

  }

}
