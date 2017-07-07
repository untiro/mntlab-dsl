job('MNTLAB-hpashuto-main-build-job') {
    description 'DSL task main job.'
    scm {
        github ''
    }
    steps {
        gradle 'test'
    }
    publishers {
        archiveJunit ''
    }
}
(1..4).each {
            def jobN = it.value
            job("MNTLAB-hpashuto-child$jobN-build-job") {
                description "DSL task child$jobN job."
                scm {
                    github ''
                }
                steps {
                    gradle 'test'
                }
                publishers {
                    archiveJunit ''
                }
            }
        }