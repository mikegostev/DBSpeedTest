package kyotocabinet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import common.Camera;
import common.LogRecord;

public class FillKC
{
 static final int    RECORDS = 10_000_000;
 static final int    nRec    = 3;

 /**
  * @param args
  * @throws IOException
  */
 // 20 000 000 -> 435s (45977rec/s) Q: 720 ( 25300rec/s ) (no ser 33333rec/s)
 // 20 000 000 -> 1351s Def1000 (14803rec/s) Q: 723

 static final String file    = "/home/mike/data/kc/carmen.kch";

 public static void main(String[] args) throws IOException
 {
  DB db = new DB();

  if(!db.open(file, DB.OWRITER | DB.OCREATE))
  {
   System.err.println("open error: " + db.error());
   return;
  }

  long tm = System.currentTimeMillis();

  for(int i = 0; i < RECORDS; i++)
  {

   if(i % 10000 == 0)
   {
    // myDatabase.sync();
    System.out.println("Rec " + i + " (" + (i * 1000.0 / (System.currentTimeMillis() - tm)) + "rec/s)");
   }

   byte[] theKey = ("key" + i).getBytes();

   Camera p = new Camera();

   p.setCity("city" + i);
   p.setCountry("country" + (i / 100));

   for(int j = 1; j <= nRec; j++)
   {
    LogRecord lr = new LogRecord();

    lr.setTime(1111);
    lr.setEventHash("hash-" + i + "-" + j);

    p.addLogRecord(lr);
   }

   ByteArrayOutputStream baos = new ByteArrayOutputStream();
   ObjectOutputStream oos = new ObjectOutputStream(baos);

   oos.writeObject(p);

   oos.close();

   db.set(theKey, baos.toByteArray());
  }

  
  if(!db.close())
  {
   System.err.println("close error: " + db.error());
  }

 }

}
