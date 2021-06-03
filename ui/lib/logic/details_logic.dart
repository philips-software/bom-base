/*
 * Copyright (c) 2020-2021, Koninklijke Philips N.V., https://www.philips.com
 * SPDX-License-Identifier: MIT
 */
import 'package:bom_base_ui/services/bombase_client.dart';

import 'logic.dart';

class DetailsLogic extends Logic {
  DetailsLogic({BomBaseClient? client})
      : client = client ?? BomBaseClient.instance;

  final BomBaseClient client;

  @override
  Future<void> init() async {}
}
