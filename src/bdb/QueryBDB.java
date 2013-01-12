package bdb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

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
  EnvironmentConfig envConfig = new EnvironmentConfig();
  
  envConfig.setAllowCreate(true);
  Environment myDbEnvironment = new Environment(FillBDB.dbDir, 
                                    envConfig);

  // Open the database. Create it if it does not already exist.
  DatabaseConfig dbConfig = new DatabaseConfig();
  dbConfig.setAllowCreate(true);
  
  Database  myDatabase = myDbEnvironment.openDatabase(null, 
                                            "test", 
                                            dbConfig); 
  
//  myDatabase.preload(1024*1024);

  long tm = System.currentTimeMillis();

  Cursor myCursor = myDatabase.openCursor(null, null);
  
  DatabaseEntry foundKey = new DatabaseEntry();
  DatabaseEntry foundData = new DatabaseEntry();

  ArrayList< Place > res = new ArrayList<>();
  
  int i = 0;
  
  while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
  {
   i++;
   
   if( i % 1000 == 0 )
   {
//    myDatabase.sync();
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   }
   
//   if( foundData.getData().length > 0 )
//    continue;
   
   byte[] data  = foundData.getData();
   
   if( true )
    continue;
   
   ByteArrayInputStream bais = new ByteArrayInputStream(data);
   ObjectInputStream ois = new ObjectInputStream( bais );
   
   Place p = (Place)ois.readObject();
   
   ois.close();
   
   for( LogRecord lr : p.getLog() )
   {
    if( lr.getFirstName().equals(personName) )
    {
     res.add(p);
     break;
    }
   }
   



   
  }
  
  myCursor.close();
  
  myDatabase.close();
  myDbEnvironment.close();
  System.out.println("Time: "+(System.currentTimeMillis()-tm));
 
  for( Place l : res )
  {
   System.out.println("Found country: "+l.getCountry()+" city: "+l.getCity());
  }
 }

}
