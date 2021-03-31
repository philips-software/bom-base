/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../../model/package.dart';
import '../package_details/package_details_screen.dart';

class PackageTile extends StatelessWidget {
  static final dateTimeFormat = DateFormat.yMd().add_jms();

  PackageTile(this.pkg);

  final Package pkg;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(pkg.purl.toString()),
      subtitle: Text('Last updated: ${dateTimeFormat.format(pkg.updated)}'),
      onTap: () => Navigator.of(context).push(
        MaterialPageRoute(
          builder: (context) => PackageDetailsScreen(id: pkg.id),
        ),
      ),
    );
  }
}
