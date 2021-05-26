/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import '../../model/package.dart';

class LicenseCard extends StatelessWidget {
  const LicenseCard(this.package, {Key? key}) : super(key: key);

  final Package package;

  @override
  Widget build(BuildContext context) {
    final style = Theme.of(context).textTheme;
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            SelectableText(
              package.declaredLicense,
              style: style.headline5,
              textAlign: TextAlign.center,
            ),
            if (package.detectedLicenses.isNotEmpty)
              Padding(
                padding: const EdgeInsets.only(top: 8.0),
                child: Text('Detected:'),
              ),
            ...package.detectedLicenses.map((lic) => _license(lic)),
          ],
        ),
      ),
    );
  }

  Widget _license(String license) {
    return Row(
      children: [
        if (license == package.declaredLicense)
          Icon(Icons.check, color: Colors.green)
        else
          Icon(Icons.warning, color: Colors.red),
        Flexible(child: Text(license)),
      ],
    );
  }
}
