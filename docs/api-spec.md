## Event API (Read Only)

### GET /events/{id}

**Response 200**

```json
{
  "id": 1,
  "name": "EVENT NAME",
  "startAt": "2026-03-21T10:00:00+09:00",
  "endAt": "2026-03-21T18:00:00+09:00",
  "totalQuantity": 200,
  "remainingQuantity": 120,
  "status": "ACTIVE"
}
```

`status`: `ACTIVE` | `SOLD_OUT`

**Status Codes**

* 200 OK
* 404 Not Found

---

## Order API

### POST /orders

**Request**

```json
{
  "eventId": 1,
  "quantity": 1
}
```

**Response 201**

```json
{
  "id": 1001,
  "buyerToken": "550e8400-e29b-41d4-a716-446655440000",
  "expiresAt": "2026-03-21T12:10:00Z",
  "status": "CREATED"
}
```

`status`: `CREATED` | `PAID` | `CANCELED` | `EXPIRED`

**Status Codes**

* 201 Created → Success
* 409 Conflict → SOLD_OUT
* 404 Not Found → Event missing

---

### GET /orders/{id}

**Response 200**

```json
{
  "orderId": 1001,
  "status": "PAID"
}
```

**Status Codes**

* 200 OK
* 404 Not Found
* 410 Gone (optional expired)

---

## Payment API (Mock PG)

### POST /payments

**Request**

```json
{
  "orderId": 1001,
  "amount": 10000
}
```

**Response 201**

```json
{
  "paymentId": 5001,
  "status": "PENDING"
}
```

**Status Codes**

* 201 Created
* 404 Not Found → Order missing
* 409 Conflict → Invalid order state

---

### POST /payments/{id}/resolve

**Request**

```json
{
  "result": "APPROVED" // or FAILED
}
```

**Response 200**

```json
{
  "paymentId": 5001,
  "status": "APPROVED"
}
```

**Status Codes**

* 200 OK
* 409 Conflict → Already resolved
* 404 Not Found