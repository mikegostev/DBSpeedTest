package common;

public class HairyObjectFactory
{
 private final HairyClassPool clsPool;
 private final int nAttrs;
 private int idGen=1;
 
 public HairyObjectFactory(int nCls, int nAttr)
 {
  clsPool = new HairyClassPool(nCls);
  this.nAttrs = nAttr;
 }
 
 public HairyObject getNextObject()
 {
  HairyObject o = new HairyObject();
  
  o.setId("obj"+idGen++);
  
  for( int i=0; i< nAttrs; i++)
  {
   HairyClass cl = null;
   
   search: while( true )
   {
    cl = clsPool.getRandomClass();
   
    for( HairyAttribute a : o.getAttributes() )
    {
     if( a.getCls() == cl )
      continue search;
    }
    
    break;
   }
   
   switch (cl.getType())
   {
   case STRING:
    
    o.addAttribute( new HairyAttribute(cl, "val-"+(idGen%100) ) );
    
    break;

   case INT:
    
    o.addAttribute( new HairyAttribute(cl, idGen%100) );
    
    break;
    
   case BOOLEAN:
    
    o.addAttribute( new HairyAttribute(cl, new Boolean( i%2 == 0 ) ) );
    
    break;
    
   case REAL:
    
    o.addAttribute( new HairyAttribute(cl, new Double((double)idGen+i) ) );
    
    break;
    
   default:
    break;
   }
  }
  
  return o;
 }

 public HairyClassPool getClassPool()
 {
  return clsPool;
 }
}
