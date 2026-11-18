-- =====================================================================
-- AlertaCidade - Script DQL (consultas exigidas pela rubrica)
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1) INNER JOIN entre duas tabelas
-- Sensores com o ponto de monitoramento onde estao instalados.
-- ---------------------------------------------------------------------
SELECT s.cd_sensor        AS codigo_sensor,
       s.tp_sensor        AS tipo,
       s.ds_unidade       AS unidade,
       s.st_sensor        AS status_sensor,
       p.nm_ponto         AS ponto,
       p.nm_bairro        AS bairro
  FROM T_AC_SENSOR s
 INNER JOIN T_AC_PONTO_MONITORAMENTO p
    ON p.id_ponto = s.id_ponto
 ORDER BY p.nm_bairro, p.nm_ponto, s.cd_sensor;

-- ---------------------------------------------------------------------
-- 2) INNER JOIN + funcoes de grupo + GROUP BY
-- Estatisticas das leituras por sensor: quantidade, media, minimo e maximo.
-- ---------------------------------------------------------------------
SELECT s.cd_sensor                AS codigo_sensor,
       s.tp_sensor                AS tipo,
       s.ds_unidade               AS unidade,
       COUNT(m.id_medicao)        AS qtd_leituras,
       ROUND(AVG(m.nr_valor), 2)  AS media,
       MIN(m.nr_valor)            AS minimo,
       MAX(m.nr_valor)            AS maximo,
       MAX(m.dt_hora_medicao)     AS ultima_leitura
  FROM T_AC_SENSOR s
 INNER JOIN T_AC_MEDICAO m
    ON m.id_sensor = s.id_sensor
 GROUP BY s.cd_sensor, s.tp_sensor, s.ds_unidade
 ORDER BY maximo DESC;

-- ---------------------------------------------------------------------
-- 3) LEFT OUTER JOIN + WHERE + ORDER BY
-- Todos os pontos ATIVOS com seus alertas, inclusive os pontos que nunca
-- tiveram alerta (aparecem com colunas de alerta nulas).
-- O filtro WHERE e aplicado na tabela da esquerda para preservar o LEFT JOIN.
-- ---------------------------------------------------------------------
SELECT p.nm_ponto                         AS ponto,
       p.nm_bairro                        AS bairro,
       NVL(a.nv_alerta, 'SEM ALERTA')     AS nivel_alerta,
       a.st_alerta                        AS status_alerta,
       a.dt_hora_alerta                   AS data_hora_alerta,
       a.ds_mensagem                      AS mensagem
  FROM T_AC_PONTO_MONITORAMENTO p
  LEFT OUTER JOIN T_AC_ALERTA a
    ON a.id_ponto = p.id_ponto
 WHERE p.st_ativo = 'S'
 ORDER BY p.nm_bairro, p.nm_ponto, a.dt_hora_alerta DESC;
