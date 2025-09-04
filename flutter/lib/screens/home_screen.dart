import 'package:ai_assistant/screens/home/profile_screen.dart';
import 'package:flutter/material.dart';

import 'package:speech_to_text/speech_recognition_result.dart';
import 'package:speech_to_text/speech_to_text.dart';
import 'package:flutter_tts/flutter_tts.dart';

import 'package:ai_assistant/api/openai_services.dart';
import 'package:ai_assistant/screens/home/chat_screen.dart';
import 'package:ai_assistant/screens/home/history_screen.dart';
import 'package:ai_assistant/utils/strings.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final speechToText = SpeechToText();
  final flutterTts = FlutterTts();

  final OpenAIService openAIService = OpenAIService();

  String lastWords = '';
  String? generatedContent;
  String? generatedImageUrl;

  int start = 200;
  int delay = 200;

  @override
  void initState() {
    super.initState();

    initSpeechToText();
    initTextToSpeech();
  }

  Future<void> initTextToSpeech() async {
    await flutterTts.setSharedInstance(true);
    setState(() {});
  }

  Future<void> initSpeechToText() async {
    await speechToText.initialize();
    setState(() {});
  }

  Future<void> startListening() async {
    await speechToText.listen(onResult: onSpeechResult);
    setState(() {});
  }

  Future<void> stopListening() async {
    await speechToText.stop();
    setState(() {});
  }

  void onSpeechResult(SpeechRecognitionResult result) {
    setState(() {
      lastWords = result.recognizedWords;
    });
  }

  Future<void> systemSpeak(String content) async {
    await flutterTts.speak(content);
  }

  @override
  void dispose() {
    super.dispose();
    speechToText.stop();
    flutterTts.stop();
  }

  @override
  Widget build(BuildContext context) {
    return const DefaultTabController(
      length: 3,
      child: Scaffold(
        body: SafeArea(
          child: Column(
            children: [
              TabBar(
                // indicatorSize: TabBarIndicatorSize.tab,
                enableFeedback: true,
                tabs: [
                  Tab(text: Strings.ask),
                  Tab(text: Strings.history),
                  Tab(text: Strings.profile),
                ],
              ),
              Expanded(
                child: TabBarView(
                  children: [
                    ChatScreen(),
                    HistoryScreen(),
                    ProfileScreen(),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
