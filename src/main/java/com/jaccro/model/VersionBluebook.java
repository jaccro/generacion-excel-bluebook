package com.jaccro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionBluebook {
  private int id;
  private ModeloBluebook modeloBluebook;
  private String codigo;
  private String nombre;
  private int anioLanzamiento;
  private int anioVigencia;
  private double valorReferencial;
  private boolean activo;
}
