package com.jaccro.service;

import java.util.ArrayList;
import java.util.List;

import com.jaccro.model.MarcaBluebook;
import com.jaccro.repository.MarcaBluebookRepository;

public class ModeloBluebookService {
  
  public List<MarcaBluebook> listByMarca() {
    try {
      return (new MarcaBluebookRepository()).listAll();
    } catch (Exception e) {
      
    }
    return (new ArrayList<MarcaBluebook>());
  }
}
