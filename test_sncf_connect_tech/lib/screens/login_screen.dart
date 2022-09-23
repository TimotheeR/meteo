import 'package:flutter/material.dart';
import 'package:test_sncf_connect_tech/colors.dart';
import 'package:test_sncf_connect_tech/screens/datas_screen.dart';
import 'dart:async';
import 'package:test_sncf_connect_tech/models/user_model.dart';

import '../models/datas_screen_model.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({Key? key}) : super(key: key);

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();

  late TextEditingController _emailController;
  late TextEditingController _passwordController;

  late bool displayPassword;

  static const String emailRegexPattern =
      r'^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$';

  late Future<DataUser> futureData;

  @override
  void initState() {
    super.initState();
    futureData = fetchDataUser();

    super.initState();
    _emailController = TextEditingController();
    _passwordController = TextEditingController();
    displayPassword = true;
  }

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(title: const Text("LOGIN")), body: content());
  }

  Widget content() {
    return SafeArea(
        child: Padding(
      padding: const EdgeInsets.only(left: 20, right: 20, top: 50),
      child: Form(
          key: _formKey,
          child: Column(
            children: [
              emailField(),
              const SizedBox(height: 16),
              passwordField(),
              const SizedBox(height: 50.0),
              _button(),
              Expanded(
                child: Align(
                    alignment: Alignment.bottomRight,
                    child: TextButton(
                        onPressed: () {
                          _emailController.text = "test@gmail.com";
                          _passwordController.text = "password";
                        },
                        child: const Text("pré-remplir"))),
              )
            ],
          )),
    ));
  }

  emailField() => TextFormField(
        textCapitalization: TextCapitalization.none,
        keyboardType: TextInputType.emailAddress,
        controller: _emailController,
        validator: (value) {
          RegExp _emailRegex = RegExp(emailRegexPattern);
          if (!_emailRegex.hasMatch(value!)) {
            return 'Veuillez saisir une adresse email.';
          }
          return null;
        },
        maxLines: 1,
        cursorColor: Colors.black,
        decoration: InputDecoration(
          labelStyle: const TextStyle(color: Colors.black87, fontSize: 16),
          labelText: "Email",
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(7.5),
            borderSide: const BorderSide(color: Colors.black87),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(7.5),
            borderSide: const BorderSide(color: Colors.black38),
          ),
        ),
      );

  passwordField() => TextFormField(
        textCapitalization: TextCapitalization.none,
        keyboardType: TextInputType.text,
        controller: _passwordController,
        validator: (value) {
          if (value == null || value.trim().isEmpty) {
            return "Veuillez saisir un mot de passe.";
          }

          return null;
        },
        maxLines: 1,
        obscureText: displayPassword,
        cursorColor: Colors.black,
        decoration: InputDecoration(
          suffixIcon: IconButton(
            icon: Visibility(
              visible: displayPassword,
              child: const Icon(Icons.visibility, color: Colors.black),
              replacement:
                  const Icon(Icons.visibility_off, color: Colors.black),
            ),
            onPressed: () {
              setState(() => displayPassword = !displayPassword);
            },
          ),
          labelStyle: const TextStyle(color: Colors.black87, fontSize: 16),
          labelText: "Mot de passe",
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(7.5),
            borderSide: const BorderSide(color: Colors.black87),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(7.5),
            borderSide: const BorderSide(color: Colors.black38),
          ),
        ),
      );

  Widget _button() {
    return ElevatedButton(
        style: ElevatedButton.styleFrom(
            padding: const EdgeInsets.symmetric(vertical: 16.0, horizontal: 30),
            elevation: 8.0,
            shape: const RoundedRectangleBorder(
                borderRadius: BorderRadius.all(Radius.circular(8.0))),
            primary: Colors.black),
        child: const Text("Connexion",
            style: TextStyle(color: Colors.white, fontSize: 20.0)),
        onPressed: () async {
          if (!_formKey.currentState!.validate()) return print('Something');
          DataUser dataUser = await futureData;
          String email = _emailController.text;
          String password = _passwordController.text;
          User u = User(email: email, password: password);
          bool good = false;
          for (var user in dataUser.usersList) {
            if (user == u) {
              good = true;
              Navigator.pushNamed(
                context,
                DatasScreen.routeName,
                arguments: DatasScreenModel(user),
              );
            }
          }
          if (!good) _showDialog();
        });
  }

  void _showDialog() {
    String title = "Erreur connexion";
    String description = "L'adresse email et/ou le mot de passe sont erronés.";

    showDialog(
      context: context,
      builder: (BuildContext context) {
        dialogContent(BuildContext context) {
          return Container(
            padding: const EdgeInsets.all(16.0),
            margin: const EdgeInsets.only(top: 60.0),
            decoration: BoxDecoration(
              color: Colors.white,
              shape: BoxShape.rectangle,
              borderRadius: BorderRadius.circular(16.0),
              boxShadow: const [
                BoxShadow(
                  color: Colors.black26,
                  blurRadius: 10.0,
                  offset: Offset(0.0, 10.0),
                ),
              ],
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                Text(
                  title,
                  style: const TextStyle(
                    fontSize: 24.0,
                    fontWeight: FontWeight.w700,
                  ),
                ),
                const SizedBox(height: 16.0),
                Text(
                  description,
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    fontSize: 16.0,
                  ),
                ),
                const SizedBox(height: 24.0),
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: <Widget>[
                    TextButton(
                      onPressed: () {
                        Navigator.of(context).pop();
                      },
                      child: const Icon(Icons.clear, color: errorColor),
                    ),
                  ],
                )
              ],
            ),
          );
        }
        return Dialog(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(20.0),
          ),
          elevation: 0.0,
          backgroundColor: Colors.transparent,
          child: dialogContent(context),
        );
      },
    );
  }
}
