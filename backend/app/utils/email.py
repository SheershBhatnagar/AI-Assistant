import os
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

def send_email(to_address: str, subject: str, html_body: str) -> bool:
    smtp_host = os.getenv("SMTP_HOST", "smtp.gmail.com")
    smtp_port = int(os.getenv("SMTP_PORT", "587"))
    smtp_username = os.getenv("SMTP_USERNAME", "sheershbhatnagar2@gmail.com")
    smtp_password = os.getenv("SMTP_PASSWORD", "upavttxpdwikbtlh")

    try:
        # Create message container
        msg = MIMEMultipart('alternative')
        msg['Subject'] = subject
        msg['From'] = f"AI Assistant Security <{smtp_username}>"
        msg['To'] = to_address

        # Record the MIME types of both parts - text/html.
        part = MIMEText(html_body, 'html', 'utf-8')
        msg.attach(part)

        # Connect to server
        server = smtplib.SMTP(smtp_host, smtp_port, timeout=10.0)
        server.ehlo()
        server.starttls()  # Secure the connection
        server.ehlo()
        server.login(smtp_username, smtp_password)
        server.sendmail(smtp_username, to_address, msg.as_string())
        server.quit()
        return True
    except Exception as e:
        print(f"SMTP error sending email to {to_address}: {e}")
        return False
