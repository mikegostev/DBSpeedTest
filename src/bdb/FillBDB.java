package bdb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class FillBDB
{

 /**
  * @param args
  * @throws IOException 
  */
 // 20 000 000 -> 435s (45977rec/s) Q: 720 ( 25300rec/s ) (no ser 33333rec/s)
 // 20 000 000 -> 1351s Def1000 (14803rec/s) Q: 723
 
 public static void main(String[] args) throws IOException
 {
  EnvironmentConfig envConfig = new EnvironmentConfig();
  
  envConfig.setAllowCreate(true);
  Environment myDbEnvironment = new Environment(new File("x:/dev/bdb/"), 
                                    envConfig);

  // Open the database. Create it if it does not already exist.
  DatabaseConfig dbConfig = new DatabaseConfig();
  dbConfig.setAllowCreate(true);
//  dbConfig.setDeferredWrite(true);
  
  Database  myDatabase = myDbEnvironment.openDatabase(null, 
                                            "test", 
                                            dbConfig); 
  
  long tm = System.currentTimeMillis();

  for( int i=0; i<20000000; i++ )
  {
   
   if( i % 1000 == 0 )
   {
//    myDatabase.sync();
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   }
   
   DatabaseEntry theKey = new DatabaseEntry( ("key"+i).getBytes() );
   
   Place p = new Place();

   p.setCity("city"+i);
   p.setCountry("country"+(i/100));
   
   for( int j=0; j < 3; j++)
   {
    LogRecord l = new LogRecord();
    
    l.setFirstName("name"+i+"-"+j);
    l.setLastName("surname"+i+"-"+j);

    
    p.addLogRecord(l);
   }
   
   
   ByteArrayOutputStream baos = new ByteArrayOutputStream();
   ObjectOutputStream oos = new ObjectOutputStream(baos);
   
   oos.writeObject(p);
   
   oos.close();
   
   myDatabase.put(null, theKey, new DatabaseEntry(baos.toByteArray()));
  }
  
//  myDatabase.sync();

  
  myDatabase.close();
  myDbEnvironment.close();
  System.out.println("Time: "+(System.currentTimeMillis()-tm));

 }
 

}
