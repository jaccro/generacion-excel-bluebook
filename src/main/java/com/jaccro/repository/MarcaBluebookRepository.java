package com.jaccro.repository;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jaccro.connection.DBConnection;
import com.jaccro.model.MarcaBluebook;

public class MarcaBluebookRepository {

  public List<MarcaBluebook> listAll() throws Exception {
    DBConnection connection = null;
    List<MarcaBluebook> list = new ArrayList<>();

    try {
      connection = new DBConnection();
      String query = String.format("SELECT \"id\", \"nombre\" FROM SGC1.\"marcas_bluebook\" "
          + "WHERE TRIM(\"nombre\") NOT IN ('-NO POSEE-', 'NO DEFINIDO', 'OTRO', 'Nueva marca', 'Nueva marca 2', 'Nueva marca 3', '?', '') "
          + "ORDER BY \"nombre\"" );
      ResultSet rs = connection.executeDataSet(query);
      while(rs.next()){
        list.add(new MarcaBluebook(rs.getInt("id"), rs.getString("nombre")));
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

  public MarcaBluebook get(int id) throws Exception {
    DBConnection connection = null;
    
    try {
      connection = new DBConnection();
      String query = String.format("SELECT \"id\", \"nombre\" FROM SGC1.\"marcas_bluebook\" WHERE \"id\"=%s ", id);
      ResultSet rs = connection.executeDataSet(query);
      while(rs.next()){
        return new MarcaBluebook(rs.getInt("id"), rs.getString("nombre"));
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
