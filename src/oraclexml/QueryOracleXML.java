package oraclexml;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryOracleXML
{

 /**
* @param args
* @throws ClassNotFoundException
* @throws SQLException
*/
 public static void main(String[] args) throws ClassNotFoundException, SQLException
 {
  Class.forName("oracle.jdbc.OracleDriver");
  // Setup the connection with the DB
  Connection conn = DriverManager
      .getConnection("jdbc:oracle:thin:@localhost:1521/orcl", "mike", "mike");
  
  Statement stmt = conn.createStatement();
  
  stmt.executeUpdate("ALTER SESSION SET CURRENT_SCHEMA=MIKE");
  
  long tm = System.currentTimeMillis();

  
  ResultSet rst = stmt.executeQuery("SELECT p.city FROM placex p, XMLTable(' for $i in /data/hash where ora:matches($i/text(),\"hash-555-2\") return $i'" +
  		" PASSING data)");

  System.out.println("Cols="+rst.getMetaData().getColumnCount());
  
  while( rst.next() )
  {
   System.out.println("City: "+rst.getString(1));
  }
  
  tm = System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" ("+(FillOracleXML.RECORDS*1000/tm)+"rec/s)");

 }

}
