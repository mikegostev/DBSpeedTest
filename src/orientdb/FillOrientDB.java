package orientdb;

import java.util.ArrayList;
import java.util.List;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class FillOrientDB
{
 static final int RECORDS = 1_000_000;
 static final int nRec = 3;

 /**
  * @param args
  */
 public static void main(String[] args)
 {
  ODatabaseDocumentTx db = new ODatabaseDocumentTx ("local:/home/mike/data/OrientDB").create();

  
  long tm = System.currentTimeMillis();

  for( int i=1; i<=RECORDS; i++ )
  {
   
   if( i % 10000 == 0 )
   {
//    myDatabase.sync();
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
    db.close();
    db = new ODatabaseDocumentTx ("local:/home/mike/data/OrientDB").open("admin", "admin");
   }
  
   ODocument c = new ODocument("Camera");
   c.field( "city", "city"+i );
   c.field( "location", "country"+(i/100) );
   
   
   List<ODocument> log = new ArrayList<>();
   
   for( int j=1; j <= nRec; j++ )
   {
    ODocument r = new ODocument("LogRecord");
    r.field( "time", 1111 );
    r.field( "hash", "hash-"+i+"-"+j );
    
    log.add(r);
   }
   
   c.field("log",log);
   
   c.save();
  
  }
  

  
  db.close();
  
 }

}
