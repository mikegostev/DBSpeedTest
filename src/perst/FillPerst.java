package perst;

import java.io.File;

import org.garret.perst.IFile;
import org.garret.perst.Index;
import org.garret.perst.Storage;
import org.garret.perst.StorageFactory;
import org.garret.perst.impl.MultiFile;

import com.pri.util.StringUtils;
import common.Camera;
import common.LogRecord;

import config.Config;

public class FillPerst
{
 public static long CAMERAS = 1_000_000_000;

 public static int fileSize=2_000_000_000;
 public static int numFiles = 300;

 static final int nRec = 3;

 
 public static void main(String[] args)
 {
  if( args.length > 0 )
  {
   Config.basePath = new File(args[0],"perst");
  }
  else
   Config.basePath = new File(Config.basePath,"perst");

  Storage db = StorageFactory.getInstance().createStorage();
  
  String[] files = new String[numFiles];
  long[] fileSzs = new long[numFiles];
  
  
  for( int i=0; i< numFiles; i++)
  {
   files[i] = new File(Config.basePath,"seg"+i+".bin").getAbsolutePath();
   fileSzs[i]=fileSize;
  }

  IFile dskst = new MultiFile(files, fileSzs, false, false);
  db.open(dskst, 1000000000);
  
  Index<Camera> root = db.createIndex(String.class, // key type
    true); // unique index
 
  db.setRoot(root);
  
  long tm = System.currentTimeMillis();

  for( long i=1; i <= CAMERAS; i++ )
  {
   
   Camera p = new Camera();

   p.setCity("city"+i);
   p.setCountry("country"+(i/100));
   p.setId(p.getCountry()+p.getCity());
   
   for( int j=1; j <= nRec; j++ )
   {
    LogRecord lr = new LogRecord();
    
    lr.setTime(1111);
    lr.setEventHash("hash-"+i+"-"+j);
    
    p.addLogRecord( lr );
   }
   
   root.put(p.getId(), p);
   
   if( (i % 100000) == 0)
    System.out.println("Done: "+i+" ("+100L*i/CAMERAS+"%) ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");

  }
  
  tm=System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(CAMERAS/tm*1000));

  
  db.close();
  dskst.close();
 }

}
