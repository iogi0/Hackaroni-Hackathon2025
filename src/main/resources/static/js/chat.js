const messages = document.getElementById("messages")
window.currentChatId = 0

loadChatList().then(() => loadHistory())

async function loadChatList() {
    let res = await fetch("/api/chat/all")
    let chats = await res.json()
    let list = document.getElementById("chat-list")
    list.innerHTML = ""
    chats.forEach((chat, i) => {
        list.innerHTML += `<li class="history-item ${i===0?"active":""}" data-id="${i}" onclick="switchChat(${i})"><i class="bx bx-message-rounded"></i> ${chat.title}</li>`
    })
}

async function loadHistory() {
    let response = await fetch("/api/chat/history?chatId=" + window.currentChatId)
    let history = await response.json()
    messages.innerHTML = ""
    history.forEach(m => {
        if (m.sender === "user") addUserMessage(m.text)
        else addBotMessage(m.text)
    })
    scrollDown()
}

document.querySelector(".chat-input").addEventListener("submit", async e => {
    e.preventDefault()

    const textInput = document.querySelector(".chat-input input[name='message']")
    const fileInput = document.getElementById("fileInput")

    let text = textInput.value.trim()
    let file = fileInput.files[0]

    if (!text && !file) return

    if (text) addUserMessage(text)
    if (file) addUserMessage("[FILE] " + file.name)

    scrollDown()

    let form = new FormData()
    form.append("chatId", window.currentChatId)
    if (text) form.append("message", text)
    if (file) form.append("file", file)

    textInput.value = ""
    fileInput.value = ""
    document.getElementById("file-tag").style.display = "none"
    document.body.classList.remove("file-attached")

    let r = await fetch("/api/chat/send", { method: "POST", body: form })
    let botMsg = await r.json()

    addBotMessage(botMsg.text)
    scrollDown()
})

function addUserMessage(txt) {
    messages.innerHTML += `<div class="message user"><p>${txt}</p></div>`
}

function addBotMessage(txt) {
    messages.innerHTML += `<div class="message bot"><p>${txt}</p></div>`
}

function scrollDown() {
    messages.scrollTop = messages.scrollHeight
}

async function switchChat(id) {
    window.currentChatId = id
    await loadHistory()
    document.querySelectorAll(".history-item").forEach(i => i.classList.remove("active"))
    document.querySelector(`.history-item[data-id="${id}"]`).classList.add("active")
}

function showFileTag() {
    const input = document.getElementById("fileInput");
    const tag = document.getElementById("file-tag");

    if (input.files.length > 0) {
        tag.textContent = input.files[0].name;
        tag.style.display = "inline-block";
        document.body.classList.add("file-attached");
    } else {
        tag.style.display = "none";
        document.body.classList.remove("file-attached");
    }
}

async function createNewChat() {
    let r = await fetch("/api/chat/new", { method: "POST" })
    let newId = await r.json()
    await loadChatList()
    switchChat(newId)
}
