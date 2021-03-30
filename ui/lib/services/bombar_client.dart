/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'dart:async';

import 'package:bom_base_ui/services/model_adapters.dart';
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';

import '../model/package.dart';

class BomBarClient {
  final Dio dio = Dio();
  final Uri baseUri =
      kIsWeb ? Uri.parse('') : Uri.parse('http://localhost:8080');
  late final Uri _packageUri = baseUri.resolve('packages');

  Future<List<Package>> find(
      String type, String namespace, String name, String version) async {
    final response = await dio.getUri(_packageUri
        .resolve('?type=$type,ns=$namespace,name=$name,version=$version'));

    return toPackageList(response.data['results']);
  }
}
