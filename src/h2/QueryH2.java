package h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryH2
{

 /**
  * @param args
  * @throws ClassNotFoundException 
  * @throws SQLException 
  */
 public static void main(String[] args) throws ClassNotFoundException, SQLException
 {
  Class.forName("org.h2.Driver");

  Connection conn = DriverManager
      .getConnection("jdbc:h2:n:/h2/carmen", "sa", "");
  
  Statement stmt = conn.createStatement();
  
  stmt.executeUpdate("SET SCHEMA carmen");
  
  long tm = System.currentTimeMillis();

  
  ResultSet rst = stmt.executeQuery("SELECT p.city FROM place p JOIN log l ON l.place_ref=p.id WHERE l.first_name REGEXP 'e1000-2'");

  while( rst.next() )
  {
   System.out.println("City: "+rst.getString(1));
  }
  
  tm = System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" ("+(FillH2.RECORDS/tm*1000)+"rec/s)");

 }

}
