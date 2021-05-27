/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class SnapshotBuilder<T> extends StatelessWidget {
  SnapshotBuilder({required this.snapshot, required this.builder});

  final AsyncSnapshot<T> snapshot;
  final Widget Function(BuildContext context, T data) builder;

  @override
  Widget build(BuildContext context) {
    if (snapshot.hasError) {
      log('Snapshot error: ${snapshot.error}', error: snapshot.error);
      return ErrorWidget(snapshot.error ?? 'Oops!?');
    }
    if (!snapshot.hasData) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Loading ...'),
            SizedBox(height: 10),
            CircularProgressIndicator.adaptive(),
          ],
        ),
      );
    }
    return builder.call(context, snapshot.data!);
  }
}
