import 'package:flutter/material.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  _SettingsScreenState createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  bool _darkMode = false;
  bool _notifications = true;
  bool _locationTracking = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Settings'),
        elevation: 0,
      ),
      body: ListView(
        padding: const EdgeInsets.all(24),
        children: [
          const Text(
            'General',
            style: TextStyle(
              color: Colors.grey,
              fontSize: 14,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 12),
          Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(16),
            ),
            child: Column(
              children: [
                SwitchListTile(
                  value: _darkMode,
                  onChanged: (val) => setState(() => _darkMode = val),
                  title: const Text(
                    'Dark Mode',
                    style: TextStyle(
                      color: Colors.white,
                    ),
                  ),
                  secondary: const Icon(
                    Icons.dark_mode,
                    color: Colors.purpleAccent,
                  ),
                ),
                Divider(color: Colors.grey[700], height: 1),
                SwitchListTile(
                  value: _notifications,
                  onChanged: (val) => setState(() => _notifications = val),
                  title: const Text(
                    'Notifications',
                    style: TextStyle(
                      color: Colors.white,
                    ),
                  ),
                  secondary: const Icon(
                    Icons.notifications_active,
                    color: Colors.purpleAccent,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 32),
          const Text(
            'Account',
            style: TextStyle(
              color: Colors.grey,
              fontSize: 14,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 12),
          Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(16),
            ),
            child: Column(
              children: [
                ListTile(
                  onTap: () {},
                  leading: const Icon(
                    Icons.lock,
                    color: Colors.purpleAccent,
                  ),
                  title: const Text(
                    'Change Password',
                    style: TextStyle(
                      color: Colors.white,
                    ),
                  ),
                  trailing: const Icon(
                    Icons.arrow_forward_ios,
                    color: Colors.grey,
                  ),
                ),
              ],
            ),
          )
        ],
      ),
    );
  }
}
