package couch;

import java.io.IOException;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
import com.fourspaces.couchdb.ViewResults;

public class QueryCouch
{

 /**
  * @param args
  * @throws IOException 
  */
 public static void main(String[] args) throws IOException
 {
  Session s = new Session("localhost",5984);
  s.setConnectionTimeout(180000);
  s.setSocketTimeout(180000);
  
  Database db = s.getDatabase("test");

  long tm = System.currentTimeMillis();
  
  ViewResults result  = db.adhoc("function (doc) { if (new RegExp('.*e501-0$').test( doc.log[0]['first name'] ) ) { emit(doc, doc); }}");
//  ViewResults result = db.getAllDocuments();
  for (Document d: result.getResults()) {
          System.out.println(d.getId());

          Document full = db.getDocument(d.getId());
          
         System.out.println("country="+full.get("country") );
  }

  System.out.println("Time: "+(System.currentTimeMillis()-tm));

 }

}
