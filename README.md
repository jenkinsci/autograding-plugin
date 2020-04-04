# Jenkins AutoGrading Plugin

[![Jenkins Version](https://img.shields.io/badge/Jenkins-2.138.4-green.svg?label=min.%20Jenkins)](https://jenkins.io/download/)
![JDK8](https://img.shields.io/badge/jdk-8-yellow.svg?label=min.%20JDK)
[![License: MIT](https://img.shields.io/badge/license-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

<img src="etc/images/graduation-cap.svg" alt="drawing" width="64"/> 

Jenkins plugin that autogrades projects based on a configurable set of metrics. Currently, you can select from the 
following metrics:
- Test statistics (e.g., number of failed tests) from the [JUnit Plugin](https://github.com/jenkinsci/junit-plugin)
- Code coverage (e.g., line coverage percentage) from the [Code Coverage API Plugin](https://github.com/jenkinsci/code-coverage-api-plugin)
- PIT mutation coverage (eg., missed mutation percentage)  from the [PIT Mutation reporting plugin](https://github.com/jenkinsci/pitmutation-plugin)
- Static analysis (e.g., number of warnings) from the [Warnings Plugin - Next Generation](https://github.com/jenkinsci/warnings-ng-plugin)

In order to autograde a project, you first need to build your project using your favorite build tool. Make sure 
your build invokes all tools that will produce the artifacts required for the autograding later on. Then 
run all post build steps that record the desired results using the plugins from the list above. Autograding is based
on the persisted Jenkins model of these plugins (i.e., Jenkins build actions), so make sure the results of these plugins
show correctly up in the Jenkins build view. The autograding has to be started as the last step: you can configure
the impact of the individual results using a simple JSON string. Currently, no UI configuration of the configuration is
available. The autograding step will read all requested build results and calculates a score based on the defined 
properties in JSON configuration.

Please have a look at the [example pipeline](etc/Jenkinsfile.autograding) that shows how to use this plugin in practice.
It consists of the following stages:   
1. Checkout from SCM 
2. Build and test the project and run the static analysis with Maven
3. Run the test cases and compute the line and branch coverage 
4. Run PIT to compute the mutation coverage 
5. Record all Maven warnings 
6. Autograde the results from steps 2-5 

The example pipeline uses the following configuration:
```json
{
  "analysis": {
    "maxScore": 100,
    "errorImpact": -10,
    "highImpact": -5,
    "normalImpact": -2,
    "lowImpact": -1
  },
  "tests": {
    "maxScore": 100,
    "passedImpact": 1,
    "failureImpact": -5,
    "skippedImpact": -1
  },
  "coverage": {
    "maxScore": 100,
    "coveredImpact": 1,
    "missedImpact": -1
  },
  "pit": {
    "maxScore": 100,
    "detectedImpact": 1,
    "undetectedImpact": -1,
    "ratioImpact": 0
  }
}

```

If you want to skip one of the tools just remove the corresponding JSON node from the configuration. 
 
[![Jenkins](https://ci.jenkins.io/job/Plugins/job/autograding-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/autograding-plugin/job/master/)
[![CI on all platforms](https://github.com/jenkinsci/autograding-plugin/workflows/CI%20on%20all%20platforms/badge.svg?branch=master)](https://github.com/jenkinsci/autograding-plugin/actions)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1be7bb5b899446968e411e6e59c8ea6c)](https://www.codacy.com/app/jenkinsci/autograding-plugin?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jenkinsci/autograding-plugin&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/jenkinsci/autograding-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/jenkinsci/autograding-plugin)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/jenkinsci/autograding-plugin.svg)](https://github.com/jenkinsci/autograding-plugin/pulls)

