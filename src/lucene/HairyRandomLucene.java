package lucene;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import common.HairyObject;
import common.StringUtils;

import config.Config;

public class HairyRandomLucene
{
 static final int RUNS=1000;

 /**
  * @param args
  * @throws IOException 
  * @throws ParseException 
  */
 public static void main(String[] args) throws IOException, ParseException
 {
  if( args.length > 0 )
  {
   Config.basePath = new File(args[0]);
  }
  
  IndexReader reader = DirectoryReader.open(NIOFSDirectory.open(new File(Config.basePath,"luceneHairy")) );
  IndexSearcher searcher = new IndexSearcher(reader);
  Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
  
  QueryParser parser = new QueryParser(Version.LUCENE_44, "id", analyzer);
  
  long tm = System.currentTimeMillis();
  
  for( int i=0; i < RUNS; i++ )
  {
   long cn = (long)(Math.random()*FillLuceneHairy.OBJECTS);
   
   String qs =  "obj"+cn;
   
   Query query = parser.parse(qs);
   
   TopDocs results = searcher.search(query, 1);
   ScoreDoc[] hits = results.scoreDocs;
   
   if( results.totalHits != 1 )
   {
    System.out.println("Wrong results num: "+results.totalHits+" Query: "+qs);
    continue;
   }
   
   Document doc = searcher.doc(hits[0].doc);
//   Document doc = searcher.doc((int)cn);
   
//   System.out.println("City: "+doc.get("city"));
//   System.out.println("Country: "+doc.get("country"));
   
   BytesRef data =  doc.getBinaryValue("_data");
   
   ByteArrayInputStream bios = new ByteArrayInputStream(data.bytes,data.offset,data.length);
   ObjectInputStream ois = new ObjectInputStream( bios );
   
   HairyObject obj;
   try
   {
    obj = (HairyObject) ois.readObject();
    if( ! obj.getId().equals(qs) )
     System.out.println("Invalid result id="+obj.getId()+" req="+qs);
   }
   catch (ClassNotFoundException e)
   {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
   

  }
  
  tm=System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(RUNS*1000/tm));

  
  /*
  Query query = parser.parse("city800000000");
  
  TopDocs results = searcher.search(query, 5);
  ScoreDoc[] hits = results.scoreDocs;
  
  System.out.println("Results: "+results.totalHits);
  
  Document doc = searcher.doc(hits[0].doc);
  
  System.out.println("City: "+doc.get("city"));
  System.out.println("Country: "+doc.get("country"));
  
  BytesRef data =  doc.getBinaryValue("data");
  
  Camera cam = Camera.load( ByteBuffer.wrap(data.bytes) );
  
  System.out.println("Camera: "+cam.getId()+" City: "+cam.getCity()+" Country: "+cam.getCountry());
  */
 }

}
