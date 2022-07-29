package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;
//import org.jcp.xml.dsig.internal.dom.Utils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RequestHandler implements HttpHandler{

    private Neo4jKevinBacon neo4j;
    private String handleMethod;
    
    public RequestHandler(Neo4jKevinBacon neo4j) {
        this.neo4j = neo4j;
    }
    
    @Override
    public void handle(HttpExchange request) throws IOException {
        // TODO Auto-generated method stub
    	
    	URI uri = request.getRequestURI();
    	String rawPath = uri.getRawPath();
        String query = uri.getQuery();
        
        ArrayList<String> splitRawPath = splitRawPath(rawPath);
    	
        if (splitRawPath.get(splitRawPath.size() - 1) == "v1") {
        	// throw error, send string?
        }
        else {
        	handleMethod = splitRawPath.get(splitRawPath.size() - 1);
        }
    	
        try {
             if(request.getRequestMethod().equals("PUT")) {
            	 
                 switch(handleMethod) {
	                 case "addMovie":
	                	 addMovie(request, splitQuery(query));
	                	 break;
	                	 
	                 case "addActor":
	                	 addActor(request, splitQuery(query));
	                	 break;
	                	 
	                 case "addRelationship":
	                	 addRelationship(request, splitQuery(query));
	                	 break;
	                	 
	                 case "addStreamingService":
	                	 addStreamingService(request, splitQuery(query));
	                	 break;
	                	 
	                 case "addStreamingOnRelationship":
	                	 addStreamingOnRelationship(request, splitQuery(query));
	                	 break;
                 }
             }
             else if (request.getRequestMethod().equals("GET")) {
            	 // TODO
            	 switch(handleMethod) {
					case "getMovie":
						getMovie(request, splitQuery(query));
						break;
						
					case "getActor":
						getActor(request, splitQuery(query));
						break;
						
					case "hasRelationship":
						hasRelationship(request, splitQuery(query));
						break;
						
					case "computeBaconNumber":
						computeBaconNumber(request, splitQuery(query));
						break;
						
					case "computeBaconPath":
						computeBaconPath(request, splitQuery(query));
						break;

            	 	 case "getMoviesOnStreamingService":
            	 		 getMoviesOnStreamingService(request, splitQuery(query));
            	 		 break;
            	 
	                 case "getActorNumber":
	                	 getActorNumber(request, splitQuery(query));
	                	 break;
	                	 
	                 case "getMostProlificActor":
	                	 getMostProlificActor(request);
	                	 break;
	             }
             } 
             else {
            	 sendString(request, "Request not found\n", 404);
             }
        } catch (Exception e) {
        	e.printStackTrace();
        	sendString(request, "Server error\n", 500);
        }
    }

    public void addMovie(HttpExchange request, Map<String, String> queryParam) throws IOException {
        
        String name;
        String movieId;
        
        if (queryParam.containsKey("name") && queryParam.containsKey("movieId")) {
            
            name =  queryParam.get("name");
            movieId = queryParam.get("movieId");
            
            neo4j.addMovie(name, movieId);
            
            String response = name + " added successfully.";
            sendString(request, response, 200);
        }
        else {
            String response = "The request body is improperly formatted or missing required information.";
            sendString(request, response, 400);
        }

    }
    
    public void addActor(HttpExchange request, Map<String, String> queryParam) throws IOException {
    	String name;
        String actorId;
        
        // add code for incorrect parameters
        if (queryParam.containsKey("name") && queryParam.containsKey("actorId")) {
            
            name =  queryParam.get("name");
            actorId = queryParam.get("actorId");
            
            neo4j.addActor(name, actorId);
            
            String response = name + " added successfully.";
            sendString(request, response, 200);
        }
        else {
            String response = "The request body is improperly formatted or missing required information.";
            sendString(request, response, 400);
        }
    }
    
    public void addRelationship(HttpExchange request, Map<String, String> queryParam) throws IOException {
    	String actorId;
    	String movieId;
    	
    	// TODO: If either actorId or movieId does not exist in the database, return 404
    	// TODO: If relationship already exists, return 400
    	
    	if (queryParam.containsKey("actorId") && queryParam.containsKey("movieId")) {
    		
    		actorId = queryParam.get("actorId");
    		movieId = queryParam.get("movieId");
    		
    		neo4j.addRelationship(actorId, movieId);
            
            String response = "ACTED_IN relationship added successfully.";
            sendString(request, response, 200);
    	}
    	else {
    		String response = "The request body is improperly formatted or missing required information.";
    		sendString(request, response, 400);
    	}
    }
    
    public void addStreamingService(HttpExchange request, Map<String, String> queryParam) throws IOException {
    	String name;
    	String streamingServiceId;
    	
    	// TODO: If streamingServiceId already exists in database, return 400
    	
    	if (queryParam.containsKey("name") && queryParam.containsKey("streamingServiceId")) {
    		
    		name = queryParam.get("name");
    		streamingServiceId = queryParam.get("streamingServiceId");
    		
    		neo4j.addStreamingService(name, streamingServiceId);
            
            String response = name + " added successfully.";
            sendString(request, response, 200);
    	}
    	else {
    		String response = "The request body is improperly formatted or missing required information.";
    		sendString(request, response, 400);
    	}
    }
    
    public void addStreamingOnRelationship(HttpExchange request, Map<String, String> queryParam) throws IOException {
    	String movieId;
    	String streamingServiceId;
    	
    	// TODO: If either movieId or streamingServiceId does not exist in the database, return 404
    	// TODO: If relationship already exists, return 400
    	
    	if (queryParam.containsKey("movieId") && queryParam.containsKey("streamingServiceId")) {
    		
    		movieId = queryParam.get("movieId");
    		streamingServiceId = queryParam.get("streamingServiceId");
    		
    		neo4j.addStreamingOnRelationship(movieId, streamingServiceId);
            
            String response = "STREAMING_ON relationship added successfully.";
            sendString(request, response, 200);
    	}
    	else {
    		String response = "The request body is improperly formatted or missing required information.";
    		sendString(request, response, 400);
    	}
    }
    
   public void getMoviesOnStreamingService(HttpExchange request, Map<String, String> queryParam) throws IOException {
    
    	String streamingServiceId;
    	
    	// TODO: If streamingServiceId does not exist in the datbaase, return 404
    	
    	if (queryParam.containsKey("streamingServiceId")) {
    		
    		streamingServiceId = queryParam.get("streamingServiceId");
    		
    		neo4j.getMoviesOnStreamingService(streamingServiceId); // TODO: assign to response
            
            String response = "???";
            sendString(request, response, 200);
    	}
    	else {
    		String response = "The request body is improperly formatted or missing required information.";
    		sendString(request, response, 400);
    	}
    }
    
    public void getActorNumber(HttpExchange request, Map<String, String> queryParam) throws IOException {
    	
    	String firstActorId;
    	String secondActorId;
    	
    	// TODO: If either firstActorId or secondActorId does not exist in the database, return 404
    	
    	if (queryParam.containsKey("firstActorId") && queryParam.containsKey("secondActorId")) {
    		
    		firstActorId = queryParam.get("firstActorId");
    		secondActorId = queryParam.get("secondActorId");
    		
    		neo4j.getActorNumber(firstActorId, secondActorId); // TODO: assign to response
            
            String response = "???";
            sendString(request, response, 200);
    	}
    	else {
    		String response = "The request body is improperly formatted or missing required information.";
    		sendString(request, response, 400);
    	}
    }
    
    public void getMostProlificActor(HttpExchange request) throws IOException {
	
		// TODO: If there are no actors in the database, return 404
    	// TODO: If there are actors in the database, but none have acted in movies, return 404
		
		String response = neo4j.getMostProlificActor();
        sendString(request, response, 200);
    }
    
    public void getMovie(HttpExchange request, Map<String, String> queryParam) throws IOException{
    	
    	String movieId = queryParam.get("movieId");
    	
    	String response = neo4j.getMovie(movieId);
    	sendString(request, response, 200);
    	
    }
    
    public void getActor(HttpExchange request, Map<String, String> queryParam) throws IOException{
    	String actorId = queryParam.get("actorId");
    	
    	String response = neo4j.getActor(actorId);
    	sendString(request, response, 200);
    }
    
    public void hasRelationship(HttpExchange request, Map<String, String> queryParam) throws IOException{
    	String actorId = queryParam.get("actorId");
    	String movieId = queryParam.get("movieId");
    	String response = neo4j.hasRelationship(actorId,movieId);
    	sendString(request, response, 200);
    }
    
    public void computeBaconNumber(HttpExchange request, Map<String, String> queryParam) throws IOException {
    
    	
    }
    
    public void computeBaconPath(HttpExchange request, Map<String, String> queryParam) throws IOException{
    	
    }
    private static ArrayList<String> splitRawPath(String rawPath) throws UnsupportedEncodingException {
        
    	ArrayList<String> result = new ArrayList<String>();
    	
        String[] splitRawPath = rawPath.split("/");
        
        for (int i = 0; i < splitRawPath.length; i++) {
        	result.add(splitRawPath[i]);
        }

        return result;
    }
    
    
    
    // From Adder.java
    private static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }
    
    // From Adder.java
 	private void sendString(HttpExchange request, String data, int restCode) 
 			throws IOException {
 		request.sendResponseHeaders(restCode, data.length());
         OutputStream os = request.getResponseBody();
         os.write(data.getBytes());
         os.close();
 	}
}