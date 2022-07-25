package ca.yorku.eecs;

import java.io.IOException;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
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
                 }
             }
             else if (request.getRequestMethod().equals("GET")) {
            	 // TODO
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
    	String name = queryParam.get("name").toString();
        String movieId = queryParam.get("movieId").toString();
        
        // add code for incorrect parameters
        
        neo4j.addMovie(name, movieId);
        
        String response = name + " added successfully.";
        sendString(request, response, 200);
    }
    
    public void addActor(HttpExchange request, Map<String, String> queryParam) throws IOException {
    	String name = queryParam.get("name").toString();
        String actorId = queryParam.get("actorId").toString();
        
        // add code for incorrect parameters
        
        neo4j.addActor(name, actorId);
        
        String response = name + " added successfully.";
        sendString(request, response, 200);
    }
    
    public void addRelationship(HttpExchange request, Map<String, String> queryParam) throws IOException {
    	String actorId = queryParam.get("actorId").toString();
        String movieId = queryParam.get("movieId").toString();
        
        // add code for incorrect parameters
        
        neo4j.addRelationship(actorId, movieId);
        
        String response = "ACTED_IN relationship added successfully.";
        sendString(request, response, 200);
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