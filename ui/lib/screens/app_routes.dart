/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:yeet/yeet.dart';

import 'package/package_screen.dart';
import 'search/search_screen.dart';

final routes = Yeet(
  children: [
    Yeet(
      path: '/',
      builder: (_) => SearchScreen(),
    ),
    Yeet(path: r'/packages', builder: (_) => SearchScreen(), children: [
      Yeet(
        path: r':id(\S*)',
        builder: (context) => PackageScreen(packageId: context.params['id']!),
      ),
    ]),
    Yeet(
      path: ':path(.*)',
      builder: (context) => NotFoundView(context.params['path']!),
    )
  ],
);

PageRoute<dynamic> appRoute(Widget Function(BuildContext) builder) {
  return (Platform.isIOS || Platform.isMacOS)
      ? CupertinoPageRoute(builder: builder)
      : MaterialPageRoute(builder: builder);
}

class NotFoundView extends StatelessWidget {
  NotFoundView(this.path);

  final String path;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        child: Center(
          child: Text(
            'Sorry; "$path" does not exist',
            style: Theme.of(context).textTheme.headline5,
          ),
        ),
      ),
    );
  }
}
