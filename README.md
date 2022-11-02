# gitlab-kubernetes-credentials-plugin

[![Build Status](https://ci.jenkins.io/job/Plugins/job/gitlab-kubernetes-credentials-plugin/job/main/badge/icon)](https://ci.jenkins.io/job/Plugins/job/gitlab-kubernetes-credentials-plugin/job/main/)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/gitlab-kubernetes-credentials.svg)](https://plugins.jenkins.io/gitlab-kubernetes-credentials)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/gitlab-kubernetes-credentials-plugin.svg?label=changelog)](https://github.com/jenkinsci/gitlab-kubernetes-credentials-plugin/releases/latest)
[![GitHub license](https://img.shields.io/github/license/jenkinsci/gitlab-kubernetes-credentials-plugin)](https://github.com/jenkinsci/gitlab-kubernetes-credentials-plugin/blob/master/LICENSE.md)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/gitlab-kubernetes-credentials.svg?color=blue)](https://plugins.jenkins.io/gitlab-kubernetes-credentials)


# Introduction

This plugin provides an extension for the [kubernetes-credentials-provider-plugin](https://github.com/jenkinsci/kubernetes-credentials-provider-plugin)
plugin, and the [gitlab-branch-source-plugin](https://github.com/jenkinsci/gitlab-branch-source-plugin) that extend the kubernetes credentials provider to create the special credential type required by the gitlab-branch-source when interacting with a GitLab server instance.

## Usage

This plugin consumes extends the kubernetes-credentials-provider-plugin to consume kubernetes secrets with a `"jenkins.io/credentials-type"` of `"gitlabToken"`. These secrets need to have a data property `"text"` that contains a base64 encoded `bearer token` for gitlab server.

### Example

```
apiVersion: v1
data:
  text: c3VwZXJkdXBlcnNlY3JldA==
kind: Secret
metadata:
  annotations:
    jenkins.io/credentials-description: The GitLab token for creating web hooks
  labels:
    jenkins.io/credentials-type: gitlabToken
  name: gitlab-hook-token
  namespace: jenkins-demo
type: Opaque
```
## LICENSE

Licensed under MIT, see [LICENSE](LICENSE)
