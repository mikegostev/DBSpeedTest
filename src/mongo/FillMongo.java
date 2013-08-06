package mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import common.Camera;
import common.LogRecord;

public class FillMongo
{
 final static int RECORDS = 1_000_000_000;

 /**
  * @param args
  * @throws UnknownHostException 
  */
 
 // 20 000 000 - > 463s (43196rec/s) Q: 89s (224719res/s)
 // 
 public static void main(String[] args) throws UnknownHostException
 {
  MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

  DB db = mongoClient.getDB( "test" );
  
//  Set<String> colls = db.getCollectionNames();

  DBCollection coll = db.getCollection("test");
  
  /*
  BasicDBObject query = new BasicDBObject();

  query.put("b", new BasicDBObject("$elemMatch", new BasicDBObject("city", "Cambridge")));
  
  long tm = System.currentTimeMillis();

  DBCursor crs = coll.find(query);
  
  while ( crs.hasNext() )
   System.out.println( crs.next() );

  System.out.println("Query Time: "+(System.currentTimeMillis()-tm));
 
  tm = System.currentTimeMillis();
  */
  long tm = System.currentTimeMillis();

  for( int i=0; i < RECORDS; i++ )
  {
   
   if( i % 10000 == 0 )
   {
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   }
   
   Camera p = new Camera();

   p.setCity("city"+i);
   p.setCountry("country"+(i/100));
   p.setId(p.getCountry()+p.getCity());
   
   for( int j=1; j <= 3; j++ )
   {
    LogRecord lr = new LogRecord();
    
    lr.setTime(1111);
    lr.setEventHash("hash-"+i+"-"+j);
    
    p.addLogRecord( lr );
   }
   
   coll.insert( saveCamera(p) );
  }
  
  mongoClient.close();
  
  tm = System.currentTimeMillis()-tm;
  System.out.println("Time: "+tm+" ("+(RECORDS*1000/tm)+"rec/s)");
  
  readCamera( coll.findOne() );
  
 }

 public static BasicDBObject saveCamera(Camera cam)
 {
  BasicDBObject doc = new BasicDBObject();

  
  doc.put("id", cam.getId() );
  doc.put("city", cam.getCity());
  doc.put("country", cam.getCountry());

  BasicDBList lst = new BasicDBList();
  
  for( LogRecord lr : cam.getLog() )
  {
   BasicDBObject per = new BasicDBObject();
   
   per.put("time", lr.getTime());
   per.put("hash", lr.getEventHash());

   lst.add( per );
  }
  
  doc.put("log", lst);

  return doc;
 }
 
 public static  Camera readCamera( DBObject dbo )
 {
  Camera cam = new Camera();
  
  cam.setId(dbo.get("id").toString());
  cam.setCity(dbo.get("city").toString());
  cam.setCountry(dbo.get("country").toString());
  
  BasicDBList logo = (BasicDBList)dbo.get("log");

  List<LogRecord> log = new ArrayList<>( logo.size() );
  
  for(Object lro : log)
  {
   DBObject lrdbo = (DBObject)lro;
   
   LogRecord lr = new LogRecord();
   
   lr.setEventHash(lrdbo.get("hash").toString());
   lr.setTime( (Long)lrdbo.get("time") );
   
   log.add(lr);
  }
  
  //System.out.println("log: "+o.getClass().getName());

  
  return cam;
 }
 
}
