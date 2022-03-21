# OES Template plugin

OES Template plugin is a simple implementation of the CloudBees Pipeline Template

## Usage

First, create a template.yaml file that describes the meaning and type of the parameter variable

```yaml

type: pipeline-template
version: 1

name: Sample
description: 这是一个样例模板
parameters:
  - name: woc
    displayName: 这就是一句反馈
```

Second, write Jenkinsfile, using variables defined by template.yml

```Jenkinsfile
pipeline {
    agent any
    stages {
        stage('Build') {
          steps {
            echo "${woc}"
          }
        }
    }
}
```

Third, create Pipeline Job and fill in configuration information with project information, such as

![img.png](doc/image/pipeline-job-config.png)