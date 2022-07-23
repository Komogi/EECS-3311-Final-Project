package ca.yorku.eecs;
import static org.neo4j.driver.v1.Values.parameters;


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
	
	public void insertMovie(String name, String movieId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MERGE (m:Movie {name: $x, movieId: $y})", 
					parameters("x", name, "y", movieId)));
			session.close();
		}
	}
}
