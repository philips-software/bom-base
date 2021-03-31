/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:flutter/material.dart';

class AttributesList extends StatelessWidget {
  AttributesList(this.attributes);

  final Map<String, dynamic> attributes;

  @override
  Widget build(BuildContext context) {
    final keys = attributes.keys.toList()..sort();
    return ListView.separated(
      itemCount: keys.length,
      separatorBuilder: (_, __) => Divider(),
      itemBuilder: (_, index) => ListTile(
        title: Text(keys[index]),
        subtitle: Text(attributes[keys[index]]),
      ),
    );
  }
}
