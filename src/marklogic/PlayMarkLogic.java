package marklogic;

import javax.xml.namespace.QName;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;

import net.xqj.marklogic.MarkLogicXQDataSource;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.List;
import org.basex.core.cmd.XQuery;

public class PlayMarkLogic
{

 /**
  * @param args
  * @throws BaseXException 
  * @throws XQException 
  */
 public static void main(String[] args) throws XQException
 {
  XQDataSource xqs = new MarkLogicXQDataSource();
  xqs.setProperty("serverName", "localhost");
  xqs.setProperty("port", "8003");

  // Change USERNAME and PASSWORD values
  XQConnection conn = xqs.getConnection("mike", "mike");

  XQPreparedExpression xqpe =
  conn.prepareExpression("declare variable $x as xs:string external; $x");

  xqpe.bindString(new QName("x"), "Hello World!", null);

  XQResultSequence rs = xqpe.executeQuery();

  while(rs.next())
    System.out.println(rs.getItemAsString(null));

  conn.close();
 }

}
