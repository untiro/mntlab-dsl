def gitURL = "https://github.com/MNT-Lab/mntlab-dsl.git"
def command = "git ls-remote -h $gitURL"

def proc = command.execute()
proc.waitFor()

if ( proc.exitValue() != 0 ) {
    println "Error, ${proc.err.text}"
    System.exit(-1)
}

def branches = proc.in.text.readLines().collect {
    it.replaceAll(/[a-z0-9]*\trefs\/heads\//, '')
}

job('./EPBYMINW2472/MNTLAB-zvirinsky-main-build-job'){
	description 'Main Job'
	scm {
        github 'MNT-Lab/mntlab-dsl', '$BRANCH_NAME'
    }
//    triggers { 
//       scm 'H/5 * * * *' 
//	} 
    steps {
    	downstreamParameterized {
            trigger("$BUILDS_TRIGGER" {
                block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                }
                parameters {
                    predefinedProp("$BRANCH_NAME")
                }
            }
        }
    }
 //   publishers {
//        archiveJunit 'build/test-results/**/*.xml'
//    }
	parameters {
        choiceParam('BRANCH_NAME', ['zvirinsky', 'master'], 'choose branch')

        
    }
}
for(i in 1..4) {
    job("./EPBYMINW2472/MNTLAB-zvirinsky-child${i}-build-job") {
    	scm {
        github 'MNT-Lab/mntlab-dsl', '$BRANCH_NAME'
    	}
    	parameters {
        choiceParam('BRANCH_NAME',branches)
        			}

    	steps {
    		shell('chmod +x script.sh; ./script.sh > output.txt; tar -czf ${BRANCH_NAME}_dsl_script.tar.gz script.sh')
    		}
    	publishers {
        archiveArtifacts {
                       pattern('${BRANCH_NAME}_dsl_script.tar.gz')
                       pattern('output.sh')
                   		}
    				}	
    	}
    }
