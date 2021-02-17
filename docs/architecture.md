# BOM-base Service Architecture

## Introduction

### Purpose
This document provides a comprehensive architectural overview of the system,
using a number of different architectural views to depict different aspects of
the system. It is intended to convey the significant architectural decisions
which have been made on the system.

### Scope
BOM-base is an **experimental** caching repository for bill-of-materials metadata.

### Definition, Acronyms and Abbreviations
Term | Description
-----|------------
PURL | Package URL
SPDX | "The Software Package Data Exchange" - An open standard for communicating software bill of material information, including components, licenses, copyrights, and security references. SPDX reduces redundant work by providing a common format for companies and communities to share important data, thereby streamlining and improving compliance.

### References
- [SDPX License list](https://spdx.org/licenses/)
- [The Software Package Data Exchange (SPDX®) Specification Version 2.2](https://spdx.github.io/spdx-spec/)

## Goals and constraints
Goals of the BOM-base service are:
1. Automatically collect metadata for relevant packages.
2. Store metadata for non-public (inner source) packages.
3. Support human curation of stored metadata.

The stakeholders of this application are:

- Metadata experts to maintain and curate metadata.
- Bill-of-materials tool developers.

The most significant requirements are:

(TO DO)

Design constraints are:

- Maintainability: Code must be easy to maintain for average programmers. This
  is why the code tries to adhere to "Clean Code" guidelines, and a strict
layering model is applied.

## Use-Case view
(TO DO)

## Logical view
![UML sequence diagram](harvesting.png "General operation overview")

## Implementation view
(TO DO)

(End of document)
