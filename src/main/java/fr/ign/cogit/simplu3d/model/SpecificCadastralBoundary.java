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
package fr.ign.cogit.simplu3d.model;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;

/**
 * 
 * Frontière de parcelle
 * 
 * 
 * Note that SpecificCadastralBoundary forms a topology map
 * 
 * @author Brasebin Mickaël
 *
 */
public class SpecificCadastralBoundary extends DefaultFeature {
	/**
	 * CadastralParcel owning the boundary
	 */
	private CadastralParcel cadastralParcel;
	/**
	 * The type of the boundary
	 */
	private SpecificCadastralBoundaryType type;
	/**
	 * The side of the boundary (relative to the parcel)
	 */
	private SpecificCadastralBoundarySide side;

	/**
	 * Optional Road linked to the boundary
	 */
	private Road road = null ;
	
	private Alignement alignement = null;
	private Recoil recoil = null;



	public SpecificCadastralBoundary(){
		
	}
	
	public SpecificCadastralBoundary(IGeometry geom) {
		this.setGeom(geom);
	}
	
	
	public CadastralParcel getCadastralParcel() {
		return cadastralParcel;
	}

	public void setCadastralParcel(CadastralParcel cadastralParcel) {
		this.cadastralParcel = cadastralParcel;
	}

	public SpecificCadastralBoundaryType getType() {
		return type;
	}

	public void setType(SpecificCadastralBoundaryType type) {
		this.type = type;
	}


	public SpecificCadastralBoundarySide getSide() {
		return side;
	}

	public void setSide(SpecificCadastralBoundarySide side) {
		this.side = side;
	}
	
	
	public Road getRoad() {
		return road;
	}

	public void setRoad(Road road) {
		this.road = road;
	}

	public Alignement getAlignement() {
		return alignement;
	}

	public void setAlignement(Alignement alignement) {
		this.alignement = alignement;
	}

	public Recoil getRecoil() {
		return recoil;
	}

	public void setRecoil(Recoil recoil) {
		this.recoil = recoil;
	}

	/**
	 * Returns the Road if defined, else the CadastralParcel
	 * @return
	 */
	@Deprecated
	public IFeature getFeatAdj(){
		if ( this.road != null ){
			return this.road ;
		}else{
			return this.cadastralParcel;
		}
	}
	
}
