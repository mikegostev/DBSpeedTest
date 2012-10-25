package dir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.pri.util.StringUtils;
import common.Camera;
import common.LogRecord;

public class FillDir
{
 public static final int CAMERAS = 10_000;
 public static final File dir = new File("e:/dev/dir/");

 // EBI F:2241cam/s  Q:  Qmt: cam/s
 
 public static void main(String[] args) throws FileNotFoundException, IOException
 {
  long tm = System.currentTimeMillis();
  
  for( int i=1; i <= CAMERAS; i++ )
  {
   
   Camera cam = new Camera();
   
   cam.setCity("city"+i);
   cam.setCountry("country"+(i/100));
   cam.setId("cam"+i);
   
   int nRec = 3;
   
   for( int j=1; j <= nRec; j++ )
   {
    LogRecord lr = new LogRecord();
    
    lr.setTime(1111);
    lr.setEventHash("hash-"+i+"-"+j);
    
    cam.addLogRecord( lr );
   }
   
   writeObject(cam);
   
   if( i%1000 == 0 )
   {
    System.out.println("Processed : "+i+" Rate: "+(i*1000/(System.currentTimeMillis()-tm))+"rec/s");
   }
  }
  
  tm=System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(CAMERAS*1000/tm));
 }
 
 private static void writeObject( Camera c ) throws IOException
 {
  FileOutputStream fos = new FileOutputStream( new File(dir,c.getId()) );
  
  ObjectOutputStream locoos = new ObjectOutputStream( fos );
  
  locoos.writeObject(c);
  locoos.close();
  
 }

}
