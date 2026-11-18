#!/usr/bin/env bash
# Cenario de teste do MVP: simula a subida e a descida da agua em um ponto novo
# e mostra a mudanca de estado (NORMAL -> ATENCAO -> CRITICO -> NORMAL).
# Uso:  ./scripts/cenario-cheia.sh [URL_BASE]     (padrao: http://localhost:8080)
# Requer bash e curl (no Windows: Git Bash).
set -euo pipefail

BASE="${1:-http://localhost:8080}"
SUFIXO="$(date +%H%M%S)"

json_id() { grep -o '"id":[0-9]*' | head -1 | cut -d: -f2; }
campo() { grep -o "\"$1\":\"\?[A-Z0-9.]*" | head -1 | sed 's/.*://; s/"//g'; }

echo "== Criando ponto e sensor em $BASE"
PONTO=$(curl -sf -X POST "$BASE/api/pontos" -H 'Content-Type: application/json' -d "{
  \"nome\":\"Cenario Cheia $SUFIXO\",\"bairro\":\"Bairro Demo\",\"cidade\":\"Sao Paulo\",
  \"latitude\":-23.55,\"longitude\":-46.63,\"cotaAtencaoCm\":50,\"cotaCriticaCm\":90}" | json_id)
SENSOR=$(curl -sf -X POST "$BASE/api/sensores" -H 'Content-Type: application/json' \
  -d "{\"pontoId\":$PONTO,\"codigo\":\"S-CEN-$SUFIXO\",\"tipo\":\"NIVEL_AGUA\"}" | json_id)
echo "ponto=$PONTO sensor=$SENSOR (cotas de demonstracao: atencao 50 cm, critica 90 cm)"
echo

printf "%-10s %-14s %-14s %s\n" "nivel_cm" "classificacao" "alerta_gerado" "estado_do_ponto"
for NIVEL in 20 35 52 68 91 104 76 40 15; do
  RESP=$(curl -sf -X POST "$BASE/api/medicoes" -H 'Content-Type: application/json' \
    -d "{\"sensorId\":$SENSOR,\"valor\":$NIVEL}")
  CLASSE=$(echo "$RESP" | campo classificacao)
  if echo "$RESP" | grep -q '"alertaGerado":null'; then ALERTA="-"; else ALERTA="sim"; fi
  ESTADO=$(curl -sf "$BASE/api/pontos/$PONTO/status" | campo estado)
  printf "%-10s %-14s %-14s %s\n" "$NIVEL" "$CLASSE" "$ALERTA" "$ESTADO"
  sleep 1
done

echo
echo "== Alertas do ponto $PONTO"
curl -sf "$BASE/api/alertas?pontoId=$PONTO"
echo
