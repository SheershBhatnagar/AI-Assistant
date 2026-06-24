import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../chat/chat_screen.dart';
import 'home_cubit.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final TextEditingController _promptController = TextEditingController();

  final List<String> _suggestions = const [
    "Write a Python script to sort a list",
    "Explain quantum physics like I'm 5",
    "Suggest healthy dinner options for tonight",
    "Help me draft a polite email requesting leave",
  ];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final cubit = context.read<HomeCubit>();
      cubit.refreshUserName();
      cubit.loadDefaultModelName();
    });
  }

  @override
  void dispose() {
    _promptController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final homeCubit = context.read<HomeCubit>();

    return Scaffold(
      backgroundColor: theme.colorScheme.background,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: BlocBuilder<HomeCubit, HomeState>(
            builder: (context, state) {
              return Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(height: 16),
                  Text(
                    "Hi, ${state.userName}!",
                    style: TextStyle(
                      fontSize: 32,
                      fontWeight: FontWeight.w900,
                      color: theme.colorScheme.primary,
                    ),
                  ),
                  Text(
                    "How can I help you today?",
                    style: TextStyle(
                      color: theme.colorScheme.secondary,
                      fontSize: 16,
                    ),
                  ),
                  const SizedBox(height: 28),
                  // Active Model Indicator
                  Material(
                    color: theme.colorScheme.surface,
                    borderRadius: BorderRadius.circular(12),
                    elevation: 1,
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 14.0, vertical: 8.0),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Container(
                            width: 8,
                            height: 8,
                            decoration: const BoxDecoration(
                              color: Color(0xFF10B981),
                              shape: BoxShape.circle,
                            ),
                          ),
                          const SizedBox(width: 8),
                          Text(
                            "Active Model: ${state.defaultModelName}",
                            style: TextStyle(
                              fontSize: 12,
                              fontWeight: FontWeight.bold,
                              color: theme.colorScheme.onSurface,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  const Spacer(),
                  // Suggestions label
                  Text(
                    "SUGGESTIONS",
                    style: TextStyle(
                      fontSize: 11,
                      fontWeight: FontWeight.bold,
                      letterSpacing: 1.0,
                      color: theme.colorScheme.onSurface.withOpacity(0.5),
                    ),
                  ),
                  const SizedBox(height: 12),
                  // Suggestions scroll row
                  SizedBox(
                    height: 48,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      itemCount: _suggestions.length,
                      itemBuilder: (context, index) {
                        final suggestion = _suggestions[index];
                        final truncated = suggestion.length > 25
                            ? '${suggestion.substring(0, 22)}...'
                            : suggestion;
                        return Padding(
                          padding: const EdgeInsets.only(right: 10.0),
                          child: InkWell(
                            onTap: () {
                              _promptController.text = suggestion;
                            },
                            borderRadius: BorderRadius.circular(16),
                            child: Container(
                              decoration: BoxDecoration(
                                color: theme.colorScheme.surface,
                                borderRadius: BorderRadius.circular(16),
                                border: Border.all(
                                  color: theme.colorScheme.onSurface.withOpacity(0.05),
                                  width: 1.0,
                                ),
                              ),
                              padding: const EdgeInsets.symmetric(horizontal: 14.0, vertical: 10.0),
                              alignment: Alignment.center,
                              child: Text(
                                truncated,
                                style: TextStyle(
                                  fontSize: 13,
                                  fontWeight: FontWeight.w500,
                                  color: theme.colorScheme.onSurface,
                                ),
                              ),
                            ),
                          ),
                        );
                      },
                    ),
                  ),
                  const SizedBox(height: 20),
                  // Input or loading indicator
                  if (state.status == HomeStatus.loading)
                    SizedBox(
                      width: double.infinity,
                      height: 60,
                      child: Center(
                        child: CircularProgressIndicator(
                          color: theme.colorScheme.primary,
                        ),
                      ),
                    )
                  else
                    ListenableBuilder(
                      listenable: _promptController,
                      builder: (context, _) {
                        final isPromptNotEmpty = _promptController.text.trim().isNotEmpty;
                        return TextField(
                          controller: _promptController,
                          style: TextStyle(color: theme.colorScheme.onSurface),
                          decoration: InputDecoration(
                            hintText: "Ask anything...",
                            hintStyle: TextStyle(
                              color: theme.colorScheme.onSurface.withOpacity(0.4),
                            ),
                            filled: true,
                            fillColor: theme.colorScheme.surface,
                            suffixIcon: IconButton(
                              icon: const Icon(Icons.send),
                              onPressed: isPromptNotEmpty
                                  ? () {
                                      final text = _promptController.text;
                                      _promptController.clear();
                                      homeCubit.startConversationFromPrompt(
                                        text,
                                        (conversationId) {
                                          Navigator.push(
                                            context,
                                            MaterialPageRoute(
                                              builder: (context) => ChatScreen(
                                                conversationId: conversationId,
                                              ),
                                            ),
                                          ).then((_) {
                                            // Reload default model on back
                                            homeCubit.loadDefaultModelName();
                                          });
                                        },
                                      );
                                    }
                                  : null,
                              color: isPromptNotEmpty ? theme.colorScheme.primary : Colors.grey,
                            ),
                            enabledBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(18),
                              borderSide: BorderSide(
                                color: theme.colorScheme.onSurface.withOpacity(0.1),
                              ),
                            ),
                            focusedBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(18),
                              borderSide: BorderSide(
                                color: theme.colorScheme.primary,
                              ),
                            ),
                          ),
                        );
                      },
                    ),
                  if (state.status == HomeStatus.error) ...[
                    const SizedBox(height: 8),
                    Text(
                      state.errorMessage,
                      style: const TextStyle(
                        color: Colors.red,
                        fontSize: 13,
                      ),
                    ),
                  ],
                ],
              );
            },
          ),
        ),
      ),
    );
  }
}
