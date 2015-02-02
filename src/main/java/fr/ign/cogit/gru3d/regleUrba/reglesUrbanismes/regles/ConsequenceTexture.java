//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, v2.1.3-b01-fcs
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2009.10.26 at 12:24:15 PM CET
//

package fr.ign.cogit.gru3d.regleUrba.reglesUrbanismes.regles;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import fr.ign.cogit.gru3d.regleUrba.Executor;
import fr.ign.cogit.gru3d.regleUrba.representation.Incoherence;
import fr.ign.cogit.gru3d.regleUrba.schemageo.Batiment;
import fr.ign.cogit.gru3d.regleUrba.schemageo.Parcelle;
import fr.ign.cogit.gru3d.regleUrba.schemageo.Toit;

/**
 * <p>
 * Java class for ConsequenceTexture complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="ConsequenceTexture">
 *   &lt;complexContent>
 *     &lt;extension base="{}Consequence">
 *       &lt;sequence>
 *         &lt;element name="nomTexture" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsequenceTexture", propOrder = { "nomTexture" })
public class ConsequenceTexture extends Consequence {

  public ConsequenceTexture() {
  }

  public ConsequenceTexture(List<String> nomTexture) {
    this.nomTexture = nomTexture;
  }

  @XmlElement(required = true)
  protected List<String> nomTexture;

  /**
   * Gets the value of the nomTexture property.
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the nomTexture property.
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getNomTexture().add(newItem);
   * </pre>
   * <p>
   * Objects of the following type(s) are allowed in the list {@link String }
   */
  public List<String> getNomTexture() {
    if (this.nomTexture == null) {
      this.nomTexture = new ArrayList<String>();
    }
    return this.nomTexture;
  }

  public void setNomTexture(List<String> nomTexture) {
    this.nomTexture = nomTexture;
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    int nbTextures = this.getNomTexture().size();

    if (nbTextures == 0) {
      return "Auncune restriction n'est imposée au niveau des textures";
    }

    String s = "";

    for (int i = 0; i < nbTextures; i++) {

      s = s + " " + this.getNomTexture().get(i);
      if (i != nbTextures - 1) {

        s += ";";
      }

    }

    return "Le batiment doit porter l'une des textures suivantes " + s;
  }

  // Vérifie si le toit ou le batiment possède une texture adéquate
  // Renvoie false si c'est le cas
  @Override
  public List<Incoherence> isConsequenceChecked(Parcelle p, boolean represent) {

    List<Incoherence> lIncoherence = new ArrayList<Incoherence>();

    List<Batiment> lBatimentsContenus = p.getlBatimentsContenus();

    int nBati = lBatimentsContenus.size();
    int nbTextures = this.getNomTexture().size();

    for (int i = 0; i < nBati; i++) {
      Batiment bati = lBatimentsContenus.get(i);

      boolean isOk = false;

      // On test la texture du batiment
      for (int j = 0; j < nbTextures; j++) {
        String nomText = this.getNomTexture().get(j);
        if (bati.getTexture().getNom().equalsIgnoreCase(nomText)) {
          isOk = true;
          break;
        }
      }

      if (!isOk && nbTextures > 0) {
        if (Executor.VERBOSE) {
          System.out
              .println("Condition non vérifiée : batiment portant une texture interdite");

        }
        
        if(represent){
        lIncoherence.add(new Incoherence(this, p, null));
        }else{
          lIncoherence.add(null);
          return lIncoherence;
        }
      }

      isOk = false;

      Toit t = bati.getToit();
      // On teste la texture du toit
      if (t == null) {

        continue;
      }

      for (int j = 0; j < nbTextures; j++) {
        String nomText = this.getNomTexture().get(j);
        if (t.getTexture().getNom().equalsIgnoreCase(nomText)) {

          isOk = true;
          break;
        }
      }

      if (!isOk && nbTextures > 0) {
        if (Executor.VERBOSE) {
          System.out
              .println("Condition non vérifiée : toit portant une texture interdite");

        }
        if(represent){
        lIncoherence.add(new Incoherence(this, p, null));
        }else{
          lIncoherence.add(null);
          return lIncoherence;
        }

      }

    }
    // C'est bon
    if (Executor.VERBOSE) {
      if (lIncoherence.size() == 0) {
        System.out.println("Condition vérifiée : les textures sont respectées");
      }
    }

    return lIncoherence;

  }

}
