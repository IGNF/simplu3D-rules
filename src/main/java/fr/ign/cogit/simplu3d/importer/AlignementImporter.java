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
package fr.ign.cogit.simplu3d.importer;

import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromGeomToLineString;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
import fr.ign.cogit.geoxygene.util.index.Tiling;
import fr.ign.cogit.simplu3d.model.Alignement;
import fr.ign.cogit.simplu3d.model.CadastralParcel;
import fr.ign.cogit.simplu3d.model.Recoil;
import fr.ign.cogit.simplu3d.model.SpecificCadastralBoundary;

/**
 * 
 * @author MBrasebin
 *
 */
public class AlignementImporter {

  public final static String ATT_TYPE = "TYPEPSC";
  public final static String ATT_Param = "Param";

  public static IFeatureCollection<Alignement> importRecul(
      IFeatureCollection<IFeature> prescriptions,
      IFeatureCollection<CadastralParcel> cadastralParcels)
      throws CloneNotSupportedException {

    IFeatureCollection<Alignement> lAlignement = new FT_FeatureCollection<Alignement>();

    if (!prescriptions.hasSpatialIndex()) {

      prescriptions.initSpatialIndex(Tiling.class, false);

    }

    for (CadastralParcel cadastralParcel : cadastralParcels) {

      IFeatureCollection<Alignement> lAlignementTemp = new FT_FeatureCollection<Alignement>();

      Collection<IFeature> coll = prescriptions.select(cadastralParcel.getGeom());

      if (coll.isEmpty()) {
        continue;
      }

      IFeatureCollection<IFeature> collToTreat = new FT_FeatureCollection<IFeature>();

      for (IFeature feat : coll) {

        // on ne garde que les types alignements et recul
        Double type = Double
            .parseDouble(feat.getAttribute(ATT_TYPE).toString());

        if (type != 11) {
          continue;
        }

        IFeature featTemp = feat.cloneGeom();

        IGeometry geom = feat.getGeom().intersection(cadastralParcel.getGeom());

        if (geom.isEmpty()) {
          continue;
        }

        featTemp.setGeom(geom);

        collToTreat.add(featTemp);
      }

      // On a des entités avec les morceaux de prescription qui nous
      // intéressent, maintenant il faut éevntuellement décomposer en segment.
      int nbCollToTreat = collToTreat.size();

      for (int i = 0; i < nbCollToTreat; i++) {
        IFeature featTemp = collToTreat.get(i);
        List<IOrientableCurve> lIOC = FromGeomToLineString.convert(featTemp
            .getGeom());

        if (lIOC.isEmpty()) {
          continue;
        }

        for (IOrientableCurve c : lIOC) {

          Alignement a;

          Integer type = Integer.parseInt(featTemp.getAttribute(ATT_TYPE)
              .toString());

          if (type == 11) {

            Recoil b = new Recoil((int) type, (ICurve) c);

            Double param = Double.parseDouble(featTemp.getAttribute(ATT_Param)
                .toString());

            b.setDistance(param);
            a = b;
          } else {
            a = new Alignement(type, (ICurve) c);

          }

          lAlignementTemp.add(a);

        }

      }

      // On a 1 feature par segment d'alignement

      List<SpecificCadastralBoundary> iFCVoie = cadastralParcel.getBoundaries();

      for (Alignement a : lAlignementTemp) {
        SpecificCadastralBoundary b = determineBestBordure(iFCVoie, a);
        if (b != null) {
          b.setAlignement(a);
        }

      }
      lAlignement.addAll(lAlignementTemp);

    }

    return lAlignement;
  }

  private static SpecificCadastralBoundary determineBestBordure(
      List<SpecificCadastralBoundary> bordures, Alignement a) {

    double scoreMax = -1;
    SpecificCadastralBoundary bCand = null;

    double rec = 0;

    if (a instanceof Recoil) {

      rec = ((Recoil) a).getDistance();
    }

    IOrientableCurve geomAlignement = FromGeomToLineString.convert(a.getGeom())
        .get(0);

    Vecteur v = new Vecteur(geomAlignement.coord().get(0), geomAlignement
        .coord().get(1));
    v.normalise();

    for (SpecificCadastralBoundary b : bordures) {

      List<IOrientableCurve> lIOC = FromGeomToLineString.convert(b.getGeom());

      Vecteur v1 = new Vecteur(lIOC.get(0).coord().get(0), lIOC.get(0).coord()
          .get(1));
      v1.normalise();

      double scal = Math.abs(v.prodScalaire(v1));

      double distance = geomAlignement.distance(b.getGeom());

      double scoreDist = 0;

      if (distance == 0) {
        if (rec == 0) {
          scoreDist = 1;
        } else {
          scoreDist = 1 / Math.abs(rec);
        }

      } else {

        scoreDist = Math.abs(1 - Math.abs(distance - rec) / rec);
      }


      double scoreTemp = scoreDist * scal;

      if (scoreTemp > scoreMax) {

        scoreMax = scoreTemp;
        bCand = b;

      }

    }

    AttributeManager.addAttribute(a, "ID_B", bCand.getId(), "Integer");
    AttributeManager.addAttribute(a, "score", scoreMax, "Double");
    return bCand;
  }

}
