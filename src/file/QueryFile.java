package file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pri.util.StringUtils;
import common.Camera;
import common.LogRecord;
import common.Search;

public class QueryFile
{

 /**
  * @param args
  * @throws IOException 
  * @throws FileNotFoundException 
  * @throws ClassNotFoundException 
  */
 public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException
 {
  File f = new File(FillFile.file);

  ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f));

  Matcher mtch = Pattern.compile( Search.regExp ).matcher("");
  
  ArrayList<Camera> res = new ArrayList<>();
  
  long tm = System.currentTimeMillis();
  
  int n=0;
  
  while( true )
  {
   Camera cam = null;
   try
   {
    cam = (Camera) ois.readObject();
   }
   catch( Exception e )
   {
    ois.close();
    System.out.println(e.getMessage());
    break;
   }
   
   for( LogRecord lr : cam.getLog() )
   {
    mtch.reset(lr.getEventHash());
    
    if( mtch.find() )
    {
     res.add(cam);
     break;
    }
   }
   
   n++;
   
   if( n%1000 == 0 )
   {
    System.out.println("Processed : "+n+" Rate: "+(n/(System.currentTimeMillis()-tm)*1000)+"rec/s");
   }

  }
  
  tm = System.currentTimeMillis() - tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(n/tm*1000));

  for( Camera c : res )
   System.out.println("Found: "+c.getId()+" in "+c.getCountry()+", "+c.getCity());
  
 }

}
