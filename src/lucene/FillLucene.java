package lucene;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import com.pri.util.StringUtils;
import common.Camera;
import common.LogRecord;

import config.Config;

public class FillLucene
{
 public static int CAMERAS = 10_000_000;
 static final int nRec = 3;
 
 /**
  * @param args
  * @throws IOException 
  */
 public static void main(String[] args) throws IOException
 {
  Directory dir = FSDirectory.open(new File(Config.basePath,"lucene") );
  Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
  IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_40, analyzer);

  iwc.setOpenMode(OpenMode.CREATE);

  IndexWriter writer = new IndexWriter(dir, iwc);
  
  byte[] outbuf = new byte[4096];
  
  ByteBuffer buf = ByteBuffer.wrap(outbuf);
  
  long tm = System.currentTimeMillis();

  for( int i=0; i < CAMERAS; i++ )
  {
   Document doc = new Document();
   
   Camera p = new Camera();

   p.setCity("city"+i);
   p.setCountry("country"+(i/100));
   p.setId(p.getCountry()+p.getCity());
   
   for( int j=1; j <= nRec; j++ )
   {
    LogRecord lr = new LogRecord();
    
    lr.setTime(1111);
    lr.setEventHash("hash-"+i+"-"+j);
    
    p.addLogRecord( lr );
   }
   
   buf.clear();
   Camera.save(buf, p);
   
   Field cityField = new StringField("city", p.getCity(), Field.Store.YES);
   doc.add(cityField);
   
   Field countryField = new StringField("country", p.getCountry(), Field.Store.YES);
   doc.add(countryField);
   
//   Field dataField = new StraightBytesDocValuesField("data", new BytesRef(outbuf,0,buf.position()));
//   doc.add(dataField);
   Field dataField = new StoredField("data", new BytesRef(outbuf,0,buf.position()));
   doc.add(dataField);
   
   writer.addDocument(doc);

  }
  
  writer.close();
  
  tm=System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(CAMERAS/tm*1000));
 }

}
