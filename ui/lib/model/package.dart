/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

class Package {
  Package({required this.id, Uri? purl, this.updated})
      : purl = purl ?? Uri.parse(Uri.decodeFull(Uri.decodeFull(id)));

  final String id;
  final Uri purl;
  final DateTime? updated;
  final Map<String, dynamic> attributes = {};

  String get title => attributes['title'] ?? '(Untitled)';

  String get description => attributes['description'] ?? '';

  List<String> get authors =>
      (attributes['attribution'] as List<dynamic>? ?? [])
          .map((value) => value as String)
          .toList(growable: false);
}
