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
					parameters("name",name,"actorId", actorId)));
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
	
	public void addStreamingService(String name, String serviceId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MERGE (a:Streaming-Service {name: $name, serviceId: $serviceId})", 
					parameters("name",name,"serviceId", serviceId)));
			session.close();
		}
	}
	
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
	
}
