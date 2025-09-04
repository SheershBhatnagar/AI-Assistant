import 'package:ai_assistant/screens/settings/settings_screen.dart';
import 'package:ai_assistant/utils/strings.dart';
import 'package:flutter/material.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Column(
          children: [
            const SizedBox(height: 40),

            // Profile avatar
            const CircleAvatar(
              radius: 54,
              backgroundImage: NetworkImage(
                'https://randomuser.me/api/portraits/men/1.jpg', // Replace with user's avatar
              ),
              backgroundColor: Colors.purpleAccent,
            ),
            const SizedBox(height: 18),

            // Name / bio
            const Text(
              'John Doe',
              style: TextStyle(
                color: Colors.white,
                fontSize: 28,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 6),
            const Text(
              'Flutter Developer | UI Enthusiast',
              style: TextStyle(
                color: Colors.grey,
                fontSize: 16,
              ),
            ),
            const SizedBox(height: 6),
            const Text(
              'john.doe@gmail.com',
              style: TextStyle(
                color: Colors.grey,
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 28),

            // Edit profile button
            SizedBox(
              width: 180,
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.purpleAccent,
                  shape: const StadiumBorder(),
                  padding: const EdgeInsets.symmetric(vertical: 14),
                ),
                onPressed: () {},
                icon: const Icon(Icons.edit, color: Colors.white),
                label: const Text(
                  'Edit Profile',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 16,
                  ),
                ),
              ),
            ),
            const SizedBox(height: 32),

            Card(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(20),
              ),
              child: Padding(
                padding: const EdgeInsets.symmetric(vertical: 5),
                child: Column(
                  children: [
                    const ListTile(
                      leading: Icon(
                        Icons.info_rounded,
                      ),
                      title: Text(Strings.about),
                    ),
                    const Divider(),
                    ListTile(
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (context) => const SettingsScreen()),
                        );
                      },
                      leading: const Icon(
                        Icons.settings_rounded,
                      ),
                      title: const Text(Strings.settings),
                    ),
                    const Divider(),
                    const ListTile(
                      leading: Icon(
                        Icons.logout_rounded,
                        color: Colors.red,
                      ),
                      title: Text(
                        Strings.logout,
                        style: TextStyle(
                          color: Colors.red,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}

class ProfileColumn extends StatelessWidget {
  final IconData icon;
  final String label;
  final String info;

  const ProfileColumn({
    super.key,
    required this.icon,
    required this.label,
    required this.info,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Icon(
          icon,
          color: Colors.purpleAccent,
          size: 28,
        ),
        const SizedBox(height: 6),
        Text(
          label,
          style: const TextStyle(
            color: Colors.grey,
            fontSize: 12,
          ),
        ),
        const SizedBox(height: 2),
        Text(
          info,
          style: const TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
            fontSize: 14,
          ),
        ),
      ],
    );
  }
}
