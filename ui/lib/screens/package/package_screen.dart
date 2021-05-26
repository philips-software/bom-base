/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_base_ui/logic/package_logic.dart';
import 'package:bom_base_ui/model/package.dart';
import 'package:flutter/material.dart';

class PackageScreen extends StatelessWidget {
  PackageScreen({Key? key, required String packageId})
      : logic = PackageLogic(packageId),
        super(key: key);

  final PackageLogic logic;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: ValueListenableBuilder<Package>(
        valueListenable: logic.package,
        builder: (context, package, _) {
          final style = Theme.of(context).textTheme;
          return Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            mainAxisSize: MainAxisSize.min,
            children: [
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(package.title, style: style.headline4),
                      Text('${package.purl}', style: style.bodyText2),
                      Text('Updated: ${package.purl}', style: style.bodyText2),
                      Text(package.description),
                      ...package.authors.map((name) => Text(name)),
                    ],
                  ),
                ),
              ),
            ],
          );
        },
      ),
    );
  }
}
