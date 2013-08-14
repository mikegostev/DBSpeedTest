package lucene;

import java.io.File;
import java.io.IOException;

import jline.ConsoleReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
public class QueryConsole
{
 public static String luceneDir;
 /**
  * @param args
  * @throws IOException 
  */
 public static void main(String[] args) throws IOException
 {
  ConsoleReader in = new ConsoleReader();
  
  String line=null;
  
  IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(luceneDir)) );
  IndexSearcher searcher = new IndexSearcher(reader);
  Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
  
  QueryParser parser = new MyQueryParser(Version.LUCENE_44, "id", analyzer);

  
  do
  {
   line=in.readLine("Q>");
   
   if( "exit".equals(line) )
    return;
   
   Query query;
   try
   {
    query = parser.parse(line);
   }
   catch(ParseException e)
   {
    System.out.println("Invalid query: "+line);
    continue;
   }
   
   TopDocs results = searcher.search(query, 5);
   ScoreDoc[] hits = results.scoreDocs;
   
   System.out.println("Found:"+ results.totalHits);

  
//   Document doc = searcher.doc(hits[0].doc);

   
  }
  while( ! "exit".equals(line));
  
  reader.close();
  
 }
 
 static class MyQueryParser extends QueryParser
 {

  public MyQueryParser(Version matchVersion, String f, Analyzer a)
  {
   super(matchVersion, f, a);
  }
  
  @Override
  protected org.apache.lucene.search.Query getRangeQuery(String field, String arg1, String arg2, boolean arg3, boolean arg4) throws ParseException
  {
   if("id".equals(field))
    return super.getRangeQuery(field, arg1, arg2, arg3, arg4);
   
//   TermRangeQuery query = (TermRangeQuery)
//     super.getRangeQuery(field, arg1, arg2,
//       arg3);
   
   return NumericRangeQuery.newIntRange(
     field,
     Integer.parseInt(arg1),
     Integer.parseInt(arg2),
     arg3,
     arg4);
  }
  
 }

}
