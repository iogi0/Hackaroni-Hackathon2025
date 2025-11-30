document.addEventListener("DOMContentLoaded", function () {
    var fileInput = document.getElementById("fileInput");
    var fileButton = document.getElementById("file-button");
    var fileTag = document.getElementById("file-tag");
    var messages = document.getElementById("messages");
    var messageInput = document.getElementById("message-input");
    var form = document.querySelector(".chat-input");

    if (fileButton && fileInput) {
        fileButton.addEventListener("click", function () {
            fileInput.click();
        });
//1
        fileInput.addEventListener("change", function () {
            if (fileInput.files.length > 0) {
                fileTag.textContent = fileInput.files[0].name;
                fileTag.style.display = "block";
            } else {
                fileTag.style.display = "none";
            }
        });
    }

    form.addEventListener("submit", async function (e) {
        e.preventDefault();
        var text = messageInput.value.trim();
        var file = fileInput.files[0];

        if (!text && !file) return;

        if (text) appendMessage("user", text);
        if (file) appendMessage("user", "[FILE] " + file.name);

        scrollDown();

        let data = new FormData();
        data.append("chatId", window.currentChatId);
        if (text) data.append("message", text);
        if (file) data.append("file", file);

        messageInput.value = "";
        fileInput.value = "";
        fileTag.style.display = "none";

        let r = await fetch("/api/chat/send", { method: "POST", body: data });
        let botMsg = await r.json();

        appendMessage("bot", botMsg.text);
        scrollDown();
    });

    async function loadHistory() {
        let response = await fetch("/api/chat/history?chatId=" + window.currentChatId);
        let history = await response.json();
        messages.innerHTML = "";
        history.forEach(m => {
            appendMessage(m.sender, m.text);
        });
        scrollDown();
    }

    function appendMessage(role, text) {
        var wrapper = document.createElement("div");
        wrapper.className = "message " + role;
        var p = document.createElement("p");
        p.textContent = text;
        wrapper.appendChild(p);
        messages.appendChild(wrapper);
    }

    function scrollDown() {
        messages.scrollTop = messages.scrollHeight;
    }

    loadHistory();
});

const profileBtn = document.getElementById("profile-btn");
const profileDropdown = document.getElementById("profile-dropdown");

if (profileBtn && profileDropdown) {
    profileBtn.addEventListener("click", e => {
        e.stopPropagation();
        profileDropdown.classList.toggle("open");
    });

    document.addEventListener("click", e => {
        if (!profileDropdown.contains(e.target) && !profileBtn.contains(e.target)) {
            profileDropdown.classList.remove("open");
        }
    });
}

function showFileTag() {
    const input = document.getElementById("fileInput");
    const tag = document.getElementById("file-tag");

    if (input.files.length > 0) {
        tag.textContent = input.files[0].name;
        tag.style.display = "block";
    } else {
        tag.style.display = "none";
    }
}
