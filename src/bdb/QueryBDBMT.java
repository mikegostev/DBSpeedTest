package bdb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import common.Camera;
import common.ParallelSearcher;

public class QueryBDBMT
{
 static String personName = "name500000-2";
 /**
  * @param args
  * @throws IOException 
  * @throws ClassNotFoundException 
  */
 final static int nThreads = 2;

 
 public static void main(String[] args) throws IOException, ClassNotFoundException
 {
  EnvironmentConfig envConfig = new EnvironmentConfig();
  
  envConfig.setAllowCreate(true);
  Environment myDbEnvironment = new Environment(new File("t:/bdb/"), 
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

  ArrayList< Camera > res = new ArrayList<>();
  
  int i = 0;
  
  final ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(10);

  Thread[] thrds = new Thread[nThreads];
  
  for( i=0; i<nThreads; i++ )
  {
   thrds[i] = new ParallelSearcher("sh-1000-2", queue, res);
   thrds[i].start();
  }

  i=0;
  
  while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
  {
   i++;
   
   if( i % 10000 == 0 )
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   
   try
   {
    queue.put(foundData.getData());
   }
   catch(InterruptedException e)
   {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
  
  }
  
  myCursor.close();
  
  myDatabase.close();
  myDbEnvironment.close();
  System.out.println("Time: "+(System.currentTimeMillis()-tm));
 
  for( Camera l : res )
  {
   System.out.println("Found country: "+l.getCountry()+" city: "+l.getCity());
  }
 }

}
