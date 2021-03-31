/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import '../../model/package.dart';
import '../../services/package_service.dart';
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
        builder: (context, snapshot) {
          if (snapshot.hasError) return Text(snapshot.error.toString());

          if (!snapshot.hasData)
            return Center(child: CircularProgressIndicator.adaptive());

          final attributes = snapshot.data!.attributes;
          return AttributesList(attributes);
        },
      ),
    );
  }
}
