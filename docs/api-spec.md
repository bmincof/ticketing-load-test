# 1. 이벤트 조회
GET /events/{eventId}

설명
이벤트 판매 상태 및 잔여 수량 조회

Response
```
{
  "eventId": "evt_123",
  "status": "OPEN",
  "remainingQuantity": 12,
  "price": 50000
}
```

에러
- 404 이벤트 없음
- 410 판매 종료

# 2. 오더 생성 (재고 홀드)
POST /orders

설명
재고를 선점하고 결제 유효 시간이 있는 Order 생성

Request
```
{
  "eventId": "evt_123"
}
```

Response (201)
```
{
  "orderId": "ord_456",
  "status": "PENDING",
  "expiresAt": "2026-01-10T12:05:00Z"
}
```

에러

- 409 재고 없음
- 410 이벤트 종료

# 3. 결제 준비
POST /payments/ready

설명
결제 시도 단위를 생성하고 PG 요청 정보 반환

Request
```
{
  "orderId": "ord_456"
}
```

Response
```
{
  "paymentId": "pay_789",
  "pg": "KAKAOPAY",
  "amount": 50000,
  "redirectUrl": "https://pg.example.com/pay",
  "expiresAt": "2026-01-10T12:05:00Z"
}
```

에러

- 404 order 없음
- 409 order 상태 불일치
- 410 order 만료

# 4. 결제 승인 Webhook
POST /payments/webhook

설명
PG → 서버 결제 결과 통보

Request (예시)
```
{
  "provider": "KAKAOPAY",
  "providerPaymentId": "pg_abc",
  "merchantUid": "pay_789",
  "status": "SUCCESS",
  "amount": 50000
}
```

Response
```
200 OK
```

실패 상황에서도 항상 200 반환 (PG 재시도 방지 목적)

# 5. 주문 상태 조회
GET /orders/{orderId}

설명
최종 주문/결제 결과 조회

Response
```
{
  "orderId": "ord_456",
  "status": "PAID",
  "eventId": "evt_123",
  "payment": {
    "paymentId": "pay_789",
    "status": "SUCCESS",
    "provider": "KAKAOPAY"
  }
}
```