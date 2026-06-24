import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'core/network/session_manager.dart';
import 'features/auth/auth_cubit.dart';
import 'features/auth/email_screen.dart';
import 'features/auth/otp_screen.dart';
import 'features/chat/chat_cubit.dart';
import 'features/home/home_cubit.dart';
import 'features/history/history_cubit.dart';
import 'features/main/main_screen.dart';
import 'features/profile/profile_cubit.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final prefs = await SharedPreferences.getInstance();
  final sessionManager = SessionManager(prefs);

  runApp(
    MultiBlocProvider(
      providers: [
        BlocProvider(create: (_) => AuthCubit(sessionManager)),
        BlocProvider(create: (_) => HomeCubit(sessionManager)),
        BlocProvider(create: (_) => HistoryCubit(sessionManager)),
        BlocProvider(create: (_) => ProfileCubit(sessionManager)),
        BlocProvider(create: (_) => ChatCubit(sessionManager)),
      ],
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    // Custom premium dark color scheme
    final darkTheme = ThemeData(
      brightness: Brightness.dark,
      colorScheme: const ColorScheme.dark(
        surface: Color(0xFF1E293B), // Slate 800
        primary: Color(0xFF818CF8), // Indigo 400
        secondary: Color(0xFF94A3B8), // Slate 400
        onSurface: Colors.white,
        error: Color(0xFFF87171), // Red 400
      ),
      useMaterial3: true,
      scaffoldBackgroundColor: const Color(0xFF0F172A),
    );

    // Custom premium light color scheme
    final lightTheme = ThemeData(
      brightness: Brightness.light,
      colorScheme: const ColorScheme.light(
        surface: Colors.white,
        primary: Color(0xFF4F46E5), // Indigo 600
        secondary: Color(0xFF475569), // Slate 600
        onSurface: Color(0xFF0F172A),
        error: Color(0xFFDC2626), // Red 600
      ),
      useMaterial3: true,
      scaffoldBackgroundColor: const Color(0xFFF8FAFC),
    );

    return BlocBuilder<ProfileCubit, ProfileState>(
      builder: (context, state) {
        return MaterialApp(
          title: 'AI Assistant',
          debugShowCheckedModeBanner: false,
          theme: lightTheme,
          darkTheme: darkTheme,
          themeMode: state.isDarkTheme ? ThemeMode.dark : ThemeMode.light,
          home: const SplashScreen(),
          routes: {
            '/otp': (context) => OtpScreen(
                  onNavigateToHome: () {
                    Navigator.pushReplacement(
                      context,
                      MaterialPageRoute(builder: (context) => const MainScreen()),
                    );
                  },
                ),
          },
        );
      },
    );
  }
}

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    _checkSession();
  }

  Future<void> _checkSession() async {
    final authCubit = context.read<AuthCubit>();
    final token = authCubit.sessionManager.jwtToken;

    // A minor delay to prevent screen flicker and simulate smooth splash loading
    await Future.delayed(const Duration(milliseconds: 600));

    if (token != null && token.isNotEmpty) {
      if (mounted) {
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => const MainScreen()),
        );
      }
    } else {
      if (mounted) {
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(
            builder: (context) => EmailScreen(
              onNavigateToOtp: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => OtpScreen(
                      onNavigateToHome: () {
                        Navigator.pushReplacement(
                          context,
                          MaterialPageRoute(
                            builder: (context) => const MainScreen(),
                          ),
                        );
                      },
                    ),
                  ),
                );
              },
            ),
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(
        child: CircularProgressIndicator(),
      ),
    );
  }
}
