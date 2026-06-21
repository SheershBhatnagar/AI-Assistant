/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Date: 07/05/26 01:05
 */

package dev.sheershbhatnagar.ai_assistant.infrastructure.external

import jakarta.mail.*
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties

class SmtpEmailClient : EmailClient {

    override suspend fun sendEmail(toAddress: String, subject: String, htmlBody: String): Boolean {
        // ENTERPRISE RULE: Never block the main Ktor thread!
        // We push this network call to the IO thread pool.
        return withContext(Dispatchers.IO) {
            try {
                // In production, these should be loaded from your application.yaml or Docker Env Vars!
                val host = System.getenv("SMTP_HOST") ?: "smtp.gmail.com"
                val port = System.getenv("SMTP_PORT") ?: "587"
                val username = System.getenv("SMTP_USERNAME") ?: "sheershbhatnagar2@gmail.com"
                val password = System.getenv("SMTP_PASSWORD") ?: "upavttxpdwikbtlh"

                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", host)
                    put("mail.smtp.port", port)
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(username, password)
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(username, "AI Assistant Security"))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress))
                    setSubject(subject)
                    // We set the content to HTML so we can style the OTP beautifully
                    setContent(htmlBody, "text/html; charset=utf-8")
                }

                Transport.send(message)
                true // Email sent successfully
            } catch (e: Exception) {
                // In a real app, you would log this error using Ktor's logger or your SystemLogs table
                e.printStackTrace()
                false // Email failed
            }
        }
    }
}
