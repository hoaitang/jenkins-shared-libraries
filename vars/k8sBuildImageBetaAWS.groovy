def call(ecr, image, sudo = true) {
    tagBeta = "${currentBuild.displayName}-${env.BRANCH_NAME}"
    prefix = ""
    if (sudo) {
        prefix = "sudo "
    }
    sh """${prefix}docker image build -t ${image}:${tagBeta} ."""
    sh """${prefix}docker tag ${image}:${tagBeta} ${ecr}/${image}:${tagBeta}"""
    sh "aws --version"

    withCredentials([[
                $class: 'AmazonWebServicesCredentialsBinding',
                credentialsId: 'aws',
                accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
            ]]) {             
                
        sh 'sudo $(aws ecr get-login --region us-east-1 | sed \'s/ -e none / /\')'
        sh """${prefix}docker push ${ecr}/${image}:${tagBeta}"""        
        } // withCredentials
}