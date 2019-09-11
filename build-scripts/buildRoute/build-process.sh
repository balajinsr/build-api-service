#!/bin/bash

pullReqNumber=$1

- Flow :-

# getBuildData?pullReqNumber=1  rest API.

# preBuildProcess?pullReqNumber=1 rest API.
    
        - pullGitChanges() method
        - doBuildValidation() method
         
# getBuildCommand rest API.

# doSonarAnalysis() - shell method. 

# postBuildProcess?pullReqNumber=1 rest API
        - generateArtifacts method.
        - uploadArtifacts method.
        - audit to DB. 
 
 
# triggerBuildNotification rest API. 


# updateBuildStatus rest API if failure case.