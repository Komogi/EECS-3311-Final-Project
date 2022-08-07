*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      30 seconds

*** Test Cases ***

getMostProlificActorFail-NoActorsInDatabase
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary
    ${resp}=    Get Request    localhost    /api/v1/getMostProlificActor    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404

addActorPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=actorName   actorId=a0
    ${resp}=    Put Request   localhost    /api/v1/addActor    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    
addActorPass-KevinBacon
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Kevin Bacon   actorId=nm0000102
    ${resp}=    Put Request   localhost    /api/v1/addActor    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    
addActorPass-ActorNotConnectedToKevinBacon
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=ActorNotConnectedToKevinBacon   actorId=a1
    ${resp}=    Put Request   localhost    /api/v1/addActor    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

addActorFail-ActorIdAlreadyExists
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=actorName   actorId=a0
    ${resp}=    Put Request    localhost    /api/v1/addActor    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addActorFail-RequestBodyImproperlyFormatted
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name="actorName"    actId="a0"
    ${resp}=    Put Request    localhost    /api/v1/addActor    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addActorFail-MissingActorId
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name="actorName"    
    ${resp}=    Put Request    localhost    /api/v1/addActor    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addMoviePass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=movieName   movieId=m0
    ${resp}=    Put Request   localhost    /api/v1/addMovie    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

addMovieFail-MovieIdAlreadyExists
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=movieName   movieId=m0
    ${resp}=    Put Request    localhost    /api/v1/addMovie    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addMovieFail-IncorrectParameters
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=movieName    movId=a0
    ${resp}=    Put Request    localhost    /api/v1/addMovie    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addMovieFail-MissingMovieId
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=movieName   
    ${resp}=    Put Request    localhost    /api/v1/addMovie    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addRelationshipPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0  movieId=m0
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

addRelationshipPass-RelationshipWithKevinBacon
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000102  movieId=m0
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

addRelationshipFail-RelationshipAlreadyExists
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0  movieId=m0
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addRelationshipFail-IncorrectParameters
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0  moeId=m0
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addRelationshipFail-MissingMovieId
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary  actorId=a0 
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addRelationshipFail-ActorIdDoesNotExist
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a3726543  movieId=m0
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404

addRelationshipFail-MovieIdDoesNotExist
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0  movieId=m43754435
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404

addRelationshipFail-ActorIdAndMovieIdDoesNotExist
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a563645  movieId=m43645653
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404

getActorPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0  
    ${resp}=    Get Request  localhost    /api/v1/getActor    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

getActorFail-ActorIdDoesNotExist
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a7236487243  
    ${resp}=    Get Request   localhost    /api/v1/getActor    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404

getActorFail-IncorrectParameters
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actId=a0
    ${resp}=    Get Request   localhost    /api/v1/getActor    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

getActorFail-MissingRequiredInformation
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    
    ${resp}=    Get Request   localhost    /api/v1/getActor    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400
    
getMoviePass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=m0  
    ${resp}=    Get Request  localhost    /api/v1/getMovie    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

getMovieFail-MovieIdDoesNotExist
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=m7236487243  
    ${resp}=    Get Request   localhost    /api/v1/getMovie    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404

getMovieFail-IncorrectParameters
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movId=m0
    ${resp}=    Get Request   localhost    /api/v1/getMovie    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

getMovieFail-MissingRequiredInformation
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    
    ${resp}=    Get Request   localhost    /api/v1/getMovie    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400    
    
hasRelationshipPass
   Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0	movieId=m0
    ${resp}=    Get Request   localhost    /api/v1/hasRelationship    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    200

hasRelationshipFail-MissingRequiredInformation
   Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    
    ${resp}=    Get Request   localhost    /api/v1/hasRelationship     json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    400

hasRelationshipFail-MissingMovieId
   Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0
    ${resp}=    Get Request   localhost    /api/v1/hasRelationship     json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    400

hasRelationshipFail-ActorIdDoesNotEixst
   Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=7235624	movieId=m0
    ${resp}=    Get Request   localhost    /api/v1/hasRelationship    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    404
    
hasRelationshipFail-MovieIdDoesNotExist
   Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0	movieId=m9868723
    ${resp}=    Get Request   localhost    /api/v1/hasRelationship    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    404

hasRelationshipFail-ActorIdAndMovieIdDoesNotExist
   Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a762487465	 movieId=m9868723
    ${resp}=    Get Request   localhost    /api/v1/hasRelationship    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    404
     
computeBaconNumberPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0
    ${resp}=    Get Request   localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    200
     Dictionary Should Contain Item    ${resp.json()}    baconNumber   1
     
computeBaconNumberPass-KevingBaconToHimself
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000102
    ${resp}=    Get Request   localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    200
     Dictionary Should Contain Item    ${resp.json()}    baconNumber   0  

computeBaconNumberFail-MissingRequiredInformation
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary   
    ${resp}=    Get Request   localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    400
     
computeBaconNumberFail-ActorIdDoesNotExist
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary   actorId=a9257434
    ${resp}=    Get Request   localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    404

computeBaconNumberFail-IncorrectParameters
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary   acrId=hgdfj
    ${resp}=    Get Request   localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    400

computeBaconNumberFail-NoPathToKevinBacon
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary   actorId=a1
    ${resp}=    Get Request   localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    404
     
computeBaconPathPass
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0
    ${resp}=    Get Request   localhost    /api/v1/computeBaconPath    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    200

computeBaconPathPass-KevinBaconToHimself
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=nm0000102
    ${resp}=    Get Request   localhost    /api/v1/computeBaconPath    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    200
     
computeBaconPathFail-ActorIdDoesNotExist
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a654321
    ${resp}=    Get Request   localhost    /api/v1/computeBaconPath    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    404
     
computeBaconPathFail-PathDoesNotExist
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a654321
    ${resp}=    Get Request   localhost    /api/v1/computeBaconPath    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    404
     
computeBaconPathFail-MissingRequiredInformation
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actId=a654321
    ${resp}=    Get Request   localhost    /api/v1/computeBaconPath    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    400

addStreamingServicePass
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Netflix    streamingServiceId=s0
    ${resp}=    Put Request    localhost    /api/v1/addStreamingService    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

addStreamingServicePass-StreamingServiceWithNoMovies
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Netflix    streamingServiceId=s1
    ${resp}=    Put Request    localhost    /api/v1/addStreamingService    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

addStreamingServiceFail-StreamingServiceIdAlreadyExists
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Hulu    streamingServiceId=s0
    ${resp}=    Put Request    localhost    /api/v1/addStreamingService    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addStreamingServiceFail-MissingRequiredInformation
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=Hulu
    ${resp}=    Put Request    localhost    /api/v1/addStreamingService    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addStreamingOnRelationshipPass
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=m0    streamingServiceId=s0
    ${resp}=    Put Request    localhost    /api/v1/addStreamingOnRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

addStreamingOnRelationshipFail-StreamingOnRelationshipAlreadyExists
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=m0    streamingServiceId=s0
    ${resp}=    Put Request    localhost    /api/v1/addStreamingOnRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400
    
addStreamingOnRelationshipFail-MissingRequiredInformation
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=m0
    ${resp}=    Put Request    localhost    /api/v1/addStreamingOnRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400
    
addStreamingOnRelationshipFail-MovieIdDoesNotExist
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=m654321    streamingServiceId=s0 
    ${resp}=    Put Request    localhost    /api/v1/addStreamingOnRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404
    
addStreamingOnRelationshipFail-StreamingServiceIdDoesNotExist
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=m0   streamingServiceId=s654321
    ${resp}=    Put Request    localhost    /api/v1/addStreamingOnRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404
    
addStreamingOnRelationshipFail-MovieIdAndStreamingServiceIdDoNotExist
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    movieId=m654321    streamingServiceId=s654321
    ${resp}=    Put Request    localhost    /api/v1/addStreamingOnRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404
    
getMoviesOnStreamingServicePass
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    streamingServiceId=s0
    ${resp}=    Get Request    localhost    /api/v1/getMoviesOnStreamingService    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

getMoviesOnStreamingServicePass-NoMoviesOnStreamingService
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    streamingServiceId=s1
    ${resp}=    Get Request    localhost    /api/v1/getMoviesOnStreamingService    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

getMoviesOnStreamingServicePass-StreamingServiceIdDoesNotExist
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    streamingServiceId=s654321
    ${resp}=    Get Request    localhost    /api/v1/getMoviesOnStreamingService    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404
    
getMoviesOnStreamingServicePass-MissingRequiredInformation
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary
    ${resp}=    Get Request    localhost    /api/v1/getMoviesOnStreamingService    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400
    
getActorNumberPass
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    firstActorId=a0    secondActorId=nm0000102
    ${resp}=    Get Request    localhost    /api/v1/getActorNumber    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    Dictionary Should Contain Item    ${resp.json()}    actorNumber   1

getActorNumberPass-ActorToThemselves
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    firstActorId=a0    secondActorId=a0
    ${resp}=    Get Request    localhost    /api/v1/getActorNumber    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    Dictionary Should Contain Item    ${resp.json()}    actorNumber   0
    
getActorNumberFail-PathDoesNotExist
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    firstActorId=a0    secondActorId=a1
    ${resp}=    Get Request    localhost    /api/v1/getActorNumber    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404
    
getActorNumberFail-FirstActorIdDoesNotExist
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    firstActorId=a654321    secondActorId=nm0000102
    ${resp}=    Get Request    localhost    /api/v1/getActorNumber    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404
    
getActorNumberFail-SecondActorIdDoesNotExist
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    firstActorId=a0    secondActorId=a654321
    ${resp}=    Get Request    localhost    /api/v1/getActorNumber    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404
    
getActorNumberFail-FirstActorIdAndSecondActorIdDoesNotExist
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    firstActorId=a654321    secondActorId=nm654321
    ${resp}=    Get Request    localhost    /api/v1/getActorNumber    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404
    
getActorNumberFail-MissingRequiredInformation
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary
    ${resp}=    Get Request    localhost    /api/v1/getActorNumber    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400
    
getMostProlificActorPass
	Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary
    ${resp}=    Get Request    localhost    /api/v1/getMostProlificActor    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200
    