/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import 'search_package.dart';

class SearchScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('BOM-Base'),
      ),
      body: Stack(
        children: [
          Center(child: Text('Running!')),
          SearchPackage(),
        ],
      ),
    );
  }
}
