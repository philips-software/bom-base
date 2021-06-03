/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import '../../model/package.dart';

class PackageEditor extends StatelessWidget {
  const PackageEditor(this.package, {Key? key}) : super(key: key);

  final Package package;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Properties'),
      ),
    );
  }
}
