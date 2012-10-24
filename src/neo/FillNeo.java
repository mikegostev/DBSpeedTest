package neo;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class FillNeo
{
 public enum RelTypes implements RelationshipType
 {
     hasCamera,
     fixedPerson
 }


 // 1 rec tx -> 31 rec/s
 // 1000 rec tx -> 6500-4800-... rec/s
 
 public static void main(String[] args)
 {
  GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "x:/dev/neo" );

  Node root = graphDb.getReferenceNode();
  
  long tm = System.currentTimeMillis();
  
  Transaction tx = null;
  
  for( int i=0; i < 20_000_000; i++ )
  {
   if( i % 1000 == 0 )
   {
    System.out.println("Rec "+i+" ("+(i*1000.0/(System.currentTimeMillis()-tm))+"rec/s)");
   
    if( tx != null )
    {
     tx.success();
     tx.finish();
    }

    tx = graphDb.beginTx();
   }
   
   
   Node place = graphDb.createNode();
   
   place.setProperty("city", "city"+i);
   place.setProperty("country", "country"+(i/100));
   
   root.createRelationshipTo(place, RelTypes.hasCamera);

   for( int j=0; j < 3; j++)
   {
    Node pers = graphDb.createNode();
    
    pers.setProperty("last name", "surname"+i+"-"+j);
    pers.setProperty("first name", "name"+i+"-"+j);

    place.createRelationshipTo(pers, RelTypes.fixedPerson);
   }
   
  }
  
  tx.success();
  tx.finish();
  
 }

}
