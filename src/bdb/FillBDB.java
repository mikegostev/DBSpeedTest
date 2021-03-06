package bdb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import common.Camera;
import common.LogRecord;

import config.Config;

public class FillBDB
{
 public static File dbDir = new File(Config.basePath, "bdb/");

 static final int CAMERAS = 1_000_000_000;
 static final int nRec = 3;

 /**
  * @param args
  * @throws IOException 
  */
 // 20 000 000 -> 435s (45977rec/s) Q: 720 ( 25300rec/s ) (no ser 33333rec/s)
 // 20 000 000 -> 1351s Def1000 (14803rec/s) Q: 723
 
 
 public static void main(String[] args) throws IOException
 {
  if( args.length > 0 )
  {
   dbDir = new File( new File(args[0]), "bdb");
  }
  
  dbDir.mkdirs();
  
  EnvironmentConfig envConfig = new EnvironmentConfig();
  
  envConfig.setAllowCreate(true);
  Environment myDbEnvironment = new Environment(dbDir, envConfig);


  // Open the database. Create it if it does not already exist.
  DatabaseConfig dbConfig = new DatabaseConfig();
  dbConfig.setAllowCreate(true);
//  dbConfig.setDeferredWrite(true);
  
  Database  myDatabase = myDbEnvironment.openDatabase(null, 
                                            "test", 
                                            dbConfig); 
  
  byte[] outbuf = new byte[4096];
  
  ByteBuffer buf = ByteBuffer.wrap(outbuf);

  
  long tm = System.currentTimeMillis();

  for( int i=0; i<CAMERAS; i++ )
  {
   
   if( i % 10000 == 0 )
   {
//    myDatabase.sync();
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   }
   
   DatabaseEntry theKey = new DatabaseEntry( ("key"+i).getBytes() );
   
   Camera p = new Camera();

   p.setCity("city"+i);
   p.setCountry("country"+(i/100));
   p.setId(p.getCountry()+p.getCity());
   
   for( int j=1; j <= nRec; j++ )
   {
    LogRecord lr = new LogRecord();
    
    lr.setTime(1111);
    lr.setEventHash("hash-"+i+"-"+j);
    
    p.addLogRecord( lr );
   }

//   buf.clear();
//   Camera.save(buf, p);

   ByteArrayOutputStream baos = new ByteArrayOutputStream(4000);
   ObjectOutputStream oos = new ObjectOutputStream( baos );
   
   oos.writeObject(p);
   
   oos.close();
   
   
   myDatabase.put(null, theKey, new DatabaseEntry(baos.toByteArray()) );
  }
  
//  myDatabase.sync();

  
  myDatabase.close();
  myDbEnvironment.close();
  System.out.println("Time: "+(System.currentTimeMillis()-tm));

 }
 

}
