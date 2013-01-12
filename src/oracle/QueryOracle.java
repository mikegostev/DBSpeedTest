package oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryOracle
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

  
  ResultSet rst = stmt.executeQuery("SELECT p.city FROM place p JOIN log l ON l.place_ref=p.id WHERE REGEXP_LIKE(l.first_name, 'e1000-2')");

  while( rst.next() )
  {
   System.out.println("City: "+rst.getString(1));
  }
  
  tm = System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" ("+(FillOracle.RECORDS/tm*1000)+"rec/s)");

 }

}
