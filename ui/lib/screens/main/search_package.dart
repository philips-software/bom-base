/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:bom_base_ui/model/package.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:material_floating_search_bar/material_floating_search_bar.dart';

import '../../services/package_service.dart';

class SearchPackage extends StatelessWidget {
  final dateTimeFormat = DateFormat.yMd().add_jms();

  @override
  Widget build(BuildContext context) {
    var service = PackageService.of(context);

    return FloatingSearchBar(
      hint: 'type:namespace/name@version',
      scrollPadding: const EdgeInsets.only(top: 16, bottom: 56),
      transitionDuration: const Duration(milliseconds: 800),
      transitionCurve: Curves.easeInOut,
      physics: const BouncingScrollPhysics(),
      axisAlignment: 0.0,
      openAxisAlignment: 0.0,
      width: 600,
      debounceDelay: const Duration(milliseconds: 500),
      onQueryChanged: (query) => service.find(query),
      transition: CircularFloatingSearchBarTransition(),
      actions: [
        // FloatingSearchBarAction(
        //   showIfOpened: false,
        //   child: CircularButton(
        //     icon: const Icon(Icons.refresh),
        //     onPressed: () {},
        //   ),
        // ),
        FloatingSearchBarAction.searchToClear(
          showIfClosed: false,
        ),
      ],
      builder: (context, transition) {
        return ClipRRect(
          borderRadius: BorderRadius.circular(4),
          child: Material(
            color: Colors.white,
            elevation: 4.0,
            child: StreamBuilder<List<Package>>(
              stream: service.found,
              builder: (context, snapshot) {
                if (!snapshot.hasData) return SizedBox.shrink();
                final packages = snapshot.data ?? [];
                return packages.isEmpty
                    ? Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Center(child: Text('(None found)')),
                      )
                    : Column(
                        children: packages.map((pkg) {
                          return ListTile(
                            title: Text(pkg.purl.toString()),
                            subtitle: Text(
                                'Last updated: ${dateTimeFormat.format(pkg.updated)}'),
                          );
                        }).toList(growable: false),
                      );
              },
            ),
          ),
        );
      },
    );
  }
}
