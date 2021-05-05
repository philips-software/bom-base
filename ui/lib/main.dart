import 'package:bom_base_ui/screens/app_ui.dart';
import 'package:bom_base_ui/services/bombase_client.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'services/package_service.dart';

void main() {
  final bomBarClient = BomBaseClient();
  runApp(
    MultiProvider(
      providers: [
        Provider<PackageService>(
            create: (_) => PackageService(client: bomBarClient)),
      ],
      child: BomBarUI(),
    ),
  );
}
