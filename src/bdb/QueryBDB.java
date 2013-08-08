package bdb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DiskOrderedCursorConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import common.Camera;

public class QueryBDB
{
 static String personName = "name500000-2";
 /**
  * @param args
  * @throws IOException 
  * @throws ClassNotFoundException 
  */
 public static void main(String[] args) throws IOException, ClassNotFoundException
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
  
//  myDatabase.preload(1024*1024);

  
  DiskOrderedCursorConfig docc = new DiskOrderedCursorConfig();
  docc.setInternalMemoryLimit(1000000000);
  
  Cursor myCursor = myDatabase.openCursor(null, null);
  
  DatabaseEntry foundKey = new DatabaseEntry();
  DatabaseEntry foundData = new DatabaseEntry();

  ArrayList< Camera > res = new ArrayList<>();
  
  int i = 0;
  
  long len=0;

  long tm = System.currentTimeMillis();
  
  while (myCursor.getNext(foundKey, foundData, LockMode.READ_UNCOMMITTED) == OperationStatus.SUCCESS)
  {
   i++;
   
   if( i % 100000 == 0 )
   {
//    myDatabase.sync();
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   }
   
//   if( foundData.getData().length > 0 )
//    continue;
   int l = foundData.getData().length;
   
   if( l > 0 )
   {
	   len+=l;
	 //  continue;
   }
   
   byte[] data  = foundData.getData();
   
   if( true )
    continue;
   
   ByteArrayInputStream bais = new ByteArrayInputStream(data);
   ObjectInputStream ois = new ObjectInputStream( bais );
   
   Camera p = (Camera)ois.readObject();
   
   ois.close();
   
   for( common.LogRecord lr : p.getLog() )
   {
    if( lr.getEventHash().equals(personName) )
    {
     res.add(p);
     break;
    }
   }
   



   
  }
  
  myCursor.close();
  
  myDatabase.close();
  myDbEnvironment.close();
  
  tm = System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" Len: "+len+" Rate: "+(i/tm*1000));
 
  for( Camera l : res )
  {
   System.out.println("Found country: "+l.getCountry()+" city: "+l.getCity());
  }
 }

}
