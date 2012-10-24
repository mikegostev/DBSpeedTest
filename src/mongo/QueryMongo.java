package mongo;

import java.net.UnknownHostException;
import java.util.regex.Pattern;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

public class QueryMongo
{

 /**
  * @param args
  * @throws UnknownHostException 
  */
 public static void main(String[] args) throws UnknownHostException
 {
  Mongo m = new Mongo( "localhost" , 27017 );

  DB db = m.getDB( "test" );
  
//  Set<String> colls = db.getCollectionNames();

  DBCollection coll = db.getCollection("test");
  
  Pattern rx = Pattern.compile(".*e500000-2", 0);
  
  BasicDBObject query = new BasicDBObject();

  query.put("log", new BasicDBObject("$elemMatch", new BasicDBObject("first name", rx)));
  
  long tm = System.currentTimeMillis();

  DBCursor crs = coll.find(query);
  
  while ( crs.hasNext() )
   System.out.println( crs.next() );

  System.out.println("Query Time: "+(System.currentTimeMillis()-tm));
 }

}
