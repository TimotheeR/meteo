import 'package:flutter/material.dart';
import 'package:test_sncf_connect_tech/models/datas_screen_model.dart';
import 'package:test_sncf_connect_tech/screens/datas_screen.dart';
import 'dart:async';
import 'package:test_sncf_connect_tech/models/weather_model.dart';
import 'package:intl/intl.dart';
import '../models/datas_2_screen_model.dart';
import '../models/weather_model.dart';

class Datas2Screen extends StatefulWidget {
  const Datas2Screen({Key? key}) : super(key: key);

  static const routeName = '/datas2Screen';
  @override
  State<Datas2Screen> createState() => _Datas2ScreenState();
}

class _Datas2ScreenState extends State<Datas2Screen> {
  late Future<DataWeather> futureData;
  late DateTime now;
  @override
  void initState() {
    super.initState();
    now = DateTime.now().toLocal();
    futureData = fetchDataWeather();
  }

  @override
  Widget build(BuildContext context) {
    final args =
        ModalRoute.of(context)!.settings.arguments as Datas2ScreenModel;
    return Scaffold(
      appBar: AppBar(
          title: const Text("Ecran supplémentaire"),
          leading: IconButton(
              onPressed: () => Navigator.pushNamed(
                    context,
                    DatasScreen.routeName,
                    arguments: DatasScreenModel(args.user),
                  ),
              icon: const Icon(Icons.arrow_back_ios_new))),
      body: SafeArea(
        child: FutureBuilder<DataWeather>(
          future: futureData,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              List<Weathers> weathersToday = [];
              List<Weathers> weathersOthersDays = [];
              num tempMaxToday = 0;
              num tempMinToday = 100;
              String textDate = "Aujourd'hui";

//isoluation des données du jour + recherche des temp max et min du jour
              for (var weather in snapshot.data!.weathersList) {
                DateTime dt =
                    DateTime.fromMillisecondsSinceEpoch(weather.dt * 1000);
                if (dt.day == now.day) {
                  weathersToday.add(weather);
                  if (weather.main.tempMax > tempMaxToday) {
                    tempMaxToday = weather.main.tempMax;
                  }

                  if (weather.main.tempMin < tempMinToday) {
                    tempMinToday = weather.main.tempMin;
                  }
                } else {
                  weathersOthersDays.add(weather);
                }
              }

//Pour la periode entre 23h et minuit, pas de données donc affichage du lendemain au complet

              if (weathersToday.isEmpty) {
                textDate = "Demain";
                for (var weather in snapshot.data!.weathersList) {
                  DateTime dt =
                      DateTime.fromMillisecondsSinceEpoch(weather.dt * 1000);
                  if (dt.day == now.day + 1) {
                    weathersToday.add(weather);
                    weathersOthersDays.remove(weather);
                    if (weather.main.tempMax > tempMaxToday) {
                      tempMaxToday = weather.main.tempMax;
                    }

                    if (weather.main.tempMin < tempMinToday) {
                      tempMinToday = weather.main.tempMin;
                    }
                  }
                }
              }

//regroupement des données météo de chaque jours + recherche des temp max et min pour chaque jour
              DateTime date = DateTime.fromMillisecondsSinceEpoch(
                  weathersOthersDays.first.dt * 1000);

              DateTime.utc(date.year, date.month, date.day);

              Map<int, WeatherDay> weatherDays = <int, WeatherDay>{
                date.day: WeatherDay(
                    dt: weathersOthersDays.first.dt,
                    tempMax: weathersOthersDays.first.main.tempMax,
                    tempMin: weathersOthersDays.first.main.tempMin,
                    icon: weathersOthersDays.first.weathers.first.icon)
              };

              for (var weather in weathersOthersDays) {
                DateTime dateWeather =
                    DateTime.fromMillisecondsSinceEpoch(weather.dt * 1000);

                if (!weatherDays.keys.contains(dateWeather.day)) {
                  weatherDays[dateWeather.day] = WeatherDay(
                      dt: weather.dt,
                      tempMax: weather.main.tempMax,
                      tempMin: weather.main.tempMin,
                      icon: weather.weathers.first.icon);
                } else {
                  if (weather.main.tempMax >
                      weatherDays[dateWeather.day]!.tempMax) {
                    weatherDays[dateWeather.day]!.tempMax =
                        weather.main.tempMax;
                  }
                  if (weatherDays[dateWeather.day]!.tempMin >
                      weather.main.tempMin) {
                    weatherDays[dateWeather.day]!.tempMin =
                        weather.main.tempMin;
                  }
                }
              }
              DateTime d = DateTime.fromMillisecondsSinceEpoch(
                  snapshot.data!.weathersList.first.dt * 1000);
              var dayDateString = DateFormat('EEEE', 'fr').format(d);

              return Center(
                child: Column(children: [
                  Expanded(
                      child: Align(
                          alignment: Alignment.center,
                          child: _buildNowPart(
                              snapshot.data!.weathersList.first))),
                  _buildWeathersTodayPart(
                      weather: snapshot.data!.weathersList.first,
                      weathersToday: weathersToday,
                      dayDateString: dayDateString,
                      textDate: textDate,
                      tempMaxToday: tempMaxToday,
                      tempMinToday: tempMinToday),
                  Expanded(
                    child: Align(
                      alignment: Alignment.topCenter,
                      child: ListView.builder(
                        padding: const EdgeInsets.symmetric(horizontal: 8),
                        itemCount: weatherDays.length,
                        shrinkWrap: true,
                        itemBuilder: (context, index) {
                          return _buildWeathersOthersDays(
                              weatherDays.values.elementAt(index));
                        },
                      ),
                    ),
                  ),
                ]),
              );
            } else if (snapshot.hasError) {
              return Text('${snapshot.error}');
            }

            return const CircularProgressIndicator();
          },
        ),
      ),
    );
  }

  Widget _buildNowPart(Weathers weather) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text("Paris", style: Theme.of(context).textTheme.headline3),
        Text(weather.weathers.first.description,
            style: Theme.of(context).textTheme.headline6),
        Text("${weather.main.temp.toStringAsFixed(0)}°C",
            style: Theme.of(context).textTheme.headline2),
      ],
    );
  }

  Widget _buildWeathersTodayPart(
      {required Weathers weather,
      required List<Weathers> weathersToday,
      required String dayDateString,
      required String textDate,
      required num tempMaxToday,
      required num tempMinToday}) {
    return Column(children: [
      Padding(
        padding: const EdgeInsets.symmetric(horizontal: 8.0),
        child: Row(
          children: [
            Padding(
              padding: const EdgeInsets.only(right: 8.0),
              child: Text(dayDateString,
                  style: const TextStyle(fontWeight: FontWeight.bold)),
            ),
            Text(textDate),
            Expanded(
              child: Align(
                alignment: Alignment.bottomRight,
                child: Text(tempMaxToday.toStringAsFixed(0),
                    style: const TextStyle(fontWeight: FontWeight.bold)),
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(left: 8.0),
              child: Text(tempMinToday.toStringAsFixed(0)),
            ),
          ],
        ),
      ),
      const Divider(),
      SizedBox(
        height: 100,
        child: ListView.builder(
          scrollDirection: Axis.horizontal,
          itemCount: weathersToday.length,
          itemBuilder: (context, index) {
            return _buildWeathersToday(weathersToday[index]);
          },
        ),
      ),
      const Divider(),
    ]);
  }

  Widget _buildWeathersToday(Weathers weathers) {
    DateTime d = DateTime.fromMillisecondsSinceEpoch(weathers.dt * 1000);
    var dateString = DateFormat('HH', 'fr').format(d);
    return Column(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(dateString + ' h'),
        Image.network(
          "http://openweathermap.org/img/wn/${weathers.weathers.first.icon}@2x.png",
          height: 50,
        ),
        Text(weathers.main.temp.toStringAsFixed(0) + '°'),
      ],
    );
  }

  Widget _buildWeathersOthersDays(WeatherDay weathers) {
    DateTime d = DateTime.fromMillisecondsSinceEpoch(weathers.dt * 1000);
    var dateString = DateFormat('EEEE', 'fr').format(d);
    return Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
      Text(dateString),
      Expanded(
        child: Align(
          alignment: Alignment.bottomRight,
          child: Image.network(
            "http://openweathermap.org/img/wn/${weathers.icon}@2x.png",
            height: 50,
          ),
        ),
      ),
      Expanded(
        child: Align(
          alignment: Alignment.centerRight,
          child: Text(weathers.tempMax.toStringAsFixed(0),
              style: const TextStyle(fontWeight: FontWeight.bold)),
        ),
      ),
      const SizedBox(width: 8),
      Text(weathers.tempMin.toStringAsFixed(0)),
    ]);
  }
}
