package org.example.zitadellogin.core.error

sealed class AppError(
    val messageFa: String,
    val cause: Throwable? = null,
) {
    class Network(messageFa: String, cause: Throwable? = null) : AppError(messageFa, cause)
    class Unauthorized(messageFa: String = "نشست شما منقضی شده یا معتبر نیست.") : AppError(messageFa)
    class BadOAuthState(messageFa: String = "پاسخ احراز هویت نامعتبر است (state).") : AppError(messageFa)
    class MissingAuthCode(messageFa: String = "کد احراز هویت دریافت نشد.") : AppError(messageFa)
    class Server(messageFa: String, cause: Throwable? = null) : AppError(messageFa, cause)
    class Unknown(messageFa: String = "خطای غیرمنتظره رخ داد.", cause: Throwable? = null) : AppError(messageFa, cause)
}
