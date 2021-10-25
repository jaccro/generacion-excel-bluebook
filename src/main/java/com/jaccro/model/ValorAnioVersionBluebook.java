package com.jaccro.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValorAnioVersionBluebook {
  private int id;
  private VersionBluebook version;
  private int anio;
  private double valorReferencial;

  public ValorAnioVersionBluebook(int id, int anio, double valorReferencial){
    this.id = id;
    this.anio = anio;
    this.valorReferencial = valorReferencial;
  }
}
