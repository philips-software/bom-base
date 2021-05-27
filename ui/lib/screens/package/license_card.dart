/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_base_ui/screens/widgets/link_text.dart';
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
                child: Text('Found in sources:', style: style.subtitle2),
              ),
            ...package.detectedLicenses.map((lic) => _license(lic)),
            if (package.sourceLocation != null)
              Padding(
                padding: const EdgeInsets.only(top: 8.0),
                child: LinkText(package.sourceLocation!),
              ),
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
          Icon(Icons.warning, color: Colors.orange),
        Flexible(child: Text(license)),
      ],
    );
  }
}
