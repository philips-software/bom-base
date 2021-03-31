/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import '../model/package.dart';

Package toPackage(Map<String, dynamic> json) {
  final attrs = json['attributes'];
  return Package(
    id: _toString(json['id']),
    purl: _toPurl(json['purl']),
    updated: _toLocalTimestamp(json['updated']),
  )
    ..title = _toOptionalString(attrs?['title'])
    ..description = _toOptionalString(attrs?['description'])
    ..downloadLocation = _toOptionalUri(attrs?['download_location'])
    ..sourceLocation = _toOptionalUri(attrs?['source_location'])
    ..declaredLicense = _toOptionalString(attrs?['declared_license'])
    ..detectedLicenses = _toOptionalStringList(attrs?['detected_license']);
}

List<Package> toPackageList(List<dynamic>? list) =>
    list
        ?.map((obj) => obj as Map<String, dynamic>)
        .map((json) => toPackage(json))
        .toList() ??
    [];

String _toString(dynamic obj) {
  try {
    return obj.toString();
  } on Exception {
    throw FormatException('Not a valid string: $obj');
  }
}

String? _toOptionalString(dynamic obj) => obj != null ? _toString(obj) : null;

List<String>? _toOptionalStringList(dynamic obj) {
  final string = _toOptionalString(obj);
  return string != null ? string.split('\n') : null;
}

Uri _toPurl(dynamic obj) {
  try {
    return Uri.parse(obj.toString());
  } on Exception {
    throw FormatException('Not a valid Package URL: $obj');
  }
}

Uri? _toOptionalUri(dynamic obj) {
  try {
    return obj != null ? Uri.parse(obj.toString()) : null;
  } on Exception {
    throw FormatException('Not a valid URL: $obj');
  }
}

DateTime _toLocalTimestamp(dynamic obj) {
  try {
    return DateTime.parse(obj.toString()).toLocal();
  } on Exception {
    throw FormatException('Not a valid ISO timestamp: $obj');
  }
}
