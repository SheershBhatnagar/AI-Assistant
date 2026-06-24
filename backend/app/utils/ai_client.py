import httpx

class AiClient:
    async def call_gemini(self, model_name: str, api_key: str, prompt: str) -> str:
        url = f"https://generativelanguage.googleapis.com/v1beta/models/{model_name}:generateContent"
        headers = {
            "x-goog-api-key": api_key,
            "Content-Type": "application/json"
        }
        body = {
            "contents": [
                {
                    "parts": [
                        {"text": prompt}
                    ]
                }
            ]
        }
        async with httpx.AsyncClient() as client:
            try:
                response = await client.post(url, headers=headers, json=body, timeout=30.0)
                response.raise_for_status()
                data = response.json()
                # candidates[0].content.parts[0].text
                return data["candidates"][0]["content"]["parts"][0]["text"]
            except Exception as e:
                # Log or rethrow
                print(f"Error calling Gemini: {e}")
                if "response" in locals() and response is not None:
                    print(f"Response content: {response.text}")
                return f"Error calling Gemini: {str(e)}"

    async def call_openai(self, model_name: str, api_key: str, prompt: str) -> str:
        url = "https://api.openai.com/v1/chat/completions"
        headers = {
            "Authorization": f"Bearer {api_key}",
            "Content-Type": "application/json"
        }
        body = {
            "model": model_name,
            "messages": [
                {
                    "role": "user",
                    "content": prompt
                }
            ]
        }
        async with httpx.AsyncClient() as client:
            try:
                response = await client.post(url, headers=headers, json=body, timeout=30.0)
                response.raise_for_status()
                data = response.json()
                # choices[0].message.content
                return data["choices"][0]["message"]["content"]
            except Exception as e:
                # Log or rethrow
                print(f"Error calling OpenAI: {e}")
                if "response" in locals() and response is not None:
                    print(f"Response content: {response.text}")
                return f"Error calling OpenAI: {str(e)}"
