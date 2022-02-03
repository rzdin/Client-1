pipeline {
    agent any
    environment {
        bucket = "my-jenkins-js-application-us-east-1-ranaziauddin2020"
        region = "us-east-1"
        aws_credential = "AWSReservedSSO_AdministratorAccess_564bcbbbca5e5655/rzdin@enquizit.com"
        application_name = "Nodejs"
       
       
    }
     stages{           
        stage('get git tag') {
            steps {
             script {
             latestTag = sh(returnStdout:  true, script: "git tag --sort=-creatordate | head -n 1").trim()
             env.BUILD_VERSION = latestTag
             echo "env-BUILD_VERSION"
             echo "${env.BUILD_VERSION}"
	         echo "*******LINE_1******"	
	         echo "*******LINE_2******"
	         echo "*******LINE_3******"
					
            }
        }
    }
        stage("Nodejs build"){
            tools {nodejs "NODEJS"}
            steps {
                sh 'rm -rf *.zip'
                sh "npm install -g npm"
                sh "ng lint"
                sh "ng e2e"
                sh "ng build --configuration" 
                
                echo "**********LINE2***********"
                
                
               
               
               
    
                sh "zip 'myapp_${BUILD_TIMESTAMP}_${env.BUILD_VERSION}.zip'  angular.json  e2e  node_modules  package-lock.json  src  tslint.json dist  JenkinFilesng.groovy  package.json  README.md   tsconfig.json"
                
             
                
              }
  }
       stage("Upload"){
            steps{
                withAWS(credentials: "${aws_credential}", region: "${region}"){
                s3Upload(file:"myapp_${BUILD_TIMESTAMP}_${env.BUILD_VERSION}.zip", bucket:"${bucket}/${env.BUILD_VERSION}")
               
                  }
            }
     post { 
        always { 
            archiveArtifacts artifacts: '*.zip'
         }
       }
    }  
 }
}