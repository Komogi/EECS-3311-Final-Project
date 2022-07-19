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

public class RequestHandler implements HttpHandler{

	public Neo4jKevinBacon neo4j;
	
	public RequestHandler() {
		neo4j = new Neo4jKevinBacon();
	}
	
	@Override
	public void handle(HttpExchange request) throws IOException {
		// TODO Auto-generated method stub
		
        try {
        	 if(request.getRequestMethod().equals("PUT")){
//             	sendString(request, "Unimplemented method\n", 501);
             	determinePut(request);
             }
           
             else if (request.getRequestMethod().equals("GET")) {
//              handleGet(request);
              determineGet(request);
              System.out.println("get");}

             
             } catch (Exception e) {
        	e.printStackTrace();
//        	sendString(request, "Server error\n", 500);
        }
		
	}
	
	
public void determineGet(HttpExchange request) {
		
	}

public void determinePut(HttpExchange request) {
	
}

public void handleAddActor() {
	
}

public void handleAddMovie() {
	
}
	
}

	
