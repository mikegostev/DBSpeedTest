package lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.pri.util.StringUtils;

import config.Config;

public class ScanLucene
{

 /**
  * @param args
  * @throws IOException 
  */
 public static void main(String[] args) throws IOException
 {
  IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(Config.basePath,"lucene")) );
  Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
  
  IndexSearcher searcher = new IndexSearcher(reader);
 
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
