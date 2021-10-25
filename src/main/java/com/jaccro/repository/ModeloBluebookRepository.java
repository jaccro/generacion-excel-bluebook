package com.jaccro.repository;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jaccro.connection.DBConnection;
import com.jaccro.model.ModeloBluebook;

public class ModeloBluebookRepository {
  
  public List<ModeloBluebook> listAll() throws Exception {
    DBConnection connection = null;
    List<ModeloBluebook> list = new ArrayList<>();

    try {
      connection = new DBConnection();
      String query = String.format("SELECT \"id\", \"id_marca\", \"nombre\" FROM SGC1.\"modelos_bluebook\" ");
      ResultSet rs = connection.executeDataSet(query);
      while(rs.next()){
        list.add(new ModeloBluebook(
            rs.getInt("id"), 
            (new MarcaBluebookRepository()).get(rs.getInt("id_marca")), 
            rs.getString("nombre")
          )
        );
      }
    } catch (SQLException e) {
      StringWriter errors = new StringWriter();
      e.printStackTrace(new PrintWriter(errors));
      throw new Exception(errors.toString());
    }

    return list;
  }

  public List<ModeloBluebook> listByMarca(int idMarca) throws Exception {
    DBConnection connection = null;
    List<ModeloBluebook> list = new ArrayList<>();

    try {
      connection = new DBConnection();
      String query = String.format(
        "SELECT \"id\", \"id_marca\", \"nombre\" FROM SGC1.\"modelos_bluebook\" "
            + "WHERE \"id_marca\"=%s "
            + "ORDER BY \"nombre\"",
        idMarca
      );
      ResultSet rs = connection.executeDataSet(query);
      while(rs.next()){
        list.add(new ModeloBluebook(
            rs.getInt("id"), 
            (new MarcaBluebookRepository()).get(rs.getInt("id_marca")), 
            rs.getString("nombre")
          )
        );
      }
    } catch (SQLException e) {
      StringWriter errors = new StringWriter();
      e.printStackTrace(new PrintWriter(errors));
      throw new Exception(errors.toString());
    } finally {
      connection.closeConnection();
    }

    return list;
  }

  public ModeloBluebook get(int id) throws Exception {
    DBConnection connection = null;

    try {
      connection = new DBConnection();
      String query = String.format(
        "SELECT \"id\", \"id_marca\", \"nombre\" FROM SGC1.\"modelos_bluebook\" WHERE \"id\"=%s",
        id
      );
      ResultSet rs = connection.executeDataSet(query);
      while(rs.next()){
        return new ModeloBluebook(
            rs.getInt("id"), 
            (new MarcaBluebookRepository()).get(rs.getInt("id_marca")), 
            rs.getString("nombre")
        );
      }
    } catch (SQLException e) {
      StringWriter errors = new StringWriter();
      e.printStackTrace(new PrintWriter(errors));
      throw new Exception(errors.toString());
    } finally {
      connection.closeConnection();
    }

    return null;
  }
}
