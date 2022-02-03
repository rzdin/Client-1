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
                sh "npm install -g @angular/cli"
                sh "npm run ng"
                sh "ng build --prod"
                echo "***********LINE***********"
                echo "**********LINE2***********"
                
                
               
               
               
    
                sh "zip 'myapp_${BUILD_TIMESTAMP}_${env.BUILD_VERSION}.zip' /workspace/client-1/*"
                
             
                
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