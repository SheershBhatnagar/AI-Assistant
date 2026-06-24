import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../core/models/models.dart';
import '../auth/email_screen.dart';
import 'edit_profile_screen.dart';
import 'profile_cubit.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<ProfileCubit>().loadData();
    });
  }

  void _showAddModelDialog(BuildContext context, ProfileCubit cubit, ThemeData theme) {
    final nameController = TextEditingController();
    final keyController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text(
            "Add Model Config",
            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
          ),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                "Note: Backend parses 'gemini' for Google APIs and 'gpt' for OpenAI APIs.",
                style: TextStyle(
                  fontSize: 12,
                  color: theme.colorScheme.secondary,
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: nameController,
                decoration: const InputDecoration(
                  labelText: "Model Name (e.g. gemini-1.5-flash)",
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 12),
              TextField(
                controller: keyController,
                decoration: const InputDecoration(
                  labelText: "API Key",
                  border: OutlineInputBorder(),
                ),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text("Cancel"),
            ),
            ElevatedButton(
              onPressed: () {
                final name = nameController.text.trim();
                final key = keyController.text.trim();
                if (name.isNotEmpty && key.isNotEmpty) {
                  cubit.addModel(name, key);
                  Navigator.pop(context);
                }
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF6366F1),
                foregroundColor: Colors.white,
              ),
              child: const Text("Save"),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final profileCubit = context.read<ProfileCubit>();

    return Scaffold(
      backgroundColor: theme.colorScheme.background,
      appBar: AppBar(
        title: Text(
          "Profile Settings",
          style: TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.w900,
            color: theme.colorScheme.primary,
          ),
        ),
        backgroundColor: theme.colorScheme.background,
        elevation: 0,
        scrolledUnderElevation: 0,
        actions: [
          IconButton(
            icon: Icon(
              Icons.exit_to_app,
              color: theme.colorScheme.error,
            ),
            onPressed: () {
              profileCubit.logout(() {
                // Navigate back to EmailScreen clearing navigation stack
                Navigator.pushAndRemoveUntil(
                  context,
                  MaterialPageRoute(
                    builder: (context) => EmailScreen(
                      onNavigateToOtp: () {
                        Navigator.pushNamed(context, '/otp');
                      },
                    ),
                  ),
                  (route) => false,
                );
              });
            },
          ),
        ],
      ),
      body: BlocBuilder<ProfileCubit, ProfileState>(
        builder: (context, state) {
          final displayName = state.userLastName.isNotEmpty
              ? "${state.userName} ${state.userLastName}"
              : state.userName;

          return Stack(
            children: [
              ListView(
                padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 8.0),
                children: [
                  // User info card
                  Card(
                    color: theme.colorScheme.surface,
                    shape: RoundedCornerShape(24),
                    elevation: 1,
                    margin: EdgeInsets.zero,
                    child: Padding(
                      padding: const EdgeInsets.all(20.0),
                      child: Row(
                        children: [
                          ClipOval(
                            child: Image.asset(
                              'assets/images/ic_avatar_17.png',
                              width: 72,
                              height: 72,
                              fit: BoxFit.cover,
                            ),
                          ),
                          const SizedBox(width: 20),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  displayName.isEmpty ? 'User' : displayName,
                                  style: TextStyle(
                                    fontSize: 20,
                                    fontWeight: FontWeight.bold,
                                    color: theme.colorScheme.primary,
                                  ),
                                ),
                                const SizedBox(height: 4),
                                Text(
                                  state.userEmail,
                                  style: TextStyle(
                                    fontSize: 14,
                                    color: theme.colorScheme.secondary,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          IconButton(
                            icon: Icon(
                              Icons.edit,
                              color: theme.colorScheme.primary,
                            ),
                            onPressed: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (context) => const EditProfileScreen(),
                                ),
                              );
                            },
                          ),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(height: 24),
                  // Preferences header
                  Text(
                    "PREFERENCES",
                    style: TextStyle(
                      fontSize: 11,
                      fontWeight: FontWeight.bold,
                      letterSpacing: 1.0,
                      color: theme.colorScheme.onSurface.withOpacity(0.5),
                    ),
                  ),
                  const SizedBox(height: 10),
                  Card(
                    color: theme.colorScheme.surface,
                    shape: RoundedCornerShape(16),
                    elevation: 1,
                    margin: EdgeInsets.zero,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                "Dark Theme",
                                style: TextStyle(
                                  fontWeight: FontWeight.bold,
                                  color: theme.colorScheme.onSurface,
                                ),
                              ),
                              Text(
                                "Toggle light and dark theme",
                                style: TextStyle(
                                  fontSize: 12,
                                  color: theme.colorScheme.secondary,
                                ),
                              ),
                            ],
                          ),
                          Switch(
                            value: state.isDarkTheme,
                            onChanged: (val) {
                              profileCubit.toggleTheme(val);
                            },
                          ),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(height: 24),
                  // Default chat config header
                  Text(
                    "DEFAULT CHAT CONFIGURATION",
                    style: TextStyle(
                      fontSize: 11,
                      fontWeight: FontWeight.bold,
                      letterSpacing: 1.0,
                      color: theme.colorScheme.onSurface.withOpacity(0.5),
                    ),
                  ),
                  const SizedBox(height: 10),
                  Card(
                    color: theme.colorScheme.surface,
                    shape: RoundedCornerShape(16),
                    elevation: 1,
                    margin: EdgeInsets.zero,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            "Default Model",
                            style: TextStyle(
                              fontSize: 14,
                              color: theme.colorScheme.secondary,
                            ),
                          ),
                          const SizedBox(height: 6),
                          LayoutBuilder(
                            builder: (context, constraints) {
                              final activeModelName = state.models.firstWhere(
                                (m) => m.id == state.defaultModelId,
                                orElse: () => AiModelResponse(id: '', name: 'None Selected', createdAt: ''),
                              ).name;

                              return PopupMenuButton<String>(
                                onSelected: (modelId) {
                                  profileCubit.setDefaultModel(modelId);
                                },
                                itemBuilder: (context) {
                                  if (state.models.isEmpty) {
                                    return [
                                      const PopupMenuItem<String>(
                                        enabled: false,
                                        child: Text("No models configured. Add one below!"),
                                      ),
                                    ];
                                  }
                                  return state.models.map((model) {
                                    return PopupMenuItem<String>(
                                      value: model.id,
                                      child: Text(model.name),
                                    );
                                  }).toList();
                                },
                                child: Container(
                                  width: double.infinity,
                                  decoration: BoxDecoration(
                                    color: theme.colorScheme.background,
                                    borderRadius: BorderRadius.circular(10),
                                  ),
                                  padding: const EdgeInsets.symmetric(horizontal: 14.0, vertical: 12.0),
                                  child: Row(
                                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                    children: [
                                      Text(
                                        activeModelName,
                                        style: TextStyle(
                                          fontWeight: FontWeight.bold,
                                          color: theme.colorScheme.onSurface,
                                        ),
                                      ),
                                      const Icon(Icons.arrow_drop_down),
                                    ],
                                  ),
                                ),
                              );
                            },
                          ),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(height: 24),
                  // Configured models header
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        "CONFIGURED AI MODELS",
                        style: TextStyle(
                          fontSize: 11,
                          fontWeight: FontWeight.bold,
                          letterSpacing: 1.0,
                          color: theme.colorScheme.onSurface.withOpacity(0.5),
                        ),
                      ),
                      IconButton(
                        icon: const Icon(Icons.add),
                        color: const Color(0xFF6366F1),
                        onPressed: () => _showAddModelDialog(context, profileCubit, theme),
                      ),
                    ],
                  ),
                  const SizedBox(height: 10),
                  if (state.models.isEmpty)
                    Padding(
                      padding: const EdgeInsets.symmetric(vertical: 12.0),
                      child: Center(
                        child: Text(
                          "No custom models added. Click '+' to add Gemini or OpenAI.",
                          textAlign: TextAlign.center,
                          style: TextStyle(
                            color: theme.colorScheme.secondary,
                            fontSize: 13,
                          ),
                        ),
                      ),
                    )
                  else
                    ...state.models.map((model) {
                      final isGemini = model.name.toLowerCase().contains("gemini");
                      return Padding(
                        padding: const EdgeInsets.only(bottom: 10.0),
                        child: Card(
                          color: theme.colorScheme.surface,
                          shape: RoundedCornerShape(16),
                          elevation: 1,
                          margin: EdgeInsets.zero,
                          child: Padding(
                            padding: const EdgeInsets.all(16.0),
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      model.name,
                                      style: TextStyle(
                                        fontWeight: FontWeight.bold,
                                        color: theme.colorScheme.onSurface,
                                      ),
                                    ),
                                    Text(
                                      isGemini ? "Provider: Gemini" : "Provider: OpenAI",
                                      style: TextStyle(
                                        fontSize: 12,
                                        color: theme.colorScheme.secondary,
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        ),
                      );
                    }),
                  const SizedBox(height: 24),
                ],
              ),
              if (state.status == ProfileStatus.loading)
                Container(
                  color: Colors.black.withOpacity(0.1),
                  child: const Center(
                    child: CircularProgressIndicator(),
                  ),
                ),
            ],
          );
        },
      ),
    );
  }
}

class RoundedCornerShape extends RoundedRectangleBorder {
  RoundedCornerShape(double radius)
      : super(borderRadius: BorderRadius.circular(radius));
}
