/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import '../model/package.dart';

Package toPackage(Map<String, dynamic> json) {
  return Package(
    id: _toString(json['id']),
    purl: _toPurl(json['purl']),
    updated: _toLocalTimestamp(json['updated']),
  )..attributes.addAll(json['attributes'] ?? {});
}

List<Package> toPackageList(List<dynamic>? list) =>
    list
        ?.map((obj) => obj as Map<String, dynamic>)
        .map((json) => toPackage(json))
        .toList() ??
    [];

String _toString(dynamic obj) {
  try {
    return obj?.toString() ?? '?';
  } on Exception {
    throw FormatException('Not a valid string: $obj');
  }
}

Uri _toPurl(dynamic obj) {
  try {
    return Uri.parse(obj?.toString() ?? 'undefined');
  } on Exception {
    throw FormatException('Not a valid Package URL: $obj');
  }
}

DateTime _toLocalTimestamp(dynamic obj) {
  try {
    if (obj == null) return DateTime(2000);
    return DateTime.parse(obj.toString()).toLocal();
  } on Exception {
    throw FormatException('Not a valid ISO timestamp: $obj');
  }
}
