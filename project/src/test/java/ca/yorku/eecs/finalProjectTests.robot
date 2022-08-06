*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      30 seconds

*** Test Cases ***
addActorPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=actorName   actorId=a0
    ${resp}=    Put Request   localhost    /api/v1/addActor    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    200

addActorFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=actorName   actorId=a0
    ${resp}=    Put Request    localhost    /api/v1/addActor    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addActorFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name="actorName"    actId="a0"
    ${resp}=    Put Request    localhost    /api/v1/addActor    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addActorFail
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

addMovieFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=movieName   movieId=m0
    ${resp}=    Put Request    localhost    /api/v1/addMovie    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addMovieFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    name=movieName    movId=a0
    ${resp}=    Put Request    localhost    /api/v1/addMovie    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addMovieFail
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

addRelationshipFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0  movieId=m0
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addRelationshipFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0  moeId=m0
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addRelationshipFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary  actorId=a0 
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

addRelationshipFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a3726543  movieId=m0
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404

addRelationshipFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0  movieId=m43754435
    ${resp}=    Put Request   localhost    /api/v1/addRelationship    data=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404

addRelationshipFail
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

getActorFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a7236487243  
    ${resp}=    Get Request   localhost    /api/v1/getActor    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    404

getActorFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actId=a0
    ${resp}=    Get Request   localhost    /api/v1/getActor    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

getActorFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    
    ${resp}=    Get Request   localhost    /api/v1/getActor    json=${params}    headers=${headers}
    Should Be Equal As Strings    ${resp.status_code}    400

computeBaconNumberPass
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a123457
    ${resp}=    Get Request   localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    200
     Dictionary Should Contain Item    ${resp.json()}    baconNumber   2

computeBaconNumberFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary   
    ${resp}=    Get Request   localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    400
     

computeBaconNumberFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary   actorId=a9257434
    ${resp}=    Get Request   localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    404

computeBaconNumberFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary   acrId=hgdfj
    ${resp}=    Get Request   localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    400

computeBaconNumberFail
    Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary   actorId=a0
    ${resp}=    Get Request   localhost    /api/v1/computeBaconNumber    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    404

hasRelationshipPass
   Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0	movieId=m0
    ${resp}=    Get Request   localhost    /api/v1/hasRelationship    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    200

hasRelationshipFail
   Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    
    ${resp}=    Get Request   localhost    /api/v1/hasRelationship     json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    400

hasRelationshipFail
   Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a123456
    ${resp}=    Get Request   localhost    /api/v1/hasRelationship     json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    400

hasRelationshipFail
   Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=7235624	movieId=m0
    ${resp}=    Get Request   localhost    /api/v1/hasRelationship    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    404
    
hasRelationshipFail
   Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a0	movieId=m9868723
    ${resp}=    Get Request   localhost    /api/v1/hasRelationship    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    404

hasRelationshipFail
   Create Session    localhost    http://localhost:8080
    ${headers}=    Create Dictionary    Content-Type=application/json
    ${params}=    Create Dictionary    actorId=a762487465	 movieId=m9868723
    ${resp}=    Get Request   localhost    /api/v1/hasRelationship    json=${params}    headers=${headers}	
     Should Be Equal As Strings    ${resp.status_code}    404






