package bdb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DiskOrderedCursor;
import com.sleepycat.je.DiskOrderedCursorConfig;
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
 final static int nThreads = 4;

 
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

  long tm = System.currentTimeMillis();

  DiskOrderedCursorConfig docc = new DiskOrderedCursorConfig();
  DiskOrderedCursor myCursor = myDatabase.openCursor(docc);
  
  DatabaseEntry foundKey = new DatabaseEntry();
  DatabaseEntry foundData = new DatabaseEntry();

  ArrayList< Camera > res = new ArrayList<>();
  
  int i = 0;
  
//  final DisruptorQueue<byte[]> queue = new DisruptorQueue<byte[]>(5,  new SingleThreadedClaimStrategy(8),
//    new SleepingWaitStrategy());
//  final BlockingQueue<byte[]> queue = new  MyLinkedBlockingQueue<byte[]>(10);

//  BlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(10, false);
  BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
  
  Thread[] thrds = new Thread[nThreads];
  
  for( i=0; i<nThreads; i++ )
  {
   thrds[i] = new ParallelSearcher("sh-1000-2", queue, res);
   thrds[i].start();
  }

  i=0;
  
  long len = 0;
  
  while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
  {
   i++;
   
   if( i % 10000 == 0 )
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   
   byte[] data = foundData.getData();
   
   len+=data.length;
   
   try
   {
    queue.put( data );
   }
   catch(InterruptedException e)
   {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
  
  }
  

  try
  {
   queue.put( new byte[0] );
  }
  catch(InterruptedException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  
  myCursor.close();
  
  myDatabase.close();
  myDbEnvironment.close();
  
  tm=System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" Rate: "+(i/tm*1000)+" Len: "+len );
 
  for( Camera l : res )
  {
   System.out.println("Found country: "+l.getCountry()+" city: "+l.getCity());
  }
 }

 private static class HackedConcQueue<E> extends ConcurrentLinkedQueue<E> implements BlockingQueue<E>{

  @Override
  public void put(E event) throws InterruptedException {
      do {
          if (offer(event)) {
              return;
          }
          // Note interruption check after offer as spec says only done when blocking
          if (Thread.currentThread().isInterrupted()) {
              throw new InterruptedException();
          }
          Thread.yield();
      } while (true);
  }

  @Override
  public E take() throws InterruptedException {
      E result;
      do {
          result = poll();
          if (result != null) {
              return result;
          }
          // Note interruption check after poll as spec says only done when blocking
          if (Thread.currentThread().isInterrupted()) {
              throw new InterruptedException();
          }
          Thread.yield();
      } while (true);
  }

  @Override
  public int drainTo(Collection<? super E> arg0) {
      // TODO Auto-generated method stub
      return 0;
  }

  @Override
  public int drainTo(Collection<? super E> arg0, int arg1) {
      // TODO Auto-generated method stub
      return 0;
  }

  @Override
  public boolean offer(E arg0, long arg1, TimeUnit arg2)
          throws InterruptedException {
      // TODO Auto-generated method stub
      return false;
  }

  @Override
  public E poll(long arg0, TimeUnit arg1) throws InterruptedException {
      // TODO Auto-generated method stub
      return null;
  }

  @Override
  public int remainingCapacity() {
      // TODO Auto-generated method stub
      return 0;
  }
  
}

 
}
