package com.jaccro.connection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DBConnection {
  Connection connection = null;
  CallableStatement cmd = null;
  PreparedStatement statement;
  ResultSet rs = null;

  public DBConnection() throws SQLException {
    DataSource ds = DSCreator.getDataSource();
    this.connection = ds.getConnection();
  }

  public void closeConnection() throws SQLException {
    if (rs != null) {
      if (!rs.isClosed())
        rs.close();
    }
    
    if (connection != null)
      connection.close();
  }

  public void executeNonQuery(String query) throws SQLException {
    statement = connection.prepareStatement(query);
    statement.executeUpdate();
  }

  public ResultSet executeDataSet(String query) throws SQLException {
    statement = connection.prepareStatement(query);
    return statement.executeQuery();
  }

  public void executeProcedureNonQuery(String proc, Object[] args) throws SQLException {

    cmd = connection.prepareCall(proc);

    for (int i = 0; i < args.length; i++) {
      cmd.setObject(i + 1, args[i]);
    }
    cmd.executeUpdate();
  }

  public ResultSet executeProcedureDataSet(String proc, Object[] args) throws SQLException {
    cmd = connection.prepareCall(proc);

    for (int i = 0; i < args.length; i++) {
      cmd.setObject(i + 1, args[i]);
    }
    rs = cmd.executeQuery();
    return rs;
  }
}
