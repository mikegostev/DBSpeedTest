package couch;
import java.io.IOException;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;


public class FillCouch
{


 // 400 rec/s Q: 10736rec/s
 
 public static void main(String[] args) throws IOException
 {
  Session s = new Session("localhost",5984);
  
  s.deleteDatabase("test");
  s.createDatabase("test");
  
  Database db = s.getDatabase("test");


  long tm = System.currentTimeMillis();

  for( int i=0; i<500_000; i++ )
  {
   
   if( i % 1000 == 0 )
   {
//    myDatabase.sync();
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   }
   
   Document place = new Document();
   
   place.put("city", "city"+i);
   place.put("country", "country"+(i/100));
   

   for( int j=0; j < 3; j++)
   {
    Document pers = new Document();
    
    pers.put("last name", "surname"+i+"-"+j);
    pers.put("first name", "name"+i+"-"+j);

    place.accumulate("log", pers);
   }
   
   db.saveDocument(place);
  }
   
  System.out.println("Time: "+(System.currentTimeMillis()-tm));

 
 }

}
