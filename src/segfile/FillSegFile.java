package segfile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.pri.util.StringUtils;
import common.Camera;
import common.LogRecord;

public class FillSegFile
{
 public static final int CAMERAS = 10_000_000;
 public static final String file = "/home/mike/data/segdata.ser";

 // EBI F: 19000cam/s Q:15000cam/s
 
 public static void main(String[] args) throws FileNotFoundException, IOException
 {
  File f = new File(file);

  FileOutputStream out = new FileOutputStream( f );
  ObjectOutputStream outoos = new ObjectOutputStream( out );

  
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
   
   ByteArrayOutputStream baos = new ByteArrayOutputStream();
   ObjectOutputStream oos = new ObjectOutputStream( baos );

   oos.writeObject(cam);
   
   oos.close();
   
   byte[] byts = baos.toByteArray();
   
   outoos.writeInt( byts.length );
   outoos.write(byts);
   
   if( i%10000 == 0 )
   {
    System.out.println("Processed : "+i+" Rate: "+(i/(System.currentTimeMillis()-tm)*1000)+"rec/s");
   }
  }
  
  outoos.close();
  
  tm=System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(CAMERAS/tm*1000));
 }
 

}
