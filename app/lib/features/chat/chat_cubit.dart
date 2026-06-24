import 'package:flutter_bloc/flutter_bloc.dart';
import '../../core/models/models.dart';
import '../../core/network/network_client.dart';
import '../../core/network/session_manager.dart';

enum ChatStatus { idle, loading, sending, error }

class ChatState {
  final ChatStatus status;
  final List<MessageResponse> messages;
  final String errorMessage;

  ChatState({
    this.status = ChatStatus.idle,
    this.messages = const [],
    this.errorMessage = '',
  });

  ChatState copyWith({
    ChatStatus? status,
    List<MessageResponse>? messages,
    String? errorMessage,
  }) {
    return ChatState(
      status: status ?? this.status,
      messages: messages ?? this.messages,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }
}

class ChatCubit extends Cubit<ChatState> {
  final SessionManager sessionManager;
  late final NetworkClient networkClient;

  ChatCubit(this.sessionManager) : super(ChatState()) {
    networkClient = NetworkClient(sessionManager);
  }

  Future<void> loadMessages(String conversationId) async {
    emit(state.copyWith(status: ChatStatus.loading));

    final result = await networkClient.get<List<MessageResponse>>(
      'api/v1/chat/conversations/$conversationId/messages',
      (json) => (json as List)
          .map((item) => MessageResponse.fromJson(item))
          .toList(),
    );

    result.fold(
      onSuccess: (history) {
        emit(state.copyWith(
          status: ChatStatus.idle,
          messages: history,
        ));
      },
      onFailure: (err) {
        emit(state.copyWith(
          status: ChatStatus.error,
          errorMessage: err.toString().replaceAll('Exception: ', ''),
        ));
      },
    );
  }

  Future<void> sendMessage(String conversationId, String content) async {
    if (content.trim().isEmpty) return;

    // Add temporary message
    final tempMsg = MessageResponse(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      senderType: 'user',
      content: content,
      createdAt: DateTime.now().toIso8601String(),
    );

    emit(state.copyWith(
      status: ChatStatus.sending,
      messages: [...state.messages, tempMsg],
    ));

    final defaultModelId = sessionManager.defaultModelId;
    final request = SendMessageRequest(
      conversationId: conversationId,
      modelId: defaultModelId,
      content: content,
      senderType: 'user',
    );

    final result = await networkClient.post<Map<String, dynamic>, MessageResponse>(
      'api/v1/chat/messages',
      request.toJson(),
      (json) => MessageResponse.fromJson(json),
    );

    await result.fold(
      onSuccess: (data) async {
        await loadMessages(conversationId);
      },
      onFailure: (err) {
        emit(state.copyWith(
          status: ChatStatus.error,
          errorMessage: err.toString().replaceAll('Exception: ', ''),
        ));
      },
    );
  }
}
