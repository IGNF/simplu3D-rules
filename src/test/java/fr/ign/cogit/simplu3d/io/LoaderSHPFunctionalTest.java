package fr.ign.cogit.simplu3d.io;

import java.io.File;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.simplu3d.demo.DemoEnvironmentProvider;
import fr.ign.cogit.simplu3d.io.nonStructDatabase.shp.LoaderSHP;
import fr.ign.cogit.simplu3d.model.AbstractBuilding;
import fr.ign.cogit.simplu3d.model.BasicPropertyUnit;
import fr.ign.cogit.simplu3d.model.CadastralParcel;
import fr.ign.cogit.simplu3d.model.Environnement;
import fr.ign.cogit.simplu3d.model.ParcelBoundary;
import fr.ign.cogit.simplu3d.model.UrbaDocument;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 *
 * Test LoaderSHP with sample environments
 * 
 * @author MBorne
 *
 */
public class LoaderSHPFunctionalTest extends TestCase {

	public void testDefaultEnvironment(){
		Environnement env = DemoEnvironmentProvider.getDefaultEnvironment();

		UrbaDocument plu = env.getUrbaDocument();

		Assert.assertNotNull(plu);
		Assert.assertEquals(1, env.getUrbaZones().size());

		IFeatureCollection<ParcelBoundary> bordures = new FT_FeatureCollection<ParcelBoundary>();

		int count = 0;
		Assert.assertEquals("Toutes les parcelles sont chargées.", 19, env.getCadastralParcels().size());

		Assert.assertNotNull(env.getBpU());

		Assert.assertEquals("Toutes les unités foncières sont chargées.", 19, env.getBpU().size());

		for (BasicPropertyUnit bPU : env.getBpU()) {

			Assert.assertNotNull(bPU.getCadastralParcels());
			Assert.assertFalse(bPU.getCadastralParcels().isEmpty());

			for (CadastralParcel sp : bPU.getCadastralParcels()) {

				count = count + sp.getBoundaries().size();

				Assert.assertNotNull(sp.getBoundaries());
				Assert.assertFalse(sp.getBoundaries().isEmpty());

				for (ParcelBoundary b : sp.getBoundaries()) {
					bordures.add(b);

				}

			}
		}

		Assert.assertEquals("Toutes les limites séparatives sont chargées.", 140, count);

		Assert.assertEquals("Toutes les sous parcelles sont chargées.", 19, env.getSubParcels().size());

		IFeatureCollection<IFeature> featToits = new FT_FeatureCollection<IFeature>();

		Assert.assertEquals("Les emprises sont générées.", 40, env.getBuildings().size());

		for (AbstractBuilding b : env.getBuildings()) {
			featToits.add(new DefaultFeature(b.getFootprint()));
		}

		IFeatureCollection<IFeature> featFaitage = new FT_FeatureCollection<IFeature>();
		for (AbstractBuilding b : env.getBuildings()) {
			featFaitage.add(new DefaultFeature(b.getRoof().getRoofing()));
		}

		Assert.assertEquals("Les faîtages sont générés.", 40, featFaitage.size());

		Assert.assertNotNull(env.getRoads());
		Assert.assertFalse(env.getRoads().isEmpty());

		Assert.assertTrue("Le test est un succès", true);
	}
	
	public void testLoadProjectT1(){
		File path = new File(
			getClass().getClassLoader().getResource("ProjectT1").getPath()
		);
		try {
			Environnement env = LoaderSHP.load(path);
			assertEquals( 5, env.getBpU().size() ) ;			
			assertEquals( 0, env.getBuildings().size() ) ;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testLoadProjectT2(){
		File path = new File(
			getClass().getClassLoader().getResource("ProjectT2").getPath()
		);
		try {
			Environnement env = LoaderSHP.load(path);
			assertEquals( 4, env.getBpU().size() ) ;			
			assertEquals( 3, env.getBuildings().size() ) ;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testLoadProjectT3(){
		File path = new File(
			getClass().getClassLoader().getResource("ProjectT3").getPath()
		);
		try {
			Environnement env = LoaderSHP.load(path);
			assertEquals( 19, env.getBpU().size() ) ;			
			assertEquals( 0, env.getBuildings().size() ) ;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testLoadProjectT4(){
		File path = new File(
			getClass().getClassLoader().getResource("ProjectT4").getPath()
		);
		try {
			Environnement env = LoaderSHP.load(path);
			assertEquals( 1, env.getBpU().size() ) ;			
			assertEquals( 1, env.getBuildings().size() ) ;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
