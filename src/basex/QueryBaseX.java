package basex;

import h2.FillH2;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Open;
import org.basex.core.cmd.XQuery;

public class QueryBaseX
{

 /**
  * @param args
  * @throws BaseXException 
  */
 public static void main(String[] args) throws BaseXException
 {
  Context context = new Context();
  
  new Open("Carmen").execute(context);

  long tm = System.currentTimeMillis();

  System.out.println( new XQuery("for $cam in doc('Carmen.xml')//hash[matches(text(),'sh-555-2')]/../.. return concat( data($cam/city),', ',data($cam/country))").execute(context) );
  
  tm = System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" ("+(FillBaseX.RECORDS/tm*1000)+"rec/s)");

 }

}
