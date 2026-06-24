import 'package:shared_preferences/shared_preferences.dart';

class SessionManager {
  static const _keyHostAddress = 'host_address';
  static const _keyJwtToken = 'jwt_token';
  static const _keyUserId = 'user_id';
  static const _keyUserEmail = 'user_email';
  static const _keyUserFirstName = 'user_first_name';
  static const _keyUserLastName = 'user_last_name';
  static const _keyDefaultModelId = 'default_model_id';
  static const _keyIsDarkTheme = 'is_dark_theme';

  final SharedPreferences _prefs;

  SessionManager(this._prefs);

  String? get hostAddress => _prefs.getString(_keyHostAddress);
  String? get jwtToken => _prefs.getString(_keyJwtToken);
  String? get userId => _prefs.getString(_keyUserId);
  String? get userEmail => _prefs.getString(_keyUserEmail);
  String? get userFirstName => _prefs.getString(_keyUserFirstName);
  String? get userLastName => _prefs.getString(_keyUserLastName);
  String? get defaultModelId => _prefs.getString(_keyDefaultModelId);
  bool get isDarkTheme => _prefs.getBool(_keyIsDarkTheme) ?? true;

  Future<void> saveSession({
    required String host,
    required String token,
    required String userIdVal,
    required String email,
    required String firstName,
  }) async {
    await _prefs.setString(_keyHostAddress, host);
    await _prefs.setString(_keyJwtToken, token);
    await _prefs.setString(_keyUserId, userIdVal);
    await _prefs.setString(_keyUserEmail, email);
    await _prefs.setString(_keyUserFirstName, firstName);
  }

  Future<void> saveUserProfile({
    required String firstName,
    String? lastName,
    required String email,
  }) async {
    await _prefs.setString(_keyUserFirstName, firstName);
    if (lastName != null) {
      await _prefs.setString(_keyUserLastName, lastName);
    } else {
      await _prefs.remove(_keyUserLastName);
    }
    await _prefs.setString(_keyUserEmail, email);
  }

  Future<void> saveThemeSetting(bool isDark) async {
    await _prefs.setBool(_keyIsDarkTheme, isDark);
  }

  Future<void> saveHostAddress(String host) async {
    await _prefs.setString(_keyHostAddress, host);
  }

  Future<void> saveDefaultModel(String modelId) async {
    await _prefs.setString(_keyDefaultModelId, modelId);
  }

  Future<void> clearSession() async {
    await _prefs.remove(_keyJwtToken);
    await _prefs.remove(_keyUserId);
    await _prefs.remove(_keyUserEmail);
    await _prefs.remove(_keyUserFirstName);
    await _prefs.remove(_keyUserLastName);
    await _prefs.remove(_keyDefaultModelId);
  }
}
