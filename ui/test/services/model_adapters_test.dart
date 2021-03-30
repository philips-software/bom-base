/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_base_ui/model/package.dart';
import 'package:bom_base_ui/services/model_adapters.dart';
import 'package:flutter_test/flutter_test.dart';

main() {
  group('parsing $Package from JSON', () {
    const id = 'id';
    final purl = Uri.parse('pkg:purl');
    final timestamp = DateTime.now();
    final packageJson = {
      'id': id,
      'purl': purl.toString(),
      'updated': timestamp.toIso8601String()
    };

    test('converts instance', () {
      final pkg = toPackage(packageJson);

      expect(pkg.id, id);
      expect(pkg.purl, purl);
      expect(pkg.updated, timestamp);
    });

    test('converts list', () {
      final list = toPackageList([packageJson]);

      expect(list.first, isInstanceOf<Package>());
    });

    test('throws for missing id', () {
      expect(() => toPackage({}), throwsFormatException);
    });

    test('throws for missing package URL', () {
      expect(() => toPackage({'id': id}), throwsFormatException);
    });

    test('throws for missing timestamp', () {
      expect(() => toPackage({'id': id, 'purl': purl.toString()}),
          throwsFormatException);
    });
  });
}
