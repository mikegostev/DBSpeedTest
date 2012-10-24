package mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryMySQL
{

 /**
  * @param args
  * @throws ClassNotFoundException 
  * @throws SQLException 
  */
 public static void main(String[] args) throws ClassNotFoundException, SQLException
 {
  Class.forName("com.mysql.jdbc.Driver");

  Connection conn = DriverManager
      .getConnection("jdbc:mysql://localhost/?"
          + "user=root");
  
  Statement stmt = conn.createStatement();
  
  stmt.executeUpdate("USE carmen");
  
  long tm = System.currentTimeMillis();

  
  ResultSet rst = stmt.executeQuery("SELECT p.city FROM place p JOIN log l ON l.place_ref=p.id WHERE l.first_name REGEXP 'e1000-2'");

  while( rst.next() )
  {
   System.out.println("City: "+rst.getString(1));
  }
  
  tm = System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" ("+(FillMySQL.RECORDS/tm*1000)+"rec/s)");

 }

}
