package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RequestHandler implements HttpHandler{

    private Neo4jKevinBacon neo4j;
    private String handleMethod;
    
    /* 
     * Constructor for the Request Handler
     * 
     * @param     neo4j		the current Neo4j database instance
     */
    public RequestHandler(Neo4jKevinBacon neo4j) {
        this.neo4j = neo4j;
    }
    
    /* 
     * Processes the request to the API and passes it off to specific handlers. 
     * 
     * @param     request		request to the API
     * @return    void
     * @throws	  IOException 	
     */
    @Override
    public void handle(HttpExchange request) throws IOException {
    	
    	URI uri = request.getRequestURI();
    	String rawPath = uri.getRawPath();
    	String body = Utils.convert(request.getRequestBody());
    	
    	try {
			JSONObject jsonBody = new JSONObject(body);
			ArrayList<String> splitRawPath = splitRawPath(rawPath);
			handleMethod = splitRawPath.get(splitRawPath.size() - 1);
			
			if(request.getRequestMethod().equals("PUT")) {
	       	 
	            switch(handleMethod) {
	                 case "addMovie":
	                	 addMovie(request, jsonBody);
	                	 break;
	                	 
	                 case "addActor":
	                	 addActor(request, jsonBody);
	                	 break;
	                	 
	                 case "addRelationship":
	                	 addRelationship(request, jsonBody);
	                	 break;
	                	 
	                 case "addStreamingService":
	                	 addStreamingService(request, jsonBody);
	                	 break;
	                	 
	                 case "addStreamingOnRelationship":
	                	 addStreamingOnRelationship(request, jsonBody);
	                	 break;
	            }
	        }
	        else if (request.getRequestMethod().equals("GET")) {
	            	
		       	 switch(handleMethod) {
						case "getMovie":
							getMovie(request, jsonBody);
							break;
							
						case "getActor":
							getActor(request, jsonBody);
							break;
							
						case "hasRelationship":
							hasRelationship(request, jsonBody);
							break;
							
						case "computeBaconNumber":
							computeBaconNumber(request, jsonBody);
							break;
							
						case "computeBaconPath":
							computeBaconPath(request, jsonBody);
							break;
		
		           	 	 case "getMoviesOnStreamingService":
		           	 		 getMoviesOnStreamingService(request, jsonBody);
		           	 		 break;
		       	 
		                 case "getActorNumber":
		                	 getActorNumber(request, jsonBody);
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

    /* 
     * Handles adding a Movie Node to the database and sends an appropriate response
     * 
     * @param     request         request to the api
     * @param     jsonBody        JSON representation of the request body
     * @return    void
     */
    public void addMovie(HttpExchange request, JSONObject jsonBody) throws IOException, JSONException {
        
        String name;
        String movieId;
        String response;
        try {
        	if (jsonBody.has("name") && jsonBody.has("movieId")) {
                
                name =  jsonBody.getString("name");
                movieId = jsonBody.getString("movieId");
                
                String hasMovie = neo4j.hasMovie(movieId).toLowerCase();
                if(hasMovie.equals(" true")) {
                	response = name + " movie already exists";
                	sendString(request, response, 400);

                }
                else {
                	neo4j.addMovie(name, movieId);
                    
                    response = name + " added successfully.";
                    sendString(request, response, 200);
                }
            }
            else {
                response = "The request body is improperly formatted or missing required information.";
                sendString(request, response, 400);
            }
        }
        finally {
        	
        }
    }
    
    /* 
     * Handles adding an Actor Node to the database and sends an appropriate response
     * 
     * @param     request         request to the api
     * @param     jsonBody        JSON representation of the request body
     * @return    void
     */
    public void addActor(HttpExchange request, JSONObject jsonBody) throws IOException, JSONException {
    	String name;
        String actorId;
        String response;
        
        try {
        	 if (jsonBody.has("name") && jsonBody.has("actorId")) {
                 
                 name =  jsonBody.getString("name");
                 actorId = jsonBody.getString("actorId");
                 
                 String actorPresent = neo4j.hasActor(actorId).toLowerCase();
                 
                 if(actorPresent.equals(" true")) {
                 	response = name + " actor already exists";
                 	sendString(request, response, 400);

                 }
                 else {
                	 neo4j.addActor(name, actorId);
                     
                     response = name + " added successfully.";
                     sendString(request, response, 200);
                 }
             }
             else {
                 response = "The request body is improperly formatted or missing required information.";
                 sendString(request, response, 400);
             }
        }
       finally{
    	   
       }
    }
    
    /* 
     * Handles adding an ACTED_IN Relationship between Actor and Movie to the database and sends an appropriate response
     * 
     * @param     request         request to the api
     * @param     jsonBody        JSON representation of the request body
     * @return    void
     */
    public void addRelationship(HttpExchange request, JSONObject jsonBody) throws IOException, JSONException {
    	String actorId;
    	String movieId;
    	String response;
    	
    	try {
    		if (jsonBody.has("actorId") && jsonBody.has("movieId")) {
        		
        		actorId = jsonBody.getString("actorId");
        		movieId = jsonBody.getString("movieId");
        		String actorPresent = neo4j.hasActor(actorId).toLowerCase();
        		String moviePresent = neo4j.hasMovie(movieId).toLowerCase();
        		if(!actorPresent.equals(" true") && !moviePresent.equals(" true")) {
        			response ="Actor and Movie do not exist";
                	sendString(request, response, 404);
        		}
        		else if(!moviePresent.equals(" true")) {
        			response ="Movie does not exist";
                	sendString(request, response, 404);
        		}
        		else if(!actorPresent.equals(" true")) {
        			response ="Actor does not exist";
                	sendString(request, response, 404);
        		}
        		String relationshipPresent = neo4j.hasRel(actorId,movieId).toLowerCase();
               
                if(relationshipPresent.equals("true")) {
                	response ="Relationship already exists";
                	sendString(request, response, 400);

                }
                else {
                	neo4j.addRelationship(actorId, movieId);
                    
                    response = "ACTED_IN relationship added successfully.";
                    sendString(request, response, 200);
                }
        	}
        	else {
        		response = "The request body is improperly formatted or missing required information.";
        		sendString(request, response, 400);
        	}
    	}
    	finally {
    		
    	}
    }
    
    /* 
     * Handles adding a StreamingService Node to the database and sends an appropriate response
     * 
     * @param     request		request to the API
     * @param     jsonBody		JSON representation of the request body
     * @return    void
     * @throws	  IOException, JSONException
     */
    public void addStreamingService(HttpExchange request, JSONObject jsonBody) throws IOException, JSONException {
    	String name;
    	String streamingServiceId;
    	String response;

    	if (jsonBody.has("name") && jsonBody.has("streamingServiceId")) {
    		
    		name = jsonBody.getString("name");
    		streamingServiceId = jsonBody.getString("streamingServiceId");
    		
    		if (neo4j.hasStreamingService(streamingServiceId)) {
    			response = "A Streaming Service with streamingServiceId: " + streamingServiceId + " already exists in the database.";
                sendString(request, response, 400);
    		}
    		else {
    			neo4j.addStreamingService(name, streamingServiceId);
    			response = name + " added successfully.";
                sendString(request, response, 200);
    		}
    	}
    	else {
    		response = "The request body is improperly formatted or missing required information.";
    		sendString(request, response, 400);
    	}
    }
    
    /* 
     * Handles adding a STREAMING_ON relationship to the database and sends an appropriate response
     * 
     * @param     request		request to the API
     * @param     jsonBody		JSON representation of the request body
     * @return    void
     * @throws	  IOException, JSONException
     */
    public void addStreamingOnRelationship(HttpExchange request, JSONObject jsonBody) throws IOException, JSONException {
    	String movieId;
    	String streamingServiceId;
    	String response;
    	
    	if (jsonBody.has("movieId") && jsonBody.has("streamingServiceId")) {
    		
    		movieId = jsonBody.getString("movieId");
    		streamingServiceId = jsonBody.getString("streamingServiceId");
    		
    		if (neo4j.hasStreamingOnRelationship(movieId, streamingServiceId)) {
    			response = "STREAMING_ON relationship already exists in the database.";
    			sendString(request, response, 400);
    		}
    		else if (!neo4j.hasMovie(movieId).toLowerCase().equals(" true") && !neo4j.hasStreamingService(streamingServiceId)) {
    			response = "Movie with id: " + movieId + " and Streaming Service with streamingServiceId: "
    					+ streamingServiceId + " do not exist in the database.";
    			sendString(request, response, 404);
    		}
    		else if (!neo4j.hasMovie(movieId).toLowerCase().equals(" true")) {
    			response = "Movie with id: " + movieId + " does not exist in the database.";
    			sendString(request, response, 404);
    		}
    		else if (!neo4j.hasStreamingService(streamingServiceId)) {
    			response = "Streaming Service with streamingServiceId " + streamingServiceId + " does not exist in the database.";
    			sendString(request, response, 404);
    		}
    		else {
    			neo4j.addStreamingOnRelationship(movieId, streamingServiceId);
    			response = "STREAMING_ON relationship added successfully.";
                sendString(request, response, 200);
    		}
    	}
    	else {
    		response = "The request body is improperly formatted or missing required information.";
    		sendString(request, response, 400);
    	}
    }
    
    /* 
     * Handles getting Movies that are STREAMING_ON a Streaming Service and sends an appropriate response
     * 
     * @param     request		request to the API
     * @param     jsonBody		JSON representation of the request body
     * @return    void
     * @throws	  IOException, JSONException
     */
    public void getMoviesOnStreamingService(HttpExchange request, JSONObject jsonBody) throws IOException, JSONException {
    
    	String streamingServiceId;
    	String response;
    	
    	if (jsonBody.has("streamingServiceId")) {
    		
    		streamingServiceId = jsonBody.getString("streamingServiceId");
            
            if (!neo4j.hasStreamingService(streamingServiceId)) {
            	response = "There exists no Streaming Service with streamingServiceId:" + streamingServiceId + " in the database.";
            	sendString(request, response, 404);
            }
            else if (!neo4j.hasMoviesOnStreamingService(streamingServiceId)) {
            	response = String.format("{\n");
                response += "\"Movies\": []\n";
                response += "}\n";
            	sendString(request, response, 200);
            }
            else {
            	response = neo4j.getMoviesOnStreamingService(streamingServiceId);;
            	sendString(request, response, 200);
            }
    	}
    	else {
    		response = "The request body is improperly formatted or missing required information.";
    		sendString(request, response, 400);
    	}
    }
    
    /* 
     * Handles getting the Actor Number and sends an appropriate response
     * 
     * @param     request		request to the API
     * @param     jsonBody		JSON representation of the request body
     * @return    void
     * @throws	  IOException, JSONException
     */
    public void getActorNumber(HttpExchange request, JSONObject jsonBody) throws IOException, JSONException {
    	
    	String firstActorId;
    	String secondActorId;
    	
    	if (jsonBody.has("firstActorId") && jsonBody.has("secondActorId")) {
    		
    		firstActorId = jsonBody.getString("firstActorId");
    		secondActorId = jsonBody.getString("secondActorId");
    		
    		String firstActorPresent = neo4j.hasActor(firstActorId).toLowerCase();
    		String secondActorPresent = neo4j.hasActor(secondActorId).toLowerCase();
    		
        	if(firstActorPresent.equals(" true") && secondActorPresent.equals(" true")) {
        		
        		if (firstActorId.equals(secondActorId)) {
        			String response = String.format("{\n");
                    response += "\"actorNumber\": " + 0 + "\n";
                    response += "}\n";
                    sendString(request, response, 200);
        		}
        		else if (neo4j.hasPathFromActorToActor(firstActorId, secondActorId)) {
        			String response = neo4j.getActorNumber(firstActorId, secondActorId);
                	sendString(request, response, 200);
        		}
        		else {
        			String response = "There does not exist a path between " + firstActorId + " and " + secondActorId + ".";
                	sendString(request, response, 404);
        		}
        	}
        	else if (!firstActorPresent.equals(" true") && !secondActorPresent.equals(" true")){
        		String response = "Given firstActorId and secondActorId do not exist in the database";
        		sendString(request, response, 404);
        	}
        	else if (!firstActorPresent.equals(" true")){
        		String response = "Given firstActorId does not exist in the database";
        		sendString(request, response, 404);
        	}
        	else if (!secondActorPresent.equals(" true")){
        		String response = "Given secondActorId does not exist in the database";
        		sendString(request, response, 404);
        	}        	
    	}
    	else {
    		String response = "The request body is improperly formatted or missing required information.";
    		sendString(request, response, 400);
    	}
    }
    
    /* 
     * Handles getting the most prolific Actor in the databse and sends an appropriate response
     * 
     * @param     request		request to the API
     * @param     jsonBody		JSON representation of the request body
     * @return    void
     * @throws	  IOException
     */
    public void getMostProlificActor(HttpExchange request) throws IOException {
	   	
    	if (!neo4j.hasActors()) {
    		String response= "There are no Actors in the database.";
            sendString(request, response, 404);
    	}
    	else if (!neo4j.hasActedInRelationships()) {
    		String response= "There are no Actors that have acted in Movies, in the database.";
            sendString(request, response, 404);
    	}
    	else {
    		String response = neo4j.getMostProlificActor();
            sendString(request, response, 200);
    	}
    }
    
    /* 
     * Handles getting a Movie from the database and sends an appropriate response
     * 
     * @param     request         request to the api
     * @param     jsonBody        JSON representation of the request body
     * @return    void
     */
    public void getMovie(HttpExchange request, JSONObject jsonBody) throws IOException, JSONException{
    	
    	String movieId;
    	if(jsonBody.has("movieId")) {
    		movieId = jsonBody.getString("movieId");
    		String moviePresent = neo4j.hasMovie(movieId).toLowerCase();
    		if(moviePresent.equals(" true")) {
    			String response = neo4j.getMovie(movieId);
    			sendString(request, response, 200);
    		}
    		else {
    			String response = "Given MovieId does not exist in the database";
    			sendString(request, response, 404);
    		}
    	}
    	else {
    		String response = "Request body improperly formatted";
        	sendString(request, response, 400);
    	}
    
    	
    }
    
    /* 
     * Handles getting an Actor from the database and sends an appropriate response
     * 
     * @param     request         request to the api
     * @param     jsonBody        JSON representation of the request body
     * @return    void
     */
    public void getActor(HttpExchange request, JSONObject jsonBody) throws IOException, JSONException{
    	String actorId;
    
		if(jsonBody.has("actorId")) {
    		actorId = jsonBody.getString("actorId");
    		String actorPresent = neo4j.hasActor(actorId).toLowerCase();
    		
        	if(actorPresent.equals(" true")) {
        		String response = neo4j.getActor(actorId);
            	sendString(request, response, 200);
        	}
        	else {
        		String response = "Given ActorId does not exist in the database";
        		sendString(request, response, 404);
        	}
        	
    	}
    	else {
    		String response = "Request body improperly formatted";
    		sendString(request, response, 400);
    	}
    }
    
    /* 
     * Handles getting the result of whether a relationship exists between an Actor and a Movie
     * 
     * @param     request         request to the api
     * @param     jsonBody        JSON representation of the request body
     * @return    void
     */
    public void hasRelationship(HttpExchange request, JSONObject jsonBody) throws IOException, JSONException{
    	String actorId;
    	String movieId;

    	
    	String response;
    	
    	if(jsonBody.has("actorId") && jsonBody.has("movieId")) {
        	actorId = jsonBody.getString("actorId");
        	movieId = jsonBody.getString("movieId");
      	
        	String actorPresent = neo4j.hasActor(actorId).toLowerCase();
        	String moviePresent = neo4j.hasMovie(movieId).toLowerCase();
       	
        	if(actorPresent.equals(" true") && moviePresent.equals(" true")) {
      		
        		response = neo4j.hasRelationship(actorId,movieId);
        		sendString(request, response, 200);
        	}
        	else if(!moviePresent.equals(" true") && !actorPresent.equals(" true")) {
        		response = "Given MovieId and ActorId not present";
        		sendString(request, response, 404);
        	}
        	else if(!actorPresent.equals(" true")){
        		response = "Given ActorId not present";
        		sendString(request, response, 404);
        	}
        	else if(!moviePresent.equals(" true")) {
        		response = "Given MovieId not present";
        		sendString(request, response, 404);
        	}
        	
    	}
    	else {
    		response = "Request body improperly formatted";
    		sendString(request, response, 400);
    	}
		
    }
    
    /* 
     * Handles computing the Bacon Number
     * 
     * @param     request         request to the api
     * @param     jsonBody        JSON representation of the request body
     * @return    void
     */
    
    public void computeBaconNumber(HttpExchange request, JSONObject jsonBody) throws IOException, JSONException {
    	String actorId;
    	String response;
    	
    	if(jsonBody.has("actorId")) {
        	actorId = jsonBody.getString("actorId");
        	
        	String actorPresent = neo4j.hasActor(actorId).toLowerCase();
        	
        	if(!actorPresent.equals(" true")){
        		response = "Given ActorId not present";
        		sendString(request, response, 404);
        	}
        	else if (actorId.equals("nm0000102")){
        		response = String.format("{\n");
                response += "\"baconNumber\": " + 0 + "\n";
                response += "}\n";
                sendString(request, response, 200);
        	}
        	else if (!neo4j.hasPathToKevinBacon(actorId)) {
        		response = "There is no path from " + actorId + " to Kevin Bacon.";
        		sendString(request, response, 404);
        	}
        	else {
        		response = neo4j.computeBaconNumber(actorId);
        		sendString(request, response, 200);
        	}
    	}
    	else {
    		response = "Request body improperly formatted";
    		sendString(request, response, 400);
    	}
    }
    
    /* 
     * Handles getting the BaconPath and sends the appropriate response
     * 
     * @param     request		request to the API
     * @param     jsonBody		a JSON representation of the body fo the request
     * @return    void
     * @throws	  IOException, JSONException 	
     */
    public void computeBaconPath(HttpExchange request, JSONObject jsonBody) throws IOException, JSONException{
    	String actorId;
    	String response;
    	
    	if(jsonBody.has("actorId")) {
        	actorId = jsonBody.getString("actorId");
        	
        	String actorPresent = neo4j.hasActor(actorId).toLowerCase();

        	if(!actorPresent.equals(" true")){
        		response = "Given ActorId not present";
        		sendString(request, response, 404);
        	}
        	else if (actorId.equals("nm0000102")){
        		response = String.format("{\n");
            	response += "\"baconPath\": [\n";
            	response += String.format("%s,\n", "nm0000102");
                response += "]\n\n";
                sendString(request, response, 200);
        	}
        	else if (!neo4j.hasPathToKevinBacon(actorId)) {
        		response = "There is no path from " + actorId + " to Kevin Bacon.";
        		sendString(request, response, 404);
        	}
        	else {
        		response = neo4j.computeBaconPath(actorId);
        		sendString(request, response, 200);
        	}
    	}
    	else {
    		response = "Request body improperly formatted";
    		sendString(request, response, 400);
    	}
    }
    
    /* 
     * Splits the Raw Path of a request into a String Array
     * 
     * @param     rawPath		the Raw Path to be split
     * @return    result		a String Array containing the split Raw Path
     * @throws	  UnsupportedEncodingException
     */
    private static ArrayList<String> splitRawPath(String rawPath) throws UnsupportedEncodingException {
        
    	ArrayList<String> result = new ArrayList<String>();
    	
        String[] splitRawPath = rawPath.split("/");
        
        for (int i = 0; i < splitRawPath.length; i++) {
        	result.add(splitRawPath[i]);
        }

        return result;
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