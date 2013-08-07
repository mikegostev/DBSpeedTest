package bdb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import common.Camera;
import common.DisruptorSearcher;
import common.ValueEvent;

public class QueryBDBDisruptor
{
 static String personName = "name500000-2";
 /**
  * @param args
  * @throws IOException 
  * @throws ClassNotFoundException 
  */
 final static int nThreads = 3;

 
 @SuppressWarnings("unchecked")
 public static void main(String[] args) throws IOException, ClassNotFoundException
 {
  if( args.length > 0 )
  {
   FillBDB.dbDir = new File( new File(args[0]), "bdb");
  }

  EnvironmentConfig envConfig = new EnvironmentConfig();
  
  envConfig.setAllowCreate(true);
  Environment myDbEnvironment = new Environment(new File("/home/mike/data/bdb/"), 
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
  
  DisruptorSearcher handler = new DisruptorSearcher("sh-1000-2", res);
  
  ExecutorService exec = Executors.newFixedThreadPool(6);
  
  Disruptor<ValueEvent> disruptor =
    new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY, exec, 
                              new SingleThreadedClaimStrategy(8),
                              new SleepingWaitStrategy());
  
  disruptor.handleEventsWith(handler,
    new DisruptorSearcher("sh-1000-2", res),
    new DisruptorSearcher("sh-1000-2", res));
  
  RingBuffer<ValueEvent> ringBuffer = disruptor.start();

  i=0;
  
  while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
  {
   i++;
   
   if( i % 10000 == 0 )
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   
   long sequence = ringBuffer.next();
   ValueEvent event = ringBuffer.get(sequence);

   event.setValue(foundData.getData());

//    make the event available to EventProcessors
   ringBuffer.publish(sequence);   

  
  }
  
  disruptor.shutdown();
  exec.shutdown();
  
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
