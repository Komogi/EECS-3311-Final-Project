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
	/* 
	 * Adds an Movie node to the database
	 * 
	 * @param 	name		the 'Name' of the Movie to be added
	 * @param 	movieId	the 'id' of the Movie to be added
	 * @return 	void
	 */
	public void addMovie(String name, String movieId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MERGE (m:Movie {Name: $name, id: $id})", 
					parameters("name", name, "id", movieId)));
			session.close();
		}
	}
	
	/* 
	 * Adds an Actor node to the database
	 * 
	 * @param 	name		the 'Name' of the Actor to be added
	 * @param 	actorId	the 'id' of the Actor to be added
	 * @return 	void
	 */
	
	public void addActor(String name, String actorId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MERGE (a:Actor {Name: $name, id: $id})", 
					parameters("name", name, "id", actorId)));
			session.close();
		}
	}

	/* 
	 * Adds an 'ACTED_IN' Relationship between the given Actor and Movie node
	 * 
	 * @param 	actorId		the 'id' of the Actor who has 'ACTED_IN' a Movie
	 * @param 	movieId		the 'id' of the Movie being 'ACTED_IN'
	 * 
	 * @return 	void
	 */
	
	
	public void addRelationship(String actorId, String movieId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MATCH (a:Actor {id:$x}),"
					+ "(m:Movie {id:$y})\n" + 
					 "MERGE (a)-[r:ACTED_IN]->(m)\n" , parameters("x", actorId, "y", movieId)));
			session.close();
		}
	}
	
	public void addStreamingService(String name, String streamingServiceId) {
		
		try (Session session = driver.session()){
			
			session.writeTransaction(tx -> tx.run("MERGE (s:StreamingService {Name: $name, streamingServiceId: $streamingServiceId})", 
					parameters("name", name, "streamingServiceId", streamingServiceId)));
			
			session.close();
		}
	}
	
	public void addStreamingOnRelationship(String movieId, String streamingServiceId) {
		
		try (Session session = driver.session()){
			
			session.writeTransaction(tx -> tx.run("MATCH (m:Movie {id:$id}), (s:StreamingService {streamingServiceId:$streamingServiceId})"
					+ "MERGE (m)-[r:STREAMING_ON]->(s)", 
					parameters("id", movieId, "streamingServiceId", streamingServiceId)));
			
			session.close();
		}
	}
	
	// GET REQUESTS
	/* 
	 * Gets the Name of the given Movie and list of Actors Acting in it
	 * 
	 * @param 	movieId		the 'id' of the Movie we want to Get
	 * 			
	 * @return 	result		String containing the movieId, Name and all the Actors Acting in the Movie
	 */

	public String getMovie(String movieId) {
		String result = "";
		String processedRecords = "";
        ArrayList<String> records = new ArrayList<String>();
        
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult node_boolean = tx.run("MATCH(m:Movie) WHERE m.id=$id RETURN m.Name"
                        ,parameters("id", movieId) );

                StatementResult node_boolean2 = tx.run("MATCH (a)-[:ACTED_IN]->(m) WHERE m.id=$id RETURN a.id",parameters("id",movieId));
                
                while(node_boolean.hasNext()) {
                    
                    records.add(node_boolean.next().toString());
                }
                while(node_boolean2.hasNext()) {
                    records.add(node_boolean2.next().toString());
                }
            }
            for(int i = 0; i < records.size(); i++) {
            	
            	processedRecords=records.get(i).split("\"")[1];
            	processedRecords= processedRecords.substring(0,processedRecords.length());
            	records.set(i, processedRecords);
            }

            result = String.format("{\n \"movieId\": %s,\n", movieId);
            result += String.format(" \"name\": \"%s\",\n ",records.get(0) );
            result += "\"actors\": [\n";
            for(int i = 1; i < records.size() - 1;i++) {
            	result+= String.format("    \"%s\",\n",records.get(i));
            }
            result+= String.format("    \"%s\"\n",records.get(records.size() - 1));
            result+="    ]\n}";
            
            session.close();
        }
        return result;
	}
	
	/* 
	 * Gets the Name of the given Actor and list of Movies the Actor is Acting in
	 * 
	 * @param 	actorId		'id' of the Actor we want to Get
	 * 			
	 * @return 	result		String containing the actorId, Name and all the Movies the Actor has ACTED_IN
	 */
	public String getActor(String actorId) {
		String result = "";
		String processedRecords = "";
        
        ArrayList<String> records = new ArrayList<String>();
        
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult node_boolean = tx.run("MATCH(a:Actor) WHERE a.id=$id RETURN a.Name"
                        ,parameters("id", actorId) );

                StatementResult node_boolean2 = tx.run("MATCH (a)-[:ACTED_IN]->(m) WHERE a.id=$id RETURN m.id",parameters("id",actorId));
                
                while(node_boolean.hasNext()) {
                    records.add(node_boolean.next().toString());
                }
                
                while(node_boolean2.hasNext()) {
                    records.add(node_boolean2.next().toString());
                }
            }
           
            
            for(int i = 0; i < records.size(); i++) {
            	
            	processedRecords=records.get(i).split("\"")[1];
            	processedRecords= processedRecords.substring(0,processedRecords.length());

            	records.set(i, processedRecords);
            }

            result = String.format("{\n \"actorId\": %s,\n", actorId);
            result += String.format(" \"name\": \"%s\",\n ",records.get(0) );
          
           
            result += "\"movies\": [\n";
            for(int i = 1; i < records.size() - 1;i++) {
            	result+= String.format("    \"%s\",\n",records.get(i));
            	
            }
            result+= String.format("    \"%s\"\n",records.get(records.size() - 1));
            result+="    ]\n}";
            
            session.close();
        }
        return result;
	}
	
	/* 
	 * Tells if the given actorId and movieId have an ACTED_IN Relationship between them or not
	 * 
	 * @param 	movieId		the 'id' of the Movie we want to Get
	 * 			
	 * @return 	result		String containing the actorId, movieId and a boolean telling if there is an
	 * 						ACTED_IN Relationship present between the given Actor and Movie
	 */
	
	public String hasRelationship(String actorId, String movieId) {
		String result = "";
		String[] splitRecords = new String[10];
		String records = "";
		
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult node_boolean = tx.run("OPTIONAL MATCH (a:Actor), (m:Movie) WHERE a.id=$x AND  m.id=$y "
                		+ "RETURN exists((a)-[:ACTED_IN]->(m))" , 
                		parameters("x",actorId, "y", movieId));


                while(node_boolean.hasNext()) {
            		records+=node_boolean.next().toString();
            	}
               
            }
            	

               splitRecords = records.split(":");
              
              
               int i = 1;  
               records="";
               while(i < 6) {
            	   records+= splitRecords[2].charAt(i);
            	   i++;
               }

        }

		if(records.toLowerCase().equals("true}")) {
			records = "true";
		}
		result = "{\n";
		result+= String.format(" \"actorId\" : %s,\n \"movieId\" : %s,\n \"hasRelationship\" : %s\n}", actorId,movieId,records.toLowerCase());
		 
		return result;
	}
	
	/* 
	 * Tells if Database base contains the given Actor node or not
	 * 
	 * @param 	actorId		'id' of the Actor we want to check 
	 * 			
	 * @return 	result		String saying 'true' or 'false' depending on if the database contains the given
	 * 						actorId or not
	 */
	
	public String hasActor(String actorId) {

		String[] a = new String[10];
		String result = "";
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult node_boolean = tx.run("OPTIONAL MATCH (a:Actor) WHERE a.id=$x"
                		+ " RETURN a IS NOT NULL AS Predicate" , 
                		parameters("x",actorId));

                result = node_boolean.single().toString();
            }
               
               a = result.split(":");
               
               int i = 0;  
               result="";
               while(i < 5) {
            	   result+= a[1].charAt(i);
            	   i++;
               }
        }
		return result;
	}
	
	/* 
	 * Tells if there exists a Relationship between the given Actor and Movie
	 * 
	 * @param 	actorId		'id' of the Actor we want to check 
	 * 			
	 * @return 	result		String saying 'true' or 'false' depending on if the database contains a Relationship 
	 * 						between the given actorId and movieId
	 */
	
	public String hasRel(String actorId, String movieId) {
		String[] a = new String[10];
		String result = "";
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	StatementResult node_boolean = tx.run("OPTIONAL MATCH (a:Actor), (m:Movie) WHERE a.id=$x AND  m.id=$y "
                		+ "RETURN exists((a)-[:ACTED_IN]->(m))" , 
                		parameters("x",actorId, "y", movieId));

                result = node_boolean.single().toString();
            }
               
            a = result.split(":");

            int i = 1;  
            result="";
            while(i < 6) {
         	   result+= a[2].charAt(i);
         	   i++;
            }

     }

		if(result.toLowerCase().equals("true}")) {
			result = "true";
		}
               
        
		return result;
	}
	

	/* 
	 * Tells if Database base contains the given Movie node or not
	 * 
	 * @param 	movieId		'id' of the Movie we want to check 
	 * 			
	 * @return 	result		String saying 'true' or 'false' depending on if the database contains the given
	 * 					movieId or not
	 */

	public String hasMovie(String movieId) {
		String[] a = new String[10];
		String result = "";
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult node_boolean = tx.run("OPTIONAL MATCH (m:Movie) WHERE m.id=$x"
                		+ " RETURN m IS NOT NULL AS Predicate" , 
                		parameters("x",movieId));

                result = node_boolean.single().toString();
            }
              
               a = result.split(":");
               
               int i = 0;  
               result="";
               while(i < 5) {
            	   result+= a[1].charAt(i);
            	   i++;
               }
        }
		return result;
	}
	
	/*
	 * Computes the Bacon Number
	 * 
	 * @param 	actorId		'id' of the Actor we want to calculate the Bacon Number for
	 * 
	 * @return 	String containing the baconNumber of the given Actor
	 */
	public String computeBaconNumber(String actorId) {
		
		String result = "";
        
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (start:Actor {id:$x}), "
            			+ "(end:Actor {id:$y}), p = shortestPath((start)-[:ACTED_IN*]-(end)) "
            			+ "RETURN length(p)",
                		parameters("x", actorId, "y", "nm0000102"));
            	
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
		ArrayList<String> processed = new ArrayList<String>();
        
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (start:Actor {id:$x}), "
            			+ "(end:Actor {id:$y}), p = shortestPath((start)-[:ACTED_IN*]-(end)) "
            			+ "RETURN p",
                		parameters("x", actorId, "y", "nm0000102"));
            	
            	ArrayList<String> records = new ArrayList<String>();
            	
            	while (statementResult.hasNext()) {         		
            		records.add(statementResult.next().toString());
            	}
            	
            	for(int i = 0; i < records.size(); i++) {
            		
                	unprocessed = records.get(i).split("-");
                
                }
            	
            	for (int i = 0; i < unprocessed.length; i += 2) {
            		processed.add(findId(unprocessed[i]));
            		
            	}
            	/*for(int i =0; i < processed.size(); i++) {
            		System.out.println(processed.get(i));
            	}*/
            	records = new ArrayList<String>();
            	
            	for(int i = 0; i < processed.size(); i++) {
                	
            		statementResult = tx.run("MATCH (n) WHERE id(n)=$x RETURN n.id",
                    		parameters("x", Integer.parseInt(processed.get(i))));
//            		System.out.println(statementResult.hasNext());
            		while (statementResult.hasNext()) {         		
                		records.add(statementResult.next().toString());
                	}
                }
            	
            	for(int i = 0; i < records.size(); i++) {
                	
                	String tmp = records.get(i).split("\"")[1];
                	tmp = tmp.substring(0, tmp.length());
                	records.set(i, tmp);
                	
                	// System.out.println(records.get(i));
                }
            	
            	result = String.format("{\n");
            	result += "\t\"baconPath\": [\n";
            	for(int i = 0; i < records.size() - 1; i++) {
            		result += String.format("\t\t\"%s\",\n", records.get(i));
            	
            	}
            	result += String.format("\t\t\"%s\"\n", records.get(records.size()-1));
                result += "\t]\n}\n";
            }
            
            session.close();
        }
        
        return result;
	}
	
	// Private Helper for computeBaconPath to isolate Ids in a path
	private String findId(String str) {
		
		String result = "";
		boolean startOfNumber = false;
		boolean endOfNumber = false;
		int index = 0;
		
		while (endOfNumber == false) {
			if (Character.isDigit(str.charAt(index))) {
				result += str.charAt(index);
				startOfNumber = true;
			}
			else if (!Character.isDigit(str.charAt(index)) && startOfNumber == true) {
				endOfNumber = true;
			}
			
			index++;
		}
		
		return result;
	}
	
	public boolean hasPathToKevinBacon(String actorId) {
		
		boolean result = true;
		
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (start:Actor {id:$x}), "
            			+ "(end:Actor {id:$y}), p = shortestPath((start)-[:ACTED_IN*]-(end)) "
            			+ "RETURN p",
                		parameters("x", actorId, "y", "nm0000102"));
            	
            	if (!statementResult.hasNext()) {
            		result = false;
            	}
            }
            
            session.close();
        }
		
		return result;
	}

	public void addMovieStreamingServiceRelationship(String movieId, String streamingServiceId) {
		try (Session session = driver.session()){
			session.writeTransaction(tx -> tx.run("MATCH (m:Movie {id:$x}),"
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
            	
            	StatementResult moviesOnStreamingServiceResult = tx.run("MATCH (m)-[:STREAMING_ON]->(s) WHERE s.streamingServiceId=$x RETURN m.id",
                		parameters("x", streamingServiceId));

                while(moviesOnStreamingServiceResult.hasNext()) {
                    records.add(moviesOnStreamingServiceResult.next().toString());
                }
                for(int i = 0; i < records.size(); i++) {
                	System.out.println(records.get(i));
                }
                for(int i = 0; i < records.size(); i++) {
                	
                	tmp = records.get(i).split("\"")[1];
                	tmp = tmp.substring(0, tmp.length());
                	records.set(i, tmp);
                }
                
                for(int i = 0; i < records.size(); i++) {
                	System.out.println(records.get(i));
                }
                result = String.format("{\n");
                result += "\"movies\": [\n";
                for(int i = 0; i < records.size() - 1; i++) {
                	result += String.format("    \"%s\",\n", records.get(i));
                }
                result += String.format("    \"%s\"\n", records.get(records.size() - 1));
                result +="    ]\n}";
            }
            
            session.close();
        }
		
		return result;
	}
	
	public boolean hasMoviesOnStreamingService(String streamingServiceId) {
		
		boolean result = true;
		
		try (Session session = driver.session()){
			
			try (Transaction tx = session.beginTransaction()) {
				StatementResult statementResult = tx.run("MATCH (m:Movie)-[r:STREAMING_ON]->(s:StreamingService)\n"
						+ "WHERE s.streamingServiceId=$streamingServiceId RETURN m",
						parameters("streamingServiceId", streamingServiceId));
				
				if (!statementResult.hasNext()) {
					result = false;
				}
			}
			
			session.close();
		}
		
		System.out.println(result);
		
		return result;
	}
	
	// TOOD: Calculate Actor Number and Return It
	public String getActorNumber(String firstActorId, String secondActorId) {
		
		String result = "";
        
        try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (start:Actor {id:$x}), "
            			+ "(end:Actor {id:$y}), p = shortestPath((start)-[:ACTED_IN*]-(end)) "
            			+ "RETURN length(p)",
                		parameters("x", firstActorId, "y", secondActorId));
            	
            	String queryResult = statementResult.next().toString();
            	
            	//System.out.println(queryResult);
            	queryResult = queryResult.substring(queryResult.length() - 3, queryResult.length() - 2);
            	
            	result = String.format("{\n");
                result += "\"actorNumber\": " + Integer.parseInt(queryResult)/2 + "\n";
                result += "}\n";
            }
            
            session.close();
        }
        
        return result;
	}
	
	public boolean hasPathFromActorToActor(String firstActorId, String secondActorId) {
		
		boolean result = true;
		
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (start:Actor {id:$x}), "
            			+ "(end:Actor {id:$y}), p = shortestPath((start)-[:ACTED_IN*]-(end)) "
            			+ "RETURN p",
                		parameters("x", firstActorId, "y", secondActorId));
            	
            	if (!statementResult.hasNext()) {
            		result = false;
            	}
            }
            
            session.close();
        }
		
		return result;
	}
	
	// TODO: Calculate Most Prolific Actor and Return It
//	public String getMostProlificActor() {
//		
//		String result = "";
//		String mostProlificActorId = "";
//		StatementResult statementResult;
//		
//		ArrayList<String> actors = new ArrayList<String>();
//		HashMap<String, Integer> actorsCount = new HashMap<String, Integer>();
//		
//		try (Session session = driver.session())
//        {
//        	try (Transaction tx = session.beginTransaction()) {
//        		statementResult = tx.run("MATCH (a:Actor)-[r:ACTED_IN]->(m:Movie) RETURN a.id");
//        		
//        		while (statementResult.hasNext()) {
//            		actors.add(statementResult.next().toString());
//            	}
//            	
//            	// Isolate actorIDs
//    	    	for(int i = 0; i < actors.size(); i++) {        	
//    	         	String tmp = actors.get(i).split("\"")[2];
//    	         	tmp = tmp.substring(0, tmp.length() - 1);
//    	         	actors.set(i, tmp);
//    	        }
//    	    	
//    	    	// Add and Count all distinct Actors
//    	    	for(int i = 0; i < actors.size(); i++) {
//    	    		if(!actorsCount.containsKey(actors.get(i))) {
//    	    			actorsCount.put(actors.get(i), 1);
//    	    		}
//    	    		else {
//    	    			actorsCount.put(actors.get(i), actorsCount.get(actors.get(i))+1);
//    	    		}
//    	    	}
//    	    	
//    	    	Integer max = 0;
//            	
//    	    	// Get Actor with the highest Count
//    	    	for (String actorId: actorsCount.keySet()) {
//    	    		if (actorsCount.get(actorId) > max) {
//    	    			max = actorsCount.get(actorId);
//    	    			mostProlificActorId = actorId;
//    	    		}
//    	    	}
//    	    	
//    	    	System.out.println("Most Prolific Actor's Id is: " + mostProlificActorId);
//    	    	
//    	    	statementResult = tx.run("MATCH (a:Actor) WHERE a.id=$id RETURN a.name",
//                        parameters("id", mostProlificActorId));
//    	    	
//    	    	System.out.println(statementResult.hasNext());
//        	}
//        	
//        	session.close();
//        }
//		
//		return result;
//	}
	
	public String getMostProlificActor() {
		String result = "";
		
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (a)-[r:ACTED_IN]->(m) RETURN a.id, count(r) AS connections"
            			+ " ORDER BY connections DESC");
            	
            	String queryResult = statementResult.next().toString();
            	
            	String[] r = queryResult.split("\"");

            	String actor = r[1];
            	System.out.println(actor);
//            	actor = r[1].substring(0,actor.length()-1);
//            	actor = "\"" + actor + "\"";
            
            	result = this.getActor(actor);            

            }
            
            session.close();
        }
		
		return result;
	}
	
	public boolean hasActors() {
		boolean result = true;
		
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (a:Actor) "
            			+ "RETURN a LIMIT 1");
            	
            	if (!statementResult.hasNext()) {
            		result = false;
            	}
            }
            
            session.close();
        }
		
		return result;
	}
	
	public boolean hasActedInRelationships() {
		boolean result = true;
		
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (a)-[r:ACTED_IN]->(m) "
            			+ "RETURN type(r) LIMIT 1");
            	
            	if (!statementResult.hasNext()) {
            		result = false;
            	}
            }
            
            session.close();
        }
		
		return result;
	}
	
	public boolean hasStreamingService(String streamingServiceId) {
		boolean result = true;
		
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (s:StreamingService) "
            			+ "WHERE s.streamingServiceId=$streamingServiceId RETURN s LIMIT 1",
            			parameters("streamingServiceId", streamingServiceId));
            	
            	if (!statementResult.hasNext()) {
            		result = false;
            	}
            }
            
            session.close();
        }
		
		return result;
	}
	
	public boolean hasStreamingOnRelationship(String movieId, String streamingServiceId) {
		boolean result = true;
		
		try (Session session = driver.session())
        {
            try (Transaction tx = session.beginTransaction()) {
            	
            	StatementResult statementResult = tx.run("MATCH (m:Movie {id:$id}), (s:StreamingService {streamingServiceId:$streamingServiceId}) "
            			+ "WHERE (m)-[:STREAMING_ON]->(s) RETURN m LIMIT 1",
            			parameters("id", movieId, "streamingServiceId", streamingServiceId));
            	
            	if (!statementResult.hasNext()) {
            		result = false;
            	}
            }
            
            session.close();
        }
		
		return result;
	}
}
