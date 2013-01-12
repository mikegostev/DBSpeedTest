package marklogic;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;


import net.xqj.marklogic.MarkLogicXQDataSource;

public class QueryMarkLogic
{

 /**
  * @param args
  * @throws XQException 
  */
 public static void main(String[] args) throws XQException
 {
  XQDataSource xqs = new MarkLogicXQDataSource();
  xqs.setProperty("serverName", "localhost");
  xqs.setProperty("port", "8004");

  // Change USERNAME and PASSWORD values
  XQConnection conn = xqs.getConnection("mike", "mike");

  long tm = System.currentTimeMillis();

  XQPreparedExpression xqpe =
  conn.prepareExpression("//hash[matches(text(),'sh-5555-2')]/../../city/text()");

//  xqpe.bindString(new QName("x"), "Hello World!", null);

  XQResultSequence rs = xqpe.executeQuery();

  while(rs.next())
    System.out.println(rs.getItemAsString(null));

  conn.close();
  
  tm = System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" ("+(FillMarkLogic.RECORDS/tm*1000)+"rec/s)");

 }

}
