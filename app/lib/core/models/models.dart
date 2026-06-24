class SendOtpRequest {
  final String email;

  SendOtpRequest({required this.email});

  Map<String, dynamic> toJson() => {'email': email};
}

class VerifyOtpRequest {
  final String email;
  final String otp;

  VerifyOtpRequest({required this.email, required this.otp});

  Map<String, dynamic> toJson() => {'email': email, 'otp': otp};
}

class AuthResponse {
  final String token;
  final String message;

  AuthResponse({required this.token, required this.message});

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      token: json['token'] ?? '',
      message: json['message'] ?? '',
    );
  }
}

class RegisterUserRequest {
  final String email;
  final String firstName;
  final String? middleName;
  final String? lastName;

  RegisterUserRequest({
    required this.email,
    required this.firstName,
    this.middleName,
    this.lastName,
  });

  Map<String, dynamic> toJson() => {
        'email': email,
        'firstName': firstName,
        'middleName': middleName,
        'lastName': lastName,
      };
}

class UserResponse {
  final String id;
  final String email;
  final String firstName;
  final String? lastName;
  final String token;

  UserResponse({
    required this.id,
    required this.email,
    required this.firstName,
    this.lastName,
    required this.token,
  });

  factory UserResponse.fromJson(Map<String, dynamic> json) {
    return UserResponse(
      id: json['id'] ?? '',
      email: json['email'] ?? '',
      firstName: json['firstName'] ?? '',
      lastName: json['lastName'],
      token: json['token'] ?? '',
    );
  }
}

class UserProfileResponse {
  final String id;
  final String email;
  final String firstName;
  final String? lastName;

  UserProfileResponse({
    required this.id,
    required this.email,
    required this.firstName,
    this.lastName,
  });

  factory UserProfileResponse.fromJson(Map<String, dynamic> json) {
    return UserProfileResponse(
      id: json['id'] ?? '',
      email: json['email'] ?? '',
      firstName: json['firstName'] ?? '',
      lastName: json['lastName'],
    );
  }
}

class UpdateProfileRequest {
  final String firstName;
  final String? lastName;

  UpdateProfileRequest({required this.firstName, this.lastName});

  Map<String, dynamic> toJson() => {
        'firstName': firstName,
        'lastName': lastName,
      };
}

class ErrorResponse {
  final int status;
  final String error;
  final String message;

  ErrorResponse({
    required this.status,
    required this.error,
    required this.message,
  });

  factory ErrorResponse.fromJson(Map<String, dynamic> json) {
    return ErrorResponse(
      status: json['status'] ?? 0,
      error: json['error'] ?? '',
      message: json['message'] ?? '',
    );
  }
}

class CreateConversationRequest {
  final String title;

  CreateConversationRequest({required this.title});

  Map<String, dynamic> toJson() => {'title': title};
}

class ConversationResponse {
  final String id;
  final String? title;
  final String createdAt;

  ConversationResponse({
    required this.id,
    this.title,
    required this.createdAt,
  });

  factory ConversationResponse.fromJson(Map<String, dynamic> json) {
    return ConversationResponse(
      id: json['id'] ?? '',
      title: json['title'],
      createdAt: json['createdAt'] ?? '',
    );
  }
}

class SendMessageRequest {
  final String conversationId;
  final String? modelId;
  final String content;
  final String senderType;

  SendMessageRequest({
    required this.conversationId,
    this.modelId,
    required this.content,
    required this.senderType,
  });

  Map<String, dynamic> toJson() => {
        'conversationId': conversationId,
        if (modelId != null) 'modelId': modelId,
        'content': content,
        'senderType': senderType,
      };
}

class MessageResponse {
  final String id;
  final String senderType;
  final String content;
  final String createdAt;

  MessageResponse({
    required this.id,
    required this.senderType,
    required this.content,
    required this.createdAt,
  });

  factory MessageResponse.fromJson(Map<String, dynamic> json) {
    return MessageResponse(
      id: json['id'] ?? '',
      senderType: json['senderType'] ?? 'user',
      content: json['content'] ?? '',
      createdAt: json['createdAt'] ?? '',
    );
  }
}

class CreateAiModelRequest {
  final String userId;
  final String name;
  final String apiKey;

  CreateAiModelRequest({
    required this.userId,
    required this.name,
    required this.apiKey,
  });

  Map<String, dynamic> toJson() => {
        'userId': userId,
        'name': name,
        'apiKey': apiKey,
      };
}

class AiModelResponse {
  final String id;
  final String name;
  final String createdAt;

  AiModelResponse({
    required this.id,
    required this.name,
    required this.createdAt,
  });

  factory AiModelResponse.fromJson(Map<String, dynamic> json) {
    return AiModelResponse(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      createdAt: json['createdAt'] ?? '',
    );
  }
}

class UpdateUserSettingsRequest {
  final String defaultModelId;

  UpdateUserSettingsRequest({required this.defaultModelId});

  Map<String, dynamic> toJson() => {'defaultModelId': defaultModelId};
}

class UserSettingsResponse {
  final String id;
  final String userId;
  final String defaultModelId;
  final String createdAt;
  final String updatedAt;

  UserSettingsResponse({
    required this.id,
    required this.userId,
    required this.defaultModelId,
    required this.createdAt,
    required this.updatedAt,
  });

  factory UserSettingsResponse.fromJson(Map<String, dynamic> json) {
    return UserSettingsResponse(
      id: json['id'] ?? '',
      userId: json['userId'] ?? '',
      defaultModelId: json['defaultModelId'] ?? '',
      createdAt: json['createdAt'] ?? '',
      updatedAt: json['updatedAt'] ?? '',
    );
  }
}
