import 'package:flutter_bloc/flutter_bloc.dart';
import '../../core/models/models.dart';
import '../../core/network/network_client.dart';
import '../../core/network/session_manager.dart';

enum HistoryStatus { idle, loading, error }

class HistoryState {
  final HistoryStatus status;
  final List<ConversationResponse> conversations;
  final String errorMessage;

  HistoryState({
    this.status = HistoryStatus.idle,
    this.conversations = const [],
    this.errorMessage = '',
  });

  HistoryState copyWith({
    HistoryStatus? status,
    List<ConversationResponse>? conversations,
    String? errorMessage,
  }) {
    return HistoryState(
      status: status ?? this.status,
      conversations: conversations ?? this.conversations,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }
}

class HistoryCubit extends Cubit<HistoryState> {
  final SessionManager sessionManager;
  late final NetworkClient networkClient;

  HistoryCubit(this.sessionManager) : super(HistoryState()) {
    networkClient = NetworkClient(sessionManager);
  }

  Future<void> loadConversations() async {
    emit(state.copyWith(status: HistoryStatus.loading));

    final result = await networkClient.get<List<ConversationResponse>>(
      'api/v1/chat/conversations',
      (json) => (json as List)
          .map((item) => ConversationResponse.fromJson(item))
          .toList(),
    );

    result.fold(
      onSuccess: (history) {
        emit(state.copyWith(
          status: HistoryStatus.idle,
          conversations: history,
        ));
      },
      onFailure: (err) {
        emit(state.copyWith(
          status: HistoryStatus.error,
          errorMessage: err.toString().replaceAll('Exception: ', ''),
        ));
      },
    );
  }
}
