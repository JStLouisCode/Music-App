import 'package:flutter/material.dart';
import 'package:music_app/domain/note.dart';

class PracticeScreen extends StatelessWidget {
  const PracticeScreen({
    super.key,
    this.target = const Note(69),
  });

  final Note target;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // Main dark background
      backgroundColor: const Color(0xFF101414),

      // Top app bar
      appBar: AppBar(
        backgroundColor: const Color(0xFF101414),
        elevation: 0,
        title: const Row(
          children: [
            Icon(
              Icons.graphic_eq,
              color: Color(0xFF14B8A6),
            ),
            SizedBox(width: 8),
            Text(
              'Music Trainer',
              style: TextStyle(
                color: Color(0xFFF5F7F7),
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
        actions: const [
          Padding(
            padding: EdgeInsets.only(right: 16),
            child: Icon(
              Icons.settings_outlined,
              color: Color(0xFFA7B0AF),
            ),
          ),
        ],
      ),

      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            child: Container(
              width: 380,
              margin: const EdgeInsets.all(20),
              padding: const EdgeInsets.all(24),

              // Main card
              decoration: BoxDecoration(
                color: const Color(0xFF1A2221),
                borderRadius: BorderRadius.circular(28),
                border: Border.all(
                  color: const Color(0xFF263331),
                ),
                boxShadow: const [
                  BoxShadow(
                    color: Color(0x55000000),
                    blurRadius: 25,
                    offset: Offset(0, 10),
                  ),
                ],
              ),

              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Page heading
                  const Text(
                    'Practice',
                    style: TextStyle(
                      color: Color(0xFFF5F7F7),
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                    ),
                  ),

                  const SizedBox(height: 6),

                  const Text(
                    'Train your pitch one note at a time.',
                    style: TextStyle(
                      color: Color(0xFFA7B0AF),
                      fontSize: 15,
                    ),
                  ),

                  const SizedBox(height: 28),

                  // Instrument heading
                  const Text(
                    'YOUR INSTRUMENT',
                    style: TextStyle(
                      color: Color(0xFF8D9997),
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                      letterSpacing: 1,
                    ),
                  ),

                  const SizedBox(height: 10),

                  // Instrument selector (visual prototype)
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 16,
                      vertical: 15,
                    ),
                    decoration: BoxDecoration(
                      color: const Color(0xFF163C38),
                      borderRadius: BorderRadius.circular(16),
                      border: Border.all(
                        color: const Color(0xFF245B55),
                      ),
                    ),
                    child: const Row(
                      children: [
                        Icon(
                          Icons.music_note,
                          color: Color(0xFF14B8A6),
                        ),
                        SizedBox(width: 12),
                        Expanded(
                          child: Text(
                            'Guitar',
                            style: TextStyle(
                              color: Color(0xFFF5F7F7),
                              fontSize: 17,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),
                        Icon(
                          Icons.keyboard_arrow_down,
                          color: Color(0xFF14B8A6),
                        ),
                      ],
                    ),
                  ),

                  const SizedBox(height: 24),

                  // Note challenge card
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.symmetric(
                      vertical: 30,
                      horizontal: 20,
                    ),
                    decoration: BoxDecoration(
                      color: const Color(0xFF141C1B),
                      borderRadius: BorderRadius.circular(22),
                      border: Border.all(
                        color: const Color(0xFF245B55),
                      ),
                    ),
                    child: Column(
                      children: [
                        const Text(
                          'PLAY THIS NOTE',
                          style: TextStyle(
                            color: Color(0xFF14B8A6),
                            fontSize: 12,
                            fontWeight: FontWeight.bold,
                            letterSpacing: 1.2,
                          ),
                        ),

                        const SizedBox(height: 12),

                        // Note supplied by the team's domain model
                        Text(
                          target.toString(),
                          style: const TextStyle(
                            color: Color(0xFF2DD4BF),
                            fontSize: 82,
                            height: 1,
                            fontWeight: FontWeight.bold,
                          ),
                        ),

                        const SizedBox(height: 14),

                        const Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(
                              Icons.graphic_eq,
                              color: Color(0xFF8D9997),
                              size: 18,
                            ),
                            SizedBox(width: 6),
                            Text(
                              'Match the pitch',
                              style: TextStyle(
                                color: Color(0xFF8D9997),
                                fontSize: 14,
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),

                  const SizedBox(height: 24),

                  // Listening button (visual only for now)
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.symmetric(
                      vertical: 17,
                    ),
                    decoration: BoxDecoration(
                      color: const Color(0xFF14B8A6),
                      borderRadius: BorderRadius.circular(16),
                      boxShadow: const [
                        BoxShadow(
                          color: Color(0x3314B8A6),
                          blurRadius: 14,
                          offset: Offset(0, 5),
                        ),
                      ],
                    ),
                    child: const Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.mic,
                          color: Color(0xFF081312),
                        ),
                        SizedBox(width: 10),
                        Text(
                          'Start Listening',
                          style: TextStyle(
                            color: Color(0xFF081312),
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ],
                    ),
                  ),

                  const SizedBox(height: 18),

                  // Listening status
                  const Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                        Icons.circle,
                        size: 9,
                        color: Color(0xFF2DD4BF),
                      ),
                      SizedBox(width: 8),
                      Text(
                        'Ready to listen',
                        style: TextStyle(
                          color: Color(0xFFA7B0AF),
                          fontSize: 14,
                        ),
                      ),
                    ],
                  ),

                  const SizedBox(height: 16),

                  // Future new-note control
                  const Center(
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(
                          Icons.refresh,
                          color: Color(0xFF14B8A6),
                          size: 19,
                        ),
                        SizedBox(width: 7),
                        Text(
                          'Try another note',
                          style: TextStyle(
                            color: Color(0xFF14B8A6),
                            fontSize: 14,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}