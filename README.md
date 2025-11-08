# WebSocket Demo - Real-Time Chat Application

A Spring Boot application demonstrating real-time messaging using WebSocket with STOMP protocol. This project showcases how to build a secure, real-time chat system with JWT authentication, PostgreSQL persistence, and both WebSocket and SockJS support.

## Features

- **Real-Time Messaging**: WebSocket-based chat using STOMP protocol
- **JWT Authentication**: Secure WebSocket connections with JWT token validation
- **Message Persistence**: Messages are stored in PostgreSQL database
- **Dual Endpoint Support**: 
  - Plain WebSocket endpoint (`/ws`) for native clients
  - SockJS endpoint (`/ws-sockjs`) for browser clients with fallback support
- **Private Messaging**: User-to-user private message delivery
- **REST API**: HTTP endpoints for retrieving message history
- **Spring Security**: Integrated security configuration

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose** (for PostgreSQL database)
- **PostgreSQL 13** (or use Docker Compose)

## Project Structure

```
websocket-demo-mvn/
├── src/
│   ├── main/
│   │   ├── java/test/websocketdemomvn/
│   │   │   ├── ChatController.java          # WebSocket message handling
│   │   │   ├── WebSocketConfig.java          # WebSocket configuration
│   │   │   ├── WebSocketAuthInterceptor.java # JWT authentication for WebSocket
│   │   │   ├── SecurityConfig.java           # Spring Security configuration
│   │   │   ├── JwtTokenService.java          # JWT token validation
│   │   │   ├── MessageRestController.java   # REST API for messages
│   │   │   ├── MessageRepository.java        # JPA repository
│   │   │   ├── Message.java                  # Message entity
│   │   │   ├── User.java                     # User entity
│   │   │   └── UserService.java              # User service
│   │   └── resources/
│   │       └── application.yaml              # Application configuration
│   └── test/
├── docker-compose.yaml                       # PostgreSQL database setup
└── pom.xml                                   # Maven dependencies
```

## Getting Started

### 1. Start the Database

Start PostgreSQL using Docker Compose:

```bash
docker-compose up -d
```

This will start a PostgreSQL container on port `5555` with:
- Database: `messages`
- Username: `postgres`
- Password: `postgres`

### 2. Configure the Application

The application is configured via `src/main/resources/application.yaml`. The default configuration connects to:
- Database URL: `jdbc:postgresql://localhost:5555/messages`
- Username: `postgres`
- Password: `postgres`

### 3. Build and Run

Using Maven Wrapper (recommended):

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

Or using Maven directly:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080` (default Spring Boot port).

## Usage

### WebSocket Endpoints

The application provides two WebSocket endpoints:

1. **Plain WebSocket**: `ws://localhost:8080/ws`
2. **SockJS WebSocket**: `ws://localhost:8080/ws-sockjs`

### Authentication

WebSocket connections require JWT authentication. The token can be provided in two ways:

1. **Query Parameter**: `ws://localhost:8080/ws?token=YOUR_JWT_TOKEN`
2. **Authorization Header**: `Authorization: Bearer YOUR_JWT_TOKEN`

**Note**: The current `JwtTokenService` implementation is a placeholder that accepts any token. You should implement proper JWT validation logic for production use.

### STOMP Message Flow

#### Sending Messages

**Destination**: `/app/chat.send`

**Message Format**:
```json
{
  "content": "Hello, this is a test message",
  "receiverUserId": 123
}
```

#### Receiving Messages

**Subscribe to**: `/queue/messages`

Messages are delivered to the specific user using Spring's user destination prefix (`/user`).

### Example Client Connection (JavaScript)

```javascript
// Connect to WebSocket
const socket = new SockJS('http://localhost:8080/ws-sockjs?token=YOUR_JWT_TOKEN');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // Subscribe to receive messages
    stompClient.subscribe('/user/queue/messages', function(message) {
        const receivedMessage = JSON.parse(message.body);
        console.log('Received message:', receivedMessage);
    });
    
    // Send a message
    stompClient.send('/app/chat.send', {}, JSON.stringify({
        content: 'Hello from client!',
        receiverUserId: 123
    }));
});
```

### REST API

#### Get Messages

**Endpoint**: `GET /api/messages/{userId}`

**Authentication**: Required (via Spring Security)

**Response**: List of messages for the specified user

**Note**: This endpoint currently returns `null` and needs implementation.

## Configuration

### Application Properties

Key configuration in `application.yaml`:

- **Database**: PostgreSQL connection settings
- **JPA**: Hibernate auto-update DDL enabled
- **Logging**: Debug level for WebSocket and Security components

### WebSocket Configuration

- **Application Destination Prefix**: `/app` (for sending messages)
- **Broker Destination Prefix**: `/queue` (for receiving messages)
- **User Destination Prefix**: `/user` (for private messaging)

## Development Notes

### Current TODOs

The codebase contains several TODO comments indicating areas that need implementation:

1. **JwtTokenService**: Implement proper JWT token validation and username extraction
2. **ChatController**: Complete user lookup logic (currently using placeholder users)
3. **MessageRestController**: Implement message retrieval logic
4. **WebSocketConfig**: Review channel interceptor necessity

### Security Considerations

- CSRF protection is disabled (stateless API)
- Form login and HTTP Basic authentication are disabled
- WebSocket endpoints (`/ws/**`) are permitted without authentication at the HTTP level, but authentication is enforced via `WebSocketAuthInterceptor`

## Technology Stack

- **Spring Boot 3.5.7**: Application framework
- **Spring WebSocket**: WebSocket support
- **Spring Security**: Security framework
- **Spring Data JPA**: Database persistence
- **PostgreSQL**: Relational database
- **Lombok**: Boilerplate code reduction
- **Maven**: Build tool

## Troubleshooting

### Database Connection Issues

- Ensure Docker Compose is running: `docker-compose ps`
- Check if PostgreSQL is accessible on port 5555
- Verify database credentials in `application.yaml`

### WebSocket Connection Issues

- Check browser console for connection errors
- Verify JWT token is being sent correctly
- Check application logs for authentication failures
- Ensure CORS is configured correctly for your client origin

### Port Conflicts

If port 8080 is already in use, you can change it in `application.yaml`:

```yaml
server:
  port: 8081
```

## License

This is a demo project for educational purposes.

