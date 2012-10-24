package mongo;

import java.net.UnknownHostException;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

public class FillMongo
{

 /**
  * @param args
  * @throws UnknownHostException 
  */
 
 // 20 000 000 - > 463s (43196rec/s) Q: 89s (224719res/s)
 // 
 public static void main(String[] args) throws UnknownHostException
 {
  Mongo m = new Mongo( "localhost" , 27017 );

  DB db = m.getDB( "test" );
  
//  Set<String> colls = db.getCollectionNames();

  DBCollection coll = db.getCollection("test");
  
  BasicDBObject query = new BasicDBObject();

  query.put("b", new BasicDBObject("$elemMatch", new BasicDBObject("city", "Cambridge")));
  
  long tm = System.currentTimeMillis();

  DBCursor crs = coll.find(query);
  
  while ( crs.hasNext() )
   System.out.println( crs.next() );

  System.out.println("Query Time: "+(System.currentTimeMillis()-tm));
 
  tm = System.currentTimeMillis();
  
  for( int i=0; i < 0; i++ )
  {
   
   if( i % 1000 == 0 )
    System.out.println(i);
   
   BasicDBObject doc = new BasicDBObject();
   
   doc.put("city", "city"+i);
   doc.put("country", "country"+(i/100));
   
   BasicDBList lst = new BasicDBList();
   
   for( int j=0; j < 3; j++)
   {
    BasicDBObject per = new BasicDBObject();
    
    per.put("first name", "name"+i+"-"+j);
    per.put("last name", "surname"+i+"-"+j);
    
    lst.add( per );
   }
   
   doc.put("log", lst);
   
   coll.insert( doc);
  }
  
  System.out.println("Time: "+(System.currentTimeMillis()-tm));
  
 }

}