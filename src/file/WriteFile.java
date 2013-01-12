package file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import config.Config;

public class WriteFile
{
 public static String fileName = "file.bin";
 
 public static int RECORDS = 10000000;
 public static int bufSize = 1024;

 /**
  * @param args
  * @throws IOException 
  */
 public static void main(String[] args) throws IOException
 {
  RandomAccessFile f = new RandomAccessFile(new File(Config.basePath, fileName), "rw");

  //byte[] buf = new byte[bufSize];
  long tm = System.currentTimeMillis();
 
  ByteBuffer buffer = ByteBuffer.allocate( bufSize );
  
  FileChannel fc = f.getChannel();
  
  for(int i=0; i <  RECORDS; i++ )
  {
   fc.position((long)i*bufSize);
   
   buffer.clear();
   
   buffer.putInt(i);
   
   byte[] strb = ("Hello"+i).getBytes();
   
   
   buffer.putInt(strb.length);
   buffer.put(strb);
   
   buffer.limit(bufSize);
   
   buffer.flip();
   
   fc.write(buffer);
   
  }
  
  fc.close();
  
  tm = System.currentTimeMillis()-tm;
  System.out.println("Time: "+tm+" ("+(RECORDS/tm*1000)+"rec/s)");

 }

}
