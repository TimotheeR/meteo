import 'package:flutter/services.dart' show rootBundle;
import 'dart:convert';
import 'package:flutter/services.dart';

Future<DataUser> fetchDataUser() async {
  var jsonText = await rootBundle.loadString('assets/users.json');
  return DataUser.fromJson(jsonDecode(jsonText));
}

class DataUser {
  List<User> usersList = [];

  DataUser({required this.usersList});

  DataUser.fromJson(Map<String, dynamic> json) {
    List<User> u = [];

    json["users"]?.forEach((e) {
      u.add(User.fromJson(e));
    });
    usersList = u;
  }

  @override
  String toString() {
    return 'DataUser{usersList : ${usersList.toString()}}';
  }
}

class User {
  late String email;
  late String password;
  late String name;

  User({required this.email, required this.password, this.name = ""});

  User.fromJson(Map<String, dynamic> json) {
    email = json['email'];
    password = json['password'];
    name = json['name'];
  }

  @override
  bool operator ==(Object other) =>
      other is User &&
      other.runtimeType == runtimeType &&
      other.email == email &&
      other.password == password;

  @override
  int get hashCode => email.hashCode;

  @override
  String toString() {
    return 'User{email: $email, password: $password, name: $name }';
  }
}
