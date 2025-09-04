import 'package:flutter/material.dart';

class ChatScreen extends StatelessWidget {
  const ChatScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.of(context).size.width;

    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          // Central Icon & Titles
          const Icon(
            Icons.smart_toy,
            size: 64,
            color: Colors.purpleAccent,
          ),
          const SizedBox(height: 16),
          const Text(
            'AI Assistant',
            style: TextStyle(
              fontSize: 36,
              fontWeight: FontWeight.bold,
              color: Colors.white,
            ),
          ),
          const SizedBox(height: 8),
          const Text(
            'Your intelligent companion for coding, writing, learning, and creative tasks.',
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 16,
              color: Colors.grey,
            ),
          ),
          const SizedBox(height: 32),

          // Info Cards
          Column(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              _InfoCard(
                width: width,
                icon: Icons.code,
                title: "Write code",
                subtitle: "Help me debug a Python function",
              ),
              const SizedBox(width: 16),
              _InfoCard(
                width: width,
                icon: Icons.edit,
                title: "Creative writing",
                subtitle: "Write a short story about space exploration",
              ),
              const SizedBox(width: 16),
              _InfoCard(
                width: width,
                icon: Icons.flash_on,
                title: "Quick answer",
                subtitle: "Explain quantum computing in simple terms",
              ),
            ],
          ),
          const Spacer(),

          // Chat input field
          Container(
            decoration: BoxDecoration(
              color: Colors.grey,
              borderRadius: BorderRadius.circular(12),
            ),
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Row(
              children: [
                const Icon(
                  Icons.language,
                  color: Colors.grey,
                ),
                const SizedBox(width: 8),
                const Expanded(
                  child: TextField(
                    decoration: InputDecoration(
                      hintText: "Ask me anything…",
                      border: InputBorder.none,
                      hintStyle: TextStyle(color: Colors.black45),
                    ),
                    style: TextStyle(color: Colors.white),
                  ),
                ),
                IconButton(
                  icon: const Icon(
                    Icons.send,
                    color: Colors.blue,
                  ),
                  onPressed: () {},
                )
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _InfoCard extends StatelessWidget {
  final double width;
  final IconData icon;
  final String title;
  final String subtitle;

  const _InfoCard({
    required this.width,
    required this.icon,
    required this.title,
    required this.subtitle,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      color: Colors.black12,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Container(
        width: width,
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  icon,
                  size: 28,
                  color: Colors.purpleAccent,
                ),
                const SizedBox(width: 10),
                Text(
                  title,
                  style: const TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                    color: Colors.white,
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: 10,
            ),
            Text(
              subtitle,
              style: const TextStyle(
                fontSize: 13,
                color: Colors.grey,
              ),
            )
          ],
        ),
      ),
    );
  }
}
