package fr.ign.cogit.simplu3d.importer.model;

import java.io.File;
import java.net.URL;

import tudresden.ocl20.pivot.model.IModel;
import tudresden.ocl20.pivot.modelinstance.IModelInstance;
import tudresden.ocl20.pivot.modelinstancetype.exception.TypeNotFoundInModelException;
import tudresden.ocl20.pivot.modelinstancetype.java.internal.modelinstance.JavaModelInstance;
import tudresden.ocl20.pivot.standalone.facade.StandaloneFacade;
import fr.ign.cogit.simplu3d.model.application.AbstractBuilding;
import fr.ign.cogit.simplu3d.model.application.Environnement;
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
 * @author Brasebin Mickaël
 * 
 * @version 1.0
 **/
public class ImportModelInstanceEnvironnement {

  public static IModel getModel(String modelPorviderURL) {

    try {
      StandaloneFacade.INSTANCE.initialize(new URL("file:"
          + new File("log4j.properties").getAbsolutePath()));

      IModel model = StandaloneFacade.INSTANCE.loadJavaModel(new File(
          modelPorviderURL));

      return model;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;

  }

  public static IModelInstance getModelInstance(IModel model, Environnement env) {

    try {

      IModelInstance modelInstance = new JavaModelInstance(model);

      completeInstances(modelInstance, env);

      return modelInstance;

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;

  }

  private static void completeInstances(IModelInstance modelInstance,
      Environnement env) throws TypeNotFoundInModelException {

    /*
     * 
     * 
     * Gestion des sous parcelles
     */

    for (SubParcel sp : env.getSubParcels()) {
      modelInstance.addModelInstanceElement(sp);
      if (sp.getGeom() == null) {
        System.out.println("Patata");
      }
      modelInstance.addModelInstanceElement(sp.getLod2MultiSurface());
      modelInstance.addModelInstanceElement(sp.getGeom());

      // Gestion des bordures
      /*
       * for(SpecificCadastralBoundary b:sp.getSpecificCadastralBoundary()){
       * modelInstance.addModelInstanceElement(b);
       * modelInstance.addModelInstanceElement(b.getGeom()); }
       */

    }

    // Gestion des bâtiments

    for (AbstractBuilding b : env.getBuildings()) {
      modelInstance.addModelInstanceElement(b);
    }

  }

}
