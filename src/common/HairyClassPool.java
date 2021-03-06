package common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import common.HairyClass.TYPE;

public class HairyClassPool
{
 static final int INT_FRACTION=20;
 static final int REAL_FRACTION=20;
 static final int STRING_FRACTION=40;
 static final int BOOL_FRACTION=20;
 
 private final List<HairyClass> pool;
 
 private final Random rnd = new Random();

 public HairyClassPool( int sz )
 {
  pool = new ArrayList<>(sz);
  
  int oneLen = INT_FRACTION + REAL_FRACTION + STRING_FRACTION + BOOL_FRACTION;

  int i=0;
  int lim = STRING_FRACTION*sz/oneLen;
  
  for(; i< lim; i++)
   pool.add( new HairyClass("STR"+i, TYPE.STRING) );
   
  lim = (STRING_FRACTION+INT_FRACTION)*sz/oneLen;

  for(; i< lim; i++)
   pool.add( new HairyClass("INT"+i, TYPE.INT) );
   
  lim = (STRING_FRACTION+INT_FRACTION+REAL_FRACTION)*sz/oneLen;
  
  for(; i< lim; i++)
   pool.add( new HairyClass("REAL"+i, TYPE.REAL) );
   
  lim = sz;

  for(; i< lim; i++)
   pool.add( new HairyClass("BOOL"+i, TYPE.BOOLEAN) );
   

//  for( int i = 1; i <=sz; i++)
//  {
//   int r = rnd.nextInt(oneLen);
//   
//   if( r < STRING_FRACTION )
//    pool.add( new HairyClass("STR"+i, TYPE.STRING) );
//   else if( r < STRING_FRACTION+INT_FRACTION )
//    pool.add( new HairyClass("INT"+i, TYPE.INT) );
//   else if( r < STRING_FRACTION+INT_FRACTION+BOOL_FRACTION )
//    pool.add( new HairyClass("BOOL"+i, TYPE.BOOLEAN) );
//   else
//    pool.add( new HairyClass("REAL"+i, TYPE.REAL) );
//  }
  
 }
 
 public List<HairyClass> getClasses()
 {
  return pool;
 }
 
 public HairyClass getRandomClass()
 {
  return pool.get( rnd.nextInt(pool.size()) );
 }
 
 
}
