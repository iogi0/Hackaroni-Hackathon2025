document.addEventListener("DOMContentLoaded", () => {
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

    loadChatCards();
});

async function loadChatCards() {
    let res = await fetch("/api/chat/all");
    let chats = await res.json();

    let container = document.getElementById("chat-cards");
    let template = document.getElementById("chat-card-template");

    container.innerHTML = "";

    chats.forEach((chat, index) => {
        let fragment = template.content.cloneNode(true);
        let card = fragment.querySelector(".card");
        let title = fragment.querySelector(".card-title");
        let meta = fragment.querySelector(".card-meta");
        let date = fragment.querySelector(".card-date");

        title.textContent = chat.title;
        meta.textContent = "Messages: " + (chat.messages ? chat.messages.length : 0);
        date.textContent = "";

        card.addEventListener("click", () => openChat(index));

        container.appendChild(fragment);
    });
}

async function createNewChat() {
    let r = await fetch("/api/chat/new", { method: "POST" });
    let newId = await r.json();
    window.location.href = "/chat/" + newId;
}

function openChat(id) {
    window.location.href = "/chat/" + id;
}
