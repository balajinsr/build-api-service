#!/bin/bash

 usage(){
   if [ $args -lt 2  -o $args -gt 2 ]; then
     echo "Usage: Please pass following arguments - sh build-process.sh <<siloId>> <<pullRequestId>>"
     echo
     exit 1
   fi
 }
 
 
buildServiceURL=""
siloId=$1
pullReqNumber=$2
buildNumber=${BUILD_NUMBER}
silo_workspace=""  #read from getBuildData API


#Flow :-
preBuildProcess
doSonarAnalysis
build
postBuildProcess

preBuildProcess() {
   pullGitchanges
   curl -s -w "\n%{http_code}" -H "Content-Type: application/json" -X POST -g -d '{ "siloId":"'"${siloId}"'", "buildNumber":"'"${buildNumber}"'", "pullReqNumber":"'"${pullReqNumber}"'"}' --silent ${buildServiceURL}/preBuildProcess)| {
     read body
     read code
     
     if [ $code -ne 200 ]
     then
        errorMsg=$(echo ${body}|jq -r '.error.message')
   		echo "${errorMsg}"
   		sendErrorEmail
   		#TODO:  send an error email by api call.
 	 else
   		echo "`date` - pre Build process done."
 	 fi
  }    
}

doSonarAnalysis() {
	 echo "Sonar-Scanner - ${SONAR_SCANNER_HOME}/bin/sonar-scanner -e -Dproject.settings=${silo_workspace}/sonar-project.properties -Dsonar.projectVersion=${buildNumber} -Dsonar.projectBaseDir=${silo_workspace}"
	 ${SONAR_SCANNER_HOME}/bin/sonar-scanner -e -Dproject.settings=${silo_workspace}/sonar-project.properties -Dsonar.projectVersion=${buildNumber} -Dsonar.projectBaseDir=${silo_workspace}
	 if [ $? -gt 0 ]; then
	    echo 'Sonar Scan Failed..'
	    reason = "Sonar analysis failed"
	    updateBuildStatus $reason
	 fi
}

build() {
   #getBuildCommand rest API.
}

postBuildProcess{

}

## temp ############
pullGitChanges() {
     taskId=testbranch  #get it using api call by using pullReqNumber
     cleanworkspace
     git fetch origin testbranch
	 git pull origin master 
	
	 git merge ${taskId} --no-commit --no-ff 
	 success=$?
	 echo "merge status: ${status}"
	 if [[ $success -eq 0 ]];
	 then
	 	revert_local_changes
	    echo "check of merge $taskId to master branch is success"  
	 else
	    revert_local_changes
	    updateBuildStatus
	    git branch -D $taskId
	    sendBuildNotification
	 fi 
}

revert_local_changes() {
  git reset
  git checkout .
  git clean -df
}

cleanworkspace() {
	revert_local_changes
	git checkout master
	git branch | grep -v "master" | xargs git branch -D
}

#####################################

sendBuildNotification() {
     curl -s -w "\n%{http_code}" -H "Content-Type: application/json" -X POST -g -d '{ "siloId":"'"${siloId}"'", "buildNumber":"'"${buildNumber}"'", "pullReqNumber":"'"${pullReqNumber}"'"}' --silent ${buildServiceURL}/sendBuildNotification)| {
     read body
     read code
     
     if [ $code -ne 200 ]
     then
        errorMsg=$(echo ${body}|jq -r '.error.message')
   		echo "sendBuildAuditNotification service failed - ${errorMsg}"
   		sendErrorEmail
 	 else
   		echo "`date` - send Notifications service success."
	    exit 1
 	 fi
  }    
}

sendErrorEmail() {
     curl -s -w "\n%{http_code}" -H "Content-Type: application/json" -X POST -g -d '{ "siloId":"'"${siloId}"'", "buildNumber":"'"${buildNumber}"'", "pullReqNumber":"'"${pullReqNumber}"'","toEmailAddress":"'"${toEmailAddress}"'","errorMessage":"'"${errorMessage}"'"}' --silent ${buildServiceURL}/sendErrorEmail)| {
     read body
     read code
     
     if [ $code -ne 200 ]
     then
        errorMsg=$(echo ${body}|jq -r '.error.message')
   		echo "sendErrorEmail service failed - ${errorMsg}"
 	 else
   		echo "`date` - sendErrorEmail service success."
 	 fi
 	 exit 1
  }
}

updateBuildStatus() {
     buildStatus=$1
     reason=$2
     
     curl -s -w "\n%{http_code}" -H "Content-Type: application/json" -X POST -g -d '{ "buildAuditReq":{"siloId":"'"${siloId}"'", "buildNumber":"'"${buildNumber}"'", "pullReqNumber":"'"${pullReqNumber}"'"},"statusCode":"${buildStatus}", "buildAuditAddlData":{"reason":"'"${reason}"'"}}' --silent ${buildServiceURL}/updateBuildAudit) | {
     read body
     read code
     
     if [ $code -ne 200 ]
     then
        errorMsg=$(echo ${body}|jq -r '.error.message')
   		echo "sendErrorEmail service failed - ${errorMsg}"
 	 else
   		echo "`date` - updateBuildStatus service success."
 	 fi	 
  }
}


# getBuildData?pullReqNumber=1  rest API.
# preBuildProcess?pullReqNumber=1 rest API.   
#        - pullGitChanges() method
#        - doBuildValidation() method
#doSonarAnalysis() - shell method. 

#postBuildProcess?pullReqNumber=1 rest API
#        - generateArtifacts method.
#        - uploadArtifacts method.
#        - audit to DB.  
#sendBuildNotification rest API. 
#updateBuildStatus rest API if failure case.
