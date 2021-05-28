/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'dart:async';
import 'dart:developer';

import 'package:bom_base_ui/services/model_adapters.dart';
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';

import '../model/package.dart';

class BomBaseClient {
  static late final instance = BomBaseClient();

  BomBaseClient() {
    if (kDebugMode) {
      _enableHttpLogging();
    }
  }

  final Dio dio = Dio();
  final Uri baseUri =
      Uri.http(kIsWeb && !kDebugMode ? '' : 'localhost:8080', '/');
  late final Uri _packagesUri = baseUri.resolve('packages/');

  void _enableHttpLogging() {
    dio.interceptors.add(LogInterceptor(
      responseBody: false,
      requestHeader: false,
      responseHeader: false,
      logPrint: (o) => log(o as String),
    ));
  }

  Future<List<Package>> find(
      String type, String namespace, String name, String version) async {
    final response = await _exec(() => dio.getUri(baseUri
            .resolve('packages')
            .replace(queryParameters: {
          'type': type,
          'ns': namespace,
          'name': name,
          'version': version
        })));

    return toPackageList(response.data['results']);
  }

  Future<Package> getPackage(String id) async {
    final response = await _exec(() => dio.getUri(_packagesUri.resolve(id)));

    return toPackage(response.data);
  }

  Future<T> _exec<T>(Future<T> Function() request) async {
    try {
      return await request.call();
    } on DioError catch (ex) {
      log('Response status ${ex.response?.statusCode ?? "-"}', error: ex.error);
      throw BackendException(ex.message);
    }
  }
}

class BackendException implements Exception {
  BackendException(this.message);

  final String message;

  @override
  String toString() => message;
}
