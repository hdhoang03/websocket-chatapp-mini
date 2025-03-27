const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);
let username = '';

document.getElementById('enter-chat').addEventListener('click', function () {
    const inputUsername = document.getElementById('username').value.trim();
    if (inputUsername) {
        username = inputUsername;
        document.getElementById('username-container').style.display = 'none';
        document.querySelector('.chat-container').style.display = 'flex';
        connectWebSocket();
    }
});

function connectWebSocket() {
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/chat', function (message) {
            const receivedMessage = JSON.parse(message.body);
            showMessage(receivedMessage);
        });

        stompClient.subscribe('/user/queue/message', function (message) {
            const privateMsg = JSON.parse(message.body);
            showPrivateMessage(privateMsg);
        });

        // Gửi thông báo user tham gia chat
        stompClient.send("/app/chat.addUser", {}, JSON.stringify({ sender: username, type: 'JOIN' }));
    });
}

document.getElementById('send').addEventListener('click', sendMessage);
document.getElementById('send-private').addEventListener('click', sendPrivateMessage);

document.getElementById('message').addEventListener('keypress', function (event) {
    if (event.key === 'Enter') {
        sendMessage();
    }
});

document.getElementById('private-message').addEventListener('keypress', function (event) {
    if (event.key === 'Enter') {
        sendPrivateMessage();
    }
});

function sendMessage() {
    const messageInput = document.getElementById('message');
    const messageContent = messageInput.value.trim();

    if (messageContent && username) {
        const message = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));
        messageInput.value = '';
    }
}

function sendPrivateMessage() {
    const privateMessageInput = document.getElementById('private-message');
    const receiverInput = document.getElementById('receiver');
    const messageContent = privateMessageInput.value.trim();
    const receiver = receiverInput.value.trim();

    if (messageContent && receiver && username) {
        const message = {
            sender: username,
            receiver: receiver,
            content: messageContent,
            type: 'PRIVATE'
        };
        stompClient.send("/app/chat.privateMessage", {}, JSON.stringify(message));
        privateMessageInput.value = '';
    }
}

function showMessage(message) {
    const chatMessages = document.getElementById('chat-messages');
    const messageElement = document.createElement('div');
    messageElement.classList.add('message');
    messageElement.textContent = `${message.sender}: ${message.content}`;
    chatMessages.appendChild(messageElement);
    chatMessages.scrollTop = chatMessages.scrollHeight; // Cuộn xuống tin nhắn mới nhất
}

function showPrivateMessage(message) {
    const privateMessages = document.getElementById('private-messages');
    const messageElement = document.createElement('div');
    messageElement.classList.add('private-message');
    messageElement.textContent = `Private from ${message.sender}: ${message.content}`;
    privateMessages.appendChild(messageElement);
    privateMessages.scrollTop = privateMessages.scrollHeight;
}
