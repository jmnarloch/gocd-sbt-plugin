# Go Continuous Delivery SBT plugin

> Go Continuous Delivery SBT task plugin

[![Build Status](https://travis-ci.org/jmnarloch/gocd-sbt-plugin.svg)](https://travis-ci.org/jmnarloch/gocd-sbt-plugin)
[![Coverage Status](https://coveralls.io/repos/jmnarloch/gocd-sbt-plugin/badge.svg?branch=master&service=github)](https://coveralls.io/github/jmnarloch/gocd-sbt-plugin?branch=master)

## Installation

Download the plugin and copy it into `$GO_SERVER_HOME/plugins/external` and restart the Go server.

The plugin should appear on Plugins page.

## Usage

Add SBT tasks to your build stage.

## Options

### Tasks

The lists of SBT tasks to execute. (required)

Example: clean build

### SBT home

The SBT installation directory. (optional)

You may also specify SBT_HOME environment variable either for the specific build or entire Go Environment.

### Additional options

Any additional options to pass to SBT

Example: --warn

## License

Apache 2.0