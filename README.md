# Diary

## Table of contents
1. [Introduction](#introduction)
2. [What is a token?](#token_definition)
    - [Json Web Token (JWT)](#jwt_information)
    - [JWT Claims](#jwt_claims)
    - [JWT Signature](#jwt_signature)
3. [JWT Service](#jwt_service)
    - [Token Generation](#token_generation)
    - [Token Validation and Verification](#token_control)
    - [Token Revocation and Management](#token_management)
    - [Token Integration](#token_integration)
4. [JWT Authentication Filter](#jwt_filter)
    - [Authorization Header](#auth_header)
    - [Security Context](#security_context)
    

## Introduction <a name="introduction"></a>
Welcome to the diary spring boot project. This project was created to learn how to set up spring
security in the application. Here we learn about the filter chain, tokens, JWT and more.

## What is a token? <a name="token_definition"></a>
In information technology, a ***token refers to a piece of data or authentication code that represents 
a user's identity or access rights***. It can be used for various purposes like authentication, 
authorization, or secure communication between systems. 

### Json Web Token (JWT) <a name="jwt_information"></a>
**JWT stands for JSON Web Token**. It is a compact, self-contained means of transmitting information 
between parties as a JSON object. JWTs are used for securely transmitting information between a 
client and a server.

They consist of three parts: 
- a **header** specifying the type of token and the hashing algorithm used, 
- a **payload** containing the actual information (***claims***) being transmitted,
- a **signature** that ensures the integrity of the token.

JWTs are commonly used for authentication and authorization in web applications. Once a user is 
authenticated, a JWT is often generated and sent back to the client, which is then included in 
subsequent requests to access protected routes or resources on the server. They are portable, 
stateless, and can securely carry information, reducing the need for the server to store session state.

### JWT Claims <a name="jwt_claims"></a>
Token claims refer to the pieces of information or assertions contained within a token's payload. These 
***claims provide details about the entity (user, system, etc.) associated with the token or specify access 
rights, metadata, or any other relevant information***.

There are three types of claims in a JSON Web Token (JWT):
- **Reserved Claims** - These are predefined claims standardized by the JWT specification. 
Some common reserved claims include ***iss*** (issuer), ***sub*** (subject), ***exp*** (expiration time), 
***iat*** (issued at), and ***nbf*** (not before).
- **Public Claims** - These are custom claims defined by the token issuer and are meant to convey additional 
information relevant to the application or system using the token. They are not standardized and can vary 
depending on the specific use case.
- **Private Claims** - These are custom claims agreed upon between parties that share the token. They are not 
registered or public and are used for specific purposes within the context of those parties' applications or 
systems.

Claims provide context and information about the token, allowing the recipient to make decisions regarding 
access, permissions, or other actions based on the data within the token payload.

### JWT Signature <a name="jwt_signature"></a>
Token signatures are generated using cryptographic algorithms that ensure the integrity and authenticity 
of the token. 

The process typically involves the following steps:
1. **Header and Payload Creation** - The token consists of a header (containing information about the 
token type and the hashing algorithm used) and a payload (containing the actual data or claims).
2. **Encoding** - Both the header and payload are encoded using a specified algorithm such as Base64.
3. **Concatenation** - The encoded header and payload are concatenated together, usually separated by a 
dot (.) to form the message that will be signed.
4. **Signing the Message** - The concatenated message is signed using a cryptographic algorithm (e.g., HMAC, RSA) 
and a secret key (in the case of symmetric algorithms) or a private key (in the case of asymmetric algorithms).
5. **Generation of Signature** - The signature is produced by applying the chosen algorithm to the concatenated 
message and the secret/private key. This process generates a unique string of characters that serves as the 
token's signature.
6. **Appending the Signature** - The generated signature is then appended to the token, creating a complete and signed token.

Upon receiving the token, the recipient can verify its authenticity by extracting the header and payload, re-signing the 
concatenated message with the appropriate key, and comparing the generated signature with the received signature. If they 
match, it confirms the token's integrity and authenticity.

## JWT Service <a name="jwt_service"></a>
A JWT service is a software component or system that specializes in handling the generation, validation, and management 
of JWT tokens. Here's a breakdown of its primary functionalities:

### **Token Generation:** <a name="token_generation"></a>
1. The JWT service generates JWT tokens by encoding user-defined claims or information into a JSON-based payload.
2. It creates a unique signature by applying a secure hashing algorithm (such as HMAC or RSA) using a secret key.
3. The service constructs the token by combining the header, payload, and signature, producing a string of characters.
  
```
public static String generateToken(HashMap<String, Object> extraClaims, Key key, UserDetails user) {

    return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(user.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + TOKEN_LIFESPAN))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
}
```
  
### **Token Validation and Verification:** <a name="token_control"></a>
1. Upon receiving a JWT from a client, the service verifies its authenticity and integrity.
  
```
public static boolean isTokenValid(String token, Key key, UserDetails user) {
    String username = JsonWebTokenClaims.extractUsername(token, key);

    return (username.equals(user.getUsername()) && !isTokenExpired(token, key));
}   
```
  
2. It decodes the received token to extract the payload and header information.
  
```
public static String extractUsername(String token, Key key) {
    Claims claims = extractAllClaims(token, key);

    return claims.getSubject();
}
```   

```
public static Claims extractAllClaims(String token, Key key) {

    return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
}
   ```

3. The service checks the expiration (if an expiration time is included in the payload) and other claims to ensure 
the token's validity.
  
```
public static boolean isTokenExpired(String token, Key key) {

    return JsonWebTokenClaims.extractExpirationDate(token, key).before(new Date());
}
```

### **Token Revocation and Management (optional):** <a name="token_management"></a>
1. In some cases, JWT services might provide mechanisms to revoke or invalidate tokens before their natural expiration. 
This might involve maintaining a blacklist or using token revocation lists (CRLs) or token introspection services.

### **Integration with Authentication and Authorization:** <a name="token_integration"></a>
1. JWT services are often integrated into authentication and authorization systems, allowing users to securely access 
resources by presenting their valid tokens.
2. They provide a way to enforce access controls by validating the claims embedded within the tokens.

Overall, a JWT service is crucial for implementing secure authentication and authorization mechanisms in web applications 
and APIs. By generating, validating, and managing JWT tokens, this service ensures the integrity and authenticity of 
transmitted information, thereby enhancing the overall security of the system.

## JWT Authentication Filter <a name="jwt_filter"></a>
The JSON Web Token Authentication Filter is a part of a validation and verification process. This process is run by a 
filter chain, which is a sequence of connected filters. These filters check the client's request and decide what to do 
with it.

### Authorization Header <a name="auth_header"></a>
The token is stored in the request's header called **Authorization**. The header's structure is a string "***Bearer*** **TOKEN**".
The token is an encoded string using a hashing algorithm. 

```
private static final String PREFIX = "Bearer ";
...

@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");

    //Validate request form.
    if (authHeader != null && authHeader.startsWith(PREFIX)) {

        //Extract important data from raw request.
        String token = authHeader.substring(PREFIX.length());
        Optional<String> username = this.jwtService.fetchUsername(token);

        ...             
    }
    
    //Run next filter.
    filterChain.doFilter(request, response);
}
```

### Security Context <a name="security_context"></a>
The security context stores data about the authentication status of a current request. It updates authentications
all the time.

```
...
private final JwtService jwtService;
private final UserService userService;

@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    ...
    //Check user authentication status.
    if (username.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails user = this.userService.loadUserByUsername(username.get());

        //Check user token.
        if (this.jwtService.isTokenValid(token, user)) {

            //Create authentication for current request.
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //Update security context.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
    ...
}
```
