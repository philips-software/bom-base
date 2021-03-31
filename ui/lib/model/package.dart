/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

class Package {
  Package({required this.id, required this.purl, required this.updated});

  final String id;
  final Uri purl;
  final DateTime updated;
  final Map<String, dynamic> attributes = {};
}
