package vertica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import common.HairyAttribute;
import common.HairyClass;
import common.HairyClass.TYPE;
import common.HairyClassPool;
import common.HairyObject;
import common.HairyObjectFactory;
import common.StringUtils;


public class FillVertica
{
 public static String verticaHost = "172.22.69.122";
 public static int OBJECTS = 1_000_000;

 public static void main(String[] args)
 {
  if( args.length == 1 )
   verticaHost = args[0];
  
  HairyObjectFactory fact = new HairyObjectFactory(1000, 10);
  
  HairyClassPool cPool = fact.getClassPool();
  
  Connection conn;
  
  try
  {
   Class.forName("com.vertica.jdbc.Driver");
  }
  catch(ClassNotFoundException e)
  {
   System.err.println("Could not find the JDBC driver class.\n");
   e.printStackTrace();
   return;
  }
  try
  {
   conn = DriverManager.getConnection("jdbc:vertica://"+verticaHost+":5433/sage", "dbadmin", "monster");
   // So far so good, lets issue a query...
   Statement stmt = conn.createStatement();
   
   StringBuilder sb = new StringBuilder(4000);
   
   sb.append("CREATE TABLE HairyObjects (_id varchar");

   for(HairyClass hc : cPool.getClasses())
   {
    sb.append(',').append(hc.getName()).append(" ");
    
    switch( hc.getType() )
    {
     case STRING:
      
      sb.append("varchar");
      
      break;

     case INT:
      
      sb.append("integer");
      
      break;

     case REAL:
      
      sb.append("real");
      
      break;

     case BOOLEAN:
      
      sb.append("boolean");
      
      break;
      
     default:
      break;
    }
    
   }
   
   sb.append(')');
   
   stmt.executeUpdate(sb.toString());

   StringBuilder valList = new StringBuilder(2000);
   
   long tm = System.currentTimeMillis();

   for( int i=0; i < OBJECTS; i++)
   {
    HairyObject obj = fact.getNextObject();
    sb.setLength(0);
    valList.setLength(0);
    
    sb.append("insert into HairyObjects (_id");
    valList.append('\'').append(obj.getId()).append("'");
    
    for( HairyAttribute at : obj.getAttributes() )
    {
     sb.append(',').append(at.getCls().getName());
     
     if(at.getCls().getType() == TYPE.STRING )
      valList.append(",'").append(at.getValue().toString()).append('\'');
     else if( at.getCls().getType() == TYPE.INT ) 
      valList.append(",").append(at.getValue());
     else if( at.getCls().getType() == TYPE.REAL ) 
      valList.append(",").append(at.getValue());
     else
      valList.append(",").append(at.getValue());
    }
    
    sb.append(") values (").append(valList.toString()).append(')');

//    System.out.println(sb.toString());
    
    stmt.executeUpdate(sb.toString());

    if( (i % 100) == 0)
     System.out.println("Done: "+i+" ("+100L*i/OBJECTS+"%) ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");

   }
   
   System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(OBJECTS/tm*1000));

   
   stmt.close();
   conn.close();
  }
  catch(SQLException e)
  {
   System.err.println("Could not connect to the database.\n");
   e.printStackTrace();
   return;
  }
 } // end of main method

}
