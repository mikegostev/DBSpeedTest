package segfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import com.pri.util.StringUtils;
import common.Camera;
import common.ParallelSearcher;

public class QueryMTSegFile
{
 final static int nThreads = 2;


 public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException
 {
  final ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(10);
  
  File f = new File(FillSegFile.file);

  ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f));

  
  final ArrayList<Camera> res = new ArrayList<>();
  
  long tm = System.currentTimeMillis();
  
  int n=0;
  
  Thread[] thrds = new Thread[nThreads];
  
  for( int i=0; i<nThreads; i++ )
  {
   thrds[i] = new ParallelSearcher("sh-1000-2", queue, res);
   thrds[i].start();
  }
  
  while( true )
  {
   byte camBuf[] = null;
   
   try
   {
    int bufLen = ois.readInt();
    camBuf = new byte[bufLen];
    ois.readFully(camBuf);
    queue.put(camBuf);
   }
   catch( Exception e )
   {
    try
    {
     queue.put( new byte[0] );
    }
    catch(InterruptedException e1)
    {
     // TODO Auto-generated catch block
     e1.printStackTrace();
    }
    
    e.printStackTrace();
    
    ois.close();
    break;
   }
   

   
   n++;
   
   if( n%10000 == 0 )
   {
    System.out.println("Processed : "+n+" Rate: "+(n/(System.currentTimeMillis()-tm)*1000)+"rec/s");
   }

  }
  
 
  try
  {
   for( int i=0; i<nThreads; i++ )
    thrds[i].join();
  }
  catch(InterruptedException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  
  tm = System.currentTimeMillis() - tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(n/tm*1000));

  for( Camera c : res )
   System.out.println("Found: "+c.getId()+" in "+c.getCountry()+", "+c.getCity());
  
 }


}
