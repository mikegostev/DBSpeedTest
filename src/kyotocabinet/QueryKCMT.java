package kyotocabinet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import common.Camera;
import common.ParallelSearcher;

public class QueryKCMT
{
 /**
  * @param args
  * @throws IOException 
  * @throws ClassNotFoundException 
  */
 final static int nThreads = 3;

 
 public static void main(String[] args) throws IOException, ClassNotFoundException
 {
  DB db = new DB();

  if(!db.open(FillKC.file, DB.OREADER))
  {
   System.err.println("open error: " + db.error());
   return;
  }

  // traverse records
  Cursor cur = db.cursor();
  cur.jump();
  byte[][] rec;

  
  long tm = System.currentTimeMillis();


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
  
  while ( (rec=cur.get(true) ) != null )
  {
   i++;
   
   if( i % 10000 == 0 )
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   
   byte[] data = rec[1];
   
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
  
  cur.disable();

  // close the database
  if(!db.close()){
    System.err.println("close error: " + db.error());
   return;
  }
  
  tm=System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" Rate: "+(i/tm*1000)+" Len: "+len );
 
  for( Camera l : res )
  {
   System.out.println("Found country: "+l.getCountry()+" city: "+l.getCity());
  }
 }

}
