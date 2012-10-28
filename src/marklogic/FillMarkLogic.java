package marklogic;

import javax.xml.namespace.QName;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import org.basex.core.cmd.XQuery;

import net.xqj.marklogic.MarkLogicXQDataSource;

public class FillMarkLogic
{
 public static final int RECORDS = 1_000_000;

 /**
  * @param args
  * @throws XQException 
  */
 public static void main(String[] args) throws XQException
 {
  XQDataSource xqs = new MarkLogicXQDataSource();
  xqs.setProperty("serverName", "localhost");
  xqs.setProperty("port", "8003");

  // Change USERNAME and PASSWORD values
  XQConnection conn = xqs.getConnection("mike", "mike");
  XQExpression xqe = conn.createExpression();
  
  long tm = System.currentTimeMillis();

  
  for( int i=0; i < RECORDS; i++)
  {
   if( i % 1000 == 0 )
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   
   String country = "country"+(i/100);
   String city ="city"+i;

   String xml = "<camera><city>"+city+"</city><country>"+country+"</country><log>";
   
   for( int j=1; j<=3; j++)
   {
    xml+="<hash>hash-"+i+"-"+j+"</hash>";
   }
   
   xml+="</log></camera>";
   
   xqe.executeQuery("insert <camera>a</camera> into test");

  }
 
  tm = System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" ("+(RECORDS/tm*1000)+"rec/s)");

  conn.close();
  
 }

}
