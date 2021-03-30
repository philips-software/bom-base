import 'package:bom_base_ui/services/bombar_client.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'screens/main/main_screen.dart';
import 'services/package_service.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  final bomBarClient = BomBarClient();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'BOM-Base',
      theme: ThemeData(
        primarySwatch: Colors.green,
      ),
      home: MultiProvider(providers: [
        Provider<PackageService>(
            create: (_) => PackageService(client: bomBarClient)),
      ], child: SearchScreen()),
    );
  }
}
