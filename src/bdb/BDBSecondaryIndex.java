package bdb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.util.concurrent.atomic.AtomicInteger;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;
import common.Camera;

public class BDBSecondaryIndex
{
 static long time;
 /**
  * @param args
  */
 public static void main(String[] args)
 {
  if( args.length > 0 )
  {
   FillBDB.dbDir = new File( new File(args[0]), "bdb");
  }
  
  
  EnvironmentConfig envConfig = new EnvironmentConfig();
  
  envConfig.setAllowCreate(false);
  Environment myDbEnvironment = new Environment(FillBDB.dbDir, envConfig);


  // Open the database. Create it if it does not already exist.
  DatabaseConfig dbConfig = new DatabaseConfig();
  dbConfig.setAllowCreate(false);
//  dbConfig.setDeferredWrite(true);
  
  Database  myDatabase = myDbEnvironment.openDatabase(null, 
                                            "test", 
                                            dbConfig); 
  
  SecondaryConfig ccfg = new SecondaryConfig();
  ccfg.setAllowCreate(true);
  ccfg.setKeyCreator( new ByCityKeyCreator() );
  ccfg.setSortedDuplicates(true);
  
  time = System.currentTimeMillis();

  
  SecondaryDatabase mySecDb = myDbEnvironment.openSecondaryDatabase(null, "byCity", myDatabase, ccfg);
  
  mySecDb.close();
  myDatabase.close();
  myDbEnvironment.close();
  
  System.out.println("Time: "+(System.currentTimeMillis()-time));
 }

 static class ByCityKeyCreator implements SecondaryKeyCreator
 {
  AtomicInteger count= new AtomicInteger( 0 );
  
  @Override
  public boolean createSecondaryKey(SecondaryDatabase arg0, DatabaseEntry k, DatabaseEntry v, DatabaseEntry r)
  {
   ByteArrayInputStream bais = new ByteArrayInputStream(v.getData());
   
   Camera c = null;
   
   try
   {
    ObjectInputStream ois = new ObjectInputStream( bais );
    c = (Camera)ois.readObject();
   }
   catch(Exception e)
   {
    return false;
   }
   
   r.setData(c.getCity().getBytes());
   
   int cnt = count.getAndIncrement();
   if( cnt % 10000 == 0 )
    System.out.println("Rec "+cnt+" ("+(cnt*1000.0/(System.currentTimeMillis()-time))+"rec/s)");

   return true;
  }
  
 }
 
}
