package fr.ign.cogit.simplu3d.demo.nonStructDatabase.shp;

import java.io.File;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.calculation.CampSkeleton;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromGeomToSurface;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;
import fr.ign.cogit.simplu3d.importer.AssignBuildingPartToSubParcel;
import fr.ign.cogit.simplu3d.importer.CadastralParcelLoader;
import fr.ign.cogit.simplu3d.io.nonStructDatabase.shp.LoaderSHP;
import fr.ign.cogit.simplu3d.model.AbstractBuilding;
import fr.ign.cogit.simplu3d.model.BasicPropertyUnit;
import fr.ign.cogit.simplu3d.model.CadastralParcel;
import fr.ign.cogit.simplu3d.model.Environnement;
import fr.ign.cogit.simplu3d.model.Road;
import fr.ign.cogit.simplu3d.model.SpecificCadastralBoundary;
import fr.ign.cogit.simplu3d.model.SubParcel;
import fr.ign.cogit.simplu3d.model.UrbaDocument;

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
public class LoaderSHPExec {
	public static IFeatureCollection<IFeature> featC = new FT_FeatureCollection<>();

	public static void main(String[] args) throws Exception {

//		RoadReader.ATT_NOM_RUE = "NOM_VOIE_G";
//		RoadReader.ATT_LARGEUR = "LARGEUR";
//		RoadReader.ATT_TYPE = "NATURE";

		CadastralParcelLoader.WIDTH_DEP = 30;

		// Rerouting towards the new files
//		LoaderSHP.NOM_FICHIER_PLU = "DOC_URBA.shp";
//		LoaderSHP.NOM_FICHIER_ZONAGE = "zones_UB2.shp";
//		LoaderSHP.NOM_FICHIER_PARCELLE = "parcelles_UB2.shp";
//		LoaderSHP.NOM_FICHIER_TERRAIN = "MNT_UB2_L93.asc";
//		LoaderSHP.NOM_FICHIER_VOIRIE = "Voirie_UB2.shp";
//		LoaderSHP.NOM_FICHIER_BATIMENTS = "Bati_UB2_3D.shp";
//
//		LoaderSHP.NOM_FICHIER_PRESC_LINEAIRE = "[Insert File Name].shp";

//		CadastralParcelLoader.ATT_ID_PARC = "NUMERO";
		CadastralParcelLoader.TYPE_ANNOTATION = 2;

		//UrbaZoneReader.ATT_TYPE_ZONE = "TYPE";

		AssignBuildingPartToSubParcel.RATIO_MIN = 0.5;

		// String folder =
		// "C:/Users/mbrasebin/Desktop/Ilots_test/COGIT78/78020432/";
		String folder = "D:/0_Masson/1_CDD_SIMPLU/2_Travail/0_Workspace/simplu3d/simplu3D-rules/src/main/resources/fr/ign/cogit/simplu3d/data/dataRennes/";
		String folderOut = folder + "out/";

		File fOut = new File(folderOut);
		fOut.mkdir();

		double valShiftB = 1.5;

		Environnement env = LoaderSHP.load(new File(folder));

		UrbaDocument plu = env.getUrbaDocument();

		System.out.println("Nombre de zones dans le PLU : " + env.getUrbaZones().size());

		IFeatureCollection<IFeature> bordures_translated = new FT_FeatureCollection<>();
		IFeatureCollection<SpecificCadastralBoundary> bordures = new FT_FeatureCollection<SpecificCadastralBoundary>();

		int count = 0;

		IFeatureCollection<IFeature> lTotArc = new FT_FeatureCollection<>();
		System.out.println("nb Parcelles : " + env.getCadastralParcels().size());

		for (BasicPropertyUnit bPU : env.getBpU()) {

			if (bPU.getId() == 41)
				continue;

			IPolygon pol = (IPolygon) FromGeomToSurface.convertGeom(bPU.getGeom()).get(0);

			int coordSize = pol.coord().size() - 1;

			double[] tab = new double[coordSize];

			for (int i = 0; i < coordSize; i++) {
				tab[i] = Math.PI / 4;
			}

			CampSkeleton cs = new CampSkeleton(pol, tab);

			lTotArc.addAll(cs.getInteriorArcs());

			for (CadastralParcel sp : bPU.getCadastralParcels()) {

				count = count + sp.getBoundaries().size();

				IDirectPosition centroidParcel = sp.getGeom().centroid();

				AttributeManager.addAttribute(sp, "ID", sp.getId(), "Integer");
				AttributeManager.addAttribute(sp, "NBBord", sp.getBoundaries().size(), "Integer");
				AttributeManager.addAttribute(sp, "NBBat", bPU.getBuildings().size(), "Integer");

				for (SpecificCadastralBoundary b : sp.getBoundaries()) {
					bordures.add(b);

					AttributeManager.addAttribute(b, "ID", b.getId(), "Integer");
					AttributeManager.addAttribute(b, "Type", b.getType(), "Integer");
					AttributeManager.addAttribute(b, "IDPar", sp.getId(), "Integer");
					AttributeManager.addAttribute(b, "Type", b.getSide(), "Integer");

					if (b.getFeatAdj() != null) {

						if (b.getFeatAdj() instanceof CadastralParcel) {

							AttributeManager.addAttribute(b, "Adj", ((CadastralParcel) b.getFeatAdj()).getId(),
									"Integer");
						} else if (b.getFeatAdj() instanceof Road) {
							AttributeManager.addAttribute(b, "Adj", ((Road) b.getFeatAdj()).getId(), "Integer");
						}

					} else {
						AttributeManager.addAttribute(b, "Adj", 0, "Integer");

					}

					if (b.getGeom() == null || b.getGeom().isEmpty()) {

						continue;
					}

					IDirectPosition centroidGeom = b.getGeom().coord().get(0);

					Vecteur v = new Vecteur(centroidParcel, centroidGeom);

					Vecteur v2 = new Vecteur(b.getGeom().coord().get(0),
							b.getGeom().coord().get(b.getGeom().coord().size() - 1));
					v2.setZ(0);
					v2.normalise();

					Vecteur vOut = v2.prodVectoriel(new Vecteur(0, 0, 1));

					IGeometry geom = ((IGeometry) b.getGeom().clone());

					if (v.prodScalaire(vOut) < 0) {
						vOut = vOut.multConstante(-1);
					}

					IGeometry geom2 = geom.translate(valShiftB * vOut.getX(), valShiftB * vOut.getY(), 0);

					if (!geom2.intersects(sp.getGeom())) {
						geom2 = geom.translate(-valShiftB * vOut.getX(), -valShiftB * vOut.getY(), 0);
					}

					IFeature feat = new DefaultFeature(geom2);

					AttributeManager.addAttribute(feat, "Type", b.getType(), "Integer");
					AttributeManager.addAttribute(feat, "Side", b.getSide(), "Integer");
					bordures_translated.add(feat);

				}

			}
		}

		System.out.println("Nombre sbordurs" + count);

		// Export des parcelles

		ShapefileWriter.write(env.getCadastralParcels(), folderOut + "parcelles.shp");
		ShapefileWriter.write(bordures, folderOut + "bordures.shp");
		ShapefileWriter.write(bordures_translated, folderOut + "bordures_translated.shp");

		ShapefileWriter.write(featC, folderOut + "buffer.shp");

		ShapefileWriter.write(lTotArc, folderOut + "arcs.shp");

		System.out.println("Nombre de bpu : " + env.getBpU().size());

		ShapefileWriter.write(env.getBpU(), folderOut + "bpu.shp");

		/*
		 * IFeatureCollection<Alignement> featAL = new
		 * FT_FeatureCollection<Alignement>();
		 * 
		 * 
		 * 
		 * for (Alignement a : env.getAlignements()) {
		 * 
		 * if (a != null) { featAL.add(a);
		 * 
		 * AttributeManager.addAttribute(a, "ID", a.getId(), "Integer"); }
		 */

		System.out.println("Sous Parcelles  " + env.getSubParcels().size());

		for (SubParcel sp : env.getSubParcels()) {
			AttributeManager.addAttribute(sp, "Test", 0, "Integer");
			AttributeManager.addAttribute(sp, "NB Bat", sp.getBuildingsParts().size(), "Integer");
		}

		ShapefileWriter.write(env.getSubParcels(), folderOut + "sousParcelles.shp");

		IFeatureCollection<IFeature> featToits = new FT_FeatureCollection<IFeature>();

		System.out.println("NB emprise " + env.getBuildings().size());
		for (AbstractBuilding b : env.getBuildings()) {
			featToits.add(new DefaultFeature(b.getFootprint()));
		}

		ShapefileWriter.write(featToits, folderOut + "emprise.shp");

		IFeatureCollection<IFeature> featFaitage = new FT_FeatureCollection<IFeature>();
		for (AbstractBuilding b : env.getBuildings()) {
			featFaitage.add(new DefaultFeature(b.getRoof().getRoofing()));
		}

		System.out.println("Faitage : " + featFaitage.size());

		ShapefileWriter.write(featFaitage, folderOut + "pignon.shp");

		IFeatureCollection<IFeature> featRoute = new FT_FeatureCollection<>();

		for (Road r : env.getRoads()) {

			AttributeManager.addAttribute(r, "Nom", r.getName(), "String");
			featRoute.add(r);

		}

		ShapefileWriter.write(featRoute, folderOut + "roads.shp");

		IFeatureCollection<IFeature> featOutTestCons = new FT_FeatureCollection<>();
		for (CadastralParcel sp : env.getCadastralParcels()) {

			featOutTestCons.add(new DefaultFeature(sp.getConsLine()));

			System.out.println(sp.getBoundaries().size());

		}

		ShapefileWriter.write(featOutTestCons, folderOut + "featConsF.shp");

		System.out.println("Chat qui râle");

	}

}
