package common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lmax.disruptor.EventHandler;

public class DisruptorSearcher implements EventHandler<ValueEvent>
{
 private final Collection<Camera> res;
 private final Matcher mtch;
 
 public DisruptorSearcher( String m, Collection<Camera> r )
 {
  res = r;
  mtch = Pattern.compile( m ).matcher("");
 }
 
 public void onEvent(final ValueEvent event, final long sequence, final boolean endOfBatch) throws Exception
 {
  Camera cam = read(event.getValue());

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

 private static Camera read(byte[] camBuf) throws IOException, ClassNotFoundException
 {
  ByteArrayInputStream bais = new ByteArrayInputStream(camBuf);

  ObjectInputStream ois = new ObjectInputStream(bais);

  Camera c = (Camera) ois.readObject();

  return c;
 }
}
