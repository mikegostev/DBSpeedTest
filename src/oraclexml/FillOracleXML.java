package oraclexml;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.xdb.XMLType;


public class FillOracleXML
{
 public static final int RECORDS = 15_000;

 // SSD: Fill: 1754rec/s Q: 126rec/s
 
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

//  stmt.executeUpdate("drop table placex");
//  stmt.executeUpdate("drop SEQUENCE placexseq");

//  stmt.executeUpdate("begin\n execute immediate 'drop table place';\n exception when others then null;\n end;");
//  stmt.executeUpdate("begin\n execute immediate 'drop table log';\n exception when others then null;\n end;");
  
  stmt.executeUpdate("CREATE TABLE placex (id INT NOT NULL PRIMARY KEY, city VARCHAR(255) NOT NULL, country VARCHAR(255) NOT NULL, data XMLType)");

  stmt.executeUpdate("CREATE SEQUENCE placexseq MINVALUE 1 START WITH 1 INCREMENT BY 1 CACHE 10");

  
 
  PreparedStatement plcStmt = conn.prepareStatement("INSERT INTO placex (id,country,city,data) VALUES(placexseq.nextval,?,?,?)");
  
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
  
   String xml="<data><hash>hash-"+i+"-1</hash><hash>hash-"+i+"-2</hash><hash>hash-"+i+"-3</hash></data>";
   
   XMLType xt = XMLType.createXML(conn, xml);
   
   plcStmt.setObject(3, xt);
   
   plcStmt.executeUpdate();
   
  
  }
  
  conn.close();

  System.out.println("Time: "+(System.currentTimeMillis()-tm));
 }


}
