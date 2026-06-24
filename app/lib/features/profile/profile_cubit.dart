import 'dart:async';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../core/models/models.dart';
import '../../core/network/network_client.dart';
import '../../core/network/session_manager.dart';

enum ProfileStatus { idle, loading, error }

class ProfileState {
  final ProfileStatus status;
  final String userEmail;
  final String userName;
  final String userLastName;
  final List<AiModelResponse> models;
  final String? defaultModelId;
  final bool isDarkTheme;
  final String errorMessage;

  ProfileState({
    this.status = ProfileStatus.idle,
    this.userEmail = '',
    this.userName = '',
    this.userLastName = '',
    this.models = const [],
    this.defaultModelId,
    this.isDarkTheme = true,
    this.errorMessage = '',
  });

  ProfileState copyWith({
    ProfileStatus? status,
    String? userEmail,
    String? userName,
    String? userLastName,
    List<AiModelResponse>? models,
    String? defaultModelId,
    bool? isDarkTheme,
    String? errorMessage,
  }) {
    return ProfileState(
      status: status ?? this.status,
      userEmail: userEmail ?? this.userEmail,
      userName: userName ?? this.userName,
      userLastName: userLastName ?? this.userLastName,
      models: models ?? this.models,
      defaultModelId: defaultModelId ?? this.defaultModelId,
      isDarkTheme: isDarkTheme ?? this.isDarkTheme,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }
}

class ProfileCubit extends Cubit<ProfileState> {
  final SessionManager sessionManager;
  late final NetworkClient networkClient;

  ProfileCubit(this.sessionManager)
      : super(ProfileState(
          userEmail: sessionManager.userEmail ?? '',
          userName: sessionManager.userFirstName ?? '',
          userLastName: sessionManager.userLastName ?? '',
          isDarkTheme: sessionManager.isDarkTheme,
          defaultModelId: sessionManager.defaultModelId,
        )) {
    networkClient = NetworkClient(sessionManager);
  }

  void resetState() {
    emit(state.copyWith(status: ProfileStatus.idle, errorMessage: ''));
  }

  Future<void> loadData() async {
    emit(state.copyWith(status: ProfileStatus.loading));

    // 1. Fetch latest profile info from API
    final profileResult = await networkClient.get<UserProfileResponse>(
      'api/v1/users/profile',
      (json) => UserProfileResponse.fromJson(json),
    );

    await profileResult.fold(
      onSuccess: (profile) async {
        await sessionManager.saveUserProfile(
          firstName: profile.firstName,
          lastName: profile.lastName,
          email: profile.email,
        );
        emit(state.copyWith(
          userEmail: profile.email,
          userName: profile.firstName,
          userLastName: profile.lastName ?? '',
        ));
      },
      onFailure: (err) {
        // Silent fail, keep cached data
      },
    );

    // 2. Fetch models
    final modelsResult = await networkClient.get<List<AiModelResponse>>(
      'api/v1/models',
      (json) => (json as List)
          .map((item) => AiModelResponse.fromJson(item))
          .toList(),
    );

    await modelsResult.fold(
      onSuccess: (modelList) async {
        final settingsResult = await networkClient.get<UserSettingsResponse>(
          'api/v1/users/settings',
          (json) => UserSettingsResponse.fromJson(json),
        );

        await settingsResult.fold(
          onSuccess: (settings) async {
            await sessionManager.saveDefaultModel(settings.defaultModelId);
            emit(state.copyWith(
              status: ProfileStatus.idle,
              models: modelList,
              defaultModelId: settings.defaultModelId,
            ));
          },
          onFailure: (err) {
            emit(state.copyWith(
              status: ProfileStatus.idle,
              models: modelList,
            ));
          },
        );
      },
      onFailure: (err) {
        emit(state.copyWith(
          status: ProfileStatus.error,
          errorMessage: err.toString().replaceAll('Exception: ', ''),
        ));
      },
    );
  }

  Future<void> updateProfile({
    required String firstName,
    String? lastName,
    required void Function(bool success) onComplete,
  }) async {
    if (firstName.trim().isEmpty) {
      emit(state.copyWith(
        status: ProfileStatus.error,
        errorMessage: "First name cannot be empty",
      ));
      onComplete(false);
      return;
    }

    emit(state.copyWith(status: ProfileStatus.loading));

    final request = UpdateProfileRequest(
      firstName: firstName.trim(),
      lastName: (lastName == null || lastName.trim().isEmpty) ? null : lastName.trim(),
    );

    final result = await networkClient.put<Map<String, dynamic>, UserProfileResponse>(
      'api/v1/users/profile',
      request.toJson(),
      (json) => UserProfileResponse.fromJson(json),
    );

    await result.fold(
      onSuccess: (profile) async {
        await sessionManager.saveUserProfile(
          firstName: profile.firstName,
          lastName: profile.lastName,
          email: profile.email,
        );
        emit(state.copyWith(
          status: ProfileStatus.idle,
          userName: profile.firstName,
          userLastName: profile.lastName ?? '',
          userEmail: profile.email,
        ));
        onComplete(true);
      },
      onFailure: (err) {
        emit(state.copyWith(
          status: ProfileStatus.error,
          errorMessage: err.toString().replaceAll('Exception: ', ''),
        ));
        onComplete(false);
      },
    );
  }

  Future<void> toggleTheme(bool isDark) async {
    await sessionManager.saveThemeSetting(isDark);
    emit(state.copyWith(isDarkTheme: isDark));
  }

  Future<void> addModel(String name, String apiKey) async {
    if (name.trim().isEmpty || apiKey.trim().isEmpty) return;

    emit(state.copyWith(status: ProfileStatus.loading));

    final userIdVal = sessionManager.userId ?? '';
    final request = CreateAiModelRequest(
      userId: userIdVal,
      name: name.trim(),
      apiKey: apiKey.trim(),
    );

    final result = await networkClient.post<Map<String, dynamic>, AiModelResponse>(
      'api/v1/models',
      request.toJson(),
      (json) => AiModelResponse.fromJson(json),
    );

    await result.fold(
      onSuccess: (data) async {
        await loadData();
      },
      onFailure: (err) {
        emit(state.copyWith(
          status: ProfileStatus.error,
          errorMessage: err.toString().replaceAll('Exception: ', ''),
        ));
      },
    );
  }

  Future<void> setDefaultModel(String modelId) async {
    emit(state.copyWith(status: ProfileStatus.loading));

    final request = UpdateUserSettingsRequest(defaultModelId: modelId);
    final result = await networkClient.put<Map<String, dynamic>, UserSettingsResponse>(
      'api/v1/users/settings',
      request.toJson(),
      (json) => UserSettingsResponse.fromJson(json),
    );

    await result.fold(
      onSuccess: (settings) async {
        await sessionManager.saveDefaultModel(settings.defaultModelId);
        emit(state.copyWith(
          status: ProfileStatus.idle,
          defaultModelId: settings.defaultModelId,
        ));
      },
      onFailure: (err) {
        emit(state.copyWith(
          status: ProfileStatus.error,
          errorMessage: err.toString().replaceAll('Exception: ', ''),
        ));
      },
    );
  }

  Future<void> logout(void Function() onLogoutComplete) async {
    await sessionManager.clearSession();
    onLogoutComplete();
  }
}
