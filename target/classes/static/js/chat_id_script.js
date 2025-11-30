document.addEventListener("DOMContentLoaded", () => {
    const messages = document.getElementById("messages")
    const fileInput = document.getElementById("fileInput")
    const fileButton = document.getElementById("file-button")
    const fileTag = document.getElementById("file-tag")
    const messageInput = document.getElementById("message-input")
    const form = document.querySelector(".chat-input")

    if (fileButton && fileInput) {
        fileButton.addEventListener("click", () => fileInput.click())
        fileInput.addEventListener("change", showFileTag)
    }

    form.addEventListener("submit", async e => {
        e.preventDefault()

        let text = messageInput.value.trim()
        let file = fileInput.files[0]

        if (!text && !file) return

        appendMessage(messages, "user", text || ("[FILE] " + file.name))
        scrollDown(messages)

        let formData = new FormData()
        formData.append("chatId", window.currentChatId)

        if (text) formData.append("message", text)
        if (file) formData.append("file", file)

        messageInput.value = ""
        fileInput.value = ""
        fileTag.style.display = "none"

        let response = await fetch("/api/chat/send", {
            method: "POST",
            body: formData
        })

        let botReply = await response.json()
        let raw = botReply.text || botReply.answer || botReply.intent || "Empty response"

        appendMessage(messages, "bot", raw)
        scrollDown(messages)
    })

    async function loadHistory() {
        let response = await fetch("/api/chat/history?chatId=" + window.currentChatId)
        let history = await response.json()

        messages.innerHTML = ""

        history.forEach(m => appendMessage(messages, m.sender, m.text))

        scrollDown(messages)
    }

    loadHistory()
})

function appendMessage(container, role, text) {
    const wrapper = document.createElement("div");
    wrapper.className = "message " + role;

    const bubble = document.createElement("div");
    bubble.className = "msg-content";

    if (role === "bot" && window.marked && window.DOMPurify) {
        let html = marked.parse(text);
        bubble.innerHTML = DOMPurify.sanitize(html);

        if (window.MathJax && MathJax.typesetPromise) {
            MathJax.typesetPromise();
        }
    } else {
        bubble.textContent = text;
    }

    wrapper.appendChild(bubble);
    container.appendChild(wrapper);
}


function scrollDown(container) {
    container.scrollTop = container.scrollHeight
}

function showFileTag() {
    const input = document.getElementById("fileInput")
    const tag = document.getElementById("file-tag")

    if (input.files.length > 0) {
        tag.textContent = input.files[0].name
        tag.style.display = "block"
    } else {
        tag.style.display = "none"
    }
}
