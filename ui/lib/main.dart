import 'package:flutter/material.dart';

import 'screens/main/main_screen.dart';

void main() {
  runApp(MyApp());
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
