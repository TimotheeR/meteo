import 'dart:async';
import 'package:flutter/material.dart';
import 'package:test_sncf_connect_tech/models/datas_2_screen_model.dart';
import 'package:test_sncf_connect_tech/models/weather_model.dart';
import 'package:intl/intl.dart';
import 'package:test_sncf_connect_tech/screens/datas_2_screen.dart';
import 'package:test_sncf_connect_tech/screens/login_screen.dart';

import '../models/datas_screen_model.dart';

class DatasScreen extends StatefulWidget {
  const DatasScreen({Key? key}) : super(key: key);

  static const routeName = '/datasScreen';

  @override
  State<DatasScreen> createState() => _DatasScreenState();
}

class _DatasScreenState extends State<DatasScreen> {
  late Future<DataWeather> futureData;

  @override
  void initState() {
    super.initState();
    futureData = fetchDataWeather();
  }

  @override
  Widget build(BuildContext context) {
    final args = ModalRoute.of(context)!.settings.arguments as DatasScreenModel;
    return Scaffold(
      appBar: AppBar(
        title: Text('Bienvenue ${args.user.name}'),
        leading: IconButton(
            onPressed: () => Navigator.pushReplacement(context,
                MaterialPageRoute(builder: (context) => const LoginScreen())),
            icon: const Icon(Icons.arrow_back_ios)),
        actions: [
          IconButton(
              onPressed: () => Navigator.pushNamed(
                    context,
                    Datas2Screen.routeName,
                    arguments: Datas2ScreenModel(args.user),
                  ),
              icon: const Icon(Icons.arrow_forward_ios))
        ],
      ),
      body: FutureBuilder<DataWeather>(
        future: futureData,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            return ListView.builder(
              shrinkWrap: true,
              itemCount: snapshot.data!.weathersList.length,
              itemBuilder: (context, index) {
                return _buildListTile(snapshot.data!.weathersList[index]);
              },
            );
          } else if (snapshot.hasError) {
            return Text('${snapshot.error}');
          }

          return const CircularProgressIndicator();
        },
      ),
    );
  }

  Widget _buildListTile(Weathers weathers) {
    DateTime d = DateTime.fromMillisecondsSinceEpoch(weathers.dt * 1000);
    var dateString = DateFormat('dd MMM yyyy HH:mm', 'fr').format(d);

    return Card(
        elevation: 0,
        color: Colors.grey[350],
        child: ListTile(
          contentPadding: const EdgeInsets.only(right: 8),
          leading: Image.network(
              "http://openweathermap.org/img/wn/${weathers.weathers.first.icon}@2x.png"),
          title: Text(dateString),
          trailing: Text("${weathers.main.temp.toStringAsFixed(2)}°C"),
          subtitle: Text(weathers.weathers.first.description),
        ));
  }
}
