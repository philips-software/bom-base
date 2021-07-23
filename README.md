<div align="center">

# BOM-Base

[![Release](https://img.shields.io/github/release/philips-software/bom-base.svg)](https://github.com/philips-software/bom-base/releases)

**Description**: BOM-Base is an _experimental_ caching repository for
bill-of-materials metadata

**Status**: Experimental research prototype

_Powered by Philips SWAT Eindhoven_

</div>

While producing a bill-of-materials, a private metadata cache is desirable to
augment the publicly available metadata with local curations and store metadata
for packages that are not publicly released.

The service consists of a metadata store with "harvesters" to collect metadata.
The unavailability and modification of metadata automatically triggers
harvesters to fill the cache.

The harvesting mechanism starts from a client requesting metadata for a specific
package. If the package is unknown, one or more harvesters start collecting
metadata from external sources. The harvester for the relevant package
management repository obtains the basic package metadata, and another harvester
might additionally pull various curated fields for the same package
from [ClearlyDefined](https://clearlydefined.io). A scoring mechanism ensures
the most reliable metadata is kept. The availability of a source code location
(and no scanned license) could trigger the license scanning harvester to
download and scan the source code for licenses and other copyright information.
If the scanned license does not match the license declared in the originating
repository, it can be contested by a harvester that checks consistency between
the "declared" and "detected" license fields. When a client later requests the
same package, it receives the latest updated metadata.

The user interface allows human inspection and curation of the metadata. A
manual change of such metadata can in turn trigger other processes to complete
additional fields.

(See the [architecture document](docs/architecture.md) for a detailed technical
description.)

## Dependencies

The service requires at least Java 11.

## Installation

### Build the executable JAR

The Flutter web user interface should be first built through the `install_ui`
script in the `/ui` directory. (This script checks and builds the web
application and installs it into the `/src/main/resources/static` directory of
the backend.)

Next, the backend can be built through the Maven `mvn clean install` command,
and yields a "fat" executable jar containing all dependencies.

The backend server starts as a standard Java executable:

```
java -jar BOM-base-<version>.jar
```

Some useful command line parameters are:

- `--server.port=9090` changes the http port (from default 8080) to 9090.
- `--bom-base.scan-licenses=false` disables the source code license scanner,
  reducing the machine load during development and testing.
- `--bom-base.harvest-clearly-defined=false` disables the clearly-defined lookup

### Install ScanCode Toolkit license scanner

Scanning licenses from source files is delegated
to [ScanCode Toolkit](https://github.com/nexB/scancode-toolkit).

Follow any of
these [installation instructions](https://scancode-toolkit.readthedocs.io/en/latest/getting-started/install.html)
to install the command line application.

Then make sure the `scancode` and `extractcode` commands are accessible from any
directory by updating the path or creating symbolic links in an appropriate
location.

### Docker

After building the project, you can build and run the application using Docker.

Build docker image:

```bash
docker build -f docker/Dockerfile -t bom-base .
```

Run docker container:

```
docker run -p 8080:8080 bom-base
```

### Image from docker hub

The latest released version is also available from Docker Hub:

```
docker run -p 8080:8080 philipssoftware/bom-base:latest
```

## Configuration

(Empty)

## Usage

The service exposes a REST API and a user interface on port 8080.

Proper operation can be checked by e.g.:

```sh
curl http://localhost:8080/packages/pkg%253Anpm%252Fmarked%25400.7.0 | jq
```

Harvesters will then start collecting the metadata for
the `pkg:npm/marked@0.7.0`
package if its metadata was not yet available. Else it returns the existing
metadata for the package.

## How to test the software

Unit tests for this Maven are run by the `mvn clean test` command.

Note that ScanCode Toolkit must be installed for all tests to pass. (See
installation instructions)

## Known issues

(BOM-Base is still under development.)

## Disclaimer

BOM-Base is an _experimental_ tool, and not suited for production.

## Contact / Getting help

Submit an issue in the issue tracker of this project.

## License

See [LICENSE.md](LICENSE.md).

## Credits and references

- BOM-Base relies for scanning of license information from source code
  on [ScanCode Toolkit](https://github.com/nexB/scancode-toolkit).
- Many thanks go out to the nice people
  at [OSS Review Toolkit](https://github.com/oss-review-toolkit/ort) for their
  work and being an inspiration to try a different approach for managing
  bill-of-materials metadata.
- If you are looking for tools to build a bill-of-materials, you might want to
  have a look at
  the [SPDX-Builder](https://github.com/philips-software/spdx-builder) project
  that can (among various other solutions) use BOM-Base metadata to build rich
  bill-of-materials documents in the SPDX format.
