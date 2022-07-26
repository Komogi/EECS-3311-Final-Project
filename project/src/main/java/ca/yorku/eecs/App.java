package ca.yorku.eecs;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

public class App 
{
    static int PORT = 8080; // DO NOT CHANGE
    static public Neo4jKevinBacon neo4j;
    
    public static void main(String[] args) throws IOException
    {
    	// Setting up Neo4j server
    	neo4j = new Neo4jKevinBacon();
    	
    	// Setting up Http server
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0); // DO NOT CHANGE

        RequestHandler requestHandler = new RequestHandler(neo4j);
        server.createContext("/api/v1/", requestHandler::handle);
        
        server.start(); // DO NOT CHANGE
        System.out.printf("Server started on port %d...\n", PORT); // DO NOT CHANGE
    }
    
 
}
