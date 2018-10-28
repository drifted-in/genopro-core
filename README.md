## Motivation

[GenoPro®](https://www.genopro.com/) is one of the best software for drawing family trees and genograms. There are several out-of-the-box reports available, but sometimes they aren't sufficient for specific needs.

It is possible to create custom reports, but:
- they have to be written in Visual Basic script
- they have to be executed in GenoPro as they rely on some internal objects

For one very specific report I've used Java for parsing data directly from GNO files. Later on I've used improved parser for another report. I've realized it would be helpful to transform my code into lightweight library which could be used by anybody.

## Limitations

This library covers just a limited subset of GNO file format capabilities. Currently it supports:
- basic genomaps details
- basic individual details
- basic family relation details

## List of reports based on this library

- [WebApp exporter](https://github.com/drifted-in/genopro-webapp-exporter)
