'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }

    event.preventDefault();
}

function onConnected() {
    stompClient.subscribe('/user/' + username + '/private', onPrivateMessageReceived); // Nhắn riêng
    stompClient.subscribe('/topic/chat', onMessageReceived); // Nhắn chung
    stompClient.send("/app/chat.addUser", {}, JSON.stringify({ sender: username, type: 'JOIN' }));
    connectingElement.classList.add('hidden');
    console.log("Subscribed to /user/" + username + "/private"); // Debug
}

function onError(error) {
    connectingElement.textContent = 'Không thể kết nối. Vui lòng thử lại!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    var receiverInput = document.querySelector("#receiver").value.trim();

    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };

        if (!receiverInput) {
            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
            console.log("Đã gửi tin nhắn tổng");
        } else {
            chatMessage.receiver = receiverInput;
            stompClient.send("/app/chat.privateMessage", {}, JSON.stringify(chatMessage));
            console.log("Đã gửi tin nhắn riêng tới:", receiverInput);
            // Hiển thị tin nhắn riêng cho người gửi
            displayMessage(chatMessage, true);
        }

        messageInput.value = '';
    } else {
        console.log("Không gửi được: Thiếu nội dung hoặc kết nối STOMP");
    }

    event.preventDefault();
}

function onPrivateMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    console.log("Nhận tin nhắn riêng:", message);
    displayMessage(message, true); // Hiển thị tin nhắn riêng
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    console.log("Nhận tin nhắn:", message);
    displayMessage(message, false); // Hiển thị tin nhắn chung
}

function displayMessage(message, isPrivate) {
    var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        messageElement.textContent = message.sender + ' đã tham gia!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        messageElement.textContent = message.sender + ' đã rời đi!';
    } else {
        messageElement.classList.add('chat-message');
        if (isPrivate) {
            messageElement.classList.add('private-message');
        }

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style.backgroundColor = getAvatarColor(message.sender);
        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var messageHeader = message.sender;
        if (isPrivate) {
            messageHeader += " → " + (message.receiver === username ? "Bạn" : message.receiver) + " (Tin nhắn riêng)";
        }
        usernameElement.textContent = messageHeader;
        messageElement.appendChild(usernameElement);

        var textElement = document.createElement('p');
        textElement.textContent = message.content;
        messageElement.appendChild(textElement);
    }

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);