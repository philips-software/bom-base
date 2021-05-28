/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'dart:async';

import 'package:bom_base_ui/services/bombase_client.dart';
import 'package:flutter/cupertino.dart';
import 'package:provider/provider.dart';

import '../model/package.dart';

class PackageService {
  PackageService({BomBaseClient? client})
      : client = client ?? BomBaseClient.instance;

  final BomBaseClient client;
  final _foundStream = StreamController<List<Package>>.broadcast();
  Package? current;

  Stream<List<Package>> get found => _foundStream.stream;

  factory PackageService.of(BuildContext context) =>
      Provider.of(context, listen: false);

  /// Queries packages that match the provided [pattern] of
  /// 'type:namespace/name@version'.
  void find(String pattern) {
    final match = RegExp(r'(([^:/@]*):)?(([^/@]*)/)?([^@]*)(@(.*))?')
        .firstMatch(pattern.replaceAll(RegExp(r'\s+'), ''));
    final type = match?.group(2) ?? '';
    final namespace = match?.group(4) ?? '';
    final name = match?.group(5) ?? '';
    final version = match?.group(7) ?? '';

    client
        .find(type, namespace, name, version)
        .then((packages) => _foundStream.sink.add(packages));
  }

  /// Picks a package by [id].
  Future<Package> select(String id) async {
    current = await client.getPackage(id);
    return current!;
  }

  void dispose() {
    _foundStream.close();
  }
}
