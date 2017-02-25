package fr.ign.cogit.simplu3d.checker.hackurba;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.convert.FromGeomToLineString;
import fr.ign.cogit.geoxygene.sig3d.analysis.ProspectCalculation;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.simplu3d.checker.GeometricConstraints;
import fr.ign.cogit.simplu3d.checker.IRuleChecker;
import fr.ign.cogit.simplu3d.checker.RuleContext;
import fr.ign.cogit.simplu3d.checker.UnrespectedRule;
import fr.ign.cogit.simplu3d.model.BasicPropertyUnit;
import fr.ign.cogit.simplu3d.model.CadastralParcel;
import fr.ign.cogit.simplu3d.model.ParcelBoundary;
import fr.ign.cogit.simplu3d.model.ParcelBoundarySide;
import fr.ign.cogit.simplu3d.model.ParcelBoundaryType;
import fr.ign.cogit.simplu3d.model.Regulation;

public class CheckProspectToSeparativeBoundaries implements IRuleChecker {

  public final static String CODE_DISTANCE_PROSPECT_BUILINDG = "PROSPECT_EXISTING";

  public CheckProspectToSeparativeBoundaries() {

  }

  @Override
  public List<UnrespectedRule> check(BasicPropertyUnit bPU,
      RuleContext context) {
    List<UnrespectedRule> lUNR = new ArrayList<>();

    return lUNR;
  }

  @Override
  public List<GeometricConstraints> generate(BasicPropertyUnit bPU) {
    List<GeometricConstraints> geomConstraints = new ArrayList<>();

    Regulation r1 = bPU.getR1();

    if (r1 != null && r1.getArt_74() != 99 && r1.getArt_74() != 0) {

      List<GeometricConstraints> gc = generateGEometricConstraintsForOneRegulation(
          bPU, r1);
      if (gc != null) {
        geomConstraints.addAll(gc);

      }

    }

    Regulation r2 = bPU.getR2();

    if (r2 != null && r2.getArt_74() != 99 && r2.getArt_74() != 0) {

      List<GeometricConstraints> gc = generateGEometricConstraintsForOneRegulation(
          bPU, r2);
      if (gc != null) {
        geomConstraints.addAll(gc);

      }

    }

    return geomConstraints;

  }

  private List<IMultiCurve<IOrientableCurve>> getBotLimit(BasicPropertyUnit bPU) {
    List<IMultiCurve<IOrientableCurve>> img = new ArrayList<>();
    img.add(new GM_MultiCurve<>());
    img.add(new GM_MultiCurve<>());
    img.add(new GM_MultiCurve<>());
    
    
    

    for (CadastralParcel cP : bPU.getCadastralParcels()) {
      for (ParcelBoundary sc : cP.getBoundaries()) {
        if (sc.getType().equals(ParcelBoundaryType.BOT)){

          img.get(0).addAll(FromGeomToLineString.convert(sc.getGeom()));
        }
        
        if ((sc.getSide().equals(ParcelBoundarySide.LEFT))
            && (sc.getType().equals(ParcelBoundaryType.LAT))) {

          img.get(1).addAll(FromGeomToLineString.convert(sc.getGeom()));
        }
        
        
        
        if ((sc.getSide().equals(ParcelBoundarySide.RIGHT))
            && (sc.getType().equals(ParcelBoundaryType.LAT))) {

          img.get(2).addAll(FromGeomToLineString.convert(sc.getGeom()));
        }
      }
    }

    return img;
  }

  private List<GeometricConstraints> generateGEometricConstraintsForOneRegulation(
      BasicPropertyUnit bPU, Regulation r) {

    List<IMultiCurve<IOrientableCurve>> imc = getBotLimit(bPU);

    int coeff = r.getArt_74();

    List<GeometricConstraints> lGeom = new ArrayList<>();
    
    if(r.getGeomBande().isEmpty()){
      return lGeom;
    }
    
    
    for(IMultiCurve<IOrientableCurve> curveTemp : imc){
      
      if(! curveTemp.buffer(0.5).intersects(r.getGeomBande())){
        continue;
      }
      IGeometry geom = ProspectCalculation.calculate(r.getGeomBande(),curveTemp, 
          1.0 / coeff, 0);

      if (geom != null) {
        lGeom.add(new GeometricConstraints(
            "Distance minimale des constructions par rapport aux limites séparatives relative à a hauteur du bâtiment. Retrait  1/"
                + coeff,
            geom, CODE_DISTANCE_PROSPECT_BUILINDG));

      }

    }


    return lGeom;
  }

}
