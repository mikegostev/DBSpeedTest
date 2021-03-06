package lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.store.NIOFSDirectory;

import common.Camera;
import common.ParallelSearcherEx;
import common.StringUtils;

import config.Config;

public class ScanLuceneMT
{
 final static int nThreads = 3;

 /**
  * @param args
  * @throws IOException 
  */
 public static void main(String[] args) throws IOException
 {
  if( args.length > 0 )
  {
   Config.basePath = new File(args[0]);
  }
  
  IndexReader reader = DirectoryReader.open(NIOFSDirectory.open(new File(Config.basePath,"lucene")) );
 
  int len = reader.maxDoc();
  
  ArrayList< Camera > res = new ArrayList<>();
  
  BlockingQueue<byte[]> queue = new LinkedBlockingQueue<byte[]>(100);
//  BlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(10, false);
  
  Thread[] thrds = new Thread[nThreads];
  
  int i;
  
  for( i=0; i<nThreads; i++ )
  {
   thrds[i] = new ParallelSearcherEx("sh-1000000-2", queue, res);
   thrds[i].start();
  }
  
  Visitor vis = new Visitor(queue);
  
  long tm = System.currentTimeMillis();

  for( i=0; i < len; i++ )
  {
   if( (i % 100000) == 0)
    System.out.println("Done: "+i+" ("+100L*i/len+"%) ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");

   reader.document(i, vis);
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
  
  tm=System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(len/tm*1000));

  for( Camera l : res )
  {
   System.out.println("Found country: "+l.getCountry()+" city: "+l.getCity());
  }
  
 }

 static class Visitor extends StoredFieldVisitor
 {
  private final BlockingQueue<byte[]> queue;
  private long len=0;
  
  public Visitor( BlockingQueue<byte[]> q )
  {
   queue=q;
  }

  @Override
  public void binaryField(FieldInfo fieldInfo, byte[] value)
  {
   len+=value.length;
   
   try
   {
    queue.put(value);
   }
   catch(InterruptedException e)
   {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
   
//   Camera c = Camera.load( ByteBuffer.wrap(value) );
//   
//   for( LogRecord lr : c.getLog() )
//   {
//    if( match.equals(lr.getEventHash()) )
//     System.out.println("Found: "+c.getCountry()+" "+c.getCity());
//   }
  }
  
  @Override
  public Status needsField(FieldInfo arg0) throws IOException
  {
//   System.out.println(arg0.name);
   
   if( arg0.name.equals("data") )
    return Status.YES;
   
   return Status.NO;
  }
  
 }
 
}
