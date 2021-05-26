/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_base_ui/model/package.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('$Package', () {
    test('generates purl from id', () {
      final package = Package(id: 'pkg%253atype%252fname%40version');

      expect(package.purl, Uri.parse('pkg:type/name@version'));
    });
  });
}
