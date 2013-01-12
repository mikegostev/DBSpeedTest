package common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParallelSearcher extends Thread
{
 private final BlockingQueue<byte[]> queue;
 private final Collection<Camera> res;
 private final Matcher mtch;
 
 public ParallelSearcher( String m,  BlockingQueue<byte[]> q, Collection<Camera> r )
 {
  queue = q;
  res = r;
  mtch = Pattern.compile( m ).matcher("");
 }

 @Override
 public void run()
 {
  while(true)
  {
   try
   {
    byte[] bytes = queue.take();

    if(bytes.length == 0)
    {
     queue.put(bytes);
     return;
    }
    
//    if( true )
//    continue;
    
    Camera cam = read(bytes);

    for(LogRecord lr : cam.getLog())
    {
     mtch.reset(lr.getEventHash());

     if(mtch.find())
     {
      res.add(cam);
      break;
     }
    }
   }
   catch(InterruptedException e)
   {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
   catch(ClassNotFoundException e)
   {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
   catch(IOException e)
   {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
  }
 }
 
 private static Camera read(byte[] camBuf) throws IOException, ClassNotFoundException
 {
  ByteArrayInputStream bais = new ByteArrayInputStream(camBuf);
  
  ObjectInputStream ois = new ObjectInputStream( bais );
  
  Camera c = (Camera)ois.readObject();
  
  return c;
 }

}
