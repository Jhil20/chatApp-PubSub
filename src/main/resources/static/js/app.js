let username = '';

// Show the username input screen
function showUsernameScreen() {
    const usernameScreen = document.getElementById('usernameScreen');
    const chatScreen = document.getElementById('chatScreen');
    const startChatBtn = document.getElementById('startChatBtn');
    const usernameInput = document.getElementById('usernameInput');

    startChatBtn.addEventListener('click', function () {
        username = usernameInput.value.trim();
        if (username !== '') {
            // Hide username input screen and show chat screen
            usernameScreen.style.display = 'none';
            chatScreen.style.display = 'block';
            document.getElementById('usernameDisplay').textContent = `Hello, ${username}`;
            startChat();
        } else {
            alert("Please enter a valid username");
        }
    });
}

// Start the chat functionality
function startChat() {
    // Fetch initial messages
    // receiveMessages();

    // Event listener for sending a message when the user clicks the Send button
    document.getElementById('sendBtn').addEventListener('click', function () {
        const message = document.getElementById('message').value.trim();
        if (message !== '') {
            sendMessage(message);
            document.getElementById('message').value = '';  // Clear the input field
        }
    });

    // Poll the server every 3 seconds to get new messages
    document.getElementById('receiveBtn').addEventListener('click', function () {
        receiveMessages();
    });
}

// API base URL (replace with your actual API URL)
const apiUrlSend = '/publisher/publishMessage';

// Function to send message to the server
function sendMessage(message) {
    fetch(apiUrlSend, {
        method: 'POST',
        body: message,
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return "success" // Ensure the server returns a JSON response
        })
        .then(data => {
            console.log('Message sent:', data);
        })
        .catch(error => {
            console.error('Error sending message:', error);
        });
}


const apiUrlReceive = '/subscriber/subscribeMessage';

// Function to receive messages from the server
function receiveMessages() {

    fetch(apiUrlReceive)
        .then(response => response.json())
        .then(data => {
            console.log('Received Data:', data);

            const chatBox = document.getElementById('chatBox');
            chatBox.innerHTML = ''; // Clear chat box

            if (data && data.message) {
                const messageElement = document.createElement('div');
                messageElement.classList.add('message');
                messageElement.innerText = data.message;
                chatBox.appendChild(messageElement);
            } else {
                console.log("No message received or incorrect data structure.");
            }

            // Scroll to bottom
            chatBox.scrollTop = chatBox.scrollHeight;
        })
        .catch(error => {
            console.error('Error:', error);
        });
}
// Initial setup
showUsernameScreen();
