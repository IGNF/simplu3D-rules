package fr.ign.cogit.simplu3d.importer.applicationClasses;

import java.io.File;
import java.util.List;

import tudresden.ocl20.pivot.pivotmodel.Constraint;
import tudresden.ocl20.pivot.standalone.facade.StandaloneFacade;
import fr.ign.cogit.simplu3d.model.application.Environnement;
import fr.ign.cogit.simplu3d.model.application.Rule;
import fr.ign.cogit.simplu3d.model.application.UrbaZone;
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
public class RulesImporter {

  public static void importer(String folder, UrbaZone z) {
    // on charge le fichier OCL du nom de la zone
    File f = new File(folder + z.getName() + ".ocl");

    // System.out.println("Trying to load : " + f.getAbsolutePath());

    if (f.exists()) {
      try {
        List<Constraint> lC = StandaloneFacade.INSTANCE.parseOclConstraints(
            Environnement.getModel(), f);

        int id = 0;

        for (Constraint c : lC) {

          Rule r = new Rule(c, (++id) + "");
          z.getRules().add(r);

        }

      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      
        System.exit(1);
      }

    }

  }

}
