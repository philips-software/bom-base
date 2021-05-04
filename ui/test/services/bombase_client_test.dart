/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */

import 'package:bom_base_ui/services/bombase_client.dart';
import 'package:mockito/annotations.dart';
import 'package:test/test.dart';

import 'dio_mock_server.dart';

@GenerateMocks([])
void main() {
  group('$BomBaseClient', () {
    const id = 'id';
    const type = 'type';
    const namespace = 'namespace';
    const name = 'name';
    const version = 'version';
    final purl = Uri.parse('pkg:$type/$namespace/$name@$version');
    final timestamp = DateTime.now();

    late BomBaseClient client;
    late DioMockServer mockServer;

    setUp(() {
      client = BomBaseClient();
      mockServer = DioMockServer(client.dio);
    });

    group('error handling', () {
      test('throws on error response', () {
        const message = 'Message';

        mockServer.respondStatus(404, statusMessage: message);

        expect(
            () => client.find(type, namespace, name, version),
            throwsA(predicate<BackendException>(
                (e) => e.message.contains('status error [404]'))));
      });
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

    group('get package', () {
      test('gets package by id', () async {
        const attr = 'title';

        mockServer.respondJson({
          'updated': timestamp.toIso8601String(),
          'attributes': {attr: attr},
        });

        final pkg = await client.getPackage(id);

        final query = mockServer.requests.first;
        expect(query.path, client.baseUri.resolve('packages/$id').toString());
        expect(query.method, 'GET');
        expect(pkg.attributes[attr], attr);
      });
    });
  });
}
