package ca.yorku.eecs;
import static org.neo4j.driver.v1.Values.parameters;

import org.json.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;


public class Neo4jKevinBacon {

	private Driver driver;
	private String uriDb;
	
	public Neo4jKevinBacon() {
		uriDb = "bolt://localhost:7687"; // may need to change if you used a different port for your DBMS
		Config config = Config.builder().withoutEncryption().build();
		driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j","123456"), config);
	}
	
	// PUT REQUESTS
	public void addMovie(String name, String movieId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MERGE (m:Movie {name: $x, movieId: $y})", 
					parameters("x", name, "y", movieId)));
			session.close();
		}
	}
	
	public void addActor(String name, String actorId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MERGE (a:Actor {name: $name, actorId: $actorId})", 
					parameters("name", name,"actorId", actorId)));
			session.close();
		}
	}
	
	public void addRelationship(String actorId, String movieId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MATCH (a:Actor {actorId:$x}),"
					+ "(m:Movie {movieId:$y})\n" + 
					 "MERGE (a)-[r:ACTED_IN]->(m)\n" , parameters("x", actorId, "y", movieId)));
			session.close();
		}
	}
	
	public void addStreamingService(String name, String streamingServiceId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MERGE (s:StreamingService {name: $name, streamingServiceId: $streamingServiceId})", 
					parameters("name", name,"actorId", streamingServiceId)));
			session.close();
		}
	}
	
	// GET REQUESTS

	public JSONObject getMovie(String movieId) {
		JSONObject j = new JSONObject();
		return j;
	}
	
	public JSONObject getActor(String actorId) {
		JSONObject j = new JSONObject();
		return j;
	}
	
	public JSONObject hasRelationship(String actorId, String movieId) {
		JSONObject j = new JSONObject();
		return j;
	}
	
	public JSONObject computerBaconNumber(String actorId) {
		JSONObject j = new JSONObject();
		return j;
	}
	
	public JSONObject computeBaconPath(String actorId) {
		JSONObject j = new JSONObject();
		return j;
	}

	public void addMovieStreamingServiceRelationship(String movieId, String streamingServiceId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MATCH (m:Movie {movieId:$x}),"
					+ "(s:StreamingService {streamingServiceId:$y})\n" + 
					 "MERGE (m)-[r:STREAMING_ON]->(s)\n" , parameters("x", movieId, "y", streamingServiceId)));
			session.close();
		}
	}
	
	// TODO: Find all movies that has a STREAMING_ON relationship with the streamingServiceId, and return them
	public StatementResult getMoviesOnStreamingService(String streamingServiceId) {
		
		StatementResult node_boolean;
		
		try (Session session = driver.session())
        {
        	try (Transaction tx = session.beginTransaction()) {
        		node_boolean = tx.run("RETURN EXISTS( (:Movie)"
        				+ "-[:STREAMING_ON]-(:StreamingService {streamingServiceId: $x}) ) as bool"
						,parameters("x", streamingServiceId));
        	}
        }
		
		return node_boolean;
	}
	
	// TOOD: Calculate Actor Number and Return It
	public StatementResult getActorNumber(String actor1Id, String actor2Id) {
		StatementResult node_boolean;
		
		try (Session session = driver.session())
        {
        	try (Transaction tx = session.beginTransaction()) {
        		node_boolean = tx.run("RETURN EXISTS( (:Movie)"
        				+ "-[:STREAMING_ON]-(:StreamingService {streamingServiceId: $x}) ) as bool");
        	}
        }
		
		return node_boolean;
	}
	
	// TODO: Calculate Most Prolific Actor and Return It
	public StatementResult getMostProlificActor() {
		StatementResult node_boolean;
		
		try (Session session = driver.session())
        {
        	try (Transaction tx = session.beginTransaction()) {
        		node_boolean = tx.run("RETURN EXISTS( (:Movie)"
        				+ "-[:STREAMING_ON]-(:StreamingService {streamingServiceId: $x}) ) as bool");
        	}
        }
		
		return node_boolean;
	}
	
}
