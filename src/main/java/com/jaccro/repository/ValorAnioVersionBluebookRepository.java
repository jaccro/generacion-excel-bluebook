package com.jaccro.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jaccro.connection.DBConnection;
import com.jaccro.model.MarcaBluebook;
import com.jaccro.model.ModeloBluebook;
import com.jaccro.model.ValorAnioVersionBluebook;
import com.jaccro.model.VersionBluebook;
import com.jaccro.util.ExceptionUtil;

public class ValorAnioVersionBluebookRepository {
  
  public List<Integer> listDistinctYear() throws Exception {
    DBConnection connection = null;
    List<Integer> list = new ArrayList<>();

    try {
      connection = new DBConnection();
      String query = String.format("SELECT DISTINCT \"anio\"  FROM SGC1.\"valores_anios_version_bluebook\"" );
      ResultSet rs = connection.executeDataSet(query);
      while(rs.next()){
        list.add(rs.getInt("anio"));
      }
    } catch (SQLException e) {
      throw new Exception(ExceptionUtil.stacktraceToString(e));
    } finally {
      connection.closeConnection();
    }

    return list;
  }

  public List<ValorAnioVersionBluebook> listByVersion(int idVersion) throws Exception {
    List<ValorAnioVersionBluebook> lista = new ArrayList<>();
    DBConnection connection = null;
    
    try {
      connection = new DBConnection();
      String query = String.format("SELECT \"id\", \"anio\", \"valor_referencial\" FROM SGC1.\"valores_anios_version_bluebook\" "
          + "WHERE \"id_version\"=%s",
          idVersion
      );
      ResultSet rs = connection.executeDataSet(query);
      while(rs.next()){
        lista.add( new ValorAnioVersionBluebook(rs.getInt("id"), rs.getInt("anio"), rs.getDouble("valor_referencial")) );
      }
    } catch (SQLException e) {
      throw new Exception(ExceptionUtil.stacktraceToString(e));
    } finally {
      connection.closeConnection();
    }

    return lista;
  }

  public List<ValorAnioVersionBluebook> listAll() throws Exception {
    List<ValorAnioVersionBluebook> lista = new ArrayList<>();
    DBConnection connection = null;
    
    try {
      connection = new DBConnection();
      String query = String.format("SELECT t1.\"id\", t1.\"anio\", t1.\"valor_referencial\", "
          + "t1.\"id_version\", t2.\"codigo\", t2.\"nombre\" AS \"nombre_version\", t2.\"anio_lanzamiento\", "
          + "t2.\"anio_vigencia\", t2.\"valor_referencial\", t2.\"activo\", "
          + "t2.\"id_modelo\", t3.\"nombre\" AS \"nombre_modelo\", t3.\"id_marca\" "
          + "FROM SGC1.\"valores_anios_version_bluebook\" AS t1 INNER JOIN SGC1.\"versiones_bluebook\" AS t2 "
          + "ON t1.\"id_version\" = t2.\"id\" "
          + "INNER JOIN SGC1.\"modelos_bluebook\" t3 "
          + "ON t2.\"id_modelo\" = t3.\"id\" "
          + "ORDER BY \"id_marca\", \"id_modelo\", \"id_version\", \"anio\" "
      );
      ResultSet rs = connection.executeDataSet(query);
      while(rs.next()){
        lista.add( new ValorAnioVersionBluebook(
            rs.getInt("id"),
            (new VersionBluebook(
              rs.getInt("id_version"), 
              ( new ModeloBluebook(rs.getInt("id_modelo"), (new MarcaBluebook(rs.getInt("id_marca"), "")), rs.getString("nombre_modelo"))),
              rs.getString("codigo"), 
              rs.getString("nombre_version"), 
              rs.getInt("anio_lanzamiento"), 
              rs.getInt("anio_vigencia"), 
              rs.getDouble("valor_referencial"),
              rs.getBoolean("activo"))
            ), 
            rs.getInt("anio"), 
            rs.getDouble("valor_referencial")) 
        );
      }
    } catch (SQLException e) {
      throw new Exception(ExceptionUtil.stacktraceToString(e));
    } finally {
      connection.closeConnection();
    }

    return lista;
  }

  public ValorAnioVersionBluebook getByVersionAndAnio(int idVersion, int anio) throws Exception {
    DBConnection connection = null;
    
    try {
      connection = new DBConnection();
      String query = String.format("SELECT \"id\", \"anio\", \"valor_referencial\" FROM SGC1.\"valores_anios_version_bluebook\" "
          + "\"id_version\"=%s AND \"anio\"=%s",
          idVersion,
          anio
      );
      ResultSet rs = connection.executeDataSet(query);
      while(rs.next()){
        return new ValorAnioVersionBluebook(rs.getInt("id"), rs.getInt("anio"), rs.getDouble("valor_referencial"));
      }
    } catch (SQLException e) {
      throw new Exception(ExceptionUtil.stacktraceToString(e));
    } finally {
      connection.closeConnection();
    }

    return null;
  }
}
