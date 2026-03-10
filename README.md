# Backend e-commerce Spring Boot

Projet backend prêt à lancer avec:
- Spring Boot 3
- Spring Security
- JWT
- JPA/Hibernate
- H2 en mémoire
- logique métier du projet respectée

## Logique métier implémentée
- un client doit être authentifié pour passer une commande
- la quantité commandée ne peut pas dépasser le stock disponible
- une commande validée impacte immédiatement le stock
- une commande validée est considérée comme définitive
- un produit supprimé ne s'affiche plus dans le catalogue public
- seules les routes admin permettent la gestion des produits, catégories et états des commandes

## Comptes de démonstration
- Admin: `admin@shop.com` / `Admin123!`
- Client: `client@shop.com` / `Client123!`

## Lancer le projet
```bash
mvn spring-boot:run
```

## Construire le jar
```bash
mvn clean package
java -jar target/ecommerce-backend-1.0.0.jar
```

## Endpoints principaux
### Authentification
- `POST /api/auth/register`
- `POST /api/auth/login`

### Catalogue public
- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/categories`

### Client authentifié
- `POST /api/orders`
- `GET /api/orders/me`

### Admin authentifié
- `POST /api/admin/categories`
- `POST /api/admin/products`
- `PUT /api/admin/products/{id}`
- `DELETE /api/admin/products/{id}`
- `GET /api/admin/orders`
- `PATCH /api/admin/orders/{orderId}/status`

## Exemple login
```json
{
  "email": "client@shop.com",
  "password": "Client123!"
}
```

## Exemple création commande
Ajouter le token JWT dans l'en-tête:
`Authorization: Bearer <token>`

```json
{
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 2, "quantity": 1 }
  ]
}
```

## Console H2
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:ecommerce_db`
- User: `sa`
- Password: vide
