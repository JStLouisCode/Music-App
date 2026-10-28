import 'package:flutter/material.dart';
import 'package:music_app/domain/note.dart';

/// Placeholder practice screen. The real UI (instrument picker, listening state,
/// feedback) will replace this.
class PracticeScreen extends StatelessWidget {
  const PracticeScreen({super.key, this.target = const Note(69)});

  final Note target;

  @override
  Widget build(BuildContext context) {
    final textTheme = Theme.of(context).textTheme;
    return Scaffold(
      appBar: AppBar(title: const Text('Practice')),
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text('Play this note', style: textTheme.labelLarge),
            Text(target.toString(), style: textTheme.displayLarge),
          ],
        ),
      ),
    );
  }
}
