/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:developer';

import 'package:bom_base_ui/model/package.dart';
import 'package:bom_base_ui/services/bombase_client.dart';
import 'package:flutter/foundation.dart';

import 'logic.dart';

class PackageLogic extends Logic {
  PackageLogic(this.id, {BomBaseClient? client})
      : client = client ?? BomBaseClient.instance;

  final BomBaseClient client;
  final String id;

  final isLoading = ValueNotifier<bool>(false);
  final error = ValueNotifier<String?>(null);
  late final package = ValueNotifier<Package>(Package(id: id));

  @override
  Future<void> init() async {
    try {
      isLoading.value = true;
      error.value = null;
      package.value = await client.getPackage(id);
    } catch (e) {
      error.value = e.toString();
      log('Loading $id failed', error: e);
    } finally {
      isLoading.value = false;
    }
  }
}
