package bdb;

import java.io.File;
import java.io.IOException;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import common.StringUtils;

public class RandomSearchBDB
{
 static final int RUNS=10000;

 /**
  * @param args
  * @throws IOException 
  * @throws ParseException 
  */
 public static void main(String[] args) throws IOException
 {
  if( args.length > 0 )
  {
   FillBDB.dbDir = new File( new File(args[0]), "bdb");
  }

  
  EnvironmentConfig envConfig = new EnvironmentConfig();
  
  envConfig.setAllowCreate(true);

  Environment myDbEnvironment = new Environment(FillBDB.dbDir, envConfig);

  // Open the database. Create it if it does not already exist.
  DatabaseConfig dbConfig = new DatabaseConfig();
  dbConfig.setAllowCreate(true);
  
  Database  myDatabase = myDbEnvironment.openDatabase(null, 
                                            "test", 
                                            dbConfig); 

  DatabaseEntry theData = new DatabaseEntry();
  
  int dataSize = 0;
  
  long tm = System.currentTimeMillis();
  
  for( int i=0; i < RUNS; i++ )
  {
   long cn = (long)(Math.random()*FillBDB.CAMERAS);
   
   String qs =  "key"+cn;
   DatabaseEntry theKey = new DatabaseEntry(qs.getBytes("UTF-8"));

   if (myDatabase.get(null, theKey, theData, LockMode.DEFAULT) !=   OperationStatus.SUCCESS)
   {
    System.out.println("Key not found: "+qs);
    continue;
   }
   
   dataSize += theData.getData().length;
   
//   Camera cam = Camera.load( ByteBuffer.wrap(theData.getData()) );
//   
//   if( ! cam.getCity().equals("city"+i) )
//    System.out.println("Invalid result");

  }
  
  tm=System.currentTimeMillis()-tm;
  
  System.out.println("Data read: "+dataSize);
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(RUNS*1000/tm));

  myDatabase.close();
  myDbEnvironment.close();
 }

}
