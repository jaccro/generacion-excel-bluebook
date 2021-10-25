package com.jaccro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModeloBluebook {
  private int id;
  private MarcaBluebook marcaBluebook;
  private String nombre;

  public ModeloBluebook(int id, String nombre){
    this.id = id;
    this.nombre = nombre;
  }
}
