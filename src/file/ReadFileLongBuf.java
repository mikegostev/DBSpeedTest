package file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import config.Config;

public class ReadFileLongBuf
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
 
  ByteBuffer buffer = ByteBuffer.allocate( WriteFile.bufSize*loadSlots );
  
  FileChannel fc = f.getChannel();
  
  byte[] strbuf = new byte[WriteFile.bufSize];
  
  for(int i=0; i <  WriteFile.RECORDS; i++ )
  {
   int offs = i % loadSlots;
   
   if( offs == 0 )
   {
    fc.position((long)i*WriteFile.bufSize);
    
    buffer.clear();
    
    fc.read(buffer);
    
    buffer.flip();
   }
   
   buffer.position(offs*WriteFile.bufSize);
   
   int j = buffer.getInt();
   int len = buffer.getInt();
   buffer.get(strbuf, 0, len);
   
   String str = new String(strbuf,0,len);
   
   if( "Hello1000000".equals(str) )
    System.out.println("i="+i+" "+str);
  }
  
  fc.close();
  
  tm = System.currentTimeMillis()-tm;
  System.out.println("Time: "+tm+" ("+(WriteFile.RECORDS/tm*1000)+"rec/s)");
 }

}
