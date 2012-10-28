package basex;

import h2.FillH2;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.XQuery;

public class FillBaseX
{
 public static final int RECORDS = 1_000_000;

 //F: 559rec/s Q: 95000
 
 public static void main(String[] args) throws BaseXException
 {
  Context context = new Context();

  new DropDB("Carmen").execute(context);
  
  new CreateDB("Carmen", "<world></world>").
  execute(context);
 
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
   
   new XQuery("insert node "+xml+" as last into doc('Carmen.xml')/world ").execute(context);

  }
 
  tm = System.currentTimeMillis()-tm;
  
  System.out.println("Time: "+tm+" ("+(RECORDS/tm*1000)+"rec/s)");

  new Close().execute(context);
 }

}
