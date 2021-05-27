/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_base_ui/logic/package_logic.dart';
import 'package:bom_base_ui/model/package.dart';
import 'package:flutter/material.dart';

import 'download_card.dart';
import 'license_card.dart';
import 'package_card.dart';

class PackageScreen extends StatelessWidget {
  PackageScreen({Key? key, required String packageId})
      : logic = PackageLogic(packageId)..init(),
        super(key: key);

  final PackageLogic logic;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Package'),
      ),
      body: SingleChildScrollView(
        child: ValueListenableBuilder<Package>(
          valueListenable: logic.package,
          builder: (context, package, _) {
            return Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              mainAxisSize: MainAxisSize.min,
              children: [
                PackageCard(package),
                LicenseCard(package),
                if (package.downloadLocation != null) DownloadCard(package),
              ],
            );
          },
        ),
      ),
    );
  }
}
