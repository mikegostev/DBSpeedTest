package marklogic;

import javax.xml.namespace.QName;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import org.basex.core.cmd.XQuery;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;

import net.xqj.marklogic.MarkLogicXQDataSource;

public class FillMarkLogic
{
 public static final int RECORDS = 10_000_000;

 /**
  * @param args
  * @throws XQException 
  */
 public static void main(String[] args) throws XQException
 {
  
  DatabaseClient cli = DatabaseClientFactory.newClient("localhost", 8003, "mike", "mike", Authentication.DIGEST);

  XMLDocumentManager docMgr = cli.newXMLDocumentManager();
  
  
  long tm = System.currentTimeMillis();

  
  for( int i=0; i < RECORDS; i++)
  {
//   cli.openTransaction();
   
   if( i % 10000 == 0 )
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   
   String country = "country"+(i/100);
   String city ="city"+i;

   String xml = "<camera><city>"+city+"</city><country>"+country+"</country><log>";
   
   for( int j=1; j<=3; j++)
   {
    xml+="<hash>hash-"+i+"-"+j+"</hash>";
   }
   
   xml+="</log></camera>";
   
   StringHandle sh = new StringHandle();
   sh.set(xml);
   
   docMgr.write("/carmen/cam"+i+".xml", sh);
   
  }
 
  cli.release();
  
  tm = System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" ("+(RECORDS/tm*1000)+"rec/s)");
 
 }

}
