import 'package:flutter/material.dart';

class HistoryScreen extends StatefulWidget {
  const HistoryScreen({super.key});

  @override
  State<HistoryScreen> createState() => _HistoryScreenState();
}

class _HistoryScreenState extends State<HistoryScreen> {
  final historyData = [
    {
      'name': 'AI Assistant',
      'lastMsg': 'Sure, I can help you debug!',
      'timestamp': '12:45 AM',
    },
    {
      'name': 'Creative Writing',
      'lastMsg': 'The stars shimmered in the void...',
      'timestamp': '10:30 PM',
    },
    {
      'name': 'Quick Answer',
      'lastMsg': 'Quantum computing uses qubits...',
      'timestamp': 'Yesterday',
    },
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: ListView.separated(
        padding: const EdgeInsets.all(24),
        separatorBuilder: (_, __) => const Divider(color: Colors.grey),
        itemCount: historyData.length,
        itemBuilder: (context, index) {
          final item = historyData[index];
          return Dismissible(
            key: Key(item['name']!),
            direction: DismissDirection.endToStart,
            // swipe left to right also possible
            background: Container(
              alignment: Alignment.centerRight,
              color: Colors.red,
              padding: const EdgeInsets.symmetric(horizontal: 24),
              child: const Icon(
                Icons.delete,
                color: Colors.white,
              ),
            ),
            onDismissed: (direction) {
              setState(() {
                historyData.removeAt(index);
              });
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text('${item['name']} deleted'),
                  behavior: SnackBarBehavior.floating,
                ),
              );
            },
            child: ListTile(
              onTap: () {
                // Navigate to conversation details
              },
              title: Text(
                item['name'] as String,
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                ),
              ),
              subtitle: Text(
                item['lastMsg'] as String,
                style: const TextStyle(
                  color: Colors.grey,
                ),
              ),
              trailing: Text(
                item['timestamp'] as String,
                style: const TextStyle(
                  color: Colors.grey,
                  fontSize: 12,
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}
