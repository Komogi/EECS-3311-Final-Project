package ca.yorku.eecs;

import java.io.IOException;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class MovieAdder implements HttpHandler{

	private Neo4jKevinBacon neo4j;
	
	public MovieAdder(Neo4jKevinBacon neo4j) {
		this.neo4j = neo4j;
	}
	
	@Override
	public void handle(HttpExchange request) throws IOException {
		
        try {
            if (request.getRequestMethod().equals("PUT")) {
                handlePut(request);
                System.out.println("PUT");
            } else
            	sendString(request, "Unimplemented method\n", 501);
        } catch (Exception e) {
        	e.printStackTrace();
        	sendString(request, "Server error\n", 500);
        }
		
	}
	
	// From Adder.java
	private void sendString(HttpExchange request, String data, int restCode) 
			throws IOException {
		request.sendResponseHeaders(restCode, data.length());
        OutputStream os = request.getResponseBody();
        os.write(data.getBytes());
        os.close();
	}
	
    
    private void handlePut(HttpExchange request) throws IOException {
    	
        URI uri = request.getRequestURI();
        String query = uri.getQuery();
        System.out.println("query: " + query);
        Map<String, String> queryParam = splitQuery(query);
        System.out.println("queryParam: " + queryParam);
        
        String name = queryParam.get("name").toString();
        String movieId = queryParam.get("movieId").toString();
        
        // add code for incorrect parameters
        
        // Do ADD on neo4j server
        neo4j.insertMovie(name, movieId);
        
        String response = name + " added successfully.";
        sendString(request, response, 200);
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
}

	
