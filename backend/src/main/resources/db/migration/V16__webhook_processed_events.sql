-- Idempotência e proteção contra replay no webhook Mercado Pago
CREATE TABLE webhook_processed_events (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  payment_id BIGINT NOT NULL,
  request_id VARCHAR(255) NOT NULL,
  processed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (payment_id, request_id)
);

CREATE INDEX idx_webhook_events_payment_request ON webhook_processed_events(payment_id, request_id);
