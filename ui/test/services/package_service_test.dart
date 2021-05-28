/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_base_ui/model/package.dart';
import 'package:bom_base_ui/services/bombase_client.dart';
import 'package:bom_base_ui/services/package_service.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';
import 'package:test/test.dart';

import 'package_service_test.mocks.dart';

@GenerateMocks([BomBaseClient])
void main() {
  group('$PackageService', () {
    late MockBomBaseClient client;
    late PackageService service;

    setUp(() {
      client = MockBomBaseClient();
      service = PackageService(client: client);
    });

    tearDown(() {
      service.dispose();
    });

    group('find packages', () {
      const type = 'type';
      const namespace = 'namespace';
      const name = 'name';
      const version = 'version';
      const queryString = '$type:$namespace/$name@$version';

      setUp(() {
        when(client.find(any, any, any, any))
            .thenAnswer((_) => Future.value([]));
      });

      test('extracts parts from query string', () {
        service.find(queryString);

        verify(client.find(type, namespace, name, version));
      });

      test('removes all spaces from query string', () {
        service.find('$type :\t$namespace\n/ $name @ $version');

        verify(client.find(type, namespace, name, version));
      });

      test('extracts type from query string', () {
        service.find('$type:');

        verify(client.find(type, '', '', ''));
      });

      test('extracts name from query string', () {
        service.find('$name');

        verify(client.find('', '', name, ''));
      });

      test('extracts namespace from query string', () {
        service.find('$namespace/$name');

        verify(client.find('', namespace, name, ''));
      });

      test('extracts version from query string', () {
        service.find('@$version');

        verify(client.find('', '', '', version));
      });

      test('publishes search results as stream', () {
        final package = Package(
          id: 'id',
          purl: Uri.parse('pkg:purl'),
          updated: DateTime.now(),
        );
        when(client.find(type, namespace, name, version))
            .thenAnswer((_) => Future.value([package]));

        service.find(queryString);

        expect(service.found, emits([package]));
      });
    });

    group('select package', () {
      const id = 'id';
      late Package package;

      setUp(() {
        package = Package(
          id: 'id',
          purl: Uri.parse('pkg:purl'),
          updated: DateTime.now(),
        );
      });

      test('selects package by id', () async {
        when(client.getPackage(id)).thenAnswer((_) => Future.value(package));

        final pkg = await service.select(id);

        expect(pkg, package);
        expect(service.current, package);
      });
    });
  });
}
