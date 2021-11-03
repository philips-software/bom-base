#!/usr/bin/env bash

###################################################################
# Script to batch-analyze Black Duck component licenses against 3rd
# party metadata.
#
# Optional $1 parameter is the URL of a BOM-Base server
#
# Recipe of this script:
#
# The listed project versions are extracted from Black Duck as SPDX
# files and a tree of Package URLs. The tree of Package URLs is used
# to create a second SPDX file using independent metadata from ClearlyDefined
# and package management repositories. The declared licenses from both
# sources are compared, resulting in a list of all packages and a diff list of
# packages where the declared licenses don't match. These lists are merged
# across all projects, and separate lists are extracted for specific package
# types (e.g. Maven, NuGet, NPM). These lists provide an indications of the
# license diffs per type of development environment.
###################################################################
set -e


BOM_BASE_URL="http://localhost:8080"
if [ "$1" ]; then
    BOM_BASE_URL=$1
fi

readonly projects=(
  'blackduck-project|blackduck-version|analyse-output-filename'
  'blackduck-project-2|blackduck-version-2|analyse-output-filename-2'
)

function info () {
  echo " +================================================================================================ "
  echo " | ${1}"
  echo " +------------------------------------------------------------------------------------------------ "
}

# Start with and empty list of projects
ALL=()

# Processes a single project
# $1 is the Black Duck project
# $2 is the Black Duck project version
# $3 is the nick name
# $4 is the BomBase url
function blackduck () {
    ./analyze.sh "$1" "$2" "$3" "$4"
    ALL+=( $3 )
}

function scan_projects () {
    local project version filename
    for fields in "${projects[@]}"
    do
        IFS=$'|' read -r project version filename <<< "$fields"
        # Collect and process per Black Duck project version
        blackduck "$project" "$version" "$filename" "$BOM_BASE_URL"
    done
}

scan_projects

# Truncate totals files
rm -f packages.csv
rm -f diffs.csv

# Merge packages of all projects
for PROJECT in "${ALL[@]}"; do
    cat "$PROJECT-all.csv" >> packages.csv
    cat "$PROJECT-diff.csv" >> diffs.csv
done

sort -u "packages.csv" -o "packages.csv"
sort -u "diffs.csv" -o "diffs.csv"

# Extracts a package type from the diffs
# $1 is the group name
function extractType () {
    grep "^\"pkg:$1/" packages.csv > "$1-packages.csv"
    PACKAGES=$(grep -c ^ "$1-packages.csv")
    grep "^\"pkg:$1/" diffs.csv > "$1-diffs.csv"
    DIFFS=$(grep -c ^ "$1-diffs.csv")
    echo "Type $1 totals $PACKAGES with $DIFFS license mismatches"
}

extractType nuget
extractType npm
extractType maven
extractType pypi
