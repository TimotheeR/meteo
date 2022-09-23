import 'package:flutter/material.dart';
import 'package:test_sncf_connect_tech/screens/datas_2_screen.dart';
import 'package:test_sncf_connect_tech/screens/datas_screen.dart';
import 'package:test_sncf_connect_tech/screens/login_screen.dart';

import 'package:flutter_localizations/flutter_localizations.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      supportedLocales: const [Locale('fr', '')],
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate
      ],
      title: 'Météo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      initialRoute: '/',
      routes: {
        '/': (context) => const LoginScreen(),
        DatasScreen.routeName: (context) => const DatasScreen(),
        Datas2Screen.routeName: (context) => const Datas2Screen(),
      },
    );
  }
}
