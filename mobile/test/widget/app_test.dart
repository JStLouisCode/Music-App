import 'package:flutter_test/flutter_test.dart';
import 'package:music_app/presentation/app.dart';

void main() {
  testWidgets('practice screen shows the target note', (tester) async {
    await tester.pumpWidget(const MusicApp());

    expect(find.text('Play this note'), findsOneWidget);
    expect(find.text('A4'), findsOneWidget);
  });
}
