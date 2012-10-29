package neo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.kernel.Traversal;

import com.pri.util.StringUtils;
import common.Camera;

public class QueryNeo
{
 
 static class Ev implements Evaluator
 {
  Matcher mch = Pattern.compile("me222-2").matcher("");
  int i=0;
  long tm = System.currentTimeMillis();
  
  @Override
  public Evaluation evaluate(Path arg0)
  {
   i++;

   if(i % 10000 == 0)
    System.out.println("Node " + i + " (" + (i * 1000.0 / (System.currentTimeMillis() - tm)) + "node/s)");

   
   Node nod = arg0.endNode();
   
   
   
   if( ! nod.hasProperty("last name") )
    return Evaluation.EXCLUDE_AND_CONTINUE;
   
   String ln = (String)nod.getProperty("last name");

   mch.reset(ln);
   
   if( mch.find() )
    return Evaluation.INCLUDE_AND_CONTINUE;
   
   return Evaluation.EXCLUDE_AND_CONTINUE;
  }
  
 }
 
 public enum RelTypes implements RelationshipType
 {
     hasCamera,
     fixedPerson
 }


 // 1 rec tx -> 31 rec/s
 // 1000 rec tx -> 6500-4800-... rec/s
 
 public static void main(String[] args)
 {
  GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "n:/neo" );

  Node root = graphDb.getReferenceNode();
  
  int i=0;
  long tm = System.currentTimeMillis();

  
  for(Path position : Traversal.description().depthFirst().evaluator(new Ev()).traverse(root))
  {
   System.out.println("Found: "+position.endNode().getSingleRelationship(RelTypes.fixedPerson, Direction.INCOMING).getStartNode().getProperty("city"));

  }
  
  tm = System.currentTimeMillis() - tm;
  
  System.out.println("Time: "+StringUtils.millisToString(tm)+" Rate: "+(1_000_000/tm*1000));

 }

}
