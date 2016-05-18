/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.0
 **/
package fr.ign.cogit.simplu3d.indicator;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.building.CG_AbstractBuilding;
import fr.ign.cogit.simplu3d.model.application.AbstractBuilding;
import fr.ign.cogit.simplu3d.model.application.BasicPropertyUnit;
import fr.ign.cogit.simplu3d.model.application.CadastralParcel;
import fr.ign.cogit.simplu3d.model.application.SpecificCadastralBoundary;
import fr.ign.cogit.simplu3d.model.application.SpecificCadastralBoundary.SpecificCadastralBoundaryType;
import fr.ign.cogit.simplu3d.model.application.SubParcel;

/**
 * 
 * Calcul de la hauteur d'un bâtiment
 * 
 * @author MBrasebin
 *
 */
public class HauteurCalculation {

  public enum POINT_HAUT_TYPE {
    PLUS_HAUT_EGOUT, PLUS_HAUT_FAITAGE, PLANCHER_PLUS_ELEVE
  };

  public static double calculate(AbstractBuilding b, int type_pb, int type_ph) {

    double zBas = calculateZBas(b, type_pb);

    double zHaut = calculateZHaut(b, type_ph);

    return zHaut - zBas;

  }

  public static double calculateZHaut(AbstractBuilding b, int type_ph) {

    double zHaut = Double.NaN;

    switch (type_ph) {
      case 0:
        zHaut = calculateZHautPPE(b);
        break;
      case 1:
        zHaut = calculateZHautPHE(b);
        break;
      case 2:
        zHaut = calculateZHautPHF(b);
        break;

    }

    return zHaut;
  }

  public static double calculateZBas(AbstractBuilding b, Integer type_pb) {

    double zBas = -1;

    if (type_pb == PointBasType.EMPRISE_PUBLIQUE) {
      zBas = calculateZBasEP(b);
    }

    if (type_pb == PointBasType.PLUS_BAS_BATIMENT) {
      zBas = calculateZBasPBB(b);
    }

    if (type_pb == PointBasType.PLUS_BAS_TERRAIN) {
      zBas = calculateZBasPBT(b);
    }

    if (type_pb == PointBasType.PLUS_HAUT_TERRAIN) {
      zBas = calculateZBasPHT(b);
    }

    return zBas;
  }

  // //////////////////DIFFERENTS TYPES DE ZHAUT
  // // IL s'agit d'un Z et pas d'un H bien sur
  public static double calculateZHautPPE(AbstractBuilding b) {

    double hauteurParEtage = b.getStoreyHeightsAboveGround();

    if (hauteurParEtage <= 0 || !StoreyCalculation.USE_STOREYS_HEIGH_ATT) {
      hauteurParEtage = StoreyCalculation.HAUTEUR_ETAGE;
    }

    int nbEtage = StoreyCalculation.process(b);

    double hauteur = hauteurParEtage * nbEtage;

    Box3D box = new Box3D(b.getGeom());

    return hauteur + box.getLLDP().getZ();
  }

  public static double calculateZHautPHE(AbstractBuilding b) {

    IGeometry g = b.getRoof().getGutter();

    Box3D box = new Box3D(g);

    return box.getURDP().getZ();
  }

  public static double calculateZHautPHF(CG_AbstractBuilding b) {
    Box3D box = new Box3D(b.getGeom());
    return box.getURDP().getZ();
  }

  // //////////////////DIFFERENTS TYPES DE ZBAS

  private static double calculateZBasPHT(AbstractBuilding b) {

    List<SubParcel> spList = b.getSubParcels();

    double zMax = Double.NEGATIVE_INFINITY;

    for (SubParcel sp : spList) {

      Box3D box = new Box3D(sp.getGeom());

      zMax = Math.max(zMax, box.getLLDP().getZ());

    }

    return zMax;
  }

  private static double calculateZBasPBT(AbstractBuilding b) {

    List<SubParcel> spList = b.getSubParcels();

    double zMin = Double.POSITIVE_INFINITY;

    for (SubParcel sp : spList) {

      Box3D box = new Box3D(sp.getGeom());

      zMin = Math.min(zMin, box.getLLDP().getZ());

    }

    return zMin;
  }

  private static double calculateZBasPBB(CG_AbstractBuilding b) {
    Box3D box = new Box3D(b.getGeom());
    return box.getLLDP().getZ();
  }

  private static double calculateZBasEP(AbstractBuilding b) {
    BasicPropertyUnit bpuList = b.getbPU();

    double zMin = Double.POSITIVE_INFINITY;

    for (CadastralParcel cp : bpuList.getCadastralParcel()) {

      for (SubParcel sp : cp.getSubParcel()) {
        IFeatureCollection<SpecificCadastralBoundary> bordures = sp
            .getSpecificCadastralBoundaryColl();

        for (SpecificCadastralBoundary bord : bordures) {

          if (bord.getType() == SpecificCadastralBoundaryType.ROAD
              || bord.getType() == SpecificCadastralBoundaryType.PUB) {

            Box3D box = new Box3D(bord.getGeom());

            zMin = Math.min(zMin, box.getLLDP().getZ());

          }

        }
      }
    }

    if (zMin == Double.POSITIVE_INFINITY) {
      zMin = calculateZBasPBB(b);
    }

    return zMin;
  }

}
