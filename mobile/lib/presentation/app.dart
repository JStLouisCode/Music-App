import 'package:flutter/material.dart';
import 'package:music_app/presentation/practice_screen.dart';

class MusicApp extends StatelessWidget {
  const MusicApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Music Trainer',
      theme: ThemeData(
        colorSchemeSeed: Colors.teal,
        brightness: Brightness.dark,
      ),
      home: const PracticeScreen(),
    );
  }
}
