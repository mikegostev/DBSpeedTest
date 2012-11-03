package jdbm4;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentNavigableMap;

import net.kotek.jdbm.DB;
import net.kotek.jdbm.DBMaker;

import common.Camera;
import common.LogRecord;

public class FillJDMB4
{
 static final int RECORDS = 10_000_000;
 static final int nRec = 3;

 /**
  * @param args
  * @throws IOException 
  */
 // 20 000 000 -> 435s (45977rec/s) Q: 720 ( 25300rec/s ) (no ser 33333rec/s)
 // 20 000 000 -> 1351s Def1000 (14803rec/s) Q: 723
 
 static final String file = "/home/mike/data/jdbm4/db";
 
 public static void main(String[] args) throws IOException
 {
  DB db = DBMaker.newFileDB(new File( file ) ).closeOnJvmShutdown().make();
  
  ConcurrentNavigableMap<String, Camera> map = db.getTreeMap("collectionName");
 
  
  long tm = System.currentTimeMillis();

  for( int i=1; i<=RECORDS; i++ )
  {
   
   if( i % 10000 == 0 )
   {
//    myDatabase.sync();
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
    db.commit();
   }
   
   String theKey = "key"+i;
   
   Camera p = new Camera();

   p.setCity("city"+i);
   p.setCountry("country"+(i/100));
   
   for( int j=1; j <= nRec; j++ )
   {
    LogRecord lr = new LogRecord();
    
    lr.setTime(1111);
    lr.setEventHash("hash-"+i+"-"+j);
    
    p.addLogRecord( lr );
   }


   map.put(theKey, p);
   

   

  }
  
  tm= System.currentTimeMillis() - tm;

  System.out.println("Time: "+tm+" Rate: "+(RECORDS/tm*1000));

 }
 

}
