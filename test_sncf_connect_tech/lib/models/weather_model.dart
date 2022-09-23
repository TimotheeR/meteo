import 'dart:async';
import 'dart:convert';

import 'package:http/http.dart' as http;

Future<DataWeather> fetchDataWeather() async {
  final response = await http.get(Uri.parse(
      "http://api.openweathermap.org/data/2.5/forecast?q=Paris&appid=a91b5b6acd1591f69c97078b990e5937&lang=fr&units=metric"));

  if (response.statusCode == 200) {
    return DataWeather.fromJson(jsonDecode(response.body));
  } else {
    throw Exception('Failed to load datas');
  }
}

class DataWeather {
  late String cod;
  late num message;
  late num cnt;
  List<Weathers> weathersList = [];

  DataWeather(
      {required this.cod,
      required this.message,
      required this.cnt,
      required this.weathersList});

  DataWeather.fromJson(Map<String, dynamic> json) {
    List<Weathers> w = [];

    json["list"]?.forEach((e) {
      w.add(Weathers.fromJson(e));
    });
    weathersList = w;
    cod = json['cod'];
    message = json['message'];
    cnt = json['cnt'];
  }

  @override
  String toString() {
    return 'Weather{cod: $cod, message: $message, cnt: $cnt}';
  }
}

class Weathers {
  late int dt;
  late Main main;
  List<Weather> weathers = [];

  Weathers({required this.dt, required this.main, required this.weathers});

  Weathers.fromJson(Map<String, dynamic> json) {
    List<Weather> w = [];

    json["weather"]?.forEach((e) {
      w.add(Weather.fromJson(e));
    });

    weathers = w;
    dt = json['dt'];
    main = Main.fromJson(json["main"]);
  }

  @override
  String toString() {
    return 'Weathers{dt: ${DateTime.fromMillisecondsSinceEpoch(dt * 1000).day}}';
  }
}

class Weather {
  final num id;
  final String main;
  final String description;
  final String icon;

  Weather(
      {required this.id,
      required this.main,
      required this.description,
      required this.icon});

  factory Weather.fromJson(Map<String, dynamic> json) {
    return Weather(
        id: json['id'],
        main: json['main'],
        description: json['description'],
        icon: json['icon']);
  }

  @override
  String toString() {
    return 'Weather{dt: $id}';
  }
}

class Main {
  final num temp;
  final num tempMin;
  final num tempMax;

  Main({required this.temp, required this.tempMin, required this.tempMax});

  factory Main.fromJson(Map<String, dynamic> json) {
    return Main(
        temp: json['temp'],
        tempMin: json['temp_min'],
        tempMax: json['temp_max']);
  }

  @override
  String toString() {
    return 'Main{temp: $temp}';
  }
}

class WeatherDay {
  late int dt;
  late String icon;
  late num tempMin;
  late num tempMax;

  WeatherDay(
      {required this.dt,
      required this.icon,
      required this.tempMin,
      required this.tempMax});
  @override
  String toString() {
    return 'WeatherDay{dt: $dt, icon: $icon, tempMin: $tempMin, tempMax: $tempMax}';
  }
}
