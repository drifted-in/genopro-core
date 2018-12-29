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

## Parsing options

There are several parser options available for specific needs:

- `excludeUntitledGenoMaps` - use `true` to exclude GenoMaps without a title (the GenoMap title is not the name, it can be specified in GenoMap properties, by default it is empty). This is handy when some stuff needs to be hidden in your report. It can be moved to dedicated GenoMaps which titles are left empty.
- `excludeUnknownIndividuals` - use `true` to parse details about individuals without name.
- `resolveHyperlinks` - use `true` to retrieve hyperlink details (if there are multiple instances of the specific individual on multiple hyperlinked GenoMaps and there is a need to work with this data). If the hyperlink points to an untitled GenoMap and the `excludeUntitledGenoMaps` option is set to `true`, the hyperlink is ignored.
- `anonymizedSinceDate` - use specific date, current date or `NULL` to select the desired anonymization mode, see the Anonymization section.

## Anonymization

In common use cases the original data needs to be anonymized. While it can be done after retrieving all data, a basic anonymization is available out-of-the-box.

Currently 2 anonymization modes are available. The mode is determined from passed date parameter. If date is `NULL` or future date, the anonymization is disabled completely.

### Suppressing uncertain data

If the specific date in past is provided:

- Individuals are kept if deceased or born before the specified date (only if a birth date is known).
- Parents relations are kept if none of parents is anonymized.
- Children relations are kept if none of children is anonymized.

### Suppressing date values of living individuals

If the current date is provided, all individuals and families are kept, but date values are cleared for all living individuals.

## The list of reports based on this library

- [WebApp exporter](https://github.com/drifted-in/genopro-webapp-exporter)
