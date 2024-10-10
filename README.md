## Motivation

[GenoPro®](https://www.genopro.com/) is one of the best software for drawing family trees and genograms. There are
several out-of-the-box reports available, but sometimes they aren't sufficient for specific needs.

It is possible to create custom reports, but:
- they have to be written in Visual Basic script
- they have to be executed in GenoPro as they rely on some internal objects

A long time ago, for one particular report, I used Java to parse data directly from GNO files. Later on, I used
an improved parser for another report. I've realized it would be helpful to transform my code into a lightweight
library that anybody could reuse.

## Limitations

This library covers just a limited subset of GNO file format capabilities. Currently it supports:
- basic genomaps details
- basic individual details
- basic family relation details
- basic text label details

## Parsing options

There are several parser options available for specific needs:

- `untitledGenoMapsExcluded` - use `true` to exclude GenoMaps without a title (the GenoMap title is not the name,
  it can be specified in GenoMap properties, but by default it is empty). This is handy when some stuff needs to be
  hidden in your report. It can be moved to dedicated GenoMaps which titles are left empty.

- `unknownIndividualsExcluded` - use `true` to exclude individuals without name.

- `hyperlinkedIndividualInstancesDeduplicated` - use `true` to deduplicate individual instances present on multiple
  GenoMaps, mutually hyperlinked, if there is a need to work with merged data. When the `excludeUntitledGenoMaps`
  option is set to `true` and any individual instance is located on an untitled GenoMaps, its details are ignored.

- `textLabelsExcluded` - use `true` to exclude text labels.

- `anonymizedSinceDate` - use specific date, current date or `NULL` to select the desired anonymization mode,
  see the Anonymization section.

## Anonymization

In common use cases the original data needs to be anonymized. While it can be done after retrieving all data, a basic
anonymization is available out of the box.

### Strict mode (skip uncertain data)

Set the specific date in the past to ensure:
- only those guaranteed not living individuals on the given date are kept
 (those without birth and death details are excluded automatically)
- only those parent/child pedigree links are kept if none of family members is anonymized

### Lenient mode (keep uncertain data but clear dates)

Set the current date (year) to ensure:
- all individuals and their pedigree links are kept, but birth and death dates of those uncertain individuals
  are cleared.

### Unrestricted mode (no anonymization)

Left the parameter unset or set the future date to disable anonymization.

## The list of reports based on this library

- [WebApp exporter](https://github.com/drifted-in/genopro-webapp-exporter)
