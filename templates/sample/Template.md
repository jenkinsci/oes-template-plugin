# Introduction

Build the base image

# Parameter Configuration

```yaml
coderepo:
  url: xxx
  auth: xxx
  branch: ${BRANCH} # Can be obtained through build parameters

dockerpush:
  registry: xxx
  auth: xxx
  namespace: xxx
  image: xxx

build_method: docker-compose # support `docker-compose` or `docker`

```

# Plugin Dependencies

* https://www.jenkins.io/doc/pipeline/steps/workflow-basic-steps/
* https://plugins.jenkins.io/docker-workflow/
* https://github.com/jenkinsci/oes-template-plugin
