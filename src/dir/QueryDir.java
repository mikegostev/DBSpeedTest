package dir;

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

public class QueryDir
{

 /**
  * @param args
  * @throws IOException 
  * @throws FileNotFoundException 
  * @throws ClassNotFoundException 
  */
 public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException
 {
  File[] files = FillDir.dir.listFiles();

  ArrayList<Camera> res = new ArrayList<>();
  
  Matcher mtch = Pattern.compile( Search.regExp ).matcher("");

  long tm = System.currentTimeMillis();
  
  int n=0;
  
  for( File f : files )
  {
   ObjectInputStream ios = new ObjectInputStream( new FileInputStream(f) );
   
   Camera cam = (Camera)ios.readObject();
   
   ios.close();
   
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
    System.out.println("Processed : "+n+" Rate: "+(n*1000/(System.currentTimeMillis()-tm))+"rec/s");
   }

  }
  
  tm = System.currentTimeMillis() - tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(n*1000/tm));

  for( Camera c : res )
   System.out.println("Found: "+c.getId()+" in "+c.getCountry()+", "+c.getCity());

  
 }

}
