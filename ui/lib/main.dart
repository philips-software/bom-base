import 'package:bom_base_ui/services/bombase_client.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'screens/main/main_screen.dart';
import 'services/package_service.dart';

void main() {
  final bomBarClient = BomBaseClient();
  runApp(
    MultiProvider(
      providers: [
        Provider<PackageService>(
            create: (_) => PackageService(client: bomBarClient)),
      ],
      child: MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'BOM-Base',
      theme: ThemeData(
        primarySwatch: Colors.green,
      ),
      home: SearchScreen(),
    );
  }
}
