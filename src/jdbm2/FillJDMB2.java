package jdbm2;

import java.io.IOException;

import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import common.Camera;
import common.LogRecord;

public class FillJDMB2
{
 static final int RECORDS = 10_000_000;
 static final int nRec = 3;

 /**
  * @param args
  * @throws IOException 
  */
 // 20 000 000 -> 435s (45977rec/s) Q: 720 ( 25300rec/s ) (no ser 33333rec/s)
 // 20 000 000 -> 1351s Def1000 (14803rec/s) Q: 723
 
 static final String file = "/home/mike/data/jdbm2/db";
 
 public static void main(String[] args) throws IOException
 {
  RecordManager recMan = RecordManagerFactory.createRecordManager(file);
  
  PrimaryTreeMap<String,Camera> treeMap = recMan.treeMap("maindb"); 
 
  
  long tm = System.currentTimeMillis();

  for( int i=1; i<=RECORDS; i++ )
  {
   
   if( i % 10000 == 0 )
   {
//    myDatabase.sync();
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
    recMan.commit();
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


   treeMap.put(theKey, p);
   

   

  }
  
  tm= System.currentTimeMillis() - tm;

  System.out.println("Time: "+tm+" Rate: "+(RECORDS/tm*1000));

 }
 

}
