import 'package:flutter_bloc/flutter_bloc.dart';
import '../../core/models/models.dart';
import '../../core/network/network_client.dart';
import '../../core/network/session_manager.dart';

enum HomeStatus { idle, loading, error }

class HomeState {
  final HomeStatus status;
  final String userName;
  final String defaultModelName;
  final String errorMessage;

  HomeState({
    this.status = HomeStatus.idle,
    this.userName = 'User',
    this.defaultModelName = 'Default Model',
    this.errorMessage = '',
  });

  HomeState copyWith({
    HomeStatus? status,
    String? userName,
    String? defaultModelName,
    String? errorMessage,
  }) {
    return HomeState(
      status: status ?? this.status,
      userName: userName ?? this.userName,
      defaultModelName: defaultModelName ?? this.defaultModelName,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }
}

class HomeCubit extends Cubit<HomeState> {
  final SessionManager sessionManager;
  late final NetworkClient networkClient;

  HomeCubit(this.sessionManager)
      : super(HomeState(
          userName: sessionManager.userFirstName ?? 'User',
        )) {
    networkClient = NetworkClient(sessionManager);
  }

  void refreshUserName() {
    emit(state.copyWith(userName: sessionManager.userFirstName ?? 'User'));
  }

  Future<void> loadDefaultModelName() async {
    final defaultModelId = sessionManager.defaultModelId;
    if (defaultModelId != null && defaultModelId.isNotEmpty) {
      final result = await networkClient.get<List<AiModelResponse>>(
        'api/v1/models',
        (json) => (json as List)
            .map((item) => AiModelResponse.fromJson(item))
            .toList(),
      );

      result.fold(
        onSuccess: (models) {
          final matchingIndex = models.indexWhere((m) => m.id == defaultModelId);
          if (matchingIndex != -1) {
            emit(state.copyWith(defaultModelName: models[matchingIndex].name));
          }
        },
        onFailure: (err) {
          // Silent fail or default
        },
      );
    } else {
      emit(state.copyWith(defaultModelName: 'Default Model'));
    }
  }

  Future<void> startConversationFromPrompt(
    String prompt,
    void Function(String conversationId) onNavigateToChat,
  ) async {
    if (prompt.trim().isEmpty) return;

    emit(state.copyWith(status: HomeStatus.loading));

    final title = prompt.length > 25
        ? '${prompt.substring(0, 22)}...'
        : prompt;

    final convRequest = CreateConversationRequest(title: title);
    final convResult = await networkClient.post<Map<String, dynamic>, ConversationResponse>(
      'api/v1/chat/conversations',
      convRequest.toJson(),
      (json) => ConversationResponse.fromJson(json),
    );

    await convResult.fold(
      onSuccess: (conversation) async {
        final defaultModelId = sessionManager.defaultModelId;
        final msgRequest = SendMessageRequest(
          conversationId: conversation.id,
          modelId: defaultModelId,
          content: prompt,
          senderType: 'user',
        );

        final msgResult = await networkClient.post<Map<String, dynamic>, MessageResponse>(
          'api/v1/chat/messages',
          msgRequest.toJson(),
          (json) => MessageResponse.fromJson(json),
        );

        msgResult.fold(
          onSuccess: (message) {
            emit(state.copyWith(status: HomeStatus.idle));
            onNavigateToChat(conversation.id);
          },
          onFailure: (err) {
            emit(state.copyWith(
              status: HomeStatus.error,
              errorMessage: err.toString().replaceAll('Exception: ', ''),
            ));
          },
        );
      },
      onFailure: (err) {
        emit(state.copyWith(
          status: HomeStatus.error,
          errorMessage: err.toString().replaceAll('Exception: ', ''),
        ));
      },
    );
  }
}
