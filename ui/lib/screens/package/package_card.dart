/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../../model/package.dart';
import '../app_routes.dart';
import '../attributes/package_editor.dart';
import '../widgets/edit_card.dart';
import '../widgets/link_text.dart';

class PackageCard extends StatelessWidget {
  static final dateTimeFormat = DateFormat.yMd().add_Hm();

  const PackageCard(this.package, {Key? key}) : super(key: key);

  final Package package;

  @override
  Widget build(BuildContext context) {
    final style = Theme.of(context).textTheme;
    return EditCard(
      onEdit: () =>
          Navigator.push(context, appRoute((_) => PackageEditor(package))),
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(package.title, style: style.headline4),
            SelectableText('${package.purl}'),
            if (package.updated != null)
              Text(
                'Updated: ${dateTimeFormat.format(package.updated!)}',
                style: style.caption,
              ),
            if (package.homePage != null)
              Padding(
                padding: const EdgeInsets.only(top: 8.0),
                child: LinkText(package.homePage!),
              ),
            if (package.description.isNotEmpty)
              Padding(
                padding: const EdgeInsets.only(top: 8.0),
                child: Text(package.description),
              ),
            if (package.authors.isNotEmpty) const SizedBox(height: 8.0),
            ...package.authors.map((name) => Text(
                  name,
                  style: TextStyle(fontStyle: FontStyle.italic),
                )),
          ],
        ),
      ),
    );
  }
}
