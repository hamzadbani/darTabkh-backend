# Guide d'utilisation API DarTabkh avec JWT et Swagger

## Vue d'ensemble
Cette API utilise JWT (JSON Web Token) pour l'authentification et Swagger pour la documentation et les tests.

## Architecture
L'API suit une architecture propre avec :
- **DTOs (Data Transfer Objects)** : Tous les endpoints acceptent et retournent uniquement des DTOs
- **Mappers (MapStruct)** : Conversion automatique entre entités JPA et DTOs
- **Validation (Jakarta Validation)** : Validation automatique des DTOs et paramètres
- **Gestion d'erreurs centralisée** : ControllerAdvice avec exceptions personnalisées
- **Séparation des responsabilités** : Controllers → Services → Repositories

## Démarrer l'application
```bash
./mvnw spring-boot:run
```

## Accès à Swagger UI
Une fois l'application démarrée, accédez à la documentation Swagger :
- URL: `http://localhost:8080/swagger-ui.html`

## Endpoints d'authentification

### 1. Inscription (Register)
**POST** `/api/auth/register`

Exemple de requête (rôle par défaut : CLIENT) :
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "Password123"
}
```

**Règles de validation pour l'inscription** :
- **username** : 3-50 caractères, lettres, chiffres, points, tirets et underscores uniquement
- **email** : Format email valide, maximum 100 caractères
- **password** : 6-100 caractères, doit contenir au moins 1 minuscule, 1 majuscule et 1 chiffre

Exemple de requête avec rôle spécifique :
```json
{
  "username": "chef_marie",
  "email": "marie@example.com",
  "password": "Password123",
  "role": "COOKER"
}
```

Réponse :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "CLIENT"
}
```

### 2. Connexion (Login)
**POST** `/api/auth/login`

Exemple de requête :
```json
{
  "email": "john@example.com",
  "password": "Password123"
}
```

**Règles de validation pour le login** :
- **email** : Format email valide et non vide
- **password** : Non vide

Réponse :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "CLIENT"
}
```

## Utilisation du token JWT

### Dans Swagger UI
1. Clicquez sur le bouton "Authorize" en haut à droite de Swagger UI
2. Entrez votre token précédé de "Bearer " : `Bearer your_jwt_token_here`
3. Cliquez sur "Authorize"

### Dans les requêtes cURL
```bash
curl -H "Authorization: Bearer your_jwt_token_here" \
     -X GET http://localhost:8080/api/user/profile
```

### Dans Postman ou autres outils API
Ajoutez dans l'en-tête Authorization :
```
Authorization: Bearer your_jwt_token_here
```

## Endpoints protégés
Tous les endpoints (sauf `/api/auth/**`) nécessitent une authentification JWT.

### Exemples d'endpoints protégés :

#### GET `/api/user/profile`
Retourne le profil de l'utilisateur connecté.

**Réponse** :
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "CLIENT",
  "enabled": true
}
```

#### GET `/api/user/test`
Endpoint de test pour vérifier l'authentification.

**Réponse** :
```json
{
  "message": "Hello john@example.com! This is a protected endpoint."
}
```

#### GET `/api/user/search`
Recherche un utilisateur par email (exemple de validation de paramètre).

**Paramètres** :
- `email` (query parameter) : Email de l'utilisateur à rechercher (format email valide)

**Réponse** :
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "role": "CLIENT",
  "enabled": true
}
```

## Configuration JWT
- **Durée de validité** : 24 heures (86400000 ms)
- **Algorithme** : HMAC SHA-256
- **Clé secrète** : Configurée dans `application.properties`

## Structure des rôles
- `CLIENT` : Client qui commande des repas (rôle par défaut)
- `COOKER` : Cuisinier qui prépare les repas
- `ADMIN` : Administrateur du système

## Gestion des erreurs

L'API utilise un système de gestion d'erreurs personnalisé qui retourne des réponses JSON structurées avec les informations suivantes :

### Format de réponse d'erreur
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Description de l'erreur",
  "path": "/api/auth/register",
  "details": ["Champ spécifique: Message d'erreur"]
}
```

### Codes d'erreur HTTP et leurs causes

#### 400 Bad Request
- **Validation échouée** : Données d'entrée invalides (email mal formaté, mot de passe trop court, etc.)
- **Contraintes violées** : Violation des règles de validation
- **Arguments invalides** : Paramètres de requête incorrects

Exemple de réponse :
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/auth/register",
  "details": [
    "email: Email should be valid",
    "password: Password must be at least 6 characters"
  ]
}
```

#### 401 Unauthorized
- **Token manquant ou invalide** : JWT expiré, malformé ou absent
- **Identifiants incorrects** : Email/mot de passe invalides lors du login
- **Email déjà utilisé** : Tentative d'inscription avec un email existant
- **Nom d'utilisateur déjà utilisé** : Tentative d'inscription avec un nom d'utilisateur existant

Exemple de réponse :
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Authentication Failed",
  "message": "Invalid email or password",
  "path": "/api/auth/login"
}
```

#### 404 Not Found
- **Ressource introuvable** : Utilisateur, repas, ou autre ressource non trouvée

Exemple de réponse :
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "User not found with email: user@example.com",
  "path": "/api/user/profile"
}
```

#### 500 Internal Server Error
- **Erreur serveur inattendue** : Problème technique interne

Exemple de réponse :
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred. Please try again later.",
  "path": "/api/auth/register"
}
```

### Exceptions personnalisées
L'API utilise les exceptions personnalisées suivantes :
- **`AuthenticationException`** : Pour les erreurs d'authentification (401)
- **`ResourceNotFoundException`** : Pour les ressources introuvables (404)
