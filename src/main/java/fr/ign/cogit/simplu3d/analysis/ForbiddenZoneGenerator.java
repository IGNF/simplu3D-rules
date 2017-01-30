package fr.ign.cogit.simplu3d.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.index.Tiling;
import fr.ign.cogit.simplu3d.io.feature.PrescriptionReader;
import fr.ign.cogit.simplu3d.model.BasicPropertyUnit;
import fr.ign.cogit.simplu3d.model.Prescription;
import fr.ign.cogit.simplu3d.model.PrescriptionType;

public class ForbiddenZoneGenerator {

	private double radiusOfInfluence = 20;
	
	private final static double DISTANCERECOILEVEGETATION = 3;
	private final static double DISTANCERECOILERESERVEDEMPLACEMENT = 3;
	private final static double DISTANCERECOILPAYSAGE= 3;
	

	public IGeometry generateUnionGeometry(IFeatureCollection<Prescription> prescriptions, BasicPropertyUnit bPU) {

		if (!prescriptions.hasSpatialIndex()) {
			prescriptions.initSpatialIndex(Tiling.class, false);
		}

		Collection<Prescription> selected = prescriptions.select(bPU.getGeom().buffer(radiusOfInfluence));

		List<IGeometry> geometries = new ArrayList<IGeometry>();

		for (Prescription pres : selected) {
			IGeometry geom = generateWithCheckedGeom(pres, bPU);

			if (geom != null && !geom.isEmpty()) {
				geometries.add(geom);
			}

		}

		if (geometries.isEmpty())
			return null;

		IGeometry geom = bPU.getGeom();

		for (IGeometry geomTemp : geometries) {
			geom = geom.difference(geomTemp);
		}
		return geom;
	}

	public IGeometry generate(Prescription p, BasicPropertyUnit bPU) {
		if (!bPU.getGeom().intersects(p.getGeom()))
			return null;

		return generateWithCheckedGeom(p, bPU);
	}

	/**
	 * @TODO
	 * @param p
	 * @param bPU
	 * @return
	 */
	private IGeometry generateWithCheckedGeom(Prescription p, BasicPropertyUnit bPU) {
		PrescriptionType type = p.getType();

		switch (type) {
		case ELEMENT_PAYSAGE:
			return generateGeometryElementPaysage(p, bPU);
		case EMPLACEMENT_RESERVE:
			return generateEmplacementReserve(p, bPU);
		case ESPACE_BOISE:
			return generateEspaceBoise(p, bPU);
		case FACADE_ALIGNMENT:
			break;
		case LIMITATION_BRUIT:
			break;
		case RECOIL:
			return generateReculFacade(p, bPU);
		case SECTEUR_MIXITE:
			break;
		case UNKNOWN:
			break;
		default:
			break;

		}
		return null;
	}

	private IGeometry generateReculFacade(Prescription p, BasicPropertyUnit bPU) {
		
		Object recul = p.getAttribute(PrescriptionReader.ATT_RECOIL);
		
		if(recul == null) return null;
		
		double valRecul = Double.parseDouble(recul.toString());
		
		if(valRecul <= 0) return null;

		return p.getGeom().buffer(valRecul);
	}

	private IGeometry generateEspaceBoise(Prescription p, BasicPropertyUnit bPU) {
		switch (p.getGeom().dimension()) {

		case 0:
		case 1:

			return p.getGeom().buffer(DISTANCERECOILEVEGETATION);
		case 2:
			return p.getGeom();
		}
		
		return null;
	}

	private IGeometry generateEmplacementReserve(Prescription p, BasicPropertyUnit bPU) {
		switch (p.getGeom().dimension()) {

		case 0:
		case 1:
			return p.getGeom().buffer(DISTANCERECOILERESERVEDEMPLACEMENT);
		case 2:
			return p.getGeom();
		}		
		
		return null;
	}

	private IGeometry generateGeometryElementPaysage(Prescription p, BasicPropertyUnit bPU) {
		switch (p.getGeom().dimension()) {

		case 0:
		case 1:
			return p.getGeom().buffer(DISTANCERECOILPAYSAGE);
		case 2:
			return p.getGeom();
		}
		
		return null;
	}
}