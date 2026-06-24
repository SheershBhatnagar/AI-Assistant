import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'chat_cubit.dart';

class ChatScreen extends StatefulWidget {
  final String conversationId;

  const ChatScreen({super.key, required this.conversationId});

  @override
  State<ChatScreen> createState() => _ChatScreenState();
}

class _ChatScreenState extends State<ChatScreen> {
  final TextEditingController _inputController = TextEditingController();
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<ChatCubit>().loadMessages(widget.conversationId);
    });
  }

  @override
  void dispose() {
    _inputController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  void _scrollToBottom() {
    if (_scrollController.hasClients) {
      _scrollController.animateTo(
        _scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeOut,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final chatCubit = context.read<ChatCubit>();

    return Scaffold(
      backgroundColor: theme.colorScheme.background,
      appBar: AppBar(
        title: const Text(
          "AI Chat Session",
          style: TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        backgroundColor: theme.colorScheme.background,
        elevation: 0,
        scrolledUnderElevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.pop(context),
        ),
      ),
      body: SafeArea(
        child: BlocConsumer<ChatCubit, ChatState>(
          listener: (context, state) {
            WidgetsBinding.instance.addPostFrameCallback((_) {
              _scrollToBottom();
            });
          },
          builder: (context, state) {
            return Column(
              children: [
                Expanded(
                  child: Builder(
                    builder: (context) {
                      if (state.status == ChatStatus.loading && state.messages.isEmpty) {
                        return Center(
                          child: CircularProgressIndicator(
                            color: theme.colorScheme.primary,
                          ),
                        );
                      }

                      if (state.status == ChatStatus.error && state.messages.isEmpty) {
                        return Center(
                          child: Padding(
                            padding: const EdgeInsets.all(24.0),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Text(
                                  state.errorMessage,
                                  style: TextStyle(color: theme.colorScheme.error),
                                  textAlign: TextAlign.center,
                                ),
                                const SizedBox(height: 16),
                                ElevatedButton(
                                  onPressed: () => chatCubit.loadMessages(widget.conversationId),
                                  child: const Text("Retry"),
                                ),
                              ],
                            ),
                          ),
                        );
                      }

                      return ListView.builder(
                        controller: _scrollController,
                        padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
                        itemCount: state.messages.length + (state.status == ChatStatus.sending ? 1 : 0),
                        itemBuilder: (context, index) {
                          if (index == state.messages.length) {
                            // AI is thinking bubble
                            return Padding(
                              padding: const EdgeInsets.symmetric(vertical: 8.0),
                              child: Row(
                                children: [
                                  const SizedBox(
                                    width: 16,
                                    height: 16,
                                    child: CircularProgressIndicator(
                                      strokeWidth: 2,
                                      color: Color(0xFF6366F1),
                                    ),
                                  ),
                                  const SizedBox(width: 10),
                                  Text(
                                    "AI is thinking...",
                                    style: TextStyle(
                                      fontSize: 13,
                                      color: theme.colorScheme.secondary,
                                    ),
                                  ),
                                ],
                              ),
                            );
                          }

                          final msg = state.messages[index];
                          final isUser = msg.senderType == 'user';
                          final bubbleColor = isUser ? const Color(0xFF6366F1) : theme.colorScheme.surface;
                          final textColor = isUser ? Colors.white : theme.colorScheme.onSurface;

                          final shape = isUser
                              ? const RoundedRectangleBorder(
                                  borderRadius: BorderRadius.only(
                                    topLeft: Radius.circular(18),
                                    topRight: Radius.circular(18),
                                    bottomLeft: Radius.circular(18),
                                    bottomRight: Radius.circular(4),
                                  ),
                                )
                              : const RoundedRectangleBorder(
                                  borderRadius: BorderRadius.only(
                                    topLeft: Radius.circular(18),
                                    topRight: Radius.circular(18),
                                    bottomLeft: Radius.circular(4),
                                    bottomRight: Radius.circular(18),
                                  ),
                                );

                          return Align(
                            alignment: isUser ? Alignment.centerRight : Alignment.centerLeft,
                            child: Container(
                              constraints: const BoxConstraints(maxWidth: 280),
                              margin: const EdgeInsets.symmetric(vertical: 6.0),
                              child: Material(
                                color: bubbleColor,
                                shape: shape,
                                elevation: isUser ? 1 : 0.5,
                                child: Padding(
                                  padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
                                  child: Text(
                                    msg.content,
                                    style: TextStyle(
                                      color: textColor,
                                      fontSize: 15,
                                    ),
                                  ),
                                ),
                              ),
                            ),
                          );
                        },
                      );
                    },
                  ),
                ),
                // Message input field
                Material(
                  color: theme.colorScheme.surface,
                  elevation: 2,
                  child: Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
                    child: Row(
                      children: [
                        Expanded(
                          child: TextField(
                            controller: _inputController,
                            style: TextStyle(color: theme.colorScheme.onSurface),
                            decoration: InputDecoration(
                              hintText: "Type a message...",
                              hintStyle: TextStyle(
                                color: theme.colorScheme.onSurface.withOpacity(0.4),
                              ),
                              filled: true,
                              fillColor: theme.colorScheme.background,
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(24),
                                borderSide: BorderSide.none,
                              ),
                              contentPadding: const EdgeInsets.symmetric(
                                horizontal: 18,
                                vertical: 10,
                              ),
                            ),
                          ),
                        ),
                        const SizedBox(width: 12),
                        ListenableBuilder(
                          listenable: _inputController,
                          builder: (context, _) {
                            final isInputNotEmpty = _inputController.text.trim().isNotEmpty;
                            return InkWell(
                              onTap: isInputNotEmpty
                                  ? () {
                                      final text = _inputController.text;
                                      _inputController.clear();
                                      chatCubit.sendMessage(widget.conversationId, text);
                                    }
                                  : null,
                              borderRadius: BorderRadius.circular(16),
                              child: Container(
                                width: 48,
                                height: 48,
                                decoration: BoxDecoration(
                                  color: isInputNotEmpty
                                      ? const Color(0xFF6366F1)
                                      : const Color(0xFF6366F1).withOpacity(0.4),
                                  borderRadius: BorderRadius.circular(16),
                                ),
                                alignment: Alignment.center,
                                child: const Icon(
                                  Icons.send,
                                  color: Colors.white,
                                  size: 20,
                                ),
                              ),
                            );
                          },
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            );
          },
        ),
      ),
    );
  }
}
