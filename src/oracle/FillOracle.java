package oracle;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class FillOracle
{
 public static final int RECORDS = 1_000_000;

 // SSD: Fill: 778rec/s Q: 253000rec/s
 
 public static void main(String[] args) throws ClassNotFoundException, SQLException
 {
  Class.forName("oracle.jdbc.OracleDriver");
  // Setup the connection with the DB
  Connection conn = DriverManager
      .getConnection("jdbc:oracle:thin:@localhost:1521/orcl", "mike", "mike");
  
  Statement stmt = conn.createStatement();
  
//  stmt.executeUpdate("DROP SCHEMA IF EXISTS carmen");
//  
//  stmt.executeUpdate("CREATE SCHEMA carmen");
//  
//  stmt.executeUpdate("USE carmen");
  
// stmt.close();
//
// stmt=conn.createStatement();
  
  stmt.executeUpdate("ALTER SESSION SET CURRENT_SCHEMA=MIKE");

  stmt.executeUpdate("drop table log");
  stmt.executeUpdate("drop table place");
  stmt.executeUpdate("drop SEQUENCE placeseq");
  stmt.executeUpdate("drop SEQUENCE logseq");

//  stmt.executeUpdate("begin\n execute immediate 'drop table place';\n exception when others then null;\n end;");
//  stmt.executeUpdate("begin\n execute immediate 'drop table log';\n exception when others then null;\n end;");
  
  stmt.executeUpdate("CREATE TABLE place (id INT NOT NULL PRIMARY KEY, city VARCHAR(255) NOT NULL, country VARCHAR(255) NOT NULL)");

  stmt.executeUpdate("CREATE SEQUENCE placeseq MINVALUE 1 START WITH 1 INCREMENT BY 1 CACHE 10");
  stmt.executeUpdate("CREATE SEQUENCE logseq MINVALUE 1 START WITH 1 INCREMENT BY 1 CACHE 10");

  
  stmt.executeUpdate("CREATE TABLE log (id integer not null primary key, place_ref int not null," +
   " first_name varchar(255) not null, last_name varchar(255) not null, foreign key (place_ref) references place (id) )");
  
  
  PreparedStatement plcStmt = conn.prepareStatement("INSERT INTO place (id,country,city) VALUES(placeseq.nextval,?,?)",new int[] {1});
  PreparedStatement logStmt = conn.prepareStatement("INSERT INTO log (id,place_ref,first_name,last_name) VALUES(logseq.nextval,?,?,?)");
  
  long tm = System.currentTimeMillis();

  
  for( int i=0; i < RECORDS; i++)
  {
   if( i % 1000 == 0 )
   {
// myDatabase.sync();
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   }

   
   plcStmt.setString(1,"country"+(i/100));
   plcStmt.setString(2,"city"+i);
  
   plcStmt.executeUpdate();
   
   ResultSet rs = plcStmt.getGeneratedKeys();

   int id = 0;
   BigDecimal rid;
   if(rs.next())
   {
	rid = (BigDecimal) rs.getObject(1);
	id = rid.intValue();
   }
   
   rs.close();
   
   for( int j=0; j < 3; j++)
   {
    logStmt.setInt(1, (int)id);
    logStmt.setString(2, "name"+i+"-"+j);
    logStmt.setString(3, "surname"+i+"-"+j);
    
    logStmt.executeUpdate();
   }

   
  }
  
  conn.close();

  System.out.println("Time: "+(System.currentTimeMillis()-tm));
 }


}
