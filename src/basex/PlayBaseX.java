package basex;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.List;
import org.basex.core.cmd.XQuery;

public class PlayBaseX
{

 /**
  * @param args
  * @throws BaseXException 
  */
 public static void main(String[] args) throws BaseXException
 {
  Context context = new Context();

  
  new CreateDB("Carmen", "<world><camera><city>Cambridge</city></camera></world>").
  execute(context);
  
  new XQuery("insert node <camera><city>Fulbourn</city></camera> as last into doc('Carmen.xml')/world ").execute(context);
  
//  System.out.print(new List().execute(context));
  
  System.out.println(new XQuery("doc('Carmen.xml')//city[matches(text(),'bridge')]/text()").execute(context));
 }

}
