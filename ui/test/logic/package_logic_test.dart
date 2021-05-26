/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_base_ui/logic/package_logic.dart';
import 'package:bom_base_ui/model/package.dart';
import 'package:bom_base_ui/services/bombase_client.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';
import 'package:test/test.dart';

import '../services/package_service_test.mocks.dart';

@GenerateMocks([BomBaseClient])
void main() {
  group('$PackageLogic', () {
    const packageId = 'package id';

    late MockBomBaseClient client;
    late PackageLogic logic;
    late Package package;

    setUp(() {
      client = MockBomBaseClient();
      logic = PackageLogic(packageId, client: client);
      package = Package(
          id: packageId,
          purl: Uri.parse('pkg:type/ns/name@version'),
          updated: DateTime.now());
    });

    test('creates instance', () {
      expect(logic.package.value.id, packageId);
      expect(logic.error.value, isNull);
      expect(logic.isLoading.value, isFalse);
    });

    test('loads package from backend', () async {
      logic.error.value = 'previous';
      when(client.getPackage(packageId))
          .thenAnswer((_) => Future.value(package));

      await logic.init();

      expect(logic.package.value.updated, isNotNull);
      expect(logic.isLoading.value, isFalse);
      expect(logic.error.value, isNull);
    });

    test('indicates loading failure', () async {
      when(client.getPackage(packageId))
          .thenAnswer((realInvocation) => Future.error(Error()));

      await logic.init();

      expect(logic.error.value, isNotNull);
      expect(logic.isLoading.value, isFalse);
    });
  });
}
