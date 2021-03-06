package ram;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bdb.LogRecord;
import bdb.Place;


public class FillQueryRamHash
{
 public static final int RECORDS = 10_000_000;


 // Def. capacity F: 91670rec/s
 // RECORDS cap. F: 108 88 89 122rec/s Q: 2 187 000rec/s 2523k 2400k
 
 public static void main(String[] args)
 {
  HashMap<String, Place> map = new HashMap<>(RECORDS);
  
  long tm = System.currentTimeMillis();

  for( int i=0; i<RECORDS; i++ )
  {
   
   if( i % 1000 == 0 )
   {
//    myDatabase.sync();
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   }
   
   String theKey = "key"+i;
   
   Place p = new Place();

   p.setCity("city"+i);
   p.setCountry("country"+(i/100));
   
   for( int j=0; j < 3; j++)
   {
    LogRecord l = new LogRecord();
    
    l.setFirstName("name"+i+"-"+j);
    l.setLastName("surname"+i+"-"+j);

    
    p.addLogRecord(l);
   }
   
 
   map.put( theKey, p );
  }

  tm = System.currentTimeMillis()-tm;
  System.out.println("Time: "+tm+" ("+(RECORDS/tm*1000)+"rec/s)");

  Pattern pt = Pattern.compile("e5000-1");
  
  Matcher mch = pt.matcher("");
  
  tm = System.currentTimeMillis();

  ArrayList<Place> res = new ArrayList<>();
  
  for( Place p : map.values() )
  {
   for( LogRecord lr : p.getLog() )
   {
    mch.reset(lr.getFirstName());
    
    if( mch.find() )
    {
     res.add(p);
     break;
    }
   }
  }
  
  tm = System.currentTimeMillis()-tm;
  System.out.println("Time: "+tm+" ("+(RECORDS/tm*1000)+"rec/s)");

  for( Place p : res )
   System.out.println("Found: "+p.getCity());
  
  
 }

}
