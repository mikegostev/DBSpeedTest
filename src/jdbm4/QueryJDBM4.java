package jdbm4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;

import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import common.Camera;
import common.LogRecord;

public class QueryJDBM4
{
 static String personName = "name500000-2";
 /**
  * @param args
  * @throws IOException 
  * @throws ClassNotFoundException 
  */
 public static void main(String[] args) throws IOException, ClassNotFoundException
 {
  RecordManager recMan = RecordManagerFactory.createRecordManager(FillJDMB4.file);
  
  PrimaryTreeMap<String,Camera> treeMap = recMan.treeMap("maindb"); 
  
  Matcher mtch = java.util.regex.Pattern.compile("sh-7777-2").matcher("");
  
//  myDatabase.preload(1024*1024);

  long tm = System.currentTimeMillis();

  ArrayList< Camera > res = new ArrayList<>();
  
  int i = 0;
  
  long len=0;
  
  for( Camera c : treeMap.values() )
  {
   i++;
   
   if( i % 10000 == 0 )
   {
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   }
   
   for( LogRecord lr : c.getLog() )
   {
    mtch.reset( lr.getEventHash() );
    if( mtch.find() )
    {
     res.add(c);
     break;
    }
   }
  }
  
  
  tm = System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" Len: "+len+" Rate: "+(i/tm*1000));
 
  for( Camera l : res )
  {
   System.out.println("Found country: "+l.getCountry()+" city: "+l.getCity());
  }
 }

}
