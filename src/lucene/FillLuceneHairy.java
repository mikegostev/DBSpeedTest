package lucene;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import common.HairyAttribute;
import common.HairyObject;
import common.HairyObjectFactory;
import common.StringUtils;

import config.Config;

public class FillLuceneHairy
{
 public static long OBJECTS = 1_000_000_000;


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
  
  Directory dir = NIOFSDirectory.open(new File(Config.basePath,"luceneHairy") );
  Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
  IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_44, analyzer);

  iwc.setOpenMode(OpenMode.CREATE);

  IndexWriter writer = new IndexWriter(dir, iwc);
  
  LinkedBlockingQueue<Document> q = new LinkedBlockingQueue<>(10);
  
  ObjGen gen = new ObjGen(q);
  
  long tm = System.currentTimeMillis();

  for( long i=1; i <= OBJECTS; i++ )
  {
   Document doc;
   
   
   while( true )
   {
    try
    {
     doc = q.take();
     break;
    } 
    catch (InterruptedException e)
    {
    }
   }
   
   writer.addDocument(doc);

   if( (i % 100000) == 0)
    System.out.println("Done: "+i+" ("+100L*i/OBJECTS+"%) ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   
  }
  
  writer.close();
  
  gen.enough();
  
  tm=System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(OBJECTS/tm*1000));
 }

 static class ObjGen extends Thread
 {
  BlockingQueue<Document> queue;
  volatile boolean enough=false;
  HairyObjectFactory fact = new HairyObjectFactory(1000, 10);

  ObjGen(BlockingQueue<Document> q)
  {
   queue = q;
   start();
  }
  
  void enough()
  {
   enough = true;
   this.interrupt();
  }
  
  public void run()
  {
   while( ! enough )
   {
    HairyObject obj = fact.getNextObject();

    Document doc = new Document();
    
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream(4000);
    ObjectOutputStream oos;
    try
    {
     oos = new ObjectOutputStream( baos );
     oos.writeObject(obj);
     
     oos.close();
    } catch (IOException e1)
    {
     // TODO Auto-generated catch block
     e1.printStackTrace();
    }
    
    
    byte[] data = baos.toByteArray();

    for(HairyAttribute at : obj.getAttributes() )
    {
     switch (at.getCls().getType())
     {
     case STRING:
      
      doc.add( new StringField(at.getCls().getName(), at.getValue().toString(), Field.Store.NO));
      
      break;
      
     case INT:

      doc.add( new IntField(at.getCls().getName(), (Integer)at.getValue(), Field.Store.NO));

      break;

     case BOOLEAN:

      doc.add( new IntField(at.getCls().getName(), ((Boolean)at.getValue())?1:0, Field.Store.NO));

      break;

     case REAL:

      doc.add( new DoubleField(at.getCls().getName(), (Double)at.getValue(), Field.Store.NO));

      break;
  
     default:
      break;
     }
    }

    doc.add(new StoredField("_data", new BytesRef(data,0,data.length)));

    
    try
    {
     queue.put(doc);
    } 
    catch (Exception e)
    {
     if( enough )
      return;
    }
    
   }
  }
  
 }
}
