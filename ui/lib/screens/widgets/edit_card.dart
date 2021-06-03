/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:flutter/material.dart';

class EditCard extends StatelessWidget {
  const EditCard({Key? key, required this.child, this.onEdit})
      : super(key: key);

  final Widget child;
  final void Function()? onEdit;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Row(
        mainAxisSize: MainAxisSize.max,
        children: [
          Expanded(child: child),
          if (onEdit != null)
            IconButton(
              onPressed: onEdit,
              icon: Icon(Icons.edit),
            ),
        ],
      ),
    );
  }
}
