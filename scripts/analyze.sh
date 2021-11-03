#!/usr/bin/env bash

###################################################################
# Script to batch-analyze Black Duck component licenses against 3rd
# party metadata.
#
# Mandatory $1 Blackduck project
# Mandatory $2 Blackduck version
# Mandatory $3 filename
# Optional $4 parameter is the URL of a BOM-Base server
#
# Recipe of this script:
#
# The listed project versions are extracted from Black Duck as SPDX
# files and a tree of Package URLs. The tree of Package URLs is used
# to create a second SPDX file using independent metadata from ClearlyDefined
# and package management repositories. The declared licenses from both
# sources are compared, resulting in a list of all packages and a diff list of
# packages where the declared licenses don't match.
###################################################################
set -e

function info () {
  echo " +================================================================================================ "
  echo " | ${1}"
  echo " +------------------------------------------------------------------------------------------------ "
}

function checkEnvironmentVariables () {
  info "Check Environment Variables"
  if [ -z "$BLACKDUCK_API_TOKEN" ]; then
    echo " | BLACKDUCK_API_TOKEN is not set"
    exit 1
  fi

  if [ -z "$BLACKDUCK_URL" ]; then
    echo " | BLACKDUCK_URL is not set"
    exit 1
  fi
}

checkEnvironmentVariables

BOM_BASE_URL="http://localhost:8080"
if [ "$4" ]; then
    BOM_BASE_URL=$4
fi

function checkBOMBaseAvailable () {
  info "Check BOMBase is running"
  # Aborts if BOM-Base server cannot be reached
  if [ $(curl --write-out '%{http_code}' --head --silent --output /dev/null $BOM_BASE_URL/packages) -ne 200 ]; then
      echo " | Could not reach BOM-Base at URL $BOM_BASE_URL (see https://github.com/philips-software/bom-base)"
      exit 1
  fi
}

checkBOMBaseAvailable

# Aborts if the specified tool is not installed
# $1 is the command name
# $2 is a link to installation instructions for the tool
function checkInstalled () {
    if ! type $1 &> /dev/null; then
        echo "Requires '$1' (see $2)"
        exit 1
    fi
}

info "Check Installed tools"

# Ensure the required tools are installed
checkInstalled spdx-builder https://github.com/philips-software/spdx-builder
checkInstalled bompare https://github.com/philips-labs/bompare

# Extracts an SPDX and tree file from a Black Duck project.
# $1 is the Black Duck project
# $2 is the Black Duck project version
# $3 is the nick name
function exportFromBD () {
    if [ -f "$3.spdx" ] && [ -f "$3.tree" ]; then
        echo " | Skipping Black Duck export for $1 $2; files already exist"
    else
        echo " | Exporting $3 could take a while..."
        spdx-builder blackduck $1 $2 -o "$3.spdx" --tree > "$3.tree" 2>&1
    fi
}

# Builds an SPDX file from a tree file.
# $1 is the nick name
function buildFromTree () {
    echo " | Building $1 from tree..."
    cat $1.tree | spdx-builder tree --format purl --config "shared.yml" --bombase $BOM_BASE_URL --force -o "$1-tree.spdx"
}

# Lists differences between Black Duck and tree SPDX files.
# $1 is the nick name
function licensesDiff () {
    bompare licenses --spdx-tag-value "$1.spdx" --spdx-tag-value "$1-tree.spdx" --out "$1-diff.csv" --diffOnly
}

# Lists all packages in the Black Duck SPDX output.
# $1 is the nick name
function listPackages() {
    echo " | list Packages"
    bompare bom --spdx-tag-value "$1.spdx" --out "$1-all.csv"
}

# Processes a single project
# $1 is the Black Duck project
# $2 is the Black Duck project version
# $3 is the nick name
function blackduck () {
    info "Blackduck function "$1" "$2" "$3""
    exportFromBD "$1" "$2" "$3"
    listPackages "$3"
    buildFromTree "$3"
    licensesDiff "$3"
}

# Collect and process per Black Duck project version
blackduck "$1" "$2" "$3"
