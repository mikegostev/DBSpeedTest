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

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;

public class PlayMarkLogic2
{

 /**
  * @param args
  * @throws BaseXException 
  * @throws XQException 
  */
 public static void main(String[] args) throws XQException
 {
  DatabaseClient cli = DatabaseClientFactory.newClient("127.0.0.1", 8004, "mike", "mike", Authentication.BASIC);
  cli.openTransaction();
  XMLDocumentManager docMgr = cli.newXMLDocumentManager();
  
  StringHandle sh = new StringHandle();
  sh.set("<cam><city>city1</city></cam>");
  
  docMgr.write("/carmen/cam1.xml", sh);
 
  sh = new StringHandle();
  sh.set("<cam><city>city2</city></cam>");
  
  docMgr.write("/carmen/cam2.xml", sh);

  cli.release();
 }

}
