#!/bin/sh

###################################################################
# Script to manually curate licenses in BOM-Base using the REST API.
# Optional $1 parameter specifies BOM-Base server (defaults to local).
###################################################################

set -e

BOM_BASE_URL="http://localhost:8080"
if [ "$1" ]; then
    BOM_BASE_URL=$1
fi

# Aborts if BOM-Base server cannot be reached
if [ $(curl --write-out '%{http_code}' --head --silent --output /dev/null $BOM_BASE_URL/packages) -ne 200 ]; then
    echo "Could not reach BOM-Base at URL $BOM_BASE_URL (see https://github.com/philips-software/bom-base)"
    exit 1
fi

###################################################################
# Applies URL encoding
# $1 holds the string to encode
# Returns the URL-encoded string
###################################################################
function urlencode() {
  local string="${1}"
  local strlen=${#string}
  local encoded=""
  local pos c o

  for (( pos=0 ; pos<strlen ; pos++ )); do
     c=${string:$pos:1}
     case "$c" in
        [-_.~a-zA-Z0-9] ) o="${c}" ;;
        * )               printf -v o '%%%02x' "'$c"
     esac
     encoded+="${o}"
  done
  echo "${encoded}"    
}

###################################################################
# Curates a package license
# $1 holds the package URL
# $2 holds the declared license
###################################################################
function curate() {
    curl --request POST -sS -H 'Content-Type: application/json' \
    -d "{\"declared_license\":\"$2\"}" \
    "$BOM_BASE_URL/packages/$(urlencode $(urlencode $1))/details" > /dev/null
}


###################################################################
# Append all package license curations here:
# (Don't forget to use the proper quotes)
###################################################################
curate 'pkg:npm/svg2ttf@1.2.0' 'MIT'
curate 'pkg:maven/com.github.jnr/jnr-posix@3.0.27' 'CPL-1.0 OR GPL-2.0-only OR LGPL-2.1'
curate 'pkg:maven/com.ibm.icu/icu4j@4.6' 'ICU'
curate 'pkg:maven/com.ibm.icu/icu4j@59.1' 'ICU'
curate 'pkg:maven/org.mockito/mockito-core@1.10.19' 'MIT'
curate 'pkg:maven/org.mockito/mockito-core@3.3.3' 'MIT'
curate 'pkg:maven/org.mockito/mockito-junit-jupiter@3.3.3' 'MIT'
curate 'pkg:maven/org.postgresql/postgresql@9.4-1206-jdbc42' 'PostgreSQL'
curate 'pkg:maven/xmlpull/xmlpull@1.1.3.1' 'Indiana Extreme License 1.1.1'
curate 'pkg:nuget/7-Zip.StandaloneConsole.x64@19.0.0' 'LGPL-2.1-or-later OR BSD-3-Clause'
curate 'pkg:nuget/BCrypt-Official@0.1.109' 'BSD-3-Clause'
curate 'pkg:nuget/BarcodeLib@2.2.3' 'Apache-2.0'
curate 'pkg:nuget/BoDi@1.4.0-alpha1' 'Apache-2.0'
curate 'pkg:nuget/BoDi@1.4.1' 'Apache-2.0'
curate 'pkg:nuget/CommandLineParser@2.4.3' 'MIT'
curate 'pkg:nuget/CommandLineParser@2.7.82' 'MIT'
curate 'pkg:nuget/DDay.iCal@1.0.1' 'BSD-3-Clause'
curate 'pkg:nuget/DHTMLX.Scheduler.NET@3.4.0' 'Proprietary'
curate 'pkg:nuget/Gherkin@6.0.0' 'MIT'
curate 'pkg:nuget/Gherkin@6.0.0-beta1' 'MIT'
curate 'pkg:nuget/Google.Protobuf@3.7.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.ElementModel@1.2.1' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.ElementModel@1.6.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.STU3@1.2.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.STU3@1.2.1' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.STU3@1.6.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.Serialization@1.2.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.Serialization@1.6.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.Specification.STU3@1.2.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.Support.Poco@1.6.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.Support@1.2.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.Support@1.2.1' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.Support@1.6.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.FhirPath@1.2.1' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.FhirPath@1.6.0' 'BSD-3-Clause'
curate 'pkg:nuget/Microsoft.CodeCoverage@16.5.0' 'Microsoft .NET Library License'
curate 'pkg:nuget/Microsoft.NET.Test.Sdk@16.5.0' 'Microsoft .NET Library License'
curate 'pkg:nuget/Microsoft.TeamFoundation.DistributedTask.Common.Contracts@16.153.0' 'Microsoft .NET Library License'
curate 'pkg:nuget/Microsoft.TeamFoundationServer.Client@16.153.0' 'Microsoft .NET Library License'
curate 'pkg:nuget/Microsoft.TestPlatform.ObjectModel@16.5.0' 'Microsoft .NET Library License'
curate 'pkg:nuget/Microsoft.TestPlatform.TestHost@16.5.0' 'Microsoft .NET Library License'
curate 'pkg:nuget/Microsoft.VisualStudio.Services.Client@16.153.0' 'Microsoft .NET Library License'
curate 'pkg:nuget/NUnit3TestAdapter@3.16.1' 'MIT'
curate 'pkg:nuget/NUnit@3.12.0' 'MIT'
curate 'pkg:nuget/Npgsql@3.2.7' 'PostgreSQL'
curate 'pkg:nuget/RestSharp@106.6.9' 'Apache-2.0'
curate 'pkg:nuget/Sendgrid@9.11.0' 'MIT'
curate 'pkg:nuget/SonarAnalyzer.CSharp@8.7.0.17535' 'LGPL-3.0-only'
curate 'pkg:nuget/SpecFlow.Autofac@3.0.225' 'BSD-3-Clause'
curate 'pkg:nuget/SpecFlow.Autofac@3.1.86' 'BSD-3-Clause'
curate 'pkg:nuget/SpecFlow.NUnit@3.0.22' 'BSD-3-Clause'
curate 'pkg:nuget/SpecFlow.NUnit@3.1.86' 'BSD-3-Clause'
curate 'pkg:nuget/SpecFlow.Tools.MsBuild.Generation@3.0.225' 'BSD-3-Clause'
curate 'pkg:nuget/SpecFlow.Tools.MsBuild.Generation@3.1.86' 'BSD-3-Clause'
curate 'pkg:nuget/SpecFlow@3.0.225' 'BSD-3-Clause'
curate 'pkg:nuget/SpecFlow@3.1.86' 'BSD-3-Clause'
curate 'pkg:nuget/SpecSync.AzureDevOps.SpecFlow.3-0@2.1.10' 'Spec Solutions EULA'
curate 'pkg:nuget/SpecSync.AzureDevOps@2.1.10' 'Spec Solutions EULA'
curate 'pkg:nuget/System.IO.Abstractions@6.0.15' 'MIT'
curate 'pkg:nuget/housework@0.1.9' 'MIT'
curate 'pkg:nuget/protobuf-net.Protogen@2.3.17' 'Apache-2.0'
curate 'pkg:nuget/protobuf-net@2.4.0' 'Apache-2.0'
curate 'pkg:nuget/sqlite-net-sqlcipher@1.5.231' 'MIT'
curate 'pkg:pypi/cryptography@3.4.7' 'Apache-2.0 OR BSD-3-Clause'
curate 'pkg:pypi/packaging@20.9' 'Apache-2.0 OR BSD-2-Clause'
curate 'pkg:nuget/Microsoft.CSharp@4.5.0' 'MIT'
curate 'pkg:nuget/jQuery@1.6.4' 'MIT'
curate 'pkg:nuget/Gherkin@6.0.0-beta1' 'MIT'
curate 'pkg:nuget/BoDi@1.4.0-alpha1' 'Apache-2.0'
curate 'pkg:nuget/RazorEngine@3.10.0' 'Apache-2.0'
curate 'pkg:nuget/RestSharp@106.6.9' 'Apache-2.0'
curate 'pkg:nuget/Google.Protobuf@3.7.0' 'BSD-3-Clause'
curate 'pkg:nuget/Gherkin@6.0.0' 'MIT'
curate 'pkg:nuget/System.Data.SQLite.Core@1.0.106' 'Public Domain'
curate 'pkg:nuget/System.Data.SQLite.EF6@1.0.106' 'Public Domain'
curate 'pkg:nuget/System.Data.SQLite.EF6@1.0.106' 'Public Domain'
curate 'pkg:nuget/System.Data.SQLite@1.0.106' 'Public Domain'
curate 'pkg:npm/%40hypnosphi/kotlin-extensions@0.0.2' 'Apache-2.0'
curate 'pkg:npm/becke-ch--regex--s0-0-v1--base--pl--lib@1.4.0' 'MIT'
curate 'pkg:npm/ng2-file-upload@1.4.0' 'MIT'
curate 'pkg:npm/openjpeg.js@0.10.2' 'BSD-2-Clause'
curate 'pkg:npm/store2@2.12.0' 'MIT'
curate 'pkg:npm/timers-browserify' 'MIT'
curate 'pkg:npm/ttf2woff@1.3.0' 'MIT'
curate 'pkg:nuget/CGenT.Sprache.Release@1.0.0.1' 'MIT'
curate 'pkg:nuget/CommandLineParser@2.8.0' 'MIT'
curate 'pkg:nuget/DocumentFormat.OpenXml@1.0' 'MIT'
curate 'pkg:nuget/DotNetZip@1.13.3' 'MS-PL AND Zlib AND Apache-2.0 AND MIT'
curate 'pkg:nuget/DotNetZip@1.15.0' 'MS-PL AND Zlib AND Apache-2.0 AND MIT'
curate 'pkg:nuget/EnterpriseLibrary.Caching@5.0.505.0' 'MS-PL'
curate 'pkg:nuget/GraphViz.NET@1.0.' 'MIT'
curate 'pkg:nuget/Hangfire.Core@1.7.19' 'LGPL-3.0-or-later OR Commercial'
curate 'pkg:nuget/Hangfire.SqlServer@1.7.19' 'LGPL-3.0-or-later OR Commercial'
curate 'pkg:nuget/Hl7.Fhir.ElementModel@1.2.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.ElementModel@1.3.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.ElementModel@1.5.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.STU3@1.3.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.STU3@1.5.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.Serialization@1.3.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.Serialization@1.5.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.Support.Poco@1.5.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.Support@1.3.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.Fhir.Support@1.5.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.FhirPath@1.2.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.FhirPath@1.3.0' 'BSD-3-Clause'
curate 'pkg:nuget/Hl7.FhirPath@1.5.0' 'BSD-3-Clause'
curate 'pkg:nuget/MartinCostello.Logging.XUnit@0.1.0' 'Apache-2.0'
curate 'pkg:nuget/Microsoft.Data.SqlClient.SNI@2.0.0' 'Microsoft .NET Library License'
curate 'pkg:nuget/Microsoft.OData.Core@7.7.1' 'MIT'
curate 'Microsoft.OData.Edm@7.7.1' 'MIT'
curate 'pkg:nuget/Microsoft.Spatial@7.7.1' 'MIT'
curate 'Microsoft.SqlServer.Management.SqlParser' 'SQL Server Shared Management Objects (SMO) License'
curate 'Microsoft.SqlServer.SqlManagementObjects' 'SQL Server Shared Management Objects (SMO) License'
curate 'pkg:nuget/NSubstitute@3.1.0' 'BSD-3-Clause'
curate 'pkg:nuget/NetMQ@4.0.0.1' 'LGPL-3.0-or-later'
curate 'pkg:nuget/NetSpell@2.1.7' 'BSD-3-Clause'
curate 'pkg:nuget/RabbitMQ.Client@5.1.0' 'Apache-2.0 OR MPL-2.0'
curate 'pkg:nuget/SharpZipLib.NETStandard@1.0.7' 'MIT'
curate 'pkg:nuget/SonarAnalyzer.CSharp@8.1.0.13383' 'LGPL-3.0-or-later'
curate 'pkg:nuget/SonarAnalyzer.CSharp@8.7.0.17535' 'LGPL-3.0-or-later'
curate 'pkg:nuget/SpecFlow.NUnit@3.0.225' 'BSD-3-Clause'
curate 'pkg:nuget/System.Text.Encoding.CodePages@4.6.0-preview6.19264.9' 'MIT'
curate 'pkg:nuget/p3-sharpcompress@0.10.5.2' 'MIT'
curate 'pkg:nuget/protobuf-net@2.3.4' 'Apache-2.0'
curate 'pkg:maven/com.jcabi/jcabi-aspects@0.22.5' 'Apache-2.0'
curate 'pkg:maven/ch.qos.logback/logback-classic@1.1.11' 'EPL-1.0 OR LGPL-2.1-or-later'
curate 'pkg:maven/ch.qos.logback/logback-classic@1.1.7' 'EPL-1.0 OR LGPL-2.1-or-later'
curate 'pkg:maven/ch.qos.logback/logback-classic@1.2.3' 'EPL-1.0 OR LGPL-2.1-or-later'
curate 'pkg:maven/ch.qos.logback/logback-core@1.1.11' 'EPL-1.0 OR LGPL-2.1-or-later'
curate 'pkg:maven/ch.qos.logback/logback-core@1.1.7' 'EPL-1.0 OR LGPL-2.1-or-later'
curate 'pkg:maven/ch.qos.logback/logback-core@1.2.3' 'EPL-1.0 OR LGPL-2.1-or-later'
curate 'pkg:maven/apache-xerces/xercesImpl@2.9.1' 'Apache-2.0'
curate 'pkg:maven/com.alibaba.spring/spring-context-velocity' 'Apache-2.0'
curate 'pkg:maven/com.amazon.redshift/redshift-jdbc42-no-awssdk@1.2.10.1009' 'Amazon Redshift ODBC and JDBC Driver License'
curate 'pkg:maven/com.amazon.redshift/redshift-jdbc42@1.2.1.1001' 'Amazon Redshift ODBC and JDBC Driver License'
curate 'pkg:maven/com.amazon.redshift/redshift-jdbc42@1.2.10.1009' 'Amazon Redshift ODBC and JDBC Driver License'
curate 'pkg:maven/com.amazonaws/aws-java-sdk-iot@1.11.792' 'Apache-2.0'
curate 'pkg:maven/com.jcabi.incubator/xembly@0.22' 'BSD-3-Clause'
curate 'pkg:maven/com.mockrunner/mockrunner-core@2.0.4' 'Apache-1.1'
curate 'pkg:maven/com.mockrunner/mockrunner-servlet@2.0.4' 'Apache-1.1'
curate 'pkg:maven/com.oracle/ojdbc7@12.1.0.2' 'Proprietary'

