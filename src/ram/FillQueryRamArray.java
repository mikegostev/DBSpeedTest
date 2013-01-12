package ram;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bdb.LogRecord;
import bdb.Place;


public class FillQueryRamArray
{
 public static final int RECORDS = 10_000_000;


 // F: 113 121 Q:3522 3748
 // PreAlloc F: 128 Q: 3051 3581
 
 public static void main(String[] args)
 {
  List<Place> map = new ArrayList<>(RECORDS);
  
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
   
 
   map.add( p );
  }

  tm = System.currentTimeMillis()-tm;
  System.out.println("Time: "+tm+" ("+(RECORDS/tm*1000)+"rec/s)");

  Pattern pt = Pattern.compile("e5000-1");
  
  Matcher mch = pt.matcher("");
  
  tm = System.currentTimeMillis();

  ArrayList<Place> res = new ArrayList<>();
  
  for( Place p : map )
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
