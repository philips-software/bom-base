/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

import '../../model/package.dart';
import '../widgets/edit_card.dart';
import '../widgets/link_text.dart';

class DownloadCard extends StatelessWidget {
  const DownloadCard(this.package, {Key? key}) : super(key: key);

  final Package package;

  @override
  Widget build(BuildContext context) {
    final style = Theme.of(context).textTheme;
    return EditCard(
        child: Padding(
      padding: const EdgeInsets.all(8.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text('Download', style: style.headline6),
          if (package.downloadLocation != null)
            LinkText(package.downloadLocation!),
          SizedBox(height: 8),
          Wrap(
            spacing: 8,
            alignment: WrapAlignment.end,
            children: [
              if (package.sha1 != null)
                Chip(
                  label: Text('SHA1'),
                ),
              if (package.sha256 != null) Chip(label: Text('SHA256')),
            ],
          )
        ],
      ),
    ));
  }
}
