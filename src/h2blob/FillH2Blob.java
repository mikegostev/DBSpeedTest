package h2blob;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import common.Camera;
import common.LogRecord;


public class FillH2Blob
{
 public static final int RECORDS = 10_000_000;

 // Fill EBI: 23800rec/s  Q: 1:48000rec/s 2:67000rec/s 3:66000rec/s
 
 public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
 {
  Class.forName("org.h2.Driver");
  // Setup the connection with the DB
  Connection conn = DriverManager
      .getConnection("jdbc:h2:t:/home/mike/data/h2blob/blob", "sa", "");
  
  Statement stmt = conn.createStatement();
  
  stmt.executeUpdate("DROP SCHEMA IF EXISTS blob");
  
  stmt.executeUpdate("CREATE SCHEMA blob");
  
  stmt.executeUpdate("SET SCHEMA blob");
  
//  stmt.close();
//  
//  stmt=conn.createStatement();
  
  stmt.executeUpdate("CREATE TABLE place (id integer not null auto_increment primary key, city varchar(255) not null, country varchar(255) not null, other blob)");


  PreparedStatement plcStmt = conn.prepareStatement("INSERT INTO place (country,city,other) VALUES(?,?,?)");
  
  long tm = System.currentTimeMillis();

  
  for( int i=0; i < RECORDS; i++)
  {
   if( i % 10000 == 0 )
   {
//    myDatabase.sync();
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   }

   Camera cam = new Camera();
   
   cam.setCity("city"+i);
   cam.setCountry("country"+(i/100));
   
   for( int j=0; j < 3; j++)
   {
    LogRecord lr = new LogRecord();
    
    lr.setTime(1111);
    lr.setEventHash("hash-"+i+"-"+j);
    
    cam.addLogRecord(lr);
   }
   
   plcStmt.setString(1,cam.getCountry());
   plcStmt.setString(2,cam.getCity());
  
   ByteArrayOutputStream baos = new ByteArrayOutputStream();
   ObjectOutputStream oos = new ObjectOutputStream( baos );
   
   oos.writeObject(cam);
  
   oos.close();
   
   plcStmt.setBytes(3, baos.toByteArray());

   
   plcStmt.executeUpdate();
   
  }
  
  conn.close();

  System.out.println("Time: "+(System.currentTimeMillis()-tm));
 }


}
