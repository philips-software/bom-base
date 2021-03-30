/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_base_ui/services/bombar_client.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';

import 'dio_mock_server.dart';

@GenerateMocks([])
main() {
  group('$BomBarClient', () {
    const id = 'id';
    const type = 'type';
    const namespace = 'namespace';
    const name = 'name';
    const version = 'version';
    final purl = Uri.parse('pkg:$type/$namespace/$name@$version');
    final timestamp = DateTime.now();

    late BomBarClient client;
    late DioMockServer mockServer;

    setUp(() {
      client = BomBarClient();
      mockServer = DioMockServer(client.dio);
    });

    group('find packages', () {
      test('query packages', () async {
        mockServer.respondJson({
          'results': [
            {
              'id': id,
              'purl': purl.toString(),
              'updated': timestamp.toIso8601String()
            }
          ]
        });

        final packages = await client.find(type, namespace, name, version);

        final query = mockServer.requests.first;
        expect(
            query.path,
            client.baseUri
                .resolve(
                    'packages?type=$type&ns=$namespace&name=$name&version=$version')
                .toString());
        expect(query.method, 'GET');
        expect(packages.length, 1);
        final pkg = packages.first;
        expect(pkg.id, id);
        expect(pkg.purl, purl);
        expect(pkg.updated, timestamp);
      });
    });
  });
}
