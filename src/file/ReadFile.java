package file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ReadFile
{

 /**
  * @param args
  * @throws IOException 
  */
 public static void main(String[] args) throws IOException
 {
  RandomAccessFile f = new RandomAccessFile(new File("c:/dbtest/file.bin"), "rw");

  //byte[] buf = new byte[bufSize];
  long tm = System.currentTimeMillis();
 
  ByteBuffer buffer = ByteBuffer.allocate( WriteFile.bufSize );
  
  FileChannel fc = f.getChannel();
  
  byte[] strbuf = new byte[WriteFile.bufSize];
  
  for(int i=0; i <  WriteFile.RECORDS; i++ )
  {
   fc.position((long)i*WriteFile.bufSize);
   
   buffer.clear();
   
   fc.read(buffer);
   
   buffer.flip();

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
