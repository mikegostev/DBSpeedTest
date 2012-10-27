package h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class FillH2
{
 public static final int RECORDS = 1_000_000;

 // Fill EBI: 7576rec/s  Q: 207000rec/s
 // SSD F: 37429 Q: 70000
 
 public static void main(String[] args) throws ClassNotFoundException, SQLException
 {
  Class.forName("org.h2.Driver");
  // Setup the connection with the DB
  Connection conn = DriverManager
      .getConnection("jdbc:h2:c:/data/h2", "sa", "");
  
  Statement stmt = conn.createStatement();
  
  stmt.executeUpdate("DROP SCHEMA IF EXISTS carmen");
  
  stmt.executeUpdate("CREATE SCHEMA carmen");
  
  stmt.executeUpdate("SET SCHEMA carmen");
  
//  stmt.close();
//  
//  stmt=conn.createStatement();
  
  stmt.executeUpdate("CREATE TABLE place (id integer not null auto_increment primary key, city varchar(255) not null, country varchar(255) not null)");

  stmt.executeUpdate("CREATE TABLE log (id integer not null auto_increment primary key, place_ref int not null," +
  		" first_name varchar(255) not null, last_name varchar(255) not null, foreign key (place_ref) references place (id) )");
  
  
  PreparedStatement plcStmt = conn.prepareStatement("INSERT INTO place (country,city) VALUES(?,?)");
  PreparedStatement logStmt = conn.prepareStatement("INSERT INTO log (place_ref,first_name,last_name) VALUES(?,?,?)");
  
  long tm = System.currentTimeMillis();

  
  for( int i=0; i < RECORDS; i++)
  {
   if( i % 1000 == 0 )
   {
//    myDatabase.sync();
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   }

   
   plcStmt.setString(1,"country"+(i/100));
   plcStmt.setString(2,"city"+i);
  
   plcStmt.executeUpdate();
   
   ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");

   int id = 0;

   if(rs.next())
    id = rs.getInt(1);
   
   rs.close();
   
   for( int j=0; j < 3; j++)
   {
    logStmt.setInt(1, id);
    logStmt.setString(2, "name"+i+"-"+j);
    logStmt.setString(3, "surname"+i+"-"+j);
    
    logStmt.executeUpdate();
   }

   
  }
  
  conn.close();

  System.out.println("Time: "+(System.currentTimeMillis()-tm));
 }


}
