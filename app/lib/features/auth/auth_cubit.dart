import 'dart:async';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../core/models/models.dart';
import '../../core/network/network_client.dart';
import '../../core/network/session_manager.dart';

enum AuthStatus { idle, loading, otpSent, success, error }

class AuthState {
  final AuthStatus status;
  final String hostAddress;
  final String email;
  final String otp;
  final String errorMessage;

  AuthState({
    this.status = AuthStatus.idle,
    this.hostAddress = 'http://127.0.0.1:8080',
    this.email = '',
    this.otp = '',
    this.errorMessage = '',
  });

  AuthState copyWith({
    AuthStatus? status,
    String? hostAddress,
    String? email,
    String? otp,
    String? errorMessage,
  }) {
    return AuthState(
      status: status ?? this.status,
      hostAddress: hostAddress ?? this.hostAddress,
      email: email ?? this.email,
      otp: otp ?? this.otp,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }
}

class AuthCubit extends Cubit<AuthState> {
  final SessionManager sessionManager;
  late final NetworkClient networkClient;

  AuthCubit(this.sessionManager)
      : super(AuthState(
          hostAddress: sessionManager.hostAddress ?? 'http://127.0.0.1:8080',
        )) {
    networkClient = NetworkClient(sessionManager);
  }

  void updateHostAddress(String host) {
    emit(state.copyWith(hostAddress: host));
  }

  void updateEmail(String email) {
    emit(state.copyWith(email: email));
  }

  void updateOtp(String otp) {
    emit(state.copyWith(otp: otp));
  }

  void resetState() {
    emit(state.copyWith(
      status: AuthStatus.idle,
      errorMessage: '',
      otp: '',
    ));
  }

  Future<void> sendOtp() async {
    if (state.email.trim().isEmpty || state.hostAddress.trim().isEmpty) {
      emit(state.copyWith(
        status: AuthStatus.error,
        errorMessage: "Email and Host Address cannot be empty",
      ));
      return;
    }

    emit(state.copyWith(status: AuthStatus.loading));

    await sessionManager.saveHostAddress(state.hostAddress.trim());

    final request = SendOtpRequest(email: state.email.trim());
    final result = await networkClient.post(
      'api/v1/auth/send-otp',
      request.toJson(),
      (json) => json as Map<dynamic, dynamic>,
    );

    result.fold(
      onSuccess: (data) {
        emit(state.copyWith(status: AuthStatus.otpSent));
      },
      onFailure: (err) {
        emit(state.copyWith(
          status: AuthStatus.error,
          errorMessage: err.toString().replaceAll('Exception: ', ''),
        ));
      },
    );
  }

  Future<void> verifyOtp(void Function() onSuccess) async {
    if (state.otp.trim().isEmpty) {
      emit(state.copyWith(
        status: AuthStatus.error,
        errorMessage: "OTP cannot be empty",
      ));
      return;
    }

    emit(state.copyWith(status: AuthStatus.loading));

    final request = VerifyOtpRequest(email: state.email.trim(), otp: state.otp.trim());
    final result = await networkClient.post(
      'api/v1/auth/verify-otp',
      request.toJson(),
      (json) => AuthResponse.fromJson(json),
    );

    await result.fold(
      onSuccess: (authResponse) async {
        await sessionManager.saveSession(
          host: state.hostAddress.trim(),
          token: authResponse.token,
          userIdVal: '',
          email: state.email.trim(),
          firstName: 'User',
        );

        final profileResult = await networkClient.get(
          'api/v1/users/profile',
          (json) => UserProfileResponse.fromJson(json),
        );

        await profileResult.fold(
          onSuccess: (profile) async {
            await sessionManager.saveSession(
              host: state.hostAddress.trim(),
              token: authResponse.token,
              userIdVal: profile.id,
              email: profile.email,
              firstName: profile.firstName,
            );
            if (profile.lastName != null) {
              await sessionManager.saveUserProfile(
                firstName: profile.firstName,
                lastName: profile.lastName,
                email: profile.email,
              );
            }
          },
          onFailure: (err) async {
            await sessionManager.saveSession(
              host: state.hostAddress.trim(),
              token: authResponse.token,
              userIdVal: '00000000-0000-0000-0000-000000000000',
              email: state.email.trim(),
              firstName: 'User',
            );
          },
        );

        emit(state.copyWith(status: AuthStatus.success));
        onSuccess();
      },
      onFailure: (err) {
        emit(state.copyWith(
          status: AuthStatus.error,
          errorMessage: err.toString().replaceAll('Exception: ', ''),
        ));
      },
    );
  }
}
