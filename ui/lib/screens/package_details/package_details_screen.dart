/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import '../../model/package.dart';
import '../../services/package_service.dart';
import '../shared/snapshot_builder.dart';
import 'attributes_list.dart';

class PackageDetailsScreen extends StatelessWidget {
  PackageDetailsScreen({required this.id});

  final String id;

  @override
  Widget build(BuildContext context) {
    var service = PackageService.of(context);

    return Scaffold(
      appBar: AppBar(
        title: Text('Package details'),
      ),
      body: FutureBuilder<Package>(
        future: service.select(id),
        builder: (context, snapshot) => SnapshotBuilder<Package>(
          snapshot: snapshot,
          builder: (context, data) => AttributesList(data?.attributes ?? {}),
        ),
      ),
    );
  }
}
