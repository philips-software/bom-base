/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_base_ui/services/bombar_client.dart';
import 'package:bom_base_ui/services/package_service.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';

import 'package_service_test.mocks.dart';

@GenerateMocks([BomBarClient])
main() {
  group('$PackageService', () {
    late MockBomBarClient client;

    setUp(() {
      client = MockBomBarClient();
    });

    group('find packages', () {
      const type = 'type';
      const namespace = 'namespace';
      const name = 'name';
      const version = 'version';

      late PackageService service;

      setUp(() {
        service = PackageService(client: client);
        when(client.find(any, any, any, any))
            .thenAnswer((_) => Future.value([]));
      });

      test('extracts parts from query', () {
        service.find('$type:$namespace/$name@$version');

        verify(client.find(type, namespace, name, version));
      });

      test('removes all spaces from query', () {
        service.find('$type :\t$namespace\n/ $name @ $version');

        verify(client.find(type, namespace, name, version));
      });

      test('extracts type from query', () {
        service.find('$type:');

        verify(client.find(type, '', '', ''));
      });

      test('extracts name from query', () {
        service.find('$name');

        verify(client.find('', '', name, ''));
      });

      test('extracts namespace from query', () {
        service.find('$namespace/$name');

        verify(client.find('', namespace, name, ''));
      });

      test('extracts version from query', () {
        service.find('@$version');

        verify(client.find('', '', '', version));
      });
    });
  });
}
