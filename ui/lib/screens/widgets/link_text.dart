/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class LinkText extends StatelessWidget {
  const LinkText(this.url, {Key? key, this.text}) : super(key: key);

  final Uri url;
  final String? text;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => _open(url),
      child: MouseRegion(
        cursor: SystemMouseCursors.click,
        child: Text(
          '${text ?? url}',
          style: TextStyle(decoration: TextDecoration.underline),
        ),
      ),
    );
  }

  void _open(Uri location) {
    launch(location.toString());
  }
}
