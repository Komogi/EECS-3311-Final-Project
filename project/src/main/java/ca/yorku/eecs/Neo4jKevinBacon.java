package ca.yorku.eecs;
import static org.neo4j.driver.v1.Values.parameters;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

import com.sun.net.httpserver.HttpExchange;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			session.writeTransaction(tx -> tx.run("MERGE (m:Movie {name: $x, id: $y})", 
					parameters("x", name, "y", movieId)));
			session.close();
		}
	}
	
	public void addActor(String name, String actorId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MERGE (a:Actor {name: $name, id: $id})", 
					parameters("name", name,"id", actorId)));
			session.close();
		}
	}
	
	public void addRelationship(String actorId, String movieId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MATCH (a:Actor {id:$x}),"
					+ "(m:Movie {id:$y})\n" + 
					 "MERGE (a)-[r:ACTED_IN]->(m)\n" , parameters("x", actorId, "y", movieId)));
			session.close();
		}
	}
	
	public String addStreamingService(String name, String streamingServiceId) {
		
		String result = "200";
		
		try (Session session = driver.session()){
			
			try (Transaction tx = session.beginTransaction()) {
				StatementResult statementResult = tx.run("MATCH (s:StreamingService) "
						+ "WHERE s.streamingServiceId=$streamingServiceId RETURN count(s)", 
						parameters("streamingServiceId", streamingServiceId));
				
				String queryResult = statementResult.next().toString();
				
				// Getting the count
				queryResult = queryResult.substring(queryResult.length() - 3, queryResult.length() - 2);
				
				if (queryResult.equals("1")) {
					result = "404";
				}
				else {
					 tx.run("MERGE (s:StreamingService {name: $name, streamingServiceId: $streamingServiceId})", 
							parameters("name", name, "streamingServiceId", streamingServiceId));
				}
			}
			
			session.close();
		}
		
		return result;
	}
	
	public String addStreamingOnRelationship(String movieId, String streamingServiceId) {
		
		String result = "200";
		
		try (Session session = driver.session()){
			
			try (Transaction tx = session.beginTransaction()) {
				
				StatementResult statementResult = tx.run("MATCH (m:Movie) "
						+ "WHERE m.movieId=$movieId RETURN count(m)", 
						parameters("movieId", movieId));
				
				String queryResult = statementResult.next().toString();
				
				String movieCountResult = queryResult.substring(queryResult.length() - 3, queryResult.length() - 2);
				
				statementResult = tx.run("MATCH (s:StreamingService) "
						+ "WHERE s.streamingServiceId=$streamingServiceId RETURN count(s)", 
						parameters("streamingServiceId", streamingServiceId));
				
				queryResult = statementResult.next().toString();
				
				String streamingServiceCountResult = queryResult.substring(queryResult.length() - 3, queryResult.length() - 2);
				
				if (movieCountResult.equals("0") || streamingServiceCountResult.equals("0")) {
					result = "404";
				}
				else {
					statementResult = tx.run("MATCH (m:Movie {movieId:$x})-[r:STREAMING_ON]->(s:StreamingService {streamingServiceId:$y})\n" + 
							 "RETURN count(r)\n" , parameters("x", movieId, "y", streamingServiceId));
					
					queryResult = statementResult.next().toString();
					
					String streamingOnCountResult = queryResult.substring(queryResult.length() - 3, queryResult.length() - 2);
					
					System.out.println(streamingOnCountResult);
					
					if (streamingOnCountResult.equals("1")) {
						result = "400";
					}
					else {
						tx.run("MATCH (m:Movie {movieId:$x}), (s:StreamingService {streamingServiceId:$y})\n" + 
								 "MERGE (m)-[r:STREAMING_ON]->(s)\n" , parameters("x", movieId, "y", streamingServiceId));
					}
				}
			}

			session.close();
		}
		
		return result;
	}
	
	// GET REQUESTS
	 

	public String getMovie(String movieId) {
		String result = "";
		String tmp2 = "";
        ArrayList<String> tmp = new ArrayList<String>();
        
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult node_boolean = tx.run("MATCH(m:Movie) WHERE m.movieId=$x RETURN m.name"
                        ,parameters("x", movieId) );

                StatementResult node_boolean2 = tx.run("MATCH (a)-[:ACTED_IN]->(m) WHERE m.movieId=$x RETURN a.actorId",parameters("x",movieId));
                
                while(node_boolean.hasNext()) {
                    
                    tmp.add(node_boolean.next().toString());
                }
                while(node_boolean2.hasNext()) {
                    tmp.add(node_boolean2.next().toString());
                }
            }
            for(int i = 0; i < tmp.size(); i++) {
            	
            	tmp2=tmp.get(i).split("\"")[2];
            	tmp2= tmp2.substring(0,tmp2.length()-1);
            	tmp.set(i, tmp2);
            }

            result = String.format("{\n \"movieId\": %s,\n", movieId);
            result += String.format(" \"name\": \"%s\",\n ",tmp.get(0) );
            result += "\"actors\": [\n";
            for(int i = 1; i < tmp.size() - 1;i++) {
            	result+= String.format("    \"%s\",\n",tmp.get(i));
            }
            result+= String.format("    \"%s\",\n",tmp.get(tmp.size() - 1));
            result+="    ]\n}";
            
            session.close();
        }
        return result;
	}
	
	
	public String getActor(String actorId) {
		String result = "";
		String tmp2 = "";
        
        ArrayList<String> tmp = new ArrayList<String>();
        
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult node_boolean = tx.run("MATCH(a:Actor) WHERE a.actorId=$x RETURN a.name"
                        ,parameters("x", actorId) );

                StatementResult node_boolean2 = tx.run("MATCH (a)-[:ACTED_IN]->(m) WHERE a.actorId=$x RETURN m.movieId",parameters("x",actorId));
                
                while(node_boolean.hasNext()) {
                    tmp.add(node_boolean.next().toString());
                }
                
                while(node_boolean2.hasNext()) {
                    tmp.add(node_boolean2.next().toString());
                }
            }
           
            for(int i = 0; i < tmp.size(); i++) {
            	
            	tmp2=tmp.get(i).split("\"")[2];
            	tmp2= tmp2.substring(0,tmp2.length()-1);
            	tmp.set(i, tmp2);
            }

            result = String.format("{\n \"actorId\": %s,\n", actorId);
            result += String.format(" \"name\": \"%s\",\n ",tmp.get(0) );
            result += "\"movies\": [\n";
            for(int i = 1; i < tmp.size() - 1;i++) {
            	result+= String.format("    \"%s\",\n",tmp.get(i));
            }
            result+= String.format("    \"%s\",\n",tmp.get(tmp.size() - 1));
            result+="    ]\n}";
            
            session.close();
        }
        return result;
	}
	
//	"MATCH (a:Actor), (m:Movie) WHERE a.actorId=$x AND  m.movieId=$y "
//+ "RETURN EXISTS ((a)-[:ACTED_IN]->(m))" , 
//parameters("x",actorId, "y", movieId)));
	
	public String hasRelationship(String actorId, String movieId) {
		String result = "";
		String[] a = new String[10];
		String ans = "";
		
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult node_boolean = tx.run("OPTIONAL MATCH (a:Actor), (m:Movie) WHERE a.actorId=$x AND  m.movieId=$y "
                		+ "RETURN ((a)-[:ACTED_IN]->(m)) IS NOT NULL AS Predicate" , 
                		parameters("x",actorId, "y", movieId));

                ans = node_boolean.single().toString();
            }
            

               a = ans.split(":");
               int i = 0;  
               ans="";
               while(i < 5) {
            	   ans+= a[1].charAt(i);
            	   i++;
               }

        }
		result = "{\n";
		result+= String.format(" \"actorId\" : %s,\n \"movieId\" : %s,\n \"hasRelationship\" : %s\n}", actorId,movieId,ans.toLowerCase());
		 
		return result;
	}
	
	public String hasActor(String actorId) {
//		boolean result = false;
		String[] a = new String[10];
		String ans = "";
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult node_boolean = tx.run("OPTIONAL MATCH (a:Actor) WHERE a.actorId=$x"
                		+ " RETURN a IS NOT NULL AS Predicate" , 
                		parameters("x",actorId));

                ans = node_boolean.single().toString();
            }
               
               a = ans.split(":");
               
               int i = 0;  
               ans="";
               while(i < 5) {
            	   ans+= a[1].charAt(i);
            	   i++;
               }
        }
		return ans;
	}
	
	public String hasMovie(String movieId) {
		String[] a = new String[10];
		String ans = "";
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult node_boolean = tx.run("OPTIONAL MATCH (m:Movie) WHERE m.movieId=$x"
                		+ " RETURN m IS NOT NULL AS Predicate" , 
                		parameters("x",movieId));

                ans = node_boolean.single().toString();
            }
              
               a = ans.split(":");
               
               int i = 0;  
               ans="";
               while(i < 5) {
            	   ans+= a[1].charAt(i);
            	   i++;
               }
        }
		return ans;
	}
	
	public String computeBaconNumber(String actorId) {
		
		String result = "";
        
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (start:Actor {actorId:$x}), "
            			+ "(end:Actor {actorId:$y}), p = shortestPath((start)-[:ACTED_IN*]-(end)) "
            			+ "RETURN length(p)",
                		parameters("x", actorId, "y", "\"nm0000102\""));
            	
            	String queryResult = statementResult.next().toString();
            	
            	//System.out.println(queryResult);
            	queryResult = queryResult.substring(queryResult.length() - 3, queryResult.length() - 2);
            	
            	result = String.format("{\n");
                result += "\"baconNumber\": " + Integer.parseInt(queryResult)/2 + "\n";
                result += "}\n";
            }
            
            session.close();
        }
        
        return result;
		
	}
	
	public String computeBaconPath(String actorId) {
		String result = "";
		String[] unprocessed = new String[100];
		ArrayList<String> processed = new ArrayList();
        
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (start:Actor {actorId:$x}), "
            			+ "(end:Actor {actorId:$y}), p = shortestPath((start)-[:ACTED_IN*]-(end)) "
            			+ "RETURN p",
                		parameters("x", actorId, "y", "\"nm0000102\""));
            	
            	ArrayList<String> records = new ArrayList<String>();
            	
            	while (statementResult.hasNext()) {         		
            		records.add(statementResult.next().toString());
            	}
            	
            	for(int i = 0; i < records.size(); i++) {
                	
            		System.out.println(records.get(i));
            		
                	unprocessed = records.get(i).split("-");
                }
            	
            	processed.add(unprocessed[0].substring(unprocessed[0].length() - 2, unprocessed[0].length() - 1));
            	
            	int indexOffset = 1;
            	
            	for (int i = 2; i < unprocessed.length; i += 2) {
            		processed.add(unprocessed[i].substring(1 + indexOffset, 2 + indexOffset));
            		
            		if (indexOffset == 1) {
            			indexOffset--;
            		}
            		else {
            			indexOffset++;
            		}
            	}
            	
            	records = new ArrayList<String>();
            	
            	for(int i = 0; i < processed.size(); i++) {
                	
            		statementResult = tx.run("MATCH (n) WHERE id(n)=$x RETURN n.id",
                    		parameters("x", processed.get(i)));
            		
            		while (statementResult.hasNext()) {         		
                		records.add(statementResult.next().toString());
                	}
                }
            	
            	for(int i = 0; i < records.size(); i++) {
                	
                	String tmp = records.get(i).split("\"")[2];
                	tmp = tmp.substring(0, tmp.length()-1);
                	records.set(i, tmp);
                	
                	System.out.println(records.get(i));
                }
            	
            	
            	result = String.format("{\n");
                result += "\"baconNumber\": \n";
                result += "}\n";
            }
            
            session.close();
        }
        
        return result;
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
	public String getMoviesOnStreamingService(String streamingServiceId) {
		
		String result = "";
		String tmp = "";
        ArrayList<String> records = new ArrayList<String>();
        
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (s) WHERE s.streamingServiceId=$x RETURN count(s)",
                		parameters("x", streamingServiceId));
            	
            	String queryResult = statementResult.next().toString();
            	String streamingServiceCount = queryResult.substring(queryResult.length() - 3, queryResult.length() - 2);
            	
            	if (streamingServiceCount.equals("0")) {
            		result = "404";
            	}
            	else {
            		StatementResult moviesOnStreamingServiceResult = tx.run("MATCH (m)-[:STREAMING_ON]->(s) WHERE s.streamingServiceId=$x RETURN m.movieId",
                    		parameters("x", streamingServiceId));

                    while(moviesOnStreamingServiceResult.hasNext()) {
                        records.add(moviesOnStreamingServiceResult.next().toString());
                    }
                    
                    for(int i = 0; i < records.size(); i++) {
                    	
                    	tmp = records.get(i).split("\"")[2];
                    	tmp = tmp.substring(0, tmp.length() - 1);
                    	records.set(i, tmp);
                    }

                    result = String.format("{\n");
                    result += "\"movies\": [\n";
                    for(int i = 0; i < records.size() - 1; i++) {
                    	result += String.format("    \"%s\",\n", records.get(i));
                    }
                    result += String.format("    \"%s\"\n", records.get(records.size() - 1));
                    result +="    ]\n}";
            	}
            }
            
            session.close();
        }
		
		return result;
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
        	
        	session.close();
        }
		
		return node_boolean;
	}
	
	// TODO: Calculate Most Prolific Actor and Return It
	public String getMostProlificActor() {
		
		String result = "";
		String mostProlificActorId = "";
		StatementResult node_boolean;
		
		ArrayList<String> actors = new ArrayList<String>();
		HashMap<String, Integer> actorsCount = new HashMap<String, Integer>();
		
		try (Session session = driver.session())
        {
        	try (Transaction tx = session.beginTransaction()) {
        		node_boolean = tx.run("MATCH (a:Actor)-[r:ACTED_IN]->(m:Movie) RETURN a.actorId");
        	}
        	
        	while (node_boolean.hasNext()) {
        		actors.add(node_boolean.next().toString());
        	}

        	
        	// Isolate actorIDs
	    	for(int i = 0; i < actors.size(); i++) {        	
	         	String tmp = actors.get(i).split("\"")[2];
	         	tmp = tmp.substring(0, tmp.length() - 1);
	         	actors.set(i, tmp);
	        }
	    	
//	    	for(int i = 0; i < actors.size(); i++) {
//        		System.out.println(actors.get(i));
//        	}
	    	// Add and Count all distinct Actors
	    	for(int i = 0; i < actors.size(); i++) {
	    		if(!actorsCount.containsKey(actors.get(i))) {
	    			actorsCount.put(actors.get(i), 1);
	    		}
	    		else {
	    			actorsCount.put(actors.get(i), actorsCount.get(actors.get(i))+1);
	    		}
	    	}
	    	
//	    	for(String x: actorsCount.keySet()) {
//	    		String key = x;
////	    	    int value = actorsCount.get(x);
//	    	    System.out.println(key + " " );	
//	    	}
	    	Integer max = 0;
        	
	    	// Get Actor with the highest Count
	    	for (String actorId: actorsCount.keySet()) {
	    		if (actorsCount.get(actorId) > max) {
	    			max = actorsCount.get(actorId);
	    			mostProlificActorId = actorId;
	    		}
	    	}
	    	
	    	

	    	// result = getActor(mostProlificActorId);
        }
//		result = this.getActor("a123456");
		System.out.println("Most Prolific Actor's Id is: " + mostProlificActorId);
//		String result = "";
		String tmp2 = "";
        
        ArrayList<String> tmp = new ArrayList<String>();
        
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult node_boolean2 = tx.run("MATCH(a:Actor) WHERE a.actorId=$x RETURN a.name"
                        ,parameters("x", mostProlificActorId) );

                StatementResult node_boolean3 = tx.run("MATCH (a)-[:ACTED_IN]->(m) WHERE a.actorId=$x RETURN m.movieId",parameters("x",mostProlificActorId));
                
                while(node_boolean.hasNext()) {
                    tmp.add(node_boolean2.next().toString());
                }
                
                while(node_boolean2.hasNext()) {
                    tmp.add(node_boolean3.next().toString());
                }
            }
           
            for(int i = 0; i < tmp.size(); i++) {
            	
            	tmp2=tmp.get(i).split("\"")[2];
            	tmp2= tmp2.substring(0,tmp2.length()-1);
            	tmp.set(i, tmp2);
            }

            result = String.format("{\n \"actorId\": %s,\n", mostProlificActorId);
            result += String.format(" \"name\": \"%s\",\n ",tmp.get(0) );
            result += "\"movies\": [\n";
            for(int i = 1; i < tmp.size() - 1;i++) {
            	result+= String.format("    \"%s\",\n",tmp.get(i));
            }
            result+= String.format("    \"%s\",\n",tmp.get(tmp.size() - 1));
            result+="    ]\n}";

        }
		
		return result;
	}
	
}
