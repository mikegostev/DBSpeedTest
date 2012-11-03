package orientdb;

import java.util.List;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

public class QueryOrientDB
{

 /**
  * @param args
  */
 public static void main(String[] args)
 {
  ODatabaseDocumentTx db = new ODatabaseDocumentTx ("local:/home/mike/data/OrientDB").open("admin", "admin");

  long tm = System.currentTimeMillis();

//  List<ODocument> result = db.query(new ONativeSynchQuery<OQueryContextNative>
//  (db, "Person", new OQueryContextNative()) {
//                       @Override
//                       public boolean filter(OQueryContextNative iRecord) {
//                               return iRecord.field("log")..field("name").eq("Rome").and().
//                                 field("name").like("G%").go();
//                       };
//               });
  

  
  List<ODocument> result = db.query(
    new OSQLSynchQuery<ODocument>("select * from Camera where log contains (hash matches '.*sh-4444-2.*')"));
  
  tm = System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" Rate: "+(FillOrientDB.RECORDS/tm*1000));

  
  for(ODocument doc : result )
   System.out.println( "Found: "+doc.field("location"));
 }

}
