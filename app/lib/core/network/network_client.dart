import 'dart:async';
import 'package:dio/dio.dart';
import '../models/models.dart';
import 'session_manager.dart';

class Result<T> {
  final T? value;
  final Exception? error;

  Result.success(this.value) : error = null;
  Result.failure(this.error) : value = null;

  bool get isSuccess => error == null;
  bool get isFailure => error != null;

  Future<void> fold({
    required FutureOr<void> Function(T data) onSuccess,
    required FutureOr<void> Function(Exception error) onFailure,
  }) async {
    if (isSuccess) {
      await onSuccess(value as T);
    } else {
      await onFailure(error!);
    }
  }
}

class NetworkClient {
  final SessionManager sessionManager;
  late final Dio dio;

  NetworkClient(this.sessionManager) {
    dio = Dio(BaseOptions(
      connectTimeout: const Duration(seconds: 15),
      receiveTimeout: const Duration(seconds: 15),
    ));

    // Dynamic Base URL and Authorization Header Interceptor
    dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) {
        options.baseUrl = getBaseUrl();
        final token = sessionManager.jwtToken;
        if (token != null && token.isNotEmpty) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        options.headers['Content-Type'] = 'application/json';
        return handler.next(options);
      },
    ));
  }

  String getBaseUrl() {
    final savedHost = sessionManager.hostAddress;
    final host = (savedHost == null || savedHost.trim().isEmpty)
        ? "http://127.0.0.1:8080"
        : savedHost.trim();
    return host.endsWith("/") ? host : "$host/";
  }

  Future<dynamic> _handleResponse(Response response) async {
    final statusCode = response.statusCode ?? 0;
    if (statusCode >= 200 && statusCode < 300) {
      return response.data;
    } else {
      final errorData = response.data;
      String message;
      try {
        final errorObj = ErrorResponse.fromJson(errorData);
        message = errorObj.message;
      } catch (e) {
        message = "HTTP $statusCode: ${response.statusMessage ?? ''}";
      }
      throw Exception(message);
    }
  }

  Future<Result<R>> get<R>(
    String path,
    R Function(dynamic json) parser,
  ) async {
    try {
      final response = await dio.get(path);
      final dynamic data = await _handleResponse(response);
      return Result.success(parser(data));
    } on DioException catch (e) {
      String message = e.message ?? e.toString();
      if (e.response != null) {
        try {
          final errorObj = ErrorResponse.fromJson(e.response!.data);
          message = errorObj.message;
        } catch (_) {
          message = "HTTP ${e.response!.statusCode}: ${e.response!.statusMessage ?? ''}";
        }
      }
      return Result.failure(Exception(message));
    } on Exception catch (e) {
      return Result.failure(e);
    } catch (e) {
      return Result.failure(Exception(e.toString()));
    }
  }

  Future<Result<R>> post<T, R>(
    String path,
    T body,
    R Function(dynamic json) parser,
  ) async {
    try {
      final response = await dio.post(path, data: body);
      final dynamic data = await _handleResponse(response);
      return Result.success(parser(data));
    } on DioException catch (e) {
      String message = e.message ?? e.toString();
      if (e.response != null) {
        try {
          final errorObj = ErrorResponse.fromJson(e.response!.data);
          message = errorObj.message;
        } catch (_) {
          message = "HTTP ${e.response!.statusCode}: ${e.response!.statusMessage ?? ''}";
        }
      }
      return Result.failure(Exception(message));
    } on Exception catch (e) {
      return Result.failure(e);
    } catch (e) {
      return Result.failure(Exception(e.toString()));
    }
  }

  Future<Result<R>> put<T, R>(
    String path,
    T body,
    R Function(dynamic json) parser,
  ) async {
    try {
      final response = await dio.put(path, data: body);
      final dynamic data = await _handleResponse(response);
      return Result.success(parser(data));
    } on DioException catch (e) {
      String message = e.message ?? e.toString();
      if (e.response != null) {
        try {
          final errorObj = ErrorResponse.fromJson(e.response!.data);
          message = errorObj.message;
        } catch (_) {
          message = "HTTP ${e.response!.statusCode}: ${e.response!.statusMessage ?? ''}";
        }
      }
      return Result.failure(Exception(message));
    } on Exception catch (e) {
      return Result.failure(e);
    } catch (e) {
      return Result.failure(Exception(e.toString()));
    }
  }
}
