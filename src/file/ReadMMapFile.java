package file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import config.Config;

public class ReadMMapFile
{
 public static int loadSlots=1000;
 /**
  * @param args
  * @throws IOException 
  */
 public static void main(String[] args) throws IOException
 {
  RandomAccessFile f = new RandomAccessFile(new File(Config.basePath, WriteFile.fileName), "rw");

  //byte[] buf = new byte[bufSize];
  long tm = System.currentTimeMillis();
 
  FileChannel fc = f.getChannel();
  
  MappedByteBuffer buf=null;
  
  byte[] strbuf = new byte[WriteFile.bufSize];
  
  for(int i=0; i <  WriteFile.RECORDS; i++ )
  {
   int offs = i % loadSlots;
   
   if( offs == 0)
   {
    buf = fc.map(MapMode.READ_ONLY, (long)i*WriteFile.bufSize, loadSlots*WriteFile.bufSize);
    buf.load();
   }
   
   buf.position(offs*WriteFile.bufSize);
   
   int j = buf.getInt();
   int len = buf.getInt();

//   System.out.println("j="+j+" len="+len);

   
   buf.get(strbuf, 0, len);
   
   String str = new String(strbuf,0,len);
   
   if( "Hello1000000".equals(str) )
    System.out.println("i="+i+" "+str);
  }
  
  fc.close();
  
  tm = System.currentTimeMillis()-tm;
  System.out.println("Time: "+tm+" ("+(WriteFile.RECORDS/tm*1000)+"rec/s)");
 }

}
