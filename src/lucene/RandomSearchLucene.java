package lucene;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

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

import common.Camera;
import common.StringUtils;

import config.Config;

public class RandomSearchLucene
{
 static final int RUNS=10000;

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
  
  IndexReader reader = DirectoryReader.open(NIOFSDirectory.open(new File(Config.basePath,"lucene")) );
  IndexSearcher searcher = new IndexSearcher(reader);
  Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
  
  QueryParser parser = new QueryParser(Version.LUCENE_44, "city", analyzer);
  
  long tm = System.currentTimeMillis();
  
  for( int i=0; i < RUNS; i++ )
  {
   long cn = (long)(Math.random()*FillLucene.CAMERAS);
   
   String qs =  "city"+cn;
   
   Query query = parser.parse(qs);
   
   TopDocs results = searcher.search(query, 5);
   ScoreDoc[] hits = results.scoreDocs;
   
   if( results.totalHits != 1 )
   {
    System.out.println("Wrong results num: "+results.totalHits+" Query: "+qs);
    continue;
   }
   
   Document doc = searcher.doc(hits[0].doc);
   
//   System.out.println("City: "+doc.get("city"));
//   System.out.println("Country: "+doc.get("country"));
   
   BytesRef data =  doc.getBinaryValue("data");
   
   Camera cam = Camera.load( ByteBuffer.wrap(data.bytes) );
   
   if( ! cam.getCity().equals(qs) )
    System.out.println("Invalid result");

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
