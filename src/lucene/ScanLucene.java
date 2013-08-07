package lucene;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.store.FSDirectory;

import com.pri.util.StringUtils;
import common.Camera;
import common.LogRecord;

import config.Config;

public class ScanLucene
{

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
  
  IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(Config.basePath,"lucene")) );
  
  int len = reader.maxDoc();
  
  Visitor vis = new Visitor("hash-1000000-2");
  
  long tm = System.currentTimeMillis();

  for( int i=0; i < len; i++ )
   reader.document(i, vis);
 
  tm=System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(len/tm*1000));

 }

 static class Visitor extends StoredFieldVisitor
 {
  private final String match;
  private long len=0;
  
  public Visitor( String m )
  {
   match=m;
  }

  @Override
  public void binaryField(FieldInfo fieldInfo, byte[] value)
  {
   len+=value.length;
   
   Camera c = Camera.load( ByteBuffer.wrap(value) );
   
   for( LogRecord lr : c.getLog() )
   {
    if( match.equals(lr.getEventHash()) )
     System.out.println("Found: "+c.getCountry()+" "+c.getCity());
   }
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
