package com.jaccro.repository;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jaccro.connection.DBConnection;
import com.jaccro.model.ModeloBluebook;
import com.jaccro.model.VersionBluebook;
import com.jaccro.util.ExceptionUtil;

public class VersionBluebookRepository {
  
  public List<VersionBluebook> listByMarca(int idMarca) throws Exception {
    DBConnection connection = null;
    List<VersionBluebook> list = new ArrayList<>();

    try {
      connection = new DBConnection();
      String query = String.format(
        "SELECT t1.\"id\", t1.\"id_modelo\", t2.\"nombre\" AS \"nombre_modelo\", t1.\"codigo\", t1.\"nombre\", "
            + "t1.\"anio_lanzamiento\", t1.\"anio_vigencia\", t1.\"valor_referencial\", \"activo\" "
            + "FROM SGC1.\"versiones_bluebook\" AS t1 INNER JOIN SGC1.\"modelos_bluebook\" AS t2 "
            + "ON t1.\"id_modelo\" = t2.\"id\" "
            + "WHERE t2.\"id_marca\" = %s "
            + "ORDER BY t2.\"nombre\", t1.\"nombre\"",
        idMarca
      );
      ResultSet rs = connection.executeDataSet(query);
      while(rs.next()){
        list.add(new VersionBluebook(
            rs.getInt("id"), 
            (new ModeloBluebook(rs.getInt("id_modelo"), rs.getString("nombre_modelo"))),
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getInt("anio_lanzamiento"),
            rs.getInt("anio_vigencia"),
            rs.getDouble("valor_referencial"),
            rs.getBoolean("activo")
          )
        );
      }
    } catch (SQLException e) {
      throw new Exception(ExceptionUtil.stacktraceToString(e));
    } finally {
      connection.closeConnection();
    }

    return list;
  }

  public VersionBluebook get(int id) throws Exception {
    DBConnection connection = null;

    try {
      connection = new DBConnection();
      String query = String.format(
        "SELECT \"id\", \"id_modelo\", \"codigo\", \"nombre\", \"anio_lanzamiento\", \"anio_vigencia\", \"valor_referencial\", \"activo\" "
            + "WHERE \"id\"=%s",
        id
      );
      ResultSet rs = connection.executeDataSet(query);
      while(rs.next()){
        return new VersionBluebook(
          rs.getInt("id"), 
          (new ModeloBluebookRepository()).get(rs.getInt("id_modelo")), 
          rs.getString("codigo"),
          rs.getString("nombre"),
          rs.getInt("anio_lanzamiento"),
          rs.getInt("anio_vigencia"),
          rs.getDouble("valor_referencial"),
          rs.getBoolean("activo")
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
