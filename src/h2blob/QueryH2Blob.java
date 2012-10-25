package h2blob;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import com.pri.util.StringUtils;
import common.Camera;
import common.ParallelSearcher;

public class QueryH2Blob
{
 final static int nThreads = 2;
 /**
  * @param args
  * @throws ClassNotFoundException 
  * @throws SQLException 
  */
 public static void main(String[] args) throws ClassNotFoundException, SQLException
 {
  Class.forName("org.h2.Driver");

  Connection conn = DriverManager
      .getConnection("jdbc:h2:e:/dev/h2/blob", "sa", "");
  
  final ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(10);
  final ArrayList<Camera> res = new ArrayList<>();

  
  Statement stmt = conn.createStatement();
  
  stmt.executeUpdate("SET SCHEMA blob");
  
  Thread[] thrds = new Thread[nThreads];
  
  for( int i=0; i<nThreads; i++ )
  {
   thrds[i] = new ParallelSearcher("sh-1000-2", queue, res);
   thrds[i].start();
  }
  
 
  long tm = System.currentTimeMillis();

  
  ResultSet rst = stmt.executeQuery("SELECT other FROM place");

  int n=0;
  
  while( rst.next() )
  {
   
   try
   {
    queue.put( rst.getBytes(1) );
   }
   catch(InterruptedException e)
   {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
   
   n++;
   
   if( n%1000 == 0 )
   {
    System.out.println("Processed : "+n+" Rate: "+(n/(System.currentTimeMillis()-tm)*1000)+"rec/s");
   }

  }
  
  try
  {
   queue.put( new byte[0] );
  }
  catch(InterruptedException e1)
  {
   // TODO Auto-generated catch block
   e1.printStackTrace();
  }
  
  try
  {
   for( int i=0; i<nThreads; i++ )
    thrds[i].join();
  }
  catch(InterruptedException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  
  tm = System.currentTimeMillis() - tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(n/tm*1000));

  for( Camera c : res )
   System.out.println("Found: "+c.getId()+" in "+c.getCountry()+", "+c.getCity());

 }

}
