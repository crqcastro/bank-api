#!/usr/bin/env bash
# seed.sh — popula o banco com clientes, contas e transacoes via API
# Uso: ./scripts/seed.sh [BASE_URL]
# Exemplo: ./scripts/seed.sh http://localhost:8080

set -euo pipefail

BASE_URL="${1:-http://localhost:8080}/api/v1"
GREEN='\033[0;32m'; RED='\033[0;31m'; CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

log()  { echo -e "${CYAN}${BOLD}[seed]${NC} $*"; }
ok()   { echo -e "  ${GREEN}✓${NC} $*"; }
fail() { echo -e "  ${RED}✗${NC} $*" >&2; exit 1; }

# Gera numero de CPF unico (11 digitos) usando timestamp + random
doc() { echo "$(date +%s%N | tail -c 8)$((RANDOM % 100 + 10))" | head -c 11; }

post() {
  local url="$1" body="$2"
  local resp status
  resp=$(curl -s -w "\n%{http_code}" -X POST "$url" -H "Content-Type: application/json" -d "$body")
  status=$(echo "$resp" | tail -1)
  body_out=$(echo "$resp" | head -1)
  if [[ "$status" != "201" && "$status" != "200" ]]; then
    fail "POST $url retornou $status: $body_out"
  fi
  echo "$body_out"
}

do_patch() {
  local url="$1" body="$2"
  local resp status
  resp=$(curl -s -w "\n%{http_code}" -X PATCH "$url" -H "Content-Type: application/json" -d "$body")
  status=$(echo "$resp" | tail -1)
  if [[ "$status" != "200" ]]; then
    fail "PATCH $url retornou $status: $(echo "$resp" | head -1)"
  fi
}

id_of() { echo "$1" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])"; }

# ─── Healthcheck ────────────────────────────────────────────────────────────
log "Verificando API em ${BASE_URL%/api/v1} ..."
for i in $(seq 1 15); do
  if curl -sf "${BASE_URL%/api/v1}/actuator/health" | grep -q '"status":"UP"'; then
    ok "API disponivel"; break
  fi
  [ "$i" -eq 15 ] && fail "API nao respondeu apos 15 tentativas"
  echo "  aguardando... ($i)"
  sleep 2
done

# ─── Clientes ───────────────────────────────────────────────────────────────
log "Criando clientes..."

C1=$(post "$BASE_URL/customers" "{
  \"name\":\"Alice Oliveira\",\"document\":\"$(doc)\",\"documentType\":\"CPF\",
  \"email\":\"alice.$(date +%s)@bank.com\",\"phone\":\"11999990001\"}")
C1_ID=$(id_of "$C1"); ok "Alice          id=$C1_ID"

C2=$(post "$BASE_URL/customers" "{
  \"name\":\"Bruno Santos\",\"document\":\"$(doc)\",\"documentType\":\"CPF\",
  \"email\":\"bruno.$(date +%s)@bank.com\",\"phone\":\"11999990002\"}")
C2_ID=$(id_of "$C2"); ok "Bruno          id=$C2_ID"

C3=$(post "$BASE_URL/customers" "{
  \"name\":\"Carla Mendes\",\"document\":\"$(doc)\",\"documentType\":\"CPF\",
  \"email\":\"carla.$(date +%s)@bank.com\",\"phone\":\"11999990003\"}")
C3_ID=$(id_of "$C3"); ok "Carla          id=$C3_ID"

C4=$(post "$BASE_URL/customers" "{
  \"name\":\"Diego Ferreira\",\"document\":\"$(doc)\",\"documentType\":\"CPF\",
  \"email\":\"diego.$(date +%s)@bank.com\",\"phone\":\"11999990004\"}")
C4_ID=$(id_of "$C4"); ok "Diego          id=$C4_ID"

C5=$(post "$BASE_URL/customers" "{
  \"name\":\"Eva Costa\",\"document\":\"$(doc)\",\"documentType\":\"CPF\",
  \"email\":\"eva.$(date +%s)@bank.com\",\"phone\":\"11999990005\"}")
C5_ID=$(id_of "$C5"); ok "Eva            id=$C5_ID"

# ─── Contas ─────────────────────────────────────────────────────────────────
log "Criando contas..."

A1=$(post "$BASE_URL/accounts" "{\"customerId\":\"$C1_ID\"}"); A1_ID=$(id_of "$A1"); ok "Conta Alice    id=$A1_ID"
A2=$(post "$BASE_URL/accounts" "{\"customerId\":\"$C2_ID\"}"); A2_ID=$(id_of "$A2"); ok "Conta Bruno    id=$A2_ID"
A3=$(post "$BASE_URL/accounts" "{\"customerId\":\"$C3_ID\"}"); A3_ID=$(id_of "$A3"); ok "Conta Carla    id=$A3_ID"
A4=$(post "$BASE_URL/accounts" "{\"customerId\":\"$C4_ID\"}"); A4_ID=$(id_of "$A4"); ok "Conta Diego    id=$A4_ID"
A5=$(post "$BASE_URL/accounts" "{\"customerId\":\"$C5_ID\"}"); A5_ID=$(id_of "$A5"); ok "Conta Eva      id=$A5_ID"
A1B=$(post "$BASE_URL/accounts" "{\"customerId\":\"$C1_ID\"}"); A1B_ID=$(id_of "$A1B"); ok "Conta Alice 2  id=$A1B_ID"

# ─── Depositos ──────────────────────────────────────────────────────────────
log "Realizando depositos..."

post "$BASE_URL/transactions/deposit" "{\"accountId\":\"$A1_ID\",\"amount\":5000.00,\"description\":\"Salario\"}" > /dev/null
ok "Alice   +R\$ 5.000,00"
post "$BASE_URL/transactions/deposit" "{\"accountId\":\"$A2_ID\",\"amount\":3200.00,\"description\":\"Salario\"}" > /dev/null
ok "Bruno   +R\$ 3.200,00"
post "$BASE_URL/transactions/deposit" "{\"accountId\":\"$A3_ID\",\"amount\":8500.00,\"description\":\"Freelance\"}" > /dev/null
ok "Carla   +R\$ 8.500,00"
post "$BASE_URL/transactions/deposit" "{\"accountId\":\"$A4_ID\",\"amount\":1200.00,\"description\":\"Deposito\"}" > /dev/null
ok "Diego   +R\$ 1.200,00"
post "$BASE_URL/transactions/deposit" "{\"accountId\":\"$A5_ID\",\"amount\":4700.00,\"description\":\"Salario\"}" > /dev/null
ok "Eva     +R\$ 4.700,00"
post "$BASE_URL/transactions/deposit" "{\"accountId\":\"$A1B_ID\",\"amount\":500.00,\"description\":\"Reserva\"}" > /dev/null
ok "Alice 2 +R\$   500,00"

# ─── Saques ─────────────────────────────────────────────────────────────────
log "Realizando saques..."

post "$BASE_URL/transactions/withdraw" "{\"accountId\":\"$A1_ID\",\"amount\":350.00,\"description\":\"Aluguel\"}" > /dev/null
ok "Alice   -R\$   350,00"
post "$BASE_URL/transactions/withdraw" "{\"accountId\":\"$A3_ID\",\"amount\":1200.00,\"description\":\"Equipamentos\"}" > /dev/null
ok "Carla   -R\$ 1.200,00"
post "$BASE_URL/transactions/withdraw" "{\"accountId\":\"$A5_ID\",\"amount\":200.00,\"description\":\"Supermercado\"}" > /dev/null
ok "Eva     -R\$   200,00"

# ─── Transferencias ─────────────────────────────────────────────────────────
log "Realizando transferencias..."

post "$BASE_URL/transactions/transfer" \
  "{\"originAccountId\":\"$A1_ID\",\"destinationAccountId\":\"$A2_ID\",\"amount\":500.00,\"description\":\"Pagamento servico\"}" > /dev/null
ok "Alice -> Bruno   R\$   500,00"

post "$BASE_URL/transactions/transfer" \
  "{\"originAccountId\":\"$A3_ID\",\"destinationAccountId\":\"$A1_ID\",\"amount\":750.00,\"description\":\"Reembolso\"}" > /dev/null
ok "Carla -> Alice   R\$   750,00"

post "$BASE_URL/transactions/transfer" \
  "{\"originAccountId\":\"$A5_ID\",\"destinationAccountId\":\"$A4_ID\",\"amount\":300.00,\"description\":\"Emprestimo\"}" > /dev/null
ok "Eva   -> Diego   R\$   300,00"

post "$BASE_URL/transactions/transfer" \
  "{\"originAccountId\":\"$A2_ID\",\"destinationAccountId\":\"$A3_ID\",\"amount\":100.00,\"description\":\"Split conta\"}" > /dev/null
ok "Bruno -> Carla   R\$   100,00"

post "$BASE_URL/transactions/transfer" \
  "{\"originAccountId\":\"$A1_ID\",\"destinationAccountId\":\"$A1B_ID\",\"amount\":200.00,\"description\":\"Reserva emergencia\"}" > /dev/null
ok "Alice -> Alice2  R\$   200,00"

# ─── Bloqueio de conta (Diego — simula fraude) ───────────────────────────────
log "Bloqueando conta Diego (simulacao de fraude)..."
do_patch "$BASE_URL/accounts/$A4_ID/block" '{"justification":"Atividade suspeita detectada"}'
ok "Conta Diego bloqueada"

# ─── Resumo ─────────────────────────────────────────────────────────────────
log "Seed concluido!"
echo ""
printf "  %-22s %s\n" "Clientes criados:"  "5"
printf "  %-22s %s\n" "Contas criadas:"    "6  (Alice tem 2)"
printf "  %-22s %s\n" "Depositos:"         "6"
printf "  %-22s %s\n" "Saques:"            "3"
printf "  %-22s %s\n" "Transferencias:"    "5"
printf "  %-22s %s\n" "Contas bloqueadas:" "1  (Diego)"
echo ""
printf "  %-22s %s\n" "Swagger:" "${BASE_URL%/api/v1}/swagger-ui.html"
printf "  %-22s %s\n" "Grafana:" "http://localhost:3000  (admin/admin)"
echo ""
