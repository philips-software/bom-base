/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'dart:async';

import 'package:bom_base_ui/services/bombar_client.dart';

import '../model/package.dart';

class PackageService {
  PackageService({required this.client});

  final BomBarClient client;
  final _foundStream = StreamController<List<Package>>();

  Stream get found => _foundStream.stream.asBroadcastStream();

  /// Queries packages that match the provided [pattern] of
  /// 'type:namespace/name@version'.
  void find(String pattern) async {
    final match = RegExp(r'(([^:/@]*):)?(([^/@]*)/)?([^@]*)(@(.*))?')
        .firstMatch(pattern.replaceAll(RegExp(r'\s+'), ''));
    final type = match?.group(2) ?? '';
    final namespace = match?.group(4) ?? '';
    final name = match?.group(5) ?? '';
    final version = match?.group(7) ?? '';

    client.find(type, namespace, name, version);
  }

  void dispose() {
    _foundStream.close();
  }
}
